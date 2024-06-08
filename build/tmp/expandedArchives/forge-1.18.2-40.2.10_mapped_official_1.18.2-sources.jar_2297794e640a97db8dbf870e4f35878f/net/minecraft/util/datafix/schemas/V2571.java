package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V2571 extends NamespacedSchema {
   public V2571(int p_145845_, Schema p_145846_) {
      super(p_145845_, p_145846_);
   }

   protected static void registerMob(Schema p_145850_, Map<String, Supplier<TypeTemplate>> p_145851_, String p_145852_) {
      p_145850_.register(p_145851_, p_145852_, () -> {
         return V100.equipment(p_145850_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_145854_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_145854_);
      registerMob(p_145854_, map, "minecraft:goat");
      return map;
   }
}