package Listeners;

import Logs.Command;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;

public class GenericReactionListener extends ListenerAdapter {

	private Command command;
	private Member member;
	private Message message;
	private String text;
	private int number;
	private static HashMap<Integer, String> emotes = new HashMap<>();

	public GenericReactionListener(Command command, Member member, Message message) {
		this.command = command;
		this.member = member;
		this.message = message;
	}

//	@Override
//	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
//		if (event.getMessageId().equals(this.message.getId())) {		// Same message
//			if (event.getMember().equals(this.member)) {				// Same member
//				command.setGeneric(message, event.getGuild().getTextChannelById(event.getMessageId()));
//			}
//		}
//	}
}
