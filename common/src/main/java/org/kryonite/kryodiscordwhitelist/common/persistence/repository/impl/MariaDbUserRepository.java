package org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.kryonite.kryodiscordwhitelist.common.persistence.entity.User;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;

public class MariaDbUserRepository implements UserRepository {

  protected static final String CREATE_USER_TABLE =
      "CREATE TABLE IF NOT EXISTS user "
          + "(id int primary key auto_increment, "
          + "discord_id bigint not null, "
          + "minecraft_name varchar(16) unique not null, "
          + "minecraft_uuid varchar(36) unique)";
  protected static final String INSERT_USER = "INSERT INTO user (discord_id, minecraft_name) VALUES(?, ?)";
  protected static final String GET_USER_BY_DISCORD_ID = "SELECT * FROM user WHERE minecraft_name = ?";
  protected static final String GET_USER_BY_NAME = "SELECT * FROM user WHERE minecraft_name = ?";
  protected static final String GET_USER_BY_MINECRAFT_UUID = "SELECT * FROM user WHERE minecraft_uuid = ?";
  protected static final String DELETE_USER_BY_MINECRAFT_NAME = "DELETE FROM user WHERE minecraft_name = ?";
  protected static final String UPDATE_USER = "UPDATE user SET minecraft_uuid = ? WHERE minecraft_name = ?";
  protected static final String UPDATE_USER_WHERE_DISCORD_ID =
      "UPDATE user SET minecraft_uuid = null, minecraft_name = ? "
          + "WHERE discord_id = ?";

  private final Connection connection;

  public MariaDbUserRepository(Connection connection) throws SQLException {
    this.connection = connection;

    try (PreparedStatement createTable = connection.prepareStatement(CREATE_USER_TABLE)) {
      createTable.executeUpdate();
    }
  }

  @Override
  public boolean addIfNotPresent(String minecraftName) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_NAME)) {
      preparedStatement.setString(1, minecraftName);

      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.first()) {
        return false;
      }
    }

    insertUser(-1, minecraftName);
    return true;
  }

  @Override
  public void save(User user) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_DISCORD_ID)) {
      preparedStatement.setLong(1, user.getDiscordId());

      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.first()) {
        updateUser(user);
      } else {
        insertUser(user.getDiscordId(), user.getMinecraftName());
      }
    }
  }

  private void updateUser(User user) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_WHERE_DISCORD_ID)) {
      preparedStatement.setString(1, user.getMinecraftName());
      preparedStatement.setLong(2, user.getDiscordId());

      preparedStatement.executeUpdate();
    }
  }

  private void insertUser(long discordId, String minecraftName) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER)) {
      preparedStatement.setLong(1, discordId);
      preparedStatement.setString(2, minecraftName);

      preparedStatement.executeUpdate();
    }
  }

  @Override
  public boolean removeUser(String minecraftName) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_BY_MINECRAFT_NAME)) {
      preparedStatement.setString(1, minecraftName);

      return preparedStatement.executeUpdate() > 0;
    }
  }

  @Override
  public Optional<User> get(UUID minecraftUuid) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_MINECRAFT_UUID)) {
      preparedStatement.setString(1, minecraftUuid.toString());

      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.first()) {
        return Optional.of(
            User.create(
                resultSet.getLong("discord_id"),
                resultSet.getString("minecraft_name"),
                UUID.fromString(resultSet.getString("minecraft_uuid"))
            )
        );
      }
    }

    return Optional.empty();
  }

  @Override
  public boolean updateIfPresent(UUID minecraftUuid, String minecraftName) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER)) {
      preparedStatement.setString(1, minecraftUuid.toString());
      preparedStatement.setString(2, minecraftName);

      return preparedStatement.executeUpdate() > 0;
    }
  }
}
