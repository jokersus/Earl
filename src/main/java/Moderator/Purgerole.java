package Moderator;

import Logs.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

public class Purgerole extends Command {

	public Purgerole() {
		super("Purgerole", "Purges every member of a given role");
	}

	@Override
	public void run(Message message) {
		super.run(message);
		if (message.getContentRaw().length() < this.name.length() + 2) {
			invalidUsage(message);
		} else {
			if (!message.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
				selfNoPermission(message, Permission.MANAGE_ROLES);
				return;
			} else if (!message.getMember().hasPermission(Permission.MANAGE_ROLES)) {
				memberNoPermission(message, Permission.MANAGE_ROLES, message.getMember());
				return;
			}

			String[] args = message.getContentRaw().substring(11, message.getContentRaw().length()).split(", ");
			for (String roleId : args) {
				List<Member> members = message.getGuild().getMembersWithRoles(message.getGuild().getRoleById(roleId));
				for (Member member : members) {
					message.getGuild()
							.getController()
							.removeSingleRoleFromMember(member,
									message.getGuild().getRoleById(roleId))
							.queue();
					message.getChannel().sendMessage(new EmbedBuilder()
							.setDescription("Removed " + message.getGuild().getRoleById(roleId).getName() + " from " + member.getEffectiveName())
							.setColor(member.getColor())
							.build()).queue();
				}
			}
		}
	}
}
