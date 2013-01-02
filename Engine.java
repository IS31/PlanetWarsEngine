// Copyright 2010 owners of the AI Challenge project
//
// Licensed under the Apache License, Version 2.0 (the "License"); you may not
// use this file except in compliance with the License. You may obtain a copy
// of the License at http://www.apache.org/licenses/LICENSE-2.0 . Unless
// required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.
//
// Author: Jeff Cameron (jeff@jpcameron.com)
//
// Plays a game of Planet Wars between two computer programs.
// NOTICE: code has been modified to allow synchronized gameplay (one player
// per turn).

import java.io.*;
import java.util.*;

public class Engine {
    public static void KillClients(List<Process> clients) {
	for (Process p : clients) {
	    if (p != null) {
		p.destroy();
	    }
	}
    }

    public static boolean AllTrue(boolean[] v) {
	for (int i = 0; i < v.length; ++i) {
	    if (!v[i]) {
		return false;
	    }
	}
	return true;
    }

    public static void main(String[] args) {
	// Check the command-line arguments.
	if (args.length != 3 && args.length != 5) {
	    System.err.println("ERROR: wrong number of command-line " +
			       "arguments.");
	    System.err.println("USAGE: java -jar PlayGame.jar <map_file_name> " +
			       "\"java <player_one>\" " +
			       "\"java <player_two>\" [<max_num_turns> <max_turn_time>] ");
	    System.err.println("Explanation:");
	    System.err.println("<map_file_name>: Location of .txt file of map to use for this game");
	    System.err.println("\"java <player_one>\": Player1. Make sure to add quotes, and add the 'java' part before the bot name. Also make sure your bot is actually compiled (there should be a .class file of your bot file)");
	    System.err.println("\"java <player_two>\": Idem");
	    System.err.println("Optional (add both values as argument when deviating from defaults):");
	    System.err.println("<max_num_turns>: Maximum number of turns this game may take. Default: 100");
	    System.err.println("<max_turn_time>: Maximum number of time a bot is allowed to take per turn. Default: 100");
	    System.exit(1);
	}
	// Initialize the game. Load the map.
	String mapFilename = args[0];
	int maxNumTurns = 100;
	int maxTurnTime = 100; 
	String logFilename = "log.txt";
	//optional arguments
	if (args.length == 5) {
		maxTurnTime = Integer.parseInt(args[4]);
		maxNumTurns = Integer.parseInt(args[3]);
	}
	Game game = new Game(mapFilename, maxNumTurns, 0, logFilename);
	if (game.Init() == 0) {
	    System.err.println("ERROR: failed to start game. map: " +
			       mapFilename);
	}
	// Start the client programs (players).
	List<Process> clients = new ArrayList<Process>();
	for (int i = 1; i <= 2; ++i) {
	    String command = args[i];
	    Process client = null;
	    try {
		client = Runtime.getRuntime().exec(command);
	    } catch (Exception e) {
		client = null;
	    }
	    if (client == null) {
		KillClients(clients);
		System.err.println("ERROR: failed to start client: " +
				   command);
		System.exit(1);
	    }
	    clients.add(client);
	}
	boolean[] isAlive = new boolean[clients.size()];
	for (int i = 0; i < clients.size(); ++i) {
	    isAlive[i] = (clients.get(i) != null);
	}
	
	System.err.println("Engine entering main game loop");
	
	int numTurns = 0;
	int ap = 0; //MODIFIED: active player, based on current numTurns
	// Enter the main game loop.
	while (game.Winner() < 0) {
	    // Send the game state to the clients.
	    System.err.println("The game state:");
	    System.err.print(game);
	    //MODIFIED: send game state only to active player (ap)
	    //for (int i = 0; i < clients.size(); ++i) {
		if (clients.get(ap) == null || !game.IsAlive(ap + 1)) {
		    continue;
		}
		String message = game.PovRepresentation(ap + 1) + "go\n";
		try {
		    OutputStream out = clients.get(ap).getOutputStream();
		    OutputStreamWriter writer = new OutputStreamWriter(out);
		    writer.write(message, 0, message.length());
		    writer.flush();
		    //System.err.println("engine > player" + (ap + 1) + ": " +
			//		 message);
		} catch (Exception e) {
		    clients.set(ap, null);
		}
	    //}
	    // Get orders from the clients.
	    StringBuilder[] buffers = new StringBuilder[clients.size()];
	    boolean[] clientDone = new boolean[clients.size()];
	    for (int i = 0; i < clients.size(); ++i) {
		buffers[i] = new StringBuilder();
		clientDone[i] = false;
	    }
	    long startTime = System.currentTimeMillis();
	    while (!AllTrue(clientDone) &&
		   System.currentTimeMillis() - startTime < maxTurnTime) {
		//for (int i = 0 ; i < clients.size(); ++i) {
	    // MODIFIED: one player per turn
	    
		    if (!isAlive[ap] || !game.IsAlive(ap + 1) || clientDone[ap]) {
			clientDone[ap] = true;
			continue;
		    }
                    try {
                        InputStream inputStream =
                            clients.get(ap).getInputStream();
                        while (inputStream.available() > 0) {
                            char c = (char)inputStream.read();
                            if (c == '\n') {
                                String line = buffers[ap].toString().trim();
                                //System.err.println("P" + (ap+1) + ": " + line);
                                line = line.toLowerCase().trim();
                                System.err.println("player" + (ap + 1) + " > engine: " + line);
                                // Modified: only process 1 order
                                game.IssueOrder(ap + 1, line);
                                clientDone[ap] = true;
                                /*
                                if (line.equals("go")) {
                                	clientDone[i] = true;
                                } else {
                                	game.IssueOrder(i + 1, line);
                                }
                                */
                                buffers[ap] = new StringBuilder();
                                break;
                            } else {
                                buffers[ap].append(c);
                            }
                        }
                        StringBuilder buf = new StringBuilder();
                        InputStream stderr = clients.get(ap).getErrorStream();
                        while (stderr.available() > 0){
                            char c = (char)stderr.read();
                            if (c == '\n') {
                                String ln = buf.toString();
                                System.err.println("Player " + (ap+1) + ": " + ln);
                                buf = new StringBuilder();
                            }
                            else {
                                buf.append(c);
                            }
                        }
                    } catch (Exception e) {
			System.err.println("WARNING: player " + (ap+1) +
					   " crashed.");
			clients.get(ap).destroy();
			game.DropPlayer(ap + 1);
			isAlive[ap] = false;
		    }
		}
	    //MODIFIED: update active player
	    ap = (ap + 1)%2;
	   // }
	    for (int i = 0 ; i < clients.size(); ++i) {
		if (!isAlive[i] || !game.IsAlive(i + 1)) {
		    continue;
		}
		if (clientDone[i]) {
		    continue;
		}
		// Do NOT drop players at timeouts
		/*
		System.err.println("WARNING: player " + (i+1) +
				   " timed out.");
		clients.get(i).destroy();
		game.DropPlayer(i + 1);
		isAlive[i] = false;
		*/
	    }
	    ++numTurns;
	    System.err.println("Turn " + numTurns);
	    System.out.print(game.FlushGamePlaybackString());
	    System.out.flush();
	    game.DoTimeStep();
	    
	    //Keep advancing turns, until there are no ships in flight. 
	    //This way each player has complete knowledge of game state on start
	    while (game.getFleets().size() != 0) {
//	    	++numTurns;
//		    System.err.println("Turn " + numTurns);
//		    System.out.print(game.FlushGamePlaybackString());
//		    System.out.flush();
		    game.skipTimeStep();
	    }
	}
	KillClients(clients);
	if (game.Winner() > 0) {
	    System.err.println("Player " + game.Winner() + " Wins!");
	} else {
	    System.err.println("Draw!");
	}
	System.out.println(game.GamePlaybackString());
    }
    public void WriteLogMessage(String logFilename, String message) {
    	
        if (logFilename == null) {
          return;
        }
        try {
    	  BufferedWriter logFile = new BufferedWriter(new FileWriter(logFilename, true));
          logFile.write(message);
          logFile.newLine();
          logFile.flush();
          logFile.close();
        } catch (Exception e) {
          // whatev
        }
      }
}
