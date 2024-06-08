package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class RecipesRenameningFix extends RecipesRenameFix {
   private static final Map<String, String> RECIPES = ImmutableMap.<String, String>builder().put("minecraft:acacia_bark", "minecraft:acacia_wood").put("minecraft:birch_bark", "minecraft:birch_wood").put("minecraft:dark_oak_bark", "minecraft:dark_oak_wood").put("minecraft:jungle_bark", "minecraft:jungle_wood").put("minecraft:oak_bark", "minecraft:oak_wood").put("minecraft:spruce_bark", "minecraft:spruce_wood").build();

   public RecipesRenameningFix(Schema p_16744_, boolean p_16745_) {
      super(p_16744_, p_16745_, "Recipes renamening fix", (p_16747_) -> {
         return RECIPES.getOrDefault(p_16747_, p_16747_);
      });
   }
}