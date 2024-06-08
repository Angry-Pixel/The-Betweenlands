package net.minecraft.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.gameevent.GameEvent;

public class GameEventTags {
   public static final TagKey<GameEvent> VIBRATIONS = create("vibrations");
   public static final TagKey<GameEvent> IGNORE_VIBRATIONS_SNEAKING = create("ignore_vibrations_sneaking");

   private static TagKey<GameEvent> create(String p_203853_) {
      return TagKey.create(Registry.GAME_EVENT_REGISTRY, new ResourceLocation(p_203853_));
   }
}