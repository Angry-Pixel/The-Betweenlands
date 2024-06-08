package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V143 extends Schema {
   public V143(int p_17415_, Schema p_17416_) {
      super(p_17415_, p_17416_);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17418_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17418_);
      map.remove("TippedArrow");
      return map;
   }
}