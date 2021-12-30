package org.kryonite.kryodiscordbot.persistence.repository;

import org.kryonite.kryodiscordbot.persistence.entity.User;

public interface UserRepository {

  /**
   * Saves the given {@link User} into the database. Only the {@link User#getDiscordId()} and {@link User#getMinecraftName()}
   * are used. The minecraft_uuid is set to null if the user existed previously.
   *
   * @param user The user which should be saved.
   */
  void save(User user);
}
