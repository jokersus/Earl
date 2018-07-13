import Default.Core;
import Listeners.CommandListener;
import Listeners.Invites;
import Listeners.MemberJoinListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.Timer;

public class Main extends ListenerAdapter {

	public static void main(String[] args) throws LoginException, InterruptedException {

		String tempToken;
		if (System.getenv("TOKEN") != null) {
			tempToken = System.getenv("TOKEN");
		} else if (Core.TOKEN != null && !Core.TOKEN.isEmpty()) {
			tempToken = Core.TOKEN;
		} else {
			System.out.println("Token not found");
			throw new LoginException();
		}

		new JDABuilder(AccountType.BOT).setToken(tempToken).addEventListener(new CommandListener()).addEventListener(new MemberJoinListener()).buildAsync();

	}
}