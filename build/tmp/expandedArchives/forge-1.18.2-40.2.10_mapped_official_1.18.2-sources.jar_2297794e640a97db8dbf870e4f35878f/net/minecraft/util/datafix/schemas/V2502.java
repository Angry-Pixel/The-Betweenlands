package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V2502 extends NamespacedSchema {
   public V2502(int p_17859_, Schema p_17860_) {
      super(p_17859_, p_17860_);
   }

   protected static void registerMob(Schema p_17864_, Map<String, Supplier<TypeTemplate>> p_17865_, String p_17866_) {
      p_17864_.register(p_17865_, p_17866_, () -> {
         return V100.equipment(p_17864_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17868_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17868_);
      registerMob(p_17868_, map, "minecraft:hoglin");
      return map;
   }
}