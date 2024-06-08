package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V1801 extends NamespacedSchema {
   public V1801(int p_17746_, Schema p_17747_) {
      super(p_17746_, p_17747_);
   }

   protected static void registerMob(Schema p_17751_, Map<String, Supplier<TypeTemplate>> p_17752_, String p_17753_) {
      p_17751_.register(p_17752_, p_17753_, () -> {
         return V100.equipment(p_17751_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17755_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17755_);
      registerMob(p_17755_, map, "minecraft:illager_beast");
      return map;
   }
}