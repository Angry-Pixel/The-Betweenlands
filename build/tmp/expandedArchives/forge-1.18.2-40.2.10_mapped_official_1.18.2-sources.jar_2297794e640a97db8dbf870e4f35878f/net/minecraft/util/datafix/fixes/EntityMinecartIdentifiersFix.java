package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;

public class EntityMinecartIdentifiersFix extends DataFix {
   private static final List<String> MINECART_BY_ID = Lists.newArrayList("MinecartRideable", "MinecartChest", "MinecartFurnace");

   public EntityMinecartIdentifiersFix(Schema p_15479_, boolean p_15480_) {
      super(p_15479_, p_15480_);
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<String> taggedchoicetype = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(References.ENTITY);
      TaggedChoiceType<String> taggedchoicetype1 = (TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(References.ENTITY);
      return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", taggedchoicetype, taggedchoicetype1, (p_15485_) -> {
         return (p_145290_) -> {
            if (!Objects.equals(p_145290_.getFirst(), "Minecart")) {
               return p_145290_;
            } else {
               Typed<? extends Pair<String, ?>> typed = taggedchoicetype.point(p_15485_, "Minecart", p_145290_.getSecond()).orElseThrow(IllegalStateException::new);
               Dynamic<?> dynamic = typed.getOrCreate(DSL.remainderFinder());
               int i = dynamic.get("Type").asInt(0);
               String s;
               if (i > 0 && i < MINECART_BY_ID.size()) {
                  s = MINECART_BY_ID.get(i);
               } else {
                  s = "MinecartRideable";
               }

               return Pair.of(s, ((com.mojang.serialization.DataResult<com.mojang.serialization.Dynamic<Pair<String, ?>>>)typed.write()).map((p_145294_) -> {
                  return taggedchoicetype1.types().get(s).read(p_145294_);
               }).result().orElseThrow(() -> {
                  return new IllegalStateException("Could not read the new minecart.");
               }));
            }
         };
      });
   }
}