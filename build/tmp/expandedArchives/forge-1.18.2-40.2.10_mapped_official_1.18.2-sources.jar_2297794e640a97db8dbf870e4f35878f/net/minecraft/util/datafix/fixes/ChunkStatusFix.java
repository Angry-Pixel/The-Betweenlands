package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class ChunkStatusFix extends DataFix {
   public ChunkStatusFix(Schema p_15247_, boolean p_15248_) {
      super(p_15247_, p_15248_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.CHUNK);
      Type<?> type1 = type.findFieldType("Level");
      OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type1);
      return this.fixTypeEverywhereTyped("ChunkStatusFix", type, this.getOutputSchema().getType(References.CHUNK), (p_15251_) -> {
         return p_15251_.updateTyped(opticfinder, (p_145230_) -> {
            Dynamic<?> dynamic = p_145230_.get(DSL.remainderFinder());
            String s = dynamic.get("Status").asString("empty");
            if (Objects.equals(s, "postprocessed")) {
               dynamic = dynamic.set("Status", dynamic.createString("fullchunk"));
            }

            return p_145230_.set(DSL.remainderFinder(), dynamic);
         });
      });
   }
}