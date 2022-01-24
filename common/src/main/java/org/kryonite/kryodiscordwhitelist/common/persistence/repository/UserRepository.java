package org.kryonite.kryodiscordwhitelist.common.persistence.repository;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.kryonite.kryodiscordwhitelist.common.persistence.entity.User;

public interface UserRepository {

  /**
   * Adds the given minecraft name to the database if it wasn't already present.
   *
   * @param minecraftName The minecraft name to persist
   * @return true if the user got persisted and wasn't already present
   */
  boolean addIfNotPresent(String minecraftName) throws SQLException;

  /**
   * Saves the given {@link User} into the database. Only the {@link User#getDiscordId()}
   * and {@link User#getMinecraftName()} are used. The minecraft_uuid is set to null if the user existed previously.
   *
   * @param user The user which should be saved.
   */
  void save(User user) throws SQLException;

  /**
   * Removes the given minecraft name from the database if it was present.
   *
   * @param minecraftName The minecraft name to remove
   * @return true if the user got removed
   */
  boolean removeUser(String minecraftName) throws SQLException;

  /**
   * Returns the {@link User} with the given minecraft uuid if present.
   *
   * @param minecraftUuid The players minecraft uuid
   * @return an optional with the {@link User}
   */
  Optional<User> get(UUID minecraftUuid) throws SQLException;

  /**
   * Returns a list of all whitelisted {@link User}'s.
   *
   * @return an optional with a list containing all {@link User}'s
   */
  Optional<List<String>> getAllUsernames() throws SQLException;

  /**
   * Update the minecraft uuid if the player is present. Returns false if the player with the given minecraft name
   * did not exist.
   *
   * @param minecraftUuid The players minecraft uuid
   * @param minecraftName The players minecraft name
   * @return if the player was found and updated in the database
   */
  boolean updateIfPresent(UUID minecraftUuid, String minecraftName) throws SQLException;
}
