import java.util.*;

/**
 * The board game Pandemic in Java.
 */
public class Pandemic {
    
    Character character;
    Cards infectionCards = new Cards();
    PathFinder pathFinder = new PathFinder();
    PlayerCards playerCards = new PlayerCards(pathFinder);
    ArrayList<String> researchStations = new ArrayList<>();
    static ArrayList<String> playerHand = new ArrayList<>();
    static HashMap<String, Boolean> curedBooleans = new HashMap<>();
    static HashMap<String, Boolean> eradicatedBooleans = new HashMap<>();
    static int[] infectionRate = {2,2,2,3,3,4,4};
    static int infectionRateIndex = 0;
    static int outbreakCounter = 0;
    String drawnCard = "";

    /**
     * The constructor for Pandemic.
     * Instantiates a new Character object with the character type passed in.
     * Also adds a research station in Atlanta and sets up the curedBooleans
     * and the eradicatedBooleans.
     * @param characterName String the type of character the user wants to play as.
     */
    Pandemic(String characterName) {
        if (characterName.equalsIgnoreCase("medic")) {
            character = new Medic(pathFinder, playerCards);
        } else if (characterName.equalsIgnoreCase("pilot")) {
            character = new Pilot(pathFinder, playerCards);
        } else {
            System.out.println("That character does not exist.");
            System.exit(1);
        }
        researchStations.add("Atlanta");
        curedBooleans.put("blue", false);
        curedBooleans.put("yellow", false);
        curedBooleans.put("black", false);
        curedBooleans.put("red", false);

        eradicatedBooleans.put("blue", false);
        eradicatedBooleans.put("yellow", false);
        eradicatedBooleans.put("black", false);
        eradicatedBooleans.put("red", false);
    }
    
    /**
     * Carries out all necessary steps to set up and start the game.
     * Draws initial infection cards, draws cards for the playerHand,
     * and asks for and assigns the game difficulty, which also adds the
     * epidemic cards to the playerCards deck.
     */
    public void startGame() {
        System.out.println("");
        Scanner scanner = new Scanner(System.in);
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 4; j++) {
                String drawnCard = infectionCards.draw();
                int newCubeNum = character.pathFinder.changeCubes(drawnCard, i);
                System.out.println("The number of cubes at " + drawnCard.replace("_", " ") + " is now " + newCubeNum + ".");
            }
        }
        for (int i = 0; i < 5; i++) {
            playerHand.add(playerCards.draw());
        }
        playerCards.printAllCards(playerHand);
        boolean incorrectInput = true;
        while (incorrectInput) {
            System.out.println("");
            System.out.println("What difficulty would you like?");
            String difficulty = scanner.nextLine();
            incorrectInput = false;
            switch (difficulty.toLowerCase()) {
                case "introductory":
                    playerCards.addEpidemics(4);
                    break;
                case "standard":
                    playerCards.addEpidemics(5);
                    break;
                case "heroic":
                    playerCards.addEpidemics(6);
                    break;
                default:
                    System.out.println("");
                    System.out.println("That is not a valid difficulty.");
                    incorrectInput = true;
            }
        }
    }
    
    /**
     * Carries out all necessary steps that occur between turns.
     * Checks if the user has won, draws and displays playerCards
     * and if the playerHand is larger than 7 then calls the 
     * chooseCardToRemove() function, draws and displays infectionCards,
     * checks if the user has lost due to outbreaks and prints the outbreak counter.
     */
    public void betweenTurns() {
        checkWin();
        System.out.println("");
        System.out.println("");
        System.out.println("~~~~~~~~~~ Your turn has ended. ~~~~~~~~~~");
        character.resetActions();
        System.out.println("");
        checkEradication();
        System.out.println("");
        System.out.println("~~~~~~~~~~ Player Cards are being drawn. ~~~~~~~~~~");
        for (int i = 0; i < 2; i++) {
            System.out.println("");
            System.out.println("Drawing card #" + (i + 1) + ":");
            drawnCard = playerCards.draw();
            if (!drawnCard.equals("Epidemic")) {
                System.out.println("You drew: " + drawnCard.replace("_", " "));
                playerHand.add(drawnCard);
            }
        }
        if (playerHand.size() > 7) {
            playerHand = playerCards.chooseCardToRemove(playerHand);
        }
        playerCards.printAllCards(playerHand);
        System.out.println("");
        System.out.println("~~~~~~~~~~ Cities are being infected. ~~~~~~~~~~");
        for (int i = 0; i < infectionRate[infectionRateIndex]; i++) {
            System.out.println("");
            System.out.println("Infecting city #" + (i + 1) + ":");
            drawnCard = infectionCards.draw();
            int newCubeNum = character.pathFinder.changeCubes(drawnCard, 1);
            System.out.println("The number of cubes at " + drawnCard.replace("_", " ") + " is now " + newCubeNum + ".");
            if (outbreakFailure()) {
                lose();
            }
        }
        System.out.println("");
        System.out.println("Outbreak counter is at " + outbreakCounter + ".");
        printAllCubes();
    }

    /**
     * Checks if the user has at least 5 cards of one color, and if so,
     * cures that disease and removes those cards from their hand.
     * @return int the number of remaining actions.
     */
    public int cureDisease() {
        if (character.getActions() > 0) {
            int numBlueCards = 0;
            int numYellowCards = 0;
            int numBlackCards = 0;
            int numRedCards = 0;
            for (String card : playerHand) {
                String color = PlayerCards.getColor(card);
                switch (color) {
                    case "blue":
                        numBlueCards++;
                        break;
                    case "yellow":
                        numYellowCards++;
                        break;
                    case "black":
                        numBlackCards++;
                        break;
                    case "red":
                        numRedCards++;
                        break;
                }
            }
            System.out.println("");
            if (numBlueCards >= 5 && !curedBooleans.get("blue")) {
                curedBooleans.replace("blue", true);
                System.out.println("You cured blue!");
                character.changeActions(-1);
                remove5("blue");
            } else if (numYellowCards >= 5 && !curedBooleans.get("yellow")) {
                curedBooleans.replace("yellow", true);
                System.out.println("You cured yellow!");
                character.changeActions(-1);
                remove5("yellow");
            } else if (numBlackCards >= 5 && !curedBooleans.get("black")) {
                curedBooleans.replace("black", true);
                System.out.println("You cured black!");
                character.changeActions(-1);
                remove5("black");
            } else if (numRedCards >= 5 && !curedBooleans.get("red")) {
                curedBooleans.replace("red", true);
                System.out.println("You cured red!");
                character.changeActions(-1);
                remove5("red");
            } else {
                System.out.println("You do not have enough cards to cure a disease that has not been cured yet.");
            }
        } else {
            System.out.println("You do not have enough actions.");
        }
        return character.getActions();
    }

    /**
     * Removes 5 cards of the passed color from playerHand.
     * @param color String the desired color to remove.
     */
    private void remove5(String color) {
        int cardsRemoved = 0;
        for (int i = 0; i < playerHand.size(); i++) {
            if (cardsRemoved == 5)
                break;
            if (PlayerCards.getColor(playerHand.get(i)).equals(color)) {
                playerHand.remove(i);
                cardsRemoved++;
                i--;
            }
        }
    }

    /**
     * Checks if any of the diseases are eradicated. If so, then it prints
     * a statement that the disease has been eradicated.
     */
    private void checkEradication() {
        for (String desiredColor : eradicatedBooleans.keySet()) {
            boolean tempBoolean = true;
            if (curedBooleans.get(desiredColor)) {
                for (String city : PathFinder.cityVertex.keySet()) {
                    String color = PlayerCards.getColor(city);
                    if (color.equals(desiredColor)) {
                        int numCubes = pathFinder.getCubes(city);
                        if (numCubes > 0) {
                            tempBoolean = false;
                        }
                    }
                }
                eradicatedBooleans.replace(desiredColor, tempBoolean);
                if (tempBoolean) {
                    String firstLetter = desiredColor.substring(0,1);
                    System.out.println(desiredColor.replaceFirst(firstLetter, firstLetter.toUpperCase()) + " is eradicated.");
                }
            }
        }
    }

    /**
     * Prints all of the cities and their cubes by calling the PathFinder
     * printAllCubes() method.
     */
    public void printAllCubes() {
        System.out.println("");
        System.out.println("~~~~~~~~~~ Cubes Per City ~~~~~~~~~~");
        character.pathFinder.printAllCubes();
    }

    /**
     * Prints the cured status of all of the diseases.
     */
    public void printCures() {
        System.out.println("");
        for (String color : curedBooleans.keySet()) {
            String cured = "not cured";
            if (curedBooleans.get(color))
                cured = "cured";
            String firstLetter = color.substring(0,1);
            System.out.println(color.replaceFirst(firstLetter, firstLetter.toUpperCase()) + " is " + cured + ".");
        }
    }

    /**
     * Prints all of the available commands with a description of what they do.
     */
    public void help() {
        System.out.println("");
        System.out.println("Here are your available commands:");
        System.out.println("move - Move to a city connected by white line(s) to the one you are in.");
        System.out.println("direct flight - Discard a Player Card and move to the city on the card.");
        System.out.println("charter flight - Discard the Player Card for the city you are in and move anywhere.");
        System.out.println("shuttle flight - Move from a city with a research station to any other city that has a research station.");
        System.out.println("remove cubes - Remove cubes from the city you are currently in.");
        System.out.println("cure disease - While at a research station, discard 5 Player Cards of the same color from your hand to cure the disease of that color.");
        System.out.println("build research station - Discard the Player Card that matches the city you are in to place a research station there.");
        System.out.println("show research stations - Displays the locations of all built research stations.");
        System.out.println("show hand - Displays the Player Cards you have.");
        System.out.println("show cures - Displays the current cures.");
        System.out.println("show cubes - Displays all cities and the number of cubes in each.");
        System.out.println("end turn - Ends your turn early.");
        System.out.println("help - Displays available commands.");
    }

    /**
     * Returns a boolean of if the outbreak counter is greater than 7.
     * @return boolean of if the outbreak counter is greater than 7.
     */
    public boolean outbreakFailure() {
        return outbreakCounter > 7;
    }

    /**
     * Prints a statement informing the user that they lost and then
     * quits the program.
     */
    public static void lose() {
        System.out.println("");
        System.out.println("you lose :(");
        System.exit(1);
    }

    /**
     * Checks if the user has won by checking if all the diseases have been cured.
     */
    public void checkWin() {
        if (curedBooleans.get("blue") && curedBooleans.get("yellow") &&
            curedBooleans.get("black") && curedBooleans.get("red")) {
                System.out.println("CONGRATULATIONS! YOU HAVE SAVED THE WORLD! :)");
                System.exit(1);
            }
    }

    /**
     * Clears the screen by printing 75 blank lines.
     */
    static private void cls() {
        for (int i = 0; i < 75; i++) {
            System.out.println("");;
        }
    }

    /**
     * The main gameplay loop for Pandemic.
     */
    public static void main(String[] args) { // finish javadocs for Cards; make sure that if actions are reduced, it is in the javadocs; do readme; win the game
        cls();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which character would you like?");
        String input = scanner.nextLine();
        Pandemic pandemic = new Pandemic(input);
        pandemic.startGame();
        while (true) {
            int remainingActions = pandemic.character.getActions();
            while (remainingActions > 0) {
                String currentLocation = pandemic.character.getLocation();
                String displayCurrentLocation = currentLocation.replace("_", " ");
                System.out.println("");
                System.out.println("Current Location: " + displayCurrentLocation);
                System.out.println("You have " + remainingActions + " action(s) left.");
                System.out.println("What would you like to do?");
                input = scanner.nextLine();
                if (input.equalsIgnoreCase("move")) {
                    System.out.println("");
                    System.out.println("Where would you like to move?");
                    input = scanner.nextLine();
                    remainingActions = pandemic.character.move(input);
                } else if (input.equalsIgnoreCase("direct flight")) {
                    System.out.println("");
                    System.out.println("Where would you like to fly to?");
                    input = scanner.nextLine();
                    remainingActions = pandemic.character.directFlight(input);
                } else if (input.equalsIgnoreCase("charter flight")) {
                    System.out.println("");
                    System.out.println("Where would you like to fly to?");
                    input = scanner.nextLine();
                    remainingActions = pandemic.character.charterFlight(input);
                } else if (input.equalsIgnoreCase("shuttle flight")) {
                    System.out.println("");
                    System.out.println("Where would you like to fly to?");
                    input = scanner.nextLine();
                    remainingActions = pandemic.character.shuttleFlight(input, pandemic.researchStations);
                } else if (input.equalsIgnoreCase("remove cubes")) {
                    System.out.println("");
                    remainingActions = pandemic.character.removeCube();
                } else if (input.equalsIgnoreCase("build research station")) {
                    System.out.println("");
                    if (Pandemic.playerHand.contains(currentLocation)) {
                        if (remainingActions > 0) {
                            pandemic.researchStations.add(currentLocation);
                            Pandemic.playerHand.remove(currentLocation);
                            remainingActions = pandemic.character.changeActions(-1);
                            System.out.println("You have successfully built a research station in " + displayCurrentLocation + ".");
                        } else {
                            System.out.println("You do not have enough actions.");
                        }
                    } else {
                        System.out.println("You do not have the player card for " + displayCurrentLocation + ".");
                    }
                } else if (input.equalsIgnoreCase("show research stations")) {
                    System.out.println("");
                    System.out.println("Research Station Locations:");
                    for (String location : pandemic.researchStations) {
                        System.out.println(location);
                    }
                } else if (input.equalsIgnoreCase("show cubes")) {
                    pandemic.printAllCubes();
                } else if (input.equalsIgnoreCase("show cures")) {
                    pandemic.printCures();
                } else if (input.equalsIgnoreCase("show hand")) {
                    pandemic.playerCards.printAllCards(playerHand);
                } else if (input.equalsIgnoreCase("cure disease")) {
                    if (pandemic.researchStations.contains(currentLocation)) {
                        remainingActions = pandemic.cureDisease();
                    } else {
                        System.out.println("There is no research station in " + displayCurrentLocation + ".");
                    }
                } else if (input.equalsIgnoreCase("help")) {
                    pandemic.help();
                } else if (input.equalsIgnoreCase("end turn")) {
                    remainingActions = 0;
                } else {
                    System.out.println("");
                    System.out.println("That's not a real command.");
                }
            }
            pandemic.betweenTurns();
        }
    }
}