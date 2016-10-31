import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
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
		menu.put(3, "Exit Manager");
	}

	// prompts user to choose action, checks if user entered integer
	private Integer promptAction() throws NumberFormatException, IOException {
		System.out.printf("There are currently %d registered players.%n", players.length);
		System.out.println("Your options (choose menu option number):");
		for (Map.Entry<Integer, String> option : menu.entrySet()) {
			System.out.printf("%d.  %s%n", option.getKey(), option.getValue());
		}
		System.out.print("What do you want to do:   ");
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
			// Allows for adding players to team
			case 2:
				Team team;
				try {
					team = promptForTeam();
					Player player = promptForPlayer();
					team.addPlayer(player);
					System.out.printf("Player %s %s was added to team %s%n%n", player.getFirstName(), player.getLastName(), team.getTeamName());
				} catch (IOException e) {
					System.out.println("Problem with input");
					e.printStackTrace();
				}
				break;
			// Exits the program
			case 3:
				System.out.println("Thank you for using Soccer League Organizer");
				break;
			default:
				System.out.println("Unknown choice... Try again.%n%n%n");
			}
		} while (choice != 3);
	}

	// Prompts user to select player and add him to team
	private Player promptForPlayer() throws NumberFormatException, IOException {
		System.out.println("All players:");
		int counter = 1;
		for (Player player : players) {
			System.out.println(counter + ".)" + player);
			counter++;
		}
		System.out.print("Which player to add (by player number):  ");
		return players[Integer.parseInt(reader.readLine()) - 1];
	}

	// Prompts user to select team
	private Team promptForTeam() throws IOException {
		System.out.println("Available teams:");
		int counter = 1;
		for (Team team : season.getTeams()) {
			System.out.printf("%d.) %s%n", counter, team);
			counter++;
		}
		System.out.print("Your choice (by team name):  ");
		return season.getTeam(reader.readLine());
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
