package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V2707 extends NamespacedSchema {
   public V2707(int p_145894_, Schema p_145895_) {
      super(p_145894_, p_145895_);
   }

   protected static void registerEntity(Schema p_145899_, Map<String, Supplier<TypeTemplate>> p_145900_, String p_145901_) {
      p_145899_.register(p_145900_, p_145901_, () -> {
         return V100.equipment(p_145899_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_145903_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_145903_);
      registerEntity(p_145903_, map, "minecraft:marker");
      return map;
   }
}