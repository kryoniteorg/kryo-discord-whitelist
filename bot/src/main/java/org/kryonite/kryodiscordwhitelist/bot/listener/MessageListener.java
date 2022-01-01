package org.kryonite.kryodiscordwhitelist.bot.listener;

import java.sql.SQLException;
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

    boolean saveSuccessful = saveUserToDatabase(event, messageContent);

    message.delete()
        .and(event.getChannel().sendMessage(createReply(event, messageContent, saveSuccessful)))
        .queue();
  }

  private boolean saveUserToDatabase(@NotNull MessageReceivedEvent event, String messageContent) {
    User user = User.create(event.getAuthor().getIdLong(), messageContent, null);
    log.info("{}", user);
    try {
      userRepository.save(user);
    } catch (SQLException exception) {
      log.error("Failed to save user {}", user, exception);
      return false;
    }

    return true;
  }

  private String createReply(@NotNull MessageReceivedEvent event, String messageContent,
                             boolean saveSuccessful) {
    StringBuilder reply = new StringBuilder()
        .append("<@")
        .append(event.getAuthor().getIdLong())
        .append("> ")
        .append(messageContent);
    if (saveSuccessful) {
      reply.append(" wurde erfolgreich auf die Whitelist gesetzt.");
    } else {
      reply.append(" konnte nicht erfolgreich auf die Whitelist gesetzt werden.");
    }
    return reply.toString();
  }
}
