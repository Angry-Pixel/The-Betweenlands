package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V700 extends Schema {
   public V700(int p_17985_, Schema p_17986_) {
      super(p_17985_, p_17986_);
   }

   protected static void registerMob(Schema p_17990_, Map<String, Supplier<TypeTemplate>> p_17991_, String p_17992_) {
      p_17990_.register(p_17991_, p_17992_, () -> {
         return V100.equipment(p_17990_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17994_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17994_);
      registerMob(p_17994_, map, "ElderGuardian");
      return map;
   }
}