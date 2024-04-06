/**
 * This class is a secondary class of the "Labyrinth of Daedalus" application.
 * This  class creates the NPCs that the player encounters in the labyrinth.
 *
 * @author Svilen Dilchev
 * @version 2022.12.01
 */
public class NPC
{
    private String greeting;
    private String type;
    private Room currentRoom;

    /**
     * Constructor used to initialise the NPCs
     * @param greeting the text displayed to the player when the NPC is encountered
     * @param type the type of NPC (Monster, Fairy, Wizard)
     */
    public NPC(String greeting, String type){
        this.greeting = greeting;
        this.type = type;
    }

    /**
     * Returns the greeting of the NPC as a String
     * @return the greeting of the NPC
     */
    public String getGreeting(){return greeting;}

    /**
     * Returns the type of NPC as a String
     * @return the type of NPC
     */
    public String getType(){return type;}

    /**
     * Returns the current room where the NPC can be encountered so that when it moves it doesn't go to the same room
     * @return the current room of the NPC
     */
    public Room getCurrentRoom(){return currentRoom;}

    /**
     * Used to change the room of the NPC
     * @param currentRoom the new room of the NPC
     */
    public void setCurrentRoom(Room currentRoom){this.currentRoom = currentRoom;}
}
