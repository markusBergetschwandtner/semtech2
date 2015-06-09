package at.jku.semtech.miniprojekt2.services;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

public class KaffeeService {
    private static final String URI = "http://www.semanticweb.org/markus/ontologies/2015/4/untitled-ontology-7";
    private static final String OWLFILE = "kaffee_ontologie.owl";

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
	    System.out.println(claus);
	    for (OWLIndividual individual : claus.getIndividuals(ontology)) {
		System.out.println("        " + individual);
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

	changes.add(addObjectProperty(individual, PropertyType.URSPRUNG,
		getIndividual(land)));
	changes.add(addObjectProperty(individual, PropertyType.SERVIERT,
		getIndividual(serviertIn)));

	OWLIndividual zutat;
	Set<OWLClassExpression> type;

	for (String zutatName : zutaten) {
	    zutat = getIndividual(zutatName);
	    type = zutat.getTypes(ontology);

	    if (zutat != null) {
		// TODO machst Du Babo
		if (type.contains(null)) {
		    changes.add(addObjectProperty(individual,
			    PropertyType.MILCH, zutat));
		} else {
		    changes.add(addObjectProperty(individual,
			    PropertyType.INHALT, zutat));
		}
	    }
	}

	manager.applyChanges(changes);

	try {
	    manager.saveOntology(ontology);
	} catch (OWLOntologyStorageException e) {
	    e.printStackTrace();
	}
    }

    private static OWLOntologyChange addObjectProperty(
	    OWLIndividual individual, PropertyType property,
	    OWLIndividual object) {
	OWLObjectProperty prop = dataFactory.getOWLObjectProperty(IRI
		.create(iri + "#" + property.getString()));

	OWLAxiom axiom = dataFactory.getOWLObjectPropertyAssertionAxiom(prop,
		individual, object);

	return new AddAxiom(ontology, axiom);
    }

    private static OWLIndividual getIndividual(String name) {
	OWLIndividual individual;
	individual = dataFactory.getOWLNamedIndividual(IRI.create(iri + "#"
		+ name));
	return individual;
    }

    public static List<String> bringstDuHerkunftslandAlleBabo() {
	startUp();

	OWLClass landClaus = dataFactory.getOWLClass(IRI.create(iri
		+ "#Herkunftsland"));
	List<String> laender = new ArrayList<String>();

	for (Node<OWLNamedIndividual> nodes : reasoner.getInstances(landClaus,
		false).getNodes()) {
	    for (OWLNamedIndividual land : nodes.getEntities()) {
		laender.add(land.getIRI().toString().replaceAll(iri + "#", ""));
	    }
	}
	return laender;
    }

    public static List<Object> bringstDuZutatenAlleBabo() {
	startUp();

	OWLClass zutatenClaus = dataFactory.getOWLClass(IRI.create(iri
		+ "#Zutaten"));
	List<Object> zutaten = new ArrayList<Object>();

	NodeSet<OWLClass> zutatenClaeuse = reasoner.getSubClasses(zutatenClaus,
		true);

	for (OWLClass claus : zutatenClaeuse.getFlattened()) {
	    List<String> subZutaten = new ArrayList<String>();
	    OWLClass zutatenSubClaus = dataFactory.getOWLClass(IRI.create(iri
		    + "#" + labelFor(claus, ontology)));
	    NodeSet<OWLClass> zutatenSubClaeuse = reasoner.getSubClasses(
		    zutatenSubClaus, true);

	    zutaten.add(labelFor(claus, ontology));
	    boolean subZ = false;
	    for (OWLClass subClaus : zutatenSubClaeuse.getFlattened()) {
		if (labelFor(subClaus, ontology).equals("Nothing")) {
		    break;
		} else {
		    subZ = true;
		    subZutaten.add(labelFor(subClaus, ontology));
		}
	    }
	    if (subZ) {
		zutaten.add(subZutaten);
	    }
	}

	return zutaten;
    }

    private static String labelFor(OWLEntity clazz, OWLOntology o) {
	Set<OWLAnnotation> annotations = clazz.getAnnotations(o);
	for (OWLAnnotation anno : annotations) {
	    String result = anno.accept(labelExtractor);
	    if (result != null) {
		return result;
	    }
	}
	return clazz.getIRI().getFragment();
    }
}
