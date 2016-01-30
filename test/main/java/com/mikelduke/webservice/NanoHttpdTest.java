package com.mikelduke.webservice;

import java.util.Map;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.util.ServerRunner;

public class NanoHttpdTest extends NanoHTTPD {
	/**
     * logger to log to.
     */
    private static final Logger LOG = Logger.getLogger(NanoHttpdTest.class.getName());

    public static void main(String[] args) {
        ServerRunner.run(NanoHttpdTest.class);
    }

    public NanoHttpdTest() {
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session) {
        NanoHTTPD.Method method = session.getMethod();
        String uri = session.getUri();
        NanoHttpdTest.LOG.info(method + " '" + uri + "' ");
LOG.info("IP: " + session.getRemoteIpAddress());
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n" + "  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }

        msg += "</body></html>\n";

        return newFixedLengthResponse(msg);
    }
}
