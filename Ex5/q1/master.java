// import java.io.*;
// import java.net.*;
// import java.util.Scanner;

// public class master {
//     public static void main(String[] args) {
//         try (Scanner scanner = new Scanner(System.in);
//              ServerSocket server = new ServerSocket(5000)) {
            
//             System.out.print("Enter starting Book ID: ");
//             int startID = scanner.nextInt();
//             System.out.print("Enter ending Book ID: ");
//             int endID = scanner.nextInt();
//             int rangeSize = 10;
            
//             System.out.println("Master node waiting for workers on port 5000...");
            
//             while (startID <= endID) {
//                 Socket worker = server.accept();
//                 PrintWriter out = new PrintWriter(worker.getOutputStream(), true);
                
//                 int workerEnd = Math.min(startID + rangeSize - 1, endID);
//                 String message = startID + " " + workerEnd;
                
//                 out.println(message);
//                 System.out.println("Assigned books: [" + message + "] to worker: " + worker.getInetAddress());
                
//                 startID = workerEnd + 1;
//             }
            
//             System.out.println("All tasks assigned. Master node shutting down.");
            
//         } catch (Exception e) {
//             System.err.println("Master Node Error: " + e.getMessage());
//         }
//     }
// }

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class master {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             ServerSocket server = new ServerSocket(5000)) {
            
            System.out.print("Enter starting Book ID: ");
            int startID = scanner.nextInt();
            System.out.print("Enter ending Book ID: ");
            int endID = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Enter search term: ");
            String searchTerm = scanner.nextLine();
            int rangeSize = 10;
            
            ConcurrentLinkedQueue<int[]> taskQueue = new ConcurrentLinkedQueue<>();
            for (int i = startID; i <= endID; i += rangeSize) {
                int end = Math.min(i + rangeSize - 1, endID);
                taskQueue.add(new int[]{i, end});
            }
            
            int totalTasks = taskQueue.size();
            AtomicInteger completedTasks = new AtomicInteger(0);
            AtomicInteger totalOccurrences = new AtomicInteger(0);
            
            System.out.println("Master waiting for workers on port 5000...");
            
            while (completedTasks.get() < totalTasks) {
                Socket worker = server.accept();
                int[] task = taskQueue.poll();
                
                if (task != null) {
                    new Thread(() -> {
                        try {
                            worker.setSoTimeout(300000); 
                            PrintWriter out = new PrintWriter(worker.getOutputStream(), true);
                            BufferedReader in = new BufferedReader(new InputStreamReader(worker.getInputStream()));
                            
                            String message = task[0] + " " + task[1] + " " + searchTerm;
                            out.println(message);
                            System.out.println("Assigned books: [" + task[0] + "-" + task[1] + "] to worker: " + worker.getInetAddress());
                            
                            String response = in.readLine();
                            if (response != null) {
                                int count = Integer.parseInt(response);
                                totalOccurrences.addAndGet(count);
                                completedTasks.incrementAndGet();
                                System.out.println("Worker " + worker.getInetAddress() + " found " + count + " occurrences. Progress: " + completedTasks.get() + "/" + totalTasks);
                            } else {
                                throw new IOException("Worker disconnected prematurely.");
                            }
                        } catch (Exception e) {
                            System.err.println("Worker failed/timed out! Re-queuing task: [" + task[0] + "-" + task[1] + "]");
                            taskQueue.add(task); 
                        } finally {
                            try { worker.close(); } catch (IOException ignored) {}
                        }
                    }).start();
                } else {
                    worker.close();
                }
            }
            
            System.out.println("ALL TASKS COMPLETED!");
            System.out.println("Total consolidated occurrences of '" + searchTerm + "': " + totalOccurrences.get());
        } catch (Exception e) {
            System.err.println("Master Node Error: " + e.getMessage());
        }
    }
}