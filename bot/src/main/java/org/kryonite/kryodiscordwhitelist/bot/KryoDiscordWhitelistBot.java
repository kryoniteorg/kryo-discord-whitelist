package org.kryonite.kryodiscordwhitelist.bot;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import javax.security.auth.login.LoginException;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.kryonite.kryodiscordwhitelist.bot.listener.MessageListener;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository;
import org.mariadb.jdbc.Driver;

@Slf4j
public class KryoDiscordWhitelistBot extends ListenerAdapter {

  private final UserRepository userRepository;

  public KryoDiscordWhitelistBot() throws SQLException {
    setupForkJoinPoolParallelism();

    HikariConfig hikariConfig = new HikariConfig();

    // The driver class has to be set manually otherwise shadowJar will not probably change the import on relocation.
    hikariConfig.setDriverClassName(Driver.class.getName());
    log.info("Using {}", hikariConfig.getDriverClassName());

    hikariConfig.setJdbcUrl(getEnv("CONNECTION_STRING"));
    hikariConfig.setPoolName("kryo-discord-whitelist-pool");

    userRepository = new MariaDbUserRepository(new HikariDataSource(hikariConfig));
  }

  public KryoDiscordWhitelistBot(HikariDataSource hikariDataSource) throws SQLException {
    setupForkJoinPoolParallelism();
    userRepository = new MariaDbUserRepository(hikariDataSource);
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

  private void setupBot() throws LoginException, InterruptedException {
    JDABuilder.createDefault(getEnv("TOKEN"))
        .addEventListeners(new MessageListener(userRepository))
        .setActivity(Activity.playing("Minecraft"))
        .build()
        .awaitReady();
  }
}
