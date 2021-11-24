import java.io.*;
import java.util.*;

public class Cards {

    ArrayList<String> cards = new ArrayList<>();
    ArrayList<String> discardedCards = new ArrayList<>();
    Random randomNum = new Random();

    /**
     * Constructor for Cards.
     * Calls importCities and passes the empty
     * ArrayList<String> cards.
     */
    public Cards() {
        importCities(cards);
    }

    /**
     * Loads the file "Cities.tsv" and puts the values into cards.
     * @param cards ArrayList<String> to put the cards into.
     */
    public void importCities(ArrayList<String> cards) {
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
                cards.add(line);
            }
        }
        shuffle(cards);
    }

    /**
     * Shuffles the items in shuffleCards.
     * @param shuffleCards ArrayList<String> to be shuffled.
     */
    private void shuffle(ArrayList<String> shuffleCards) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < shuffleCards.size(); j++) {
                int randomIndex = randomNum.nextInt(shuffleCards.size());
                String temp = shuffleCards.get(j);
                shuffleCards.set(j, shuffleCards.get(randomIndex));
                shuffleCards.set(randomIndex, temp);
            }
        }
    }

    /**
     * Removes and returns the first item in cards.
     * Adds removed card to discardedCards.
     * If cards is empty, shuffle discardedCards and put them in cards,
     * then draws a card.
     * @return String the card that was drawn.
     */
    public String draw() {
        String drawnCard = null;
        try {
            drawnCard = cards.remove(0);
            discardedCards.add(drawnCard);
        } catch(Exception e) {
            shuffleAndReplace();
            drawnCard = cards.remove(0);
            discardedCards.add(drawnCard);
        }
        return drawnCard;
    }

    /**
     * Removes and returns the card at the end of cards.
     * Adds removed card to discardedCards.
     * If cards is empty, shuffle discardedCards and put them in cards,
     * then draws the last card.
     * @return String the card that was drawn.
     */
    public String drawEpidemic() {
        String drawnCard = null;
        try {
            drawnCard = cards.remove(cards.size() - 1);
            discardedCards.add(drawnCard);
        } catch(Exception e) {
            shuffleAndReplace();
            drawnCard = cards.remove(cards.size() - 1);
            discardedCards.add(drawnCard);
        }
        return drawnCard;
    }

    /**
     * Shuffles discarded cards and places them one by one
     * at the start of cards.
     */
    public void shuffleAndReplace() {
        shuffle(discardedCards);
        for (String card : discardedCards) {
            cards.add(0, card);
        }
        discardedCards.clear();
    }

    /**
     * Prints all the cards in cards.
     * @return String an empty String.
     */
    public String toString() {
        for (String string : cards) {
            System.out.println(string);
        }
        return "";
    }
}
