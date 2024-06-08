package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Map;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class StatsRenameFix extends DataFix {
   private final String name;
   private final Map<String, String> renames;

   public StatsRenameFix(Schema p_145705_, String p_145706_, Map<String, String> p_145707_) {
      super(p_145705_, false);
      this.name = p_145706_;
      this.renames = p_145707_;
   }

   protected TypeRewriteRule makeRule() {
      return TypeRewriteRule.seq(this.createStatRule(), this.createCriteriaRule());
   }

   private TypeRewriteRule createCriteriaRule() {
      Type<?> type = this.getOutputSchema().getType(References.OBJECTIVE);
      Type<?> type1 = this.getInputSchema().getType(References.OBJECTIVE);
      OpticFinder<?> opticfinder = type1.findField("CriteriaType");
      TaggedChoiceType<?> taggedchoicetype = opticfinder.type().findChoiceType("type", -1).orElseThrow(() -> {
         return new IllegalStateException("Can't find choice type for criteria");
      });
      Type<?> type2 = taggedchoicetype.types().get("minecraft:custom");
      if (type2 == null) {
         throw new IllegalStateException("Failed to find custom criterion type variant");
      } else {
         OpticFinder<?> opticfinder1 = DSL.namedChoice("minecraft:custom", type2);
         OpticFinder<String> opticfinder2 = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
         return this.fixTypeEverywhereTyped(this.name, type1, type, (p_181062_) -> {
            return p_181062_.updateTyped(opticfinder, (p_181066_) -> {
               return p_181066_.updateTyped(opticfinder1, (p_181069_) -> {
                  return p_181069_.update(opticfinder2, (p_181071_) -> {
                     return this.renames.getOrDefault(p_181071_, p_181071_);
                  });
               });
            });
         });
      }
   }

   private TypeRewriteRule createStatRule() {
      Type<?> type = this.getOutputSchema().getType(References.STATS);
      Type<?> type1 = this.getInputSchema().getType(References.STATS);
      OpticFinder<?> opticfinder = type1.findField("stats");
      OpticFinder<?> opticfinder1 = opticfinder.type().findField("minecraft:custom");
      OpticFinder<String> opticfinder2 = NamespacedSchema.namespacedString().finder();
      return this.fixTypeEverywhereTyped(this.name, type1, type, (p_145712_) -> {
         return p_145712_.updateTyped(opticfinder, (p_145716_) -> {
            return p_145716_.updateTyped(opticfinder1, (p_145719_) -> {
               return p_145719_.update(opticfinder2, (p_145721_) -> {
                  return this.renames.getOrDefault(p_145721_, p_145721_);
               });
            });
         });
      });
   }
}