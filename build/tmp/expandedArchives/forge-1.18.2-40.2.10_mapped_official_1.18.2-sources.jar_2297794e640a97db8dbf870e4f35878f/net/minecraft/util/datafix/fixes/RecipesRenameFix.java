package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class RecipesRenameFix extends DataFix {
   private final String name;
   private final Function<String, String> renamer;

   public RecipesRenameFix(Schema p_16732_, boolean p_16733_, String p_16734_, Function<String, String> p_16735_) {
      super(p_16732_, p_16733_);
      this.name = p_16734_;
      this.renamer = p_16735_;
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, String>> type = DSL.named(References.RECIPE.typeName(), NamespacedSchema.namespacedString());
      if (!Objects.equals(type, this.getInputSchema().getType(References.RECIPE))) {
         throw new IllegalStateException("Recipe type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.name, type, (p_16739_) -> {
            return (p_145615_) -> {
               return p_145615_.mapSecond(this.renamer);
            };
         });
      }
   }
}