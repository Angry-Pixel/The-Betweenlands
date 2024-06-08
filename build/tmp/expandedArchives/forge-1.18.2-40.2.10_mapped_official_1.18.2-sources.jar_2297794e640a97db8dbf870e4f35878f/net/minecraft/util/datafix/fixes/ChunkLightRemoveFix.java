package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class ChunkLightRemoveFix extends DataFix {
   public ChunkLightRemoveFix(Schema p_15025_, boolean p_15026_) {
      super(p_15025_, p_15026_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.CHUNK);
      Type<?> type1 = type.findFieldType("Level");
      OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type1);
      return this.fixTypeEverywhereTyped("ChunkLightRemoveFix", type, this.getOutputSchema().getType(References.CHUNK), (p_15029_) -> {
         return p_15029_.updateTyped(opticfinder, (p_145208_) -> {
            return p_145208_.update(DSL.remainderFinder(), (p_145210_) -> {
               return p_145210_.remove("isLightOn");
            });
         });
      });
   }
}