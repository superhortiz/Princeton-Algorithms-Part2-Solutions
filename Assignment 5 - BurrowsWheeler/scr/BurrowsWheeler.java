import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * The {@code BurrowsWheeler} class provides methods for applying the Burrows-Wheeler transform
 * and inverse transform. It uses {@code CircularSuffixArray} for suffix array construction.
 */
public class BurrowsWheeler {
    private static final int R = 256; // Extended ASCII

    /**
     * Applies the Burrows-Wheeler transform, reading from standard input and writing to standard
     * output.
     */
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        int n = csa.length();
        char[] t = new char[n];
        int index, first = 0;
        for (int i = 0; i < n; i++) {
            index = csa.index(i);
            t[i] = s.charAt((index + n - 1) % n);
            if (index == 0) first = i;
        }
        BinaryStdOut.write(first);
        for (char c : t) {
            BinaryStdOut.write(c);
        }
        BinaryStdOut.close();
    }

    /**
     * Applies the Burrows-Wheeler inverse transform, reading from standard input and writing to
     * standard output.
     */
    public static void inverseTransform() {
        // Read data from standard input
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();
        char[] arrayT = t.toCharArray();
        int n = arrayT.length;

        // Create the next array
        int[] next = new int[n];
        for (int i = 0; i < n; i++) next[i] = i;
        sort(arrayT, next);

        // Reconstruct the original string using the next array
        int nextIndex = first;
        for (int i = 0; i < n; i++) {
            BinaryStdOut.write(arrayT[next[nextIndex]]);
            nextIndex = next[nextIndex];
        }
        BinaryStdOut.close();
    }

    /**
     * Sorts the characters and constructs the next array using counting sort.
     *
     * @param a     the character array
     * @param index the array to store sorted indices
     */
    private static void sort(char[] a, int[] index) {
        int n = a.length;
        int[] count = new int[R + 1];
        for (int i = 0; i < n; i++) count[a[i] + 1]++;  // Count frequencies
        for (int r = 0; r < R; r++) count[r + 1] += count[r];  // Cumulates
        for (int i = 0; i < n; i++) index[count[a[i]]++] = i;  // Complete index
    }

    /**
     * Main method to determine whether to apply Burrows-Wheeler transform or inverse transform
     * based on command-line arguments.
     *
     * @param args the command-line arguments
     * @throws IllegalArgumentException if the arguments are not valid
     */
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else
            throw new IllegalArgumentException(
                    "Usage: BurrowsWheeler - for encoding, + for decoding");
    }
}