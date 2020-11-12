package flashcards.entities;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CardCollection {
    List<Card> cards = new ArrayList<>();

    public void dumpToFile(String filePath) throws IOException {
        File file = new File(filePath);
        file.createNewFile();
        printCardToFile(file);
    }

    private void printCardToFile(File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Card card : cards) {
                writer.println(String.join(":",
                        card.getTerm(), card.getDefinition(), String.valueOf(card.getMistakes())));
            }
        }
    }

    public Boolean hasTerm(String term) {
        for (Card card : cards) {
            if (card.getTerm().equals(term)) {
                return true;
            }
        }
        return false;
    }

    public Boolean hasDefinition(String definition) {
        for (Card card : cards) {
            if (card.getDefinition().equals(definition)) {
                return true;
            }
        }
        return false;
    }

    public void add(String term, String definition) {
        cards.add(new Card(term, definition));
    }

    public void remove(String cardToRemove) {
        Card card = findByTerm(cardToRemove);
        cards.remove(card);
    }

    public Card findByTerm(String term) {
        for (Card card : cards) {
            if (card.getTerm().equals(term)) {
                return card;
            }
        }
        return null;
    }

    public Card findByDefinition(String definition) {
        for (Card card : cards) {
            if (card.getDefinition().equals(definition)) {
                return card;
            }
        }
        return null;
    }

    public int addFromFile(String importPath) throws IOException {
        File file = new File(importPath);
        ArrayList<Card> cardsFromFile = readCardsFromFile(file);
        int countImported = 0;
        for (Card cardToImport : cardsFromFile) {
            if (hasTerm(cardToImport.getTerm())) {
                Card card = findByTerm(cardToImport.getTerm());
                if (card.getDefinition().equals(cardToImport.getDefinition())) {
                    continue;
                }
                card.setDefinition(cardToImport.getDefinition());
                card.setMistakes(cardToImport.getMistakes());
            } else {
                cards.add(cardToImport);
            }
            countImported++;
        }
        return countImported;
    }

    private ArrayList<Card> readCardsFromFile(File file) throws IOException {
        ArrayList<Card> cards = new ArrayList<>();
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                cards.add(Card.createFromString(reader.nextLine()));
            }
        }
        return cards;
    }

    public Integer getSize() {
        return cards.size();
    }

    public Card get(Integer randIndex) {
        return cards.get(randIndex);
    }

    public ArrayList<Card> getHardestCards() {
        int counter = 0;
        ArrayList<Card> hardestCards = new ArrayList<>();
        for (Card card : cards) {
            if (card.getMistakes() < counter || card.getMistakes() == 0) {
                continue;
            }
            if (card.getMistakes() > counter) {
                hardestCards.clear();
                counter = card.getMistakes();
            }
            hardestCards.add(card);
        }
        return hardestCards;
    }

    public void clearStats() {
        for (Card card : cards) {
            card.setMistakes(0);
        }
    }
}
