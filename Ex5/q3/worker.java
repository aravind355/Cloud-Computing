import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class worker {
    
    static class PiCalculationTask implements Callable<Long> {
        private final long pointsToCalculate;

        PiCalculationTask(long points) {
            this.pointsToCalculate = points;
        }

        @Override
        public Long call() {
            long inside = 0;
            for (long i = 0; i < pointsToCalculate; i++) {
                double x = Math.random();
                double y = Math.random();
                if (x * x + y * y < 1) {
                    inside++;
                }
            }
            return inside;
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket workerServer = new ServerSocket(0); 
            int myPort = workerServer.getLocalPort();
            String myIP = InetAddress.getLocalHost().getHostAddress();

            try (Socket regSocket = new Socket("localhost", 5001);
                 PrintWriter regOut = new PrintWriter(regSocket.getOutputStream(), true)) {
                 
                regOut.println("REGISTER " + myIP + " " + myPort);
                System.out.println("Successfully registered with master as IP: " + myIP + " | Port: " + myPort);
                
            } catch (Exception e) {
                System.err.println("Failed to register");
                return;
            }

            System.out.println("Waiting for job");
            
            while (true) { 
                try (Socket masterSocket = workerServer.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(masterSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(masterSocket.getOutputStream(), true)) {
                    
                    String message = in.readLine();
                    if (message == null) continue;
                    
                    long assignedPoints = Long.parseLong(message);
                    System.out.println("\nTarget: " + assignedPoints + " points.");

                    int noOfThreads = Runtime.getRuntime().availableProcessors();
                    System.out.println(noOfThreads + " threads for calculation.");
                    
                    long pointsPerThread = assignedPoints / noOfThreads;
                    ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
                    List<Future<Long>> results = new ArrayList<>();

                    long startTime = System.currentTimeMillis();

                    for (int i = 0; i < noOfThreads; i++) {
                        long points = (i == noOfThreads - 1) 
                            ? pointsPerThread + (assignedPoints % noOfThreads) 
                            : pointsPerThread;
                        
                        results.add(executor.submit(new PiCalculationTask(points)));
                    }

                    long localInsideCircle = 0;
                    for (Future<Long> future : results) {
                        localInsideCircle += future.get();
                    }

                    executor.shutdown();
                    long endTime = System.currentTimeMillis();

                    System.out.println("Worker calculation finished in " + (endTime - startTime) + " ms.");
                    System.out.println("Total points inside circle : " + localInsideCircle);

                    out.println(localInsideCircle);
                    
                } catch (Exception e) {
                     System.err.println("Error processing job: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Worker Initialization Error: " + e.getMessage());
        }
    }
}