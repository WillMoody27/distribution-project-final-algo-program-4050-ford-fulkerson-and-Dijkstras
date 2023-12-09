import java.util.Arrays;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        // Testing graph provided by Paul
        Supplier manufacturer1 = new Supplier("Manufacturer 1", 0, 0, 600, 1000);
        Supplier manufacturer2 = new Supplier("Manufacturer 2", 0, 0, 600, 1000);
        Supplier depot = new Supplier("Depot", 0, 0, 0, 750);
        Supplier distributor1 = new Supplier("Distributor1", 1, 210, 0, 610);
        Supplier distributor2 = new Supplier("Distributor2", 2, 220, 0, 525);
        Supplier distributor3 = new Supplier("Distributor3", 3, 230, 0, 530);
        Supplier distributor4 = new Supplier("Distributor4", 4, 100, 0, 440);
        Supplier distributor5 = new Supplier("Distributor5", 5, 250, 0, 550);

        // Initialize transporters (EDGES)
        Transporter transporter1 = new Transporter("Transporter1", manufacturer1, depot, 4, 600, 0);
        Transporter transporter2 = new Transporter("Transporter2", manufacturer2, distributor1, 9, 600, 0);
        Transporter transporter3 = new Transporter("Transporter3", depot, distributor1, 10, 100, 0);
        Transporter transporter4 = new Transporter("Transporter4", depot, distributor2, 6, 400, 0);
        Transporter transporter5 = new Transporter("Transporter5", distributor1, distributor3, 8, 400, 0);
        Transporter transporter6 = new Transporter("Transporter6", distributor1, distributor5, 2, 400, 0);
        Transporter transporter7 = new Transporter("Transporter7", distributor2, distributor3, 3, 100, 0);
        Transporter transporter8 = new Transporter("Transporter8", distributor2, distributor4, 7, 40, 0);
        Transporter transporter9 = new Transporter("Transporter9", distributor3, distributor4, 5, 90, 0);

        // Create a collection of suppliers and transporters
        Collection<Supplier> suppliers = Arrays.asList(manufacturer1, manufacturer2, depot, distributor1, distributor2, distributor3, distributor4, distributor5);
        Collection<Transporter> transporters = Arrays.asList(transporter1, transporter2, transporter3, transporter4, transporter5, transporter6, transporter7, transporter8, transporter9);

        // TODO-TEST: 1. Calculate the cheapest path from the manufacturer to last distributor
        Collection<Transporter> shortestPath = SimpleAllocator.cheapestPath(suppliers, transporters, manufacturer1, distributor2);
        // TODO-TEST: Display the results of the cheapest path
        System.out.println("Cheapest path:\n" + SimpleAllocator.displayAllocations(shortestPath));

        // TODO-TEST: 2. Determine the allocateForDemand and show the content of the method.
        Collection<Transporter> allocatedTransporters = SimpleAllocator.allocateForDemand(suppliers, transporters);
        System.out.println("\nAllocated transporters:\n" + SimpleAllocator.displayAllocations(allocatedTransporters)
                + "\nTotal cost: $"
                + SimpleAllocator.totalTransporterCost(allocatedTransporters));
        System.out.println("Total demand: " + SimpleAllocator.totalDemand(suppliers));
        System.out.println("Total amount shipped: " + SimpleAllocator.totalAmountShipped(allocatedTransporters));
    }
}

