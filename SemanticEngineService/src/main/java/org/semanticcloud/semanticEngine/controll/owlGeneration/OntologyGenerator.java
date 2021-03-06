package org.semanticcloud.semanticEngine.controll.owlGeneration;

import org.semanticcloud.semanticEngine.model.conditions.ClassCondition;
import org.semanticcloud.semanticEngine.model.ConfigurationException;
import org.semanticcloud.semanticEngine.model.OntologyUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OntologyGenerator {
	private static OntologyGenerator instance;
	private static OWLDataFactory factory;
	private static OWLOntologyManager manager;
	
	private final ClassRestrictionGenerator classRestrictionGenerator;
	private IndividualGenerator individualGenerator;

	public ClassRestrictionGenerator getClassRestrictionGenerator() {
		return classRestrictionGenerator;
	}
	
	public IndividualGenerator getIndividualGenerator() {
		return individualGenerator;
	}
	
	public static void setGlobalInstance(OntologyGenerator kb) {
		instance = kb;
	}

	public static OntologyGenerator getGlobalInstance() {
		return instance;
	}
	
	public static OWLDataFactory getOwlApiFactory(){
		return factory;
	}
	

	public static void initialize(String uri, String localOntologyFolder) {
		setGlobalInstance(loadFromFile(uri, localOntologyFolder));
	}

	public static OntologyGenerator loadFromFile(String uri, String localOntologyFolder) {
		manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();
		
		return new OntologyGenerator();
	}
	
	private OntologyGenerator() {
		classRestrictionGenerator = new ClassRestrictionGenerator(factory);
		individualGenerator = new IndividualGenerator(factory);
	}

	public String convertToOwlClass(String classUri, ClassCondition condition) {
		OWLOntology destinationOntology;
		try {
            IRI iri = IRI.create(classUri);

            String ontologyIRI = OntologyUtils.getNamespace(iri);

            destinationOntology = manager.createOntology(IRI.create(ontologyIRI));

			OWLClassExpression resultExpression = getClassRestrictionGenerator().convertToOntClass(classUri, condition);
			
			addToOntologyAsClass(destinationOntology, resultExpression, classUri);
			
			return serializeToString(destinationOntology);
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}			
	}

    public String convertToOwlIndividual(String individualUri, ClassCondition condition) {
        OWLOntology destinationOntology = convertToOwlIndividualOntology(individualUri, condition);

        try {
            return serializeToString(destinationOntology);
        } catch (OWLOntologyStorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

	public String convertToOwlIndividual2(String individualUri, ClassCondition condition) {
		OWLOntology destinationOntology;
		try {
			destinationOntology = manager.createOntology();

			List<OWLAxiom> axioms = getIndividualGenerator().convertToOntIndividual(individualUri, condition);
			
			addToOntologyAsIndividualDescription(destinationOntology, axioms);
			
			return serializeToString(destinationOntology);
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
	}

	private void addToOntologyAsIndividualDescription(OWLOntology destinationOntology, List<OWLAxiom> axioms) {
		for (OWLAxiom owlAxiom : axioms) {
			manager.addAxiom(destinationOntology, owlAxiom);
		}		
	}

    private void addToOntologyAsClass(OWLOntology destinationOntology, OWLClassExpression resultExpression, String conditionUri) {
        OWLClass resultClass = factory.getOWLClass(IRI.create(conditionUri));

        OWLAxiom equivalentClassAxiom = factory.getOWLEquivalentClassesAxiom(resultClass, resultExpression);
        addImportDeclarations(destinationOntology, new ArrayList<OWLAxiom>(
                Arrays.asList(new OWLAxiom[] { equivalentClassAxiom })));

        manager.addAxiom(destinationOntology, equivalentClassAxiom);
    }

	private void addToOntologyAsClass2(OWLOntology destinationOntology, OWLClassExpression resultExpression, String conditionUri) {
		OWLClass resultClass = factory.getOWLClass(IRI.create(conditionUri));
		
		OWLAxiom equivalentClassAxiom = factory.getOWLEquivalentClassesAxiom(resultClass, resultExpression);
		
		manager.addAxiom(destinationOntology, equivalentClassAxiom);
	}

    private String serializeToString(OWLOntology destinationOntology) throws OWLOntologyStorageException {
        return serializeToString(destinationOntology, true);
    }

	private String serializeToString2(OWLOntology destinationOntology) throws OWLOntologyStorageException {
		StringDocumentTarget serializationTarget = new StringDocumentTarget();
		
		manager.saveOntology(destinationOntology, serializationTarget);
		return serializationTarget.toString();
	}


    public OWLOntology convertToOwlClassOntology(String classUri, ClassCondition condition) {
        OWLOntology destinationOntology;
        try {
            IRI iri = IRI.create(classUri);

            String ontologyIRI = OntologyUtils.getNamespace(iri);

            destinationOntology = manager.createOntology(IRI.create(ontologyIRI));

            OWLClassExpression resultExpression = getClassRestrictionGenerator().convertToOntClass(classUri, condition);

            //addImportDeclarations(destinationOntology, resultExpression);
            addToOntologyAsClass(destinationOntology, resultExpression, classUri);

            return destinationOntology;

        } catch (OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }



    public OWLOntology convertToOwlIndividualOntology(String individualUri, ClassCondition condition) {
        OWLOntology destinationOntology;
        try {
            IRI iri = IRI.create(individualUri);
            String ontologyIRI = OntologyUtils.getNamespace(iri);

            destinationOntology = manager.createOntology(IRI.create(ontologyIRI));

            List<OWLAxiom> axioms = getIndividualGenerator().convertToOntIndividual(individualUri, condition);

            addToOntologyAsIndividualDescription(destinationOntology, axioms);
            addImportDeclarations(destinationOntology, axioms);
            return destinationOntology;

        } catch (OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }



    private void addImportDeclarations(OWLOntology destinationOntology,
                                       List<OWLAxiom> axioms) {

        for (OWLAxiom axiom : axioms) {
            for(OWLEntity ent : axiom.getSignature()){

                IRI ontoIRI = IRI.create(OntologyUtils.getNamespace(ent.getIRI()));

                OWLImportsDeclaration importsDeclaration = factory.getOWLImportsDeclaration(ontoIRI);
                if(!ontoIRI.toString().contains("XMLSchema")
                        && !destinationOntology.getOntologyID().getOntologyIRI().equals(ontoIRI)
                        && !destinationOntology.getImportsDeclarations().contains(importsDeclaration)){
                    AddImport addImportChange = new AddImport(destinationOntology, importsDeclaration);
                    manager.applyChange(addImportChange);
                }
            }
        }
    }







    private String serializeToString(OWLOntology destinationOntology, boolean useRdf) throws OWLOntologyStorageException {
        StringDocumentTarget serializationTarget = new StringDocumentTarget();
        if(useRdf){
            RDFXMLDocumentFormat rdfXmlFormat = new RDFXMLDocumentFormat();
            manager.saveOntology(destinationOntology, rdfXmlFormat, serializationTarget);
        }
        else{
            OWLXMLDocumentFormat owlXmlFormat = new OWLXMLDocumentFormat();
            manager.saveOntology(destinationOntology, owlXmlFormat, serializationTarget);
        }
        return serializationTarget.toString();
    }
	

	
}
