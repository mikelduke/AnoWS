package com.mikelduke.webservice.annotated;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mikelduke.webservice.annotations.AWS;

import fi.iki.elonen.NanoHTTPD;

public class AnnotatedWebservice extends NanoHTTPD {
	private static final String CLAZZ = AnnotatedWebservice.class.getName();
	private static final Logger LOG = Logger.getLogger(CLAZZ);
	
	private List<Object> annotatedObjects = new ArrayList<Object>();
	private AnnotationDescriptionService ads = null;

	public AnnotatedWebservice(int port, Class<?>... classes) throws InstantiationException, IllegalAccessException {
		this(null, port, classes);
	}
	
	public AnnotatedWebservice(int port, Object... objects) throws InstantiationException, IllegalAccessException {
		this(null, port, objects);
	}

	public AnnotatedWebservice(String hostname, int port, Class<?>...classes ) throws InstantiationException, IllegalAccessException {
		super(hostname, port);
		
		for (Class<?> clazz : classes) {
			addClass(clazz);
		}
	}
	
	public AnnotatedWebservice(String hostname, int port, Object... objects) throws InstantiationException, IllegalAccessException {
		super(hostname, port);
		
		for (Object o : objects) {
			this.annotatedObjects.add(o);
		}
	}
	
	public AnnotatedWebservice addClass(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		Object o = clazz.newInstance();
		addObject(o);
		
		return this;
	}
	
	public AnnotatedWebservice addObject(Object o) {
		if (!this.annotatedObjects.contains(o)) {
			this.annotatedObjects.add(o);
		} else {
			LOG.warning("Object " + o.getClass() + " is already registered");
		}
		
		return this;
	}
	
	@Override
    public NanoHTTPD.Response serve(IHTTPSession session) {
        NanoHTTPD.Method httpMethod = session.getMethod();
        String uri = session.getUri();
        
        LOG.info(httpMethod + " URL:'" + uri + "' IP: " + session.getRemoteIpAddress());
        
        AWSResponse response;
        
        try {
			response = runAnnotatedMethodForPath(session, uri, httpMethod.name());
		} catch (Exception e) {
			LOG.logp(Level.SEVERE, CLAZZ, "server", 
					"Error in Webservice: " + e.getMessage(), e);
			response = new AWSResponse(500, "Internal Error");
		}
        
        return response.build();
    }
	
	private AWSResponse runAnnotatedMethodForPath(IHTTPSession session, String path, String httpMethod) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		Object responseObj = null;
		boolean methodFound = false;
		
		for (Object o: this.annotatedObjects) {
			java.lang.reflect.Method m = getAnnotatedMethodForPath(o, httpMethod, path);

			if (m != null) {
				Class<?>[] params = m.getParameterTypes();
				if (params == null || params.length == 0) {
					methodFound = true;
					responseObj = m.invoke(o);
				} else if (params.length == 1 && params[0].equals(IHTTPSession.class)) {
					methodFound = true;
					responseObj = m.invoke(o, session);
				}
			}
		}
		
		if (methodFound) {
			return convertResponseObject(session, responseObj);
		} else {
			return new AWSResponse(404);
		}
	}
	
	private java.lang.reflect.Method getAnnotatedMethodForPath(Object o, String httpMethod, String path) {
		java.lang.reflect.Method[] methods = o.getClass().getMethods();
		
		for (java.lang.reflect.Method m: methods) {
			AWS aws = m.getAnnotation(AWS.class);
			
			if (AWSUtil.isCompatible(aws, httpMethod, path)) {
				return m;
			}
		}
		return null;
	}
	
	public void enableAWSRootDescriptions() {
		if (ads == null) {
			ads = new AnnotationDescriptionService(this);
		}
		
		addObject(ads);
	}
	
	public void disableAWSRootDescriptions() {
		this.annotatedObjects.remove(ads);
		
		ads = null;
	}
	
	List<Object> getAnnotatedObjectList() {
		return this.annotatedObjects;
	}
	
	private AWSResponse convertResponseObject(IHTTPSession session, Object responseObj) throws IOException {
		AWSResponse response = null;
		
		if (responseObj == null) {
			response = new AWSResponse(200);
		} else if (responseObj instanceof String) {
			String resp = (String) responseObj;
			response = new AWSResponse(200, resp);
		} else if (responseObj instanceof AWSResponse) {
			response = (AWSResponse) responseObj;
		} else if (responseObj instanceof InputStream) {
			InputStream is = (InputStream) responseObj;
			response = new AWSResponse(200, URLConnection.guessContentTypeFromStream(is), is);
		} else {
			throw new IOException("Error Converting Response from " + responseObj);
		}
		
		return response;
	}
}
