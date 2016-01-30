package com.mikelduke.webservice.annotated;

import com.mikelduke.webservice.annotations.AWS;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

class AWSUtil {
	private AWSUtil() { }
	
	static NanoHTTPD.Response.Status getNanoHTTPDStatus(int code) {
    	for (Status s : Status.values()) {
    		if (s.getRequestStatus() == code) return s;
    	}
    	return null;
    }
	
	static boolean checkPath(AWS aws, String requestPath) {
		if (requestPath.equals(aws.path())) return true;
		else if (aws.startsWith() && requestPath.startsWith(aws.path())) return true;
		else return false;
	}
	
	static boolean checkHttpMethod(AWS aws, String requestMethod) {
		if (requestMethod.equals(aws.method())) return true;
		else if (aws.method().equals("*")) return true;
		else return false;
	}
	
	static boolean isCompatible(AWS aws, String requestMethod, String requestPath) {
		if (aws == null) return false;
		
		if (checkPath(aws, requestPath) 
				&& checkHttpMethod(aws, requestMethod)) {
			return true;
		}
		return false;
	}
}
