package flashcards;

import flashcards.entities.Card;
import flashcards.entities.CardCollection;
import flashcards.entities.Console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {

    final private static CardCollection cards = new CardCollection();
    final private static Console console = new Console();
    private static String exportPath = "";

    public static void main(String[] args) throws IOException {
        checkArgs(args);
        printDialog();
        if (!exportPath.isEmpty()) {
            actionExport(exportPath);
        }
        console.say("Bye bye!");
    }

    private static void checkArgs(String[] args) throws IOException {
        if (args.length == 0) {
            return;
        }
        for (int i = 0; i <= args.length; i++) {
            if (args[i].startsWith("-")) {
                switch (args[i]) {
                    case "-import":
                        actionImport(args[i + 1]);
                        break;
                    case "-export":
                        exportPath = args[i + 1];
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * O(1)
     */
    private static void printDialog() throws IOException {
        String input;
        do {
            console.say("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            input = console.readLine().trim().toLowerCase();
            switch (input) {
                case "add":
                    actionAdd();
                    continue;
                case "remove":
                    actionRemove();
                    continue;
                case "import":
                    actionImport();
                    continue;
                case "export":
                    actionExport();
                    continue;
                case "ask":
                    actionAsk();
                    continue;
                case "log":
                    actionLog();
                    continue;
                case "hardest card":
                    actionHardestCard();
                    continue;
                case "reset stats":
                    actionResetStats();
            }
        } while (!input.equals("exit"));
    }

    public static void actionAdd() throws IOException {
        System.out.println("The card:");
        String term = console.readLine();
        if (cards.hasTerm(term)) {
            console.say("The card \"%s\" already exists.\n", term);
            return;
        }
        console.say("The definition of the card:");
        String definition = console.readLine();
        if (cards.hasDefinition(definition)) {
            console.say("The definition \"%s\" already exists.\n", definition);
            return;
        }
        cards.add(term, definition);
        console.say("The pair (\"%s\":\"%s\") has been added\n", term, definition);
    }

    public static void actionRemove() throws IOException {
        console.say("Which card?");
        String cardToRemove = console.readLine();
        if (!cards.hasTerm(cardToRemove)) {
            console.say("Can't remove \"%s\": there is no such card!\n", cardToRemove);
            return;
        }
        cards.remove(cardToRemove);
        console.say("Card has been removed");
    }

    public static void actionImport() throws IOException {
        console.say("File name:");
        String path = console.readLine();
        actionImport(path);
    }

    public static void actionImport(String path) throws IOException {
        int countImportedCards = 0;
        try {
            countImportedCards = cards.addFromFile(path);
        } catch (FileNotFoundException e) {
            console.say("File not found.");
            return;
        }
        console.say("%d cards have been loaded.\n", countImportedCards);
    }

    private static void actionExport() {
        console.say("File name:");
        String path = console.readLine();
        actionExport(path);
    }

    private static void actionExport(String path) {
        try {
            cards.dumpToFile(path);
        } catch (IOException e) {
            console.say("Can't write to file %s", path);
            return;
        }
        console.say("%d cards have been saved.", cards.getSize());
    }

    public static void actionAsk() throws IOException {
        console.say("How much cards do you want to check out?");

        int count = 0;

        try {
            count = Integer.parseInt(console.readLine());
        } catch (NumberFormatException $exception) {
            console.say("Please enter a number!");
            return;
        }

        Random generator = new Random();

        for (int i = 0; i < count; i++) {
            Integer randIndex = generator.nextInt(cards.getSize());
            Card card = cards.get(randIndex);

            console.say("Print the definition of \"%s\":\n", card.getTerm());
            String answer = console.readLine();
            if (answer.equals(card.getDefinition())) {
                console.say("Correct!");
            } else {
                card.setMistakes(card.getMistakes() + 1);
                if (cards.hasDefinition(answer)) {
                    Card correctAnswer = cards.findByDefinition(answer);
                    console.say("Wrong. The right answer is \"%s\", but your definition is correct for \"%s\".\n",
                            card.getDefinition(), correctAnswer.getTerm());
                } else {
                    console.say("Wrong. The right answer is \"%s\"\n", card.getDefinition());
                }
            }
        }
    }

    public static void actionLog() {
        console.say("File name:");
        String path = console.readLine();
        try {
            File file = new File(path);
            PrintWriter writer = new PrintWriter(file);
            for (String logString : console.getLog()) {
                writer.println(logString);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        console.say("The log has been saved");
    }

    public static void actionHardestCard() {
        ArrayList<Card> hardestCards = cards.getHardestCards();
        if (hardestCards.isEmpty()) {
            console.say("There are no cards with errors.");
            return;
        }
        int errors = hardestCards.get(0).getMistakes();
        List<String> termsList = hardestCards.stream().map(card -> "\"" + card.getTerm() + "\"").collect(Collectors.toList());
        String message = termsList.size() > 1 ? "The hardest cards are %s" : "The hardest card is %s";
        console.say(message + " with %d errors.", String.join(", ", termsList), errors);
    }

    public static void actionResetStats() {
        cards.clearStats();
        console.say("Card statistics have been reset.");
    }
}

