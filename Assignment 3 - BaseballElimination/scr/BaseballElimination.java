import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The {@code BaseballElimination} class provides a solution to the baseball elimination problem
 * using the Ford-Fulkerson algorithm.
 * It determines whether a given team is mathematically eliminated from winning the division.
 */
public class BaseballElimination {
    private static final int MATCHES_START_INDEX = 4;
    private final int numberOfTeams, maxWins;
    private final String firstPlace;
    private final int[] wins, loss, left;
    private final int[][] matches;
    private final HashMap<String, Integer> teamToIndex;

    /**
     * Constructs a new {@code BaseballElimination} instance, reading team data from the specified
     * input file.
     *
     * @param filename the name of the input file containing team data
     */
    public BaseballElimination(String filename) {
        In data = new In(filename);
        numberOfTeams = data.readInt();
        data.readLine();
        wins = new int[numberOfTeams];
        loss = new int[numberOfTeams];
        left = new int[numberOfTeams];
        matches = new int[numberOfTeams][numberOfTeams];
        teamToIndex = new HashMap<>();

        int i = 0;
        int max = Integer.MIN_VALUE;
        String maxTeam = "";

        while (!data.isEmpty()) {
            String line = data.readLine();
            if (line == null) break;
            line = line.trim();
            String[] lineParts = line.split("\\s+");
            String team = lineParts[0];
            teamToIndex.put(team, i);
            wins[i] = Integer.parseInt(lineParts[1]);
            loss[i] = Integer.parseInt(lineParts[2]);
            left[i] = Integer.parseInt(lineParts[3]);
            for (int j = 0; j < numberOfTeams; j++) {
                matches[i][j] = Integer.parseInt(lineParts[j + MATCHES_START_INDEX]);
            }
            if (max < wins[i]) {
                max = wins[i];
                maxTeam = team;
            }
            i++;
        }

        // The maximum number of winnings and the team associated
        firstPlace = maxTeam;
        maxWins = max;
    }

    /**
     * Returns the number of teams in the division.
     *
     * @return the number of teams
     */
    public int numberOfTeams() {
        return numberOfTeams;
    }

    /**
     * Returns an iterable containing the names of all teams.
     *
     * @return an iterable of team names
     */
    public Iterable<String> teams() {
        return teamToIndex.keySet();
    }

    /**
     * Returns the number of wins for the specified team.
     *
     * @param team the name of the team
     * @return the number of wins
     * @throws IllegalArgumentException if the team name is invalid
     */
    public int wins(String team) {
        if (!teamToIndex.containsKey(team)) throw new IllegalArgumentException();
        int index = teamToIndex.get(team);
        return wins[index];
    }

    /**
     * Returns the number of losses for the specified team.
     *
     * @param team the name of the team
     * @return the number of losses
     * @throws IllegalArgumentException if the team name is invalid
     */
    public int losses(String team) {
        if (!teamToIndex.containsKey(team)) throw new IllegalArgumentException();
        int index = teamToIndex.get(team);
        return loss[index];
    }

    /**
     * Returns the number of remaining games for the specified team.
     *
     * @param team the name of the team
     * @return the number of remaining games
     * @throws IllegalArgumentException if the team name is invalid
     */
    public int remaining(String team) {
        if (!teamToIndex.containsKey(team)) throw new IllegalArgumentException();
        int index = teamToIndex.get(team);
        return left[index];
    }

    /**
     * Returns the number of remaining games between the two specified teams.
     *
     * @param team1 the name of the first team
     * @param team2 the name of the second team
     * @return the number of remaining games between the two teams
     * @throws IllegalArgumentException if either team name is invalid
     */
    public int against(String team1, String team2) {
        if (!teamToIndex.containsKey(team1) || !teamToIndex.containsKey(team2))
            throw new IllegalArgumentException();
        int i = teamToIndex.get(team1);
        int j = teamToIndex.get(team2);
        return matches[i][j];
    }

    /**
     * Determines if the specified team is mathematically eliminated from winning the division.
     *
     * @param team the name of the team to evaluate
     * @return {@code true} if the team is eliminated, {@code false} otherwise
     * @throws IllegalArgumentException if the team name is invalid
     */
    public boolean isEliminated(String team) {
        if (!teamToIndex.containsKey(team)) throw new IllegalArgumentException();
        if (maxWins > wins(team) + remaining(team)) return true;

        int indexEvaluatedTeam = teamToIndex.get(team);
        FordFulkerson solver = getSolver(team);
        for (int i = 0; i < numberOfTeams; i++) {
            if (i != indexEvaluatedTeam) {
                if (solver.inCut(i)) return true;
            }
        }
        return false;
    }

    /**
     * Returns a subset R of teams that eliminates the specified team if it is eliminated.
     *
     * @param team the name of the team to evaluate
     * @return an iterable of team names that eliminate the specified team, or {@code null} if the
     * team is not eliminated
     * @throws IllegalArgumentException if the team name is invalid
     */
    public Iterable<String> certificateOfElimination(String team) {
        if (!teamToIndex.containsKey(team)) throw new IllegalArgumentException();

        List<String> list = new ArrayList<>();
        if (maxWins > wins(team) + remaining(team)) {
            list.add(firstPlace);
            return list;
        }

        FordFulkerson solver = getSolver(team);
        for (String checkTeam : teamToIndex.keySet()) {
            if (!checkTeam.equals(team) && solver.inCut(teamToIndex.get(checkTeam))) {
                list.add(checkTeam);
            }
        }

        if (list.isEmpty()) return null;
        return list;
    }

    /**
     * Returns the Ford-Fulkerson solver for the flow network constructed for the specified team.
     *
     * @param evaluatedTeam the name of the team to evaluate
     * @return the Ford-Fulkerson solver
     */
    private FordFulkerson getSolver(String evaluatedTeam) {
        // Teams will have vertices from 0 to numberOfTeams - 1. Game vertices will
        // have vertices from numberOfTeams to (numberOfTeams + numberOfGames - 1).
        // Source vertex will be (numberOfTeams + numberOfGames).
        // Target vertex will be (numberOfTeams + numberOfGames + 1).
        int indexEvaluatedTeam = teamToIndex.get(evaluatedTeam);
        int numberOfGames = (numberOfTeams - 2) * (numberOfTeams - 1) / 2;
        int source = numberOfTeams + numberOfGames;
        int target = numberOfTeams + numberOfGames + 1;
        int maxEvaluatedTeam = wins(evaluatedTeam) + remaining(evaluatedTeam);
        FlowNetwork network = new FlowNetwork(numberOfTeams + numberOfGames + 2);

        int verticeNumber = numberOfTeams;
        for (int i = 0; i < numberOfTeams; i++) {
            // Add edge from team to target
            int weight = maxEvaluatedTeam - wins[i];
            FlowEdge teamToTarget = new FlowEdge(i, target, weight);
            network.addEdge(teamToTarget);

            for (int j = i + 1; j < numberOfTeams; j++) {
                if (i != indexEvaluatedTeam && j != indexEvaluatedTeam) {
                    // Add edge from source to game vertex
                    FlowEdge sourceToGameVertex = new FlowEdge(source, verticeNumber,
                                                               matches[i][j]);
                    network.addEdge(sourceToGameVertex);

                    // Add edge from game vertex to team i
                    FlowEdge gameVertexToTeamI = new FlowEdge(verticeNumber, i, Integer.MAX_VALUE);
                    network.addEdge(gameVertexToTeamI);

                    // Add edge from game vertex to team j
                    FlowEdge gameVertexToTeamJ = new FlowEdge(verticeNumber, j, Integer.MAX_VALUE);
                    network.addEdge(gameVertexToTeamJ);
                    verticeNumber++;
                }
            }
        }
        FordFulkerson solver = new FordFulkerson(network, source, target);
        return solver;
    }

    /**
     * The main method that reads input data and prints the elimination results for each team.
     *
     * @param args the command-line arguments (expected to contain the input filename)
     */
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        StdOut.println(division.teams());
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}