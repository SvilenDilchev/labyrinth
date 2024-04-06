import java.util.*;

/**
 * This class is the main class of the "Labyrinth of Daedalus" application.
 * "Labyrinth of Daedalus" is a very simple, text based adventure game.  Users
 * can walk around the labyrinth, fight monsters, collect artifacts, and die of exhaustion.
 * <p>
 * To play this game, create an instance of this class and call the "play"
 * method.
 * <p>
 * This main class creates and initialises all the others: it creates all
 * rooms, creates the parser and starts the game.  It also evaluates and
 * executes the commands that the parser returns.
 *
 * @author Michael Kölling, David J. Barnes, and Svilen Dilchev
 * @version 2022.12.01
 */

public class Game
{
    private Random rand = new Random();
    private Parser parser;
    private Room currentRoom;
    private Room prevRoom;
    private String threadNumber;
    private int vitality;
    private int staircaseLevel;
    private int endlessCorridorRoom;
    private int binaryRoom;
    private int stepCounter;
    private int riddleTipCounter;
    private int exhaustCoefficient;
    private int playerBackPackWeight;
    private int totalArtefactCount;
    private boolean victory;
    private boolean buttonPressed;
    private boolean isRiddleSolved;
    private List<String> staircaseMovement;
    private List<String> corridorMovement;
    private List<String> binaryMovement;
    private List<String> playerItems;
    private List<Room> labyrinthRooms;
    private List<Room> visitedRooms;
    private List<Artefact> backpack;
    private List<NPC> npcList;

    /**
     * Create the game and initialise its internal map.
     */
    public Game()
    {
        threadNumber = generateThreadNumber();
        vitality = 10;
        staircaseLevel = 0;
        endlessCorridorRoom = 0;
        binaryRoom = 0;
        stepCounter = 0;
        riddleTipCounter = 0;
        exhaustCoefficient = 5;
        playerBackPackWeight = 15;
        totalArtefactCount = 0;
        victory = false;
        buttonPressed = false;
        isRiddleSolved = false;

        staircaseMovement = new ArrayList<>();
        corridorMovement = new ArrayList<>();
        binaryMovement = new ArrayList<>();
        playerItems = new ArrayList<>();
        visitedRooms = new ArrayList<>();
        labyrinthRooms = new ArrayList<>();
        backpack = new ArrayList<>();
        npcList = new ArrayList<>();
        parser = new Parser();

        createRooms();
        generateNPCs(5);
        moveNPCs();
    }

    /**
     * Generates the number on Ariadna's Thread.
     * @return the number on Ariadna's Thread in String form
     */
    private String generateThreadNumber()
    {
        String threadNum = "";
        for (int i = 0; i < 6; i++)
        {
            threadNum += rand.nextInt(2);
        }
        return threadNum;
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        Room entrance, centre, puzzleRoom, spiralStaircase, pit, secretCorridor, treasury, mainHall, lionRoom, lavaPit, binaryCorridor, endlessCorridor, mysteryRoom;

        // create the rooms
        entrance = new Room("at the entrance to The Labyrinth of Daedalus.");
        mainHall = new Room("in the main hall of the labyrinth.");
        puzzleRoom = new Room("in what appears to be a puzzle room.");
        treasury = new Room("in the labyrinths treasury.");
        spiralStaircase = new Room("in a room with a spiral staircase in front of you. You can go up or down.");
        pit = new Room("in the pit.");
        secretCorridor = new Room("in the secret corridor shown by Ariadna's thread.");
        lionRoom = new Room("in a room full of hungry lions. The door closes behind you instantly.");
        lavaPit = new Room("in a room with a lava pit.");
        centre = new Room("at the centre of the labyrinth.");
        binaryCorridor = new Room("in a room with 2 doors. The one to the left is dark and dim and the one to the right has a bright light above it");
        endlessCorridor = new Room("in a very long corridor. You feel exhaustion coming upon you.");
        mysteryRoom = new Room("in a weird room with glowing light everywhere. There is a button on the opposite wall.");

        // initialise room exits
        entrance.setExit("north", mainHall);

        mainHall.setExit("south", entrance);
        mainHall.setExit("west", puzzleRoom);
        mainHall.setExit("north", endlessCorridor);
        mainHall.setExit("east", spiralStaircase);

        puzzleRoom.setExit("east", mainHall);

        treasury.setExit("east", puzzleRoom);

        spiralStaircase.setExit("west", mainHall);
        spiralStaircase.setExit("up", spiralStaircase);
        spiralStaircase.setExit("down", spiralStaircase);

        //the pit's exits do not exist yet

        secretCorridor.setExit("south", pit);
        secretCorridor.setExit("north", mainHall);

        binaryCorridor.setExit("south", mainHall);

        endlessCorridor.setExit("forward", endlessCorridor);
        endlessCorridor.setExit("backward", mainHall);

        //the centre doesn't have exits yet

        mysteryRoom.setExit("north", treasury);

        currentRoom = entrance;  // start game outside

        //Add rooms to list
        labyrinthRooms.add(entrance);           //index 0
        labyrinthRooms.add(mainHall);           //index 1
        labyrinthRooms.add(endlessCorridor);    //index 2
        labyrinthRooms.add(puzzleRoom);         //index 3
        labyrinthRooms.add(treasury);           //index 4
        labyrinthRooms.add(spiralStaircase);    //index 5
        labyrinthRooms.add(pit);                //index 6
        labyrinthRooms.add(secretCorridor);     //index 7
        labyrinthRooms.add(centre);             //index 8
        labyrinthRooms.add(binaryCorridor);     //index 9
        labyrinthRooms.add(lionRoom);           //index 10
        labyrinthRooms.add(lavaPit);            //index 11
        labyrinthRooms.add(mysteryRoom);        //index 12
    }

    /**
     * Create NPCs of 3 types.
     * @param count the amount of NPCs to be generated
     */
    private void generateNPCs(int count)
    {
        for (int i = 0; i < count; i++)
        {
            switch (rand.nextInt(3))
            {
                case 0 -> npcList.add(new NPC("I wanna kill ya! Wanna fight?", "Monster"));
                case 1 -> npcList.add(new NPC("I want to heal you! Do you accept my gift?", "Fairy"));
                case 2 -> npcList.add(new NPC("I wanna teleport ya! Do you accept?", "Wizard"));
                default ->
                {
                }
            }
        }

    }

    /**
     * Move the NPCs to the rooms they can be encountered in.
     */
    private void moveNPCs()
    {
        for (Room rm : labyrinthRooms)
        {
            rm.setNPC(null);
        }
        labyrinthRooms.get(8).setNPC(new NPC("This is to ignore guard clause.", "Boss"));

        for (NPC current : npcList)
        {
            int index = rand.nextInt(1, 8);
            Room chosen = labyrinthRooms.get(index);

            while (chosen.hasNPC() && current.getCurrentRoom() != null && current.getCurrentRoom().equals(chosen))
            {
                index = rand.nextInt(1, 8);
                chosen = labyrinthRooms.get(index);
            }
            current.setCurrentRoom(labyrinthRooms.get(index));
            chosen.setNPC(current);
        }
    }

    /**
     * Main play routine. Loops until end of play.
     */
    public void play()
    {
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.

        boolean finished = false;
        while (!finished)
        {
            Command command = parser.getCommand();
            finished = processCommand(command);

            if (command.isUnknown())
            {
                continue;
            }
            System.out.println();
            switch (labyrinthRooms.indexOf(currentRoom))
            {
                case 0:
                    finished = setEntrance();
                    break;
                case 1:
                    setMainHall(command);
                    break;
                case 2:
                    setEndlessCorridor();
                    break;
                case 3:
                    setPuzzleRoom(prevRoom, command);
                    break;
                case 4:
                    setTreasury(command);
                    break;
                case 5:
                    setSpiralStairCase();
                    break;
                case 6:
                    setPit(command);
                    break;
                case 7:
                    setSecretCorridor(command);
                    break;
                case 8:
                    setCentre(command);
                    break;
                case 9:
                    setBinaryCorridor(command);
                    break;
                case 10:
                    finished = setLionRoom();
                    break;
                case 11:
                    finished = setLavaPit();
                    break;
                case 12:
                    setMysteryRoom(command);
                default:
                    break;
            }

            // Check for death.
            if (vitality <= 0)
            {
                System.out.println("You run out of strength in the labyrinth and lose your life to exhaustion.");
                break;
            }
            //Check for NPC encounter
            if (currentRoom.hasNPC() && !currentRoom.equals(labyrinthRooms.get(8)))
            {
                System.out.println("\nYou encounter a " + currentRoom.getNPC().getType() + " in the room.");
                System.out.println(currentRoom.getNPC().getType() + ": " + currentRoom.getNPC().getGreeting());
            }
            //Display backpack contents and weight
            System.out.println("\n------------------------------");
            currentRoom.printRoomArtefacts();
            System.out.println("Remaining backpack weight - " + playerBackPackWeight);
            System.out.println("------------------------------\n");
            //Display exits unless the game has ended
            if (!(victory && currentRoom.equals(labyrinthRooms.get(0))) && !finished)
            {
                System.out.println(currentRoom.getExitString());
            }
        }
        System.out.println("Thank you for playing. Good bye.");
    }

    /**
     * Sets up the entrance and controls its interactions with the player's state and commands
     * @return true if the Minotaur has been defeated when the player exits the labyrinth, false if it hasn't
     */
    private boolean setEntrance()
    {
        if (victory)
        {
            System.out.println("Congratulations on winning the game!\n");
            System.out.println("You exit the Labyrinth with the following artefacts and equipment:");
            for (String item : playerItems)
            {
                System.out.println(item);
            }
            if (backpack.size() != 0)
            {
                for (Artefact item : backpack)
                {
                    System.out.println(item.getName());
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Sets up the main hall and controls its interactions with the player's state and commands
     * @param cmd the command used to make some messages to the player only be displayed upon entry into the room and artefacts generate only on entry
     */
    private void setMainHall(Command cmd)
    {
        if (playerItems.contains("Ariadna's Thread"))
        {
            System.out.println("The hallway before you doesn't look endless anymore. Instead there's a door at its end.");
            currentRoom.setExit("north", labyrinthRooms.get(9));
        } else
        {
            System.out.println("It appears endless but has 2 doors to the east and west.");
        }
        if (prevRoom.equals(labyrinthRooms.get(3)) && !playerItems.contains("Ariadna's Thread"))
        {
            System.out.println("You hear the room behind you change.");
            isRiddleSolved = false;
        } else if (prevRoom.equals(labyrinthRooms.get(7)))
        {
            System.out.println("You had fallen through a hole in the ceiling.");
            if (playerItems.contains("Hermes's Boots"))
            {
                System.out.println("You can use Hermes's Boots to fly back up through it.");
                currentRoom.setExit("up", labyrinthRooms.get(7));
            } else
            {
                System.out.println("It is too high to reach. You cannot currently go back to the secret corridor through there.");
            }
        }
        if (cmd.getCommandWord().equals("go") && prevRoom.hasExit(cmd.getSecondWord()))
        {
            generateArtefact();
        }
    }

    /**
     * Sets up the endless corridor and controls its interactions with the player's state and commands
     */
    private void setEndlessCorridor()
    {
        System.out.println("The hallway seems endless");
        if (prevRoom.equals(labyrinthRooms.get(1)))
        {
            endlessCorridorRoom = 0;
        }

        if (endlessCorridorRoom == 0)
        {
            if (currentRoom.hasExit("backward"))
            {
                currentRoom.removeExit("backward");
            }
            currentRoom.setExit("south", labyrinthRooms.get(1));
        } else
        {
            if (currentRoom.hasExit("south"))
            {
                currentRoom.removeExit("south");
            }
            currentRoom.setExit("backward", currentRoom);
        }

        if (endlessCorridorRoom == 15 && !playerItems.contains("Hermes's Boots"))
        {
            System.out.println("You find Hermes's Boots in front of you.");
        }
    }

    /**
     * Sets up the puzzle room and controls its interactions with the player's state and commands
     * @param cmd the command used to make some messages to the player only be displayed upon entry into the room and artefacts generate only on entry
     * @param prevRoom used to determine which entry message should be displayed to the player
     */
    private void setPuzzleRoom(Room prevRoom, Command cmd)
    {
        if (playerItems.contains("Ariadna's Thread"))
        {
            System.out.println("The door at the other end of the room seems open.");
            return;
        }

        if (currentRoom.equals(labyrinthRooms.get(3)) && cmd.getCommandWord().equals("go") && prevRoom.hasExit(cmd.getSecondWord()))
        {
            System.out.println("The puzzle prompt reads: What only gets larger the more you take away from it?");
        }
        if (prevRoom.equals(labyrinthRooms.get(4)) && cmd.getCommandWord().equals("go"))
        {
            System.out.println("You see the puzzle room rearrange itself before your eyes into its original form. The door you came through has disappeared.");
            isRiddleSolved = false;
        }
        if (cmd.getCommandWord().equals("go") && prevRoom.hasExit(cmd.getSecondWord()))
        {
            generateArtefact();
        }
    }

    /**
     * Sets up the treasury and controls its interactions with the player's state and commands
     * @param cmd the command used to make some messages to the player only be displayed upon entry into the room
     */
    private void setTreasury(Command cmd)
    {
        if (!cmd.getCommandWord().equals("go"))
        {
            return;
        }

        if (!playerItems.contains("Ariadna's Thread"))
        {
            System.out.println("You find yourself looking at Ariadna's thread.");
        } else
        {
            System.out.println("The treasury seems to be empty.");
            if (prevRoom.equals(labyrinthRooms.get(3)) && prevRoom.hasExit(cmd.getSecondWord()))
            {
                System.out.println("Upon reentering the labyrinth's treasury with Ariadna's Thread in hand you find that a door has appeared to the south.");
                currentRoom.setExit("south", labyrinthRooms.get(12));
            }
        }
    }

    /**
     * Sets up the spiral staircase and controls its interactions with the player's state and commands
     */
    private void setSpiralStairCase()
    {
        currentRoom.removeExit("west");
        if (currentRoom.hasExit("north"))
        {
            currentRoom.removeExit("north");
        }

        if (prevRoom.equals(labyrinthRooms.get(1)))
        {
            staircaseLevel = 0;
        } else if (prevRoom.equals(labyrinthRooms.get(6)))
        {
            staircaseLevel = -5;
        } else if (prevRoom.equals(labyrinthRooms.get(8)))
        {
            staircaseLevel = 5;
        }

        switch (staircaseLevel)
        {
            case 0:
                if (!currentRoom.hasExit("west"))
                {
                    currentRoom.setExit("west", labyrinthRooms.get(1));
                }
                if (!currentRoom.hasExit("up"))
                {
                    currentRoom.setExit("up", currentRoom);
                }
                if (!currentRoom.hasExit("down"))
                {
                    currentRoom.setExit("down", currentRoom);
                }
                break;
            case 3:
                if (!playerItems.contains("The Harpe"))
                {
                    System.out.println("You see The Harpe, Perseus's weapon, in front of you.");
                }
                break;
            case 5:
                if (playerItems.contains("Ariadna's Thread"))
                {
                    System.out.println("You feel Ariadna's Thread vibrating. It outlines a hatch on the ceiling of the room.");
                    if (!victory)
                    {
                        System.out.println("It is locked.");
                        currentRoom.removeExit("up");
                    } else
                    {
                        System.out.println("It leads to the centre of the Labyrinth.");
                        currentRoom.setExit("up", labyrinthRooms.get(8));
                    }
                    if (!currentRoom.hasExit("down"))
                    {
                        currentRoom.setExit("down", currentRoom);
                    }
                }
                break;
            case -5:
                System.out.println("You find yourself in front of a pit to the north of you. Jump down?");
                currentRoom.setExit("north", labyrinthRooms.get(6));
                currentRoom.removeExit("down");
                break;
            default:
                if (!currentRoom.hasExit("up"))
                {
                    currentRoom.setExit("up", currentRoom);
                }
                if (!currentRoom.hasExit("down"))
                {
                    currentRoom.setExit("down", currentRoom);
                }
                break;
        }

    }

    /**
     * Sets up the pit and controls its interactions with the player's state and commands
     * @param cmd the command used to make some messages to the player only be displayed upon entry into the room and artefacts generate only on entry
     */
    private void setPit(Command cmd)
    {
        if (!playerItems.contains("Ariadna's Thread") && !playerItems.contains("Hermes's Boots"))
        {
            System.out.println("You have no escape from the pit you jumped into.");
            System.out.println("You slowly feel your energy run out.");
            vitality = 0;
            return;
        }
        if (playerItems.contains("Ariadna's Thread") && cmd.getCommandWord().equals("go") && prevRoom.hasExit(cmd.getSecondWord()))
        {
            System.out.println("You feel Ariadna's Thread getting out of your bag and outlining a door.");
            System.out.println("The labyrinth shifts and reveals the pathway to the north.");
            currentRoom.setExit("north", labyrinthRooms.get(7));
        }

        if (playerItems.contains("Hermes's Boots") && cmd.getCommandWord().equals("go") && prevRoom.hasExit(cmd.getSecondWord()))
        {
            System.out.println("\nYou can use Hermes's Boots to fly back up the pit.");
            currentRoom.setExit("up", labyrinthRooms.get(5));
        }
        if (cmd.getCommandWord().equals("go") && prevRoom.hasExit(cmd.getSecondWord()))
        {
            generateArtefact();
        }

    }

    /**
     * Sets up the secret corridor and controls its interactions with the player's state and commands
     * @param cmd the command used to make it so artefacts generate only on entry
     */
    private void setSecretCorridor(Command cmd)
    {
        if (!playerItems.contains("Zeus's Aegis"))
        {
            System.out.println("Before you lays Zeus's Aegis.");
        }
        if (cmd.getCommandWord().equals("go") && prevRoom.hasExit(cmd.getSecondWord()))
        {
            generateArtefact();
        }
    }

    /**
     * Sets up the centre and controls its interactions with the player's state and commands
     */
    private void setCentre(Command cmd)
    {
        if (!victory && cmd.getCommandWord().equals("go") && prevRoom.hasExit(cmd.getSecondWord()))
        {
            System.out.println("You face off against the great Minotaur of the Labyrinth.");
            System.out.println("The door behind you closes.");
            System.out.println("You have to choose to fight the Minotaur directly or run until it gets exhausted and you can easily take it down.");
        }
    }

    /**
     * Sets up the binary corridor and controls its interactions with the player's state and commands
     * @param cmd the command used to limit entry into the corridor
     */
    private void setBinaryCorridor(Command cmd)
    {
        if (!(cmd.getCommandWord().equals("go") || cmd.getCommandWord().equals("back")))
        {
            return;
        }

        if (prevRoom.equals(labyrinthRooms.get(1)))
        {
            binaryRoom = 0;
        } else if (prevRoom.equals(labyrinthRooms.get(8)))
        {
            binaryRoom = 5;
        }

        if (binaryRoom != 0)
        {
            currentRoom.setExit("backward", currentRoom);
            currentRoom.removeExit("south");
        } else
        {
            currentRoom.setExit("south", labyrinthRooms.get(1));
            currentRoom.removeExit("backward");
        }

        Room correctForward;
        if (binaryRoom == 5)
        {
            correctForward = labyrinthRooms.get(8);
        } else
        {
            correctForward = currentRoom;
        }

        int deathIndex = 10 + rand.nextInt(2);
        if (threadNumber.charAt(binaryRoom) == '0')
        {
            currentRoom.setExit("left", correctForward);
            currentRoom.setExit("right", labyrinthRooms.get(deathIndex));
        } else
        {
            currentRoom.setExit("right", correctForward);
            currentRoom.setExit("left", labyrinthRooms.get(deathIndex));
        }
    }

    /**
     * Sets up the lion room and controls its interactions with the player's state and commands
     * @return true if the player dies in the room, false if he survives
     */
    private boolean setLionRoom()
    {
        int escapeCount = 0;
        if (playerItems.contains("The Harpe") && playerItems.contains("Zeus's Aegis"))
        {
            escapeCount++;
        }

        if (escapeCount > 0)
        {
            switch (rand.nextInt(escapeCount + 1))
            {
                case 0 ->
                {
                    System.out.println("You don't react on time and get eaten by the lions. Game Over!");
                    return true;
                }
                case 1 ->
                {
                    System.out.println("You pull out the Harpe and the Aegis and kill the Lions.");
                    System.out.println("The door opens up from behind you and you leave.");
                    back();
                    return false;
                }
                default ->
                {
                }
            }

        } else
        {
            System.out.println("You cannot fight off the lions. Game Over!");
            return true;
        }
        return false;
    }

    /**
     * Sets up the lava pit and controls its interactions with the player's state and commands
     * @return true if the player dies in the room, false if he survives
     */
    private boolean setLavaPit()
    {
        int escapeCount = 0;
        if (playerItems.contains("Hermes's Boots"))
        {
            escapeCount++;
        }

        if (escapeCount > 0)
        {
            switch (rand.nextInt(escapeCount + 1))
            {
                case 0 ->
                {
                    System.out.println("You don't react on time and fall into the pit of lava. Game Over!");
                    return true;
                }
                case 1 ->
                {
                    System.out.println("You fly away with Hermes's boots before falling in the pit.");
                    System.out.println("You leave the room.");
                    back();
                    return false;
                }
                default ->
                {
                }
            }
        } else
        {
            System.out.println("You don't react on time and fall in the pit of lava. Game Over!");
            return true;
        }
        return false;
    }

    /**
     * Sets up the mystery room and controls its interactions with the player's state and commands
     * @param cmd command used determine when to generate the room
     */
    private void setMysteryRoom(Command cmd)
    {
        if (prevRoom.equals(labyrinthRooms.get(4)) && cmd.getCommandWord().equals("go"))
        {
            currentRoom.setExit("north", labyrinthRooms.get(4));
            buttonPressed = false;
        }
    }

    /**
     * Randomly generates an artefact in a room when the player enters.
     */
    private void generateArtefact()
    {
        if (totalArtefactCount >= 8)
        {
            return;
        }
        if (rand.nextInt(2) == 1)
        {
            Artefact artefact = new Artefact();
            currentRoom.addArtefact(artefact);
            totalArtefactCount++;
        }
    }

    /**
     * Deal damage to the player based on how many steps they've taken.
     */
    private void takeExhaustionDamage()
    {
        if (stepCounter % exhaustCoefficient == 0)
        {
            vitality -= 1;
            System.out.println();
            System.out.println("You've grown weaker from exhaustion while exploring.");
            System.out.println("Vitality - " + vitality);
        }
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the Daedalus's Labyrinth!");
        System.out.println("It's very fun to play... Not really.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     *
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command)
    {
        boolean wantToQuit = false;

        if (command.isUnknown())
        {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();

        switch (commandWord)
        {
            case "help" -> printHelp();
            case "go" -> goRoom(command);
            case "quit" -> wantToQuit = quit(command);
            case "answer" -> answer(command);
            case "take" -> take();
            case "back" -> back();
            case "fight" -> wantToQuit = fight(command);
            case "run" -> wantToQuit = run(command);
            case "loot" -> loot(command);
            case "press" -> press();
            default ->
            {
            }
        }

        // else command not recognised.
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the
     * command words.
     */
    private void printHelp()
    {
        System.out.println("You are in Daedalus's Labyrinth");
        System.out.println("You need to defeat the Minotaur at the centre and then escape.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /**
     * Try to in to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command)
    {
        if (bossFightCheck())
        {
            return;
        }
        if (currentRoom.hasNPC() && currentRoom.getNPC().getType().equals("Monster"))
        {
            System.out.println("The Monster blocks you from just calmly leaving the room!");
            return;
        }
        if (!command.hasSecondWord())
        {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }


        prevRoom = currentRoom;
        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);


        if (nextRoom == null)
        {
            System.out.println("There is no door!");
            return;
        }

        checkChangeStaircaseFloor(command, nextRoom);
        checkChangeEndlessCorridorRoom(command, nextRoom);
        checkChangeBinaryRoom(command, nextRoom);
        currentRoom = nextRoom;
        System.out.println(currentRoom.getShortDescription());
        visitedRooms.add(prevRoom);
        stepCounter++;
        moveNPCs();
        if (prevRoom.equals(labyrinthRooms.get(12)) && buttonPressed)
        {
            System.out.println("The doorway you passed through disappears behind you in a flash of light. You cannot go back through there.");
        }
        takeExhaustionDamage();
    }

    /**
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     *
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command)
    {
        if (command.hasSecondWord())
        {
            System.out.println("Quit what?");
            return false;
        } else
        {
            return true;  // signal that we want to quit
        }
    }

    /**
     * Used to answer question that are asked to the player
     * @param command used to determine who and what is being answered
     */
    private void answer(Command command)
    {
        if (bossFightCheck())
        {
            return;
        }
        if (!command.hasSecondWord())
        {
            System.out.println("Answer something...");
            return;
        }
        boolean canAnswer = false;
        if (currentRoom.equals(labyrinthRooms.get(3)) && !playerItems.contains("Ariadna's Thread"))
        {
            riddleTipCounter++;
            if (isRiddleSolved)
            {
                return;
            }
            if (command.getSecondWord().equals("hole"))
            {
                currentRoom.setExit("west", labyrinthRooms.get(4));
                System.out.println("You see the labyrinth twist and rearrange itself in front of you. A door opens up to the west.");
                isRiddleSolved = true;
            } else
            {
                System.out.println("You hear a voice come from every corner of the room:\nWrong Answer!\nMake sure to answer in 1 word.");
                if (riddleTipCounter == 3)
                {
                    System.out.println("Hint: You can dig one with a shovel.");
                } else if (riddleTipCounter < 3)
                {
                    System.out.println("A hint will be given in " + (3 - riddleTipCounter) + " tries.");
                }
                System.out.println("You wait and think until you're ready to guess again. (Step count increased by 1)");
                stepCounter++;
            }
            canAnswer = true;
        }
        if (currentRoom.hasNPC())
        {
            switch (currentRoom.getNPC().getType())
            {
                case "Monster":
                    if (command.getSecondWord().equals("no") && playerItems.contains("The Harpe"))
                    {
                        System.out.println("The monster gets intimidated and leaves. You gain 1 vitality");
                        vitality++;
                        currentRoom.setNPC(null);
                    } else if (command.getSecondWord().equals("no") && !playerItems.contains("The Harpe"))
                    {
                        System.out.println("The monster doesn't care about your answer, attacks you, then leaves. You lose 2 vitality.");
                        vitality -= 2;
                        currentRoom.setNPC(null);
                    } else if (command.getSecondWord().equals("yes"))
                    {
                        System.out.println("You fight the monster.");
                        if (playerItems.contains("The Harpe") || playerItems.contains("Zeus's Aegis"))
                        {
                            System.out.println("You kill the monster and gain 1 vitality.");
                            vitality++;
                            currentRoom.setNPC(null);
                        } else
                        {
                            System.out.println("You lose the fight but play dead. The monster is tricked and leaves. You lose 3 vitality.");
                            vitality -= 3;
                            currentRoom.setNPC(null);
                        }
                    } else if (!(command.getSecondWord().equals("yes") || command.getSecondWord().equals("no")))
                    {
                        System.out.println("Answer the monster. It's a simple yes or no question.");
                    }
                    break;
                case "Fairy":
                    switch (command.getSecondWord())
                    {
                        case "yes" ->
                        {
                            System.out.println("The fairy gives you some Ambrosia. You eat it and instantly feel healthier. (Gain 1 vitality)");
                            System.out.println("Joyful the fairy leaves");
                            currentRoom.setNPC(null);
                            vitality++;
                        }
                        case "no" ->
                        {
                            System.out.println("The fairy seems sad. Flying slowly away, it leaves the room.");
                            currentRoom.setNPC(null);
                        }
                        default -> System.out.println("Answer the fairy... it's a simple yes or no question.");
                    }
                    break;
                case "Wizard":
                    switch (command.getSecondWord())
                    {
                        case "yes" ->
                        {
                            System.out.println("The wizard teleports you to the entrance of the Maze and gives you an amulet than you can use to teleport back.");
                            prevRoom = currentRoom;
                            currentRoom = labyrinthRooms.get(0);
                            visitedRooms.add(prevRoom);
                            stepCounter++;
                            currentRoom.setNPC(null);
                        }
                        case "no" ->
                        {
                            System.out.println("The wizard curses at you and teleports himself out of the room.");
                            currentRoom.setNPC(null);
                        }
                        default -> System.out.println("Answer the wizard. It's a simple yes or no question.");
                    }
                    break;
                default:
                    break;
            }
            canAnswer = true;
        }
        if (!canAnswer)
        {
            System.out.println("Answer what? No question was asked.");
        }
    }

    /**
     * Used to pick up important quest items / equipment that is used to win the game.
     */
    private void take()
    {
        if (bossFightCheck())
        {
            return;
        }
        if (currentRoom.equals(labyrinthRooms.get(4)) && !playerItems.contains("Ariadna's Thread"))
        {
            playerItems.add("Ariadna's Thread");
            System.out.println("You have successfully picked up Ariadna's Thread. Now you can navigate the labyrinth more easily.");
            System.out.println("The thread weaves itself and you can make out the number " + threadNumber + " forming from it.");
        } else if (currentRoom.equals(labyrinthRooms.get(5)) && staircaseLevel == 3 && !playerItems.contains("The Harpe"))
        {
            playerItems.add("The Harpe");
            System.out.println("You pick up the legendary sword responsible for killing Medusa.");
        } else if (currentRoom.equals(labyrinthRooms.get(7)) && !playerItems.contains("Zeus's Aegis"))
        {
            playerItems.add("Zeus's Aegis");
            System.out.println("You pick up the shield of the King of Olympus. Shield bashes have a chance to stun the enemy.");
        } else if (currentRoom.equals(labyrinthRooms.get(2)) && endlessCorridorRoom == 15 && !playerItems.contains("Hermes's Boots"))
        {
            playerItems.add("Hermes's Boots");
            exhaustCoefficient = 8;
            stepCounter = 0;
            System.out.println("You pick up and put on Hermes's Boots. They allow you to take flight. You will get exhausted less often.");
        } else
        {
            System.out.println("What are you trying to take? There's nothing there.");
        }
    }

    /**
     * Used to return to a previous room.
     * Only works if the player can manually return through the way he entered.
     */
    private void back()
    {
        if (bossFightCheck())
        {
            return;
        }

        if (currentRoom.hasNPC() && currentRoom.getNPC().getType().equals("Monster"))
        {
            System.out.println("The Monster blocks you from just calmly leaving the room!");
            return;
        }
        if (visitedRooms.size() == 0)
        {
            System.out.println("Back where? You're at the start.");
            return;
        }

        Room returnRoom = visitedRooms.get(visitedRooms.size() - 1);

        if (returnRoom.equals(labyrinthRooms.get(12)) && !currentRoom.equals(labyrinthRooms.get(4)))
        {
            System.out.println("You cannot go back to the mystery room.");
            return;
        }
        if (currentRoom.equals(labyrinthRooms.get(6)) && returnRoom.equals(labyrinthRooms.get(5)))
        {
            if (playerItems.contains("Hermes's Boots"))
            {
                System.out.println("You take flight with Hermes's Boots.");
            } else
            {
                System.out.println("You cannot exit the pit by going back.");
                return;
            }
        }
        if (currentRoom.equals(labyrinthRooms.get(1)) && returnRoom.equals(labyrinthRooms.get(7)))
        {
            if (playerItems.contains("Hermes's Boots"))
            {
                System.out.println("You take flight with Hermes's Boots.");
            } else
            {
                System.out.println("You cannot reach the hatch and go back to the secret corridor.");
                return;
            }
        }

        if (currentRoom.equals(labyrinthRooms.get(5)) && staircaseMovement.size() != 0)
        {
            if (staircaseMovement.get(staircaseMovement.size() - 1).equals("up"))
            {
                staircaseLevel--;
            } else
            {
                staircaseLevel++;
            }
            staircaseMovement.remove(staircaseMovement.size() - 1);
        }

        if (currentRoom.equals(labyrinthRooms.get(2)) && corridorMovement.size() != 0)
        {
            if (corridorMovement.get(corridorMovement.size() - 1).equals("forward"))
            {
                endlessCorridorRoom--;
            } else
            {
                endlessCorridorRoom++;
            }
            corridorMovement.remove(corridorMovement.size() - 1);
        }

        if (currentRoom.equals(labyrinthRooms.get(9)) && binaryMovement.size() != 0)
        {
            if (binaryMovement.get(binaryMovement.size() - 1).equals("forward"))
            {
                binaryRoom--;
            } else
            {
                binaryRoom++;
            }
            binaryMovement.remove(binaryMovement.size() - 1);
        }

        currentRoom = returnRoom;
        System.out.println(currentRoom.getShortDescription());
        visitedRooms.remove(visitedRooms.size() - 1);
        stepCounter++;
        moveNPCs();
        takeExhaustionDamage();
    }

    /**
     * Command used to fight Monsters and the Minotaur
     * @param command used to determine what exactly is being fought
     * @return true if the player dies, false if he survives
     */
    private boolean fight(Command command)
    {
        if (!currentRoom.hasNPC() || (currentRoom.equals(labyrinthRooms.get(8)) && victory))
        {
            System.out.println("There's no one to fight here.");
            return false;
        }
        if (!command.hasSecondWord())
        {
            System.out.println("Fight who?");
            return false;
        }
        if (!currentRoom.equals(labyrinthRooms.get(8)))
        {
            switch (command.getSecondWord())
            {
                case "Monster" ->
                {
                    System.out.println("You surprise the monster by directly engaging in combat instead of answering the question.");
                    if (playerItems.contains("Hermes's Boots") || playerItems.contains("The Harpe") || playerItems.contains("Zeus's Aegis"))
                    {
                        System.out.println("You defeat the monster and gain 1 vitality for it.");
                        vitality++;
                        currentRoom.setNPC(null);
                    } else
                    {
                        System.out.println("However, you are not well prepared. The monster blocks your attack and strikes back.");
                        System.out.println("A boulder comes flying out of nowhere and hits the Monster, killing it.");
                        System.out.println("You survived by getting lucky. (Lose 1 vitality)");
                        vitality--;
                        currentRoom.setNPC(null);
                    }
                }
                case "Fairy" ->
                {
                    System.out.println("You lunge and the fairy. It dodges and disappears instantly.");
                    currentRoom.setNPC(null);
                }
                case "Wizard" ->
                {
                    System.out.println("You attack the wizard. He dodges, strikes you with his staff, and teleports away. (Lose 1 vitality)");
                    vitality--;
                    currentRoom.setNPC(null);
                }
                default -> System.out.println("Fight what? (Capitalise first letter)");
            }
            return false;
        }
        if (command.getSecondWord().equals("Minotaur"))
        {
            if (!playerItems.contains("The Harpe") && !playerItems.contains("Zeus's Aegis") && vitality >= 9)
            {
                System.out.println("You successfully defeat the Minotaur in hand-to-hand combat!");
            } else if (!playerItems.contains("The Harpe") && playerItems.contains("Zeus's Aegis") && vitality >= 7)
            {
                System.out.println("You successfully defeat the Minotaur using your shield.");
            } else if (playerItems.contains("The Harpe") && !playerItems.contains("Zeus's Aegis") && vitality >= 5)
            {
                System.out.println("You successfully defeat the Minotaur using your legendary sword.");
            } else if (playerItems.contains("The Harpe") && playerItems.contains("Zeus's Aegis") && vitality >= 2)
            {
                System.out.println("You successfully defeat the Minotaur using your combat arms.");
            } else
            {
                System.out.println("You fall to the Minotaur and lose your life");
                System.out.println("Game Over!");
                return true;
            }
            defeatMinotaur();
        } else
        {
            System.out.println("You can only fight the Minotaur here.");
        }

        return false;
    }

    /**
     * Used in combat with Monsters and the Minotaur
     * @param command used to determine who is being ran around/away from/
     * @return true if the player dies, false if he survives
     */
    private boolean run(Command command)
    {
        if (!currentRoom.hasNPC() || (currentRoom.equals(labyrinthRooms.get(8)) && victory))
        {
            System.out.println("Who are you trying to run from?");
            return false;
        }
        if (!command.hasSecondWord())
        {
            System.out.println("Run from whom?");
            return false;
        }
        if (!currentRoom.equals(labyrinthRooms.get(8)))
        {
            if (command.getSecondWord().equals("Monster"))
            {
                if (playerItems.contains("Hermes's Boots"))
                {
                    System.out.println("You start running around. The monster cannot catch up to you. It trips, falls and breaks it's neck. (Gain 1 vitality)");
                    vitality++;
                    currentRoom.setNPC(null);
                } else
                {
                    System.out.println("The monster catches you and strikes at you. You fall down and lose 2 vitality");
                    System.out.println("You decide to play dead. The monster gets tricked and leaves.");
                    vitality -= 2;
                    currentRoom.setNPC(null);
                }
            } else
            {
                System.out.println("Why are you running around? (Capitalise first letter)");

            }
            return false;
        }
        if (command.getSecondWord().equals("Minotaur"))
        {
            if (!playerItems.contains("Hermes's Boots") && !playerItems.contains("Zeus's Aegis") && vitality >= 9)
            {
                System.out.println("You successfully outrun the Minotaur and finish it off when it is tired.");
            } else if (!playerItems.contains("Hermes's Boots") && playerItems.contains("Zeus's Aegis") && vitality >= 7)
            {
                System.out.println("You successfully defeat the Minotaur by running laps around it and stunning it with your shield");
            } else if (playerItems.contains("Hermes's Boots") && !playerItems.contains("Zeus's Aegis") && vitality >= 4)
            {
                System.out.println("You successfully exhaust the Minotaur by flying around with Hermes's Boots and finish him off with ease when he falls down.");
            } else if (playerItems.contains("Hermes's Boots") && playerItems.contains("Zeus's Aegis") && vitality >= 1)
            {
                System.out.println("You fly around the Minotaur and bash him with your shield. The great beast stands no chance against you.");
            } else
            {
                System.out.println("The Minotaur catches up to you and fells you.");
                System.out.println("Game Over!");
                return true;
            }
            defeatMinotaur();
        } else
        {
            System.out.println("You can only run from the Minotaur here.");
        }

        return false;
    }

    /**
     * Used by the player to loot artefacts from the labyrinth.
     * Also, can be used as a form of healing if the player tries to loot food
     * @param command used to determine what is being looted
     */
    private void loot(Command command)
    {
        if (bossFightCheck())
        {
            return;
        }
        if (!command.hasSecondWord())
        {
            System.out.println("Loot what?");
            return;
        }
        if (command.getSecondWord().equals("food"))
        {
            if (rand.nextInt(3) == 2)
            {
                System.out.println("You find an old moldy sandwich on the floor. You eat it. You gain 1 vitality point.");
                vitality++;
            } else
            {
                System.out.println("You find nothing but dust and echoes. You take a bit to reflect on your misery (step counter increased).");
                stepCounter++;
            }
        } else if (command.getSecondWord().equals("artefact"))
        {
            if (currentRoom.getArtefactCount() == 0)
            {
                System.out.println("There are no artefacts in this room.");
                return;
            }
            if (!command.hasThirdWord())
            {
                System.out.println("Loot what artefact?");
                return;
            }
            int artefactIndex;
            try
            {
                artefactIndex = Integer.parseInt(command.getThirdWord()) - 1;
            } catch (Exception e)
            {
                System.out.println("Which artefact? (Use a number)");
                return;
            }
            if (artefactIndex >= currentRoom.getArtefactCount())
            {
                System.out.println("There aren't that many artefacts in this room.");
                return;
            }
            if (artefactIndex < 0)
            {
                System.out.println("Try a positive number.");
                return;
            }
            Artefact chosen = currentRoom.getArtefact(artefactIndex);
            if (chosen == null)
            {
                System.out.println("That's not a valid artefact.");
                return;
            }
            if (chosen.isCursed())
            {
                System.out.println("You cannot pick up a cursed artefact.");
                return;
            }
            if (playerBackPackWeight - chosen.getWeight() < 0)
            {
                System.out.println("This artefact is too heavy for you to pick up.");
                return;
            }

            backpack.add(chosen);
            currentRoom.removeArtefact(chosen);
            playerBackPackWeight -= chosen.getWeight();
            System.out.println("You have successfully picked up the " + chosen.getName() + ". It has been added to your backpack.\n");
        } else
        {
            System.out.println("Loot what?");
        }
    }

    /**
     * Used to chance the exit of the mystery room from the treasury to a random location in the labyrinth
     */
    private void press()
    {
        if (bossFightCheck())
        {
            return;
        }
        if (!currentRoom.equals(labyrinthRooms.get(12)))
        {
            System.out.println("There's nothing to press.");
            return;
        }
        System.out.println("You can hear the who labyrinth shift and change. You do not know where you will emerge if you exit the mystery room.");
        int index = rand.nextInt(12);
        currentRoom.setExit("north", labyrinthRooms.get(index));
        staircaseLevel = rand.nextInt(11) - 5;
        binaryRoom = rand.nextInt(6);
        endlessCorridorRoom = rand.nextInt(20);
        buttonPressed = true;
    }

    /**
     * Used to limit access to certain commands during the Minotaur fight
     * @return true if the player is facing the Minotaur, false if he isn't
     */
    private boolean bossFightCheck()
    {
        if (currentRoom.equals(labyrinthRooms.get(8)) && !victory)
        {
            System.out.println("You cannot do that now");
            return true;
        }
        return false;
    }

    /**
     * Used to display information about the player's victory over the Minotaur and how to end the game.
     */
    private void defeatMinotaur()
    {
        System.out.println("The door you came from opens up.");
        System.out.println("A hatch also opens at the centre of the arena.\nYour mission has been accomplished and all that's left is to leave the Labyrinth.");
        currentRoom.setExit("down", labyrinthRooms.get(5));
        currentRoom.setExit("backward", labyrinthRooms.get(9));
        victory = true;
    }

    /**
     * Used to track movement through the repeating rooms in the endless corridor for the back command to utilise
     * @param command used to determine which way the player is moving
     * @param next used to determine which way the player is moving
     */
    private void checkChangeEndlessCorridorRoom(Command command, Room next)
    {
        if (currentRoom.equals(labyrinthRooms.get(2)) && next != null)
        {
            if (command.getSecondWord().equals("forward"))
            {
                endlessCorridorRoom++;
                corridorMovement.add("forward");
            } else if (command.getSecondWord().equals("backward"))
            {
                endlessCorridorRoom--;
                corridorMovement.add("backward");
            }
        }
    }

    /**
     * Used to track movement through the repeating rooms in the spiral staircase for the back command to utilise
     * @param command used to determine which way the player is moving
     * @param next used to determine which way the player is moving
     */
    private void checkChangeStaircaseFloor(Command command, Room next)
    {
        if (currentRoom.equals(labyrinthRooms.get(5)) && next != null)
        {
            if (command.getSecondWord().equals("up"))
            {
                staircaseLevel++;
                staircaseMovement.add("up");
            } else if (command.getSecondWord().equals("down"))
            {
                staircaseLevel--;
                staircaseMovement.add("down");
            }
        }
    }

    /**
     * Used to track movement through the repeating rooms in the binary corridor for the back command to utilise
     * @param command used to determine which way the player is moving
     * @param next used to determine which way the player is moving
     */
    private void checkChangeBinaryRoom(Command command, Room next)
    {
        if (currentRoom.equals(labyrinthRooms.get(9)))
        {
            if (command.getSecondWord().equals("backward"))
            {
                binaryRoom--;
                binaryMovement.add("backward");
            } else if (next.equals(currentRoom))
            {
                binaryRoom++;
                binaryMovement.add("forward");
            }
        }
    }
}