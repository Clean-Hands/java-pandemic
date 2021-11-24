import java.io.*;
import java.util.*;

public class PlayerCards extends Cards {

    private static HashMap<String, String> cityColors = new HashMap<>();
    ArrayList<String> cards = new ArrayList<>();
    PathFinder pathFinder;

    /**
     * Constructor for PlayerCards.
     * Calls importCities(), passing cards.
     * Calls importCityColors().
     * Takes the PathFinder object and assigns it
     * to the one in this instance.
     * @param pathFinder PathFinder object.
     */
    public PlayerCards(PathFinder pathFinder) {
        importCities(cards);
        importCityColors();
        this.pathFinder = pathFinder;
    }

    /**
     * Loads the file "CityColors.tsv" and puts values
     * into the cityColors HashMap.
     */
    private void importCityColors() {
        File inputFile = new File("CityColors.tsv");
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
                cityColors.put(splitline[0], splitline[1]);
            }
        }
    }

    /**
     * Adds numOfEpidemics epidemic cards into cards. Splits cards
     * into numOfEpidemics subsets and places one epidemic
     * card into each at a random position.
     * @param numOfEpidemics int the number of epidemic cards desired.
     */
    public void addEpidemics(int numOfEpidemics) {
        int cardsSubset = cards.size()/numOfEpidemics;
        for (int i = 0; i < numOfEpidemics; i++) {
            int randomIndex = randomNum.nextInt(cardsSubset) + (i * cardsSubset);
            cards.add(randomIndex, "Epidemic");
        }
    }

    /**
     * Draws a card and returns it.
     * If there are no more cards in cards, calls lose().
     * If the card is an epidemic card, it informs the user
     * and then makes an epidemic happen.
     * @return String the drawn card.
     */
    @Override
    public String draw() {
        if (cards.size() == 0)
            Pandemic.lose();
        String drawnCard = cards.remove(0);
        if (drawnCard.equals("Epidemic")) {
            Pandemic.infectionRateIndex++;
            String epidemicCard = drawEpidemic();
            System.out.println("EPIDEMIC AT: " + epidemicCard.replace("_", " "));
            pathFinder.changeCubes(epidemicCard, 3);
            shuffleAndReplace();
        }
        return drawnCard;
    }

    /**
     * Shows the player their hand, then allows them to choose a card to remove.
     * Loops until playerHand.size() is 7.
     * @param playerHand ArrayList<String> of the player's cards.
     * @return ArrayList<String> of the player's cards after the desired cards
     * have been removed.
     */
    public ArrayList<String> chooseCardToRemove(ArrayList<String> playerHand) {
        Scanner scanner = new Scanner(System.in);
        while (playerHand.size() > 7) {
            System.out.println("");
            System.out.println("Your hand has " + playerHand.size() + " cards in it.");
            System.out.println("The maximum hand size is 7.");
            System.out.println("Please choose a card to remove:");
            System.out.println("");
            printAllCards(playerHand);
            System.out.println("");
            String input = scanner.nextLine();
            input = input.replace(" ", "_");
            if (playerHand.contains(input)) {
                playerHand.remove(input);
            } else {
                System.out.println("That is not a valid input.");
            }
        }
        return playerHand;
    }

    /**
     * Returns the color of the passed city.
     * @param city String the city you would like the color of.
     * @return String the color of the passed city.
     */
    public static String getColor(String city) {
        return cityColors.get(city);
    }

    /**
     * Prints all cards in playerHand.
     * @param playerHand ArrayList<String> of the player's cards.
     */
    public void printAllCards(ArrayList<String> playerHand) {
        System.out.println("");
        System.out.println("Your cards:");
        for (String card : playerHand) {
            System.out.println(card.replace("_", " ") + " - " + cityColors.get(card));
        }
    }
}
