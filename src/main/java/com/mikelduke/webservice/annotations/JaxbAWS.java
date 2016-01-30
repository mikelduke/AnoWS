package com.mikelduke.webservice.annotations;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="service")
public class JaxbAWS {

	public String path;
	public String method;
	public boolean startsWith;
	public String description;
	
	public JaxbAWS() { }
	
	public JaxbAWS(AWS aws) {
		this.path = aws.path();
		this.method = aws.method();
		this.startsWith = aws.startsWith();
		this.description = aws.description();
	}

}
