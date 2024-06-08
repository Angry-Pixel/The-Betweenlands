package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;

public abstract class BlockRenameFixWithJigsaw extends BlockRenameFix {
   private final String name;

   public BlockRenameFixWithJigsaw(Schema p_145150_, String p_145151_) {
      super(p_145150_, p_145151_);
      this.name = p_145151_;
   }

   public TypeRewriteRule makeRule() {
      TypeReference typereference = References.BLOCK_ENTITY;
      String s = "minecraft:jigsaw";
      OpticFinder<?> opticfinder = DSL.namedChoice("minecraft:jigsaw", this.getInputSchema().getChoiceType(typereference, "minecraft:jigsaw"));
      TypeRewriteRule typerewriterule = this.fixTypeEverywhereTyped(this.name + " for jigsaw state", this.getInputSchema().getType(typereference), this.getOutputSchema().getType(typereference), (p_145155_) -> {
         return p_145155_.updateTyped(opticfinder, this.getOutputSchema().getChoiceType(typereference, "minecraft:jigsaw"), (p_145157_) -> {
            return p_145157_.update(DSL.remainderFinder(), (p_145159_) -> {
               return p_145159_.update("final_state", (p_145162_) -> {
                  return DataFixUtils.orElse(p_145162_.asString().result().map((p_145168_) -> {
                     int i = p_145168_.indexOf(91);
                     int j = p_145168_.indexOf(123);
                     int k = p_145168_.length();
                     if (i > 0) {
                        k = Math.min(k, i);
                     }

                     if (j > 0) {
                        k = Math.min(k, j);
                     }

                     String s1 = p_145168_.substring(0, k);
                     String s2 = this.fixBlock(s1);
                     return s2 + p_145168_.substring(k);
                  }).map(p_145159_::createString), p_145162_);
               });
            });
         });
      });
      return TypeRewriteRule.seq(super.makeRule(), typerewriterule);
   }

   public static DataFix create(Schema p_145164_, String p_145165_, final Function<String, String> p_145166_) {
      return new BlockRenameFixWithJigsaw(p_145164_, p_145165_) {
         protected String fixBlock(String p_145176_) {
            return p_145166_.apply(p_145176_);
         }
      };
   }
}