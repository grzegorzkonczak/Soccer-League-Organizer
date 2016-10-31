import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Players;
import com.teamtreehouse.model.Season;

public class LeagueManager {

  public static void main(String[] args) {
    Player[] players = Players.load();
    Season season = new Season();
    Manager manager = new Manager(players, season);
    manager.run();
  }

}
