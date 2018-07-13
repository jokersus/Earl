package Listeners;

import Commands.*;
//import Default.Default.Core;
import Default.Core;
import Logs.Command;
import Moderator.Kick;
import Moderator.Purgerole;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.HashMap;
import java.util.Timer;

public class CommandListener extends ListenerAdapter {
	private Complain complain;
	private WelcomeGoodbye joinLeaveListener;
	private HashMap<String, Command> current = new HashMap<>();

	public CommandListener() {
		complain = new Complain();
		joinLeaveListener = new WelcomeGoodbye();
		Complain.init();
		Suggest.init();
		Anime.init();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		if (event.getChannel().getId().equals("424516377022562304") && event.getAuthor().getId().equals("420250496419364868") && event.getMessage().getContentRaw().contains("has been tragically obliterated.")) {
			event.getMessage().addReaction("\uD83C\uDDEB").queue();
			return;
		}
		if (event.getMessage().getContentRaw().equals("pin me daddy")) {
			event.getMessage().pin().queue();
			event.getChannel().sendMessage("Daddy Lancy has pinned " + event.getMember().getAsMention()).queue();
			event.getMessage().unpin().queue();
		}
		if (event.getMessage().getContentRaw().equals("ping me daddy")) {
			event.getChannel().sendMessage(event.getMember().getAsMention()).queue();
		}
		if (event.getMessage().getContentRaw().startsWith(Core.prefix)) {
			switch (event.getMessage().getContentRaw().split(" ")[0]) {
				case Core.prefix + "die":
					if (!event.getAuthor().getId().equals(Core.Owner)) {
						return;
					}
					event.getJDA().removeEventListener(event.getJDA().getRegisteredListeners());
					Complain.end();
					Suggest.end();
					event.getMessage().getChannel().sendMessage(new EmbedBuilder()
							.setColor(Color.RED)
							.setDescription("Shutting down...")
							.build()).queue();
					System.exit(0);
					break;
				case "pin me daddy":
					event.getMessage().pin().queue();
					event.getChannel().sendMessage("Daddy Lancy has pinned " + event.getMember().getAsMention()).queue();
					event.getMessage().unpin().queue();
					break;
				case "ping me daddy":
					event.getChannel().sendMessage(event.getMember().getAsMention() + " daddy Lancy has pinged you").queue();
					break;
				case Core.prefix + "ping":
					new Ping().run(event.getMessage());
					break;
				case Core.prefix + "kick":
					new Kick().run(event.getMessage());
					break;
				case Core.prefix + "complain":
					if (current.containsKey(event.getGuild().getId())) {
						complain.kill();
						current.remove(event.getGuild().getId());
					}
					current.put(event.getGuild().getId(), new Complain());
					current.get(event.getGuild().getId()).run(event.getMessage());
					break;
				case Core.prefix + "suggest":
					new Suggest().run(event.getMessage());
					break;
				case Core.prefix + "purgerole":
					new Purgerole().run(event.getMessage());
					break;
				case Core.prefix + "welcome":
					joinLeaveListener.run(event.getMessage());
					break;
				case Core.prefix + "goodbye":
					joinLeaveListener.run(event.getMessage());
					break;
				case Core.prefix + "quote":
					new Quote().run(event.getMessage());
					break;
//				case Core.prefix + "wish":
//					new Wish().run(event.getMessage());
//					break;
				case Core.prefix + "next":
					new Anime().run(event.getMessage());
					break;
			}
		} else {
			new Fun().run(event.getMessage());
		}
	}


	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("Logged in as " + event.getJDA().getSelfUser().getName());
		Invites.init(event.getJDA().getGuildById("292277485310312448"));
		Invites.getLatest();
		new Timer().schedule(new Invites(), 0, 120000);
	}
}
