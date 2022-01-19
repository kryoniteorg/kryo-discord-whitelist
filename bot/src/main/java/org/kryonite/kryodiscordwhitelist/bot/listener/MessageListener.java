package org.kryonite.kryodiscordwhitelist.bot.listener;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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
    message.delete()
        .queue();

    if (messageContent.length() > 16) {
      event.getChannel()
          .sendMessage(createReply(event, messageContent, SaveUserResult.NAME_TO_LONG))
          .queue();
      return;
    }

    SaveUserResult saveUserResult = saveUserToDatabase(event, messageContent);
    event.getChannel()
        .sendMessage(createReply(event, messageContent, saveUserResult))
        .queue();
  }

  private SaveUserResult saveUserToDatabase(@NotNull MessageReceivedEvent event, String messageContent) {
    User user = User.create(event.getAuthor().getIdLong(), messageContent, null);
    log.info("{}", user);
    try {
      userRepository.save(user);
    } catch (SQLException exception) {
      if (exception instanceof SQLIntegrityConstraintViolationException) {
        return SaveUserResult.DUPLICATE;
      }

      log.error("Failed to save user {}", user, exception);
      return SaveUserResult.FAILED;
    }

    return SaveUserResult.SUCCESSFUL;
  }

  private String createReply(@NotNull MessageReceivedEvent event, String messageContent,
                             SaveUserResult saveUserResult) {
    StringBuilder reply = new StringBuilder()
        .append("<@")
        .append(event.getAuthor().getIdLong())
        .append("> ")
        .append(messageContent);

    switch (saveUserResult) {
      case SUCCESSFUL -> reply.append(" wurde erfolgreich auf die Whitelist gesetzt.");
      case DUPLICATE -> reply.append(" befindet sich bereits auf der Whitelist.");
      case NAME_TO_LONG -> reply.append(" ist kein Minecraft Username.");
      case FAILED -> reply.append(" konnte nicht erfolgreich auf die Whitelist gesetzt werden.");
      default -> throw new IllegalStateException("Unexpected value: " + saveUserResult);
    }
    return reply.toString();
  }

  private enum SaveUserResult {
    SUCCESSFUL,
    DUPLICATE,
    NAME_TO_LONG,
    FAILED
  }
}
