package org.kryonite.kryodiscordbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.kryonite.kryodiscordbot.listener.MessageListener;
import org.kryonite.kryodiscordbot.persistence.repository.UserRepository;
import org.kryonite.kryodiscordbot.persistence.repository.impl.MariaDbUserRepository;

public class KryoDiscordBot extends ListenerAdapter {

  public static void main(String[] args) throws LoginException, InterruptedException, SQLException {
    Connection connection = DriverManager.getConnection(System.getenv("CONNECTION_STRING"));
    UserRepository userRepository = new MariaDbUserRepository(connection);

    JDABuilder.createDefault(System.getenv("TOKEN"))
        .addEventListeners(new MessageListener(userRepository))
        .setActivity(Activity.playing("Minecraft"))
        .build()
        .awaitReady();
  }
}
