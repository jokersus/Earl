package Logs;

import Default.Core;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.io.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class Command {

	//	public static HashMap<String, Default.Server> servers = new HashMap<>();
	public String name;
	public String info;
	public long usage;
	public String optionalLog = "";


	public Command(String name, String info) {
		this.name = name;
		this.info = info;
		try {
			BufferedReader in = new BufferedReader(new FileReader("src/main/java/Logs/" + this.name + ".txt"));
			this.usage = Long.parseLong(in.readLine());
			in.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (java.lang.NumberFormatException e) {
			this.usage = 0;
		}
	}

//	public Logs.Command(String name, String info, int usage) {
//		this.name = name;
//		this.info = info;
//		this.usage = usage;
//	}


	public void run(Message message) {
		this.usage = this.usage + 1;
		String log = "[" + ZonedDateTime.now(Clock.systemUTC()) + "] " +
				message.getAuthor().getName() + "#" +
				message.getAuthor().getDiscriminator() +
				" (" + message.getAuthor().getId() +
				")" + " used " + this.name + " in " +
				message.getGuild().getName() + " | " +
				message.getChannel().getName() + " [" +
				message.getGuild().getId() + " | " +
				message.getChannel().getId() + "]" +
				" " + this.optionalLog;
		if (message.getContentRaw().contains("-v")) {
			sendUsage(message);
			log += " with optional argument -v";
		}

		System.out.println(log);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("src/main/java/Logs/log.txt", true));
			out.write(log + '\n');
			out.close();
			out = new BufferedWriter(new FileWriter("src/main/java/Logs/" + this.name + ".txt"));
			out.write(Long.toString(this.usage));
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}


	public void invalidUsage(Message message) {
		message.getChannel().sendMessage(
				new EmbedBuilder()
						.setDescription("Invalid usage. See: `" + Core.prefix + "help " + this.name.toLowerCase() + "`")
						.setColor(message.getGuild().getMember(message.getAuthor()).getColor())
						.build()
		).queue();
	}

	public void selfNoPermission(Message message, Permission permission) {
		message.getChannel().sendMessage(
				new EmbedBuilder()
						.setDescription("Bot lacks permission " + permission.getName())
						.setColor(message.getGuild().getMember(message.getAuthor()).getColor())
						.build()
		).queue();
	}

	public void memberNoPermission(Message message, Permission permission, Member member) {
		message.getChannel().sendMessage(
				new EmbedBuilder()
						.setDescription(member.getUser().getName() + " lacks permission " + permission.getName())
						.setColor(message.getGuild().getMember(message.getAuthor()).getColor())
						.build()
		).queue();
	}

	public void addOptionalLog(String string) {
		this.optionalLog += string;
	}

	public String formatTime(OffsetDateTime time) {
		return String.format("%02d", time.getHour()) + ":" + String.format("%02d", time.getSecond()) + ":" + String.format("%02d", time.getHour()) + " | " + String.format("%02d", time.getDayOfMonth()) + "." + String.format("%02d", time.getMonthValue()) + "." + time.getYear();
	}

	private void sendUsage(Message message) {
		message.getChannel().sendMessage(new EmbedBuilder()
				.setDescription(this.name + " has been used " + this.usage + " times")
				.setColor(message.getMember().getColor())
				.build()
		).queue();
	}

	public void setYes(Message message) {
	}

	public void setNo(Message message) {
	}

	public void setGeneric(Message message, Message inputMessage) {
	}

	public void genericText(Message message, String emote, String text) {
	}
}
