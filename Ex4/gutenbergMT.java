import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

public class gutenbergMT {
    private static final Logger logger = Logger.getLogger(gutenbergMT.class.getName());
    static class dt extends Thread {
        int gutenberg_id;

        dt(int id) {
            this.gutenberg_id = id;
        }

        public void run() {
            download_book(gutenberg_id);
        }
    }


    public static void download_book(int gutenberg_id) {
        try {
            String link = "https://www.gutenberg.org/cache/epub/" + gutenberg_id + "/pg" + gutenberg_id + ".txt";

            logger.info("Starting download for book: " + gutenberg_id);

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

        ArrayList<Integer> idlist = new ArrayList<>();

        for (int i = startingID; i <= endingID; i++){
            idlist.add(i);
        }

        ArrayList<dt> threads = new ArrayList<>();
        long starting = System.currentTimeMillis();

        for (int id : idlist) {
            dt t = new dt(id);
            threads.add(t);
            t.start();
        }

        for (dt t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long ending = System.currentTimeMillis();

        System.out.println("Total Time for Multi threding: " + (ending - starting) + " ms");

        sc.close();
    }
}