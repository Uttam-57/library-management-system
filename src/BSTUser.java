import java.util.*;

class BSTUser{
    class Node {
        int data;
        Node left, right;

        Node(int data) {
            this.data = data;
            left = right = null;
        }
    }

    Node root = null;

    // Insert a node into the BST
    void insert(int data) {
        Node n = new Node(data);
        if (root == null) {
            root = n;
            return;
        } else {
            Node temp = root;
            while (true) {
                if (temp.left == null && data < temp.data) {
                    temp.left = n;
                    return;
                } else if (temp.right == null && data > temp.data) {
                    temp.right = n;
                    return;
                } else {
                    if (data < temp.data)
                        temp = temp.left;
                    else
                        temp = temp.right;
                }
            }
        }

    }

    // find min from BST
    int min(Node root) {
        while (root.left != null) {
            root = root.left;
        }
        return root.data;
    }

    // Find max from BST
    int max(Node root) {
        while (root.right != null) {
            root = root.right;
        }
        return root.data;
    }

    // Search a given key from BST
  //Search a given key from BST
	int search(int key){
		if(root==null){
			System.out.println("Tree is empty");
			return -1;
		}
		else{
			Node temp=root;
			while(temp!=null){
				if(temp.data==key){
					System.out.println("Key found");
					return temp.data;
				}
				else if(key<temp.data){
					temp=temp.left;
				}
				else if(key>temp.data){
					temp=temp.right;
				}
			}
			System.out.println("Key not found");
		}
        return -1;
	}

    // Delete a value from the BST
    void delete(int value) {
        root = deleteRecursive(root, value);
    }

    Node deleteRecursive(Node root, int value) {
        if (root == null) {
            return null;
        }
        if (value < root.data) {
            root.left = deleteRecursive(root.left, value);
        } else if (value > root.data) {
            root.right = deleteRecursive(root.right, value);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            root.data = min(root.right);
            root.right = deleteRecursive(root.right, root.data);
        }
        return root;
    }

    // print inorder of BST
    void inorder(Node node) {

        if (node == null) {
            return;
        } else {

            inorder(node.left);
            System.out.print(node.data + " ");
            inorder(node.right);
        }
    }
}
