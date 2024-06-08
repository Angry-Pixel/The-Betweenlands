package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1470 extends NamespacedSchema {
   public V1470(int p_17698_, Schema p_17699_) {
      super(p_17698_, p_17699_);
   }

   protected static void registerMob(Schema p_17706_, Map<String, Supplier<TypeTemplate>> p_17707_, String p_17708_) {
      p_17706_.register(p_17707_, p_17708_, () -> {
         return V100.equipment(p_17706_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17710_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17710_);
      registerMob(p_17710_, map, "minecraft:turtle");
      registerMob(p_17710_, map, "minecraft:cod_mob");
      registerMob(p_17710_, map, "minecraft:tropical_fish");
      registerMob(p_17710_, map, "minecraft:salmon_mob");
      registerMob(p_17710_, map, "minecraft:puffer_fish");
      registerMob(p_17710_, map, "minecraft:phantom");
      registerMob(p_17710_, map, "minecraft:dolphin");
      registerMob(p_17710_, map, "minecraft:drowned");
      p_17710_.register(map, "minecraft:trident", (p_17704_) -> {
         return DSL.optionalFields("inBlockState", References.BLOCK_STATE.in(p_17710_));
      });
      return map;
   }
}