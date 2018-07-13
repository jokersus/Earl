package Default;

import Default.Core;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.Serializable;

public class Server implements Serializable {
	public String guild;
	public String prefix;
	public String log;
	public boolean autorole = false;

	public Server(Guild guild, TextChannel log) {
		this.guild = guild.getId();
		this.prefix = Core.prefix;
		this.log = log.getId();
	}

	public String getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild.getId();
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getLog() {
		return log;
	}

	public void setLog(TextChannel log) {
		this.log = log.getId();
	}
}