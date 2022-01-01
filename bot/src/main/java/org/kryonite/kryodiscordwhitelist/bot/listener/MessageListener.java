package org.kryonite.kryodiscordwhitelist.bot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.kryonite.kryodiscordwhitelist.common.persistence.entity.User;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
public class MessageListener extends ListenerAdapter {

  private static final String CHANNEL_NAME = "whitelist";

  private final UserRepository userRepository;

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    if (event.getAuthor().isBot() || !CHANNEL_NAME.equals(event.getChannel().getName())) {
      return;
    }

    Message message = event.getMessage();
    String messageContent = message.getContentStripped();

    saveUserToDatabase(event, messageContent);

    message.delete()
        .and(event.getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + "> " + messageContent
            + " wurde erfolgreich auf die Whitelist gesetzt."))
        .queue();
  }

  private void saveUserToDatabase(@NotNull MessageReceivedEvent event, String messageContent) {
    User user = User.create(event.getAuthor().getIdLong(), messageContent);
    log.info("{}", user);
    userRepository.save(user);
  }
}
