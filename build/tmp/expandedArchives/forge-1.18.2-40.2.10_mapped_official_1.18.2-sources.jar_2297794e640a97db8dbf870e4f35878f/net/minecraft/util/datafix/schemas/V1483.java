package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V1483 extends NamespacedSchema {
   public V1483(int p_17717_, Schema p_17718_) {
      super(p_17717_, p_17718_);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17720_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17720_);
      map.put("minecraft:pufferfish", map.remove("minecraft:puffer_fish"));
      return map;
   }
}