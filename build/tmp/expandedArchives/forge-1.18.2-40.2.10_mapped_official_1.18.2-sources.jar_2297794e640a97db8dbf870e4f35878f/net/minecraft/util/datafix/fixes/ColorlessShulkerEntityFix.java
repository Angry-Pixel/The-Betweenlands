package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class ColorlessShulkerEntityFix extends NamedEntityFix {
   public ColorlessShulkerEntityFix(Schema p_15315_, boolean p_15316_) {
      super(p_15315_, p_15316_, "Colorless shulker entity fix", References.ENTITY, "minecraft:shulker");
   }

   protected Typed<?> fix(Typed<?> p_15318_) {
      return p_15318_.update(DSL.remainderFinder(), (p_15320_) -> {
         return p_15320_.get("Color").asInt(0) == 10 ? p_15320_.set("Color", p_15320_.createByte((byte)16)) : p_15320_;
      });
   }
}