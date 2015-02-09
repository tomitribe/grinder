/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.tomitribe.perf.jaxrs;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ColorClient extends Assert {

    private final URL webappUrl;
    private CloseableHttpClient client;

    public ColorClient(String webappUrl) throws MalformedURLException {
        this(new URL(webappUrl));
    }

    public ColorClient(URL webappUrl) {
        this.webappUrl = webappUrl;
        client = HttpClients.createDefault();
    }

    public void getStaticHtml() throws IOException {
        final String color = execute(new HttpGet(webappUrl + "static.html"));

        assertEquals("<h1>faster?</h1>", color);
    }

    public void getColorText() throws IOException {
        final String color = execute(new HttpGet(webappUrl + "color"));

        assertEquals("white", color);
    }

    public void postColorText() throws IOException {
        execute(new HttpPost(webappUrl + "color/green"));
    }

    public void getColorObject() throws Exception {

        final HttpGet httpGet = new HttpGet(webappUrl + "color/object");
        httpGet.setHeader("Accept", "application/json");

        final String result = execute(httpGet);

        assertEquals("{\"color\":{\"b\":0,\"g\":113,\"name\":\"orange\",\"r\":231}}", result);
    }

    private String execute(HttpUriRequest request) throws IOException {
        // Shouldn't create a new http client every time, but fine for now
        try (final CloseableHttpResponse response = client.execute(request)) {

            final StatusLine status = response.getStatusLine();

            final int statusCode = status.getStatusCode();

            final String entity = (statusCode == 204) ? null : EntityUtils.toString(response.getEntity());

            if (statusCode < 200 || statusCode > 299) {
                throw new IllegalStateException(response.getStatusLine().toString());
            }

            return entity;
        }
    }
}