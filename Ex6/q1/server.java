import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class server extends UnicastRemoteObject implements piCal {
    
    protected server() throws RemoteException {
        super();
    }

    @Override
    public double calculatePi(int not, long ppt) throws RemoteException {
        System.out.println("PI calculation using: " + not + " threads, " + ppt + " points per thread.");
        
        ExecutorService executor = Executors.newFixedThreadPool(not);
        List<Future<Long>> results = new ArrayList<>();
        
        long st = System.currentTimeMillis();//starting time

        for (int i = 0; i < not; i++) {
            results.add(executor.submit(() -> {
                long inside = 0;
                for (long j = 0; j < ppt; j++) {
                    double x = Math.random();
                    double y = Math.random();
                    if (x * x + y * y < 1) {
                        inside++;
                    }
                }
                return inside;
            }));
        }

        long tic = 0; //total points inside circle
        try {
            for (Future<Long> future : results) {
                tic += future.get();
            }
        } catch (Exception e) {
            System.err.println("Thread execution error: " + e.getMessage());
        } finally {
            executor.shutdown();
        }

        long et = System.currentTimeMillis();//ending time
        System.out.println("Calculation completed in " + (et - st) + " ms.");

        long tp = (long) not * ppt; //total points
        return 4.0 * tic / tp;
    }

    public static void main(String[] args) {
        try {
            server s = new server(); 
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("PiCalculator", s);
            
            System.out.println("Pi Server is running ..");
        } catch (Exception e) {
            System.err.println("Server Exception: " + e.getMessage());
        }
    }
}