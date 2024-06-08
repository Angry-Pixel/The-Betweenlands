package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V2688 extends NamespacedSchema {
   public V2688(int p_145872_, Schema p_145873_) {
      super(p_145872_, p_145873_);
   }

   protected static void registerMob(Schema p_145877_, Map<String, Supplier<TypeTemplate>> p_145878_, String p_145879_) {
      p_145877_.register(p_145878_, p_145879_, () -> {
         return V100.equipment(p_145877_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_145881_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_145881_);
      registerMob(p_145881_, map, "minecraft:glow_squid");
      p_145881_.registerSimple(map, "minecraft:glow_item_frame");
      return map;
   }
}