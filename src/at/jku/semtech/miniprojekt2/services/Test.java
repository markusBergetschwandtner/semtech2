package at.jku.semtech.miniprojekt2.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

public class Test {

    /**
     * @param args
     * @throws IOException
     * @throws OWLOntologyStorageException
     * @throws OWLOntologyCreationException
     * @throws UnknownOWLOntologyException
     */
    public static void main(String[] args) throws UnknownOWLOntologyException,
	    OWLOntologyCreationException, OWLOntologyStorageException,
	    IOException {
	KaffeeService service = new KaffeeService();
	// service.printAllKaffee();

	List<String> list = new ArrayList<>();

	list.add("KaffeeSchwarz");
	// list.add("Schlagobers");
	// service.addNewKaffee("BabosKaffee", "BaboLand", "BabosSchale", list);
	System.out.println(service.bringstDuKaffeesVonDerZutatenBabo(list));

	//
	service.printAllKaffee();

    }

}
