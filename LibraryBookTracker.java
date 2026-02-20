import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class LibraryBookTracker {
public static void main(String[] args) {
int recordsCount = 0;
int errorCount = 0;
int searchResults = 0;
int booksAdded = 0;

        try {

            if (args.length < 2) {
                throw new InsufficientArgumentsException("ERROR: YOU MUST ENTER THE FILE NAME AND THE OPERATION");
            }

            String fileName = args[0];
            if (!fileName.endsWith(".txt")) {
                throw new InvalidFileNameException("ERROR: THE FILE MUST END WITH .txt");
            }

            File catalog = new File(args[0]);
            if (!catalog.exists()) {
                catalog.createNewFile();
            }

            List<Book> inventory = new ArrayList<>();

            //  READ CATALOG 
            try (BufferedReader reader = new BufferedReader(new FileReader(catalog))) {
                String line = reader.readLine();

                while (line != null) {

                    try {
                        String[] parts = line.split(":");

                        if (parts.length != 4) {
                            throw new MalformedBookEntryException("MISSING FIELDS");
                        }

                        String title = parts[0].trim();
                        if (title.isEmpty()) throw new MalformedBookEntryException("TITLE IS EMPTY");

                        String author = parts[1].trim();
                        if (author.isEmpty()) throw new MalformedBookEntryException("AUTHOR IS EMPTY");

                        String isbn = parts[2].trim();
                        if (isbn.length() != 13) throw new InvalidISBNException("ISBN MUST BE 13 DIGITS");

                        for (int i = 0; i < isbn.length(); i++) {
                            if (!Character.isDigit(isbn.charAt(i))) {
                                throw new InvalidISBNException("ISBN MUST CONTAIN ONLY DIGITS");
                            }
                        }

                        int copies;
                        try {
                            copies = Integer.parseInt(parts[3].trim());
                        } catch (NumberFormatException e) {
                            throw new MalformedBookEntryException("COPIES IS NOT A NUMBER");
                        }

                        if (copies <= 0) throw new MalformedBookEntryException("COPIES MUST BE POSITIVE");

                        inventory.add(new Book(title, author, isbn, copies));
                        recordsCount++;

                    } catch (BookCatalogException e) {
                        errorCount++;
                        logError(catalog, line, e.getMessage());
                    }

                    line = reader.readLine();
                }
            }

            String op = args[1];

            boolean isIsbn = op.length() == 13 && allDigits(op);
            boolean isRecord = op.contains(":") && op.split(":").length == 4;

            //  ADD BOOK 
            if (isRecord) {

                try {
                    String[] newParts = op.split(":");

                    String newTitle = newParts[0].trim();
                    if (newTitle.isEmpty()) throw new MalformedBookEntryException("TITLE IS EMPTY");

                    String newAuthor = newParts[1].trim();
                    if (newAuthor.isEmpty()) throw new MalformedBookEntryException("AUTHOR IS EMPTY");

                    String newIsbn = newParts[2].trim();
                    if (newIsbn.length() != 13) throw new InvalidISBNException("ISBN MUST BE 13 DIGITS");

                    for (int i = 0; i < newIsbn.length(); i++) {
                        if (!Character.isDigit(newIsbn.charAt(i))) {
                            throw new InvalidISBNException("ISBN MUST CONTAIN ONLY DIGITS");
                        }
                    }

                    int newCopies;
                    try {
                        newCopies = Integer.parseInt(newParts[3].trim());
                    } catch (NumberFormatException e) {
                        throw new MalformedBookEntryException("COPIES IS NOT A NUMBER");
                    }

                    if (newCopies <= 0) throw new MalformedBookEntryException("COPIES MUST BE POSITIVE");

                    Book newBook = new Book(newTitle, newAuthor, newIsbn, newCopies);
                    inventory.add(newBook);
                    booksAdded++;

                    Collections.sort(inventory, (a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));

                    FileWriter fw = new FileWriter(catalog, false);
                    for (Book b : inventory) {
                        fw.write(b.fileFormat() + "\n");
                    }
                    fw.close();

                    newBook.printHeader();
                    newBook.printFormet();

                } catch (BookCatalogException e) {
                    errorCount++;
                    logError(catalog, op, e.getMessage());
                    System.out.println(e.getMessage());
                }
            }

            else if (isIsbn) {

                int matches = 0;

                for (Book b : inventory) {
                    if (b.getIsbn().equals(op)) {
                        matches++;
                    }
                }

                if (matches > 1) {
                    throw new DuplicateISBNException("DUPLICATE ISBN FOUND");
                }

                if (matches == 0) {
                    System.out.println("Book not found");
                } else {

                    inventory.get(0).printHeader();

                    for (Book b : inventory) {
                        if (b.getIsbn().equals(op)) {
                            b.printFormet();
                            searchResults++;
                        }
                    }
                }
            }

            else {

                boolean found = false;

                for (Book b : inventory) {
                    if (b.getTitle().toLowerCase().contains(op.toLowerCase())) {
                        if (!found) b.printHeader();
                        b.printFormet();
                        searchResults++;
                        found = true;
                    }
                }

                if (!found) {
                    System.out.println("Book not found");
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        finally {
            System.out.println("Records read: " + recordsCount);
            System.out.println("Errors: " + errorCount);
            System.out.println("Search results: " + searchResults);
            System.out.println("Books added: " + booksAdded);
            System.out.println("Thank you for using the Library Book Tracker.");
        }
    }

    private static boolean allDigits(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    private static void logError(File catalog, String badText, String message) {
        try {
            File logFile = new File(catalog.getParent(), "errors.log");
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(java.time.LocalDateTime.now() + " | " + badText + " | " + message + "\n");
            fw.close();
        } catch (Exception e) {}
    }
}