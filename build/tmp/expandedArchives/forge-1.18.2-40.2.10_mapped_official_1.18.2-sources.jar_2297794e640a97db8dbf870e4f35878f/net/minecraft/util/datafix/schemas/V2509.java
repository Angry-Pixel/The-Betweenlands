package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V2509 extends NamespacedSchema {
   public V2509(int p_17881_, Schema p_17882_) {
      super(p_17881_, p_17882_);
   }

   protected static void registerMob(Schema p_17886_, Map<String, Supplier<TypeTemplate>> p_17887_, String p_17888_) {
      p_17886_.register(p_17887_, p_17888_, () -> {
         return V100.equipment(p_17886_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17890_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17890_);
      map.remove("minecraft:zombie_pigman");
      registerMob(p_17890_, map, "minecraft:zombified_piglin");
      return map;
   }
}