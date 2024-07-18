import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class dictionary {

    public static boolean searchFile(String directory, String fileName) {
        File dir = new File(directory);
        File[] files = dir.listFiles((d, name) -> name.equalsIgnoreCase(fileName));
        return files != null && files.length > 0;
    }

    public static void editFile(String filePath, List<String> userInput) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            lines.addAll(userInput);
            Collections.sort(lines, new IgnoreParenthesesComparator()); 
            Files.write(Paths.get(filePath), lines);
            System.out.println("File edited successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while editing the file.");
            e.printStackTrace();
        }
    }

    public static void createAndEditFile(String filePath, List<String> userInput) {
        try {
            Collections.sort(userInput, new IgnoreParenthesesComparator()); 
            FileWriter writer = new FileWriter(filePath);
            for (String line : userInput) {
                writer.write(line + System.lineSeparator());
            }
            writer.close();
            System.out.println("File created and edited successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file.");
            e.printStackTrace();
        }
    }

    public static void deleteEntryByLineNumber(String filePath, int lineNumber) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            
            if (lineNumber >= 1 && lineNumber <= lines.size()) {
                lines.remove(lineNumber - 1); 
                Files.write(Paths.get(filePath), lines);
                System.out.println("Entry at line " + lineNumber + " deleted successfully.");
            } else {
                System.out.println("Invalid line number. No changes made.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the entry.");
            e.printStackTrace();
        }
    }

    public static void displayFileContents(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            System.out.println("Current contents of the file:");
            for (int i = 0; i < lines.size(); i++) {
                System.out.println((i + 1) + ". " + lines.get(i)); 
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }

    public static void updateEntryByLineNumber(String filePath, int lineNumber, String newEntry) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            
            if (lineNumber >= 1 && lineNumber <= lines.size()) {
                lines.set(lineNumber - 1, newEntry); 
                Files.write(Paths.get(filePath), lines);
                System.out.println("Entry at line " + lineNumber + " updated successfully.");
            } else {
                System.out.println("Invalid line number. No changes made.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the entry.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the directory path: ");
        String directory = scanner.nextLine();

        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("The directory path does not exist.");
            scanner.close();
            return; 
        }
        
        while (true) {
            System.out.print("Enter the file name: ");
            String fileName = scanner.nextLine();

            String filePath = directory + "/" + fileName;

            boolean fileExists = searchFile(directory, fileName);

            List<String> userInput = new ArrayList<>();

            if (fileExists) {
                System.out.println("File found.");

                displayFileContents(filePath);

                System.out.println("\nMenu:");
                System.out.println("1. Add new entries");
                System.out.println("2. Delete an entry");
                System.out.println("3. Update an entry");
                System.out.println("4. Display current entries");
                System.out.println("5. Exit");

                boolean exit = false;
                while (!exit) {
                    System.out.print("Choose an option: ");
                    String option = scanner.nextLine();

                    switch (option) {
                        case "1":
                            System.out.println("Enter the text to add to the file (type 'exit' on a new line to finish):");
                            while (true) {
                                String inputLine = scanner.nextLine();
                                if ("exit".equalsIgnoreCase(inputLine)) {
                                    break;
                                }
                                userInput.add(inputLine);
                            }
                            editFile(filePath, userInput);
                            userInput.clear();
                            break;
                        case "2":
                            System.out.print("Enter the line number of the entry you want to delete: ");
                            int deleteLineNumber = Integer.parseInt(scanner.nextLine());
                            deleteEntryByLineNumber(filePath, deleteLineNumber);
                            break;
                        case "3":
                            System.out.print("Enter the line number of the entry you want to update: ");
                            int updateLineNumber = Integer.parseInt(scanner.nextLine());
                            System.out.print("Enter the new entry text: ");
                            String newEntry = scanner.nextLine();
                            updateEntryByLineNumber(filePath, updateLineNumber, newEntry);
                            break;
                        case "4":
                            displayFileContents(filePath);
                            break;
                        case "5":
                            exit = true;
                            break;
                        default:
                            System.out.println("Invalid option. Please choose again.");
                            break;
                    }
                }
                break; 
            } else {
                System.out.print("File not found. Do you want to create a new file? (yes/no): ");
                String createFileResponse = scanner.nextLine();
                if ("yes".equalsIgnoreCase(createFileResponse)) {
                    System.out.println("Enter the text to write to the file (type 'exit' on a new line to finish):");
                    while (true) {
                        String inputLine = scanner.nextLine();
                        if ("exit".equalsIgnoreCase(inputLine)) {
                            break;
                        }
                        userInput.add(inputLine);
                    }
                    createAndEditFile(filePath, userInput);
                    break; 
                } else {
                    System.out.println("Please enter a new file name.");
                }
            }
        }

        scanner.close();
    }

    static class IgnoreParenthesesComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            String s1Clean = s1.replaceAll("[()\\s]", "").toLowerCase();
            String s2Clean = s2.replaceAll("[()\\s]", "").toLowerCase();
            return s1Clean.compareTo(s2Clean);
        }
    }
}