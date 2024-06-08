package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V2519 extends NamespacedSchema {
   public V2519(int p_17892_, Schema p_17893_) {
      super(p_17892_, p_17893_);
   }

   protected static void registerMob(Schema p_17897_, Map<String, Supplier<TypeTemplate>> p_17898_, String p_17899_) {
      p_17897_.register(p_17898_, p_17899_, () -> {
         return V100.equipment(p_17897_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17901_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17901_);
      registerMob(p_17901_, map, "minecraft:strider");
      return map;
   }
}