package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Set;

public class WallPropertyFix extends DataFix {
   private static final Set<String> WALL_BLOCKS = ImmutableSet.of("minecraft:andesite_wall", "minecraft:brick_wall", "minecraft:cobblestone_wall", "minecraft:diorite_wall", "minecraft:end_stone_brick_wall", "minecraft:granite_wall", "minecraft:mossy_cobblestone_wall", "minecraft:mossy_stone_brick_wall", "minecraft:nether_brick_wall", "minecraft:prismarine_wall", "minecraft:red_nether_brick_wall", "minecraft:red_sandstone_wall", "minecraft:sandstone_wall", "minecraft:stone_brick_wall");

   public WallPropertyFix(Schema p_17154_, boolean p_17155_) {
      super(p_17154_, p_17155_);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("WallPropertyFix", this.getInputSchema().getType(References.BLOCK_STATE), (p_17157_) -> {
         return p_17157_.update(DSL.remainderFinder(), WallPropertyFix::upgradeBlockStateTag);
      });
   }

   private static String mapProperty(String p_17164_) {
      return "true".equals(p_17164_) ? "low" : "none";
   }

   private static <T> Dynamic<T> fixWallProperty(Dynamic<T> p_17161_, String p_17162_) {
      return p_17161_.update(p_17162_, (p_17168_) -> {
         return DataFixUtils.orElse(p_17168_.asString().result().map(WallPropertyFix::mapProperty).map(p_17168_::createString), p_17168_);
      });
   }

   private static <T> Dynamic<T> upgradeBlockStateTag(Dynamic<T> p_17159_) {
      boolean flag = p_17159_.get("Name").asString().result().filter(WALL_BLOCKS::contains).isPresent();
      return !flag ? p_17159_ : p_17159_.update("Properties", (p_17166_) -> {
         Dynamic<?> dynamic = fixWallProperty(p_17166_, "east");
         dynamic = fixWallProperty(dynamic, "west");
         dynamic = fixWallProperty(dynamic, "north");
         return fixWallProperty(dynamic, "south");
      });
   }
}