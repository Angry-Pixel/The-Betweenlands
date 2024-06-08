package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V2522 extends NamespacedSchema {
   public V2522(int p_17933_, Schema p_17934_) {
      super(p_17933_, p_17934_);
   }

   protected static void registerMob(Schema p_17938_, Map<String, Supplier<TypeTemplate>> p_17939_, String p_17940_) {
      p_17938_.register(p_17939_, p_17940_, () -> {
         return V100.equipment(p_17938_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17942_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17942_);
      registerMob(p_17942_, map, "minecraft:zoglin");
      return map;
   }
}