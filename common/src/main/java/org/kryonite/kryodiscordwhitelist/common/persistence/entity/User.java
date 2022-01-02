package org.kryonite.kryodiscordwhitelist.common.persistence.entity;

import java.util.UUID;
import lombok.Data;

@Data(staticConstructor = "create")
public class User {

  private final long discordId;
  private final String minecraftName;
  private final UUID minecraftUuid;
}
