package Listeners;

import Moderator.Automute;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Invite;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.time.OffsetDateTime;

public class MemberLeaveListener extends ListenerAdapter {

	private static String logChannelId = "294880275211747339";


	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {

		event.getGuild().getTextChannelById("424516377022562304").sendMessage(event.getMember().getAsMention() + " has been tragically obliterated.").embed(new EmbedBuilder()
				.setImage("https://imgur.com/MV00hh4.jpg")
				.setColor(new Color(0, 128, 255))
				.build()).queue();

		event.getGuild().getTextChannelById("294880275211747339").sendMessage(new EmbedBuilder()
				.setThumbnail(event.getUser().getAvatarUrl())
				.setTitle("❌" + " User left")
				.setDescription(event.getMember().getAsMention() + " `" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "`")
				.addField("Id", event.getUser().getId(), false)
				.addField("Joined Discord", formatTime(event.getUser().getCreationTime()), false)
				.setColor(new Color(0, 128, 255))
				.build()).queue();
	}

	private String formatTime(OffsetDateTime time) {
		return String.format("%02d", time.getHour()) + ":" + String.format("%02d", time.getSecond()) + ":" + String.format("%02d", time.getHour()) + " | " + String.format("%02d", time.getDayOfMonth()) + "." + String.format("%02d", time.getMonthValue()) + "." + time.getYear();
	}
}
