package net.minecraft.world.entity;

import java.util.UUID;
import javax.annotation.Nullable;

public interface OwnableEntity {
   @Nullable
   UUID getOwnerUUID();

   @Nullable
   Entity getOwner();
}