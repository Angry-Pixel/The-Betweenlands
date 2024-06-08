package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;

public class FurnaceRecipeFix extends DataFix {
   public FurnaceRecipeFix(Schema p_15837_, boolean p_15838_) {
      super(p_15837_, p_15838_);
   }

   protected TypeRewriteRule makeRule() {
      return this.cap(this.getOutputSchema().getTypeRaw(References.RECIPE));
   }

   private <R> TypeRewriteRule cap(Type<R> p_15850_) {
      Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>> type = DSL.and(DSL.optional(DSL.field("RecipesUsed", DSL.and(DSL.compoundList(p_15850_, DSL.intType()), DSL.remainderType()))), DSL.remainderType());
      OpticFinder<?> opticfinder = DSL.namedChoice("minecraft:furnace", this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:furnace"));
      OpticFinder<?> opticfinder1 = DSL.namedChoice("minecraft:blast_furnace", this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:blast_furnace"));
      OpticFinder<?> opticfinder2 = DSL.namedChoice("minecraft:smoker", this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:smoker"));
      Type<?> type1 = this.getOutputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:furnace");
      Type<?> type2 = this.getOutputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:blast_furnace");
      Type<?> type3 = this.getOutputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:smoker");
      Type<?> type4 = this.getInputSchema().getType(References.BLOCK_ENTITY);
      Type<?> type5 = this.getOutputSchema().getType(References.BLOCK_ENTITY);
      return this.fixTypeEverywhereTyped("FurnaceRecipesFix", type4, type5, (p_15848_) -> {
         return p_15848_.updateTyped(opticfinder, type1, (p_145372_) -> {
            return this.updateFurnaceContents(p_15850_, type, p_145372_);
         }).updateTyped(opticfinder1, type2, (p_145368_) -> {
            return this.updateFurnaceContents(p_15850_, type, p_145368_);
         }).updateTyped(opticfinder2, type3, (p_145364_) -> {
            return this.updateFurnaceContents(p_15850_, type, p_145364_);
         });
      });
   }

   private <R> Typed<?> updateFurnaceContents(Type<R> p_15852_, Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>> p_15853_, Typed<?> p_15854_) {
      Dynamic<?> dynamic = p_15854_.getOrCreate(DSL.remainderFinder());
      int i = dynamic.get("RecipesUsedSize").asInt(0);
      dynamic = dynamic.remove("RecipesUsedSize");
      List<Pair<R, Integer>> list = Lists.newArrayList();

      for(int j = 0; j < i; ++j) {
         String s = "RecipeLocation" + j;
         String s1 = "RecipeAmount" + j;
         Optional<? extends Dynamic<?>> optional = dynamic.get(s).result();
         int k = dynamic.get(s1).asInt(0);
         if (k > 0) {
            optional.ifPresent((p_15859_) -> {
               Optional<? extends Pair<R, ? extends Dynamic<?>>> optional1 = p_15852_.read(p_15859_).result();
               optional1.ifPresent((p_145360_) -> {
                  list.add(Pair.of(p_145360_.getFirst(), k));
               });
            });
         }

         dynamic = dynamic.remove(s).remove(s1);
      }

      return p_15854_.set(DSL.remainderFinder(), p_15853_, Pair.of(Either.left(Pair.of(list, dynamic.emptyMap())), dynamic));
   }
}