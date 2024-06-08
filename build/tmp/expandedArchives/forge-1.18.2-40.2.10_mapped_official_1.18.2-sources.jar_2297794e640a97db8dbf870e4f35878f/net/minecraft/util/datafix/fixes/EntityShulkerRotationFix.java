package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;

public class EntityShulkerRotationFix extends NamedEntityFix {
   public EntityShulkerRotationFix(Schema p_15680_) {
      super(p_15680_, false, "EntityShulkerRotationFix", References.ENTITY, "minecraft:shulker");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_15684_) {
      List<Double> list = p_15684_.get("Rotation").asList((p_15686_) -> {
         return p_15686_.asDouble(180.0D);
      });
      if (!list.isEmpty()) {
         list.set(0, list.get(0) - 180.0D);
         return p_15684_.set("Rotation", p_15684_.createList(list.stream().map(p_15684_::createDouble)));
      } else {
         return p_15684_;
      }
   }

   protected Typed<?> fix(Typed<?> p_15682_) {
      return p_15682_.update(DSL.remainderFinder(), this::fixTag);
   }
}