package Listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.awt.*;
import java.io.*;

public class BlacklistNotifier {

	private static final String CodeGeassId = "292277485310312448";
	private static final String ModChannelId = "293432840538947584";
	private static final File userIds = new File("src/main/java/Listeners/blacklisted_users.txt");

	public static void run(Member member) {


		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(userIds));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {

			String userId = member.getUser().getId();
			String tempId;
			while ((tempId = reader.readLine()) != null) {
				if (userId.equals(tempId)) {
					member.getGuild().getTextChannelById(ModChannelId).sendMessage(new EmbedBuilder()
							.setDescription("User " + member.getAsMention() + " is suspicious because they have been seen raiding servers")
							.setColor(Color.ORANGE)
							.build()).queue();
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
