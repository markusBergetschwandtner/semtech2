package at.jku.semtech.miniprojekt2.services;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
	KaffeeService service = new KaffeeService();
	// service.printAllKaffee();

	System.out.println(service.bringstDuKaffeesByZutaten(null));

    }

}
