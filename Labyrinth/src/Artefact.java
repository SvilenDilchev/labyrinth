import java.util.*;

/**
 * This class is a secondary class of the "Labyrinth of Daedalus" application.
 * This  class creates the artefacts that the player collects in the labyrinth.
 *
 * @author Svilen Dilchev
 * @version 2022.12.01
 */
public class Artefact
{
    Random rand = new Random();
    private String name;
    private int weight;
    private boolean isCursed;
    private static List<String> possibleArtefacts = new LinkedList<>(Arrays.asList
            ("Cornucopia", "Pandora's Box", "Caduceus", "Golden Fleece", "Golden Apple", "Thyrsus", "Medusa's Head", "Eros's Bow"));

    /**
     * Create a random and unique artefact.
     */
    public Artefact()
    {
        name = possibleArtefacts.get(rand.nextInt(possibleArtefacts.size()));
        makeUnique();
        weight = 1 + rand.nextInt(10);
        isCursed = rand.nextBoolean();
    }

    /**
     * Makes the name of the artefact uniques by removing its name from the possible artefact names.
     */
    private void makeUnique() {possibleArtefacts.remove(name);}

    /**
     * Returns the name of the artefact as a string
     * @return the name of the artefact
     */
    public String getName() {return name;}

    /**
     * Returns the weight of the artefact as a whole number.
     * @return the weight of the artefact
     */
    public int getWeight() {return weight;}

    /**
     * Returns whether the artefact is cursed and consequently able to be picked up
     * @return true if the artefact is cursed, false if it isn't
     */
    public boolean isCursed() {return isCursed;}
}
