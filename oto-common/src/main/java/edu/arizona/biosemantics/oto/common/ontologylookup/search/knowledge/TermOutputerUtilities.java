/**
 * 
 */
package edu.arizona.biosemantics.oto.common.ontologylookup.search.knowledge;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import edu.arizona.biosemantics.oto.common.ontologylookup.search.owlaccessor.OWLAccessorImpl;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.utilities.Utilities;

import org.atteo.evo.inflector.English;

/**
 * @author Hong Updates
 *
 */
public class TermOutputerUtilities {
	private static final Logger LOGGER = Logger.getLogger(TermOutputerUtilities.class);   
	public  ArrayList<OWLAccessorImpl> OWLqualityOntoAPIs = new ArrayList<OWLAccessorImpl>();
	public  ArrayList<OWLAccessorImpl> OWLentityOntoAPIs  = new ArrayList<OWLAccessorImpl>();
	public  ArrayList<String> excluded = new ArrayList<String>();
	private  String[] entityontologyFilepaths;
	private String[] qualityontologyFilepaths;


	public static boolean debug = false;
	public  String attributes = "";
	public  String adjectiveorganptn="";
	public  OWLOntology uberon = null;



	/**
	 * constructor may be needed if we need to exclude different parts of ontology.
	 * @param ontologyfolder
	 * @deprecated use the other constructor
	 */
	@Deprecated
	public TermOutputerUtilities(String eonto, String bspo, String pato, String ro, Hashtable<String,String> ontoURLs){
		//TODO:add GO:bioprocess
		entityontologyFilepaths = new String[]{eonto, bspo};
		qualityontologyFilepaths = new String[]{pato, ro};
		initTermOutputerUtilities(entityontologyFilepaths, qualityontologyFilepaths, ontoURLs);
	}

	public TermOutputerUtilities(String[] entityOntologyFilepaths, String[] qualityOntologyFilepaths, Hashtable<String,String> ontoURLs){
		//TODO:add GO:bioprocess
		entityontologyFilepaths = entityOntologyFilepaths;
		qualityontologyFilepaths = qualityOntologyFilepaths;
		initTermOutputerUtilities(entityontologyFilepaths, qualityontologyFilepaths,ontoURLs);
	}
	
	private void initTermOutputerUtilities(String[] entityOntologyFilepaths, String[] qualityOntologyFilepaths, Hashtable<String, String> ontoURLs) {
		//get organ adjectives from Dictionary
		Enumeration<String> organs = Dictionary.organadjectives.keys();
		while(organs.hasMoreElements()){
			ArrayList<String> adjs = Dictionary.organadjectives.get(organs.nextElement());
			for(String adj: adjs){
				if(adj.trim().length()>0)adjectiveorganptn+=adj.trim()+"|";
			}
		}
		//for each entity ontology
		for(String onto: entityontologyFilepaths){
			if(onto.endsWith(".owl")){
				OWLAccessorImpl api = null;	
				/* always use local copy
				 * if(Utilities.ping(ontoURLs.get(onto), 200)){
					try{
						api = new OWLAccessorImpl(ontoURLs.get(onto), new ArrayList<String>());
					}catch(Exception e){
						//ignore this onology  
					}

				}else{*/
					try{
						api = new OWLAccessorImpl(new File(onto), new ArrayList<String>());
					}catch(Exception e){
						//ignore this onology  
					}
				//}
				OWLentityOntoAPIs.add(api);
				//if(onto.endsWith(ApplicationUtilities.getProperty("ontology.uberon")+".owl")){
				uberon = api.getOntology();
				organs = api.organadjectives.keys();
				while(organs.hasMoreElements()){
					ArrayList<String> adjs = api.organadjectives.get(organs.nextElement());
					for(String adj: adjs){
						if(adj.length()>0)adjectiveorganptn += adj+"|"; 
					}						
				}	
				//}
				//this.alladjectiveorgans.add(api.adjectiveorgans);
			}/*else if(onto.endsWith(".obo")){ //no longer take OBO format
						int i = onto.lastIndexOf("/");
						int j = onto.lastIndexOf("\\");
						i = i>j? i:j;
						String ontoname = onto.substring(i+1).replaceFirst("\\.obo", "");
						OBO2DB o2d = new OBO2DB(database, onto ,ontoname);
						OBOentityOntoAPIs.add(o2d);
					}*/
		}
		adjectiveorganptn = adjectiveorganptn.replaceAll("(^\\||\\|$)", "");

		//for each quality ontology
		for(String onto: qualityontologyFilepaths){
			if(onto.endsWith(".owl")){
				OWLAccessorImpl api = null;
				/*if(Utilities.ping(ontoURLs.get(onto), 200)){
					try{
					api = new OWLAccessorImpl(ontoURLs.get(onto), new ArrayList<String>());
					}catch(Exception e){
						//ignore this onology  
					}
				}else{*/
					try{
					api= new OWLAccessorImpl(new File(onto), excluded);
					}catch(Exception e){
						//ignore this onology  
					}
				//}
				attributes += "|"+api.getLowerCaseAttributeSlimStringPattern();
				attributes = attributes.replaceAll("(^\\||\\|$)", "");
				OWLqualityOntoAPIs.add(api);
			}/*else if(onto.endsWith(".obo")){
						int i = onto.lastIndexOf("/");
						int j = onto.lastIndexOf("\\");
						i = i>j? i:j;
						String ontoname = onto.substring(i+1).replaceFirst("\\.obo", "");
						OBO2DB o2d = new OBO2DB(database, onto ,ontoname);
						OBOqualityOntoAPIs.add(o2d);
					}*/
		}
		//excluded.add(Dictionary.cellquality);//exclude "cellular quality"
	}

	/**
	 * search up the is_a path until one of the parent class is identified. 
	 * @param classlabel
	 * @return an array with two element: ids and labels of the parents
	 */
	/*public String[] retreiveParentInfoFromPATO (String classid){
		//find OWL PATO
		OWLAccessorImpl pato = null;
		for(OWLAccessorImpl api: OWLqualityOntoAPIs){
			if(api.getSource().indexOf("pato")>=0){
				pato = api;
				break;
			}
		}
		//find parent
		String [] result = {"",""}; 
		if(pato!=null){
			OWLClass c = pato.getClassByIRI(Dictionary.patoiri+classid.replaceAll(":", "_"));
			if(c!=null){
				List<OWLClass> pcs = pato.getParents(c);
				for(OWLClass pc: pcs){
					result[0] += pato.getID(pc)+",";
					result[1] += pato.getLabel(pc)+",";
				}
				result[0] = result[0].replaceFirst(",$", "");
				result[1] = result[1].replaceFirst(",$", "");
			}
		}		
		return result;
	}*/

	/**
	 * 
	 * @param classid
	 * @return parent id and label, or null if classid is null or classid does not exist in pato
	 */
	public String[] retreiveParentInfoFromPATO (String classid){
		if(classid==null) return null;

		//translate classid to a PATO id
		if(!classid.startsWith("PATO")){
			classid = Dictionary.translateToPATO.get(classid);
			if(classid==null) return null;
		}

		//find OWL PATO
		OWLAccessorImpl pato = null;
		for(OWLAccessorImpl api: OWLqualityOntoAPIs){
			if(api.getSource().indexOf("pato")>=0){
				pato = api;
				break;
			}
		}
		//find parent

		if(pato!=null){
			String [] result = {"",""}; 
			return findTargetParent(pato, classid, result);
		}		
		//return new String[] {"PATO:0000001","quality"};
		return null;
	}

	private static String[] findTargetParent(OWLAccessorImpl pato, String classid, String[] result){
		OWLClass c = pato.getOWLClassByIRI(Dictionary.baseiri+classid.replaceAll(":", "_"));
		if(c!=null){
			if(pato.getLabel(c).matches("\\b"+Dictionary.patoupperclasses+"\\b")){
				result[0] = pato.getID(c);
				result[1] = pato.getLabel(c);
				return result;
			}else{
				List<OWLClass> pcs = pato.getParents(c);
				for(OWLClass pc: pcs){
					return findTargetParent(pato, pato.getID(pc), result);
				}
			}
		}
		//if landed here, need to update Dictionary.patoupperclasses
		return null; 
	}

	/**
	 * merged to  searchOntologies(String term, String type, String ingroup)
	 * @param term
	 * @param type: entity or quality
	 * @return ArrayList of results, one result from an ontology 
	 */
	/*	public ArrayList<String[]> searchOntologies(String term, String type) throws Exception {
		//search quality ontologies
		ArrayList<String[]> results = new ArrayList<String[]>();
		//boolean added = false;
		if(type.compareTo("quality")==0){
			for(OWLAccessorImpl api: OWLqualityOntoAPIs){
				String[] result = searchOWLOntology(term, api, type);
				if(result!=null){
					//added = true;
					results.add(result);
				}
			}			
			for(OBO2DB o2d: OBOqualityOntoAPIs){
				String[] result = searchOBOOntology(term, o2d, type);
				if(result!=null){
					//added = true;
					results.add(result);
				}
			}
		}else if(type.compareTo("entity")==0){
			for(OWLAccessorImpl api: OWLentityOntoAPIs){
				String[] result = searchOWLOntology(term, api, type);
				if(result!=null){
					//added = true;
					results.add(result);
				}
			}			
			for(OBO2DB o2d: OBOentityOntoAPIs){
				String[] result = searchOBOOntology(term, o2d, type);
				if(result!=null){
					//added = true;
					results.add(result);
				}
			}
		}
		return results;

	}*/


	/**
	 * 
	 * @param term
	 * @param type
	 * @return null or a hashtable (id=>label) containing classes that have term as an relational adjective.
	 */
	public Hashtable<String, String> searchAdjectiveOrgan(String term, String type) {
		if(type.compareTo("entity")==0){
			return OWLAccessorImpl.adjectiveorgans.get(term);
		}
		return null;
	}


	/**
	 * Search a term in a subgroup of an ontology
	 * subgroup only applies to PATO relational slim //TODO complete this part.
	 * @param term
	 * @param type: entity or quality
	 * @param subgroup: inRelationalSlim
	 * @return ArrayList of results, one result from an ontology 
	 */
	public ArrayList<Hashtable<String, String>> searchOntologies(String term, String type, ArrayList<Hashtable<String, String>> results) {
		//search quality or entity ontologies, depending on the type

		//quality
		if(type.compareTo("quality")==0){
			for(OWLAccessorImpl api: OWLqualityOntoAPIs){
				Hashtable<String, String> result = searchOWLOntology(term, api, type);
				if(result!=null){
					results.add(result);
				}
			}			
			/*TODO need review : result format should be the same as OWL search
			 * for(OBO2DB o2d: OBOqualityOntoAPIs){
				Hashtable<String, String> result = searchOBOOntology(term, o2d, type);
				if(result!=null){
					//added = true;
					results.add(result);
				}
			}*/			
		}


		//entity
		if(type.compareTo("entity")==0){
			for(OWLAccessorImpl api: OWLentityOntoAPIs){
				Hashtable<String, String> result = searchOWLOntology(term, api, type);
				if(result!=null){
					results.add(result);
				}
			}			
			/*TODO need review
			 * for(OBO2DB o2d: OBOentityOntoAPIs){
				Hashtable<String, String> result = searchOBOOntology(term, o2d, type);
				if(result!=null){
					//added = true;
					results.add(result);
				}
			}*/
		}
		return results;

	}


	/**
	 * 
	 * @param term
	 * @param owlapi
	 * @param type
	 * @param slim ?? 
	 * @return 5-key hashtable: term, querytype, id, label, matchtype, iri
	 */
	private static Hashtable<String, String> searchOWLOntology(String term, OWLAccessorImpl owlapi, String type) {
		Hashtable<String, String> oresult = null; //original
		Hashtable<String, String> eresult = null; //exact
		Hashtable<String, String> nresult = null; //narrow
		Hashtable<String, String> rresult = null; //related
		//new
		Hashtable<String, String> bresult = null; //broad
		Hashtable<String, String> nrresult = null; //notrecommended
		//List<OWLClass> matches = (ArrayList<OWLClass>)owlapi.retrieveConcept(term);
		//should be

		Hashtable<String, ArrayList<OWLClass>> matches = (Hashtable<String, ArrayList<OWLClass>>)owlapi.retrieveConcept(term);
		if(matches == null || matches.size() ==0){
			return null;
			//TODO: besides phrase based search, consider also make use of the relations and definitions used in ontology
			//TODO: update other copies of the method
			//task 2 matches can be null, if the term is looked up into other ontologies - modified by Hariharan
		}else{
			//merge original and exact results: radial [original] = 'radials', radial[exact]='radius bone'
			List<OWLClass> matchclass = matches.get("original");
			if(matchclass!=null && matchclass.size()!=0){
				oresult = collectResult(term, matchclass, type, "original", owlapi);
				//return oresult;
			}
			matchclass = matches.get("exact");
			if(matchclass!=null && matchclass.size()!=0){
				eresult = collectResult(term, matchclass, type, "exact", owlapi);
				//return eresult;
			}

			matchclass = matches.get("narrow");
			if(matchclass!=null && matchclass.size()!=0){
				nresult = collectResult(term, matchclass, type, "narrow", owlapi);
				//return nresult;
			}

			matchclass = matches.get("related");
			if(matchclass!=null && matchclass.size()!=0){
				rresult = collectResult(term, matchclass, type, "related", owlapi);
				//return rresult;
			}
			
			matchclass = matches.get("broad");
			if(matchclass!=null && matchclass.size()!=0){
				bresult = collectResult(term, matchclass, type, "broad", owlapi);
				//return rresult;
			}
			
			matchclass = matches.get("notrecommended");
			if(matchclass!=null && matchclass.size()!=0){
				nrresult = collectResult(term, matchclass, type, "notrecommended", owlapi);
				//return rresult;
			}
		}
		/*if(Boolean.valueOf(ApplicationUtilities.getProperty("search.exact"))){
		oresult = merge(oresult, eresult);
		if(oresult==null){
			oresult = merge(oresult, nresult);
			if(oresult==null){
				oresult = merge(oresult, rresult);
			}
		}
		return oresult;
		}else{*/
			oresult = merge(oresult, eresult);
			oresult = merge(oresult, nresult);
			oresult = merge(oresult, rresult);
			oresult = merge(oresult, bresult);
			oresult = merge(oresult, nrresult);
			return oresult;
		//}
		//return null;
	}
	/*private Hashtable<String, String> searchOWLOntology(String term, OWLAccessorImpl owlapi, String type) {
		Hashtable<String, String> result = null;
		//List<OWLClass> matches = (ArrayList<OWLClass>)owlapi.retrieveConcept(term);
		//should be

		Hashtable<String, ArrayList<OWLClass>> matches = (Hashtable<String, ArrayList<OWLClass>>)owlapi.retrieveConcept(term);
		if(matches == null || matches.size() ==0){
			return null;
			//TODO: besides phrase based search, consider also make use of the relations and definitions used in ontology
			//TODO: update other copies of the method
			//task 2 matches can be null, if the term is looked up into other ontologies - modified by Hariharan
		}else{
			//merge original and exact results: radial [original] = 'radials', radial[exact]='radius bone'
			List<OWLClass> matchclass = matches.get("original");
			if(matchclass!=null && matchclass.size()!=0){
				result = collectResult(term, matchclass, type, "original", owlapi);
				//return result;
			}
			matchclass = matches.get("exact");
			if(matchclass!=null && matchclass.size()!=0){
				Hashtable<String, String> temp = collectResult(term, matchclass, type, "exact", owlapi);
				if(result!=null){
					result = merge(result, temp);
					return result;
				}
				return temp;
			}else if(result!=null){
				return result;
			}

			matchclass = matches.get("narrow");
			if(matchclass!=null && matchclass.size()!=0){
				result = collectResult(term, matchclass, type, "narrow", owlapi);
				return result;
			}

			matchclass = matches.get("related");
			if(matchclass!=null && matchclass.size()!=0){
				result = collectResult(term, matchclass, type, "related", owlapi);
				return result;
			}
		}
		return null;
	}*/

	/**
	 * merge exact match 'temp' to original match 'result'
	 * remove redundant results
	 * @param result
	 * @param temp
	 */
	private static Hashtable<String, String> merge(Hashtable<String, String> result,
			Hashtable<String, String> temp) {
		if(temp == null) return result;
		if(result == null){
			return temp;
		}
		result.put("term",  result.get("term"));
		result.put("querytype",  result.get("querytype"));
		
		ArrayList<String> rids = new ArrayList<String>(Arrays.asList(result.get("id").split("###")));
		ArrayList<String> rlabels = new ArrayList<String>(Arrays.asList(result.get("label").split("###")));
		ArrayList<String> riris = new ArrayList<String>(Arrays.asList(result.get("iri").split("###")));
		ArrayList<String> rplabels = new ArrayList<String>(Arrays.asList(result.get("parentlabel").split("###")));
		ArrayList<String> rdefs = new ArrayList<String>(Arrays.asList(result.get("def").split("###")));
		ArrayList<String> rmatchtypes = new ArrayList<String>(Arrays.asList(result.get("matchtype").split("###")));

		ArrayList<String> tids = new ArrayList<String>(Arrays.asList(temp.get("id").split("###")));
		ArrayList<String> tlabels = new ArrayList<String>(Arrays.asList(temp.get("label").split("###")));
		ArrayList<String> tiris = new ArrayList<String>( Arrays.asList(temp.get("iri").split("###")));
		ArrayList<String> tplabels = new ArrayList<String>(Arrays.asList(temp.get("parentlabel").split("###")));
		ArrayList<String> tdefs = new ArrayList<String>(Arrays.asList(temp.get("def").split("###")));
		ArrayList<String> tmatchtypes = new ArrayList<String>(Arrays.asList(temp.get("matchtype").split("###")));

		for(int i = 0; i<rids.size(); i++){
			if(tids.contains(rids.get(i))){//deduplicate
				tids.remove(rids.get(i));
				tlabels.remove(rlabels.get(i));
				tiris.remove(riris.get(i));
				tplabels.remove(rplabels.get(i));
				tdefs.remove(rdefs.get(i));
				tmatchtypes.remove(rmatchtypes.get(i));
			}
		}

		String ids = ""; 
		String labels = ""; 
		String iris = "";
		String plabels = "";
		String defs = "";
		String matchtypes = "";
		for(int i = 0; i<rids.size(); i++){
			ids +=rids.get(i)+"###";
			labels +=rlabels.get(i)+"###";
			iris +=riris.get(i)+"###";
			plabels +=rplabels.get(i)+"###";
			defs += rdefs.get(i)+"###";
			matchtypes +=rmatchtypes.get(i)+"###";
		}
		for(int i = 0; i<tids.size(); i++){
			ids +=tids.get(i)+"###";
			labels +=tlabels.get(i)+"###";
			iris +=tiris.get(i)+"###";
			plabels +=tplabels.get(i)+"###";
			defs += tdefs.get(i)+"###";
			matchtypes += tmatchtypes.get(i)+"###";
		}

		result.put("matchtype", matchtypes.replaceAll("(^###|###$)", "")); //original+exact
		result.put("id", ids.replaceAll("(^###|###$)", ""));
		result.put("label",labels.replaceAll("(^###|###$)", ""));
		result.put("iri", iris.replaceAll("(^###|###$)", ""));	
		result.put("parentlabel",plabels.replaceAll("(^###|###$)", ""));
		result.put("def",defs.replaceAll("(^###|###$)", ""));

		return result;
	}

	/**
	 * if multiple matches, use "###" to connect the matches
	 * @param term
	 * @param matches
	 * @param querytype
	 * @param matchtype
	 * @param owlapi
	 * @return 5-key hashtable: term, querytype, id, label, matchtype
	 */
	private static Hashtable<String, String> collectResult(String term, List<OWLClass> matches, String querytype, String matchtype, OWLAccessorImpl owlapi){
		if(matches == null || matches.size() ==0) return null;
		Hashtable<String, String> result = new Hashtable<String, String>();
		result.put("term",  term);
		result.put("querytype",  querytype);
		result.put("matchtype", "");
		result.put("id", "");
		result.put("label", "");
		result.put("iri", "");
		result.put("parentlabel", "");
		result.put("def", "");
		boolean haveresult = false;
		Iterator<OWLClass> it = matches.iterator();
		while(it.hasNext()){
			OWLClass c = it.next();
			String label = owlapi.getLabel(c);
			String id = owlapi.getID(c);
			String iri = c.getIRI().toString();
			String parentlabel = owlapi.getParentLabel(c);
			String def = owlapi.getDefinition(c);
			result.put("id", result.get("id")+ id+"###");
			result.put("label", result.get("label")+ label+"###");
			result.put("iri", result.get("iri")+ iri+"###");
			result.put("parentlabel", result.get("parentlabel")+ parentlabel+"###");
			result.put("def", result.get("def")+ (def.isEmpty()? "Definition to be added": def)+"###");
			
			result.put("matchtype", result.get("matchtype")+ matchtype +"###");
			haveresult = true;
		}
		if(haveresult){
			result.put("id", result.get("id").replaceFirst("###$", ""));
			result.put("label", result.get("label").replaceFirst("###$", ""));
			result.put("iri", result.get("iri").replaceFirst("###$", ""));
			result.put("parentlabel", result.get("parentlabel").replaceFirst("###$", ""));
			result.put("def", result.get("def").replaceFirst("###$", ""));
			result.put("matchtype", result.get("matchtype").replaceFirst("###$", ""));
		}
		if(haveresult) return result;
		return null;
	}
	/**
	 * merged to  searchOWLOntology(String term, OWLAccessorImpl owlapi, String type, String ingroup)
	 * @param term
	 * @param owlapi
	 * @param type
	 * @return 4-key hashtable: term, querytype, id, label, matchtype
	 */
	/*private String[] searchOWLOntology(String term, OWLAccessorImpl owlapi, String type) throws Exception {
		String[] result = null;
		List<OWLClass> matches = (ArrayList<OWLClass>)owlapi.retrieveConcept(term);

		//Task 2 matches can be null, if the term is looked up into other ontologies - modified by Hariharan
		if(matches!=null)
		{
			Iterator<OWLClass> it = matches.iterator();

			//exact match first
			while(it.hasNext()){
				OWLClass c = it.next();
				String label = owlapi.getLabel(c);
				String id = owlapi.getID(c);
				if(label.compareToIgnoreCase(term)==0){
					result= new String[3];
					result[0] = type;
					result[1] = id;//id
					result[2] = label;
					return result;
				}
			}

			//otherwise, append all possible matches
			it = matches.iterator();
			result = new String[]{"", "", ""};
			while(it.hasNext()){
				OWLClass c = it.next();
				String label = owlapi.getLabel(c);
				String id = owlapi.getID(c);
				result[0] = type;
				result[1] += id+";";
				result[2] += label+";";
			}
			if(result[1].length()>0){
				result[1] = result[1].replaceFirst(";$", "");
				result[2] = result[2].replaceFirst(";$", "");
				return result;
			}else{
				return null;
			}
		}
		return null;
	}*/


	/*
	 * copied from fna.charactermarkup.Utilities
	 * */
	public static String checkWN(String cmdtext){
		try{
			Runtime r = Runtime.getRuntime();	
			Process proc = r.exec(cmdtext);
			ArrayList<String> errors = new ArrayList<String>();
			ArrayList<String> outputs = new ArrayList<String>();

			// any error message?
			//StreamGobbler errorGobbler = new 
			//StreamGobblerWordNet(proc.getErrorStream(), "ERROR", errors, outputs);            

			// any output?
			StreamGobbler outputGobbler = new 
					StreamGobblerWordNet(proc.getInputStream(), "OUTPUT", errors, outputs);

			// kick them off
			//errorGobbler.start();

			outputGobbler.start();
			//outputGobbler.gobble();

			// any error???
			int exitVal = proc.waitFor();
			////System.out.println("ExitValue: " + exitVal);

			StringBuffer sb = new StringBuffer();
			for(int i = 0; i<outputs.size(); i++){
				//sb.append(errors.get(i)+" ");
				sb.append(outputs.get(i)+" ");
			}
			return sb.toString();

		}catch(Exception e){
			LOGGER.error("", e);
		}
		return "";
	}
	////////////////////////////////////////////////////////////////////////

	/**
	 * return null : word not in WN
	 * return ""   : word is not a noun or is singular
	 * return aword: word is a pl and singular form is returned
	 */
	public String checkWN4Singular(String word){

		String result = checkWN("wn "+word+" -over");
		if (result.length()==0){//word not in WN
			return null;
		}
		//found word in WN:
		String t = "";
		Pattern p = Pattern.compile("(.*?)Overview of noun (\\w+) (.*)");
		Matcher m = p.matcher(result);
		while(m.matches()){
			t += m.group(2)+" ";
			result = m.group(3);
			m = p.matcher(result);
		}
		if (t.length() ==0){//word is not a noun
			return "";
		} 
		String[] ts = t.trim().split("\\s+"); //if multiple singulars (bases =>basis and base, pick the first one
		for(int i = 0; i<ts.length; i++){
			if(ts[i].compareTo(word)!=0){//find a singular form
				return ts[i];
			}
		}
		return "";//original is a singular
	}

	public boolean isPlural(String t) {
		t = t.replaceAll("\\W", "");
		if(t.matches("\\b(series|species|fruit)\\b")){
			return true;
		}
		if(t.compareTo(toSingular(t))!=0){
			return true;
		}
		return false;
	}

	public String toSingular(String word){
		String s = null;
		word = word.toLowerCase().replaceAll("[(){}]", "").trim(); //bone/tendon

		s = Dictionary.singulars.get(word);
		if(s!=null) return s;

		if(word.matches("\\w+_[ivx-]+")){
			Dictionary.singulars.put(word, word);
			Dictionary.plurals.put(word, word);
			return word;
		}

		if(word.matches("[ivx-]+")){
			Dictionary.singulars.put(word, word);
			Dictionary.plurals.put(word, word);
			return word;
		}

		//adverbs
		if(word.matches("[a-z]{3,}ly")){
			Dictionary.singulars.put(word, word);
			Dictionary.plurals.put(word, word);
			return word;
		}

		String wordcopy = word;
		wordcopy = checkWN4Singular(wordcopy);
		if(wordcopy != null && wordcopy.length()==0){
			return word;
		}else if(wordcopy!=null){
			Dictionary.singulars.put(word, wordcopy);
			if(wordcopy.compareTo(word)!=0) Dictionary.plurals.put(wordcopy, word); //special cases where sing = pl should be saved in Dictionary
			//if(debug) //System.out.println("["+word+"]'s singular is "+wordcopy);
			return wordcopy;
		}else{//word not in wn

			Pattern p1 = Pattern.compile("(.*?[^aeiou])ies$");
			Pattern p2 = Pattern.compile("(.*?)i$");
			Pattern p3 = Pattern.compile("(.*?)ia$");
			Pattern p4 = Pattern.compile("(.*?(x|ch|sh|ss))es$");
			Pattern p5 = Pattern.compile("(.*?)ves$");
			Pattern p6 = Pattern.compile("(.*?)ices$");
			Pattern p7 = Pattern.compile("(.*?a)e$");
			Pattern p75 = Pattern.compile("(.*?)us$");
			Pattern p8 = Pattern.compile("(.*?)s$");
			Pattern p9 = Pattern.compile("(.*?)a$");
			Pattern p10 = Pattern.compile("(.*?ma)ta$"); //stigmata => stigma (20 cases)
			Pattern p11 = Pattern.compile("(.*?)des$"); //crepides => crepis (4 cases)
			Pattern p12 = Pattern.compile("(.*?)es$"); // (14 cases)

			Matcher m1 = p1.matcher(word);
			Matcher m2 = p2.matcher(word);
			Matcher m3 = p3.matcher(word);
			Matcher m4 = p4.matcher(word);
			Matcher m5 = p5.matcher(word);
			Matcher m6 = p6.matcher(word);
			Matcher m7 = p7.matcher(word);
			Matcher m75 = p75.matcher(word);
			Matcher m8 = p8.matcher(word);
			Matcher m9 = p9.matcher(word);
			Matcher m10 = p10.matcher(word);
			Matcher m11 = p10.matcher(word);
			Matcher m12 = p10.matcher(word);

			if(m1.matches()){
				s = m1.group(1)+"y";
			}else if(m2.matches()){
				s = m2.group(1)+"us";
			}else if(m3.matches()){
				s = m3.group(1)+"ium";
			}else if(m4.matches()){
				s = m4.group(1);
			}else if(m5.matches()){
				s = m5.group(1)+"f";
			}else if(m6.matches()){
				s = m9.group(1)+"ex";
				if(!inOntology(s)) s = m9.group(1)+"ix";
				if(!inOntology(s)) s = null;
			}else if(m7.matches()){
				s = m7.group(1);
			}else if(m75.matches()){
				s = word;
			}else if(m8.matches()){
				s = m8.group(1);
			}else if(m9.matches()){
				s = m9.group(1)+"um";
				if(!inOntology(s)) s = m9.group(1)+"on";
				if(!inOntology(s)) s = null;
			}else if(m10.matches()){
				s = m10.group(1);
			}else if(m11.matches()){
				s = m11.group(1)+"s";
				if(!inOntology(s)) s = null;
			}

			if(s==null & m12.matches()){
				s = m12.group(1)+"is";
				if(!inOntology(s)) s = null;
			}

			if(s != null){
				//if(debug) //System.out.println("["+word+"]'s singular is "+s);
				Dictionary.singulars.put(word, s);
				if(word.compareTo(s)!=0) Dictionary.plurals.put(s, word);
				return s;
			}
		}
		//if(debug) //System.out.println("["+word+"]'s singular is "+word);
		return word;
	}

	/**
	 * if s is in any of the searchable ontology
	 * @param s
	 * @return
	 */
	private boolean inOntology(String s) {
		ArrayList<Hashtable<String, String>> matches = new ArrayList<Hashtable<String, String>> ();
		searchOntologies(s, "entity", matches);
		if(matches.size()>0) return true; 
		return false;
	}

	public static String toPlural(String b) {
		String p = Dictionary.plurals.get(b); //before CharaParser runs, Dictionary.plurals is almost empty
		if(p == null){
			p = English.plural(b);
		}
		if(p == null) return b;
		return p;
	}

	/**
	 * 
	 * @param classIRI
	 * @param phrase
	 * @return true if class1IRI is an offspring of class2IRI
	 */
	public boolean isChildQuality(String classIRIc, String classIRIp) {
		boolean isoffspring = false;
		for(OWLAccessorImpl qapi : this.OWLqualityOntoAPIs){
			OWLClass cc = qapi.getOWLClassByIRI(classIRIc);
			OWLClass cp = qapi.getOWLClassByIRI(classIRIp);
			if(isOffSpring(cc, cp, qapi)) isoffspring = true;
		}		
		return isoffspring;
	}



	private boolean isOffSpring(OWLClass cc, OWLClass cp, OWLAccessorImpl api) {
		List<OWLClass> parents = api.getParents(cc);
		if(parents==null || parents.size()==0) return false;
		if(parents.contains(cp)) return true;
		for(OWLClass parent : parents){
			return isOffSpring(parent, cp, api);
		}
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String[] results = retreiveParentInfoFromPATO("PATO:0000402");
		////System.out.println(results[1]);
	}





}
