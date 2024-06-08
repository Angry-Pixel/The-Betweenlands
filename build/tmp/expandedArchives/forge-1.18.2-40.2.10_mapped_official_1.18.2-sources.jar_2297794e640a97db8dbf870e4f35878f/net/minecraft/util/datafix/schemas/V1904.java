package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V1904 extends NamespacedSchema {
   public V1904(int p_17757_, Schema p_17758_) {
      super(p_17757_, p_17758_);
   }

   protected static void registerMob(Schema p_17762_, Map<String, Supplier<TypeTemplate>> p_17763_, String p_17764_) {
      p_17762_.register(p_17763_, p_17764_, () -> {
         return V100.equipment(p_17762_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17766_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17766_);
      registerMob(p_17766_, map, "minecraft:cat");
      return map;
   }
}