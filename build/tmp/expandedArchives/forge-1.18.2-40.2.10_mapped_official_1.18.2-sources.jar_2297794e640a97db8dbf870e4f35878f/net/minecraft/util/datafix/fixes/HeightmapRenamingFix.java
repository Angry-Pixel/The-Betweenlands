package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class HeightmapRenamingFix extends DataFix {
   public HeightmapRenamingFix(Schema p_15891_, boolean p_15892_) {
      super(p_15891_, p_15892_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.CHUNK);
      OpticFinder<?> opticfinder = type.findField("Level");
      return this.fixTypeEverywhereTyped("HeightmapRenamingFix", type, (p_15895_) -> {
         return p_15895_.updateTyped(opticfinder, (p_145380_) -> {
            return p_145380_.update(DSL.remainderFinder(), this::fix);
         });
      });
   }

   private Dynamic<?> fix(Dynamic<?> p_15899_) {
      Optional<? extends Dynamic<?>> optional = p_15899_.get("Heightmaps").result();
      if (!optional.isPresent()) {
         return p_15899_;
      } else {
         Dynamic<?> dynamic = optional.get();
         Optional<? extends Dynamic<?>> optional1 = dynamic.get("LIQUID").result();
         if (optional1.isPresent()) {
            dynamic = dynamic.remove("LIQUID");
            dynamic = dynamic.set("WORLD_SURFACE_WG", optional1.get());
         }

         Optional<? extends Dynamic<?>> optional2 = dynamic.get("SOLID").result();
         if (optional2.isPresent()) {
            dynamic = dynamic.remove("SOLID");
            dynamic = dynamic.set("OCEAN_FLOOR_WG", optional2.get());
            dynamic = dynamic.set("OCEAN_FLOOR", optional2.get());
         }

         Optional<? extends Dynamic<?>> optional3 = dynamic.get("LIGHT").result();
         if (optional3.isPresent()) {
            dynamic = dynamic.remove("LIGHT");
            dynamic = dynamic.set("LIGHT_BLOCKING", optional3.get());
         }

         Optional<? extends Dynamic<?>> optional4 = dynamic.get("RAIN").result();
         if (optional4.isPresent()) {
            dynamic = dynamic.remove("RAIN");
            dynamic = dynamic.set("MOTION_BLOCKING", optional4.get());
            dynamic = dynamic.set("MOTION_BLOCKING_NO_LEAVES", optional4.get());
         }

         return p_15899_.set("Heightmaps", dynamic);
      }
   }
}