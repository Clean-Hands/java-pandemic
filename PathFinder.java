import java.util.*;
import java.io.*;

public class PathFinder {
	static GraphImplementation cityGraph = new GraphImplementation(false);
	static Map<String, Integer> cityVertex = new HashMap<String, Integer>();
	static Map<Integer, String> vertexCity = new HashMap<Integer, String>();
	private Map<String, Integer> cubeNumMap = new HashMap<String, Integer>();
	private ArrayList<String> outbreakCities = new ArrayList<>();
    PlayerCards playerCards;

	/**
	 * Constructs a PathFinder that represents the graph with nodes (vertices)
	 * specified as in nodeFile and edges specified as in edgeFile.
	 * @param nodeFile name of the file with the node names
	 * @param edgeFile name of the file with the edge names
	 */
	public PathFinder() {
		readNodes();
		readEdges();
	}

	/**
	 * Loads the nodes into the cityGraph, along with adding the articles and their vertices
	 * to cityVertex and vertexCity.
	 * @param nodeFile String of the location of the file with the nodes inside.
	 */
	private void readNodes() {
		File inputFile = new File("Cities.tsv");
		Scanner scanner = null;
		try {
			scanner = new Scanner(inputFile);
		} catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		}

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.length() > 0 && !line.substring(0, 1).equals("#")) {
				Integer nodeNum = cityGraph.addVertex();
				cityVertex.put(line, nodeNum);
				vertexCity.put(nodeNum, line);
				cubeNumMap.put(line, 0);
			}
		}
	}

	/**
	 * Adds the edges between the nodes to cityGraph.
	 * @param edgeFile String of the location of the file with the edges inside.
	 */
	private void readEdges() {
		File inputFile = new File("Roads.tsv");
		Scanner scanner = null;
		try {
			scanner = new Scanner(inputFile);
		} catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		}

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.length() > 0 && !line.substring(0, 1).equals("#")) {
				String[] splitline = line.split("\\s+");
				String begin = splitline[0];
				String end = splitline[1];

				int beginNum = cityVertex.get(begin);
				int endNum = cityVertex.get(end);
				cityGraph.addEdge(beginNum, endNum);
			}
		}
	}

	/**
	 * Returns a map of the predecessor of each node
	 * on the shortest path from the start node.
	 * Adapted from Data Structures and Abstractions with Java by Carrano and Henry.
	 * @param start String value of the starting node.
	 * @return HashMap of the predecessor of each node
	 * on the shortest path from the start node.
	 */
	public Map<Integer, Integer> breadthFirstSearch(String start) {
		HashMap<Integer, Integer> pathMap = new HashMap<>();
		Queue<Integer> vertexQueue = new LinkedList<>();
		Integer originVertex = cityVertex.get(start);
		pathMap.put(originVertex, null);
		vertexQueue.add(originVertex);
		while (!vertexQueue.isEmpty()) {
			int nextVertex = vertexQueue.remove();
			for (int neighborVertex : cityGraph.getNeighbors(nextVertex)) {
				if (!pathMap.containsKey(neighborVertex)) {
					pathMap.put(neighborVertex, nextVertex);
					vertexQueue.add(neighborVertex);
				}
			}
		}
		return pathMap;
	}

	/**
	 * Returns a shortest path from startNode to endNode, represented as list that has
	 * startNode at position 0, endNode in the final position, and the names of each node
	 * on the path (in order) in between. If the two nodes are the same, then the
	 * "path" is just a single node. If no path exists, returns an empty list.
	 * @param startNode name of the starting article node
	 * @param endNode name of the ending article node
	 * @return list of the names of nodes on the shortest path
	 */
	public List<String> getShortestPath(String startNode, String endNode) {
		Map<Integer, Integer> pathMap = breadthFirstSearch(startNode);
		ArrayList<String> pathList = new ArrayList<>();
		int startNodeIndex = cityVertex.get(startNode);
		int endNodeIndex = cityVertex.get(endNode);
		int currentNodeIndex = endNodeIndex;
		while (currentNodeIndex != startNodeIndex) {
			pathList.add(vertexCity.get(currentNodeIndex));
			if (pathMap.containsKey(currentNodeIndex)) {
				currentNodeIndex = pathMap.get(currentNodeIndex);
			} else {
				return null;
			}
		}
		pathList.add(vertexCity.get(startNodeIndex));
		Collections.reverse(pathList);
		return pathList;
	}

	/**
	 * Returns the number of cubes on the passed city.
	 * @param city String the city you would like the number of cubes of.
	 * @return int the number of cubes on that city.
	 */
	public int getCubes(String city) {
		return cubeNumMap.get(city);
	}

	/**
	 * Prints all cities along with their cube counts.
	 */
	public void printAllCubes() {
		String blueCities = "";
		String yellowCities = "";
		String blackCities = "";
		String redCities = "";
		for (String city : cubeNumMap.keySet()) {
			String cityColor = PlayerCards.getColor(city);
			switch(cityColor) {
				case "blue":
					blueCities += city.replace("_", " ") + ": " + cubeNumMap.get(city) + "\n";
					break;
				case "yellow":
					yellowCities += city.replace("_", " ") + ": " + cubeNumMap.get(city) + "\n";
					break;
				case "black":
					blackCities += city.replace("_", " ") + ": " + cubeNumMap.get(city) + "\n";
					break;
				case "red":
					redCities += city.replace("_", " ") + ": " + cubeNumMap.get(city) + "\n";
					break;
			}
		}
		System.out.println("");
		System.out.println("Blue Cities:");
		System.out.println(blueCities);
		System.out.println("Yellow Cities:");
		System.out.println(yellowCities);
		System.out.println("Black Cities:");
		System.out.println(blackCities);
		System.out.println("Red Cities:");
		System.out.println(redCities);
	}

	/**
	 * Changes the number of cubes on city by changeAmt. If cubes go
	 * beyond 3, calls outbreak(). If disease is eradicated, doesn't
	 * change cubes at all.
	 * @param city String the city you would like to change the cubes of.
	 * @param changeAmt int the amount you want the cubes to change by.
	 * @return int the number of cubes on that city after the change.
	 */
	public int changeCubes(String city, int changeAmt) {
		if (!Pandemic.eradicatedBooleans.get(PlayerCards.getColor(city))) {
			int returnInt = changeCubesHelper(city, changeAmt);
			outbreakCities.clear();
			return returnInt;
		} else {
			return 0;
		}
	}

	/**
	 * Helper method for changeCubes().
	 * @param city String the city you would like to change the cubes of.
	 * @param changeAmt int the amount you want the cubes to change by.
	 * @return int the number of cubes on that city after the change.
	 */
	private int changeCubesHelper(String city, int changeAmt) {
		if (cubeNumMap.get(city) + changeAmt < 0) {
			cubeNumMap.replace(city, 0);
		} else if (cubeNumMap.get(city) + changeAmt > 3) {
			cubeNumMap.replace(city, 3);
			outbreak(city);
		} else {
			cubeNumMap.replace(city, cubeNumMap.get(city) + changeAmt);
		}
		return cubeNumMap.get(city);
    }

	/**
	 * Increments outbreakCounter, informs user of an outbreak, and adds
	 * one cube to all neighboring cities. It also adds city to
	 * outbreakCities to prevent an infinite loop of outbreaks.
	 * @param city String the city the outbreak occured.
	 */
	private void outbreak(String city) {
		Pandemic.outbreakCounter++;
		System.out.println("OUTBREAK IN: " + city.replace("_", " "));
		outbreakCities.add(city);
		for (int neighborIndex : cityGraph.getNeighbors(cityVertex.get(city))) {
			String neighborName = vertexCity.get(neighborIndex);
			if (!outbreakCities.contains(neighborName)) {
				int newCubeNum = changeCubesHelper(neighborName, 1);
				System.out.println("The number of cubes at " +  neighborName.replace("_", " ") + " is now " + newCubeNum + ".");
			}
		}
	}


	/**
	 * Returns a string that tells the user how many nodes
	 * and edges are in the graph.
	 * @return String containing the number of nodes
	 * and edges within the graph.
	 */
	public String toString() {
		return "Number of nodes: " + cityGraph.numVerts() + "\n" 
			+  "Number of edges: " + cityGraph.numEdges();
	}
}
