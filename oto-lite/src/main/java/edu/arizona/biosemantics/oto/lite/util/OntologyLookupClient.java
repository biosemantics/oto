package edu.arizona.biosemantics.oto.lite.util;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

public class OntologyLookupClient {
	public void test() {
		try {
			String endpoint = "http://www.ebi.ac.uk/ontology-lookup/OntologyQuery";
			String namespace = "http://www.ebi.ac.uk/ontology-lookup/OntologyQuery";

			Service service = new Service();
			Call call = (Call) service.createCall();

			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			
			call.setOperationName(new QName(namespace, "getTermById"));
			call.addParameter("termId", org.apache.axis.Constants.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);

			call.invoke(new Object[] { "0000323" });

			System.out.println("got: '");
			
			 /*String endpoint =
				             "http://ws.apache.org:5049/axis/services/echo";
				   
				         Service  service = new Service();
				         Call     call    = (Call) service.createCall();
				   
				         call.setTargetEndpointAddress( new java.net.URL(endpoint) );
				         call.setOperationName(new QName("http://soapinterop.org/", "echoString"));
				   
				         String ret = (String) call.invoke( new Object[] { "Hello!" } );
				   
				         System.out.println("Sent 'Hello!', got '" + ret + "'");*/
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
}
