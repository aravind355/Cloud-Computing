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
        try (Socket socket = new Socket("localhost", 5001);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message = in.readLine();
            if (message == null) return;
            
            long assignedPoints = Long.parseLong(message);
            System.out.println("Received : " + assignedPoints + " points.");

            int noOfThreads = Runtime.getRuntime().availableProcessors();
            System.out.println("No. of threads for calculation ="  + noOfThreads );
            
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

            System.out.println("Me finished in " + (endTime - startTime) + " ms.");
            System.out.println("Total points inside circle : " + localInsideCircle);

            out.println(localInsideCircle);

        } catch (Exception e) {
            System.err.println("Worker Node Error: " + e.getMessage());
        }
    }
}