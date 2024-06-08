package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class VillagerDataFix extends NamedEntityFix {
   public VillagerDataFix(Schema p_17056_, String p_17057_) {
      super(p_17056_, false, "Villager profession data fix (" + p_17057_ + ")", References.ENTITY, p_17057_);
   }

   protected Typed<?> fix(Typed<?> p_17062_) {
      Dynamic<?> dynamic = p_17062_.get(DSL.remainderFinder());
      return p_17062_.set(DSL.remainderFinder(), dynamic.remove("Profession").remove("Career").remove("CareerLevel").set("VillagerData", dynamic.createMap(ImmutableMap.of(dynamic.createString("type"), dynamic.createString("minecraft:plains"), dynamic.createString("profession"), dynamic.createString(upgradeData(dynamic.get("Profession").asInt(0), dynamic.get("Career").asInt(0))), dynamic.createString("level"), DataFixUtils.orElse(dynamic.get("CareerLevel").result(), dynamic.createInt(1))))));
   }

   private static String upgradeData(int p_17059_, int p_17060_) {
      if (p_17059_ == 0) {
         if (p_17060_ == 2) {
            return "minecraft:fisherman";
         } else if (p_17060_ == 3) {
            return "minecraft:shepherd";
         } else {
            return p_17060_ == 4 ? "minecraft:fletcher" : "minecraft:farmer";
         }
      } else if (p_17059_ == 1) {
         return p_17060_ == 2 ? "minecraft:cartographer" : "minecraft:librarian";
      } else if (p_17059_ == 2) {
         return "minecraft:cleric";
      } else if (p_17059_ == 3) {
         if (p_17060_ == 2) {
            return "minecraft:weaponsmith";
         } else {
            return p_17060_ == 3 ? "minecraft:toolsmith" : "minecraft:armorer";
         }
      } else if (p_17059_ == 4) {
         return p_17060_ == 2 ? "minecraft:leatherworker" : "minecraft:butcher";
      } else {
         return p_17059_ == 5 ? "minecraft:nitwit" : "minecraft:none";
      }
   }
}