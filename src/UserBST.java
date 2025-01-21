// class UserNode {
//     User user;
//     UserNode left, right;

//     public UserNode(User user) {
//         this.user = user;
//         this.left = null;
//         this.right = null;
//     }
// }

// public class UserBST {
//     private UserNode root;

//     public UserBST() {
//         this.root = null;
//     }

//     public void insert(User user) {
//         root = insertRec(root, user);
//     }

//     private UserNode insertRec(UserNode root, User user) {
//         if (root == null) {
//             root = new UserNode(user);
//             return root;
//         }
//         if (user.getId() < root.user.getId()) {
//             root.left = insertRec(root.left, user);
//         } else if (user.getId() > root.user.getId()) {
//             root.right = insertRec(root.right, user);
//         }
//         return root;
//     }

//     public User search(int userId) {
//         UserNode result = searchRec(root, userId);
//         return (result != null) ? result.user : null;
//     }

//     private UserNode searchRec(UserNode root, int userId) {
//         if (root == null || root.user.getId() == userId) {
//             return root;
//         }
//         if (userId < root.user.getId()) {
//             return searchRec(root.left, userId);
//         }
//         return searchRec(root.right, userId);
//     }
// }
