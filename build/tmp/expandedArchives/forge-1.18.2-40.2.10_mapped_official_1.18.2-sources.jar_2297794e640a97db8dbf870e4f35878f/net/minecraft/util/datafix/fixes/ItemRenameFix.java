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

public abstract class ItemRenameFix extends DataFix {
   private final String name;

   public ItemRenameFix(Schema p_16001_, String p_16002_) {
      super(p_16001_, false);
      this.name = p_16002_;
   }

   public TypeRewriteRule makeRule() {
      Type<Pair<String, String>> type = DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString());
      if (!Objects.equals(this.getInputSchema().getType(References.ITEM_NAME), type)) {
         throw new IllegalStateException("item name type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.name, type, (p_16010_) -> {
            return (p_145402_) -> {
               return p_145402_.mapSecond(this::fixItem);
            };
         });
      }
   }

   protected abstract String fixItem(String p_16011_);

   public static DataFix create(Schema p_16004_, String p_16005_, final Function<String, String> p_16006_) {
      return new ItemRenameFix(p_16004_, p_16005_) {
         protected String fixItem(String p_16019_) {
            return p_16006_.apply(p_16019_);
         }
      };
   }
}