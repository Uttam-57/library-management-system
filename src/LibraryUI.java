import java.sql.Timestamp;
import java.util.Scanner;

public class LibraryUI {
    private Scanner scanner;
    private Librarian librarian;
    
    public LibraryUI() {
        this.librarian = new Librarian();
        this.scanner = new Scanner(System.in);
    }

    public void displayMainMenu() {
        while (true) {
            System.out.println("Welcome to Library Management System");
            System.out.println("1. Enter as Librarian");
            System.out.println("2. Enter as User");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    librarianLogin();
                    break;
                case 2:
                    userLogin();
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void librarianLogin() {
        System.out.print("Enter Librarian Password: ");
        String password = scanner.nextLine();
        if ("L1234".equals(password)) {
            displayLibrarianMenu();
        } else {
            System.out.println("Incorrect Password!");
        }
    }

    private void displayLibrarianMenu() {
        while (true) {
            System.out.println("Librarian Menu");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Display Books");
            System.out.println("4. Add User");
            System.out.println("5. Show All Users");
            System.out.println("6. Show User Details");
            System.out.println("7. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    librarian.addBook();
                    break;
                case 2:
                    librarian.removeBook();
                    break;
                case 3:
                    librarian.displayBooks();
                    break;
                case 4:
                    librarian.addUser();
                    break;
                case 5:
                    librarian.displayUsers();
                    break;
                case 6:
                    librarian.showUserDetails();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void userLogin() {
        while (true) {
            System.out.println("User Login");
            System.out.println("1. New User");
            System.out.println("2. Existing User");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    createNewUser();
                    break;
                case 2:
                    authenticateUser();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void createNewUser() {
        System.out.print("Enter Your Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Your Password: ");
        String password = scanner.nextLine();
        Timestamp membershipEnd = new Timestamp(System.currentTimeMillis() + 7 * 60 * 1000); // Membership ends in 7 minutes
        int userId = DatabaseManager.addNewUser(name, password, membershipEnd);
        if (userId != -1) {
            System.out.println("New User Created! ID: " + userId);
        }
    }

    private void authenticateUser() {
        System.out.print("Enter User ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (DatabaseManager.validateUser(userId, password)) {
            if (DatabaseManager.isMembershipExpired(userId)) {
                
                System.out.println("Membership Expired!");
                DatabaseManager.removeUser(userId);
            } else {
                displayUserMenu(userId);
            }
        } else {
            System.out.println("Invalid Credentials!");
        }
    }

    private void displayUserMenu(int userId) {
        while (true) {
            System.out.println("User Menu");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. Show Book Details");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    borrowBook(userId);
                    break;
                case 2:
                    returnBook(userId);
                    break;
                case 3:
                    displayBooksForUser();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    //User

    private void borrowBook(int userId) {
        System.out.print("Enter Book ID to Borrow: ");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        DatabaseManager.borrowBook(userId, bookId);
    }

    private void returnBook(int userId) {
        System.out.print("Enter Book ID to Return: ");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        DatabaseManager.returnBook(userId, bookId);
    }

    private void displayBooksForUser() {
        DatabaseManager.displayBooks();
    }

    public static void main(String[] args) {
        LibraryUI ui = new LibraryUI();
        ui.displayMainMenu();
    }
}
