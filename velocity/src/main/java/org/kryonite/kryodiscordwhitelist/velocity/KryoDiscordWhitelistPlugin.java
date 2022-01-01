package org.kryonite.kryodiscordwhitelist.velocity;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository;
import org.kryonite.kryodiscordwhitelist.velocity.listener.PlayerListener;
import org.mariadb.jdbc.Driver;

@Slf4j
@Plugin(id = "kryo-discord-whitelist", name = "Kryo Discord Whitelist", authors = "Kryonite Labs", version = "0.1.0")
public class KryoDiscordWhitelistPlugin {

  private final ProxyServer server;

  @Inject
  public KryoDiscordWhitelistPlugin(ProxyServer server) {
    this.server = server;
  }

  @Subscribe
  public void onInitialize(ProxyInitializeEvent event) {
    UserRepository userRepository;
    try {
      DriverManager.registerDriver(new Driver());
      Connection connection = DriverManager.getConnection(System.getenv("CONNECTION_STRING"));
      userRepository = new MariaDbUserRepository(connection);
    } catch (SQLException exception) {
      log.error("Failed to setup UserRepository", exception);
      return;
    }

    server.getEventManager().register(this, new PlayerListener(userRepository));
  }
}
