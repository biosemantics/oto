/**
 * 
 */
package edu.arizona.biosemantics.oto.common.ontologylookup.search.utilities;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;



import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.Entity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.EntityProposals;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.Quality;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.QualityProposals;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.SimpleEntity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.knowledge.Dictionary;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.search.SynRingVariation;


/**
 * @author Hong Cui
 *
 */
public class Utilities {
	private static final Logger LOGGER = Logger.getLogger(Utilities.class);   
	private static Hashtable<String, String> entityhash = new Hashtable<String, String>();

	private static Pattern p2 = Pattern.compile("(.*?)(\\d+) to (\\d+)");
	private static Pattern p1 = Pattern.compile("(first|second|third|forth|fouth|fourth|fifth|sixth|seventh|eighth|ninth|tenth)\\b(.*)");
	public static String preposition = "of|in|on|between|with|from|to|into|toward";
	private static int relationlength = 3;
	public static ArrayList<String> partofrelations = new ArrayList<String>();
	static{
		partofrelations.add("part_of");
		partofrelations.add("in");
		partofrelations.add("on");
	}

	/**
	 * 
	 */
	public Utilities() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in 
	 * the 200-399 range.
	 * @param url The HTTP URL to be pinged.
	 * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that
	 * the total timeout is effectively two times the given timeout.
	 * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the
	 * given timeout, otherwise <code>false</code>.
	 */
	public static boolean ping(String url, int timeout) {
	    url = url.replaceFirst("https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.
	    try {
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setConnectTimeout(timeout);
	        connection.setReadTimeout(timeout);
	        connection.setRequestMethod("HEAD");
	        int responseCode = connection.getResponseCode();
	        return (200 <= responseCode && responseCode <= 399);
	    } catch (IOException exception) {
	        return false;
	    }
	}

	/**
	 * like the array join function in Perl.
	 *
	 * @param tokens the tokens
	 * @param start the start index, inclusive
	 * @param end the end index, inclusive
	 * @param delimiter the delimiter
	 * @return the string
	 */
	public static String join(String[] tokens, int start, int end,
			String delimiter) {
		String result = "";
		for(int i = start; i <=end; i++) result += tokens[i]+delimiter;
		return result.replaceFirst(delimiter+"$", "");
	}

	public static void initEQHash(Hashtable<String, String> EQ) {
		EQ.put("source", "");
		EQ.put("characterid", "");
		EQ.put("stateid", "");
		EQ.put("description", "");
		EQ.put("type", ""); // do not output type to table
		EQ.put("entity", "");
		EQ.put("entitylabel", "");
		EQ.put("entityid", "");
		EQ.put("quality", "");
		EQ.put("qualitylabel", "");
		EQ.put("qualityid", "");
		EQ.put("qualitynegated", "");
		EQ.put("qualitynegatedlabel", "");
		EQ.put("qnparentlabel", "");
		EQ.put("qnparentid", "");
		EQ.put("qualitymodifier", "");
		EQ.put("qualitymodifierlabel", "");
		EQ.put("qualitymodifierid", "");
		EQ.put("entitylocator", "");
		EQ.put("entitylocatorlabel", "");
		EQ.put("entitylocatorid", "");
		EQ.put("countt", "");
	}

	/**
	 * fifth abc => abc 5
	 * abc_1 => abc 1
	 * abc_1_to_3 => abc 1, abc 2, abc 3
	 * @param entitylist: a comma-separated list of (maybe indexed) structures: entity1, entity2
	 * @return a comma-separated list of the same structures with all covered indexes enumerated and turned to numbers.
	 *         If input string is not indexed structures, the original string will be returned. 
	 */
	public static String transformIndexedStructures(String entitylist) {
		entitylist = entitylist.replaceAll("(?<=\\w)- (?=\\w)", "-");
		String transformed = entityhash.get(entitylist);
		if (transformed != null)
			return transformed;

		transformed = "";
		if (entitylist.matches(".*?(_[\\divx]+|first|second|third|forth|fouth|fourth|fifth|sixth|seventh|eighth|ninth|tenth).*")) {
			String[] entities = entitylist.split("(?<!_),(?!_)");
			for (String entity : entities) {
				// case one
				entity = entity.trim();
				if (entity.matches(".*?\\b(first|second|third|forth|fouth|fourth|fifth|sixth|seventh|eighth|ninth|tenth)\\b.*")) {
					Matcher m = p1.matcher(entity);
					if (m.matches()) {
						String position = turnPosition2Number(m.group(1));
						entity = m.group(2) + " " + position;
						transformed += entity + ",";
					} else {
						transformed += entity + ",";
					}
					// transformed = transformed.replaceFirst(",$", "").trim();
					// entityhash.put(entitylist, transformed);
					// return transformed;
				} // case two
				else if (entity.matches("(.*?_[\\divx]+)|(.*?_[\\divx]+-[\\divx]+)")) {// abc_1, abc_1_and_2, abc_1_to_3, abc_1-3
					String organ = entity.substring(0, entity.indexOf("_"));

					if (entity.matches(".*?_[\\divx]+-[\\divx]+")) {// abc_1-3
						entity = entity.replaceAll("-", "_to_");// before reformatRomans,replace "-" with "_to_"
					}

					entity = reformatRomans(entity);
					entity = entity.replaceAll("_(?=\\d+)", " ").replaceAll("(?<=\\d)_", " "); // abc_1_and_3 => abc 1 and 3
					if (entity.indexOf(" and ") < 0 && entity.indexOf(" to ") < 0) { // single entity
						transformed += entity + ",";
						// entityhash.put(entitylist, transformed);
						// return transformed;
					} else {// abc 1 and 2
						if (entity.indexOf(" and ") > 0) {
							transformed += entity.replaceFirst(" and ", "," + organ + " ") + ","; // abc 1,abc 2
							// entityhash.put(entitylist, transformed);
							// return transformed;
						}

						// abc 1 , 2 to 5 ; abc 2 to 5
						Matcher m = p2.matcher(entity);
						if (m.matches()) {
							String part1 = m.group(1);
							int from = Integer.parseInt(m.group(2));
							int to = Integer.parseInt(m.group(3));
							String temp1 = "";
							for (int i = from; i <= to; i++) {
								temp1 = temp1 + organ + " " + i + ",";
							}

							String temp = "";
							part1 = part1.replaceAll("\\D", "").trim();
							if (part1.length() > 0) {
								String[] nums = part1.split("\\s+");
								for (String n : nums) {
									temp = temp + organ + " " + n + ",";
								}
							}

							transformed = transformed + temp + temp1;
							// transformed.replaceFirst(",$", "").trim();
							// entityhash.put(entitylist, transformed);
							// return transformed;
						}
					}
				} else {// neither
					transformed += entity + ",";
				}
			}
		} else {
			transformed = entitylist;
			// entityhash.put(entitylist, entitylist);
		}
		transformed = transformed.replaceFirst(",$", "").trim();
		entityhash.put(entitylist, transformed);
		return transformed;
	}


	/**
	 * abc_iv_and_v
	 * 
	 * @param entity
	 * @return
	 */
	private static String reformatRomans(String entity) {
		String[] parts = entity.split("_");
		String reformatted = "";
		for (String part : parts) {
			if (part.matches("[ivx]+"))
				reformatted += turnRoman2Number(part) + "_";
			else
				reformatted += part + "_";
		}
		return reformatted.replaceFirst("_$", "");
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	private static String turnRoman2Number(String word) {
		int total = 0;
		if (word.endsWith("iv")) {
			total += 4;
			word = word.replaceFirst("iv$", "");
		}
		if (word.endsWith("ix")) {
			total += 9;
			word = word.replaceFirst("ix$", "");
		}
		int length = word.length();
		for (int i = 0; i < length; i++) {
			if (word.charAt(i) == 'i')
				total += 1;
			if (word.charAt(i) == 'v')
				total += 5;
			if (word.charAt(i) == 'x')
				total += 10;
		}
		return total + "";
	}

	/**
	 * fifth => 5
	 * 
	 * @param word
	 * @return
	 */
	private static String turnPosition2Number(String word) {
		if (word.compareTo("first") == 0)
			return "1";
		if (word.compareTo("second") == 0)
			return "2";
		if (word.compareTo("third") == 0)
			return "3";
		if (word.compareTo("forth") == 0)
			return "4";
		if (word.compareTo("fouth") == 0)
			return "4";
		if (word.compareTo("fourth") == 0)
			return "4";
		if (word.compareTo("fifth") == 0)
			return "5";
		if (word.compareTo("sixth") == 0)
			return "6";
		if (word.compareTo("seventh") == 0)
			return "7";
		if (word.compareTo("eighth") == 0)
			return "8";
		if (word.compareTo("ninth") == 0)
			return "9";
		if (word.compareTo("tenth") == 0)
			return "10";
		return null;
	}
	//code to remove prepositions from starting and ending of strings => Hariharan
	public static String removeprepositions(String trim) {
		for(;;)
		{
			if(trim.matches("("+preposition+")\\s.*"))
				trim = trim.substring(trim.indexOf(" ")+1);
			else
				break;
		}

		for(;;)
		{
			if(trim.matches(".*\\s("+preposition+")"))
				trim = trim.substring(0,trim.lastIndexOf(" "));
			else
				break;
		}
		return trim;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

	/**
	 * to allow using "bearer of [quality]" to construct a CompositeEntity
	 * this wrapper is not needed if REntity allowed relation+quality besides relation+entity.
	 * @param q
	 * @return
	 */
	public static SimpleEntity wrapQualityAs(Quality q) {
		SimpleEntity qentity = new SimpleEntity();
		qentity.setClassIRI(q.getClassIRI());
		qentity.setConfidenceScore(q.getConfidenceScore());
		qentity.setId(q.getId());
		qentity.setLabel(q.getLabel());
		qentity.setPLabel(q.getPLabel());
		qentity.setDef(q.getDef());
		qentity.setSearchString(q.getSearchString());
		qentity.setString(q.getString());
		return qentity;
	}

	/**
	 * add ep to entities, grouping proposals with the same phrase/string together
	 * @param entities
	 * @param ep
	 */
	public static void addEntityProposals(ArrayList<EntityProposals> entities,
			EntityProposals ep) {
		if(ep==null) return;
		for(EntityProposals aep: entities){
			for(Entity ex: aep.getProposals()){
				ArrayList<Entity> eproposals = ep.getProposals();
				for(int i = 0; i < eproposals.size(); i++){
					Entity in = eproposals.get(i);
					if(ex.content().compareTo(in.content())==0){
						eproposals.remove(in); //deduplicate
					}
				}
			}
		}
		for(EntityProposals aep: entities){
			if(ep.getPhrase().compareTo(aep.getPhrase())==0){
				aep.add(ep);
				return;
			}
		}
		entities.add(ep);
	}
	
	/**
	 * add qp to qualities, grouping proposals with the same phrase/string together
	 * not adding duplicates
	 * @param qualities
	 * @param qp
	 */
	public static void addQualityProposals(ArrayList<QualityProposals> qualities,
			QualityProposals qp) {
		if(qp==null) return;
		for(QualityProposals aqp: qualities){
			for(Quality qx: aqp.getProposals()){
				ArrayList<Quality> qproposals = qp.getProposals();
				for(int i = 0; i<qproposals.size(); i++){
					Quality in = qproposals.get(i);
					if(qx.content().compareTo(in.content())==0){
						qproposals.remove(in);//deduplicate
					}
				}
			}
		}
		
		for(QualityProposals aqp: qualities){
			if(qp.getPhrase().compareTo(aqp.getPhrase())==0){
				aqp.add(qp);
				return;
			}
		}
		qualities.add(qp);
	}
	

	
	



	public static String getSynRing4Phrase(String phrase, OntologyLookupClient OLC){
		String synring = "";
		if(phrase.length()==0) return synring;
		phrase = phrase.replaceAll("(\\(\\?:|\\))", ""); //(?:(?:shoulder) (?:girdle)) =>shoulder girdle
		String[] tokens = phrase.split("\\s+");
		//may use a more sophisticated approach to construct ngrams: A B C => A B C;A (B C); (A B) C;
		for(int i = 0; i < tokens.length; i++){
			if(tokens[i].matches(Dictionary.spatialtermptn)) synring += "(?:"+SynRingVariation.getSynRing4Spatial(tokens[i], OLC)+")"+" ";
			else synring += "(?:"+SynRingVariation.getSynRing4Structure(tokens[i], OLC)+")"+" ";
		}
		return synring.trim();
	}


	
	/*
	 * return: "//relation[@name='part_of'][@from='" + structureid + "']" +
				"|//relation[@name='in'][@from='" + structureid + "']" +
				"|//relation[@name='on'][@from='" + structureid + "']"
	 */
	public static String partofXpath(String structureid){
		String path = "";
		for(String partof: partofrelations){
			path += "//relation[@name='"+partof+"'][@from='" + structureid + "']|";
		}
		return path.replaceFirst("\\|+$","");
	}

	/**
	 * 
	 * @param entities
	 * @return true if entities hold a simple spatial entity
	 */
	public static boolean holdsSimpleSpatialEntity(ArrayList<EntityProposals> entities) {
		for(EntityProposals ep : entities){
			for(Entity e: ep.getProposals()){
				if(e instanceof SimpleEntity){
					if(e.getId().startsWith(Dictionary.spatialOntoPrefix)) return true;
				}
			}
		}
		return false;
	}


}


