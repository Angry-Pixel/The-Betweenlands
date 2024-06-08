package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Optional;

public class OptionsLowerCaseLanguageFix extends DataFix {
   public OptionsLowerCaseLanguageFix(Schema p_16659_, boolean p_16660_) {
      super(p_16659_, p_16660_);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsLowerCaseLanguageFix", this.getInputSchema().getType(References.OPTIONS), (p_16662_) -> {
         return p_16662_.update(DSL.remainderFinder(), (p_145590_) -> {
            Optional<String> optional = p_145590_.get("lang").asString().result();
            return optional.isPresent() ? p_145590_.set("lang", p_145590_.createString(optional.get().toLowerCase(Locale.ROOT))) : p_145590_;
         });
      });
   }
}