package Commands;

import Logs.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.List;

public class Quote extends Command {

	public Quote() {
		super("Quote", "Quotes and embeds a given message by ID");
	}

	@Override
	public void run(Message message) {
		super.run(message);
		String[] content = message.getContentRaw().split(" ");
		if (content.length < 2) {
			invalidUsage(message);
		} else {
			System.out.println(content[1]);
			for (TextChannel channel : message.getGuild().getTextChannels()) {
				try {
					Message temp = channel.getMessageById(content[1]).complete();
					sendQuote(temp, message.getTextChannel());
				} catch (net.dv8tion.jda.core.exceptions.ErrorResponseException e) {
					System.out.println("Not this channel");
					continue;
				}
			}
		}
	}


	private void sendQuote(Message message, TextChannel channel) {
		EmbedBuilder embed = new EmbedBuilder();
		StringBuffer attachments;
		if (!message.getAttachments().isEmpty()) {
			attachments = new StringBuffer();
			if (message.getAttachments().size() == 1) {
				if (message.getAttachments().get(0).isImage()) {
					embed.setImage(message.getAttachments().get(0).getUrl());
				}
			} else {
				for (Message.Attachment attachment : message.getAttachments()) {
					attachments.append(attachment.getUrl()).append('\n');
				}
				embed.addField("Attachments:", attachments.toString(), false);
			}
		}

		List<MessageEmbed> temp = message.getEmbeds();
		if (message.getContentRaw().isEmpty() && !temp.isEmpty()) {
			channel.sendMessage(message.getMember().getUser().getName() + "#" + message.getMember().getUser().getDiscriminator() + " in " + message.getTextChannel().getAsMention() + " || " + formatTime(message.getCreationTime())).embed(temp.get(0)).complete();
			return;
		}

		channel.sendMessage(embed
				.setAuthor(message.getAuthor().getName(), "https://discordapp.com/channels/" + message.getGuild().getId() + "/" + message.getChannel().getId() + "?jump=" + message.getId(), message.getAuthor().getAvatarUrl())
				.setDescription(message.getContentRaw())
				.setColor(message.getMember().getColor())
				.setFooter("In #" + message.getChannel().getName() + " • " + formatTime(message.getCreationTime()), null).build()).queue();
	}


	@Override
	public void invalidUsage(Message message) {
		message.getChannel().sendMessage(new EmbedBuilder()
				.setDescription("Invalid usage. Please enter a valid message ID")
				.setColor(message.getMember().getColor()).build()).queue();
	}
}