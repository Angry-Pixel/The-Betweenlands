package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class CatTypeFix extends NamedEntityFix {
   public CatTypeFix(Schema p_15007_, boolean p_15008_) {
      super(p_15007_, p_15008_, "CatTypeFix", References.ENTITY, "minecraft:cat");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_15012_) {
      return p_15012_.get("CatType").asInt(0) == 9 ? p_15012_.set("CatType", p_15012_.createInt(10)) : p_15012_;
   }

   protected Typed<?> fix(Typed<?> p_15010_) {
      return p_15010_.update(DSL.remainderFinder(), this::fixTag);
   }
}