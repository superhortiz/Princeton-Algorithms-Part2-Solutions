import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * The Outcast class identifies the outcast in an array of WordNet nouns.
 * An outcast is defined as the noun that is the least related to the others.
 */
public class Outcast {
    private final WordNet wordnet;

    /**
     * Constructs an Outcast object using the provided WordNet object.
     *
     * @param wordnet the WordNet object to be used for outcast determination
     */
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    /**
     * Given an array of WordNet nouns, returns the outcast.
     * The outcast is the noun that has the maximum distance to all other nouns.
     *
     * @param nouns an array of WordNet nouns
     * @return the outcast noun
     */
    public String outcast(String[] nouns) {
        String outcast = nouns[0];
        int maxDist = -1;

        for (String nounA : nouns) {
            int dist = 0;
            for (String nounB : nouns) {
                if (!nounA.equals(nounB)) {
                    dist = dist + wordnet.distance(nounA, nounB);
                }
            }

            if (dist > maxDist) {
                maxDist = dist;
                outcast = nounA;
            }
        }
        return outcast;
    }

    /**
     * Reads input files and prints the outcast for each file.
     * The first two command-line arguments are the synsets and hypernyms files for WordNet.
     * The remaining arguments are files containing arrays of WordNet nouns.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}