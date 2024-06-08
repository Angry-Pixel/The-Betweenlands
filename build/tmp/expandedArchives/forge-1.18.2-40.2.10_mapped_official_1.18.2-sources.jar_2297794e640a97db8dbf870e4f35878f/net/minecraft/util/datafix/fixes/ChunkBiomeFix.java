package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

public class ChunkBiomeFix extends DataFix {
   public ChunkBiomeFix(Schema p_15014_, boolean p_15015_) {
      super(p_15014_, p_15015_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.CHUNK);
      OpticFinder<?> opticfinder = type.findField("Level");
      return this.fixTypeEverywhereTyped("Leaves fix", type, (p_15018_) -> {
         return p_15018_.updateTyped(opticfinder, (p_145204_) -> {
            return p_145204_.update(DSL.remainderFinder(), (p_145206_) -> {
               Optional<IntStream> optional = p_145206_.get("Biomes").asIntStreamOpt().result();
               if (optional.isEmpty()) {
                  return p_145206_;
               } else {
                  int[] aint = optional.get().toArray();
                  if (aint.length != 256) {
                     return p_145206_;
                  } else {
                     int[] aint1 = new int[1024];

                     for(int i = 0; i < 4; ++i) {
                        for(int j = 0; j < 4; ++j) {
                           int k = (j << 2) + 2;
                           int l = (i << 2) + 2;
                           int i1 = l << 4 | k;
                           aint1[i << 2 | j] = aint[i1];
                        }
                     }

                     for(int j1 = 1; j1 < 64; ++j1) {
                        System.arraycopy(aint1, 0, aint1, j1 * 16, 16);
                     }

                     return p_145206_.set("Biomes", p_145206_.createIntList(Arrays.stream(aint1)));
                  }
               }
            });
         });
      });
   }
}