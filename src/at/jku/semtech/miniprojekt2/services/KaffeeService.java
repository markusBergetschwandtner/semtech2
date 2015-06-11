package at.jku.semtech.miniprojekt2.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
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
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

public class KaffeeService {
    private static final String URI = "http://www.semanticweb.org/markus/ontologies/2015/4/untitled-ontology-7";
    private static final String OWLFILEABSOLUT = "C:/Dropbox/JKU/Master/3. Semester/SemTech/MiniProjekte/MiniProjekt_2/kaffee_ontologie.owl";

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

	iri = IRI.create(URI);
	manager = OWLManager.createOWLOntologyManager();
	dataFactory = OWLManager.getOWLDataFactory();

	ontology = null;

	try {
	    ontology = manager.loadOntologyFromOntologyDocument(new File(
		    OWLFILEABSOLUT));
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

    public static void addNewKaffee(String name, String land, String serviert,
	    List<String> zutaten) throws UnknownOWLOntologyException,
	    OWLOntologyCreationException, OWLOntologyStorageException,
	    IOException {
	startUp();
	System.out.println("save Kaffee: " + name);
	System.out.println("Zutaten: " + zutaten.toString());

	OWLClass clausKaffee = dataFactory.getOWLClass(IRI.create(iri
		+ "#Kaffee"));
	OWLObjectProperty hatInhalt = dataFactory.getOWLObjectProperty(IRI
		.create(iri + "#hatInhalt"));
	// OWLObjectProperty hatUrsprung = dataFactory.getOWLObjectProperty(IRI
	// .create(iri + "#hatUrsprung"));
	// OWLObjectProperty serviertIn = dataFactory.getOWLObjectProperty(IRI
	// .create(iri + "#serviertIn"));

	OWLClass claus = dataFactory.getOWLClass(IRI.create(iri + "#" + name));

	// create class and add subclass
	OWLSubClassOfAxiom subClass = dataFactory.getOWLSubClassOfAxiom(claus,
		clausKaffee);
	OWLDeclarationAxiom decAxiom = dataFactory
		.getOWLDeclarationAxiom(claus);

	manager.addAxiom(ontology, decAxiom);
	manager.addAxiom(ontology, subClass);

	// add class expressions
	Set<OWLClassExpression> expressions = new HashSet<OWLClassExpression>();
	for (String s : zutaten) {
	    OWLClass clausZutat = dataFactory.getOWLClass(IRI.create(iri + "#"
		    + s));
	    OWLClassExpression ex = dataFactory.getOWLObjectSomeValuesFrom(
		    hatInhalt, clausZutat);
	    OWLEquivalentClassesAxiom eq = dataFactory
		    .getOWLEquivalentClassesAxiom(claus, ex);
	    manager.addAxiom(ontology, eq);
	    expressions.add(ex);
	}

	manager.saveOntology(ontology);

	saveFile();
    }

    private static void saveFile() throws OWLOntologyCreationException,
	    IOException, UnknownOWLOntologyException,
	    OWLOntologyStorageException {
	File file = new File(OWLFILEABSOLUT);
	if (!file.exists())
	    file.createNewFile();
	OutputStream outputStream = new FileOutputStream(file);
	manager.saveOntology(ontology, manager.getOntologyFormat(ontology),
		outputStream);

	System.out.println("The ontology in " + file.getPath()
		+ " should now contain all inferred axioms. ");
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

    public static List<String> bringstDuKaffeesVonDerZutatenBabo(
	    List<String> zutaten) {
	startUp();
	int counterStrike = zutaten.size();
	int counter = 0;
	List<String> result = new ArrayList<String>();

	OWLClass heissgetraenkClaus = dataFactory.getOWLClass(IRI.create(iri
		+ "#Heiﬂgetr‰nk"));
	NodeSet<OWLClass> heissgetraenkClaeuse = reasoner.getSubClasses(
		heissgetraenkClaus, false);

	for (OWLClass claus : heissgetraenkClaeuse.getFlattened()) {
	    counter = 0;

	    for (OWLClassExpression equivalentTo : claus
		    .getEquivalentClasses(ontology)) {
		List<String> equivalentClasses = new ArrayList<String>();
		for (OWLClass c : equivalentTo.getClassesInSignature()) {
		    equivalentClasses.add(c.toStringID());
		}

		for (String s : zutaten) {
		    if (equivalentClasses.contains(iri + "#" + s)) {
			counter++;
		    }
		}
	    }

	    if (counter == counterStrike) {
		result.add(labelFor(claus, ontology));
	    }
	}

	return result;
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
