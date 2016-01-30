package com.mikelduke.webservice.annotated;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import com.mikelduke.webservice.annotations.AWS;
import com.mikelduke.webservice.annotations.JaxbAWS;

public class AnnotationDescriptionService {

	private AnnotatedWebservice aw;
	
	public AnnotationDescriptionService(AnnotatedWebservice aw) {
		this.aw = aw;
	}

	@AWS(path="/services", method="GET", description="Annotation Description Service")
	public AWSResponse getDescriptions() throws JAXBException {
		AWSResponse response;
		
		List<JaxbAWS> awss = getAWSMethods();
		AWSList awsList = new AWSList(awss);
		String respStr = marshal(awsList, AWSList.class);
		
		response = new AWSResponse(200, respStr);
		return response;
	}
	
	private List<JaxbAWS> getAWSMethods() {
		List<JaxbAWS> annotations = new ArrayList<JaxbAWS>();
		
		for (Object o : aw.getAnnotatedObjectList()) {
			Method[] methods = o.getClass().getMethods();
			
			for (Method m : methods) {
				AWS aws = m.getAnnotation(AWS.class);
				
				if (aws != null) {
					annotations.add(new JaxbAWS(aws));
				}
			}
		}
		
		return annotations;
	}
	
	private static String marshal(Object o, Class<?> clazz) throws JAXBException {
		final Marshaller m = JAXBContext.newInstance(clazz).createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		final StringWriter w = new StringWriter();
		m.marshal(o, w);

		return w.toString();
	}
	

	@XmlRootElement(name="services")
	protected static class AWSList {
		public AWSList() { }
		public AWSList(List<JaxbAWS> list) {
			service = list;
		}
		
		public List<JaxbAWS> service;
	}
}
