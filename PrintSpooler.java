import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Print Spooler Simulation
 * Demonstrates a FIFO queue (circular array implementation) managing print jobs.
 */
public class PrintSpooler {

    // ---------- Generic circular-array queue ----------
    static class CircularQueue<T> {
        private final Object[] data;
        private int front = 0;
        private int size = 0;

        CircularQueue(int capacity) {
            data = new Object[capacity];
        }

        boolean isEmpty() { return size == 0; }
        boolean isFull()  { return size == data.length; }
        int size()        { return size; }

        void enqueue(T item) {
            if (isFull()) throw new IllegalStateException("Queue is full");
            data[(front + size) % data.length] = item;
            size++;
        }

        @SuppressWarnings("unchecked")
        T dequeue() {
            if (isEmpty()) throw new NoSuchElementException("Queue is empty");
            T item = (T) data[front];
            data[front] = null;
            front = (front + 1) % data.length;
            size--;
            return item;
        }

        @SuppressWarnings("unchecked")
        T peek() {
            if (isEmpty()) throw new NoSuchElementException("Queue is empty");
            return (T) data[front];
        }

        @SuppressWarnings("unchecked")
        T get(int position) {              // position 0 = front of queue
            return (T) data[(front + position) % data.length];
        }
    }

    // ---------- A print job ----------
    static class PrintJob {
        private static int nextId = 1;
        final int id;
        final String owner;
        final String document;
        final int pages;

        PrintJob(String owner, String document, int pages) {
            this.id = nextId++;
            this.owner = owner;
            this.document = document;
            this.pages = pages;
        }

        @Override
        public String toString() {
            return String.format("Job #%d | %-10s | %-20s | %d page(s)", id, owner, document, pages);
        }
    }

    // ---------- Spooler ----------
    private final CircularQueue<PrintJob> queue = new CircularQueue<>(5);

    void submit(String owner, String document, int pages) {
        if (queue.isFull()) {
            System.out.println("Spooler is full. Please try again later.");
            return;
        }
        PrintJob job = new PrintJob(owner, document, pages);
        queue.enqueue(job);
        System.out.println("Added: " + job);
    }

    void printNext() {
        if (queue.isEmpty()) {
            System.out.println("No jobs waiting.");
            return;
        }
        PrintJob job = queue.dequeue();
        System.out.println("Printing -> " + job);
        System.out.println("Done. " + queue.size() + " job(s) remaining.");
    }

    void showQueue() {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        System.out.println("--- Waiting jobs (front first) ---");
        for (int i = 0; i < queue.size(); i++) {
            System.out.println((i + 1) + ". " + queue.get(i));
        }
    }

    public static void main(String[] args) {
        PrintSpooler spooler = new PrintSpooler();
        Scanner in = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n1. Submit job  2. Print next  3. View queue  4. Exit");
            System.out.print("Choice: ");
            String choice = in.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Owner: ");
                    String owner = in.nextLine();
                    System.out.print("Document: ");
                    String doc = in.nextLine();
                    System.out.print("Pages: ");
                    try {
                        spooler.submit(owner, doc, Integer.parseInt(in.nextLine().trim()));
                    } catch (NumberFormatException e) {
                        System.out.println("Pages must be a number.");
                    }
                    break;
                case "2": spooler.printNext(); break;
                case "3": spooler.showQueue(); break;
                case "4": running = false; break;
                default:  System.out.println("Invalid choice.");
            }
        }
        in.close();
    }
}