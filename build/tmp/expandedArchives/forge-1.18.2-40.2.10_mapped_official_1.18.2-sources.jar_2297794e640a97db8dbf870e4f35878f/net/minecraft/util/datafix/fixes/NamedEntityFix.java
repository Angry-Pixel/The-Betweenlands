package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public abstract class NamedEntityFix extends DataFix {
   private final String name;
   private final String entityName;
   private final TypeReference type;

   public NamedEntityFix(Schema p_16465_, boolean p_16466_, String p_16467_, TypeReference p_16468_, String p_16469_) {
      super(p_16465_, p_16466_);
      this.name = p_16467_;
      this.type = p_16468_;
      this.entityName = p_16469_;
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<?> opticfinder = DSL.namedChoice(this.entityName, this.getInputSchema().getChoiceType(this.type, this.entityName));
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type), (p_16472_) -> {
         return p_16472_.updateTyped(opticfinder, this.getOutputSchema().getChoiceType(this.type, this.entityName), this::fix);
      });
   }

   protected abstract Typed<?> fix(Typed<?> p_16473_);
}