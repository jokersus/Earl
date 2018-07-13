package Listeners;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Invite;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class Invites extends TimerTask {

	private static HashMap<String, Integer> invites = new HashMap<>();

	private static Guild Guild;

	@Override
	public void run() {
		reinit();
	}

	public static void init(Guild guild) {
		Guild = guild;
		List<Invite> guildInvites = guild.getInvites().complete();
		for (Invite invite : guildInvites) {
			invites.put(invite.getCode(), invite.getUses());
		}
	}

	public static void reinit() {
		List<Invite> guildInvites = Guild.getInvites().complete();
		for (Invite invite : guildInvites) {
			if (!invites.containsKey(invite.getCode())) {
				invites.put(invite.getCode(), invite.getUses());
			}
		}
	}

	public static Invite getLatest() {
		HashMap<Invite, Integer> updatedInviteMap = getList(Guild);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (Map.Entry<Invite, Integer> entry : updatedInviteMap.entrySet()) {
			if (invites.get(entry.getKey().getCode()) == null) {
				return entry.getKey();
			} else if (!invites
					.get(entry.getKey().getCode()).equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}


	private static HashMap<Invite, Integer> getList(Guild guild) {
		HashMap<Invite, Integer> temp = new HashMap<>();
		List<Invite> guildInvites = guild.getInvites().complete();
		for (Invite invite : guildInvites) {
			temp.put(invite, invite.getUses());
		}
		if (temp.isEmpty()) {
			System.exit(5);
		}
		return temp;
	}
}
