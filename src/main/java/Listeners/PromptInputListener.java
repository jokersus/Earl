package Listeners;

import Logs.Command;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class PromptInputListener extends ListenerAdapter {

	protected Command command;
	protected Member member;
	protected Message message;


	public PromptInputListener(Command command, Member member, Message message) {
		this.command = command;
		this.member = member;
		this.message = message;
	}


	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (correctMessageCondition(message)) {
			command.setGeneric(message, event.getMessage());
		}
	}

	protected boolean correctMessageCondition(Message message) {
		return message.getChannel().equals(message.getChannel()) && message.getMember().equals(this.member);
	}
}
