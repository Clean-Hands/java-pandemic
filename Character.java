import java.util.*;

public class Character {
    
    PathFinder pathFinder;
    PlayerCards playerCards;
    String currentCity = "Atlanta";
    int actions = 4;

    /**
     * Constructor for Character.
     * Takes a PathFinder object and PlayerCards object and assigns them
     * to an instance variable of the Character class.
     * @param pathFinder PathFinder object.
     * @param playerCards PlayerCards object.
     */
    Character(PathFinder pathFinder, PlayerCards playerCards) {
        this.pathFinder = pathFinder;
        this.playerCards = playerCards;
    }

    /**
     * Moves the player to endCity, checking if they have enough
     * actions to move along the shortest path.
     * Decreases actions and returns the new number.
     * @param endCity String the location the player wants to end up.
     * @return int the remaining number of actions.
     */
    public int move(String endCity) {
        endCity = endCity.replace(" ", "_");
        if (PathFinder.cityVertex.containsKey(endCity)) {
            List<String> path = pathFinder.getShortestPath(currentCity, endCity);
            int pathLength = path.size() - 1;
            if (pathLength <= actions) {
                currentCity = endCity;
                actions -= pathLength;
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
     * If the player has the card for endCity, sets currentCity to endCity
     * and removes the card from their hand.
     * Decreases actions and returns the new number.
     * @param endCity String the location the player wants to end up.
     * @return int the remaining number of actions.
     */
    public int directFlight(String endCity) {
        endCity = endCity.replace(" ", "_");
        if (PathFinder.cityVertex.containsKey(endCity)) {
            if (Pandemic.playerHand.contains(endCity)) {
                Pandemic.playerHand.remove(endCity);
                currentCity = endCity;
                actions--;
            } else {
                System.out.println("You do not have the card for " + endCity.replace("_", " ") + ".");
            }
        } else {
            System.out.println("That city does not exist.");
        }
        return actions;
    }

    /**
     * If the player has the card for currentCity, sets currentCity to endCity
     * and removes the card from their hand.
     * Decreases actions and returns the new number.
     * @param endCity String the location the player wants to end up.
     * @return int the remaining number of actions.
     */
    public int charterFlight(String endCity) {
        endCity = endCity.replace(" ", "_");
        if (PathFinder.cityVertex.containsKey(endCity)) {
            if (Pandemic.playerHand.contains(currentCity)) {
                Pandemic.playerHand.remove(currentCity);
                currentCity = endCity;
                actions--;
            } else {
                System.out.println("You do not have the card for " + currentCity.replace("_", " ") + ".");
            }
        } else {
            System.out.println("That city does not exist.");
        }
        return actions;
    }

    /**
     * If the player is in a city with a research station and endCity has a
     * research station, then sets currentCity to endCity.
     * Decreases actions and returns the new number.
     * @param endCity String the location the player wants to end up.
     * @param researchStations ArrayList<String> of cities that have research stations.
     * @return int the remaining number of actions.
     */
    public int shuttleFlight(String endCity, ArrayList<String> researchStations) {
        endCity = endCity.replace(" ", "_");
        if (PathFinder.cityVertex.containsKey(endCity)) {
            if (researchStations.contains(currentCity)) {
                if (researchStations.contains(endCity)) {
                    Pandemic.playerHand.remove(endCity);
                    currentCity = endCity;
                    actions--;
                } else {
                    System.out.println("You do not have a research station in " + endCity.replace("_", " ") + ".");
                } 
            } else {
                System.out.println("You do not have a research station in " + currentCity.replace("_", " ") + ".");
            }
        } else {
            System.out.println("That city does not exist.");
        }
        return actions;
    }

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

    /**
     * Returns the number of remaining actions.
     * @return int the number of remaining actions.
     */
    public int getActions() {
        return actions;
    }

    /**
     * Changes actions by actionsChange.
     * Returns the remaining number of actions.
     * @param actionsChange int the amount to change actions by.
     * @return int the remaining number of actions.
     */
    public int changeActions(int actionsChange) {
        if (actions + actionsChange >= 0) {
            actions += actionsChange;
        } else {
            System.out.println("You do not have enough actions.");
        }
        return actions;
    }

    /**
     * Sets actions to 4.
     */
    public void resetActions() {
        actions = 4;
    }

    /**
     * Returns currentCity.
     * @return String currentCity.
     */
    public String getLocation() {
        return currentCity;
    }
}
