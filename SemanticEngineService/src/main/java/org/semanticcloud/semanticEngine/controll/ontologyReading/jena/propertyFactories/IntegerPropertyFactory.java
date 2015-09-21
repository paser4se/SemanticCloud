package org.semanticcloud.semanticEngine.controll.ontologyReading.jena.propertyFactories;

import org.semanticcloud.semanticEngine.entity.OntoProperty;
import org.semanticcloud.semanticEngine.controll.ontologyReading.jena.OwlPropertyFactory;
import org.semanticcloud.semanticEngine.entity.properties.IntegerProperty;
import org.apache.jena.ontology.OntProperty;

public class IntegerPropertyFactory extends OwlPropertyFactory {

    @Override
    public boolean canCreateProperty(OntProperty ontProperty) {
        if(!ontProperty.isDatatypeProperty())
            return false;
        if(ontProperty.getRange() == null)
            return false;
        if(ontProperty.getRange().getURI() == null)
            return false;

        String rangeUri = ontProperty.getRange().getURI();
        return rangeUri.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#integer") || rangeUri.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#int");
    }

    @Override
    public OntoProperty createProperty(OntProperty ontProperty) {
        return new IntegerProperty(ontProperty.getNameSpace(), ontProperty.getLocalName(), ontProperty.getRange().getURI());
    }

}
