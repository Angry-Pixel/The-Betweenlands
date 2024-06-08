package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class BlockEntityKeepPacked extends NamedEntityFix {
   public BlockEntityKeepPacked(Schema p_14848_, boolean p_14849_) {
      super(p_14848_, p_14849_, "BlockEntityKeepPacked", References.BLOCK_ENTITY, "DUMMY");
   }

   private static Dynamic<?> fixTag(Dynamic<?> p_14853_) {
      return p_14853_.set("keepPacked", p_14853_.createBoolean(true));
   }

   protected Typed<?> fix(Typed<?> p_14851_) {
      return p_14851_.update(DSL.remainderFinder(), BlockEntityKeepPacked::fixTag);
   }
}