import java.sql.*;
import java.util.*;



public class DatabaseManager {
    public static BSTUser bu = new BSTUser();

    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    // private static Connection getConnection() throws SQLException {

    // return DriverManager.getConnection(URL, USER, PASSWORD);
    // }
    private static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root",
                "password");
        if (connection != null) {
            System.out.println("connection establis");
        } else {
            System.out.println("connection not establish");
        }
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root", "password");
    }

    public static int addBook(String title, String author) {
        int bookId = -1;
        String query = "INSERT INTO books (title, author) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                bookId = rs.getInt(1);
                System.out.println("Book Added Successfully! ID = " + bookId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookId;
    }

    public static void removeBook(int bookId) {

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE id = ?")) {
            pstmt.setInt(1, bookId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {

                System.out.println("Book Removed Successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void displayBooks() {
        String query = "SELECT id, title, author, status FROM books";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            System.out.println("Available Books:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String status = rs.getString("status");
                System.out.printf("ID: %d, Title: %s, Author: %s, Status: %s%n", id, title, author, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int addNewUser(String name, String password, Timestamp membershipEnd) {
        int userId = -1;
        String query = "INSERT INTO users (name, password, membership_end) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            pstmt.setTimestamp(3, membershipEnd);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                userId = rs.getInt(1);
                System.out.println("User Added Successfully! ID = " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        bu.insert(userId);
        return userId;
    }

    public static boolean isBookExists(int bookId) {

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM books WHERE id = ?")) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean validateUser(int userId, String password) {
        if (bu.search(userId) == userId) {
            String query = "SELECT * FROM users WHERE id = ? AND password = ?";
            try (Connection conn = getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    public static boolean isMembershipExpired(int userId) {
        String query = "SELECT membership_end FROM users WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Timestamp membershipEnd = rs.getTimestamp("membership_end");
                // Ensure any books borrowed by this user are marked as available
                updateBorrowedBooksForRemovedUser(conn, userId);
                return membershipEnd.before(new Timestamp(System.currentTimeMillis()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // not Expired
    }

    public static void removeUser(int userId) {
        if (bu.search(userId) == userId) {
            try (Connection conn = getConnection()) {

                // Delete the user from the database
                try (PreparedStatement deleteUserStmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                    deleteUserStmt.setInt(1, userId);
                    deleteUserStmt.executeUpdate();

                    System.out.println("User removed successfully!");
                }
            } catch (SQLException e) {
                e.printStackTrace();

            }
        } else {
            System.out.println("NO data Found");
        }

    }

    private static void updateBorrowedBooksForRemovedUser(Connection conn, int userId) throws SQLException {
        // Check which books are borrowed by the user
        try (PreparedStatement checkBorrowedBooksStmt = conn.prepareStatement(
                "SELECT id FROM books WHERE borrowed_by = ?");
                PreparedStatement returnBookStmt = conn.prepareStatement(
                        "UPDATE books SET status = 'Available', borrowed_by = NULL WHERE borrowed_by = ?");
                PreparedStatement addTransactionStmt = conn.prepareStatement(
                        "INSERT INTO transactions (user_id, book_id, action, timestamp) VALUES (?, ?, 'return', ?)")) {

            // Get the list of borrowed books
            checkBorrowedBooksStmt.setInt(1, userId);
            ResultSet rs = checkBorrowedBooksStmt.executeQuery();

            while (rs.next()) {
                int bookId = rs.getInt("id");

                // Update the book status to available
                returnBookStmt.setInt(1, userId);
                returnBookStmt.executeUpdate();

                // Record the transaction of returning the book
                addTransactionStmt.setInt(1, userId);
                addTransactionStmt.setInt(2, bookId);
                addTransactionStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                addTransactionStmt.executeUpdate();
                removeUser(userId);
            }
        }
    }

    public static void borrowBook(int userId, int bookId) {
        try (Connection conn = getConnection();
                PreparedStatement checkUserStmt = conn
                        .prepareStatement("SELECT membership_end FROM users WHERE id = ?");
                PreparedStatement checkBookStmt = conn.prepareStatement("SELECT status FROM books WHERE id = ?");
                PreparedStatement borrowStmt = conn
                        .prepareStatement("UPDATE books SET status = 'Borrowed', borrowed_by = ? WHERE id = ?");
                PreparedStatement addTransactionStmt = conn.prepareStatement(
                        "INSERT INTO transactions (user_id, book_id, action, timestamp) VALUES (?, ?, 'borrow', ?)")) {

            // Check if the user’s membership is expired
            checkUserStmt.setInt(1, userId);
            ResultSet userRs = checkUserStmt.executeQuery();
            if (userRs.next()) {
                Timestamp membershipEnd = userRs.getTimestamp("membership_end");
                if (membershipEnd.before(new Timestamp(System.currentTimeMillis()))) {
                    System.out.println("Membership expired. Cannot borrow book.");
                    return;
                }
            }

            // Check if the book is available
            checkBookStmt.setInt(1, bookId);
            ResultSet bookRs = checkBookStmt.executeQuery();
            if (bookRs.next() && "Available".equals(bookRs.getString("status"))) {
                // Update book status to borrowed
                borrowStmt.setInt(1, userId);
                borrowStmt.setInt(2, bookId);
                borrowStmt.executeUpdate();

                // Record the transaction
                addTransactionStmt.setInt(1, userId);
                addTransactionStmt.setInt(2, bookId);
                addTransactionStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                addTransactionStmt.executeUpdate();

                System.out.println("Book Borrowed Successfully!");
            } else {
                System.out.println("Book is not available for borrowing!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void returnBook(int userId, int bookId) {
        try (Connection conn = getConnection();
                PreparedStatement checkUserStmt = conn
                        .prepareStatement("SELECT membership_end FROM users WHERE id = ?");
                PreparedStatement checkBookStmt = conn.prepareStatement("SELECT borrowed_by FROM books WHERE id = ?");
                PreparedStatement returnStmt = conn
                        .prepareStatement("UPDATE books SET status = 'Available', borrowed_by = NULL WHERE id = ?");
                PreparedStatement addTransactionStmt = conn.prepareStatement(
                        "INSERT INTO transactions (user_id, book_id, action, timestamp) VALUES (?, ?, 'return', ?)")) {

            // Check if the user’s membership is expired
            checkUserStmt.setInt(1, userId);
            ResultSet userRs = checkUserStmt.executeQuery();
            if (userRs.next()) {
                Timestamp membershipEnd = userRs.getTimestamp("membership_end");
                if (membershipEnd.before(new Timestamp(System.currentTimeMillis()))) {
                    System.out.println("Membership expired. Cannot return book.");
                    return;
                }
            }

            // Check who borrowed the book
            checkBookStmt.setInt(1, bookId);
            ResultSet bookRs = checkBookStmt.executeQuery();
            if (bookRs.next() && userId == bookRs.getInt("borrowed_by")) {
                // Update book status to available
                returnStmt.setInt(1, bookId);
                returnStmt.executeUpdate();

                // Record the transaction
                addTransactionStmt.setInt(1, userId);
                addTransactionStmt.setInt(2, bookId);
                addTransactionStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                addTransactionStmt.executeUpdate();

                System.out.println("Book Returned Successfully!");
            } else {
                System.out.println("This book is not borrowed by the user or already returned!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayUsers() {
        String query = "SELECT id, name FROM users";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            System.out.println("Registered Users:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.printf("ID: %d, Name: %s%n", id, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showUserDetails() {
        System.out.print("Enter User ID: ");
        Scanner scanner = new Scanner(System.in);
        int userId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString("password");
                Timestamp membershipEnd = rs.getTimestamp("membership_end");
                System.out.printf("ID: %d, Name: %s, Password: %s, Membership End: %s%n", userId, name, password,
                        membershipEnd);
            } else {
                System.out.println("User not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayUserDetails(int userId) {
        if (bu.search(userId) == userId) {
            String query = "SELECT * FROM users WHERE id = ?";
            try (Connection conn = getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");
                    String password = rs.getString("password");
                    Timestamp membershipEnd = rs.getTimestamp("membership_end");
                    System.out.printf("ID: %d, Name: %s, Password: %s, Membership End: %s%n", userId, name, password,
                            membershipEnd);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("User not found!");
        }

    }

    public static BSTUser getBu() {
        return bu;
    }

    public static void setBu(BSTUser bu) {
        DatabaseManager.bu = bu;
    }

    public static String getUrl() {
        return URL;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }
    public static void main(String[] args) {

        DatabaseManager dbm = new DatabaseManager();
        dbm.displayBooks();
    }

}