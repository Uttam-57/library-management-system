
public class LibraryManagementSystem {
    public static void main(String[] args) {
        LibraryUI ui = new LibraryUI();

        BSTUser tree = DatabaseManager.bu;
        // Insert data into the BST
        tree.insert(1);
        tree.insert(2);
        tree.insert(3);
        tree.insert(4);
        tree.insert(5);

        ui.displayMainMenu();

    }
}
