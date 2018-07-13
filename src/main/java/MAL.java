import net.dv8tion.jda.core.JDA;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class MAL implements Runnable {
	private JDA jda;

	public MAL(JDA jda) {
		this.jda = jda;
	}

	@Override
	public void run() {
		URLConnection connection;
		while (true) {
			try {
				connection = new URL("https://myanimelist.net").openConnection();
				Scanner scanner = new Scanner(connection.getInputStream());
				jda.getGuildById("292277485310312448").getTextChannelById("293991933284712448").sendMessage(jda.getGuildById("292277485310312448").getMemberById("275331662865367040").getAsMention() + " MAL is up again").queue();
				break;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}