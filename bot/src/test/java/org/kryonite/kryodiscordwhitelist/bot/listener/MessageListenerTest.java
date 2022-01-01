package org.kryonite.kryodiscordwhitelist.bot.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
  void shouldPersistUser() {
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
    verify(userRepositoryMock).save(User.create(discordId, discordMessage));
  }

  @Test
  void shouldNotProcessBotMessages() {
    // Arrange
    MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class, Answers.RETURNS_DEEP_STUBS);
    when(messageReceivedEvent.getAuthor().isBot()).thenReturn(false);

    // Act
    testee.onMessageReceived(messageReceivedEvent);

    // Assert
    verify(userRepositoryMock, never()).save(any());
  }
}