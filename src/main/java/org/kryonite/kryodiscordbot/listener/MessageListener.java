package org.kryonite.kryodiscordbot.listener;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class MessageListener extends ListenerAdapter {

  private static final String CHANNEL_NAME = "whitelist";

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    if (event.getAuthor().isBot()) {
      return;
    }

    MessageChannel channel = event.getChannel();

    if (!CHANNEL_NAME.equals(channel.getName())) {
      return;
    }

    Message message = event.getMessage();
    String messageContent = message.getContentStripped();

    log.info(messageContent);

    message.delete()
        .and(channel.sendMessage("Test"))
        .queue();
  }
}
