# Pandemic
The board game Pandemic coded in Java.

## Board
To be able to see where you can travel to and the colors of the cities, please use [this map](https://media.wnyc.org/i/1500/900/c/80/1/1537_Pandemic_main.jpg) as a reference. It looks like this: 

![Pandemic board](https://media.wnyc.org/i/1500/900/c/80/1/1537_Pandemic_main.jpg)

## Usage
To begin, type this into the terminal:
```
$ java Pandemic
```
The game will then ask you which character you would like. You may type either `Medic` or `Pilot`. 

---
### Medic
When the Medic removes cubes from a city, all cubes are removed. Once a cure is found for a disease, if they ever pass through a city of that color, the cubes are automatically removed without taking an action.

### Pilot
The Pilot is able to move two spaces while only using one action, instead of the normal one space per action.

---
Once you have selected a character, it will set up the game by infecting 9 cities, putting 1 cube in 3 cities, 2 cubes in another 3 cities , and 3 cubes in the remaining 3 cities. It also draws 5 Player Cards for you and shows the cards to you.

After this, it will ask you what difficulty you would like to play. You may type `Introductory`, `Standard`, or `Heroic`. This determines the number of Epidemic Cards that are in the Player Deck.

Difficulty  | Number of Epidemic Cards
------------|-------------------------
Introductory| 4
Standard    | 5
Heroic      | 6

The game starts with a research station in Atlanta.

## Playing the Game
Every turn you have 4 actions to spend, moving, removing cubes, finding cures, etc.

Here are all of the commands you can use your actions on:
Command    | Description | Actions Required
-----------|-------------|-------------
move | Move to a city connected by white line(s) to the one you are in. | **Medic:** 1 per city moved through
||| **Pilot:** 1 per 2 cities moved through
direct flight | Discard a Player Card and move to the city on the card. | 1
charter flight | Discard the Player Card for the city you are in and move anywhere. | 1
shuttle flight | Move from a city with a research station to any other city that has a research station. | 1
remove cubes | Remove cubes from the city you are currently in. | 1
cure disease | While at a research station, discard 5 Player Cards of the same color from your hand to cure the disease of that color. | 1
build research station | Discard the Player Card that matches the city you are in to place a research station there. | 1
show research stations | Displays the locations of all built research stations. | 0
show hand | Displays the Player Cards you have. | 0
show cures | Displays the current cures. | 0
show cubes | Displays all cities and the number of cubes in each. | 0
end turn | Ends your turn early. | 0
help | Displays available commands. | 0

### Between Turns
After your turn is over, the program will do a few things:

* Draws 2 Player Cards and adds them to your hand
    - If your hand has more than 7 cards, you will be prompted to remove cards to get back to 7
    - If there are no more Player Cards to draw, you lose the game
* Infects cities by drawing Infection Cards
    - If the number of cubes at a city increased beyond 3, an outbreak occurs and infects all neighboring cities
* Displays the number of outbreaks that have happened throughout the game
    - If the number of outbreaks ever gets to 8 or greater, you lose the game
* Print all cities and the number of cubes in each

### Cures
Once you have 5 Player Cards of one color, you can go to a city with a research station and use the `cure disease` command. Once you have cured the disease, removing cubes of that color become much easier. As previously stated, if a Medic ever passes through a city of the cured color(s), the cubes are automatically removed without taking an action. When a Pilot removes cubes from a city, they can remove all cubes from the city they are in while only using one action. Finding all 4 cures results in you winning the game.

### Eradication
When all cubes of a cured color are removed from the board, no new cubes of that color can be placed, no matter what.

## Rubric
### Correct and efficient use of inheritance with at least one superclass and two subclasses
We have two superclasses and three subclasses.

Our `Medic` and `Pilot` classes both extend the `Character` class. Here are the headers of the `Medic` and `Pilot` classes:
```java
public class Medic extends Character {
    ...
}
```
```java
public class Pilot extends Character {
    ...
}
```
Both `Medic.java` and `Pilot.java` inherit all methods from `Character.java` but also override some methods. An example of this is `removeCube()`, which `Pilot.java` inherits but `Medic.java` overrides.

Here is the `removeCube()` method in `Character.java`:
```java
/**
 * Removes a cube from currentCity.
 * If the color of the cube has a cure, removes all of the cubes.
 * Decreases actions and returns the new number.
 * @return int the remaining number of actions.
 */
public int removeCube() {
    if (actions > 0) {
        int beforeCubes = pathFinder.getCubes(currentCity);
        int afterCubes;
        if (beforeCubes > 0) {
            if (Pandemic.curedBooleans.get(PlayerCards.getColor(currentCity))) {
                afterCubes = pathFinder.changeCubes(currentCity, -3);
            } else {
                afterCubes = pathFinder.changeCubes(currentCity, -1);
            }
            actions--;
            System.out.println("You removed " + (beforeCubes - afterCubes) + " cubes.");
            System.out.println("There is/are now " + afterCubes + " cube(s) at " + currentCity.replace("_", " ") + ".");
        } else {
            System.out.println("Cubes are already at zero.");
        }
    } else {
        System.out.println("You do not have enough actions.");
    }
    return actions;
}
```
And here is the `removeCube()` method in `Medic.java`:
```java
/**
 * Removes all cubes from the location the player is currently in.
 * Decreases actions and returns the new number.
 * @return int the remaining number of actions.
 */
@Override
public int removeCube() {
    if (actions > 0) {
        int beforeCubes = pathFinder.getCubes(currentCity);
        if (beforeCubes > 0) {
            int afterCubes = pathFinder.changeCubes(currentCity, -3);
            actions--;
            System.out.println("You removed " + (beforeCubes - afterCubes) + " cubes.");
            System.out.println("There is/are now " + afterCubes + " cube(s) at " + currentCity.replace("_", " ") + ".");
        } else {
            System.out.println("Cubes are already at zero.");
        }
    } else {
        System.out.println("You do not have enough actions.");
    }
    return actions;
}
```
We also have the class `PlayerCards` which extends `Cards`.
### Correct and efficient use of two other class concepts
We use a graph for the connections between cities, breadth-first-search for finding the shortest path between two cities, and many HashMaps that store information about cities and the diseases.

You can see the graph implementation in `GraphImplementation.java` and is instantiated in `PathFinder.java`. Here is the instantiation of the graph along with some HashMaps in `PathFinder.java`:
```java
public class PathFinder {
	static GraphImplementation cityGraph = new GraphImplementation(false);
	static Map<String, Integer> cityVertex = new HashMap<String, Integer>();
	static Map<Integer, String> vertexCity = new HashMap<Integer, String>();
	private Map<String, Integer> cubeNumMap = new HashMap<String, Integer>();
    ...
}
```
The breadth-first-search method can also be seen in `PathFinder.java`:
```java
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
```
### Sufficiently substantive project
We coded pretty much the entire Pandemic board game within Java, with the infections, outbreaks, epidemics, varying movement types, varying characters, all 48 cities, all 4 diseases, cures for all 4 diseases, a clean and informative user interface, and the ability to win or lose.
### Good style and organization, including JavaDocs style methods comments
As you can see, all of our functions have JavaDocs style comments that explains what they take, do, and/or return.
### README complete and clear
See above :)

## Prompt
### What does your project do that is interesting and substantive?
If you look at the rubric item `Sufficiently substantive project`, you can see what makes our project interesting and substantive.
### Why is inheritance useful for your previously specified superclass and subclasses?
Inheritance is useful because it allows us to have methods that both `Medic` and `Pilot` use, but then also have their own functions/versions of the functions that are specific to that character and their abilities.

Inheritance is also useful for our `Cards` and `PlayerCards` because `PlayerCards` has all of the characteristics of `Cards` and then also has its own, such as the color of the card.
### For each of the two additional previously-specified class concepts that you used, why is that concept the best to use in your project?
A graph is the best to represent the locations and the connections between them because each city can have any number of connections to any other city, and a graph is best at representing that.

Breadth-first-search is the best to use because it (alongside the `getShortestPath()` method) enables us to find paths between the graph nodes, and then choose the shortest one, which is also the most desirable for the user.

HashMaps are the best to use when storing information about the cities and the diseases because it allows us to have the key be a city name or color, and then the value be an aspect about that city or color.