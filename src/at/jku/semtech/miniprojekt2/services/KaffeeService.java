package at.jku.semtech.miniprojekt2.services;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

public class KaffeeService {
    private static final String URI = "http://www.semanticweb.org/markus/ontologies/2015/4/untitled-ontology-7";
    private static final String OWLFILE = "kaffee_ontologie.xml";

    private static IRI iri;
    private static OWLOntologyManager manager;
    private static OWLDataFactory dataFactory;
    private static OWLOntology ontology;
    private static OWLReasoner reasoner;
    private static LabelExtractor labelExtractor = new LabelExtractor();

    static class LabelExtractor extends OWLObjectVisitorExAdapter<String>
	    implements OWLAnnotationObjectVisitorEx<String> {
	public LabelExtractor(String defaultReturnValue) {
	    super(defaultReturnValue);
	}

	public LabelExtractor() {
	    super(null);
	}

	@Override
	public String visit(OWLAnnotation annotation) {
	    if (annotation.getProperty().isLabel()) {
		OWLLiteral c = (OWLLiteral) annotation.getValue();
		return c.getLiteral();
	    }
	    return null;
	}
    }

    public static void startUp() {
	if (ontology != null)
	    return;

	URL u = Thread.currentThread().getContextClassLoader()
		.getResource(OWLFILE);

	iri = IRI.create(URI);
	manager = OWLManager.createOWLOntologyManager();
	dataFactory = OWLManager.getOWLDataFactory();

	ontology = null;
	try {
	    ontology = manager.loadOntologyFromOntologyDocument(new File(u
		    .getFile()));
	} catch (OWLOntologyCreationException e) {
	    e.printStackTrace();
	}

	ReasonerFactory factory = new ReasonerFactory();
	Configuration c = new Configuration();
	reasoner = factory.createReasoner((OWLOntology) ontology, c);

	System.out.println(ontology.toString());
    }

    public enum PropertyType {
	ALKOHOLGEHALT("hatAlkoholgehalt"), INHALT("hatInhal"), ALKOHOL(
		"hatAlkohol"), AROMA("hatAroma"), EIS("hatEis"), MILCH(
		"hatMilch"), KOFFEINGEHALT("hatKoffeingehalt"), URSPRUNG(
		"hatUrsprung"), ZUBEREITUNGSART("hatZubereitunsart"), SERVIERT(
		"wirdServiertIn");

	private String string;

	private PropertyType(String string) {
	    this.setString(string);
	}

	public String getString() {
	    return string;
	}

	public void setString(String string) {
	    this.string = string;
	}

	@Override
	public String toString() {
	    return string;
	}

    }

    public static void printAllKaffee() {
	startUp();

	for (OWLClass claus : ontology.getClassesInSignature()) {
	    for (OWLIndividual individual : claus.getIndividualsInSignature()) {
		System.out.println(individual);
	    }
	}
    }

    public static void addNewKaffee(String name, String land,
	    String serviertIn, List<String> zutaten) {
	startUp();

	OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(IRI
		.create(iri + "#" + name));

	System.out.println(individual + " hinzugefuegt!");

	OWLClass claus = dataFactory.getOWLClass(IRI.create(iri + "#Kaffee"));
	OWLAxiom axiom = dataFactory.getOWLClassAssertionAxiom(claus,
		individual);
	AddAxiom addAxiom = new AddAxiom(ontology, axiom);
	List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
	changes.add(addAxiom);
	manager.applyChange(addAxiom);

	changes.add(addDataProperty(individual, PropertyType.URSPRUNG, land));
	changes.add(addDataProperty(individual, PropertyType.SERVIERT,
		serviertIn));

    }

    private static OWLOntologyChange addDataProperty(OWLIndividual individual,
	    PropertyType property, String val) {
	OWLDataProperty prop = dataFactory.getOWLDataProperty(IRI.create(iri
		+ "#" + property.getString()));

	OWLAxiom axiom = dataFactory.getOWLDataPropertyAssertionAxiom(prop,
		individual, val);

	return new AddAxiom(ontology, axiom);
    }
}
