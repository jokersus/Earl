package Listeners;

import Logs.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {

	private Command command;
	private Member member;
	private Message message;


	public ReactionListener(Command command, Member member, Message message, String text) {
		this.command = command;
		this.member = member;
		this.message = message.getChannel().sendMessage(
				new EmbedBuilder()
						.setDescription(text)
						.setColor(message.getMember().getColor())
						.build()).complete();
		this.message.addReaction("✅").complete();
		this.message.addReaction("\u274C").queue();
	}

	public ReactionListener(Command command, Member member, Message message) {
		this.command = command;
		this.member = member;
		this.message = message;
		this.message.addReaction("✅").complete();
		this.message.addReaction("\u274C").queue();
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getMessageId().equals(message.getId())) { // Same message
			System.out.println(event.getReactionEmote().getName());
			if (event.getMember().equals(member)) { // Specified member
				if (event.getReactionEmote().getName().equals("✅")) { // Yes
					command.setYes(message);
				} else if (event.getReactionEmote().getName().equals("\u274C")) {
					command.setNo(message);
				}
				message.clearReactions().queue();
			} else if (!event.getMember().equals(message.getGuild().getSelfMember())) {
				event.getReaction().removeReaction().queue();
			}
		}
	}

	public void kill() {
		message.clearReactions().queue();
	}
}
