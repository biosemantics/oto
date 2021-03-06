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
import java.util.HashSet;
import java.util.Hashtable;
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
import org.semanticweb.owlapi.model.OWLEntity;
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

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

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
 * 
 * 
 * ontology creation use cases:
					 * add a synonym to a known class (local or external) => the synonym and its class will be in local
					 * add a new class from an external ontology to local => class and its related module will be added in the local 
					 * add a new term to local with a known class from the external ontology as its superclass => new class will be created, and the superclass and its module added in the local
					 * add a new term to local without a known class from the external ontology as its superclass, but with a super term that need also to be added to local => new class and its superclass will be created in the local and made descendant classes of entity/quality 
					 * add a new term without superclass => new class will be created and made subclass of entity/quality  

					 * 
					 * There will be cases where we'd like to add a new term to local, but there is not a sufficiently good superclass for the term in external ontology. In this case, we need to 
					 * add needed ancestor classes first to local. One of the ancestor class need to have a superclass in the external ontology. In rare cases where no superclass can be located in the 
					 * external ontology,  the added class will be a subclass of the top entity or quality class  
					  
 *
 */
public class OntologyFileServiceImpl extends RemoteServiceServlet implements OntologyFileService{

	/**
	 * 
	 */
	private static final long serialVersionUID = -668703714442141846L;
	private OntologyServiceImpl ontologyService = new OntologyServiceImpl();
	static Hashtable<String, OWLOntology> referencedOntologies = null;
	//one manager manages all the ontologies files, check if an ontology is already being managed before load it, create ontologyIRI and documentIRI mapping for each loaded ontology.
	private static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private static OWLDataFactory factory = manager.getOWLDataFactory();
	private static OWLClass entity = null;
	private static OWLClass quality = null;
	private static OWLObjectProperty partOf = null;
	private static OWLAnnotationProperty label = null;
	private static OWLAnnotationProperty synAnnotation = null;
	private static OWLAnnotationProperty defAnnotation =null;
	private static OWLAnnotationProperty creationDateAnnotation = null;
	private static OWLAnnotationProperty createdByAnnotation = null;
	private static OWLAnnotationProperty rSynAnnotation = null;
	private static OWLAnnotationProperty nSynAnnotation = null;
	private static OWLAnnotationProperty eSynAnnotation = null;
	private static OWLAnnotationProperty bSynAnnotation = null;
	

	static{
		entity = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/CARO_0000006")); //material anatomical entity
		quality = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/PATO_0000001")); //quality
		partOf = factory.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/BFO_0000050"));
		label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		synAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym"));
		defAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000115"));
		creationDateAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#creation_date"));
		createdByAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#created_by"));
		rSynAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym"));
		nSynAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym"));
		eSynAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym"));
		bSynAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym"));
	}
		
	

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
		//OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//OntologyIRI and DocumentIRI mapping, create ontology
		IRI ontologyIRI = IRI
				.create(Configuration.etc_ontology_baseIRI+prefix.toLowerCase()+".owl");
		IRI documentIRI = IRI.create(newOnto);
		SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
		manager.addIRIMapper(mapper);
		OWLOntology ont = manager.createOntology(ontologyIRI);
		//OWLDataFactory factory = manager.getOWLDataFactory();
		//add namespaces

		//import base ontologies: general
		//ArrayList<OntologyInfo> ontos = ToOntologiesDAO.getInstance().getOntologyInfoWithTaxonGroup(taxonGroup);
		ArrayList<OntologyInfo> ontos = ToOntologiesDAO.getInstance().getOntologyInfoWithTaxonGroup("general"); //general includes RO.
		for(OntologyInfo onto: ontos){
			if(!onto.getOntologyPrefix().startsWith("ETC_")){ //upload files from server, add live access to ontology url later
				File ontoFile = new File(Configuration.ontology_dir, onto.getOntologyFileName());
				IRI toImport=IRI.create(ontoFile);
				mapper = new SimpleIRIMapper(IRI.create(Configuration.oboOntologyBaseIRI+onto.getOntologyFileName()), toImport);
				manager.addIRIMapper(mapper);
				OWLImportsDeclaration importDeclaraton = factory.getOWLImportsDeclaration(toImport);
				manager.applyChange(new AddImport(ont, importDeclaraton));
				if(manager.getOntology(toImport)==null) manager.loadOntology(toImport);	
			}
		}

		//add annotation properties
		//OWLAnnotationProperty label = factory
		//		.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		/*
		    <owl:AnnotationProperty rdf:about="http://purl.obolibrary.org/obo/IAO_0000115">
	        	<rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">definition</rdfs:label>
	    	</owl:AnnotationProperty>
		 */
		//OWLAnnotationProperty annotation = factory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000115"));
		OWLLiteral literal = factory.getOWLLiteral("definition");
		OWLAnnotation anno = factory.getOWLAnnotation(label,literal);
		OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(defAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*<owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym">
        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_broad_synonym</rdfs:label>
    	</owl:AnnotationProperty>*/
		
		literal = factory.getOWLLiteral("has_broad_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(bSynAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasExactSynonym">
	        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_exact_synonym</rdfs:label>
	    </owl:AnnotationProperty>*/
		literal = factory.getOWLLiteral("has_exact_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(eSynAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym">
	        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_narrow_synonym</rdfs:label>
	    </owl:AnnotationProperty>*/
		literal = factory.getOWLLiteral("has_narrow_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(nSynAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym">
	        <rdfs:label rdf:datatype="http://www.w3.org/2001/XMLSchema#string">has_related_synonym</rdfs:label>
	    </owl:AnnotationProperty>*/
		literal = factory.getOWLLiteral("has_related_synonym");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(rSynAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#created_by"/>*/
		literal = factory.getOWLLiteral("created_by");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(createdByAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		/*
	    <owl:AnnotationProperty rdf:about="http://www.geneontology.org/formats/oboInOwl#creation_date"/>*/
		literal = factory.getOWLLiteral("creation_date");
		anno = factory.getOWLAnnotation(label,literal);
		axiom = factory.getOWLAnnotationAssertionAxiom(creationDateAnnotation.getIRI(), anno);
		manager.addAxiom(ont, axiom);

		//entity and quality classes and part_of, has_part relations are imported from ro, a "general" ontology
		//PrefixManager pm = new DefaultPrefixManager(
		//		Configuration.etc_ontology_baseIRI+prefix.toLowerCase()+"#");

		
		/*OWLClass entity = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/CARO_0000006")); //material anatomical entity
		OWLLiteral clabel = factory.getOWLLiteral("material anatomical entity", "en");
		axiom = factory.getOWLDeclarationAxiom(entity);
		manager.addAxiom(ont, axiom);
		axiom = factory.getOWLAnnotationAssertionAxiom(entity.getIRI(), factory.getOWLAnnotation(label, clabel));
		manager.addAxiom(ont, axiom);

		OWLClass quality = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/PATO_0000001")); //quality
		clabel = factory.getOWLLiteral("quality", "en");
		axiom = factory.getOWLDeclarationAxiom(entity);
		manager.addAxiom(ont, axiom);
		axiom = factory.getOWLAnnotationAssertionAxiom(entity.getIRI(), factory.getOWLAnnotation(label, clabel));
		manager.addAxiom(ont, axiom);
		




		//has_part/part_of inverse object properties
		OWLObjectProperty hasPart = factory.getOWLObjectProperty(":has_part", pm);
		OWLObjectProperty partOf = factory.getOWLObjectProperty(":part_of", pm);
		manager.addAxiom(ont,
				factory.getOWLInverseObjectPropertiesAxiom(hasPart, partOf));

		manager.addAxiom(ont, factory.getOWLTransitiveObjectPropertyAxiom(partOf));
		manager.addAxiom(ont, factory.getOWLTransitiveObjectPropertyAxiom(hasPart));
		*/
		//disjoint entity and quality classes
		axiom = factory.getOWLDisjointClassesAxiom(entity, quality);
		manager.addAxiom(ont, axiom);
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
		//OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		RPCResult<String> result = new RPCResult<String>();
		// find the ontology file
		String ontoPrefix = submission.getOntologyID();
		if(!ontoPrefix.startsWith("ETC_")){
			result.setMessage("At this time, Target ontology can only be an ETC ontology");
			result.setData(null);
			result.setSucceeded(true);
			return result;
		}

		//preload PO, PATO, HAO, PORO, and RO
		if(referencedOntologies==null){
			referencedOntologies = new Hashtable<String, OWLOntology>();
			File[] ontoFiles = new File(Configuration.ontology_dir).listFiles();
			for(File ontoFile: ontoFiles){
				OWLOntology onto =manager.loadOntologyFromOntologyDocument(ontoFile);//loading ontologies takes time, preload them here.
				referencedOntologies.put(ontoFile.getName().replaceFirst(".owl$", ""), onto);
				SimpleIRIMapper mapper = new SimpleIRIMapper(IRI.create(Configuration.oboOntologyBaseIRI+ontoFile.getName()), IRI.create(ontoFile));
				manager.addIRIMapper(mapper);
			}
		}
		
		String filename = ToOntologiesDAO.getInstance().getOntologyFileName(ontoPrefix);
		File ontoFile = new File(Configuration.fileBase/*+ File.separator + authenticationToken.getUserId()*/,  filename+".owl");
		OWLOntology ont = null;
		if(referencedOntologies.get(ontoPrefix.toLowerCase())==null){
			//load ontology
			IRI ontologyIRI = IRI
					.create(Configuration.etc_ontology_baseIRI+submission.getOntologyID().toLowerCase()+".owl");
			IRI documentIRI = IRI.create("file:/"+Configuration.fileBase+File.separator+filename+".owl");
			SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
			manager.addIRIMapper(mapper);
			ont = manager.loadOntologyFromOntologyDocument(ontoFile);
			referencedOntologies.put(ontoPrefix.toLowerCase(), ont);
		}else{
			ont = referencedOntologies.get(ontoPrefix.toLowerCase());
		}
		
		
		PrefixManager pm = new DefaultPrefixManager(
				Configuration.etc_ontology_baseIRI+submission.getOntologyID().toLowerCase()+"#");

		updateOntology(ont, submission, manager, pm, result);


		/*int version = 0;
		IRI versionIRI = IRI.create(ontologyIRI + "/version"+version);
        OWLOntologyID newOntologyID = new OWLOntologyID(ontologyIRI, versionIRI);
        SetOntologyID setOntologyID = new SetOntologyID(ont, newOntologyID);
        manager.applyChange(setOntologyID);*/
		manager.saveOntology(ont, IRI.create(ontoFile.toURI()));
		return result;
	}

	private void updateOntology(OWLOntology ont, OntologySubmission submission, OWLOntologyManager manager, PrefixManager pm, RPCResult<String> result) throws Exception{
		 //initialize a reasoner
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		OWLReasonerConfiguration config = new SimpleConfiguration(
				progressMonitor);
		OWLReasoner reasoner = reasonerFactory.createReasoner(ont, config);

		boolean asSynonym = submission.getSubmitAsSynonym();
		if(asSynonym){
			updateOntologyWithNewSynonym(ont, submission,  pm, result, reasoner);
		}else{
			updateOntologyWithNewClass(ont, submission,  pm, result, reasoner);
		}
		
		//consistency checking, if not consistent, the problem need to be fixed manually. 
		reasoner.precomputeInferences();
		boolean consistent = reasoner.isConsistent();
		if(!consistent){
			Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
			Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
			StringBuffer sb = new StringBuffer("");
			if (!unsatisfiable.isEmpty()) {
				sb.append("Warning: After the additions, the following classes have become unsatisfiable. Edit the ontology in protege to correct the problems. \n");
				for (OWLClass cls : unsatisfiable) {
					sb.append("    " + cls+"\n");
				}
				result.setData(null);
				result.setMessage(result.getMessage()+" "+sb.toString());
				result.setSucceeded(true);
				return;
			} 
		}
		return;
	}


	/**
	 * add a synonym to onto
	 * 
	 * @param ont
	 * @param submission
	 * @param manager
	 * @param pm
	 * @param result
	 * @throws Exception
	 */
	private void updateOntologyWithNewSynonym(OWLOntology ont, OntologySubmission submission, PrefixManager pm, RPCResult<String> result, OWLReasoner reasoner) throws Exception{
       
		//collect data
		String newTerm = submission.getTerm();
		boolean isQuality = submission.getEorQ().compareTo("quality")==0;
		//String source = submission.getSource();
		//String etcLocalID = submission.getLocalID();
		String[] classIDs = submission.getClassID()==null ||submission.getClassID().trim().isEmpty()? null : submission.getClassID().split("\\s*,\\s*");
		//String submittedBy = submission.getSubmittedBy();
		//String sampleSent = submission.getSampleSentence();
		String [] synonyms = submission.getSynonyms()==null ||  submission.getSynonyms().trim().isEmpty() ? null  : submission.getSynonyms().split("\\s*,\\s*");

		ArrayList<OWLClass> class4Syn = new ArrayList<OWLClass>();//in the same order
		ArrayList<String> superClass4Syn = new ArrayList<String>();//as the above
		ArrayList<OWLOntology> ontology4Syn = new ArrayList<OWLOntology> ();//as the above


		if(classIDs!=null){
			for(String classID: classIDs){ //add syn to each of the classes in current ontology class signature
				if(classID.isEmpty()) continue;
				OWLClass theClass = factory.getOWLClass(IRI.create(classID));
				boolean exist = ontologyService.exists(ont, theClass);
				if( exist && getLabel(theClass, factory)!=null && getLabel(theClass, factory).compareToIgnoreCase(newTerm)!=0) {
					//class exists in current/imported ontology => add syn
					addSynonymToClass(ont, manager, newTerm, factory, class4Syn, superClass4Syn,
							ontology4Syn, theClass);
				}else if(!exist){
					//an external class does not exist => add class, then add syn	
					theClass = addModuleOfClass(ont, result, reasoner,
							newTerm, isQuality, classID, quality, entity);  
					
					addSynonymToClass(ont, manager, newTerm, factory, class4Syn, superClass4Syn,
							ontology4Syn, theClass);					
				}
			}	
		}
		/*if(classIDs!=null){
			for(String classID: classIDs){ //add syn to each of the classes in the imported ontologies
				if(classID.isEmpty()) continue;
				OWLClass theClass = factory.getOWLClass(IRI.create(classID));
				Set<OWLOntology> importedOnts = ont.getImports();
				Iterator<OWLOntology> it = importedOnts.iterator();
				//synonym to a term in an imported ontology
				while(it.hasNext()){
					OWLOntology importedOnt = it.next();
					if(ontologyService.exists(importedOnt, theClass)){
						//create new class equivalent to theClass
						OWLLiteral clabel = factory.getOWLLiteral(getLabel(theClass, label));
						OWLClass localClass = factory.getOWLClass(":"+getLabel(theClass, label).replaceAll("\\s+", "_"), pm); //no space in class id 
						OWLAnnotation labelAnno = factory.getOWLAnnotation(label, clabel);
						OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(localClass.getIRI(), labelAnno);
						manager.addAxiom(ont, axiom);

						manager.addAxiom(ont,  factory.getOWLEquivalentClassesAxiom(theClass, localClass));
						OWLAnnotation anno = factory.getOWLAnnotation(synAnnotation, factory.getOWLLiteral(newTerm, "en"));
						axiom = factory.getOWLAnnotationAssertionAxiom(localClass.getIRI(), anno);
						manager.addAxiom(ont, axiom);

						Set<OWLClassExpression> supers = theClass.getSuperClasses(importedOnt);
						Iterator<OWLClassExpression> sit = supers.iterator();
						String superClassesString ="";
						while(sit.hasNext()){
							OWLClassExpression oce = sit.next();
							if(oce instanceof OWLClass){ 
								superClassesString += ((OWLClass) oce).getIRI().toString()+",";
							}
						}
						superClass4Syn.add(superClassesString);
						ontology4Syn.add(importedOnt);
						class4Syn.add(localClass);
					}
				}
				//}
			}	
		}*/
		//add other additional synonyms
		if(synonyms!=null){
			for(String syn : synonyms){
				if(!syn.isEmpty()){
					OWLAnnotation anno = factory.getOWLAnnotation(synAnnotation, factory.getOWLLiteral(syn, "en"));
					for(OWLClass newClass: class4Syn){
						OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), anno);
						manager.addAxiom(ont, axiom);
					}
				}
			}
		}
		//fill in more info to submission so the UI can present complete matching info.
		//String termString = "[syn:"+newTerm+"]";
		String termString = "";
		String defString = "";
		String idString = "";
		String superString = "";
		for(int i = 0; i < ontology4Syn.size(); i++){
			termString +=getLabel(class4Syn.get(i), factory)+";";
			defString +=getDefinition(class4Syn.get(i), factory)+";";
			idString += class4Syn.get(i).getIRI().toString()+";";
			superString += superClass4Syn.get(i).toString()+";";
		}

		
		submission.setClassID(termString.replaceFirst(";$", ""));
		submission.setDefinition(defString.replaceFirst(";$", ""));
		submission.setPermanentID(idString.replaceFirst(";$", ""));
		submission.setSuperClass(superString.replaceFirst(";$", ""));	
		submission.setTmpID("");

		//now update records and ontology, check for consistency later
		result.setData(newTerm);
		result.setSucceeded(true);
		return;
	}

	private void addSynonymToClass(OWLOntology ont, OWLOntologyManager manager,
			String synonym, OWLDataFactory factory,
			ArrayList<OWLClass> class4Syn,
			ArrayList<String> superClass4Syn,
			ArrayList<OWLOntology> ontology4Syn, OWLClass theClass)
			throws Exception {
		//synonym to a term in the internal ontology
		OWLAnnotationProperty synAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym"));
		OWLAnnotation anno = factory.getOWLAnnotation(synAnnotation, factory.getOWLLiteral(synonym, "en"));
		OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(theClass.getIRI(), anno);
		manager.addAxiom(ont, axiom);
		//find superclass 
		Set<OWLClassExpression> supers = theClass.getSuperClasses(fetchParentOntology(theClass.getIRI()));
		Iterator<OWLClassExpression> it = supers.iterator();
		String superClassesString ="";
		while(it.hasNext()){
			OWLClassExpression oce = it.next();
			if(oce instanceof OWLClass){ 
				superClassesString += getLabel((OWLClass)oce, factory)+",";
			}
		}
		superClass4Syn.add(superClassesString);
		ontology4Syn.add(fetchParentOntology(theClass.getIRI()));
		class4Syn.add(theClass);
	}

	/**
	 * 
	 * @param ont
	 * @param submission
	 * @param manager 
	 * @param pm 
	 * @return the classID of the new term.
	 */
	private void updateOntologyWithNewClass(OWLOntology ont, OntologySubmission submission, PrefixManager pm, RPCResult<String> result, OWLReasoner reasoner) throws Exception{
		//collect data
		String newTerm = submission.getTerm();
		boolean isQuality = submission.getEorQ().compareTo("quality")==0;
		String definition = submission.getDefinition();
		String source = submission.getSource();
		String etcLocalID = submission.getLocalID();
		String classID = submission.getClassID();
		String submittedBy = submission.getSubmittedBy();

		String sampleSent = submission.getSampleSentence();
		String [] isATerms = submission.getSuperClass().split("\\s*,\\s*");
		String [] partOfTerms = submission.getPartOfClass()==null || submission.getPartOfClass().trim().isEmpty() ? null : submission.getPartOfClass().split("\\s*,\\s*");
		String [] synonyms = submission.getSynonyms()==null ||  submission.getSynonyms().trim().isEmpty() ? null  : submission.getSynonyms().split("\\s*,\\s*");

		OWLClass newClass = null;
		if(classID !=null && classID.length()>0){
			newClass = factory.getOWLClass(IRI.create(classID));
		}else{
			//create and add the class for the newTerm
			newClass = factory.getOWLClass(":"+newTerm.replaceAll("\\s+", "_"), pm); //use ID, then create label
		}
		
		if(ontologyService.exists(ont, newClass)){
			result.setSucceeded(true);
			result.setMessage("class '"+newTerm+"' exists and defined as:"+getDefinition(newClass, factory));
			result.setData(null);
			return;
		}

		boolean extractedSuperclassModule = false;
		if(classID !=null && classID.length()>0){
			newClass = addModuleOfClass(ont, result, reasoner,
					newTerm, isQuality, classID, quality, entity);    	
			extractedSuperclassModule = true;
		}
		
		
		if(! extractedSuperclassModule){
			//add label
			OWLLiteral clabel = factory.getOWLLiteral(newTerm, "en");
			OWLDeclarationAxiom declarationAxiom = factory
					.getOWLDeclarationAxiom(newClass);
			manager.addAxiom(ont, declarationAxiom);
			OWLAnnotation labelAnno = factory.getOWLAnnotation(label, clabel);
			OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), labelAnno);
			manager.addAxiom(ont, axiom);
			//add definition annotation
			OWLAnnotation anno = factory.getOWLAnnotation(defAnnotation, factory.getOWLLiteral(definition, "en")); 
			axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), anno); 
			manager.addAxiom(ont, axiom);
			
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
		}


		//equate this to otherID: this would take all the axioms defined for that ID
		/*if(otherIDs!=null){
			for(String otherID: otherIDs){
				if(!otherID.isEmpty()){
					OWLClass eqClass = factory.getOWLClass(IRI.create(otherID));
					manager.addAxiom(ont,  factory.getOWLEquivalentClassesAxiom(eqClass, newClass));
				}
			}
		}*/
		//add synonyms
		if(synonyms!=null){
			for(String synonym: synonyms){
				if(!synonym.isEmpty()){
					OWLAnnotation anno = factory.getOWLAnnotation(eSynAnnotation, factory.getOWLLiteral(synonym, "en"));
					OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(newClass.getIRI(), anno);
					manager.addAxiom(ont, axiom);
				}
			}
		}
		
		OWLLiteral clabel;
		OWLAnnotation labelAnno;
		OWLAxiom axiom;
		//add subclass axioms
		//if superTerm is an IRI (of known ontologies): 
		//if superTerm is a term (to local ontology):
		if(isATerms!=null && !extractedSuperclassModule){
			for(String superTerm: isATerms){//IRIs or terms
				if(superTerm.isEmpty()) continue;
				OWLClass superClass = null; //the superClass
				Set<OWLClass> introducedClasses = new HashSet<OWLClass> ();//to hold all classes related to the superClass
				if(superTerm.startsWith("http")){
					superClass = factory.getOWLClass(IRI.create(superTerm)); //extract mireot module related to superClass
					OWLOntology moduleOnto = extractModule(ont, reasoner, superClass);
					introducedClasses.addAll(moduleOnto.getClassesInSignature());
				}else{//allow to create a new superClass in local ontology, which will be a subclass of entity/quality
					clabel = factory.getOWLLiteral(superTerm, "en");
					superClass = factory.getOWLClass(":"+superTerm.replaceAll("\\s+", "_"), pm); //use ID here.
					introducedClasses.add(superClass);
					//label for the superClass
					labelAnno = factory.getOWLAnnotation(label, clabel);
					axiom = factory.getOWLAnnotationAssertionAxiom(superClass.getIRI(), labelAnno);
					manager.addAxiom(ont, axiom);
					//what about definition for superClass?
					
					//add created-by annotation
					axiom = factory.getOWLAnnotationAssertionAxiom(superClass.getIRI(), factory.getOWLAnnotation(createdByAnnotation, factory.getOWLLiteral(submittedBy)));
					manager.addAxiom(ont, axiom);

					//add creation date annotation
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
					Date date = new Date();
					axiom = factory.getOWLAnnotationAssertionAxiom(superClass.getIRI(),  factory.getOWLAnnotation(creationDateAnnotation, factory.getOWLLiteral(dateFormat.format(date))));
					manager.addAxiom(ont, axiom);
				}
				
				//make all added class subclass of quality/entity
				if(isQuality){//add a quality
					if(ontologyService.isA(ont, superClass, entity)){
						result.setMessage(result.getMessage()+" Can not add the quality term '"+newTerm+"' as a child to entity term '"+superTerm+"'.");
					}else{
						OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(newClass, superClass);
						manager.addAxiom(ont, subclassAxiom);
						for(OWLClass claz: introducedClasses){
							subclassAxiom = factory.getOWLSubClassOfAxiom(claz, quality);
							manager.addAxiom(ont, subclassAxiom);
						}
					}
				}else{ //add an entity
					if(ontologyService.isA(ont, superClass, quality)){
						result.setMessage(result.getMessage()+" Can not add the entity term '"+newTerm+"' as a child to quality term '"+superTerm+"'.");
					}else{
						OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(newClass, superClass);
						manager.addAxiom(ont, subclassAxiom);  
						for(OWLClass claz: introducedClasses){
							subclassAxiom = factory.getOWLSubClassOfAxiom(claz, entity);
							manager.addAxiom(ont, subclassAxiom);
						}
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
				for(String wholeTerm: partOfTerms){//IRIs or terms
					if(wholeTerm.isEmpty()) continue;
					OWLClass wholeClass = null;
					Set<OWLClass> introducedClasses = new HashSet<OWLClass> ();//to hold all classes related to the wholeClass
					if(wholeTerm.startsWith("http")){
						//external 
						wholeClass = factory.getOWLClass(IRI.create(wholeTerm)); //extract module
						OWLOntology moduleOnto = extractModule(ont, reasoner,  wholeClass);
						introducedClasses.addAll(moduleOnto.getClassesInSignature());
					}else{
						//local
						clabel = factory.getOWLLiteral(wholeTerm, "en");
						wholeClass = factory.getOWLClass(":"+wholeTerm.replaceAll("\\s+", "_"), pm); 
						introducedClasses.add(wholeClass);
						//label for the whole class
						labelAnno = factory.getOWLAnnotation(label, clabel);
						axiom = factory.getOWLAnnotationAssertionAxiom(wholeClass.getIRI(), labelAnno);
						manager.addAxiom(ont, axiom);
						//what about definition for wholeTerm?
						//add created-by annotation
						axiom = factory.getOWLAnnotationAssertionAxiom(wholeClass.getIRI(), factory.getOWLAnnotation(createdByAnnotation, factory.getOWLLiteral(submittedBy)));
						manager.addAxiom(ont, axiom);

						//add creation date annotation
						DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
						Date date = new Date();
						axiom = factory.getOWLAnnotationAssertionAxiom(wholeClass.getIRI(),  factory.getOWLAnnotation(creationDateAnnotation, factory.getOWLLiteral(dateFormat.format(date))));
						manager.addAxiom(ont, axiom);
					}

					if(ontologyService.isA(ont, wholeClass, quality)){
						result.setMessage(result.getMessage()+" Entity '"+newTerm+"' can not be a part of quality '"+wholeTerm+"'.");	        		
					}else{
						//part of restriction
						OWLClassExpression partOfTerm = factory.getOWLObjectSomeValuesFrom(partOf, wholeClass);
						axiom = factory.getOWLSubClassOfAxiom(newClass, partOfTerm);
						manager.addAxiom(ont, axiom);
						for(OWLClass claz: introducedClasses){
							OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(claz, entity);
							manager.addAxiom(ont, subclassAxiom);
						}
					}
				}
			}
		}

		//now update records and ontology and check for consistency later
		submission.setPermanentID(newClass.getIRI().toString()); //accepted to internal ontology right away
		result.setData(newTerm);
		result.setSucceeded(true);
		return;
	}

	private OWLClass addModuleOfClass(OWLOntology ont, RPCResult<String> result,
			OWLReasoner reasoner, String newTerm, boolean isQuality,
			String classID, OWLClass quality,
			OWLClass entity) throws SQLException, Exception {
		OWLClass newClass;
		newClass = factory.getOWLClass(IRI.create(classID));
		OWLOntology moduleOnto = extractModule(ont, reasoner, newClass);
		OWLAxiom subclassAxiom;
		//make all added class subclass of quality/entity
		if(isQuality){//add a quality
			if(ontologyService.isA(ont, newClass, entity)){
				result.setMessage(result.getMessage()+" Can not add the quality term '"+newTerm+"' as a child to entity term '"+newTerm+"'.");
			}else{
				for(OWLClass claz: moduleOnto.getClassesInSignature()){
					subclassAxiom = factory.getOWLSubClassOfAxiom(claz, quality);
					manager.addAxiom(ont, subclassAxiom);
				}
			}
		}else{ //add an entity
			if(ontologyService.isA(ont, newClass, quality)){
				result.setMessage(result.getMessage()+" Can not add the entity term '"+newTerm+"' as a child to quality term '"+newTerm+"'.");
			}else{
				for(OWLClass claz: moduleOnto.getClassesInSignature()){
					subclassAxiom = factory.getOWLSubClassOfAxiom(claz, entity);
					manager.addAxiom(ont, subclassAxiom);
				}
			}
		}
		return newClass;
	}

	private OWLOntology extractModule(OWLOntology to_ont,
			 OWLReasoner reasoner,
		     OWLClass claz)
			throws SQLException, Exception {
		OWLOntology ontology = fetchParentOntology(claz.getIRI());
		//create and save inference-entailment module as an ontology file
		
		Set<OWLEntity> seeds = new HashSet<OWLEntity>();
		seeds.add(claz);
		File mod = new File(Configuration.fileBase, "module."+getLabel(claz, factory)+".owl");
		IRI moduleIRI = IRI.create(mod);
		if(mod.exists()){
			//remove the existing module -- in effect replace the old module with the new one.
			manager.removeOntology(manager.getOntology(moduleIRI));;
			mod.delete();
		}
		SyntacticLocalityModuleExtractor sme = new SyntacticLocalityModuleExtractor(
				manager, ontology, ModuleType.STAR);
		OWLOntology moduleOnto = sme.extractAsOntology(seeds, moduleIRI, -1, 0, reasoner); //take all superclass and no subclass into the seeds.
		manager.saveOntology(moduleOnto, moduleIRI);
		//import the module ontology to current onto in memory
		OWLImportsDeclaration importDeclaraton = factory.getOWLImportsDeclaration(moduleIRI);
		manager.applyChange(new AddImport(to_ont, importDeclaraton));
		if(manager.getOntology(moduleIRI)==null) manager.loadOntology(moduleIRI);
		return moduleOnto;
	}



	private OWLOntology fetchParentOntology(IRI claz) throws Exception {
		//get prefix
		String prefix = claz.toString().substring(claz.toString().lastIndexOf("/")+1);
		prefix = prefix.substring(0, prefix.indexOf("_")).toLowerCase();
		return (OWLOntology) referencedOntologies.get(prefix);
	}

	private String getDefinition(OWLClass cls, OWLDataFactory factory) throws Exception {
		//use cls to determine which ontology to use to find the def.
		OWLAnnotationProperty defAnnotation = factory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000115"));
		OWLOntology ont = fetchParentOntology(cls.getIRI());
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

	private String getLabel(OWLClass cls, OWLDataFactory factory) throws Exception {
		//use cls to determine which ontology to use to find the label.
		OWLAnnotationProperty label = factory
				.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		OWLOntology ont = fetchParentOntology(cls.getIRI());
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
