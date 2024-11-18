import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Topological;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The WordNet class provides a data type for semantic lexicons.
 * It handles the structure and operations related to WordNet synsets and hypernyms.
 */
public class WordNet {
    // Maps to store the associations between nouns and vertices
    private final Map<String, List<Integer>> nounToVertex;
    private final Map<Integer, String> vertexToNoun;
    private final SAP sap;

    /**
     * Constructor that takes the names of the two input files.
     *
     * @param synsets   the name of the synsets file
     * @param hypernyms the name of the hypernyms file
     * @throws IllegalArgumentException if any of the arguments are null
     */
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }

        In inSynsets = new In(synsets);
        nounToVertex = new HashMap<>();
        vertexToNoun = new HashMap<>();

        // Process the synsets file
        int numberOfVertices = 0;
        while (!inSynsets.isEmpty()) {
            numberOfVertices++;
            String line = inSynsets.readLine();
            String[] lineParts = line.split(",");
            int vertex = Integer.parseInt(lineParts[0]);
            String[] nouns = lineParts[1].split(" ");
            for (String noun : nouns) {
                nounToVertex.computeIfAbsent(noun, ignored -> new ArrayList<>()).add(vertex);
            }
            vertexToNoun.put(vertex, lineParts[1]);
        }

        // Process the hypernyms file
        In inHypernyms = new In(hypernyms);
        Digraph digraph = new Digraph(numberOfVertices);
        while (!inHypernyms.isEmpty()) {
            String line = inHypernyms.readLine();
            String[] lineParts = line.split(",");
            int v = Integer.parseInt(lineParts[0]);
            for (int i = 1; i < lineParts.length; i++) {
                int w = Integer.parseInt(lineParts[i]);
                digraph.addEdge(v, w);
            }
        }

        // Check for cycles and ensure the graph is rooted
        Topological topological = new Topological(digraph);
        if (!topological.hasOrder() || !isRooted(digraph)) throw new IllegalArgumentException();
        sap = new SAP(digraph);
    }

    /**
     * Checks if the digraph is rooted.
     *
     * @param digraph the digraph to check
     * @return true if the digraph is rooted, false otherwise
     */
    private boolean isRooted(Digraph digraph) {
        int roots = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if (digraph.outdegree(i) == 0) roots++;
        }
        return roots == 1;
    }

    /**
     * Returns all WordNet nouns.
     *
     * @return an iterable of all WordNet nouns
     */
    public Iterable<String> nouns() {
        return nounToVertex.keySet();
    }

    /**
     * Checks if a word is a WordNet noun.
     *
     * @param word the word to check
     * @return true if the word is a WordNet noun, false otherwise
     * @throws IllegalArgumentException if the word is null
     */
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        return nounToVertex.containsKey(word);
    }

    /**
     * Calculates the distance between two nouns.
     *
     * @param nounA the first noun
     * @param nounB the second noun
     * @return the distance between the two nouns
     * @throws IllegalArgumentException if either noun is not a WordNet noun
     */
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        List<Integer> v = nounToVertex.get(nounA);
        List<Integer> w = nounToVertex.get(nounB);
        int dist = sap.length(v, w);
        return dist;
    }

    /**
     * Finds the common ancestor of two nouns in the shortest ancestral path.
     *
     * @param nounA the first noun
     * @param nounB the second noun
     * @return the synset that is the common ancestor of the two nouns
     * @throws IllegalArgumentException if either noun is not a WordNet noun
     */
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        List<Integer> v = nounToVertex.get(nounA);
        List<Integer> w = nounToVertex.get(nounB);
        int sca = sap.ancestor(v, w);
        return vertexToNoun.get(sca);
    }

    /**
     * Unit testing of this class.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        String synsets = "synsets.txt";
        String hypernyms = "hypernyms.txt";
        WordNet wordnet = new WordNet(synsets, hypernyms);

        String word1 = "plectron";
        String word2 = "novitiate";
        StdOut.println("Word \"" + word1 + "\" is part of Wordnet? " + wordnet.isNoun(word1));
        StdOut.println("Word \"" + word2 + "\" is part of Wordnet? " + wordnet.isNoun(word2));
        StdOut.println("Shortest common ancestor: " + wordnet.sap(word1, word2));
        StdOut.println("Distance = " + wordnet.distance(word1, word2));
        StdOut.println(wordnet.nounToVertex.get(word1));
        StdOut.println(wordnet.nounToVertex.get(word2));
    }
}