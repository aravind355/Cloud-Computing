import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class gutenbergTP {
    private static final Logger logger = Logger.getLogger(gutenbergMT.class.getName());

    public static void download_book(int gutenberg_id) {
        try {
            String link = "https://www.gutenberg.org/cache/epub/" + gutenberg_id + "/pg" + gutenberg_id + ".txt";

            URL url = new URL(link);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));

            FileWriter writer = new FileWriter("book_" + gutenberg_id + ".txt");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                writer.write(inputLine + "\n");
            }

            in.close();
            writer.close();

            logger.info("this book is downloaded : " + gutenberg_id);

        } catch (Exception e) {
            logger.warning("can't download this book " + gutenberg_id);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // System.out.print("Enter Book ID: ");
        // int id = sc.nextInt();
        // download_book(id);

        System.out.print("Enter starting ID: ");
        int startingID = sc.nextInt();

        System.out.print("Enter ending ID: ");
        int endingID = sc.nextInt();

        ExecutorService exe = Executors.newFixedThreadPool(10);

        long starting = System.currentTimeMillis();

          for (int i = startingID; i <= endingID; i++) {
            final int id = i;

            exe.submit(new Runnable() {
                public void run() {
                    download_book(id);
                }
            });
        }

        exe.shutdown();

        try {
            exe.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.warning("Execution interrupted");
        }

        long ending = System.currentTimeMillis();

        System.out.println("Total Time for Thread Pool: " + (ending - starting) + " ms");

        sc.close();
    }
}