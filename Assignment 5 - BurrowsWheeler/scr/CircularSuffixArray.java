import edu.princeton.cs.algs4.StdOut;

/**
 * The {@code CircularSuffixArray} class provides a circular suffix array for a given string.
 * It uses a 3-way radix quicksort algorithm to sort the suffixes.
 */
public class CircularSuffixArray {
    private final int length;  // Length of the input string
    private final int[] index;  // Array to store the indices of sorted suffixes

    /**
     * Initializes a circular suffix array of the given string.
     *
     * @param s the input string
     * @throws IllegalArgumentException if the input string is null
     */
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        length = s.length();
        index = new int[length];
        for (int i = 0; i < length; i++) index[i] = i;
        sort(s.toCharArray(), index, 0, length - 1, 0, length);
    }

    /**
     * Sorts the circular suffix array using 3-way radix quicksort.
     *
     * @param a         the input character array
     * @param positions the array of indices to sort
     * @param lo        the lower bound of the range to sort
     * @param hi        the upper bound of the range to sort
     * @param d         the current character index to sort by
     * @param n         the length of the input string
     */
    private static void sort(char[] a, int[] positions, int lo, int hi, int d, int n) {
        if (hi <= lo || d >= n) return;
        int lt = lo, gt = hi;
        int v = a[(positions[lo] + d) % n];
        int i = lo + 1;
        while (i <= gt) {
            int t = a[(positions[i] + d) % n];
            if (t < v) exch(positions, lt++, i++);
            else if (t > v) exch(positions, i, gt--);
            else i++;
        }
        sort(a, positions, lo, lt - 1, d, n);
        sort(a, positions, lt, gt, d + 1, n);
        sort(a, positions, gt + 1, hi, d, n);
    }

    /**
     * Exchanges two elements in an array.
     *
     * @param a the array
     * @param i the index of the first element
     * @param j the index of the second element
     */
    private static void exch(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    /**
     * Returns the length of the input string.
     *
     * @return the length of the input string
     */
    public int length() {
        return length;
    }

    /**
     * Returns the index of the ith sorted suffix.
     *
     * @param i the index of the sorted suffix
     * @return the index of the ith sorted suffix
     * @throws IllegalArgumentException if the index is out of bounds
     */
    public int index(int i) {
        if (i < 0 || i >= length) throw new IllegalArgumentException();
        return index[i];
    }

    /**
     * Unit testing of the {@code CircularSuffixArray} class.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        CircularSuffixArray csa = new CircularSuffixArray(s);
        int index = csa.index(11);
        StdOut.println(index);
    }
}