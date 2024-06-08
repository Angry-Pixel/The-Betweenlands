package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.stream.Collectors;

public class OptionsKeyTranslationFix extends DataFix {
   public OptionsKeyTranslationFix(Schema p_16645_, boolean p_16646_) {
      super(p_16645_, p_16646_);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsKeyTranslationFix", this.getInputSchema().getType(References.OPTIONS), (p_16648_) -> {
         return p_16648_.update(DSL.remainderFinder(), (p_145582_) -> {
            return p_145582_.getMapValues().map((p_145588_) -> {
               return p_145582_.createMap(p_145588_.entrySet().stream().map((p_145585_) -> {
                  if (p_145585_.getKey().asString("").startsWith("key_")) {
                     String s = p_145585_.getValue().asString("");
                     if (!s.startsWith("key.mouse") && !s.startsWith("scancode.")) {
                        return Pair.of(p_145585_.getKey(), p_145582_.createString("key.keyboard." + s.substring("key.".length())));
                     }
                  }

                  return Pair.of(p_145585_.getKey(), p_145585_.getValue());
               }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            }).result().orElse((com.mojang.serialization.Dynamic) p_145582_);
         });
      });
   }
}