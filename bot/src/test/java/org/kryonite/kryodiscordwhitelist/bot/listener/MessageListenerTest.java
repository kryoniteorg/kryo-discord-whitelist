package org.kryonite.kryodiscordwhitelist.bot.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryodiscordwhitelist.common.persistence.entity.User;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageListenerTest {

  @InjectMocks
  private MessageListener testee;

  @Mock
  private UserRepository userRepositoryMock;

  @Test
  void shouldPersistUser() throws SQLException {
    // Arrange
    long discordId = 123456789123L;
    String discordMessage = "Test1234";

    MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class, Answers.RETURNS_DEEP_STUBS);
    when(messageReceivedEvent.getAuthor().isBot()).thenReturn(false);
    when(messageReceivedEvent.getAuthor().getIdLong()).thenReturn(discordId);
    when(messageReceivedEvent.getChannel().getName()).thenReturn("whitelist");
    when(messageReceivedEvent.getMessage().getContentStripped()).thenReturn(discordMessage);

    // Act
    testee.onMessageReceived(messageReceivedEvent);

    // Assert
    verify(userRepositoryMock).save(User.create(discordId, discordMessage, null));
  }

  @Test
  void shouldNotPersistUser_WhenAlreadyOnWhitelist() throws SQLException {
    // Arrange
    long discordId = 123456789123L;
    String discordMessage = "Test1234";

    MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class, Answers.RETURNS_DEEP_STUBS);
    when(messageReceivedEvent.getAuthor().isBot()).thenReturn(false);
    when(messageReceivedEvent.getAuthor().getIdLong()).thenReturn(discordId);
    when(messageReceivedEvent.getChannel().getName()).thenReturn("whitelist");
    when(messageReceivedEvent.getMessage().getContentStripped()).thenReturn(discordMessage);
    doThrow(SQLIntegrityConstraintViolationException.class).when(userRepositoryMock).save(any());

    // Act
    testee.onMessageReceived(messageReceivedEvent);

    // Assert
    verify(userRepositoryMock).save(User.create(discordId, discordMessage, null));
  }

  @Test
  void shouldHandlePersistenceErrors() throws SQLException {
    // Arrange
    long discordId = 123456789123L;
    String discordMessage = "Test1234";

    MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class, Answers.RETURNS_DEEP_STUBS);
    when(messageReceivedEvent.getAuthor().isBot()).thenReturn(false);
    when(messageReceivedEvent.getAuthor().getIdLong()).thenReturn(discordId);
    when(messageReceivedEvent.getChannel().getName()).thenReturn("whitelist");
    when(messageReceivedEvent.getMessage().getContentStripped()).thenReturn(discordMessage);

    doThrow(SQLException.class).when(userRepositoryMock).save(any());

    // Act
    testee.onMessageReceived(messageReceivedEvent);

    // Assert
    verify(messageReceivedEvent.getMessage().delete().and(any())).queue();
  }

  @Test
  void shouldNotProcessBotMessages() throws SQLException {
    // Arrange
    MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class, Answers.RETURNS_DEEP_STUBS);
    when(messageReceivedEvent.getAuthor().isBot()).thenReturn(false);

    // Act
    testee.onMessageReceived(messageReceivedEvent);

    // Assert
    verify(userRepositoryMock, never()).save(any());
  }
}
