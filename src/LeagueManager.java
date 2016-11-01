import java.util.Arrays;

import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Players;
import com.teamtreehouse.model.Season;

public class LeagueManager {

  public static void main(String[] args) {
    Player[] players = Players.load();
    System.out.printf("There are currently %d registered players.%n", players.length);
    Season season = new Season();
    Manager manager = new Manager(Arrays.asList(players), season);
    manager.run();
  }

}
