package edu.arizona.biosemantics.oto.steps.client.presenter.toontologies;

public class BrowseSuperClassURL {
	private static String OLSURL = "http://www.ontobee.org/browser/index.php?o=";

	public static String get(String ontologyName) {
		if(ontologyName==null || ontologyName.isEmpty()){
			return "http://www.ontobee.org/";
		}else
			return OLSURL +  ontologyName;
	}
}
