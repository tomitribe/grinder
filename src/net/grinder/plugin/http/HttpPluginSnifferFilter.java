// The Grinder
// Copyright (C) 2000  Paco Gomez
// Copyright (C) 2000  Phil Dawes
// Copyright (C) 2001  Phil Aston

// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

package net.grinder.plugin.http;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import net.grinder.tools.tcpsniffer.ConnectionDetails;
import net.grinder.tools.tcpsniffer.EchoFilter;
import net.grinder.tools.tcpsniffer.SnifferFilter;


/**
 *
 * @author Philip Aston
 * @version $Revision$
 */
public class HttpPluginSnifferFilter implements SnifferFilter
{
    /**
     * Initially I used the connection ID to create a SessionState for
     * each unique set of host and port names. However, browsers map
     * one logical session over a number of connections, so for now
     * I've assumed that everything belongs to the same session and
     * synchronised all HttpPluginSnifferFilters on it. It would be
     * better to have a SessionState per unique set of { serverHost,
     * serverPort, clientHost}, or to interpret application cookies.
     */
    private static SessionState s_sessionState = new SessionState();
    private EchoFilter m_echoFilter = new EchoFilter();

    public void handle(ConnectionDetails connectionDetails, byte[] buffer,
		       int bytesRead)
	throws IOException, RESyntaxException
    {
	synchronized (s_sessionState) // See JavaDoc for s_sessionState.
	{
	    // We dumbly assume the entire message, including the
	    // body, is US-ASCII encoded. This is a bug.
	    final String string = new String(buffer, 0, bytesRead, "US-ASCII");

	    final SessionState sessionState = s_sessionState;

	    final RE methodLineExpresion = getMethodLineExpression();

	    if (methodLineExpresion.match(string)) {
		// Message is start of new method.
		outputEntityData(sessionState);

		final String method = methodLineExpresion.getParen(1);
		final String url;

		// if we're running as a proxy, we get the full url in
		// the header, not just the filepath part
		if (methodLineExpresion.getParen(2).startsWith("http")) {
		    url = methodLineExpresion.getParen(2);
		} else {
		    url = connectionDetails.getURLBase("http") +
			methodLineExpresion.getParen(2);
		}

		if (method.equals("GET")) {
		    handleMethod(string, sessionState, url);
		}
		else if (method.equals("POST")) {
		    handleMethod(string, sessionState, url);
		    sessionState.setHandlingPost(true);
		    sessionState.resetEntityData();
		    addToEntityData(string, sessionState, true);
		}
		else {
		    System.err.println("Ignoring '" + method + "' from " +
				       connectionDetails.getDescription());
		}
	    }
	    else if (sessionState.getHandlingPost()) {
		addToEntityData(string, sessionState, false);
	    }
	    else {
		outputEntityData(sessionState);
		System.err.println("Ignoring request from " +
				   connectionDetails.getDescription() +
				   ":");
		m_echoFilter.handle(connectionDetails, buffer, bytesRead);
	    }
	}
    }

    private void handleMethod(String request, SessionState sessionState,
			      String url)
	throws RESyntaxException
    {
	sessionState.incrementRequestNumber();

	final int requestNumber = sessionState.getRequestNumber();

	outputProperty(requestNumber, "sleepTime",
		       Long.toString(sessionState.markTime()));
	outputProperty(requestNumber, "parameter.url", url);


	final RE ifModifiedExpression = 
	    getHeaderExpression("If-Modified-Since");

	if (ifModifiedExpression.match(request)) {
	    outputProperty(requestNumber, "parameter.ifModifiedSince",
			   ifModifiedExpression.getParen(1));
	}
    }

    private void outputEntityData(SessionState sessionState)
	throws IOException
    {
	if (sessionState.getHandlingPost()) {
	    final int requestNumber = sessionState.getRequestNumber();

	    final PostOutput postOutput = new PostOutput(requestNumber);
	    
	    postOutput.write(sessionState.getEntityData());

	    outputProperty(sessionState.getRequestNumber(), "parameter.post",
			   postOutput.getFilename());
	}

	sessionState.setHandlingPost(false);
    }

    private void outputProperty(int testNumber, String name, String value)
    {
	System.out.println("grinder.test" + testNumber + "." + name +
			   "=" + value);
    }

    private void addToEntityData(String request,
				 SessionState sessionState,
				 boolean thisMessageHasPOSTHeader)
	throws IOException, RESyntaxException
    {
	// Look for the content length in the header. Probably should
	// assert that we haven't already set the content length nor
	// added any entity data.
	final RE contentLengthExpession = getContentLengthExpression();

	if (contentLengthExpession.match(request)) {
	    final int length =
		Integer.parseInt(contentLengthExpession.getParen(1));

	    sessionState.setContentLength(length);
	}

	// Find and add the data.
	final RE messageBodyExpression = getMessageBodyExpression();

	if (messageBodyExpression.match(request)) {
	    sessionState.addEntityData(messageBodyExpression.getParen(1));
	
	    final int contentLength = sessionState.getContentLength();

	    // We flush our entity data output now if either
	    //    1. No Content-Length header was specified and this body belonged to the same
	    //        message as the POST method.
	    // or
	    //    2. We've reached or exceeded the specified Content-Length.
	    if (contentLength == -1 && thisMessageHasPOSTHeader ||
		contentLength != -1 &&
		sessionState.getEntityDataLength() >= contentLength) {

		outputEntityData(sessionState);
	    }
	}
    }

    /**
     * Regexp is not synchronised, so for now compile new objects
     * every time. If it becomes a bottleneck, the "get*Expression
     * methods should be implemented with object pools.
     *
     * From RFC 2616:
     *
     * Request-Line = Method SP Request-URI SP HTTP-Version CRLF
     * HTTP-Version = "HTTP" "/" 1*DIGIT "." 1*DIGIT http_URL =
     * "http:" "//" host [ ":" port ] [ abs_path [ "?" query ]]
     *  
     * We're flexible about CRLF.
    */
    private RE getMethodLineExpression() throws RESyntaxException
    {
	return new RE("^([:upper:]+) (.+) HTTP/\\d.\\d\\r?$", 
		      RE.MATCH_MULTILINE);
    }

    /**
     * Regexp is not synchronised, so for now compile new objects
     * every time. If it becomes a bottleneck, the "get*Expression
     * methods should be implemented with object pools.
    */
    private RE getContentLengthExpression() throws RESyntaxException
    {
	return new RE("[^(\r\n\r\n)]^Content-Length: (\\d+)", 
		      RE.MATCH_MULTILINE);
    }

    /**
     * Regexp is not synchronised, so for now compile new objects
     * every time. If it becomes a bottleneck, the "get*Expression
     * methods should be implemented with object pools.
    */
    private RE getHeaderExpression(String headerName) throws RESyntaxException
    {
	return new RE("^" + headerName + ": (.*)$", RE.MATCH_MULTILINE);
    }

    /**
     * Regexp is not synchronised, so for now compile new objects
     * every time. If it becomes a bottleneck, the "get*Expression
     * methods should be implemented with object pools.
    */
    private RE getMessageBodyExpression() throws RESyntaxException
    {
	return new RE("\r\n\r\n(.*)");
    }

    private static class SessionState
    {
	private int m_requestNumber = -1;
	private boolean m_handlingPost = false;
	private StringBuffer m_entityDataBuffer;
	private int m_contentLength;
	private long m_lastTime;

	SessionState()
	{
	    resetEntityData();
	    markTime();
	}

	public int getRequestNumber()
	{
	    return m_requestNumber;
	}

	public void incrementRequestNumber() 
	{
	    ++m_requestNumber;
	}

	public boolean getHandlingPost() 
	{
	    return m_handlingPost;
	}

	public void setHandlingPost(boolean b)
	{
	    m_handlingPost = b;
	}

	public String getEntityData() 
	{
	    return m_entityDataBuffer.toString();
	}

	public int getEntityDataLength() 
	{
	    return m_entityDataBuffer.length();
	}

	public void resetEntityData()
	{
	    m_entityDataBuffer = new StringBuffer();
	    m_contentLength = -1;
	}

	public void addEntityData(String s) 
	{
	    m_entityDataBuffer.append(s);
	}

	public int getContentLength()
	{
	    return m_contentLength;
	}

	public void setContentLength(int length)
	{
	    m_contentLength = length;
	}

	public long markTime()
	{
	    final long currentTime = System.currentTimeMillis();
	    final long result = currentTime - m_lastTime;
	    m_lastTime = currentTime;
	    return result;
	}
    }
}


class PostOutput
{
    private final static String FILENAME_PREFIX = "http-plugin-sniffer-post-";

    private final String m_filename;
    private final Writer m_writer;

    public PostOutput(int n) throws IOException
    {
	m_filename = FILENAME_PREFIX + n;
	m_writer = new BufferedWriter(new FileWriter(m_filename));
    }

    public String getFilename()
    {
	return m_filename;
    }
    
    public void write(String data) throws IOException
    {
	m_writer.write(data);
	m_writer.flush();
    }
}

