package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.Mth;

public class VillagerRebuildLevelAndXpFix extends DataFix {
   private static final int TRADES_PER_LEVEL = 2;
   private static final int[] LEVEL_XP_THRESHOLDS = new int[]{0, 10, 50, 100, 150};

   public static int getMinXpPerLevel(int p_17080_) {
      return LEVEL_XP_THRESHOLDS[Mth.clamp(p_17080_ - 1, 0, LEVEL_XP_THRESHOLDS.length - 1)];
   }

   public VillagerRebuildLevelAndXpFix(Schema p_17077_, boolean p_17078_) {
      super(p_17077_, p_17078_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getChoiceType(References.ENTITY, "minecraft:villager");
      OpticFinder<?> opticfinder = DSL.namedChoice("minecraft:villager", type);
      OpticFinder<?> opticfinder1 = type.findField("Offers");
      Type<?> type1 = opticfinder1.type();
      OpticFinder<?> opticfinder2 = type1.findField("Recipes");
      ListType<?> listtype = (ListType)opticfinder2.type();
      OpticFinder<?> opticfinder3 = listtype.getElement().finder();
      return this.fixTypeEverywhereTyped("Villager level and xp rebuild", this.getInputSchema().getType(References.ENTITY), (p_17098_) -> {
         return p_17098_.updateTyped(opticfinder, type, (p_145766_) -> {
            Dynamic<?> dynamic = p_145766_.get(DSL.remainderFinder());
            int i = dynamic.get("VillagerData").get("level").asInt(0);
            Typed<?> typed = p_145766_;
            if (i == 0 || i == 1) {
               int j = p_145766_.getOptionalTyped(opticfinder1).flatMap((p_145772_) -> {
                  return p_145772_.getOptionalTyped(opticfinder2);
               }).map((p_145769_) -> {
                  return p_145769_.getAllTyped(opticfinder3).size();
               }).orElse(0);
               i = Mth.clamp(j / 2, 1, 5);
               if (i > 1) {
                  typed = addLevel(p_145766_, i);
               }
            }

            Optional<Number> optional = dynamic.get("Xp").asNumber().result();
            if (!optional.isPresent()) {
               typed = addXpFromLevel(typed, i);
            }

            return typed;
         });
      });
   }

   private static Typed<?> addLevel(Typed<?> p_17100_, int p_17101_) {
      return p_17100_.update(DSL.remainderFinder(), (p_17104_) -> {
         return p_17104_.update("VillagerData", (p_145775_) -> {
            return p_145775_.set("level", p_145775_.createInt(p_17101_));
         });
      });
   }

   private static Typed<?> addXpFromLevel(Typed<?> p_17109_, int p_17110_) {
      int i = getMinXpPerLevel(p_17110_);
      return p_17109_.update(DSL.remainderFinder(), (p_17083_) -> {
         return p_17083_.set("Xp", p_17083_.createInt(i));
      });
   }
}