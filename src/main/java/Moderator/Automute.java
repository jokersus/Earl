package Moderator;

import Commands.Complain;
import Logs.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;

public class Automute extends Command {

	private HashMap<String, Integer> servers = new HashMap<>();
	private static final int dailySeconds = 86400;
	private final String roleId = "422621940868579338";
	private final int days = 30;


	public Automute() {
		super("Automute", "Mutes new members, if their accounts are younger than a specified period");
	}


	public void run(Member member) {
		double age = ((Instant.now().getEpochSecond() - member.getUser().getCreationTime().toEpochSecond()) / dailySeconds);
		if (age < days) {
			member.getGuild().getController().addRolesToMember(member, member.getGuild().getRoleById(roleId)).queue();
			member.getGuild().getTextChannelById(Complain.servers.get(member.getGuild().getId()).log).sendMessage(new EmbedBuilder()
					.setDescription("User " + member.getAsMention() + " has been automuted because their account is " + days + " days old")
					.setColor(Color.RED)
					.build()).queue();
		}
	}
}
