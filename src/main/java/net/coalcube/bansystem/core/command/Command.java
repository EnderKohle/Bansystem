package net.coalcube.bansystem.core.command;

import net.coalcube.bansystem.core.util.User;

public interface Command {

	public void execute(User user, String[] args);
	
}
