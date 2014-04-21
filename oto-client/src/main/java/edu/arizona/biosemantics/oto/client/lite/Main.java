package edu.arizona.biosemantics.oto.client.lite;

import edu.arizona.biosemantics.oto.common.model.lite.Download;
import edu.arizona.biosemantics.oto.common.model.lite.UploadResult;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OTOLiteClient otoLiteClient = new OTOLiteClient("http://biosemantics.arizona.edu/OTOLite/");
		Download download = otoLiteClient.download(new UploadResult(392, "secret"));
		System.out.println(download.isFinalized());
		System.out.println(download.toString());
		
		/*Upload upload = new Upload();
		upload.setGlossaryType("plants");
		List<Sentence> sentences = new ArrayList<Sentence>();
		sentences.add(new Sentence(1, "1.txt", "some", "example"));
		upload.setSentences(sentences);
		List<Term> possStr = new ArrayList<Term>();
		List<Term> possCh = new ArrayList<Term>();
		List<Term> possOt = new ArrayList<Term>();
		possStr.add(new Term("possstr"));
		possCh.add(new Term("possCh"));
		possOt.add(new Term("possOth"));
		upload.setPossibleCharacters(possCh);
		upload.setPossibleOtherTerms(possOt);
		upload.setPossibleStructures(possStr);
		
		int uploadId = otoLiteClient.upload(upload);
		System.out.println("uploadID: " + uploadId);
		
		Download download = otoLiteClient.download(uploadId);
		System.out.println(download.toString()); */
	}

}
