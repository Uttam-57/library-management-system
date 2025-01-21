import java.sql.Timestamp;
import java.util.Scanner;

public class Librarian {
    private Scanner scanner;

    public Librarian() {
        this.scanner = new Scanner(System.in);
    }

    public void addBook() {
        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Book Author: ");
        String author = scanner.nextLine();
        DatabaseManager.addBook(title, author);
    }

    public void removeBook() {
        System.out.print("Enter Book ID to Remove: ");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        DatabaseManager.removeBook(bookId);
    }

    public void displayBooks() {
        DatabaseManager.displayBooks();
    }

    public void addUser() {
        System.out.print("Enter User Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter User Password: ");
        String password = scanner.nextLine();
        Timestamp membershipEnd = new Timestamp(System.currentTimeMillis() + 7 * 60 * 1000); // Membership lasts 7 minutes
        int userId = DatabaseManager.addNewUser(name, password, membershipEnd);
        if (userId != -1) {
            System.out.println("User Added Successfully! ID = " + userId);
        }
    }

    public void displayUsers() {
        DatabaseManager.displayUsers();
    }

    public void showUserDetails() {
        System.out.print("Enter User ID to View Details: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        DatabaseManager.displayUserDetails(userId);
    }
}
