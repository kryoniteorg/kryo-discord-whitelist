package org.kryonite.kryodiscordbot.persistence.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.kryonite.kryodiscordbot.persistence.entity.User;
import org.kryonite.kryodiscordbot.persistence.repository.UserRepository;

@Slf4j
public class MariaDbUserRepository implements UserRepository {

  private static final String CREATE_USER_TABLE =
      "CREATE TABLE IF NOT EXISTS user " +
          "(discord_id bigint primary key, " +
          "minecraft_name varchar(16), " +
          "minecraft_uuid uuid)";

  private static final String INSERT_USER =
      "INSERT INTO user (discord_id, minecraft_name) VALUES(?, ?) " +
          "ON DUPLICATE KEY UPDATE minecraft_name = ?, minecraft_uuid = null";

  private final Connection connection;

  public MariaDbUserRepository(Connection connection) throws SQLException {
    this.connection = connection;

    try (PreparedStatement createTable = connection.prepareStatement(CREATE_USER_TABLE)) {
      createTable.executeUpdate();
    }
  }

  @Override
  public void save(User user) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER)) {
      preparedStatement.setLong(1, user.getDiscordId());
      preparedStatement.setString(2, user.getMinecraftName());
      preparedStatement.setString(3, user.getMinecraftName());

      preparedStatement.executeUpdate();
    } catch (SQLException error) {
      log.error("Failed to save user {}", user, error);
    }
  }
}
