package Listeners;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class SetWelcomeGoodbyeListener extends PromptInputListener {

	public WelcomeGoodbye command;
	private boolean isWelcome;

	public SetWelcomeGoodbyeListener(WelcomeGoodbye command, Member member, Message message, boolean isWelcome) {
		super(command, member, message);
		this.command = command;
		this.isWelcome = isWelcome;
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (correctMessageCondition(event.getMessage())) {		// If same member and message
			if (isWelcome) {
				command.setWelcomeMessage(event.getMessage(), event.getMessage().getContentRaw());
			} else {
				command.setGoodbyeMessage(event.getMessage(), event.getMessage().getContentRaw());
			}
		}
	}
}
