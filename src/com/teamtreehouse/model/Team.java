package com.teamtreehouse.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Team {

	public static final Integer MAX_PLAYERS = 11;
	
	private String teamName;
	private String coachName;
	private Set<Player> players;
	
	public Team(String teamName, String coachName) {
		this.teamName = teamName;
		this.coachName = coachName;
		players = new TreeSet<>();
	}

	public String getTeamName() {
		return teamName;
	}

	public String getCoachName() {
		return coachName;
	}
	
	public Integer getPlayerCount(){
		return players.size();
	}

	@Override
	public String toString() {
		return "Team " + teamName + " coached by coach " + coachName;
	}

	public void addPlayer(Player player) {
		players.add(player);
		
	}

	public List<Player> getPlayers() {
		return new ArrayList<>(players);
	}

	public void removePlayer(Player player) {
		players.remove(player);
		
	}
	
	
}
