import java.io.*;
import java.net.*;
import java.util.*;

public class gutenberg {
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

            System.out.println("this book is downloaded : " + gutenberg_id);

        } catch (Exception e) {
            System.out.println("can't download this book " + gutenberg_id);
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

        long starting = System.currentTimeMillis();

        for (int id : idlist) {
            download_book(id);
        }

        long ending = System.currentTimeMillis();

        System.out.println("Total Time for single threding: " + (ending - starting) + " ms");

        sc.close();
    }
}