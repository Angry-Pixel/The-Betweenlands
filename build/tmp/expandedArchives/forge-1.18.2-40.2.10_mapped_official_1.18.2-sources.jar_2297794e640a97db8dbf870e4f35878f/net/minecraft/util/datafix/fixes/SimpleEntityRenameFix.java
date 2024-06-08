package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;

public abstract class SimpleEntityRenameFix extends EntityRenameFix {
   public SimpleEntityRenameFix(String p_16901_, Schema p_16902_, boolean p_16903_) {
      super(p_16901_, p_16902_, p_16903_);
   }

   protected Pair<String, Typed<?>> fix(String p_16905_, Typed<?> p_16906_) {
      Pair<String, Dynamic<?>> pair = this.getNewNameAndTag(p_16905_, p_16906_.getOrCreate(DSL.remainderFinder()));
      return Pair.of(pair.getFirst(), p_16906_.set(DSL.remainderFinder(), pair.getSecond()));
   }

   protected abstract Pair<String, Dynamic<?>> getNewNameAndTag(String p_16907_, Dynamic<?> p_16908_);
}