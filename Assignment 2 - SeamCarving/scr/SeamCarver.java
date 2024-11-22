import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

/**
 * The SeamCarver class provides methods for content-aware image resizing
 * using the seam carving technique.
 */
public class SeamCarver {
    private Picture picture;
    private int width, height;
    private int[][] edgeTo;

    /**
     * Constructs a SeamCarver object based on the given Picture.
     *
     * @param picture the input picture
     * @throws IllegalArgumentException if the input picture is null
     */
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        this.picture = new Picture(picture);
        width = picture.width();
        height = picture.height();
        edgeTo = new int[width][height];
    }

    /**
     * Returns the current picture.
     *
     * @return a copy of the current picture
     */
    public Picture picture() {
        return new Picture(picture);
    }

    /**
     * Returns the width of the current picture.
     *
     * @return the width of the current picture
     */
    public int width() {
        return width;
    }

    /**
     * Returns the height of the current picture.
     *
     * @return the height of the current picture
     */
    public int height() {
        return height;
    }

    /**
     * Calculates the energy of the pixel at column {@code x} and row {@code y}.
     * The energy is a measure of the importance of a pixel in the image, with
     * higher energy indicating more important pixels.
     *
     * @param x the column index of the pixel
     * @param y the row index of the pixel
     * @return the energy of the pixel at column {@code x} and row {@code y}
     * @throws IllegalArgumentException if the indices are out of bounds
     */
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) throw new IllegalArgumentException();
        if (x == 0 || y == 0 || x == width - 1 || y == height - 1) return 1000;
        Color colorXplus = picture.get(x + 1, y);
        Color colorXminus = picture.get(x - 1, y);
        Color colorYplus = picture.get(x, y + 1);
        Color colorYminus = picture.get(x, y - 1);

        int redX = colorXplus.getRed() - colorXminus.getRed();
        int greenX = colorXplus.getGreen() - colorXminus.getGreen();
        int blueX = colorXplus.getBlue() - colorXminus.getBlue();

        int redY = colorYplus.getRed() - colorYminus.getRed();
        int greenY = colorYplus.getGreen() - colorYminus.getGreen();
        int blueY = colorYplus.getBlue() - colorYminus.getBlue();

        double deltaX = redX * redX + greenX * greenX + blueX * blueX;
        double deltaY = redY * redY + greenY * greenY + blueY * blueY;
        return Math.sqrt(deltaX + deltaY);
    }

    /**
     * Finds and returns the sequence of indices for the horizontal seam.
     *
     * @return an array of column indices that form the horizontal seam
     */
    public int[] findHorizontalSeam() {
        double[] prevDistanceTo = new double[height];
        double[] distanceTo = new double[height];

        for (int y = 0; y < height; y++) {
            prevDistanceTo[y] = energy(0, y);
        }

        for (int x = 1; x < width; x++) {
            for (int y = 0; y < height; y++) {
                distanceTo[y] = Double.POSITIVE_INFINITY;
                double currentEnergy = energy(x, y);
                for (int dy = -1; dy <= 1; dy++) {
                    int prevY = y + dy;
                    if (prevY >= 0 && prevY < height) {
                        double distance = prevDistanceTo[prevY] + currentEnergy;
                        if (distanceTo[y] > distance) {
                            distanceTo[y] = distance;
                            edgeTo[x][y] = prevY;
                        }
                    }
                }
            }
            for (int y = 0; y < height; y++) {
                prevDistanceTo[y] = distanceTo[y];
            }
        }

        double minPath = Double.POSITIVE_INFINITY;
        int vertex = 0;
        for (int y = 0; y < height; y++) {
            double dist = distanceTo[y];
            if (minPath > dist) {
                minPath = dist;
                vertex = y;
            }
        }

        int[] seamPath = new int[width];
        for (int x = width - 1; x >= 0; x--) {
            seamPath[x] = vertex;
            vertex = edgeTo[x][vertex];
        }

        return seamPath;
    }

    /**
     * Finds and returns the sequence of indices for the vertical seam.
     *
     * @return an array of row indices that form the vertical seam
     */
    public int[] findVerticalSeam() {
        double[] prevDistanceTo = new double[width];
        double[] distanceTo = new double[width];

        for (int x = 0; x < width; x++) {
            prevDistanceTo[x] = energy(x, 0);
        }

        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                distanceTo[x] = Double.POSITIVE_INFINITY;
                double currentEnergy = energy(x, y);
                for (int dx = -1; dx <= 1; dx++) {
                    int prevX = x + dx;
                    if (prevX >= 0 && prevX < width) {
                        double distance = prevDistanceTo[prevX] + currentEnergy;
                        if (distanceTo[x] > distance) {
                            distanceTo[x] = distance;
                            edgeTo[x][y] = prevX;
                        }
                    }
                }
            }
            for (int x = 0; x < width; x++) {
                prevDistanceTo[x] = distanceTo[x];
            }
        }

        double minPath = Double.POSITIVE_INFINITY;
        int vertex = 0;
        for (int x = 0; x < width; x++) {
            double dist = distanceTo[x];
            if (minPath > dist) {
                minPath = dist;
                vertex = x;
            }
        }

        int[] seamPath = new int[height];
        for (int y = height - 1; y >= 0; y--) {
            seamPath[y] = vertex;
            vertex = edgeTo[vertex][y];
        }

        return seamPath;
    }

    /**
     * Removes the horizontal seam from the current picture.
     *
     * @param seam an array of column indices that form the horizontal seam
     * @throws IllegalArgumentException if the seam is invalid
     */
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width || height <= 1)
            throw new IllegalArgumentException();

        int prevSeam = seam[0];
        for (int i = 1; i < width; i++) {
            if (Math.abs(seam[i] - prevSeam) > 1) throw new IllegalArgumentException();
            prevSeam = seam[i];
        }

        Picture newPicture = new Picture(width, height - 1);
        for (int x = 0; x < width; x++) {
            int newY = 0;
            for (int y = 0; y < height; y++) {
                if (y != seam[x]) {
                    newPicture.set(x, newY++, picture.get(x, y));
                }
            }
        }
        picture = newPicture;
        height--;
    }

    /**
     * Removes the vertical seam from the current picture.
     *
     * @param seam an array of row indices that form the vertical seam
     * @throws IllegalArgumentException if the seam is invalid
     */
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height || width <= 1)
            throw new IllegalArgumentException();

        int prevSeam = seam[0];
        for (int i = 1; i < height; i++) {
            if (Math.abs(seam[i] - prevSeam) > 1) throw new IllegalArgumentException();
            prevSeam = seam[i];
        }

        Picture newPicture = new Picture(width - 1, height);
        for (int y = 0; y < height; y++) {
            int newX = 0;
            for (int x = 0; x < width; x++) {
                if (x != seam[y]) {
                    newPicture.set(newX++, y, picture.get(x, y));
                }
            }
        }
        picture = newPicture;
        width--;
    }

    /**
     * Unit testing of this class.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Picture picture = new Picture("HJocean.png");
        SeamCarver sc = new SeamCarver(picture);
        sc.picture().show();
        for (int i = 0; i < 150; i++) {
            int[] seam = sc.findVerticalSeam();
            sc.removeVerticalSeam(seam);
        }
        sc.picture().show();
    }
}