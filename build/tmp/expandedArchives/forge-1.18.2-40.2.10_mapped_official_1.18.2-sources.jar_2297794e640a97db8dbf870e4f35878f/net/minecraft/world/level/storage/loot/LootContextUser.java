package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public interface LootContextUser {
   default Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of();
   }

   default void validate(ValidationContext p_79022_) {
      p_79022_.validateUser(this);
   }
}