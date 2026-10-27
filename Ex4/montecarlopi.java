import java.util.Scanner;

public class montecarlopi {

    static long insideCircle = 0;
    static long totalPoints = 0;

    public static synchronized void count(long inside, long total) {
        insideCircle += inside;
        totalPoints += total;
    }

    static class PiThread extends Thread {

        long noOfPoints;

        PiThread(long points) {
            this.noOfPoints = points;
        }

        public void run() {

            long inside = 0;

            for (long i = 0; i < noOfPoints; i++) {

                double x = Math.random();
                double y = Math.random();

                if (x * x + y * y < 1) {
                    inside++;
                }
            }

            count(inside, noOfPoints);
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter no. of threads: ");
        int noOfThreads = sc.nextInt();

        System.out.print("Enter no. of points per thread: ");
        long noOfPoints = sc.nextLong();

        Thread[] threads = new Thread[noOfThreads];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < noOfThreads; i++) {
            threads[i] = new PiThread(noOfPoints);
            threads[i].start();
        }

        for (int i = 0; i < noOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();

        double pi = 4.0 * insideCircle / totalPoints;

        System.out.println("\nTotal Points: " + totalPoints);
        System.out.println("Points Inside Circle: " + insideCircle);
        System.out.println("Estimated PI: " + pi);
        System.out.println("Execution Time: " + (endTime - startTime) + " ms");

        sc.close();
    }
}