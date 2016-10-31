import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Season;
import com.teamtreehouse.model.Team;

public class Manager {

	private Player[] players;
	private Season season;
	private BufferedReader reader;
	private Map<Integer, String> menu;

	public Manager(Player[] players, Season season) {
		this.players = players;
		this.season = season;
		reader = new BufferedReader(new InputStreamReader(System.in));
		menu = new HashMap<>();
		menu.put(1, "Create a new team for this season");
		menu.put(2, "Add players to team");
		menu.put(3, "Remove players from team");
		menu.put(4, "Exit Manager");
	}

	// prompts user to choose action, checks if user entered integer
	private Integer promptAction() throws NumberFormatException, IOException {
		System.out.printf("There are currently %d registered players.%n", players.length);
		System.out.println("Your options:");
		for (Map.Entry<Integer, String> option : menu.entrySet()) {
			System.out.printf("%d.  %s%n", option.getKey(), option.getValue());
		}
		System.out.print("What do you want to do(choose menu option number):   ");
		return Integer.parseInt(reader.readLine());
	}

	// Main loop of program, executes option chosen by user
	public void run() {
		Integer choice = 0;
		do {
			try {
				choice = promptAction();
			} catch (NumberFormatException e) {
				System.out.println("Please choose a number from menu");
			} catch (IOException e) {
				System.out.println("Problem with input");
				e.printStackTrace();
			}
			switch (choice) {
			// Creates new team and adds it to season
			case 1:
				try {
					Team team = promptForNewTeam();
					season.addTeam(team);
					System.out.printf("%s added!%n%n", team);
				} catch (IOException e) {
					System.out.println("Problem with input");
					e.printStackTrace();
				}
				break;
			// Allows for adding players to team up to maximum number of players
			// per team
			case 2:
				Team teamAdd;
				try {
					teamAdd = promptForTeam();
					if (teamAdd.getPlayerCount() == Team.MAX_PLAYERS) {
						System.out.println("This team has already maximum number of players.");
						System.out.println("Choose other options.%n");
					} else {
						Player player = promptForPlayer(Arrays.asList(players));
						teamAdd.addPlayer(player);
						System.out.printf("Player %s %s was added to team %s%n%n", player.getFirstName(),
								player.getLastName(), teamAdd.getTeamName());
					}
				} catch (IOException e) {
					System.out.println("Problem with input");
					e.printStackTrace();
				}
				break;
			// Allows for removing players from teams
			case 3:
				Team teamRemove;
				try {
					teamRemove = promptForTeam();
					if (teamRemove.getPlayerCount() == 0) {
						System.out.println("This team does not have players.");
						System.out.println("Choose other options.\n");
					} else {
						Player player = promptForPlayer(teamRemove.getPlayers());
						teamRemove.removePlayer(player);
						System.out.printf("Player %s %s was removed from team %s%n%n", player.getFirstName(),
								player.getLastName(), teamRemove.getTeamName());
					}
				} catch (IOException e) {
					System.out.println("Problem with input");
					e.printStackTrace();
				}
				break;
			// Exits the program
			case 4:
				System.out.println("Thank you for using Soccer League Organizer");
				break;
			default:
				System.out.println("Unknown choice... Try again.%n%n%n");
			}
		} while (choice != 4);
	}

	// Prompts user to select player and add him to team
	private Player promptForPlayer(List<Player> players) throws NumberFormatException, IOException {
		System.out.println("\nAll players:");
		int counter = 1;
		for (Player player : players) {
			System.out.println(counter + ".)" + player);
			counter++;
		}
		System.out.print("\nWhich player you want to choose (by player number):  ");
		return players.get(Integer.parseInt(reader.readLine()) - 1);
	}

	// Prompts user to select team and checks if input was number
	private Team promptForTeam() throws IOException {
		System.out.println("\nAvailable teams:");
		int counter = 1;
		for (Team team : season.getTeams()) {
			System.out.printf("%d.) %s%n", counter, team);
			counter++;
		}
		boolean properInput = false;
		Integer userInput = 0;
		// Loop that ensures player input will be number and not lesser or greater then number
		// of teams added to season
		do {
			System.out.print("\nYour choice (by team number):  ");
			try{
				userInput = Integer.parseInt(reader.readLine());
				if (userInput > 0 && userInput <= season.getTeams().size()){
					properInput = true;
				}
			} catch (NumberFormatException e) {
				System.out.println("Please enter number...");
				properInput = false;
			}
		} while (!properInput);
		return season.getTeam(userInput - 1);
	}

	// Asks user about details of team he wants to create
	private Team promptForNewTeam() throws IOException {
		System.out.println("Enter Team name:");
		String teamName = reader.readLine();
		System.out.println("Enter Coach name:");
		String coachName = reader.readLine();
		return new Team(teamName, coachName);
	}

}
