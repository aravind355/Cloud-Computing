import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class PiMaster {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in);
             ServerSocket server = new ServerSocket(5001)) {

            System.out.print("Enter number of points: ");
            long totalPoints = sc.nextLong();
            System.out.print("Enter number of worker: ");
            int totalWorkers = sc.nextInt();

            long pointsPerWorker = totalPoints / totalWorkers;
            AtomicLong totalInsideCircle = new AtomicLong(0);
            
            CountDownLatch latch = new CountDownLatch(totalWorkers);

            long startTime = System.currentTimeMillis();
            System.out.println("Master waiting for workers");

            for (int i = 0; i < totalWorkers; i++) {
                Socket worker = server.accept();
                
                new Thread(() -> {
                    try {
                        PrintWriter out = new PrintWriter(worker.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(worker.getInputStream()));

                        out.println(pointsPerWorker);
                        System.out.println("Assigned " + pointsPerWorker + " points to worker: " + worker.getInetAddress());

                        String response = in.readLine();
                        if (response != null) {
                            long insideCount = Long.parseLong(response);
                            totalInsideCircle.addAndGet(insideCount);
                            System.out.println("Worker " + worker.getInetAddress() + " returned " + insideCount + " inside points.");
                        }
                    } catch (Exception e) {
                        System.err.println("Worker connection error: " + e.getMessage());
                    } finally {
                        latch.countDown();
                        try { worker.close(); } catch (IOException ignored) {}
                    }
                }).start();
            }

            latch.await();

            long endTime = System.currentTimeMillis();

            double estimatedPi = 4.0 * totalInsideCircle.get() / totalPoints;

            System.out.println("ALL WORKERS COMPLETED!");
            System.out.println("Total Points Simulated: " + totalPoints);
            System.out.println("Total Points Inside Circle: " + totalInsideCircle.get());
            System.out.println("Estimated PI: " + estimatedPi);
            System.out.println("Total Execution Time: " + (endTime - startTime) + " ms");

        } catch (Exception e) {
            System.err.println("Master Node Error: " + e.getMessage());
        }
    }
}