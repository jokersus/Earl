package Commands;

import Logs.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Anime extends Command {

	private static HashMap<Integer, Color> embedColors = new HashMap<>();
	private Message message;

	public Anime() {
		super("Anime", "Does something anime related. I'll edit this later");
	}

	@Override
	public void run(Message message) {
		super.run(message);
		this.message = message;
		JSONObject toSend;
		JSONObject query = new JSONObject();
		JSONObject variables = new JSONObject();
		if (message.getContentRaw().length() < 6) {
			query.put("query", "{\n" +
					"  Page(perPage: 100) {\n" +
					"    media(type: ANIME, status: RELEASING) {\n" +
					"      title {\n" +
					"        userPreferred\n" +
					"      }\n" +
					"      nextAiringEpisode {\n" +
					"        timeUntilAiring\n" +
					"      }\n" +
					"    }\n" +
					"  }\n" +
					"}\n");
		} else {
			query.put("query", "query ($search: String, $status: MediaStatus) {\n" +
					"  Media(type:ANIME status:$status search:$search) {\n" +
					"    id\n" +
					"    siteUrl\n" +
					"    coverImage {\n" +
					"      medium\n" +
					"    }\n" +
					"    title {\n" +
					"      userPreferred\n" +
					"    }\n" +
					"    nextAiringEpisode {\n" +
					"      timeUntilAiring\n" +
					"    }\n" +
					"  }\n" +
					"}");
			variables.put("search", message.getContentRaw().substring(6));
			variables.put("status", "RELEASING");
		}

		toSend = execute(query, variables);
//		System.out.println(toSend.toString());

		if (toSend.getJSONObject("data").isNull("Media")) {
			variables.put("status", "NOT_YET_RELEASED");
			toSend = execute(query, variables);
//			System.out.println(toSend.toString(4));
		}

		if (handleError(toSend)) {
//			System.out.println("error");
			return;
		}

		message.getChannel().sendMessage(buildMessage(toSend.getJSONObject("data"))).queue();
	}

	private JSONObject execute(JSONObject query, JSONObject variables) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject toSend = new JSONObject();
		try {
			JSONObject object = new JSONObject();
			object.put("query", query.get("query").toString());
			object.put("variables", variables.toString());
			HttpPost request = new HttpPost("https://graphql.anilist.co");
			StringEntity params = new StringEntity(object.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			toSend = new JSONObject(EntityUtils.toString(response.getEntity()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return toSend;
	}

	private boolean handleError(JSONObject object) {
		if (object.has("errors")) {
			message.getChannel().sendMessage(new EmbedBuilder()
					.setDescription("Error: " + object.getJSONArray("errors").getJSONObject(0).getString("message"))
					.setColor(Color.RED)
					.build()).queue();
			return true;
		}
		return false;
	}

	public static void init() {
		embedColors.put(100166, new Color(45, 135, 103)); // bnha
		embedColors.put(21127, new Color(251, 250, 248)); // s;g0
		embedColors.put(99423, new Color(255, 31, 0)); // franxx
		embedColors.put(100183, new Color(240, 128, 164)); // aggo
		embedColors.put(99147, new Color(89, 84, 62)); // snk
		embedColors.put(99693, new Color(249, 0, 0)); // persona
		embedColors.put(101925, new Color(215, 226, 245)); // gintama
		embedColors.put(99586, new Color(250, 234, 219)); // harukana
	}

	private MessageEmbed buildMessage(JSONObject toSend) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		ArrayList<AnimeObject> anime = new ArrayList<>();
		if (toSend.has("Page")) {
			JSONArray array = toSend.getJSONObject("Page").getJSONArray("media");

			for (int iterator = 0; iterator < array.length(); iterator++) {
				JSONObject object = array.getJSONObject(iterator);
				if (!object.isNull("nextAiringEpisode")) {
					if (object.getJSONObject("nextAiringEpisode").get("timeUntilAiring") != null &&
							object.getJSONObject("nextAiringEpisode").getInt("timeUntilAiring") < 86400) {
						anime.add(new AnimeObject(object.getJSONObject("title").getString("userPreferred"), secondOffset(object.getJSONObject("nextAiringEpisode").getInt("timeUntilAiring"))));
					}
				}
			}

			for (AnimeObject animeObject : anime) {
				embedBuilder.addField(animeObject.getName(), animeObject.date.getDay() + " days, " +
						animeObject.date.getHour() + " hours, " +
						animeObject.date.getMinute() + " minutes, " +
						animeObject.date.getSecond() + " seconds.", false)
						.setColor(Color.GREEN)
						.setTitle("Today's schedule:", "http://anichart.net/");
			}

			return embedBuilder.build();
		} else {

			if (toSend.has("Media")) {
				toSend = toSend.getJSONObject("Media");
			}

			embedBuilder.setColor(embedColors.getOrDefault(toSend.getInt("id"), Color.GREEN));

			if (!toSend.isNull("nextAiringEpisode") && !toSend.getJSONObject("nextAiringEpisode").isNull("timeUntilAiring")) {
				int seconds = toSend.getJSONObject("nextAiringEpisode").getInt("timeUntilAiring");
				seconds = secondOffset(seconds);
				return embedBuilder
						.setTitle(toSend.getJSONObject("title").getString("userPreferred"), toSend.getString("siteUrl"))
						.addField("Next episode in",
								seconds / 86400 + " days, " +
										((seconds % 86400) / 60) / 60 + " hours, " +
										((seconds % 86400) % 3600 / 60) + " minutes, " +
										((seconds % 86400) % 3600) % 60 + " seconds.", false)
						.setThumbnail(toSend.getJSONObject("coverImage").getString("medium"))
						.build();
			} else {
				return embedBuilder
						.setTitle(toSend.getJSONObject("title").getString("userPreferred"), toSend.getString("siteUrl"))
						.addField("Next episode", "unknown", false)
						.setThumbnail(toSend.getJSONObject("coverImage").getString("medium"))
						.setColor(Color.ORANGE)
						.build();
			}
		}
	}

	private int secondOffset(int second) {
		if (second >= 601200 && second <= 604800) {
			return 604800 - second + 3600;
		} else {
			return second + 3600;
		}
	}
}


class AnimeObject {

	private String name;
	ConvenientDate date;

	AnimeObject(String name, int timeUntilAiring) {
		this.name = name;
		this.date = new ConvenientDate(timeUntilAiring);
	}

	public String getName() {
		return this.name;
	}

	class ConvenientDate {


		private int day;
		private int hour;
		private int minute;
		private int second;

		ConvenientDate(int seconds) {
			this.day = seconds / 86400;
			this.hour = ((seconds % 86400) / 60) / 60;
			this.minute = ((seconds % 86400) % 3600 / 60);
			this.second = ((seconds % 86400) % 3600) % 60;
		}

		int getDay() {
			return this.day;
		}

		int getHour() {
			return this.hour;
		}

		int getMinute() {
			return this.minute;
		}

		int getSecond() {
			return this.second;
		}
	}
}
