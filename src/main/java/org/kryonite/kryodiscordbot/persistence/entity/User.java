package org.kryonite.kryodiscordbot.persistence.entity;

import lombok.Data;

@Data(staticConstructor = "create")
public class User {

  private final long discordId;
  private final String minecraftName;
}
