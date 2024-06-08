package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V2501 extends NamespacedSchema {
   public V2501(int p_17848_, Schema p_17849_) {
      super(p_17848_, p_17849_);
   }

   private static void registerFurnace(Schema p_17853_, Map<String, Supplier<TypeTemplate>> p_17854_, String p_17855_) {
      p_17853_.register(p_17854_, p_17855_, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(p_17853_)), "RecipesUsed", DSL.compoundList(References.RECIPE.in(p_17853_), DSL.constType(DSL.intType())));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_17857_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_17857_);
      registerFurnace(p_17857_, map, "minecraft:furnace");
      registerFurnace(p_17857_, map, "minecraft:smoker");
      registerFurnace(p_17857_, map, "minecraft:blast_furnace");
      return map;
   }
}