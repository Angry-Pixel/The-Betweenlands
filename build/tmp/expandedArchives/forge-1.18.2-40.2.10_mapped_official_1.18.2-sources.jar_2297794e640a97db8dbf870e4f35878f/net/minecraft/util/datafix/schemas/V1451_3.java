package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1451_3 extends NamespacedSchema {
   public V1451_3(int p_17444_, Schema p_17445_) {
      super(p_17444_, p_17445_);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17472_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17472_);
      p_17472_.registerSimple(map, "minecraft:egg");
      p_17472_.registerSimple(map, "minecraft:ender_pearl");
      p_17472_.registerSimple(map, "minecraft:fireball");
      p_17472_.register(map, "minecraft:potion", (p_17450_) -> {
         return DSL.optionalFields("Potion", References.ITEM_STACK.in(p_17472_));
      });
      p_17472_.registerSimple(map, "minecraft:small_fireball");
      p_17472_.registerSimple(map, "minecraft:snowball");
      p_17472_.registerSimple(map, "minecraft:wither_skull");
      p_17472_.registerSimple(map, "minecraft:xp_bottle");
      p_17472_.register(map, "minecraft:arrow", () -> {
         return DSL.optionalFields("inBlockState", References.BLOCK_STATE.in(p_17472_));
      });
      p_17472_.register(map, "minecraft:enderman", () -> {
         return DSL.optionalFields("carriedBlockState", References.BLOCK_STATE.in(p_17472_), V100.equipment(p_17472_));
      });
      p_17472_.register(map, "minecraft:falling_block", () -> {
         return DSL.optionalFields("BlockState", References.BLOCK_STATE.in(p_17472_), "TileEntityData", References.BLOCK_ENTITY.in(p_17472_));
      });
      p_17472_.register(map, "minecraft:spectral_arrow", () -> {
         return DSL.optionalFields("inBlockState", References.BLOCK_STATE.in(p_17472_));
      });
      p_17472_.register(map, "minecraft:chest_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(p_17472_), "Items", DSL.list(References.ITEM_STACK.in(p_17472_)));
      });
      p_17472_.register(map, "minecraft:commandblock_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(p_17472_));
      });
      p_17472_.register(map, "minecraft:furnace_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(p_17472_));
      });
      p_17472_.register(map, "minecraft:hopper_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(p_17472_), "Items", DSL.list(References.ITEM_STACK.in(p_17472_)));
      });
      p_17472_.register(map, "minecraft:minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(p_17472_));
      });
      p_17472_.register(map, "minecraft:spawner_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(p_17472_), References.UNTAGGED_SPAWNER.in(p_17472_));
      });
      p_17472_.register(map, "minecraft:tnt_minecart", () -> {
         return DSL.optionalFields("DisplayState", References.BLOCK_STATE.in(p_17472_));
      });
      return map;
   }
}