package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1800 extends NamespacedSchema {
   public V1800(int p_17732_, Schema p_17733_) {
      super(p_17732_, p_17733_);
   }

   protected static void registerMob(Schema p_17740_, Map<String, Supplier<TypeTemplate>> p_17741_, String p_17742_) {
      p_17740_.register(p_17741_, p_17742_, () -> {
         return V100.equipment(p_17740_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17744_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17744_);
      registerMob(p_17744_, map, "minecraft:panda");
      p_17744_.register(map, "minecraft:pillager", (p_17738_) -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(p_17744_)), V100.equipment(p_17744_));
      });
      return map;
   }
}