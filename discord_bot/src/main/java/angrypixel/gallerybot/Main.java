package angrypixel.gallerybot;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;

public class Main {
	private static final String ENV_BOT_TOKEN = "DISCORD_BOT_TOKEN";
	private static final String ENV_CHANNEL_TOKEN = "DISCORD_CHANNEL_ID";
	private static final String ENV_CURRENT_INDEX_REVISION = "CURRENT_INDEX_REVISION";
	private static final String ENV_PREVIOUS_INDEX_REVISION = "PREVIOUS_INDEX_REVISION";

	public static void main(String[] args) {
		String token = System.getenv(ENV_BOT_TOKEN);
		if(token == null) {
			System.out.println(ENV_BOT_TOKEN + " environment variable does not exist");
			System.exit(1);
		}

		String channelIdStr = System.getenv(ENV_CHANNEL_TOKEN);
		if(channelIdStr == null) {
			System.out.println(ENV_CHANNEL_TOKEN + " environment variable does not exist");
			System.exit(1);
		}

		Snowflake channelId = null;
		try {
			channelId = Snowflake.of(Long.parseLong(channelIdStr));
		} catch(NumberFormatException ex) {
			System.out.println("Invalid channel id '" + channelIdStr + "'");
			System.exit(1);
		}

		String currentIndexRevision = System.getenv(ENV_CURRENT_INDEX_REVISION);
		if(currentIndexRevision == null) {
			System.out.println(ENV_CURRENT_INDEX_REVISION + " environment variable does not exist");
			System.exit(1);
		}

		String previousIndexRevision = System.getenv(ENV_PREVIOUS_INDEX_REVISION);
		if(previousIndexRevision == null) {
			System.out.println(ENV_PREVIOUS_INDEX_REVISION + " environment variable does not exist");
			System.exit(1);
		}

		Map<String, Submission> currentIndex = null;
		try {
			currentIndex = parseSubmissions(currentIndexRevision);
		} catch(Exception ex) {
			System.out.println("Failed parsing current index");
			ex.printStackTrace();
			System.exit(1);
		}

		Map<String, Submission> previousIndex = null;
		try {
			previousIndex = parseSubmissions(previousIndexRevision);
		} catch(Exception ex) {
			System.out.println("Failed parsing previous index");
			ex.printStackTrace();
			System.exit(1);
		}

		GatewayDiscordClient client = null;
		try {
			client = DiscordClientBuilder.create(token).build().login().block();
		} catch(Exception ex) {
			System.out.println("Bot failed login");
			ex.printStackTrace();
			System.exit(1);
		}

		MessageChannel channel = null;
		try {
			channel = client.getChannelById(channelId).cast(MessageChannel.class).block();
		} catch(Exception ex) {
			System.out.println("Bot could not find channel");
			ex.printStackTrace();
			System.exit(1);
		}

		System.out.println("New submissions:");
		
		for(Submission submission : currentIndex.values()) {
			if(!previousIndex.containsKey(submission.url)) {
				System.out.println(submission.url);
				
				String description;
				if(submission.description != null) {
					if(!submission.description.startsWith("\"")) {
						description = "\"" + submission.description + "\"";
					} else {
						description = submission.description;
					}
				} else {
					description = null;
				}

				channel.createEmbed(spec -> {
					spec
					.setColor(Color.BLUE)
					.setTitle(submission.title)
					.setDescription("by " + submission.author)
					.setImage(submission.url)
					.setTimestamp(Instant.now());
					if(description != null) {
						spec.addField("Description:", description, false);
					}
				}).block();
			}
		}

		try {
			client.logout().block();
		} catch(Exception ex) {
			System.out.println("Bot could not log out");
			ex.printStackTrace();
			System.exit(1);
		}
	}

	private static Map<String, Submission> parseSubmissions(String index) {
		Map<String, Submission> submissions = new LinkedHashMap<>();

		Gson gson = new Gson();

		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(index);

		JsonArray indexJson = jsonElement.getAsJsonArray();

		indexJson.forEach(galleryElement -> {
			JsonObject galleryJson = galleryElement.getAsJsonObject();

			JsonArray submissionsJson = galleryJson.get("index").getAsJsonArray();

			submissionsJson.forEach(submissionElement -> {
				Submission submission = gson.fromJson(submissionElement, Submission.class);

				if(submission.author != null && submission.title != null && submission.url != null) {
					submissions.put(submission.url, submission);
				}
			});
		});

		return submissions;
	}
}
