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

import org.tomitribe.crest.api.Command;

import java.net.URL;

public class ColorCommands {

    @Command
    public void postColorText(final URL url) throws Exception {
        new ColorClient(url).postColorText();
    }

    @Command
    public void getColorText(final URL url) throws Exception {
        new ColorClient(url).getColorText();
    }

    @Command
    public void getColorObject(final URL url) throws Exception {
        new ColorClient(url).getColorObject();
    }

    @Command
    public void getStaticHtml(final URL url) throws Exception {
        new ColorClient(url).getStaticHtml();
    }
}
