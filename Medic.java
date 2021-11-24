import java.util.*;

public class Medic extends Character {

    /**
     * Constructor for Medic. Extends Character.
     * Calls the constructor from Character, passing the PathFinder and
     * PlayerCards objects.
     * @param pathFinder PathFinder object.
     * @param playerCards PlayerCards object.
     */
    Medic(PathFinder pathFinder, PlayerCards playerCards) {
        super(pathFinder, playerCards);
    }

    /**
     * Moves the player to endCity, checking if they have enough
     * actions to move along the shortest path.
     * If the city the player is moving through is a color that is cured,
     * all of the cubes on that city are removed.
     * Decreases actions by the number of cities traveled to and returns the new number.
     * @param endCity String the location the player wants to end up.
     * @return int the remaining number of actions.
     */
    @Override
    public int move(String endCity) {
        endCity = endCity.replace(" ", "_");
        if (PathFinder.cityVertex.containsKey(endCity)) {
            List<String> path = pathFinder.getShortestPath(currentCity, endCity);
            int pathLength = path.size() - 1;
            if (pathLength <= actions) {
                currentCity = endCity;
                actions -= pathLength;
                for (String city : path) {
                    if (Pandemic.curedBooleans.get(PlayerCards.getColor(city))) {
                        int beforeCubes = pathFinder.getCubes(city);
                        int afterCubes = pathFinder.changeCubes(city, -3);
                        if (beforeCubes > 0) {
                            System.out.println("You removed " + (beforeCubes - afterCubes) + " cube(s).");
                            System.out.println("There is/are now " + afterCubes + " cube(s) at " + city.replace("_", " ") + ".");
                        }
                    }
                }
            } else {
                System.out.println("");
                System.out.println("You do not have enough actions to move here!");
                System.out.println("Your actions: " + actions);
                System.out.println("Required actions: " + pathLength);
            }
        } else {
            System.out.println("That city does not exist.");
        }
        return actions;
    }

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
                System.out.println("You removed " + (beforeCubes - afterCubes) + " cube(s).");
                System.out.println("There is/are now " + afterCubes + " cube(s) at " + currentCity.replace("_", " ") + ".");
            } else {
                System.out.println("Cubes are already at zero.");
            }
        } else {
            System.out.println("You do not have enough actions.");
        }
        return actions;
    }
}
