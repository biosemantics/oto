package edu.arizona.biosemantics.oto.common.ontologylookup.webservice;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.rpc.ServiceException;
/**
 * This class will call the ontology lookup webservice and get the parent term and definition 
 * for specific term in specific ontology
 * @author Fengqiong
 *
 */
public class OlsClient {
	private String termID;
	private String ontology;
	private String term;
	private String parent;
	private String definition;
	private boolean hasData = false;
	
	public boolean hasData() {
		return hasData;
	}
	
	public String getParent() {
		return parent;
	}
	public String getDefinition() {
		return definition;
	}
	
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	
	public String getTermID() {
		return termID;
	}
	
	public void setTermID(String id) {
		this.termID = id;
	}
	
	public String getOntology() {
		return ontology;
	}
	
	public void setOntoloty(String ontology) {
		this.ontology = ontology;
	}
	
	public OlsClient(String term, String ontology) {
		this.term = term;
		this.ontology = ontology;
		
		QueryServiceLocator locator = new QueryServiceLocator();
        try {
            Query service = locator.getOntologyQuery();
            //get termID by name
            HashMap termMap = service.getTermsByExactName(term, ontology);
            if (termMap.size() > 0) {
            	termID = termMap.keySet().iterator().next().toString();
            }
            
            if (termID != null && termID != "") {
            	//get Parent Name
            	HashMap parentMap = service.getTermParents(termID, ontology);
                if (parentMap.size() > 0) {
                	Iterator<String> iter = parentMap.values().iterator();
                	while (iter.hasNext()) {
                		if (parent == null || parent == "") {
                			parent = iter.next().toString();
                		} else {
                			parent += ", " + iter.next().toString();
                		}
                	}
                }
                
              //get Parent Name
            	HashMap defMap = service.getTermMetadata(termID, ontology);
                if (defMap != null && defMap.get("definition") != null) {
                	definition = defMap.get("definition").toString();
                }
                
                if (parent != null && !(parent.equals("") && !definition.equals(""))) {
                	hasData = true;
                }
            }
            
            
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
	}
}

