package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V2568 extends NamespacedSchema {
   public V2568(int p_17963_, Schema p_17964_) {
      super(p_17963_, p_17964_);
   }

   protected static void registerMob(Schema p_17968_, Map<String, Supplier<TypeTemplate>> p_17969_, String p_17970_) {
      p_17968_.register(p_17969_, p_17970_, () -> {
         return V100.equipment(p_17968_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17972_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17972_);
      registerMob(p_17972_, map, "minecraft:piglin_brute");
      return map;
   }
}