package org.kryonite.kryodiscordwhitelist.velocity;


import com.google.inject.Inject;
import com.rabbitmq.client.Address;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository;
import org.kryonite.kryodiscordwhitelist.velocity.command.WhitelistCommand;
import org.kryonite.kryodiscordwhitelist.velocity.listener.PlayerListener;
import org.kryonite.kryodiscordwhitelist.velocity.messaging.MessagingController;
import org.kryonite.kryomessaging.api.MessagingService;
import org.kryonite.kryomessaging.service.DefaultActiveMqConnectionFactory;
import org.kryonite.kryomessaging.service.DefaultMessagingService;
import org.mariadb.jdbc.Driver;

@Slf4j
@AllArgsConstructor
@Plugin(id = "kryo-discord-whitelist", name = "Kryo Discord Whitelist", authors = "Kryonite Labs", version = "1.0.0")
public class KryoDiscordWhitelistPlugin {

  private final ProxyServer server;
  private HikariDataSource hikariDataSource;
  private MessagingService messagingService;

  @Inject
  public KryoDiscordWhitelistPlugin(ProxyServer server) {
    this.server = server;
  }

  @Subscribe
  public void onInitialize(ProxyInitializeEvent event) {
    MessagingController messagingController;
    try {
      messagingController = setupMessagingController();
    } catch (IOException | TimeoutException exception) {
      log.error("Failed to setup MessagingService", exception);
      return;
    }

    UserRepository userRepository;
    try {
      setupHikariDataSource();
      userRepository = new MariaDbUserRepository(hikariDataSource);
    } catch (SQLException exception) {
      log.error("Failed to setup UserRepository", exception);
      return;
    }

    server.getEventManager().register(this, new PlayerListener(userRepository));

    setupCommands(messagingController, userRepository, server);
  }

  @NotNull
  private MessagingController setupMessagingController() throws IOException, TimeoutException {
    MessagingController messagingController;
    if (messagingService == null) {
      messagingService = new DefaultMessagingService(new DefaultActiveMqConnectionFactory(
          List.of(Address.parseAddress(getEnv("RABBITMQ_ADDRESS"))),
          getEnv("RABBITMQ_USERNAME"),
          getEnv("RABBITMQ_PASSWORD")
      ));
    }

    messagingController = new MessagingController(messagingService, server, getEnv("SERVER_NAME"));
    messagingController.setupPlayerRemovedFromWhitelist();
    return messagingController;
  }

  private void setupHikariDataSource() throws SQLException {
    if (hikariDataSource == null) {
      DriverManager.registerDriver(new Driver());
      HikariConfig hikariConfig = new HikariConfig();
      hikariConfig.setJdbcUrl(getEnv("CONNECTION_STRING"));
      hikariConfig.setPoolName("kryo-discord-whitelist-pool");
      hikariDataSource = new HikariDataSource(hikariConfig);
    }
  }

  private String getEnv(String name) {
    String connectionString = System.getenv(name);
    if (connectionString == null) {
      connectionString = System.getProperty(name);
    }

    return connectionString;
  }

  private void setupCommands(MessagingController messagingController, UserRepository userRepository,
                             ProxyServer server) {
    CommandMeta whitelist = this.server.getCommandManager().metaBuilder("wl").build();
    this.server.getCommandManager().register(whitelist,
        new WhitelistCommand(userRepository, messagingController, server));
  }
}
