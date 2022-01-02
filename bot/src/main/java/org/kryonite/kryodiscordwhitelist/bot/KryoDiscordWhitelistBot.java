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

  private final UserRepository userRepository;

  public KryoDiscordWhitelistBot() throws SQLException {
    setupForkJoinPoolParallelism();

    Connection connection = DriverManager.getConnection(getEnv("CONNECTION_STRING"));
    userRepository = new MariaDbUserRepository(connection);
  }

  public KryoDiscordWhitelistBot(Connection connection) throws SQLException {
    setupForkJoinPoolParallelism();
    userRepository = new MariaDbUserRepository(connection);
  }

  public static void main(String[] args) throws LoginException, InterruptedException, SQLException {
    KryoDiscordWhitelistBot kryoDiscordWhitelistBot = new KryoDiscordWhitelistBot();
    kryoDiscordWhitelistBot.setupBot();
  }

  // There's currently a bug with Java 17: https://github.com/DV8FromTheWorld/JDA/issues/1858
  private void setupForkJoinPoolParallelism() {
    int cores = Runtime.getRuntime().availableProcessors();
    if (cores <= 1) {
      System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
    }
  }

  private String getEnv(String name) {
    String connectionString = System.getenv(name);
    if (connectionString == null) {
      connectionString = System.getProperty(name);
    }

    return connectionString;
  }

  public void setupBot() throws LoginException, InterruptedException {
    JDABuilder.createDefault(getEnv("TOKEN"))
        .addEventListeners(new MessageListener(userRepository))
        .setActivity(Activity.playing("Minecraft"))
        .build()
        .awaitReady();
  }
}
