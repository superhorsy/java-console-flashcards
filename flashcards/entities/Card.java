package flashcards.entities;

public class Card {
    protected String term;
    protected String definition;
    protected Integer mistakes = 0;

    public Card(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public Card(String term, String definition, Integer mistakes) {
        this(term, definition);
        this.mistakes = mistakes;
    }

    public static Card createFromString(String string) {
        String[] parts = string.split(":");
        return new Card(parts[0], parts[1], Integer.parseInt(parts[2]));
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Integer getMistakes() {
        return mistakes;
    }

    public void setMistakes(Integer mistakes) {
        this.mistakes = mistakes;
    }
}
