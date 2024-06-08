package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class StriderGravityFix extends NamedEntityFix {
   public StriderGravityFix(Schema p_16954_, boolean p_16955_) {
      super(p_16954_, p_16955_, "StriderGravityFix", References.ENTITY, "minecraft:strider");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_16959_) {
      return p_16959_.get("NoGravity").asBoolean(false) ? p_16959_.set("NoGravity", p_16959_.createBoolean(false)) : p_16959_;
   }

   protected Typed<?> fix(Typed<?> p_16957_) {
      return p_16957_.update(DSL.remainderFinder(), this::fixTag);
   }
}