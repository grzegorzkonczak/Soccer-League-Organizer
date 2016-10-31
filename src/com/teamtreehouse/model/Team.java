package com.teamtreehouse.model;

import java.util.Set;
import java.util.TreeSet;

public class Team {

	private String teamName;
	private String coachName;
	private Set<Player> players;
	
	public Team(String teamName, String coachName) {
		this.teamName = teamName;
		this.coachName = coachName;
		players = new TreeSet();
	}

	public String getTeamName() {
		return teamName;
	}

	public String getCoachName() {
		return coachName;
	}
	
	
}
