import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Season;
import com.teamtreehouse.model.Team;

public class Manager {

	private Player[] players;
	private Season season;
	private BufferedReader reader;
	private Map<Integer, String> menu;
	private int maxTeams;

	public Manager(Player[] players, Season season) {
		this.players = players;
		this.season = season;
		reader = new BufferedReader(new InputStreamReader(System.in));
		menu = new HashMap<>();
		menu.put(1, "Create a new team for this season");
		menu.put(2, "Add players to team");
		menu.put(3, "Remove players from team");
		menu.put(4, "Display Team report");
		menu.put(5, "Display League Balance Report");
		menu.put(6, "Balance teams");
		menu.put(7, "Print out Team rooster");
		menu.put(8, "Exit Manager");
		maxTeams = players.length / 11;
	}

	// prompts user to choose action, checks if user entered integer
	private Integer promptAction() throws NumberFormatException, IOException {
		System.out.println("\nYour options:");
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
					if (season.getTeams().size() == maxTeams) {
						System.out.println("\nMaximum number of Teams already in League.");
						System.out.println("Choose other option.\n\n");
					} else {
						Team team = promptForNewTeam();
						season.addTeam(team);
						System.out.printf("%s added!%n%n", team);
					}
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

					// Check if team does not have maximum players
					if (teamAdd.getPlayerCount() == Team.MAX_PLAYERS) {
						System.out.println("This team has already maximum number of players.");
						System.out.println("Choose other options.%n");
					} else {

						// List only players that are not already in team
						List<Player> availablePlayers = new ArrayList<>(Arrays.asList(players));
						for (Team team : season.getTeams()) {
							availablePlayers.removeAll(team.getPlayers());
						}
						Player player = promptForPlayer(availablePlayers);

						// Add player to team
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
			// displays team report (height/experience) about players in
			// selected team
			case 4:
				try {
					displayTeamReport();
				} catch (IOException e) {
					System.out.println("Problem with input");
					e.printStackTrace();
				}
				break;
			// Displays high level report about balance in whole league
			case 5:
				displayLeagueBalanceReport();
				break;
			// Balance teams builded manually
			case 6:
				if (season.getTeams() != null){
					balanceTeams();
					System.out.println("\nTeams have been balanced!");
				} else {
					System.out.println("\nPlease first add teams to season...");
				}
				break;
			// Prints team rooster
			case 7:
				Team teamPrint;
				try {
					teamPrint = promptForTeam();
					printRooster(teamPrint);
					System.out.println("Thank you for using Soccer League Organizer");
				} catch (IOException e) {
					System.out.println("Problem with input");
					e.printStackTrace();
				}
				break;
			// Exits the program
			case 8:
				System.out.println("Thank you for using Soccer League Organizer");
				break;
			default:
				System.out.printf("Unknown choice... Try again.%n%n%n");
			}
		} while (choice != 7 || choice != 8);
	}

	// Balances teams by trying to minimize score difference
	private void balanceTeams() {
		populateTeams();
		Map<Team, Integer> teamsScores = createTeamScoresMap();
		Boolean isTeamsBalanced = chceckBalance(teamsScores);

		// Starts process of balancing until all teams are fully and balanced
		while (!isTeamsBalanced) {
			int totalScore = 0;
			int lowestScore = 50;
			int highestScore = 0;
			Team best = null;
			;
			Team worst = null;

			// Extract highest, lowest and average score
			for (Team team : teamsScores.keySet()) {
				totalScore += teamsScores.get(team);
				if (teamsScores.get(team) > highestScore) {
					highestScore = teamsScores.get(team);
					best = team;
				}
				if (teamsScores.get(team) < lowestScore) {
					lowestScore = teamsScores.get(team);
					worst = team;
				}
			}
			int averageScore = totalScore / season.getTeams().size();

			// Extract good player from best team and bad player from worst team
			// then switch
			Player bestPlayer = null;
			Player goodPlayer = null;
			Player averageGoodPlayer = null;
			Player worstPlayer = null;
			Player badPlayer = null;
			Player averageBadPlayer = null;
			Player goodTransfer = null;
			Player badTransfer = null;

			// select best possible player for transfer
			for (Player player : best.getPlayers()) {
				if (player.isPreviousExperience() && player.getHeightInInches() > 46) {
					bestPlayer = player;
				} else if (player.isPreviousExperience()) {
					goodPlayer = player;
				} else if (player.getHeightInInches() > 40) {
					averageGoodPlayer = player;
				}
			}

			// Remove the best possible player from team and store it in
			// variable
			if (bestPlayer != null) {
				goodTransfer = best.removePlayer(bestPlayer);
			} else if (bestPlayer == null && goodPlayer != null) {
				goodTransfer = best.removePlayer(goodPlayer);
			} else if (bestPlayer == null && goodPlayer == null && averageGoodPlayer != null) {
				goodTransfer = best.removePlayer(averageGoodPlayer);
			}

			// select worst possible player for transfer
			for (Player player : worst.getPlayers()) {
				if (!player.isPreviousExperience() && player.getHeightInInches() < 41) {
					worstPlayer = player;
				} else if (!player.isPreviousExperience()) {
					badPlayer = player;
				} else if (player.getHeightInInches() < 41) {
					averageBadPlayer = player;
				}
			}

			// Remove the worst possible player from team and store it in
			// variable
			if (worstPlayer != null) {
				badTransfer = worst.removePlayer(worstPlayer);
			} else if (worstPlayer == null && badPlayer != null) {
				badTransfer = worst.removePlayer(badPlayer);
			} else if (worstPlayer == null && badPlayer == null && averageBadPlayer != null) {
				badTransfer = worst.removePlayer(averageBadPlayer);
			}

			// Switch good and bad player
			best.addPlayer(badTransfer);
			worst.addPlayer(goodTransfer);
			
			// Re-check team scores and balance
			teamsScores = createTeamScoresMap();
			isTeamsBalanced = chceckBalance(teamsScores);
		}
	}

	// Adds players to not full teams
	private void populateTeams() {
		for (Team team : season.getTeams()) {
			while (team.getPlayerCount() < Team.MAX_PLAYERS) {
				addPlayer(team);
			}
		}

	}

	// adds player to team
	private void addPlayer(Team team) {
		Random random = new Random();
		List<Player> availablePlayers = new ArrayList<>(Arrays.asList(players));
		for (Team teamB : season.getTeams()) {
			availablePlayers.removeAll(teamB.getPlayers());
		}
		int playerToAdd = random.nextInt(availablePlayers.size());
		team.addPlayer(availablePlayers.get(playerToAdd));
	}

	// Checks if teams are acceptably balanced by comparing their scores
	private Boolean chceckBalance(Map<Team, Integer> teamsScores) {
		int totalScore = 0;
		for (Team team : teamsScores.keySet()) {
			totalScore += teamsScores.get(team);
		}
		int averageScore = totalScore / season.getTeams().size();
		for (Team team : teamsScores.keySet()) {
			if (teamsScores.get(team) > averageScore + 1 || teamsScores.get(team) < averageScore - 1) {
				return false;
			}
		}
		return true;
	}

	// Creates map of teams and assigns each team a score based on height an
	// experience of its players
	private Map<Team, Integer> createTeamScoresMap() {
		Map<Team, Integer> teamScore = new HashMap<>();
		for (Team team : season.getTeams()) {
			Integer score = 0;
			for (Player player : team.getPlayers()) {
				if (player.isPreviousExperience()) {
					score += 2;
				}
				if (player.getHeightInInches() < 41) {
					score--;
				} else if (player.getHeightInInches() > 46) {
					score++;
				}
			}
			teamScore.put(team, score);
		}
		return teamScore;
	}

	// Prints teams rooster for coach
	private void printRooster(Team team) {
		System.out.printf("\nYour team (%s) rooster coach %s:\n", team.getTeamName(), team.getCoachName());
		int counter = 1;
		for (Player player : team.getPlayers()) {
			System.out.println(counter + ".) " + player);
			counter++;
		}
		System.out.println();

	}

	// Displays full report for all teams in league
	private void displayLeagueBalanceReport() {
		for (Team team : season.getTeams()) {
			Map<Boolean, List<Player>> playerByExperience = createPlayersByExperienceMap(team);
			Map<String, List<Player>> playersByHeight = createPlayersByHeightMap(team);
			displayExperience(playerByExperience, team);
			System.out.println("Players by height:");
			for (Map.Entry<String, List<Player>> entry : playersByHeight.entrySet()) {
				System.out.printf("Total players in range %s: %d\n", entry.getKey(), entry.getValue().size());
			}
		}
	}

	// Displays report for players grouped by height
	private void displayTeamReport() throws IOException {
		Team team = promptForTeam();
		System.out.printf("\nTeam Report for %s:\n\n", team);
		System.out.println("Players by height:");
		// Create height portion of report
		Map<String, List<Player>> playersByHeight = createPlayersByHeightMap(team);
		for (Map.Entry<String, List<Player>> entry : playersByHeight.entrySet()) {
			System.out.printf("Players in range %s:\n", entry.getKey());
			for (Player player : entry.getValue()) {
				System.out.printf("%s %s\n", player.getFirstName(), player.getLastName());
			}
			System.out.printf("Total players in that range: %d\n\n", entry.getValue().size());
		}

		// Create experience portion of report
		Map<Boolean, List<Player>> playerByExperience = createPlayersByExperienceMap(team);
		displayExperience(playerByExperience, team);
	}

	// Outputs experience portion of report
	private void displayExperience(Map<Boolean, List<Player>> playerByExperience, Team team) {
		int hasExperience = 0;
		int noExperience = 0;
		if (playerByExperience.get(true) != null) {
			hasExperience = playerByExperience.get(true).size();
		}
		if (playerByExperience.get(false) != null) {
			noExperience = playerByExperience.get(false).size();
		}
		int total = hasExperience + noExperience;
		double percent = 0.0;
		if (total != 0) {
			percent = ((double) hasExperience / (double) total) * 100.0;
		}
		System.out.printf("\nExperience of players in team %s:\n", team);
		System.out.printf("%d player(s) have experience and %d player(s) has no experience.\n", hasExperience,
				noExperience);
		System.out.printf("This means around %.0f%% of players are experienced.\n\n", percent);
	}

	// Creates map of player lists grouped by experience (key is boolean
	// representation of player experience)
	private Map<Boolean, List<Player>> createPlayersByExperienceMap(Team team) {
		Map<Boolean, List<Player>> playersByExperience = new HashMap<>();
		for (Player player : team.getPlayers()) {
			if (player.isPreviousExperience()) {
				List<Player> players = playersByExperience.get(true);
				if (players == null) {
					players = new ArrayList<>();
					playersByExperience.put(true, players);
				}
				players.add(player);
			} else {
				List<Player> players = playersByExperience.get(false);
				if (players == null) {
					players = new ArrayList<>();
					playersByExperience.put(false, players);
				}
				players.add(player);
			}
		}
		return playersByExperience;
	}

	// Creates map of player lists grouped by height (key is string
	// representation of given range)
	private Map<String, List<Player>> createPlayersByHeightMap(Team team) {
		Map<String, List<Player>> playersByHeight = new HashMap<>();
		for (Player player : team.getPlayers()) {
			if (player.getHeightInInches() >= 35 && player.getHeightInInches() <= 40) {
				List<Player> players = playersByHeight.get("35 to 40 inch");
				if (players == null) {
					players = new ArrayList<>();
					playersByHeight.put("35 to 40 inch", players);
				}
				players.add(player);
			} else if (player.getHeightInInches() > 40 && player.getHeightInInches() <= 46) {
				List<Player> players = playersByHeight.get("41 to 46 inch");
				if (players == null) {
					players = new ArrayList<>();
					playersByHeight.put("41 to 46 inch", players);
				}
				players.add(player);
			} else if (player.getHeightInInches() > 46 && player.getHeightInInches() <= 50) {
				List<Player> players = playersByHeight.get("47 to 50 inch");
				if (players == null) {
					players = new ArrayList<>();
					playersByHeight.put("47 to 50 inch", players);
				}
				players.add(player);
			}
		}
		return playersByHeight;
	}

	// Prompts user to select player
	private Player promptForPlayer(List<Player> players) throws NumberFormatException, IOException {
		Collections.sort(players);
		System.out.println("\nAll players:");
		int counter = 1;
		for (Player player : players) {
			System.out.println(counter + ".)" + player);
			counter++;
		}
		Integer userInput = checkUserInput(0, players.size(),
				"\nWhich player you want to choose (by player number):  ");
		return players.get(userInput - 1);
	}

	// Prompts user to select team and checks if input was number
	private Team promptForTeam() throws IOException {
		System.out.println("\nAvailable teams:");
		int counter = 1;
		List<Team> teams = season.getTeams();
		Collections.sort(teams);
		for (Team team : teams) {
			System.out.printf("%d.) %s%n", counter, team);
			counter++;
		}
		Integer userInput = checkUserInput(0, season.getTeams().size(), "\nYour choice (by team number):  ");
		return season.getTeam(userInput - 1);
	}

	// Loops to ensure player input will be number and not lesser or
	// greater then specified
	private Integer checkUserInput(int min, int max, String text) throws IOException {
		System.out.print(text + "  ");
		boolean properInput = false;
		Integer userInput = 0;
		do {
			try {
				userInput = Integer.parseInt(reader.readLine());
				if (userInput > min && userInput <= max) {
					properInput = true;
				} else {
					System.out.println("Please enter number between " + (min + 1) + " and " + (max));
				}
			} catch (NumberFormatException e) {
				System.out.println("Please enter number between " + (min + 1) + " and " + (max));
				properInput = false;
			}
		} while (!properInput);
		return userInput;
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
