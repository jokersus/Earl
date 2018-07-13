package Commands;

import Default.Core;
import Logs.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Wish extends Command {

	private static String BotChannel = "292279509900853248";
	private static String ModChat = "452160970790535175";
	private static String logMessageChannelId = "452429016545230859";
	private static Color tanabataColor = new Color(255, 203, 226);
	private static String logMessageId = "452813690610253824";
	private static Message logMessage;
	public Wish() {
		super("Wish", "Tanabata event /r/CG 2018");
	}


	@Override
	public void run(Message message) {
		super.run(message);
		if (message.getContentRaw().length() < 6 || !message.getContentRaw().contains("|")) {
			send(message, "Invalid usage. Your wish was not recorded.\nCorrect usage: " + Core.prefix + "wish wish1 | wish2", Color.RED);
			return;
		} else if (!message.getChannel().getId().equals(BotChannel)) {
			send(message, "Wish not recorded. Please run the command in " + message.getGuild().getTextChannelById(BotChannel).getAsMention(), Color.RED);
			return;
		} else {
			logMessage = message.getJDA().getGuildById("336433630253678593").getTextChannelById(logMessageChannelId).getMessageById(logMessageId).complete();

			String messageId = "";
			String[] temp = logMessage.getContentRaw().split("\n");
			for (String pair : temp) {
				if (pair.startsWith(toHex(message.getAuthor().getId()))) {
					messageId = pair.substring(pair.indexOf("|") + 2, pair.length());
					send(message, "Message updated. Happy Tanabata", Color.GREEN);
					String[] wishes = message.getContentRaw().split(Core.prefix + "wish")[1].split("\\|");
					message.getGuild().getTextChannelById(ModChat).getMessageById(messageId).complete().editMessage(new EmbedBuilder()
							.setAuthor("\uD83C\uDF38 Someone has made a wish", null, null)
							.addField("Wish 1:", wishes[0], false)
							.addField("Wish 2:", wishes[1], false)
							.setColor(tanabataColor)
							.build()).queue();
					return;
				}
			}

			if (logMessage.getContentRaw().length() > 1900) {
				message.getGuild().getTextChannelById(ModChat).sendMessage(message.getGuild().getMemberById(Core.Owner).getAsMention() + " User/Message data message is reaching the limit").queue();
			}
		}

		send(message, "Wish recorded. Happy Tanabata", Color.GREEN);

		String[] wishes = message.getContentRaw().split(Core.prefix + "wish")[1].split("\\|");
		Message report = message.getGuild().getTextChannelById(ModChat).sendMessage(new EmbedBuilder()
				.setAuthor("\uD83C\uDF38 Someone has made a wish", null, null)
				.addField("Wish 1:", wishes[0], false)
				.addField("Wish 2:", wishes[1], false)
				.setColor(tanabataColor)
				.build()).complete();
//		Message report = message.getGuild().
//				getTextChannelById(ModChat).sendMessage(new EmbedBuilder()
//				.setAuthor("\uD83C\uDF38 Someone has made a wish", null, null)
//				.setDescription(message.getRawContent().split(Core.prefix + "wish")[1])
//				.setColor(tanabataColor)
//				.build())
//				.complete();


		StringBuilder temp = new StringBuilder();
		temp.append(logMessage.getContentRaw());
		temp.append('\n');
		temp.append(toHex(message.getAuthor().getId()));
		temp.append(" | ");
		temp.append(report.getId());
		logMessage.editMessage(temp.toString()).queue();

	}


	private void send(Message message, String text, Color color) {
		message.getChannel().sendMessage(new EmbedBuilder()
				.setDescription(text)
				.setColor(color)
				.build()).queue(scheduler -> {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					message.delete().queue();
				}
			}, 800);
		});
	}

	private String toHex(String id) {
		long longid = Long.parseLong(id);
		String hexid = "";
		while (longid != 0) {
			long digit = longid % 16;
			char hexDigit = (digit <= 9 && digit >= 0) ?
					(char) (digit + '0') : (char) (digit - 10 + 'A');
			hexid = hexDigit + hexid;
			longid = longid / 16;
		}
		return hexid;
	}

	private String hexDecoder(String hex) {
		return Long.toString(Long.parseLong(hex, 16));
	}
}
