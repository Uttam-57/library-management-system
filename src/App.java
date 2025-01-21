class Node {
    int id;
    Node left, right;

    public Node(int id) {
        this.id = id;
        left = right = null;
    }
}

class BST {
    Node root;

    // Constructor
    BST() {
        root = null;
    }

    // Insert method
    void insert(int id) {
        root = insertRec(root, id);
    }

    // Recursive insert method
    Node insertRec(Node root, int id) {
        // If the tree is empty, return a new node
        if (root == null) {
            root = new Node(id);
            return root;
        }

        // Otherwise, recur down the tree
        if (id < root.id)
            root.left = insertRec(root.left, id);
        else if (id > root.id)
            root.right = insertRec(root.right, id);

        // Return the (unchanged) node pointer
        return root;
    }

    // In-order traversal of the tree (to test the insertion)
    void inorder() {
        inorderRec(root);
    }

    // Recursive in-order traversal
    void inorderRec(Node root) {
        if (root != null) {
            inorderRec(root.left);
            System.out.println(root.id);
            inorderRec(root.right);
        }
    }
}

public class App {
    public static void main(String[] args) {
        BST tree = new BST();

        // Insert data into the BST
        tree.insert(5);
        tree.insert(2);
        tree.insert(9);
        tree.insert(7);
        tree.insert(8);

        // Print in-order traversal of the tree
        tree.inorder();
    }
}
