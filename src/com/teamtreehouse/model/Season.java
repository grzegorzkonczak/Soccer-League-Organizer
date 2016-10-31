package com.teamtreehouse.model;

import java.util.ArrayList;
import java.util.List;

public class Season {

	private List<Team> teams;
	
	public Season(){
		teams = new ArrayList<Team>();
	}
	
	public void addTeam(Team team){
		teams.add(team);
	}

	public List<Team> getTeams() {
		return teams;
	}

	public Team getTeam(Integer userInput) {
		return teams.get(userInput);
	}
}
