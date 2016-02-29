package org.semanticcloud.semanticEngine.controll.reading.owlApi.propertyFactories;

import org.semanticcloud.semanticEngine.model.ontology.OntoProperty;
import org.semanticcloud.semanticEngine.model.ontology.properties.FloatProperty;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;

public class FloatPropertyFactory extends SimpleDatatypePropertyFactory {
	public FloatPropertyFactory(){
		super("http://www.w3.org/2001/XMLSchema#float");
	}

	@Override
	public OntoProperty createProperty(OWLOntology onto, OWLProperty property) {
		return new FloatProperty(property.getIRI().getNamespace(), property.getIRI().getFragment());
	}
}
