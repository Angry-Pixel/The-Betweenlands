package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EntityRidingToPassengersFix extends DataFix {
   public EntityRidingToPassengersFix(Schema p_15638_, boolean p_15639_) {
      super(p_15638_, p_15639_);
   }

   public TypeRewriteRule makeRule() {
      Schema schema = this.getInputSchema();
      Schema schema1 = this.getOutputSchema();
      Type<?> type = schema.getTypeRaw(References.ENTITY_TREE);
      Type<?> type1 = schema1.getTypeRaw(References.ENTITY_TREE);
      Type<?> type2 = schema.getTypeRaw(References.ENTITY);
      return this.cap(schema, schema1, type, type1, type2);
   }

   private <OldEntityTree, NewEntityTree, Entity> TypeRewriteRule cap(Schema p_15642_, Schema p_15643_, Type<OldEntityTree> p_15644_, Type<NewEntityTree> p_15645_, Type<Entity> p_15646_) {
      Type<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> type = DSL.named(References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Riding", p_15644_)), p_15646_));
      Type<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> type1 = DSL.named(References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Passengers", DSL.list(p_15645_))), p_15646_));
      Type<?> type2 = p_15642_.getType(References.ENTITY_TREE);
      Type<?> type3 = p_15643_.getType(References.ENTITY_TREE);
      if (!Objects.equals(type2, type)) {
         throw new IllegalStateException("Old entity type is not what was expected.");
      } else if (!type3.equals(type1, true, true)) {
         throw new IllegalStateException("New entity type is not what was expected.");
      } else {
         OpticFinder<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> opticfinder = DSL.typeFinder(type);
         OpticFinder<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> opticfinder1 = DSL.typeFinder(type1);
         OpticFinder<NewEntityTree> opticfinder2 = DSL.typeFinder(p_15645_);
         Type<?> type4 = p_15642_.getType(References.PLAYER);
         Type<?> type5 = p_15643_.getType(References.PLAYER);
         return TypeRewriteRule.seq(this.fixTypeEverywhere("EntityRidingToPassengerFix", type, type1, (p_15653_) -> {
            return (p_145320_) -> {
               Optional<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> optional = Optional.empty();
               Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>> pair = p_145320_;

               while(true) {
                  Either<List<NewEntityTree>, Unit> either = DataFixUtils.orElse(optional.map((p_145326_) -> {
                     Typed<NewEntityTree> typed = p_15645_.pointTyped(p_15653_).orElseThrow(() -> {
                        return new IllegalStateException("Could not create new entity tree");
                     });
                     NewEntityTree newentitytree = typed.set(opticfinder1, p_145326_).getOptional(opticfinder2).orElseThrow(() -> {
                        return new IllegalStateException("Should always have an entity tree here");
                     });
                     return Either.left(ImmutableList.of(newentitytree));
                  }), Either.right(DSL.unit()));
                  optional = Optional.of(Pair.of(References.ENTITY_TREE.typeName(), Pair.of(either, pair.getSecond().getSecond())));
                  Optional<OldEntityTree> optional1 = pair.getSecond().getFirst().left();
                  if (!optional1.isPresent()) {
                     return optional.orElseThrow(() -> {
                        return new IllegalStateException("Should always have an entity tree here");
                     });
                  }

                  pair = (new Typed<>(p_15644_, p_15653_, optional1.get())).getOptional(opticfinder).orElseThrow(() -> {
                     return new IllegalStateException("Should always have an entity here");
                  });
               }
            };
         }), this.writeAndRead("player RootVehicle injecter", type4, type5));
      }
   }
}