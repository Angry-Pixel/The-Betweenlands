package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V2505 extends NamespacedSchema {
   public V2505(int p_17870_, Schema p_17871_) {
      super(p_17870_, p_17871_);
   }

   protected static void registerMob(Schema p_17875_, Map<String, Supplier<TypeTemplate>> p_17876_, String p_17877_) {
      p_17875_.register(p_17876_, p_17877_, () -> {
         return V100.equipment(p_17875_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17879_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17879_);
      registerMob(p_17879_, map, "minecraft:piglin");
      return map;
   }
}