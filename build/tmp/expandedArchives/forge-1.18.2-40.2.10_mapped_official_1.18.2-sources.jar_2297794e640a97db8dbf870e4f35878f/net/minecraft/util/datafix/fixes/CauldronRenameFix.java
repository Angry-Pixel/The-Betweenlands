package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class CauldronRenameFix extends DataFix {
   public CauldronRenameFix(Schema p_145196_, boolean p_145197_) {
      super(p_145196_, p_145197_);
   }

   private static Dynamic<?> fix(Dynamic<?> p_145201_) {
      Optional<String> optional = p_145201_.get("Name").asString().result();
      if (optional.equals(Optional.of("minecraft:cauldron"))) {
         Dynamic<?> dynamic = p_145201_.get("Properties").orElseEmptyMap();
         return dynamic.get("level").asString("0").equals("0") ? p_145201_.remove("Properties") : p_145201_.set("Name", p_145201_.createString("minecraft:water_cauldron"));
      } else {
         return p_145201_;
      }
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("cauldron_rename_fix", this.getInputSchema().getType(References.BLOCK_STATE), (p_145199_) -> {
         return p_145199_.update(DSL.remainderFinder(), CauldronRenameFix::fix);
      });
   }
}