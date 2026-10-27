import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

class WorkerInfo {
    String ip;
    int port;

    WorkerInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}

public class master {
    static final List<WorkerInfo> registeredWorkers = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        
        Thread registrationThread = new Thread(() -> {
            try (ServerSocket regServer = new ServerSocket(5001)) {
                System.out.println("Master server is running ");
                while (true) {
                    Socket workerReg = regServer.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(workerReg.getInputStream()));
                    
                    String msg = in.readLine();
                    if (msg != null && msg.startsWith("REGISTER")) {
                        String[] parts = msg.split(" ");
                        String ip = parts[1];
                        int port = Integer.parseInt(parts[2]);
                        
                        registeredWorkers.add(new WorkerInfo(ip, port));
                        System.out.println("New Worker Registered: " + ip + ":" + port + " (Total available: " + registeredWorkers.size() + ")");
                    }
                    workerReg.close();
                }
            } catch (Exception e) {
                System.err.println("Registration Server Error: " + e.getMessage());
            }
        });
        
        registrationThread.setDaemon(true); 
        registrationThread.start();

        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("\n press enter after all workers registered");
            sc.nextLine();

            if (registeredWorkers.isEmpty()) {
                System.out.println("No workers registered! Exiting.");
                return;
            }

            System.out.print("Enter number of points: ");
            long totalPoints = sc.nextLong();
            
            int totalWorkers = registeredWorkers.size();
            long pointsPerWorker = totalPoints / totalWorkers;
            AtomicLong totalInsideCircle = new AtomicLong(0);
            
            CountDownLatch latch = new CountDownLatch(totalWorkers);
            long startTime = System.currentTimeMillis();
            System.out.println("\nInitiating job with " + totalWorkers + " registered workers...");

            for (WorkerInfo worker : registeredWorkers) {
                new Thread(() -> {
                    try (Socket jobSocket = new Socket(worker.ip, worker.port);
                         PrintWriter out = new PrintWriter(jobSocket.getOutputStream(), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(jobSocket.getInputStream()))) {
                         
                        out.println(pointsPerWorker);
                        System.out.println("Assigned " + pointsPerWorker + " points to worker at " + worker.ip + ":" + worker.port);

                        String response = in.readLine();
                        if (response != null) {
                            long insideCount = Long.parseLong(response);
                            totalInsideCircle.addAndGet(insideCount);
                            System.out.println("Worker " + worker.ip + ":" + worker.port + " returned " + insideCount + " inside points.");
                        }
                    } catch (Exception e) {
                        System.err.println("Error communicating with worker " + worker.ip + ":" + worker.port + " -> " + e.getMessage());
                    } finally {
                        latch.countDown();
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