package Commands;

import Default.Core;
import Default.Server;
import Listeners.JoinLeave;
import Listeners.ReactionListener;
import Logs.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.io.*;
import java.util.HashMap;


public class Complain extends Command {

	private Message message;
	private ReactionListener listener;
	public static HashMap<String, Server> servers = new HashMap<>();

	public Complain() {
		super("Complain", "For member complaints. Moderators will be alerted by the message immediately, and the message will be deleted");
	}

	public Complain(String name, String info) {
		super(name, info);
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
				if (servers != null && servers.containsKey(message.getGuild().getId())) {
					message.getChannel().sendMessage(
							new EmbedBuilder()
									.setDescription("Complain channel already set to " + message.getGuild().getTextChannelById(servers.get(message.getGuild().getId()).getLog()).getAsMention())
									.setColor(message.getGuild().getMember(message.getAuthor()).getColor())
									.build()
					).queue();
					return;
				} else {
//					startListener(message.getChannel().sendMessage(new EmbedBuilder()
//							.setDescription("Would you like to set this channel to receive complaint alerts?")
//							.setColor(message.getMember().getColor())
//							.build()).complete(), message.getMember());
					startListener(message, message.getMember(), "Would you like to set this channel to receive complaints?");
					return;
				}
			} else {
				invalidUsage(message);
				return;
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
								.setDescription("Commands.Complain channel for " + message.getGuild().getName() + " set to " + message.getMentionedChannels().get(0).getAsMention())
								.setColor(message.getGuild().getMember(message.getAuthor()).getColor())
								.build()
				).queue();
				return;
			}
		}
		if (!servers.containsKey(message.getGuild().getId())) {
			MessageBuilder prompt = new MessageBuilder();
			prompt.setEmbed(
					new EmbedBuilder()
							.setDescription("Commands.Complain channel not found.\nPlease set it with `" + Core.prefix + this.name.toLowerCase() + " -set #channel`\nOr run `" + Core.prefix + this.name.toLowerCase() + "`")
							.setColor(message.getGuild().getSelfMember().getColor())
							.build()
			);
			message.getChannel().sendMessage(prompt.build()).queue();
			return;
		}

		send(message, message.getGuild().getTextChannelById(servers.get(message.getGuild().getId()).log));

		addOptionalLog(" Context: [" + message.getContentRaw() + "]");
		super.run(message);
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

	private void startListener(Message message, Member member, String text) {
		listener = new ReactionListener(this, member, message, text);
		message.getJDA().addEventListener(listener);
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

	public void setMessage(Message message) {
		this.message = message;
	}

	public void kill() {
		if (this.listener != null) {
			this.listener.kill();
			this.message.getJDA().removeEventListener(listener);
		}
	}

	public static void init() {
		servers = (HashMap<String, Server>) readObject();
	}

	public static void end() {
		writeObject(servers);
	}

	private static HashMap<String, Server> readObject() {
		try {
			FileInputStream fileIn = new FileInputStream("src/main/java/Server data/Complain.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			HashMap<String, Server> object = (HashMap<String, Server>) in.readObject();
			in.close();
			fileIn.close();
			return object;
		} catch (IOException i) {
			File temp = new File("src/main/java/Server data/Complain.ser");
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
			FileOutputStream fileOut = new FileOutputStream("src/main/java/Server data/Complain.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(object);
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved");
		} catch (IOException e) {
			File temp = new File("src/main/java/Server data/Complain.ser");
			try {
				temp.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
//			writeObject(object);
			e.printStackTrace();
		}
	}
}