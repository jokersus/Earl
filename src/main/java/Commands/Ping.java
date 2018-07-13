package Commands;

import Logs.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.time.Instant;

public class Ping extends Command {

	private static long lastPing = 0;
	private static double average = 0;
	private static EmbedBuilder embed = new EmbedBuilder();
	private static int currentUsage = 0;

	public Ping() {
		super("Ping", "Replies with \"Pong\"");
	}

	@Override
	public void run(Message message) {
		currentUsage++;
		long current = message.getJDA().getPing();
		average = (average * currentUsage + current) / (currentUsage + 1);
		embed.setDescription("⏱ " + Long.toString(current) + "ms" +
				" \n\n⏮ " + Long.toString(lastPing) + "ms" +
				" \n\n⚪ " + String.format("%.02f", average) + "ms");
		embed.setColor(message.getMember().getColor());
		lastPing = current;
		message.getChannel().sendMessage(embed.build()).queue();
		super.run(message);
	}
}