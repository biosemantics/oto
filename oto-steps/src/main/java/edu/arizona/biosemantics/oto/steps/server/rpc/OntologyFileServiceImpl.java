/**
 * 
 */
package edu.arizona.biosemantics.oto.steps.server.rpc;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto.steps.server.Configuration;
import edu.arizona.biosemantics.oto.steps.server.db.ToOntologiesDAO;
import edu.arizona.biosemantics.oto.steps.shared.rpc.OntologyFileService;
import edu.arizona.biosemantics.oto.steps.shared.rpc.RPCResult;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyInfo;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;

/**
 * @author Hong Cui
 * This service takes care of access to ontology files, both user created or existing ones.
 *
 */
public class OntologyFileServiceImpl extends RemoteServiceServlet implements OntologyFileService{

	/**
	 * 
	 */
	private static final long serialVersionUID = -668703714442141846L;
	private OntologyServiceImpl ontologyService = new OntologyServiceImpl();

	@Override
	public RPCResult<OntologyInfo> newEmptyOntologyFile(String userID, String uploadID, String fileName, String prefix, String taxonGroup) throws Exception{
		File newOnto = null;
		try{
			//File template = new File(Configuration.ontology_dir+File.separator+"local", "template.owl");
			newOnto = new File(Configuration.fileBase/*+ File.separator + authenticationToken.getUserId()*/, fileName+".owl");
			if(!newOnto.exists()){
				OntologyInfo ontoInfo = createOntology(newOnto, prefix, "local", taxonGroup); //TODO check user file structure.
				ToOntologiesDAO.getInstance().registerOntology(userID, uploadID, fileName, prefix, "local", taxonGroup);
				//OntologyInfo ontoInfo = new OntologyInfo(fileName, prefix, "local");
				return new RPCResult<OntologyInfo>(true, "", ontoInfo);
			}else{
				return new RPCResult<OntologyInfo>(false, "ontology file exsits", null);
			}
		}catch(SQLException e){
			//remove the ontology just created
			newOnto.delete();
			throw e;
		}catch(IOException e){
			throw e;
		}
	}

	private OntologyInfo createOntology(File newOnto, String prefix,
			String type, String taxonGroup) throws Exception{

		OntologyInfo info = new OntologyInfo(newOnto.getName(), prefix, type, taxonGroup);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//OntologyIRI and DocumentIRI mapping, create ontology
		IRI ontologyIRI = IRI
				.create(Configuration.etc_ontology_baseIRI+prefix.toLowerCase()+".owl");
		IRI documentIRI = IRI.create(newOnto);
		SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
		manager.addIRIMapper(mapper);
		OWLOntology ont = manager.createOntology(ontologyIRI);
		OWLDataFactory factory = manager.getOWLDataFactory();
		//add namespaces

		//import base ontologies: general and the ones matching the requested taxon group.
		ArrayList<OntologyInfo> ontos = ToOntologiesDAO.getInstance().getOntologyInfoWithTaxonGroup(taxonGroup);
		ontos.addAll(ToOntologiesDAO.getInstance().getOntologyInfoWithTaxonGroup("general"));
		for(OntologyInfo onto: ontos){
			if(!onto.getOntologyPrefix().startsWith("ETC_")){ //upload files from server, add live access to ontology url later
				File ontoFile = new File(Configuration.ontology_dir, onto.getOntologyFileName());
				IRI toImport=IRI.create(ontoFile);
				OWLImportsDeclaration importDeclaraton = factory.getOWLImportsDeclaration(toImport);
				manager.applyChange(new AddImport(ont, importDeclaraton));
				manager.loadOntology(toImport);
			}
		}

		//add annotation properties
		OWLAnnotationProperty label = factory
				.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		/*
		    <owl:AnnotationProperty rdf:about="http://purl.obolibrary.org/obo/IAO_0000115">
	        	<rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">definition</rdfs:label>
	    	</owl:AnnotationProperty>
		 */
		OWLAnnotationProperty annotation = factory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000115"));
		OWLLiteral literal = factory.getOWLLiteral("definition");
		OWLAnnotation anno = factory.getOWLAnnotation(label,literal);
		OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(annotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*<owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym">
        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_broad_synonym</rdfs:label>
    	</owl:AnnotationProperty>*/
		annotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym"));
		literal = factory.getOWLLiteral("has_broad_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(annotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasExactSynonym">
	        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_exact_synonym</rdfs:label>
	    </owl:AnnotationProperty>*/
		annotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym"));
		literal = factory.getOWLLiteral("has_exact_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(annotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym">
	        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_narrow_synonym</rdfs:label>
	    </owl:AnnotationProperty>*/
		annotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym"));
		literal = factory.getOWLLiteral("has_narrow_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(annotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym">
	        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_related_synonym</rdfs:label>
	    </owl:AnnotationProperty>*/
		annotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym"));
		literal = factory.getOWLLiteral("has_related_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(annotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#created_by"/>*/
		annotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#created_by"));
		literal = factory.getOWLLiteral("created_by");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(annotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#creation_date"/>*/
		annotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#creation_date"));
		literal = factory.getOWLLiteral("creation_date");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(annotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		//create entity and quality classes
		PrefixManager pm = new DefaultPrefixManager(
				Configuration.etc_ontology_baseIRI+prefix.toLowerCase()+"#");

		OWLLiteral clabel = factory.getOWLLiteral("entity", "en");
		anno = factory.getOWLAnnotation(label, clabel);
		OWLClass entityClass = factory.getOWLClass(":entity", pm); //use ID, then create label
		axiom = factory.getOWLDeclarationAxiom(entityClass);
		manager.addAxiom(ont, axiom);
		axiom = factory.getOWLAnnotationAssertionAxiom(entityClass.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		OWLClass entity = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/BFO_0000001"));
		axiom = factory.getOWLEquivalentClassesAxiom(entity, entityClass);
		manager.addAxiom(ont, axiom);

		clabel = factory.getOWLLiteral("quality", "en");
		anno = factory.getOWLAnnotation(label, clabel);
		OWLClass qualityClass = factory.getOWLClass(":quality", pm); //use ID, then create label
		axiom = factory.getOWLDeclarationAxiom(qualityClass);
		manager.addAxiom(ont, axiom);
		axiom = factory.getOWLAnnotationAssertionAxiom(qualityClass.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		OWLClass patoQuality = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/PATO_0000001"));
		axiom = factory.getOWLEquivalentClassesAxiom(patoQuality, qualityClass);
		manager.addAxiom(ont, axiom);

		//disjoint entity and quality classes
		axiom = factory.getOWLDisjointClassesAxiom(entityClass, qualityClass);
		manager.addAxiom(ont, axiom);

		//has_part/part_of inverse object properties
		OWLObjectProperty hasPart = factory.getOWLObjectProperty(":has_part", pm);
		OWLObjectProperty partOf = factory.getOWLObjectProperty(":part_of", pm);
		manager.addAxiom(ont,
				factory.getOWLInverseObjectPropertiesAxiom(hasPart, partOf));

		manager.addAxiom(ont, factory.getOWLTransitiveObjectPropertyAxiom(partOf));
		manager.addAxiom(ont, factory.getOWLTransitiveObjectPropertyAxiom(hasPart));

		//save ontology to file
		manager.saveOntology(ont, documentIRI);	
		return info;
	}

	/**
	 * update ontologyFile with new submission
	 * @param submission
	 * @return the classId for the new term of this submission
	 */
	@Override
	public RPCResult<String> updateOntologyFile(OntologySubmission submission) throws Exception {
		RPCResult<String> result = new RPCResult<String>();
		// find the ontology file
		String ontoPrefix = submission.getLocalOntologyID();
		String filename = ToOntologiesDAO.getInstance().getOntologyFileName(ontoPrefix);
		File ontoFile = new File(Configuration.fileBase/*+ File.separator + authenticationToken.getUserId()*/,  filename+".owl");

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		//load ontology
		IRI ontologyIRI = IRI
				.create(Configuration.etc_ontology_baseIRI+submission.getLocalOntologyID().toLowerCase()+".owl");
		IRI documentIRI = IRI.create("file:/"+Configuration.fileBase+File.separator+filename+".owl");
		SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
		manager.addIRIMapper(mapper);
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(ontoFile);

		PrefixManager pm = new DefaultPrefixManager(
				Configuration.etc_ontology_baseIRI+submission.getLocalOntologyID().toLowerCase()+"#");

		updateOntology(ont, submission, manager, pm, result);


		/*int version = 0;
		IRI versionIRI = IRI.create(ontologyIRI + "/version"+version);
        OWLOntologyID newOntologyID = new OWLOntologyID(ontologyIRI, versionIRI);
        SetOntologyID setOntologyID = new SetOntologyID(ont, newOntologyID);
        manager.applyChange(setOntologyID);*/
		manager.saveOntology(ont, IRI.create(ontoFile.toURI()));
		return result;
	}


	/**
	 * 
	 * @param ont
	 * @param submission
	 * @param manager 
	 * @param pm 
	 * @return the classID of the new term.
	 */
	private void updateOntology(OWLOntology ont, OntologySubmission submission, OWLOntologyManager manager, PrefixManager pm, RPCResult<String> result) throws Exception{
		String newTerm = submission.getTerm();
		boolean asSynonym = submission.getSubmitAsSynonym();
		boolean isQuality = submission.getCategory().compareTo("structure")!=0;
		String definition = submission.getDefinition();
		String source = submission.getSource();
		String etcLocalID = submission.getLocalID();
		String[] otherIDs = submission.getOtherID()==null ||submission.getOtherID().trim().isEmpty()? null : submission.getOtherID().split("\\s*,\\s*");; //when this term exists in some other ontology, for example PATO
		String submittedBy = submission.getSubmittedBy();

		String sampleSent = submission.getSampleSentence();
		String [] isATerms = submission.getSuperClass().split("\\s*,\\s*");
		String [] partOfTerms = submission.getPartOfClass()==null || submission.getPartOfClass().trim().isEmpty() ? null : submission.getPartOfClass().split("\\s*,\\s*");
		String [] synonyms = submission.getSynonyms()==null ||  submission.getSynonyms().trim().isEmpty() ? null  : submission.getSynonyms().split("\\s*,\\s*");


		OWLDataFactory factory = manager.getOWLDataFactory();
		//get annotation properties and quality and entity classes
		OWLAnnotationProperty label = factory
				.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		OWLAnnotationProperty defAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000115"));
		OWLAnnotationProperty synAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym"));
		OWLAnnotationProperty createdByAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#created_by"));
		OWLAnnotationProperty creationDateAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#creation_date"));
		OWLClass quality = factory.getOWLClass(":quality", pm);
		OWLClass entity = factory.getOWLClass(":entity", pm);

		OWLClass newClass = null;
		OWLClass class4Syn = null;
		OWLClass superClass4Syn = null;
		OWLOntology ontology4Syn = null;
		//create and add the class for the newTerm
		if(asSynonym){
			if(otherIDs!=null){
				for(String otherID: otherIDs){
					if(otherID.isEmpty()) continue;
					OWLClass theClass = factory.getOWLClass(IRI.create(otherID));
					if(ontologyService.exists(ont, theClass) && getLabel(ont, theClass, label)!=null && getLabel(ont, theClass, label).compareToIgnoreCase(newTerm)!=0) {
						//synonym to a term in the internal ontology
						newClass = theClass;
						//add newterm as synonym
						OWLAnnotation anno = factory.getOWLAnnotation(synAnnotation, factory.getOWLLiteral(newTerm, "en"));
						OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), anno);
						manager.addAxiom(ont, axiom);
						Set<OWLClassExpression> supers = newClass.getSuperClasses(ont);
						Iterator<OWLClassExpression> it = supers.iterator();
						superClass4Syn =  (OWLClass) it.next();
						ontology4Syn = ont;
						class4Syn = newClass;
						break;
					}
				}	
			}
			if(otherIDs!=null){
				for(String otherID: otherIDs){
					if(otherID.isEmpty()) continue;
					OWLClass theClass = factory.getOWLClass(IRI.create(otherID));
					//if(! ontologyService.exists(ont, theClass)){ //synonym to a term in an external ontology
					Set<OWLOntology> importedOnts = ont.getImports();
					Iterator<OWLOntology> it = importedOnts.iterator();
					while(it.hasNext()){
						OWLOntology importedOnt = it.next();
						if(ontologyService.exists(importedOnt, theClass)){
							//create new class equivalent to theClass
							OWLLiteral clabel = factory.getOWLLiteral(getLabel(importedOnt, theClass, label));
							newClass = factory.getOWLClass(":"+getLabel(importedOnt, theClass, label).replaceAll("\\s+", "_"), pm); //no space in class id 
							OWLAnnotation labelAnno = factory.getOWLAnnotation(label, clabel);
							OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), labelAnno);
							manager.addAxiom(ont, axiom);

							manager.addAxiom(ont,  factory.getOWLEquivalentClassesAxiom(theClass, newClass));
							OWLAnnotation anno = factory.getOWLAnnotation(synAnnotation, factory.getOWLLiteral(newTerm, "en"));
							axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), anno);
							manager.addAxiom(ont, axiom);

							Set<OWLClassExpression> supers = theClass.getSuperClasses(importedOnt);
							Iterator<OWLClassExpression> sit = supers.iterator();
							superClass4Syn =  (OWLClass) sit.next();
							ontology4Syn = importedOnt;
							class4Syn = theClass;
							break;
						}
					}
					//}
				}	
			}
			//add other additional synonyms
			if(synonyms!=null){
				for(String syn : synonyms){
					if(!syn.isEmpty()){
						OWLAnnotation anno = factory.getOWLAnnotation(synAnnotation, factory.getOWLLiteral(syn, "en"));
						OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), anno);
						manager.addAxiom(ont, axiom);
					}
				}
			}
			//fill in more info to submission so the UI can present complete matching info.
			//submission.setTerm(getLabel(ontology4Syn, class4Syn, label)+ "[syn:"+newTerm+"]");
			submission.setDefinition(getDefinition(ontology4Syn, class4Syn, defAnnotation));
			submission.setPermanentID(class4Syn.getIRI().toString());
			submission.setSuperClass(superClass4Syn.getIRI().toString());	
			submission.setTmpID("");
		}else{
			newClass = factory.getOWLClass(":"+newTerm.replaceAll("\\s+", "_"), pm); //use ID, then create label
			if(ontologyService.exists(ont, newClass)){
				result.setSucceeded(true);
				result.setMessage("class '"+newTerm+"' exists and defined as:"+getDefinition(ont, newClass, defAnnotation));
				result.setData(null);
				return;
			}

			OWLLiteral clabel = factory.getOWLLiteral(newTerm, "en");
			OWLDeclarationAxiom declarationAxiom = factory
					.getOWLDeclarationAxiom(newClass);
			manager.addAxiom(ont, declarationAxiom);
			OWLAnnotation labelAnno = factory.getOWLAnnotation(label, clabel);
			OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), labelAnno);
			manager.addAxiom(ont, axiom);


			//equate this to otherID
			if(otherIDs!=null){
				for(String otherID: otherIDs){
					if(!otherID.isEmpty()){
						OWLClass eqClass = factory.getOWLClass(IRI.create(otherID));
						manager.addAxiom(ont,  factory.getOWLEquivalentClassesAxiom(eqClass, newClass));
					}
				}
			}

			//add definition annotation
			OWLAnnotation anno = factory.getOWLAnnotation(defAnnotation, factory.getOWLLiteral(definition, "en")); 
			axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), anno); 
			manager.addAxiom(ont, axiom);

			//add synonyms
			if(synonyms!=null){
				for(String synonym: synonyms){
					if(!synonym.isEmpty()){
						anno = factory.getOWLAnnotation(synAnnotation, factory.getOWLLiteral(synonym, "en"));
						axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), anno);
						manager.addAxiom(ont, axiom);
					}
				}
			}

			//add source info as comment
			source = source==null? "" : source;
			sampleSent = sampleSent==null? "" : sampleSent;
			if(!(sampleSent+source).trim().isEmpty()){
				anno = factory.getOWLAnnotation(factory.getRDFSComment(), factory.getOWLLiteral("source: "+sampleSent+"[taken from: "+source+"]", "en"));
				axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), anno);
				manager.addAxiom(ont, axiom);
			}
			//add created-by annotation
			anno = factory.getOWLAnnotation(createdByAnnotation, factory.getOWLLiteral(submittedBy));
			axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), anno);
			manager.addAxiom(ont, axiom);

			//add creation date annotation
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			anno = factory.getOWLAnnotation(creationDateAnnotation, factory.getOWLLiteral(dateFormat.format(date)));
			axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), anno);
			manager.addAxiom(ont, axiom);

			//add subclass axioms
			//if superTerm is an IRI (of known ontologies): 
			//if superTerm is a term (to local ontology):
			if(isATerms!=null){
				for(String superTerm: isATerms){//IRIs or terms
					if(superTerm.isEmpty()) continue;
					OWLClass superClass = null;
					if(superTerm.startsWith("http")){
						superClass = factory.getOWLClass(IRI.create(superTerm));
					}else{
						clabel = factory.getOWLLiteral(superTerm, "en");
						superClass = factory.getOWLClass(":"+superTerm.replaceAll("\\s+", "_"), pm); //use ID here.
						//label for the superClass
						labelAnno = factory.getOWLAnnotation(label, clabel);
						axiom = factory.getOWLAnnotationAssertionAxiom(superClass.getIRI(), labelAnno);
						manager.addAxiom(ont, axiom);
					}
					if(isQuality){//add a quality
						if(ontologyService.isA(ont, superClass, entity)){
							result.setMessage(result.getMessage()+" Can not add the quality term '"+newTerm+"' as a child to entity term '"+superTerm+"'.");
						}else{
							OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(newClass, superClass);
							manager.addAxiom(ont, subclassAxiom);  
							subclassAxiom = factory.getOWLSubClassOfAxiom(superClass, quality);
							manager.addAxiom(ont, subclassAxiom);
						}
					}else{ //add an entity
						if(ontologyService.isA(ont, superClass, quality)){
							result.setMessage(result.getMessage()+" Can not add the entity term '"+newTerm+"' as a child to quality term '"+superTerm+"'.");
						}else{
							OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(newClass, superClass);
							manager.addAxiom(ont, subclassAxiom);  
							subclassAxiom = factory.getOWLSubClassOfAxiom(superClass, entity);
							manager.addAxiom(ont, subclassAxiom);
						}
					}    	
				}
			}

			//add part_of restrictions
			if(partOfTerms!=null && partOfTerms.length>0){
				if(isQuality){
					result.setMessage(result.getMessage()+" Part Of terms are not allowed for quality terms.");
				}else{
					//subclasses of Entity
					OWLObjectProperty partOf = factory.getOWLObjectProperty(":part_of", pm);
					for(String wholeTerm: partOfTerms){//IRIs or terms
						if(wholeTerm.isEmpty()) continue;
						OWLClass wholeClass = null;
						if(wholeTerm.startsWith("http")){
							wholeClass = factory.getOWLClass(IRI.create(wholeTerm));
						}else{
							clabel = factory.getOWLLiteral(wholeTerm, "en");
							wholeClass = factory.getOWLClass(":"+wholeTerm.replaceAll("\\s+", "_"), pm); 
							//label for the whole class
							labelAnno = factory.getOWLAnnotation(label, clabel);
							axiom = factory.getOWLAnnotationAssertionAxiom(wholeClass.getIRI(), labelAnno);
							manager.addAxiom(ont, axiom);
						}

						if(ontologyService.isA(ont, wholeClass, quality)){
							result.setMessage(result.getMessage()+" Entity '"+newTerm+"' can not be a part of quality '"+wholeTerm+"'.");	        		
						}else{
							//part of restriction
							OWLClassExpression partOfTerm = factory.getOWLObjectSomeValuesFrom(partOf, wholeClass);
							axiom = factory.getOWLSubClassOfAxiom(newClass, partOfTerm);
							manager.addAxiom(ont, axiom);
							OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(wholeClass, entity);
							manager.addAxiom(ont, subclassAxiom);
						}
					}
				}
			}
		}

		//consistency checking
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		OWLReasonerConfiguration config = new SimpleConfiguration(
				progressMonitor);
		OWLReasoner reasoner = reasonerFactory.createReasoner(ont, config);
		reasoner.precomputeInferences();
		boolean consistent = reasoner.isConsistent();
		if(!consistent){
			Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
			Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
			StringBuffer sb = new StringBuffer("");
			if (!unsatisfiable.isEmpty()) {
				sb.append("If added the new term and associated properties, the following classes would become unsatisfiable: \n");
				for (OWLClass cls : unsatisfiable) {
					sb.append("    " + cls+"\n");
				}
				result.setData(null);
				result.setMessage(result.getMessage()+" "+sb.toString());
				result.setSucceeded(true);
				return;
			} 
		}
		//consistent, now update records and ontology
		submission.setPermanentID(newClass.getIRI().toString()); //accepted to internal ontology right away
		result.setData(getLabel(ont, newClass, label));
		result.setSucceeded(true);
		return;
	}



	private String getDefinition(OWLOntology ont, OWLClass cls, OWLAnnotationProperty defAnnotation) {
		for (OWLAnnotation annotation : cls.getAnnotations(ont, defAnnotation)) {
			if (annotation.getValue() instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) annotation.getValue();
				//if (val.hasLang("en")) {
				return val.getLiteral();
				//}
			}
		}
		return null;
	}

	private String getLabel(OWLOntology ont, OWLClass cls, OWLAnnotationProperty label) {
		for (OWLAnnotation annotation : cls.getAnnotations(ont, label)) {
			if (annotation.getValue() instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) annotation.getValue();
				//if (val.hasLang("en")) {
				return val.getLiteral();
				//}
			}
		}
		return null;
	}


	@Override
	public RPCResult<ArrayList<OntologyInfo>> getOntologyInfo(String userID) throws Exception {
		try{
			ArrayList<OntologyInfo> data = ToOntologiesDAO.getInstance().getOntologyInfoWithUserID(userID);
			return new RPCResult<ArrayList<OntologyInfo>>(true, "", data);
		}catch(Exception e){
			e.printStackTrace();
			return new RPCResult<ArrayList<OntologyInfo>>(true, e.toString(), null);
		}

	}

	/*@Override
	public boolean generateOntologyFile(String userID, String prefix) throws Exception{
		//TODO check
		File ontoFile = new File(Configuration.fileBase/*+ File.separator + authenticationToken.getUserId()*//*,  
				ToOntologiesDAO.getInstance().getOntologyFileName(prefix));
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		OWLOntology ont = manager.loadOntologyFromOntologyDocument(ontoFile);
		updateOntology(ont);
		manager.saveOntology(ont, IRI.create(ontoFile.toURI()));
		return true;
	}

	private void updateOntology(OWLOntology ont) {
		// TODO Auto-generated method stub

	}*/


}
