package Listeners;

import Default.Core;
import Logs.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.*;
import java.util.HashMap;

public class WelcomeGoodbye extends Command {


	public static HashMap<String, JoinLeave> servers = new HashMap<>();
	private SetWelcomeGoodbyeListener welcomeGoodbyeListener;
	private PromptInputListener promptInputListener;
	private ReactionListener reactionListener;
	private boolean isWelcome;


	public WelcomeGoodbye() {
		super("WelcomeGoodbye", "Sets welcome and goodbye messages");
//		servers.put("ServerID1", new JoinLeave("address1", "address2"));
//		System.out.println(servers.get("ServerID1").getJoinImage());
//		writeObject(servers);
//		servers = readObject();
	}

	public void run(Message message) {
		super.run(message);
		if (!message.getMember().hasPermission(Permission.MANAGE_SERVER)) {
			memberNoPermission(message, Permission.MANAGE_CHANNEL, message.getMember());
			return;
		}
		isWelcome = message.getContentRaw().startsWith(Core.prefix + "welcome");
//		servers.put(message.getGuild().getId(), null);
		sendPrompt(message);
		promptInputListener = new PromptInputListener(this, message.getMember(), message);
		message.getJDA().addEventListener(promptInputListener);
	}

	private void sendInputPrompt(Message message, String prompt) {
		message.getChannel().sendMessage(new EmbedBuilder()
				.setDescription(prompt)
				.setColor(message.getMember().getColor())
				.build()).queue();
	}

	private void sendPrompt(Message message) {
		message.getChannel().sendMessage(new EmbedBuilder()
				.setDescription(":one: Set new message\n:two: See the current message\n:three: Set channel\n:four: Disable for this server")
				.setColor(message.getMember().getColor())
				.build()).queue();
	}

	@Override
	public void setGeneric(Message message, Message userMessage) {
		switch (userMessage.getContentRaw()) {
			case "1":
				sendInputPrompt(message, "Enter new message\nSpecial annotatinos:\n\t`%USER%` -> New member username\n\t`%MENTION%` -> New member mention");
				welcomeGoodbyeListener = new SetWelcomeGoodbyeListener(this, message.getMember(), message, isWelcome);
				message.getJDA().removeEventListener(promptInputListener);
				message.getJDA().addEventListener(welcomeGoodbyeListener);
				break;
			case "2":
//				sendWelcomeMessage(message.getTextChannel()).
				break;
			case "3":
//				sendInputPrompt(message, "Enter new channel, or react to set this channel");
				reactionListener = new ReactionListener(this, message.getMember(), message, "Enter new channel, or react to set this channel");
				message.getJDA().addEventListener(reactionListener);
				promptInputListener = new PromptInputListener(this, message.getMember(), message);
				message.getJDA().addEventListener(promptInputListener);
			default:
				if (!userMessage.getMentionedChannels().isEmpty()) {
					setChannel(message, userMessage.getMentionedChannels().get(0));
					message.getJDA().removeEventListener(reactionListener, promptInputListener);
//				} else {
//					invalidUsage(message);
//				}
				}
		}
	}

	public void setWelcomeMessage(Message message, String text) {
		if (servers.containsKey(message.getGuild().getId())) {
			servers.get(message.getGuild().getId()).setJoinImage(text);
		} else {
			JoinLeave temp = new JoinLeave();
			temp.setJoinText(text);
			if (text.contains("https://")) {
				if (text.charAt(1) == 'h') {
					temp.setJoinImage(text.substring(text.indexOf("https://"), text.indexOf(" ")));
				} else {
					temp.setJoinImage(text.substring(text.indexOf("https://"), text.length()));
				}
			} else if (text.contains("http://")) {
				if (text.charAt(1) == 'h') {
					temp.setJoinImage(text.substring(text.indexOf("http://"), text.indexOf(" ")));
				} else {
					temp.setJoinImage(text.substring(text.indexOf("https://"), text.length()));
				}
			}
			temp.setEnabled(true);
			servers.put(message.getGuild().getId(), temp);
		}
		message.getChannel().sendMessage("Message set").queue();
		message.getJDA().removeEventListener(welcomeGoodbyeListener);
	}

	public void setGoodbyeMessage(Message message, String text) {
		if (servers.containsKey(message.getGuild().getId())) {
			servers.get(message.getGuild().getId()).setLeaveImage(text);
		} else {
			JoinLeave temp = new JoinLeave();
			temp.setLeaveImage(text);
			temp.setEnabled(true);
			servers.put(message.getGuild().getId(), temp);
		}
		message.getChannel().sendMessage("Message set").queue();
		message.getJDA().removeEventListener(welcomeGoodbyeListener);
	}

//	private void sendWelcomeMessage(TextChannel channel) {
//		channel.sendMessage(new EmbedBuilder()
//				.setDescription()
//		.build()).queue();
//	}

	public void setChannel(Message message, TextChannel channel) {
		if (servers.containsKey(message.getGuild().getId())) {
			servers.get(message.getGuild().getId()).setChannelID(channel.getId());
		} else {
			JoinLeave temp = new JoinLeave();
			temp.setChannelID(channel.getId());
			temp.setEnabled(true);
			servers.put(message.getGuild().getId(), temp);
		}
	}

	@Override
	public void setYes(Message message) {
		setChannel(message, message.getTextChannel());
		message.getJDA().removeEventListener(promptInputListener);
		message.getJDA().removeEventListener(reactionListener);
	}

	@Override
	public void setNo(Message message) {
		message.getJDA().removeEventListener(reactionListener);
	}

	@Override
	public void genericText(Message message, String emote, String text) {
	}

	public final void writeObject(Object object) {
		try {
			FileOutputStream fileOut = new FileOutputStream("src/main/java/Server data/WelcomeGoodbye.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(object);
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final HashMap<String, JoinLeave> readObject() {
		try {
			FileInputStream fileIn = new FileInputStream("src/main/java/Server data/WelcomeGoodbye.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			HashMap<String, JoinLeave> object = (HashMap<String, JoinLeave>) in.readObject();
			in.close();
			fileIn.close();
			return object;
		} catch (IOException i) {
			i.printStackTrace();
			return null;
		} catch (ClassNotFoundException c) {
			System.out.println("Employee class not found");
			c.printStackTrace();
			return null;
		}
	}
}
