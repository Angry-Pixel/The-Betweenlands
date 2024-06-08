package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class VillagerFollowRangeFix extends NamedEntityFix {
   private static final double ORIGINAL_VALUE = 16.0D;
   private static final double NEW_BASE_VALUE = 48.0D;

   public VillagerFollowRangeFix(Schema p_17064_) {
      super(p_17064_, false, "Villager Follow Range Fix", References.ENTITY, "minecraft:villager");
   }

   protected Typed<?> fix(Typed<?> p_17066_) {
      return p_17066_.update(DSL.remainderFinder(), VillagerFollowRangeFix::fixValue);
   }

   private static Dynamic<?> fixValue(Dynamic<?> p_17068_) {
      return p_17068_.update("Attributes", (p_17071_) -> {
         return p_17068_.createList(p_17071_.asStream().map((p_145760_) -> {
            return p_145760_.get("Name").asString("").equals("generic.follow_range") && p_145760_.get("Base").asDouble(0.0D) == 16.0D ? p_145760_.set("Base", p_145760_.createDouble(48.0D)) : p_145760_;
         }));
      });
   }
}