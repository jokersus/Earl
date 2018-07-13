package Moderator;

import Logs.Command;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;

public class Mute extends Command {

	private HashMap<String, String> servers = new HashMap<>();

	public Mute() {
		super("Mute", "Mutes a user");
	}

	@Override
	public void run(Message message) {

		for (User user : message.getMentionedUsers()) {

		}
	}
}
