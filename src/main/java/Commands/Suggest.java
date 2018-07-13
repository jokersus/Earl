package Commands;

import Default.Core;
import Default.Server;
import Listeners.ReactionListener;
import Logs.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.*;
import java.util.HashMap;

public class Suggest extends Command {

	private static HashMap<String, Server> servers = new HashMap<>();
	private Message message;
	private ReactionListener listener;

	public Suggest() {
		super("Suggest", "For member feedback and suggestions");

	}


	@Override
	public void run(Message message) {
		if (servers == null) {
			servers = new HashMap<>();
		}
		super.run(message);
		this.message = message;
		if (message.getContentRaw().length() < name.length() + 2) {
			if (message.getGuild().getMember(message.getAuthor()).hasPermission(Permission.MANAGE_CHANNEL)) {
				if (servers.containsKey(message.getGuild().getId())) {
					message.getChannel().sendMessage(
							new EmbedBuilder()
									.setDescription("Suggest channel already set to " + message.getGuild().getTextChannelById(servers.get(message.getGuild().getId()).getLog()).getAsMention())
									.setColor(message.getGuild().getMember(message.getAuthor()).getColor())
									.build()
					).queue();
					return;
				} else {
					startListener(message, message.getMember(), "Would you like to set this channel to receive suggestions?");
					return;
				}
			}
		} else if (message.getContentRaw().split(" ")[1].equals("-set")) {
			if (!message.getGuild().getMember(message.getAuthor()).hasPermission(Permission.MANAGE_CHANNEL)) {
				message.getChannel().sendMessage(
						new EmbedBuilder()
								.setDescription("You do not have enough permission to use use this command.\nRequired: `MANAGE_CHANNEL`")
								.setColor(message.getGuild().getMember(message.getAuthor()).getColor())
								.build()
				).queue();
				return;
			} else if (message.getMentionedChannels().isEmpty()) {
				message.getChannel().sendMessage(
						new EmbedBuilder()
								.setDescription("Please specify a channel. Usage: `" + Core.prefix + this.name.toLowerCase() + " -set #channel")
								.setColor(message.getGuild().getMember(message.getAuthor()).getColor())
								.build()
				).queue();
				return;
			} else {
				servers.put(message.getGuild().getId(), new Server(message.getGuild(), message.getMentionedChannels().get(0)));
				message.getChannel().sendMessage(
						new EmbedBuilder()
								.setDescription("Suggest channel for " + message.getGuild().getName() + " set to " + message.getMentionedChannels().get(0).getAsMention())
								.setColor(message.getGuild().getMember(message.getAuthor()).getColor())
								.build()
				).queue();
				return;
			}
		}

		if (servers.containsKey(message.getGuild().getId())) {
			send(message, message.getGuild().getTextChannelById(servers.get(message.getGuild().getId()).log));
			send(message, message.getTextChannel());
		} else {
			kill();
			startListener(message, message.getMember(), "Would you like to set this channel to receive suggestions?");
			return;
		}
	}

	@Override
	public void setYes(Message message) {
		servers.remove(message.getGuild().getId());
		servers.put(message.getGuild().getId(), new Server(message.getGuild(), message.getTextChannel()));
		message.getChannel().sendMessage(
				new EmbedBuilder()
						.setDescription(this.name + " for " + message.getGuild().getName() + " set to " + message.getChannel().getName())
						.setColor(message.getGuild().getMember(message.getAuthor()).getColor())
						.build()
		).queue();
		message.getJDA().removeEventListener(listener);
	}

	@Override
	public void setNo(Message message) {
		message.getChannel().sendMessage(
				new EmbedBuilder()
						.setDescription(this.name + " channel not set\nPlease run the command in the desired chanel, or specify a channel by `\" + Default.Default.Core.prefix + \"suggest -set #channel")
						.setColor(message.getGuild().getMember(message.getAuthor()).getColor())
						.build()
		).queue();
		message.getJDA().removeEventListener(listener);
	}


	private void send(Message message, TextChannel channel) {
		message.delete().queue();
		channel.sendMessage(
				new EmbedBuilder()
						.setTitle(":bulb:" + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + " " + this.name.toLowerCase() + "ed:")
						.addField("Context", message.getContentRaw().substring(9, message.getContentRaw().length()), false)
						.addField("In channel", message.getGuild().getTextChannelById(message.getChannel().getId()).getAsMention(), false)
						.addField("Timestamp", formatTime(message.getCreationTime()), false)
						.setThumbnail(message.getAuthor().getAvatarUrl())
						.setColor(message.getMember().getColor())
						.setFooter("Message ID: " + message.getId() + " | User  ID: " + message.getAuthor().getId(), message.getGuild().getIconUrl())
						.build()).complete();
	}


	private void startListener(Message message, Member member, String text) {
		listener = new ReactionListener(this, member, message, text);
		message.getJDA().addEventListener(listener);
	}

	private static HashMap<String, Server> readObject() {
		try {
			FileInputStream fileIn = new FileInputStream("src/main/java/Server data/Suggest.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			HashMap<String, Server> object = (HashMap<String, Server>) in.readObject();
			in.close();
			fileIn.close();
			return object;
		} catch (IOException i) {
			File temp = new File("src/main/java/Server data/Suggest.ser");
			i.printStackTrace();
			servers = new HashMap<>();
			try {
				temp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Created new file" + temp.getName() + " at" + temp.getPath());
			return null;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return null;
		}
	}

	private static void writeObject(Object object) {
		try {
			FileOutputStream fileOut = new FileOutputStream("src/main/java/Server data/Suggest.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(object);
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved");
		} catch (IOException e) {
			File temp = new File("src/main/java/Server data/Suggest.ser");
			try {
				temp.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
//			writeObject(object);
			e.printStackTrace();
		}
	}

	public static void init() {
		servers = (HashMap<String, Server>) readObject();
	}

	public static void end() {
		writeObject(servers);
	}

	private void kill() {
		if (this.listener != null) {
			this.listener.kill();
			this.message.getJDA().removeEventListener(listener);
		}
	}
}
