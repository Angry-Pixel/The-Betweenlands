package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EntityHorseSaddleFix extends NamedEntityFix {
   public EntityHorseSaddleFix(Schema p_15442_, boolean p_15443_) {
      super(p_15442_, p_15443_, "EntityHorseSaddleFix", References.ENTITY, "EntityHorse");
   }

   protected Typed<?> fix(Typed<?> p_15445_) {
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      Type<?> type = this.getInputSchema().getTypeRaw(References.ITEM_STACK);
      OpticFinder<?> opticfinder1 = DSL.fieldFinder("SaddleItem", type);
      Optional<? extends Typed<?>> optional = p_15445_.getOptionalTyped(opticfinder1);
      Dynamic<?> dynamic = p_15445_.get(DSL.remainderFinder());
      if (!optional.isPresent() && dynamic.get("Saddle").asBoolean(false)) {
         Typed<?> typed = type.pointTyped(p_15445_.getOps()).orElseThrow(IllegalStateException::new);
         typed = typed.set(opticfinder, Pair.of(References.ITEM_NAME.typeName(), "minecraft:saddle"));
         Dynamic<?> dynamic1 = dynamic.emptyMap();
         dynamic1 = dynamic1.set("Count", dynamic1.createByte((byte)1));
         dynamic1 = dynamic1.set("Damage", dynamic1.createShort((short)0));
         typed = typed.set(DSL.remainderFinder(), dynamic1);
         dynamic.remove("Saddle");
         return p_15445_.set(opticfinder1, typed).set(DSL.remainderFinder(), dynamic);
      } else {
         return p_15445_;
      }
   }
}