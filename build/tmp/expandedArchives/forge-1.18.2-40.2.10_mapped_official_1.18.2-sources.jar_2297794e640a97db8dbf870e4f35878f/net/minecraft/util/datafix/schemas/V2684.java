package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V2684 extends NamespacedSchema {
   public V2684(int p_145856_, Schema p_145857_) {
      super(p_145856_, p_145857_);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_145859_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_145859_);
      p_145859_.registerSimple(map, "minecraft:sculk_sensor");
      return map;
   }
}