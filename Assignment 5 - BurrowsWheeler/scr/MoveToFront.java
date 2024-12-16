import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * The {@code MoveToFront} class provides static methods for applying the move-to-front encoding and
 * decoding.
 * It uses a custom doubly linked list to maintain the sequence of characters.
 */
public class MoveToFront {
    private static final int R = 256; // Number of ASCII characters
    private static final int W = 8; // Number of bits to write each index

    /**
     * Inner class representing a node in the doubly linked list.
     */
    private static class Node {
        char item;
        Node next, prev;
    }

    /**
     * Inner class representing a doubly linked list with operations for move-to-front encoding and
     * decoding.
     */
    private static class DoubleLinkedList {
        Node first, last;

        /**
         * Adds a character to the front of the list.
         *
         * @param c the character to add
         */
        public void addFirst(char c) {
            Node oldFirst = first;
            first = new Node();
            first.item = c;
            first.prev = null;
            first.next = oldFirst;
            if (last == null) last = first;
            else oldFirst.prev = first;
        }

        /**
         * Adds a character to the end of the list.
         *
         * @param c the character to add
         */
        public void addLast(char c) {
            Node oldLast = last;
            last = new Node();
            last.item = c;
            last.next = null;
            last.prev = oldLast;
            if (first == null) first = last;
            else oldLast.next = last;
        }

        /**
         * Returns the index of the specified character and removes the node from the list.
         *
         * @param c the character to find
         * @return the index of the character, or -1 if not found
         */
        public int getIndex(char c) {
            Node curr = first;
            int index = 0;
            while (curr != null) {
                if (curr.item == c) {
                    if (curr.prev != null) curr.prev.next = curr.next;
                    if (curr.next != null) curr.next.prev = curr.prev;
                    if (curr == first) first = first.next;
                    if (curr == last) last = last.prev;
                    return index;
                }
                index++;
                curr = curr.next;
            }
            return -1;
        }

        /**
         * Returns the character at the specified index and removes the node from the list.
         *
         * @param i the index of the character
         * @return the character at the specified index, or '!' if not found
         */
        public char getChar(int i) {
            Node curr = first;
            int index = 0;
            while (curr != null) {
                if (index == i) {
                    if (curr.prev != null) curr.prev.next = curr.next;
                    if (curr.next != null) curr.next.prev = curr.prev;
                    if (curr == first) first = first.next;
                    if (curr == last) last = last.prev;
                    return curr.item;
                }
                index++;
                curr = curr.next;
            }
            return '!';
        }
    }

    /**
     * Applies move-to-front encoding, reading from standard input and writing to standard output.
     */
    public static void encode() {
        DoubleLinkedList st = new DoubleLinkedList();
        for (int i = 0; i < R; i++) st.addLast((char) i);

        // Iterate for each char in the input
        while (!BinaryStdIn.isEmpty()) {
            char input = BinaryStdIn.readChar();
            int index = st.getIndex(input);
            st.addFirst(input);
            BinaryStdOut.write(index, W);
        }
        BinaryStdOut.close();
    }

    /**
     * Applies move-to-front decoding, reading from standard input and writing to standard output.
     */
    public static void decode() {
        DoubleLinkedList st = new DoubleLinkedList();
        for (int i = 0; i < R; i++) st.addLast((char) i);

        // Iterate for each char in the input
        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readInt(W);
            char c = st.getChar(index);
            BinaryStdOut.write(c);
            st.addFirst(c);
        }
        BinaryStdOut.close();
    }

    /**
     * Main method to determine whether to encode or decode based on command-line arguments.
     *
     * @param args the command-line arguments
     * @throws IllegalArgumentException if the arguments are not valid
     */
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else
            throw new IllegalArgumentException(
                    "Usage: MoveToFront - for encoding, + for decoding");
    }
}