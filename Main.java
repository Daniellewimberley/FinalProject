import java.io.*;
import java.util.*;

/**
 * FinalProject - Creative Problem-Solving with Arrays and ArrayLists
 * Problem: Simple Task Manager with persistence
 * - Pending tasks: ArrayList<String>
 * - Completed tasks: String[] (array) with dynamic grow
 * - Loads from files on start, saves on exit
 * - Menu-driven CLI
 * - Recursive input validation
 * - try/catch for parsing, indexing, and I/O
 *
 * Files used:
 *   data/pending.txt
 *   data/completed.txt
 *
 * Author: Danielle Wimberley
 * Date: Nov 2025
 */

public class Main {

    // === Data structures ===
    private static ArrayList<String> pending = new ArrayList<>();  // dynamic list of active tasks
    private static String[] completed = new String[10];            // array for completed tasks (starts small)
    private static int completedCount = 0;                          // tracks used portion of completed[]

    // === File paths ===
    private static final String DATA_DIR = "data";
    private static final String PENDING_FILE = DATA_DIR + File.separator + "pending.txt";
    private static final String COMPLETED_FILE = DATA_DIR + File.separator + "completed.txt";

    // === Scanner (single instance) ===
    private static final Scanner SC = new Scanner(System.in);

    public static void main(String[] args) {
        ensureDataFolder();
        loadAllData();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInRange("Choose an option (1-9): ", 1, 9);

            switch (choice) {
                case 1 -> addTask();
                case 2 -> listPending();
                case 3 -> listCompleted();
                case 4 -> completeTaskByIndex();
                case 5 -> removePendingByIndex();
                case 6 -> searchTasks();
                case 7 -> clearCompleted(); // just to exercise array ops
                case 8 -> exportSnapshot();
                case 9 -> {
                    saveAllData();
                    System.out.println("\nSaved! Goodbye.");
                    running = false;
                }
            }
        }
    }

    // === Menu ===
    private static void printMenu() {
        System.out.println("\n==== TASK MANAGER ====");
        System.out.println("1) Add a new task");
        System.out.println("2) List pending tasks");
        System.out.println("3) List completed tasks");
        System.out.println("4) Complete a task (move from pending -> completed)");
        System.out.println("5) Remove a pending task (delete)");
        System.out.println("6) Search tasks (pending & completed)");
        System.out.println("7) Clear ALL completed tasks");
        System.out.println("8) Export snapshot (pending + completed)");
        System.out.println("9) Save & Exit");
    }

    // === Option 1 ===
    private static void addTask() {
        String text = getNonEmptyString("Enter task description: ");
        pending.add(text);
        System.out.println("Added: \"" + text + "\"");
    }

    // === Option 2 ===
    private static void listPending() {
        System.out.println("\n-- Pending Tasks (" + pending.size() + ") --");
        if (pending.isEmpty()) {
            System.out.println("(none)");
        } else {
            for (int i = 0; i < pending.size(); i++) {
                System.out.println((i + 1) + ") " + pending.get(i));
            }
        }
    }

    // === Option 3 ===
    private static void listCompleted() {
        System.out.println("\n-- Completed Tasks (" + completedCount + ") --");
        if (completedCount == 0) {
            System.out.println("(none)");
        } else {
            for (int i = 0; i < completedCount; i++) {
                System.out.println((i + 1) + ") " + completed[i]);
            }
        }
    }

    // === Option 4 ===
    private static void completeTaskByIndex() {
        if (pending.isEmpty()) {
            System.out.println("No pending tasks to complete.");
            return;
        }
        listPending();
        int idx = getIntInRange("Enter the number of the task to complete: ", 1, pending.size()) - 1;

        try {
            String task = pending.remove(idx); // may throw IndexOutOfBoundsException (we guard via getIntInRange)
            appendCompleted(task);
            System.out.println("Completed: \"" + task + "\"");
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Invalid index. Nothing completed.");
        }
    }

    // === Option 5 ===
    private static void removePendingByIndex() {
        if (pending.isEmpty()) {
            System.out.println("No pending tasks to remove.");
            return;
        }
        listPending();
        int idx = getIntInRange("Enter the number of the task to remove: ", 1, pending.size()) - 1;

        try {
            String removed = pending.remove(idx);
            System.out.println("Removed: \"" + removed + "\"");
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Invalid index. Nothing removed.");
        }
    }

    // === Option 6 ===
    private static void searchTasks() {
        String query = getNonEmptyString("Search text: ").toLowerCase(Locale.ROOT);

        System.out.println("\nResults in Pending:");
        int found = 0;
        for (int i = 0; i < pending.size(); i++) {
            if (pending.get(i).toLowerCase(Locale.ROOT).contains(query)) {
                System.out.println(" - #" + (i + 1) + ": " + pending.get(i));
                found++;
            }
        }
        if (found == 0) System.out.println("(no matches)");

        System.out.println("\nResults in Completed:");
        int foundC = 0;
        for (int i = 0; i < completedCount; i++) {
            if (completed[i].toLowerCase(Locale.ROOT).contains(query)) {
                System.out.println(" - #" + (i + 1) + ": " + completed[i]);
                foundC++;
            }
        }
        if (foundC == 0) System.out.println("(no matches)");
    }

    // === Option 7 ===
    private static void clearCompleted() {
        if (completedCount == 0) {
            System.out.println("Completed list is already empty.");
            return;
        }
        String confirm = getNonEmptyString("Type YES to clear all completed tasks: ");
        if (confirm.equalsIgnoreCase("YES")) {
            Arrays.fill(completed, 0, completedCount, null);
            completedCount = 0;
            System.out.println("Cleared all completed tasks.");
        } else {
            System.out.println("Canceled.");
        }
    }

    // === Option 8 ===
    private static void exportSnapshot() {
        String file = "snapshot.txt";
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            w.write("=== Pending (" + pending.size() + ") ===\n");
            for (int i = 0; i < pending.size(); i++) {
                w.write((i + 1) + ") " + pending.get(i) + "\n");
            }
            w.write("\n=== Completed (" + completedCount + ") ===\n");
            for (int i = 0; i < completedCount; i++) {
                w.write((i + 1) + ") " + completed[i] + "\n");
            }
            System.out.println("Snapshot exported to " + file);
        } catch (IOException e) {
            System.out.println("Error exporting snapshot: " + e.getMessage());
        }
    }

    // === Append to completed[] with dynamic grow ===
    private static void appendCompleted(String task) {
        if (completedCount == completed.length) {
            // grow array by 50%
            int newSize = completed.length + Math.max(1, completed.length / 2);
            String[] bigger = new String[newSize];
            System.arraycopy(completed, 0, bigger, 0, completed.length);
            completed = bigger;
        }
        completed[completedCount++] = task;
    }

    // === Persistence ===
    private static void ensureDataFolder() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            boolean ok = dir.mkdirs();
            if (!ok) {
                System.out.println("Warning: could not create data folder, files will be in project root.");
            }
        }
    }

    private static void loadAllData() {
        // pending
        List<String> loadedPending = readLinesSafe(PENDING_FILE);
        pending.clear();
        pending.addAll(loadedPending);

        // completed
        List<String> loadedCompleted = readLinesSafe(COMPLETED_FILE);
        completedCount = 0;
        if (completed.length < Math.max(10, loadedCompleted.size())) {
            completed = new String[Math.max(10, loadedCompleted.size())];
        }
        for (String s : loadedCompleted) {
            completed[completedCount++] = s;
        }
    }

    private static void saveAllData() {
        writeLinesSafe(PENDING_FILE, pending);
        ArrayList<String> comp = new ArrayList<>();
        for (int i = 0; i < completedCount; i++) comp.add(completed[i]);
        writeLinesSafe(COMPLETED_FILE, comp);
    }

    private static List<String> readLinesSafe(String filename) {
        List<String> out = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) return out;

        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (!line.trim().isEmpty()) out.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading " + filename + ": " + e.getMessage());
        }
        return out;
    }

    private static void writeLinesSafe(String filename, List<String> lines) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(filename))) {
            for (String s : lines) {
                w.write(s);
                w.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving " + filename + ": " + e.getMessage());
        }
    }

    // === Recursive input validators ===
    private static int getIntInRange(String prompt, int min, int max) {
        System.out.print(prompt);
        String raw = SC.nextLine();
        try {
            int n = Integer.parseInt(raw.trim());
            if (n < min || n > max) {
                System.out.println("Please enter a number between " + min + " and " + max + ".");
                return getIntInRange(prompt, min, max); // recursive retry
            }
            return n;
        } catch (NumberFormatException e) {
            System.out.println(e + "\nYou did not give me an integer value.\nTry again!");
            return getIntInRange(prompt, min, max); // recursive retry
        }
    }

    private static String getNonEmptyString(String prompt) {
        System.out.print(prompt);
        String s = SC.nextLine();
        if (s == null || s.trim().isEmpty()) {
            System.out.println("Input cannot be empty. Try again!");
            return getNonEmptyString(prompt); // recursive retry
        }
        return s.trim();
    }
}
