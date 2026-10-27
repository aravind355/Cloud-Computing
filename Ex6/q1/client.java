import java.rmi.registry.*;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            
            System.out.print("Enter no. of threads: ");
            int not = sc.nextInt(); // no. of threads
            System.out.print("Enter number of points per thread: ");
            long ppt = sc.nextLong(); // points per thread

            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            piCal stub = (piCal) registry.lookup("PiCalculator");

            System.out.println("Sending request to server...");
            long st = System.currentTimeMillis(); // starting time
            
            double pi = stub.calculatePi(not, ppt);
            long et = System.currentTimeMillis(); // ending time

            System.out.println("Total Points calculated : " + (not * ppt));
            System.out.println("Estimated PI: " + pi);
            System.out.println("Total Execution Time : " + (et - st) + " ms");

        } catch (Exception e) {
            System.err.println("Client Exception: " + e.getMessage());
        }
    }
}