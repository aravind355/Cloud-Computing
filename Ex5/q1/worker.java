// import java.io.*;
// import java.net.*;
// import java.util.concurrent.*;

// public class worker {
    
//     public static void downloadBook(int id) {
//         try {
//             String link = "https://www.gutenberg.org/cache/epub/" + id + "/pg" + id + ".txt";
//             URL url = new URL(link);

//             try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//                  FileWriter writer = new FileWriter("book_" + id + ".txt")) {

//                 String line;
//                 while ((line = in.readLine()) != null) {
//                     writer.write(line + "\n");
//                 }
//             }
            
//             // System.out.println("Downloaded book " + id + " via " + Thread.currentThread().getName());

//             String scpCommand = "scp -i 23mcce11 book_" + id + ".txt 23mcce11@10.5.0.90:~/Desktop/Gutenburg/";
//             Process process = Runtime.getRuntime().exec(scpCommand);
//             process.waitFor();
            
//             System.out.println("Downloaded and transferred book " + id);

//         } catch (Exception e) {
//             // System.err.println("Error downloading book " + id + ": " + e.getMessage());
//             System.err.println("Error processing book " + id + ": " + e.getMessage());
//         }
//     }

//     public static void main(String[] args) {
//         try (Socket socket = new Socket("localhost", 5000);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//             String message = in.readLine();
//             if (message == null) return;
            
//             String[] parts = message.split(" ");
//             int start = Integer.parseInt(parts[0]);
//             int end = Integer.parseInt(parts[1]);

//             System.out.println("Received work range: " + start + " to " + end);

//             int numberOfThreads = 5; 
//             ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

//             for (int i = start; i <= end; i++) {
//                 final int bookId = i; 
//                 executor.submit(() -> downloadBook(bookId));
//             }

//             executor.shutdown();
//             executor.awaitTermination(1, TimeUnit.HOURS);
            
//             // System.out.println("Worker finished downloading its subset of books.");
//             System.out.println("Worker finished downloading and transferring its subset of books.");

//         } catch (Exception e) {
//             System.err.println("Worker Node Error: " + e.getMessage());
//         }
//     }
// }

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class worker {
    public static int downloadSearchAndTransfer(int id, String searchTerm) {
        int localCount = 0;
        try {
            String link = "https://www.gutenberg.org/cache/epub/" + id + "/pg" + id + ".txt";
            URL url = new URL(link);
            File bookFile = new File("book_" + id + ".txt");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                 FileWriter writer = new FileWriter(bookFile)) {

                String line;
                String lowerSearch = searchTerm.toLowerCase();
                
                while ((line = in.readLine()) != null) {
                    writer.write(line + "\n");
                    
                    String lowerLine = line.toLowerCase();
                    int index = 0;
                    while ((index = lowerLine.indexOf(lowerSearch, index)) != -1) {
                        localCount++;
                        index += lowerSearch.length();
                    }
                }
            }
            
            String scpCommand = "scp -i 23mcce11 book_" + id + ".txt 23mcce11@10.5.0.90:~/Desktop/Gutenburg/";
            Process process = Runtime.getRuntime().exec(scpCommand);
            process.waitFor();
            
            System.out.println("Processed, searched, and transferred book " + id + " (Found " + localCount + " matches)");

        } catch (Exception e) {
            System.err.println("Error processing book " + id + ": " + e.getMessage());
        }
        return localCount;
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message = in.readLine();
            if (message == null) return;
            
            String[] parts = message.split(" ", 3); 
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);
            String searchTerm = parts[2];

            System.out.println("Received work range: " + start + " to " + end + " | Search term: '" + searchTerm + "'");

            int numberOfThreads = 5; 
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
            AtomicInteger totalCountForWorker = new AtomicInteger(0);

            for (int i = start; i <= end; i++) {
                final int bookId = i; 
                executor.submit(() -> {
                    int count = downloadSearchAndTransfer(bookId, searchTerm);
                    totalCountForWorker.addAndGet(count);
                });
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
            
            System.out.println("Worker finished. Total occurrences found: " + totalCountForWorker.get());
            
            out.println(totalCountForWorker.get());

        } catch (Exception e) {
            System.err.println("Worker Node Error: " + e.getMessage());
        }
    }
}