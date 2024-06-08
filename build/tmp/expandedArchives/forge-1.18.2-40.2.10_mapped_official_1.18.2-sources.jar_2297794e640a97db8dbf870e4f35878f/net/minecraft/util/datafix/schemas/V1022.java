package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1022 extends Schema {
   public V1022(int p_17365_, Schema p_17366_) {
      super(p_17365_, p_17366_);
   }

   public void registerTypes(Schema p_17373_, Map<String, Supplier<TypeTemplate>> p_17374_, Map<String, Supplier<TypeTemplate>> p_17375_) {
      super.registerTypes(p_17373_, p_17374_, p_17375_);
      p_17373_.registerType(false, References.RECIPE, () -> {
         return DSL.constType(NamespacedSchema.namespacedString());
      });
      p_17373_.registerType(false, References.PLAYER, () -> {
         return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", References.ENTITY_TREE.in(p_17373_)), "Inventory", DSL.list(References.ITEM_STACK.in(p_17373_)), "EnderItems", DSL.list(References.ITEM_STACK.in(p_17373_)), DSL.optionalFields("ShoulderEntityLeft", References.ENTITY_TREE.in(p_17373_), "ShoulderEntityRight", References.ENTITY_TREE.in(p_17373_), "recipeBook", DSL.optionalFields("recipes", DSL.list(References.RECIPE.in(p_17373_)), "toBeDisplayed", DSL.list(References.RECIPE.in(p_17373_)))));
      });
      p_17373_.registerType(false, References.HOTBAR, () -> {
         return DSL.compoundList(DSL.list(References.ITEM_STACK.in(p_17373_)));
      });
   }
}