package Moderator;

import Default.Core;
import Default.Server;
import Listeners.ReactionListener;
import Logs.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.ArrayList;
import java.util.List;

public class Kick extends Command {

	private ReactionListener listener;
	private Member self;
	public Kick() {
		super("Kick", "Kicks a user");
	}

	@Override
	public void run(Message message) {

		if (!message.getGuild().getMember(message.getAuthor()).hasPermission(Permission.KICK_MEMBERS) && !message.getAuthor().getId().equals(Core.Owner)) {
			addOptionalLog("User does not have enough permissions");
			message.getChannel().sendMessage("You lack `KICK_MEMBERS` permission").queue();
			super.run(message);
			return;
		}

		addOptionalLog("Users mentioned: [");
		List<User> mentionedUsers = message.getMentionedUsers();
		self = message.getGuild().getSelfMember(); // Might want to move to Command class

		if (message.getContentRaw().split(" ")[1].equals("--everyone") && (message.getMember().isOwner() || message.getAuthor().getId().equals(Core.Owner))) {
			listener = new ReactionListener(this, message.getMember(), message, "Are you sure you want to kick everyone?");
			message.getJDA().addEventListener(listener);
			return;
		}

		kick(message, mentionedUsers);

		addOptionalLog("]");
		super.run(message);
	}

	private void kick(Message message, List<User> mentionedUsers) {
		for (User user : mentionedUsers) {
			Member member = message.getGuild().getMember(user);

			if (!self.canInteract(member)) {
				message.getChannel().sendMessage("Cannot kick " + member.getAsMention() + ". Reason: User is higher, or equal in the role hierarchy").queue();
				addOptionalLog(user.getName() + "#" + user.getDiscriminator() + " (" + user.getId() + ")(Not kicked: hierarchy),");
				continue;   // Next user
			}

			message.getGuild().getController().kick(member).queue(
					success -> {
						message.getChannel().sendMessage("Kicked " + member.getEffectiveName()).queue();
						addOptionalLog(user.getName() + "#" + user.getDiscriminator() + " (" + user.getId() + "), ");
					},
					error ->
					{
						if (error instanceof PermissionException) {
//							PermissionException pe = (PermissionException) error;
//							Permission missingPermission = pe.getPermission();  // Missing permission
							message.getChannel().sendMessage("Error kicking [" + member.getEffectiveName() + "]: " + error.getMessage()).queue();
							addOptionalLog(user.getName() + "#" + user.getDiscriminator() + " (" + user.getId() + ")(Not kicked: permission),");
						} else {
							message.getChannel().sendMessage("Unknown error while kicking [" +
									member.getEffectiveName() + "]: <" +
									error.getClass().getSimpleName() + (">: ") +
									error.getMessage()).queue();
							addOptionalLog(user.getName() + "#" + user.getDiscriminator() + " (" + user.getId() + ")(Not kicked: unknown),");
						}
					});
		}
	}


	@Override
	public void setYes(Message message) {
		message.getChannel().sendMessage("This command has been disabled for this build").queue();
//		List<Member> allMembersTemp = message.getGuild().getMembers();
//		List<User> allUsersTemp = new ArrayList<>();
//		for (Member member : allMembersTemp) {
//			allUsersTemp.add(member.getUser());
//		}
//		kick(message, allUsersTemp);
	}

	@Override
	public void setNo(Message message) {
		message.getJDA().removeEventListener(listener);
	}
}
