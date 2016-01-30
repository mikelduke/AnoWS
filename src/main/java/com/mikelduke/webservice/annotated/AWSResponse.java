package com.mikelduke.webservice.annotated;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import fi.iki.elonen.NanoHTTPD;

public class AWSResponse {

	//NanoHTTPD.newFixedLengthResponse(status, mimeType, data, totalBytes)
	private NanoHTTPD.Response.IStatus status;
	private String mimeType = null;
	private InputStream data = null;
	private long totalBytes = -1;
	
	public AWSResponse(int status) {
		this.status = AWSUtil.getNanoHTTPDStatus(status);
		this.data = new ByteArrayInputStream(this.status.getDescription().getBytes());
	}
	
	public AWSResponse(int status, String data) {
		this.status = AWSUtil.getNanoHTTPDStatus(status);
		this.data = new ByteArrayInputStream(data.getBytes());
	}
	
	public AWSResponse(int status, InputStream data) throws IOException {
		this(status, URLConnection.guessContentTypeFromStream(data), data, -1);
	}
	
	public AWSResponse(int status, String mimeType, InputStream data) {
		this(status, mimeType, data, -1);
	}
	
	public AWSResponse(int status, String mimeType, InputStream data, long totalBytes) {
		this.status = AWSUtil.getNanoHTTPDStatus(status);
		this.mimeType = mimeType;
		this.data = data;
		this.totalBytes = totalBytes;
	}
	
	public NanoHTTPD.Response build() {
		if (totalBytes > -1) {
			return NanoHTTPD.newFixedLengthResponse(status, mimeType, data, totalBytes);
		} else {
			return NanoHTTPD.newChunkedResponse(status, mimeType, data);
		}
	}
}
