import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class gutenberg3name {

    private static final Logger logger = Logger.getLogger(gutenberg3name.class.getName());

    static int SearchCount = 0;
    static List<Integer> searchedBooks = Collections.synchronizedList(new ArrayList<>());

    public static synchronized void incrementCount() {
        SearchCount++;
    }

    public static void download_the_book_and_search_word(int gutenberg_id, String word) {
        try {
            String link = "https://www.gutenberg.org/cache/epub/"+ gutenberg_id + "/pg"+ gutenberg_id + ".txt";

            URL url = new URL(link);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {

                String lowerLine = line.toLowerCase();
                String lowerWord = word.toLowerCase();

                int index = 0;

                while ((index = lowerLine.indexOf(lowerWord, index)) != -1) {
                    incrementCount();
                    index += lowerWord.length();
                }
            }

            in.close();

            searchedBooks.add(gutenberg_id);

            logger.info("Completed Searching book: " + gutenberg_id);

        } catch (Exception e) {
            logger.warning("Failed searching book: " + gutenberg_id);
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter starting ID: ");
        int startingID = sc.nextInt();

        System.out.print("Enter ending ID: ");
        int endingID = sc.nextInt();

        sc.nextLine();

        System.out.print("Enter word to search: ");
        String word = sc.nextLine();

        ExecutorService exe = Executors.newFixedThreadPool(10);

        long starting = System.currentTimeMillis();

        for (int i = startingID; i <= endingID; i++) {
            final int id = i;

            exe.submit(new Runnable() {
                public void run() {
                    download_the_book_and_search_word(id, word);
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

        System.out.println("\nSearch Word: " + word);
        System.out.println("Total times Found: " + SearchCount);
        System.out.println("Books Searched: " + searchedBooks);
        System.out.println("Total Execution Time: " + (ending - starting) + " ms");

        sc.close();
    }
}