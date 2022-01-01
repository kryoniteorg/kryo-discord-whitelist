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
          + "(discord_id bigint primary key, "
          + "minecraft_name varchar(16), "
          + "minecraft_uuid uuid)";

  protected static final String INSERT_USER =
      "INSERT INTO user (discord_id, minecraft_name) VALUES(?, ?) "
          + "ON DUPLICATE KEY UPDATE minecraft_name = ?, minecraft_uuid = null";

  protected static final String GET_USER = "SELECT * FROM user WHERE minecraft_uuid = ?";
  protected static final String UPDATE_USER = "UPDATE user SET minecraft_uuid = ? WHERE minecraft_name = ?";

  private final Connection connection;

  public MariaDbUserRepository(Connection connection) throws SQLException {
    this.connection = connection;

    try (PreparedStatement createTable = connection.prepareStatement(CREATE_USER_TABLE)) {
      createTable.executeUpdate();
    }
  }

  @Override
  public void save(User user) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER)) {
      preparedStatement.setLong(1, user.getDiscordId());
      preparedStatement.setString(2, user.getMinecraftName());
      preparedStatement.setString(3, user.getMinecraftName());

      preparedStatement.executeUpdate();
    }
  }

  @Override
  public Optional<User> get(UUID minecraftUuid) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USER)) {
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
