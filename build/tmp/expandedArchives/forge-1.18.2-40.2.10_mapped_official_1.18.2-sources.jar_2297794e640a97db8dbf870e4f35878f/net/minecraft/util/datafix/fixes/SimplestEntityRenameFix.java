package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class SimplestEntityRenameFix extends DataFix {
   private final String name;

   public SimplestEntityRenameFix(String p_16911_, Schema p_16912_, boolean p_16913_) {
      super(p_16912_, p_16913_);
      this.name = p_16911_;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<String> taggedchoicetype = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(References.ENTITY);
      TaggedChoiceType<String> taggedchoicetype1 = (TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(References.ENTITY);
      Type<Pair<String, String>> type = DSL.named(References.ENTITY_NAME.typeName(), NamespacedSchema.namespacedString());
      if (!Objects.equals(this.getOutputSchema().getType(References.ENTITY_NAME), type)) {
         throw new IllegalStateException("Entity name type is not what was expected.");
      } else {
         return TypeRewriteRule.seq(this.fixTypeEverywhere(this.name, taggedchoicetype, taggedchoicetype1, (p_16921_) -> {
            return (p_145688_) -> {
               return p_145688_.mapFirst((p_145692_) -> {
                  String s = this.rename(p_145692_);
                  Type<?> type1 = taggedchoicetype.types().get(p_145692_);
                  Type<?> type2 = taggedchoicetype1.types().get(s);
                  if (!type2.equals(type1, true, true)) {
                     throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", type2, type1));
                  } else {
                     return s;
                  }
               });
            };
         }), this.fixTypeEverywhere(this.name + " for entity name", type, (p_16929_) -> {
            return (p_145694_) -> {
               return p_145694_.mapSecond(this::rename);
            };
         }));
      }
   }

   protected abstract String rename(String p_16930_);
}