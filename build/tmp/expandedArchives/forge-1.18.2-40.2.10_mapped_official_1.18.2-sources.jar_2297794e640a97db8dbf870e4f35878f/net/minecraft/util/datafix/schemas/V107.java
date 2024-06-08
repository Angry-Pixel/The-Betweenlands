package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V107 extends Schema {
   public V107(int p_17386_, Schema p_17387_) {
      super(p_17386_, p_17387_);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17389_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17389_);
      map.remove("Minecart");
      return map;
   }
}