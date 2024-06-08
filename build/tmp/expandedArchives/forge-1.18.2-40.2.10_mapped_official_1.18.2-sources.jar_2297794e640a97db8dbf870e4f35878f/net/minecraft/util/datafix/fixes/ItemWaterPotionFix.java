package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemWaterPotionFix extends DataFix {
   public ItemWaterPotionFix(Schema p_16156_, boolean p_16157_) {
      super(p_16156_, p_16157_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemWaterPotionFix", type, (p_16161_) -> {
         Optional<Pair<String, String>> optional = p_16161_.getOptional(opticfinder);
         if (optional.isPresent()) {
            String s = optional.get().getSecond();
            if ("minecraft:potion".equals(s) || "minecraft:splash_potion".equals(s) || "minecraft:lingering_potion".equals(s) || "minecraft:tipped_arrow".equals(s)) {
               Typed<?> typed = p_16161_.getOrCreateTyped(opticfinder1);
               Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
               if (!dynamic.get("Potion").asString().result().isPresent()) {
                  dynamic = dynamic.set("Potion", dynamic.createString("minecraft:water"));
               }

               return p_16161_.set(opticfinder1, typed.set(DSL.remainderFinder(), dynamic));
            }
         }

         return p_16161_;
      });
   }
}