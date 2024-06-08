package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V1909 extends NamespacedSchema {
   public V1909(int p_17782_, Schema p_17783_) {
      super(p_17782_, p_17783_);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_17785_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_17785_);
      p_17785_.registerSimple(map, "minecraft:jigsaw");
      return map;
   }
}