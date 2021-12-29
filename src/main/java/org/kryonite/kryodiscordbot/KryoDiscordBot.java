package org.kryonite.kryodiscordbot;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.kryonite.kryodiscordbot.listener.MessageListener;

public class KryoDiscordBot extends ListenerAdapter {

  public static void main(String[] args) throws LoginException, InterruptedException {
    JDABuilder.createLight(System.getenv("TOKEN"))
        .addEventListeners(new MessageListener())
        .setActivity(Activity.playing("Minecraft"))
        .build()
        .awaitReady();
  }
}
