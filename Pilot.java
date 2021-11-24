import java.util.*;

public class Pilot extends Character{

    /**
     * Constructor for Pilot. Extends Character.
     * Calls the constructor from Character, passing the PathFinder and
     * PlayerCards objects.
     * @param pathFinder PathFinder object.
     * @param playerCards PlayerCards object.
     */
    Pilot(PathFinder pathFinder, PlayerCards playerCards) {
        super(pathFinder, playerCards);
    }

    /**
     * Moves the player to endCity, checking if they have enough
     * actions to move along the shortest path.
     * If the number of cities the player is traveling through is 1 or 2,
     * the number of remaining actions goes down by 1. If length is 3 or 4,
     * the number of remaining actions goes down by 2, etc.
     * @param endCity String the location the player wants to end up.
     * @return int the remaining number of actions.
     */
    @Override
    public int move(String endCity) {
        endCity = endCity.replace(" ", "_");
        if (PathFinder.cityVertex.containsKey(endCity)) {
            List<String> path = pathFinder.getShortestPath(currentCity, endCity);
            int pathLength = path.size() - 1;
            if (pathLength <= 2 * actions) {
                currentCity = endCity;
                for (int i = 0; i < pathLength; i++) {
                    if (i % 2 == 0) {
                        actions--;
                    }
                }
            } else {
                System.out.println("");
                System.out.println("You do not have enough actions to move here!");
                System.out.println("Your actions: " + actions);
                if (pathLength % 2 == 0) {
                    System.out.println("Required actions: " + (pathLength/2));
                } else {
                    System.out.println("Required actions: " + (pathLength/2 + 1));
                }
            }
        } else {
            System.out.println("That city does not exist.");
        }
        return actions;
    }
}