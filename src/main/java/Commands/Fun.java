package Commands;

import net.dv8tion.jda.core.entities.Message;

public class Fun {

	public void run(Message message) {
		String content = message.getContentRaw();
		if (content.startsWith("git ")) {
			gitJoke(message);
			return;
		}
	}

	private void gitJoke(Message message) {
		String temp = message.getContentRaw().split(" ")[1];
		message.getChannel().sendMessage("```git: '" + temp + "' is not a git command. See 'git --help'.```").queue();
	}
}
