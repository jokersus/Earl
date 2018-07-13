package Listeners;

import Moderator.Automute;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Invite;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.time.OffsetDateTime;

public class MemberJoinListener extends ListenerAdapter {

	private static String logChannelId = "294880275211747339";

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {

//		event.getGuild().getTextChannelById("424516377022562304").sendMessage(event.getMember().getAsMention() + " has joined us in the fight for a better future!").embed(new EmbedBuilder()
//				.setImage("https://i.imgur.com/xNNq6l8.jpg")
//				.addField("**Welcome to the Official /r/CodeGeass Discord Server**", "Make sure to read the rules in <#392206712381374464>", false)
//				.setFooter(event.getUser().getName() + " is our " + event.getGuild().getMembers().size() + "th member!", event.getGuild().getIconUrl())
//				.setColor(new Color(0, 128, 255))
//				.build()).queue();
//
//		event.getGuild().getTextChannelById("294880275211747339").sendMessage(new EmbedBuilder()
//				.setThumbnail(event.getUser().getAvatarUrl())
//				.setTitle("✅" + " User joined")
//				.setDescription(event.getMember().getAsMention() + " `" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "`")
//				.addField("Id", event.getUser().getId(), false)
//				.addField("Joined server", formatTime(event.getMember().getJoinDate()), false)
//				.addField("Joined Discord", formatTime(event.getUser().getCreationTime()), false)
//				.setColor(new Color(0, 128, 255))
//				.build()).queue();

		new Automute().run(event.getMember());

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Attempting to get invite");
		Invite temp = Invites.getLatest();
		if (temp == null) {
			event.getGuild().getTextChannelById(logChannelId).sendMessage(new EmbedBuilder()
					.setTitle("⛓ Through the vanity URL")
					.setColor(Color.GREEN)
					.build()).queue();
			return;
		}

		if (temp.getInviter() == null) {
			event.getGuild().getTextChannelById(logChannelId).sendMessage(new EmbedBuilder()
					.setTitle("⛓ Through the wikia widget")
					.setColor(Color.GREEN)
					.build()).queue();
			return;
		}
		event
				.getGuild()
				.getTextChannelById(logChannelId)
				.sendMessage(new EmbedBuilder()
						.setTitle("⛓ Through invite " + temp.getCode())
						.addField("Created by", temp.getInviter().getName() + "#" + temp.getInviter().getDiscriminator(), false)
						.addField("User ID", temp.getInviter().getId(), false)
						.addField("Link creation date", formatTime(temp.getCreationTime()), false)
						.addField("Uses", Integer.toString(temp.getUses()), false)
						.setThumbnail(temp.getInviter().getAvatarUrl())
						.setColor(event.getGuild().getMemberById(temp.getInviter().getId()).getColor())
						.build())
				.queue();

		Invites.init(event.getGuild());
		BlacklistNotifier.run(event.getMember());
	}

	private String formatTime(OffsetDateTime time) {
		return String.format("%02d", time.getHour()) + ":" + String.format("%02d", time.getSecond()) + ":" + String.format("%02d", time.getHour()) + " | " + String.format("%02d", time.getDayOfMonth()) + "." + String.format("%02d", time.getMonthValue()) + "." + time.getYear();
	}
}
