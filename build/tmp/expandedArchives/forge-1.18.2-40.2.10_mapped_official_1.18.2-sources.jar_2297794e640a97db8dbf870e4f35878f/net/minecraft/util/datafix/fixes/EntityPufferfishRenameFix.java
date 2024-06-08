package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class EntityPufferfishRenameFix extends SimplestEntityRenameFix {
   public static final Map<String, String> RENAMED_IDS = ImmutableMap.<String, String>builder().put("minecraft:puffer_fish_spawn_egg", "minecraft:pufferfish_spawn_egg").build();

   public EntityPufferfishRenameFix(Schema p_15587_, boolean p_15588_) {
      super("EntityPufferfishRenameFix", p_15587_, p_15588_);
   }

   protected String rename(String p_15590_) {
      return Objects.equals("minecraft:puffer_fish", p_15590_) ? "minecraft:pufferfish" : p_15590_;
   }
}