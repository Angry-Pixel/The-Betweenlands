package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1451_2 extends NamespacedSchema {
   public V1451_2(int p_17436_, Schema p_17437_) {
      super(p_17436_, p_17437_);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_17442_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_17442_);
      p_17442_.register(map, "minecraft:piston", (p_17440_) -> {
         return DSL.optionalFields("blockState", References.BLOCK_STATE.in(p_17442_));
      });
      return map;
   }
}