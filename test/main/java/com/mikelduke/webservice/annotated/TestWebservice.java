package com.mikelduke.webservice.annotated;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.mikelduke.webservice.annotations.AWS;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class TestWebservice {

	public static void main(String[] args) {
		try {
			AnnotatedWebservice aw = new AnnotatedWebservice(8080, TestWebservice.class);
			aw.enableAWSRootDescriptions();
			
			String testPath = "test/main/resources";
			File folder = new File(testPath);
			
			FileService fs = new FileService(folder);
			fs.showFolders();
			
			aw.addObject(fs);
			aw.start();
			
			System.out.println("Server started, Hit Enter to stop.\n");

	        try {
	            System.in.read();
	        } catch (Throwable ignored) {
	        }

	        aw.stop();
	        System.out.println("Server stopped.\n");
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			e.printStackTrace();
		}	
	}
	
	@AWS(path="/test")
	public String test(IHTTPSession session) {
		String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n" + "  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }

        msg += "</body></html>\n";
        
        return msg;
	}
	
	@AWS(path="/test2")
	public String test2(IHTTPSession session) {
		return "TEST NUMBER 2";
	}
	
	@AWS(path="/test3")
	public String test3() {
		return "test3";
	}
	
	@AWS(path="/test4")
	public String test4(String param1) {
		return "test4";
	}
	
	@AWS(path="/test5")
	public String test5(String param1, String param2) {
		return "test5";
	}
	
	@AWS(path="/startsWith", startsWith=true)
	public String startsWith() {
		return "startsWith Test";
	}
	
	@AWS(path="/postOnly", method="POST")
	public String postOnly() {
		return "post only!";
	}
}
