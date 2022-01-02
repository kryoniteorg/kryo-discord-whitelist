package org.kryonite.kryodiscordwhitelist.bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.kryonite.kryodiscordwhitelist.bot.listener.MessageListener;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository;

public class KryoDiscordWhitelistBot extends ListenerAdapter {

  public static void main(String[] args) throws LoginException, InterruptedException, SQLException {
    setupForkJoinPoolParallelism();

    Connection connection = DriverManager.getConnection(System.getenv("CONNECTION_STRING"));
    UserRepository userRepository = new MariaDbUserRepository(connection);

    JDABuilder.createDefault(System.getenv("TOKEN"))
        .addEventListeners(new MessageListener(userRepository))
        .setActivity(Activity.playing("Minecraft"))
        .build()
        .awaitReady();
  }

  // There's currently a bug with Java 17: https://github.com/DV8FromTheWorld/JDA/issues/1858
  private static void setupForkJoinPoolParallelism() {
    int cores = Runtime.getRuntime().availableProcessors();
    if (cores <= 1) {
      System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
    }
  }
}
