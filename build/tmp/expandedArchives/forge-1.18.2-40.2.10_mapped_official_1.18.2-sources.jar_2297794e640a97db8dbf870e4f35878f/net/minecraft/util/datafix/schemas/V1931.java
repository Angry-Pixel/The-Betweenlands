package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V1931 extends NamespacedSchema {
   public V1931(int p_17822_, Schema p_17823_) {
      super(p_17822_, p_17823_);
   }

   protected static void registerMob(Schema p_17827_, Map<String, Supplier<TypeTemplate>> p_17828_, String p_17829_) {
      p_17827_.register(p_17828_, p_17829_, () -> {
         return V100.equipment(p_17827_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17831_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17831_);
      registerMob(p_17831_, map, "minecraft:fox");
      return map;
   }
}