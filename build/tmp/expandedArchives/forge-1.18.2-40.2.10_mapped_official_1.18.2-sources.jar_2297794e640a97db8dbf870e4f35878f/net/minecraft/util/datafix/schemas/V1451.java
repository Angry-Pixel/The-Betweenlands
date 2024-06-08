package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1451 extends NamespacedSchema {
   public V1451(int p_17420_, Schema p_17421_) {
      super(p_17420_, p_17421_);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_17425_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_17425_);
      p_17425_.register(map, "minecraft:trapped_chest", () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(p_17425_)));
      });
      return map;
   }
}