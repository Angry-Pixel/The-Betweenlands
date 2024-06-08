package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class BedItemColorFix extends DataFix {
   public BedItemColorFix(Schema p_14720_, boolean p_14721_) {
      super(p_14720_, p_14721_);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      return this.fixTypeEverywhereTyped("BedItemColorFix", this.getInputSchema().getType(References.ITEM_STACK), (p_14724_) -> {
         Optional<Pair<String, String>> optional = p_14724_.getOptional(opticfinder);
         if (optional.isPresent() && Objects.equals(optional.get().getSecond(), "minecraft:bed")) {
            Dynamic<?> dynamic = p_14724_.get(DSL.remainderFinder());
            if (dynamic.get("Damage").asInt(0) == 0) {
               return p_14724_.set(DSL.remainderFinder(), dynamic.set("Damage", dynamic.createShort((short)14)));
            }
         }

         return p_14724_;
      });
   }
}