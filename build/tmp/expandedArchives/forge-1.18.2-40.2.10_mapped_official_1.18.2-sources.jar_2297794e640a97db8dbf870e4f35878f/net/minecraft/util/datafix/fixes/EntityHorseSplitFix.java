package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class EntityHorseSplitFix extends EntityRenameFix {
   public EntityHorseSplitFix(Schema p_15447_, boolean p_15448_) {
      super("EntityHorseSplitFix", p_15447_, p_15448_);
   }

   protected Pair<String, Typed<?>> fix(String p_15451_, Typed<?> p_15452_) {
      Dynamic<?> dynamic = p_15452_.get(DSL.remainderFinder());
      if (Objects.equals("EntityHorse", p_15451_)) {
         int i = dynamic.get("Type").asInt(0);
         String s;
         switch(i) {
         case 0:
         default:
            s = "Horse";
            break;
         case 1:
            s = "Donkey";
            break;
         case 2:
            s = "Mule";
            break;
         case 3:
            s = "ZombieHorse";
            break;
         case 4:
            s = "SkeletonHorse";
         }

         dynamic.remove("Type");
         Type<?> type = this.getOutputSchema().findChoiceType(References.ENTITY).types().get(s);
         return Pair.of(s, (Typed<?>)((Pair)((com.mojang.serialization.DataResult<Dynamic<?>>)p_15452_.write()).flatMap(type::readTyped).result().orElseThrow(() -> {
            return new IllegalStateException("Could not parse the new horse");
         })).getFirst());
      } else {
         return Pair.of(p_15451_, p_15452_);
      }
   }
}