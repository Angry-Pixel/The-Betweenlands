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
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemStackMapIdFix extends DataFix {
   public ItemStackMapIdFix(Schema p_16088_, boolean p_16089_) {
      super(p_16088_, p_16089_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemInstanceMapIdFix", type, (p_16093_) -> {
         Optional<Pair<String, String>> optional = p_16093_.getOptional(opticfinder);
         if (optional.isPresent() && Objects.equals(optional.get().getSecond(), "minecraft:filled_map")) {
            Dynamic<?> dynamic = p_16093_.get(DSL.remainderFinder());
            Typed<?> typed = p_16093_.getOrCreateTyped(opticfinder1);
            Dynamic<?> dynamic1 = typed.get(DSL.remainderFinder());
            dynamic1 = dynamic1.set("map", dynamic1.createInt(dynamic.get("Damage").asInt(0)));
            return p_16093_.set(opticfinder1, typed.set(DSL.remainderFinder(), dynamic1));
         } else {
            return p_16093_;
         }
      });
   }
}