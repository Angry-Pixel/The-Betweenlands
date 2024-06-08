package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;

public class OverreachingTickFix extends DataFix {
   public OverreachingTickFix(Schema p_207654_) {
      super(p_207654_, false);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.CHUNK);
      OpticFinder<?> opticfinder = type.findField("block_ticks");
      return this.fixTypeEverywhereTyped("Handle ticks saved in the wrong chunk", type, (p_207661_) -> {
         Optional<? extends Typed<?>> optional = p_207661_.getOptionalTyped(opticfinder);
         Optional<? extends Dynamic<?>> optional1 = optional.isPresent() ? optional.get().write().result() : Optional.empty();
         return p_207661_.update(DSL.remainderFinder(), (p_207670_) -> {
            int i = p_207670_.get("xPos").asInt(0);
            int j = p_207670_.get("zPos").asInt(0);
            Optional<? extends Dynamic<?>> optional2 = p_207670_.get("fluid_ticks").get().result();
            p_207670_ = extractOverreachingTicks(p_207670_, i, j, optional1, "neighbor_block_ticks");
            return extractOverreachingTicks(p_207670_, i, j, optional2, "neighbor_fluid_ticks");
         });
      });
   }

   private static Dynamic<?> extractOverreachingTicks(Dynamic<?> p_207663_, int p_207664_, int p_207665_, Optional<? extends Dynamic<?>> p_207666_, String p_207667_) {
      if (p_207666_.isPresent()) {
         List<? extends Dynamic<?>> list = p_207666_.get().asStream().filter((p_207658_) -> {
            int i = p_207658_.get("x").asInt(0);
            int j = p_207658_.get("z").asInt(0);
            int k = Math.abs(p_207664_ - (i >> 4));
            int l = Math.abs(p_207665_ - (j >> 4));
            return (k != 0 || l != 0) && k <= 1 && l <= 1;
         }).toList();
         if (!list.isEmpty()) {
            p_207663_ = p_207663_.set("UpgradeData", p_207663_.get("UpgradeData").orElseEmptyMap().set(p_207667_, p_207663_.createList(list.stream())));
         }
      }

      return p_207663_;
   }
}