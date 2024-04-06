import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;

/**
 * Class Room - a room in an adventure game.
 * <p>
 * This class is part of the "World of Zuul" application.
 * "World of Zuul" is a very simple, text based adventure game.
 * <p>
 * A "Room" represents one location in the scenery of the game.  It is
 * connected to other rooms via exits.  For each existing exit, the room
 * stores a reference to the neighboring room.
 *
 * @author Michael Kölling and David J. Barnes
 * @version 2022.12.01
 */

public class Room
{
    private String description;
    private NPC npc;
    private List<Artefact> artefactList;
    private HashMap<String, Room> exits;        // stores exits of this room.

    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open courtyard".
     *
     * @param description The room's description.
     */
    public Room(String description)
    {
        this.description = description;
        artefactList = new ArrayList<>();
        npc = null;
        exits = new HashMap<>();
    }

    /**
     * Prints all available for pick up artefacts in the room + their weight.
     */
    public void printRoomArtefacts()
    {
        if (artefactList.size() == 0)
        {
            return;
        }
        System.out.println("The room you're in contains the following artefacts:");
        for (int i = 0; i < artefactList.size(); i++)
        {
            System.out.println("" + (i + 1) + ": " + artefactList.get(i).getName() + " | Weight - " + artefactList.get(i).getWeight());
        }
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     *
     * @return Details of the room's exits.
     */
    public String getExitString()
    {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for (String exit : keys)
        {
            returnString += " " + exit;
        }
        return returnString;
    }

    /*
     * Getters and Setters for the Room's fields
     */

    /**
     * Define an exit from this room.
     *
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) {exits.put(direction, neighbor);}

    /**
     * Removes the exit of a room given a specific direction.
     */
    public void removeExit(String direction) {exits.remove(direction);}

    /**
     * @param direction the direction of the exit
     * @return true if an exit exists in that direction
     */
    public boolean hasExit(String direction) {return exits.containsKey(direction);}

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription() {return "You are " + description;}

    /**
     * Return a description of the room in the form:
     * You are in the kitchen.
     * Exits: north west
     *
     * @return A long description of this room
     */
    public String getLongDescription()
    {
        return "You are " + description + ".\n" + getExitString();
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     *
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) {return exits.get(direction);}

    /**
     * Adds an artefact to the artefacts in the room
     * @param input the artefact to be added
     */
    public void addArtefact(Artefact input) {artefactList.add(input);}

    /**
     * @param index the place of the artefact to be returned
     * @return the artefact at the specific index
     */
    public Artefact getArtefact(int index) {return artefactList.get(index);}

    /**
     * Removes an artefact from the room when it is picked up by the player
     * @param art the artefact that is being removed
     */
    public void removeArtefact(Artefact art) {artefactList.remove(art);}

    /**
     * @return the amount of artefacts in the room
     */
    public int getArtefactCount() {return artefactList.size();}

    /**
     * Assigns an NPC to the room
     * @param input the NPC being assigned to the room
     */
    public void setNPC(NPC input){npc = input;}

    /**
     * @return the NPC that is assigned to the room
     */
    public NPC getNPC(){return npc;}

    /**
     * @return true if the room has an assigned NPC
     */
    public boolean hasNPC(){return (npc != null);}
}

