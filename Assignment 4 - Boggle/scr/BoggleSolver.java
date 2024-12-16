import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedHashSet;

public class BoggleSolver {
    private static final int R = 26;  // Radix for uppercase English letters
    private LinkedHashSet<String> result; // Stores the result set of valid words
    private boolean[][] onStack; // Tracks the letters currently on the stack in DFS
    private int rowsBoard, colsBoard; // Dimensions of the Boggle board
    private Node root;  // Root of trie for the dictionary
    private char[][] currentBoard;  // Stores the string representation of the board

    /**
     * Represents a node in the R-way trie structure.
     * <p>
     * Each node contains an array of references to its child nodes (one for each letter A-Z),
     * and a boolean flag indicating whether the node represents the end of a valid word in the
     * dictionary.
     */
    private static class Node {
        private Node[] next = new Node[R]; // References to child nodes for each letter A-Z
        private boolean isString; // True if the node represents the end of a valid word
    }

    /**
     * Initializes the data structure using the given array of strings as the dictionary.
     *
     * @param dictionary Array of strings containing dictionary words.
     *                   (You can assume each word in the dictionary contains only the uppercase
     *                   letters A through Z.)
     */
    public BoggleSolver(String[] dictionary) {
        root = new Node();
        for (String word : dictionary) {
            add(word);
        }
    }

    /**
     * Returns the set of all valid words in the given Boggle board, as an Iterable.
     *
     * @param board The Boggle board.
     * @return Iterable set of all valid words.
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        rowsBoard = board.rows();
        colsBoard = board.cols();

        onStack = new boolean[rowsBoard][colsBoard];
        result = new LinkedHashSet<>();
        StringBuilder word = new StringBuilder();

        currentBoard = new char[rowsBoard][colsBoard];
        for (int row = 0; row < rowsBoard; row++) {
            for (int col = 0; col < colsBoard; col++) {
                currentBoard[row][col] = board.getLetter(row, col);
            }
        }

        // Perform DFS from each cell on the board
        for (int row = 0; row < rowsBoard; row++) {
            for (int col = 0; col < colsBoard; col++) {
                dfs(row, col, root, word);
            }
        }

        return result;
    }

    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise.
     *
     * @param word The word to score.
     * @return The score of the word.
     * (You can assume the word contains only the uppercase letters A through Z.)
     */
    public int scoreOf(String word) {
        int length = word.length();
        if (length < 3 || !contains(root, word)) {
            return 0;
        }

        switch (length) {
            case 3:
            case 4:
                return 1;
            case 5:
            case 6:
                return length - 3;
            case 7:
                return 5;
            default:
                return 11;
        }
    }

    /**
     * Performs a depth-first search (DFS) to find all valid words starting from the given cell.
     *
     * @param row  The current row position.
     * @param col  The current column position.
     * @param node The current node in the trie.
     * @param word The current word being built.
     */
    private void dfs(int row, int col, Node node, StringBuilder word) {
        if (row < 0 || row >= rowsBoard || col < 0 || col >= colsBoard || onStack[row][col]) return;

        char letter = currentBoard[row][col];
        String stringLetter;
        if (letter == 'Q') {
            stringLetter = "QU";
        }
        else {
            stringLetter = String.valueOf(letter);
        }

        // Get the status of the current string in the trie
        // -1: No path to stringLetter, 0: Path exists but not a word, 1: Path exists and is a word
        int status = getStatus(node, stringLetter);
        if (status == -1) return;

        int originalLength = word.length();
        word.append(stringLetter);

        // If the current string is a valid word, add it to the result set
        if (word.length() >= 3 && status == 1) {
            result.add(word.toString());
        }

        onStack[row][col] = true;

        // Explore all 8 possible moves from the current cell
        for (int moveRow = -1; moveRow <= 1; moveRow++) {
            for (int moveCol = -1; moveCol <= 1; moveCol++) {
                if (moveRow != 0 || moveCol != 0) {
                    if (letter == 'Q') {
                        dfs(row + moveRow, col + moveCol,
                            node.next['Q' - 'A'].next['U' - 'A'], word);
                    }
                    else {
                        dfs(row + moveRow, col + moveCol, node.next[letter - 'A'], word);
                    }
                }
            }
        }

        onStack[row][col] = false;
        word.setLength(originalLength);
    }

    /**
     * Adds the key to the trie if it is not already present.
     *
     * @param key The key to add.
     * @throws IllegalArgumentException if {@code key} is {@code null}.
     */
    private void add(String key) {
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.isString = true;
        }
        else {
            int index = key.charAt(d) - 'A';
            x.next[index] = add(x.next[index], key, d + 1);
        }
        return x;
    }

    /**
     * Checks if the trie contains the given key starting from the given node.
     *
     * @param node The starting node.
     * @param key  The key to check.
     * @return True if the trie contains the key, false otherwise.
     */
    private boolean contains(Node node, String key) {
        Node x = get(node, key, 0);
        if (x == null) return false;
        return x.isString;
    }

    /**
     * Gets the status of the given key in the trie starting from the given node.
     *
     * @param node The starting node.
     * @param key  The key to check.
     * @return -1 if no path to key, 0 if path exists but not a word, 1 if path exists and is a word.
     */
    private int getStatus(Node node, String key) {
        Node x = node;
        for (int d = 0; d < key.length(); d++) {
            if (x == null) return -1;
            int index = key.charAt(d) - 'A';
            x = x.next[index];
        }
        if (x == null) return -1;
        return x.isString ? 1 : 0;
    }

    /**
     * Gets the node corresponding to the given key starting from the given node.
     *
     * @param x   The starting node.
     * @param key The key to search for.
     * @param d   The current depth in the trie.
     * @return The node corresponding to the key, or null if not found.
     */
    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        int index = key.charAt(d) - 'A';
        return get(x.next[index], key, d + 1);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}