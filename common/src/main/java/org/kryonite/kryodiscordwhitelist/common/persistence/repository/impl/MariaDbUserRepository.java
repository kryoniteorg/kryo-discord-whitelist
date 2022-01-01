package org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
}
