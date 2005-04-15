// Copyright (C) 2005 Philip Aston
// All rights reserved.
//
// This file is part of The Grinder software distribution. Refer to
// the file LICENSE which is part of The Grinder distribution for
// licensing details. The Grinder distribution is available on the
// Internet at http://grinder.sourceforge.net/
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.

package net.grinder.communication;

import java.net.InetAddress;
import java.net.Socket;

import junit.framework.TestCase;


/**
 *  Unit tests for {@link SocketWrapper}.
 *
 * @author Philip Aston
 * @version $Revision$
 */
public class TestSocketWrapper extends TestCase {

  public void testConstructionWithBadSocket() throws Exception {
    final Acceptor acceptor = new Acceptor("localhost", 0, 1);
    final Socket socket =
      new Socket(InetAddress.getByName(null), acceptor.getPort());
    socket.close();
    acceptor.shutdown();

    try {
      new SocketWrapper(socket);
      fail("Expected CommunicationException");
    }
    catch (CommunicationException e) {
    }
  }

  public void testIsPeerShutdown() throws Exception {
    final SocketAcceptorThread socketAcceptor = new SocketAcceptorThread();

    final Connector connector =
      new Connector(socketAcceptor.getHostName(), socketAcceptor.getPort(),
                    ConnectionType.AGENT);

    final ClientSender clientSender = ClientSender.connect(connector);

    socketAcceptor.join();

    final Socket socket = socketAcceptor.getAcceptedSocket();
    while (socket.getInputStream().read() > 0);  // Discard pending bytes.

    final SocketWrapper wrapper = new SocketWrapper(socket);

    assertTrue(!wrapper.isPeerShutdown());

    clientSender.send(new SimpleMessage());

    assertTrue(!wrapper.isPeerShutdown());

    while (wrapper.getInputStream().read() > 0);  // Discard pending bytes.

    clientSender.send(new CloseCommunicationMessage());

    assertTrue(wrapper.isPeerShutdown());

    socketAcceptor.close();
  }
}
