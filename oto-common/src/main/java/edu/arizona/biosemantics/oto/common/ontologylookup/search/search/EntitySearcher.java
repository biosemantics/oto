/**
 * 
 */
package edu.arizona.biosemantics.oto.common.ontologylookup.search.search;

import java.util.ArrayList;

import org.apache.log4j.Logger;
//import org.jdom.Element;
//import org.jdom.xpath.XPath;


import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.EntityProposals;

/**
 * @author Hong Cui
 * 
 * Uses Chain of Responsibility pattern
 *
 */
public abstract class EntitySearcher {
	//protected static XPath textpath;
	private static final Logger LOGGER = Logger.getLogger(EntitySearcher.class);   
	static{	
		try{
	//		textpath = XPath.newInstance(".//text");
		}catch(Exception e){
			LOGGER.error("", e);
		}
	}

	public OntologyLookupClient OLC;

	public EntitySearcher(OntologyLookupClient OLC){
		this.OLC = OLC;
	}
	
	/*whether the request can be handled by this searcher */
	//public abstract boolean canHandle (Element root, String structid,  String entityphrase, String elocatorphrase, String originalentityphrase, String prep, int ingroup);
	/*handle the request*/
	
	/**
	 * it is possible for one search phrase to match multiple entities, for example, both sexes => organism that is female and organism that is male
	 * @param root
	 * @param structid
	 * @param entityphrase
	 * @param elocatorphrase
	 * @param originalentityphrase
	 * @param prep
	 * @return 
	 */
	public abstract ArrayList<EntityProposals> searchEntity(String entityphrase, String elocatorphrase, String originalentityphrase, String prep);
	
	
	/*otherwise, set another handler to handle the request*/
   //public abstract void  setHandler(EntitySearcher handler, Element root, String structid,  String entityphrase, String elocatorphrase, String originalentityphrase, String prep, int ingroup);
	
}
