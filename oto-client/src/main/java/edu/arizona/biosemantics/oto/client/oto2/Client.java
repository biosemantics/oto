package edu.arizona.biosemantics.oto.client.oto2;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.InvocationCallback;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.oto.client.lite.OTOLiteClient;
import edu.arizona.biosemantics.oto.common.model.lite.Decision;
import edu.arizona.biosemantics.oto.common.model.lite.Download;
import edu.arizona.biosemantics.oto.common.model.lite.Synonym;
import edu.arizona.biosemantics.oto.common.model.lite.Term;
import edu.arizona.biosemantics.oto.common.model.lite.Upload;
import edu.arizona.biosemantics.oto.common.model.lite.UploadResult;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;

public class Client extends OTOLiteClient {

	@Inject
	public Client(@Named("OTOLiteClient_Url")String url) {
		super(url);
	}
		
	@Override
	public Future<UploadResult> putUpload(Upload upload) {
		edu.arizona.biosemantics.oto2.oto.client.rest.Client client = new edu.arizona.biosemantics.oto2.oto.client.rest.Client(url);
		client.open();
		
		Collection collection = new Collection();
		Bucket structureBucket = new Bucket();
		structureBucket.setName("Structures");
		Bucket characterBucket = new Bucket();
		characterBucket.setName("Charaters");
		Bucket othersBucket = new Bucket();
		othersBucket.setName("Others");
		
		for(Term character : upload.getPossibleCharacters()) {
			edu.arizona.biosemantics.oto2.oto.shared.model.Term term = new edu.arizona.biosemantics.oto2.oto.shared.model.Term(character.getTerm());
			characterBucket.addTerm(term);
		}
		for(Term other : upload.getPossibleOtherTerms()) {
			edu.arizona.biosemantics.oto2.oto.shared.model.Term term = new edu.arizona.biosemantics.oto2.oto.shared.model.Term(other.getTerm());
			othersBucket.addTerm(term);
		}
		for(Term structure : upload.getPossibleStructures()) {
			edu.arizona.biosemantics.oto2.oto.shared.model.Term term = new edu.arizona.biosemantics.oto2.oto.shared.model.Term(structure.getTerm());
			structureBucket.addTerm(term);
		}
		
		List<Bucket> buckets = new LinkedList<Bucket>();
		buckets.add(structureBucket);
		buckets.add(characterBucket);
		buckets.add(othersBucket);
		collection.setBuckets(buckets);
		collection.setName(upload.getSource());
		Future<Collection> result = client.put(collection);
		try {
			collection = result.get();
		} catch (Exception e) {
			e.printStackTrace();
			return ConcurrentUtils.constantFuture(null);
		}
		client.close();
		return ConcurrentUtils.constantFuture(new UploadResult(collection.getId(), collection.getSecret()));
	}
	
	@Override
	public void putUpload(Upload upload, InvocationCallback<List<UploadResult>> callback) {
		Future<UploadResult> future = this.putUpload(upload);
		List<UploadResult> result = new LinkedList<UploadResult>();
		try {
			result.add(future.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
		callback.completed(result);
	}
	
	@Override
	public Future<Download> getDownload(UploadResult uploadResult) {
		edu.arizona.biosemantics.oto2.oto.client.rest.Client client = new edu.arizona.biosemantics.oto2.oto.client.rest.Client(url);
		client.open();
		Future<Collection> result = client.get(String.valueOf(uploadResult.getUploadId()), uploadResult.getSecret());
		client.close();
		Collection collection;
		try {
			collection = result.get();
		} catch (Exception e) {
			e.printStackTrace();
			return ConcurrentUtils.constantFuture(null);
		}
		
		List<Decision> decisions = new LinkedList<Decision>();
		for(Label label : collection.getLabels()) {
			for(edu.arizona.biosemantics.oto2.oto.shared.model.Term mainTerm : label.getMainTerms()) {
				List<edu.arizona.biosemantics.oto2.oto.shared.model.Term> termsSynonyms = label.getSynonyms(mainTerm);
				decisions.add(new Decision(label.getId() + "-" + mainTerm.getId(), mainTerm.getTerm(), label.getName(), 
						(termsSynonyms != null && !termsSynonyms.isEmpty()), collection.getName()));
				
				
				/*for(edu.arizona.biosemantics.oto2.oto.shared.model.Term termsSynonym : termsSynonyms) {
					decisions.add(new Decision(label.getId() + "-" + termsSynonym.getId(), termsSynonym.getTerm(), label.getName(), 
							(termsSynonyms != null && !termsSynonyms.isEmpty()), collection.getName()));
				}*/
			}
		}
		
		List<Synonym> synonyms = new LinkedList<Synonym>();
		for(Label label : collection.getLabels()) {
			for(edu.arizona.biosemantics.oto2.oto.shared.model.Term mainTerm : label.getMainTerms()) {
				List<edu.arizona.biosemantics.oto2.oto.shared.model.Term> termsSynonyms = label.getSynonyms(mainTerm);
				for(edu.arizona.biosemantics.oto2.oto.shared.model.Term termsSynonym : termsSynonyms) {
					synonyms.add(new Synonym(label.getId() + "-" + mainTerm.getId() + "-" + termsSynonym.getId(), mainTerm.getTerm(), 
							label.getName(), termsSynonym.getTerm()));
				}
			}
		}
		
		return ConcurrentUtils.constantFuture(new Download(false, decisions, synonyms));
	}

	@Override
	public void getDownload(UploadResult uploadResult, InvocationCallback<List<Download>> callback) {
		
	}
	
}
