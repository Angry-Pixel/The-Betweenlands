package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemPotionFix extends DataFix {
   private static final int SPLASH = 16384;
   private static final String[] POTIONS = DataFixUtils.make(new String[128], (p_15997_) -> {
      p_15997_[0] = "minecraft:water";
      p_15997_[1] = "minecraft:regeneration";
      p_15997_[2] = "minecraft:swiftness";
      p_15997_[3] = "minecraft:fire_resistance";
      p_15997_[4] = "minecraft:poison";
      p_15997_[5] = "minecraft:healing";
      p_15997_[6] = "minecraft:night_vision";
      p_15997_[7] = null;
      p_15997_[8] = "minecraft:weakness";
      p_15997_[9] = "minecraft:strength";
      p_15997_[10] = "minecraft:slowness";
      p_15997_[11] = "minecraft:leaping";
      p_15997_[12] = "minecraft:harming";
      p_15997_[13] = "minecraft:water_breathing";
      p_15997_[14] = "minecraft:invisibility";
      p_15997_[15] = null;
      p_15997_[16] = "minecraft:awkward";
      p_15997_[17] = "minecraft:regeneration";
      p_15997_[18] = "minecraft:swiftness";
      p_15997_[19] = "minecraft:fire_resistance";
      p_15997_[20] = "minecraft:poison";
      p_15997_[21] = "minecraft:healing";
      p_15997_[22] = "minecraft:night_vision";
      p_15997_[23] = null;
      p_15997_[24] = "minecraft:weakness";
      p_15997_[25] = "minecraft:strength";
      p_15997_[26] = "minecraft:slowness";
      p_15997_[27] = "minecraft:leaping";
      p_15997_[28] = "minecraft:harming";
      p_15997_[29] = "minecraft:water_breathing";
      p_15997_[30] = "minecraft:invisibility";
      p_15997_[31] = null;
      p_15997_[32] = "minecraft:thick";
      p_15997_[33] = "minecraft:strong_regeneration";
      p_15997_[34] = "minecraft:strong_swiftness";
      p_15997_[35] = "minecraft:fire_resistance";
      p_15997_[36] = "minecraft:strong_poison";
      p_15997_[37] = "minecraft:strong_healing";
      p_15997_[38] = "minecraft:night_vision";
      p_15997_[39] = null;
      p_15997_[40] = "minecraft:weakness";
      p_15997_[41] = "minecraft:strong_strength";
      p_15997_[42] = "minecraft:slowness";
      p_15997_[43] = "minecraft:strong_leaping";
      p_15997_[44] = "minecraft:strong_harming";
      p_15997_[45] = "minecraft:water_breathing";
      p_15997_[46] = "minecraft:invisibility";
      p_15997_[47] = null;
      p_15997_[48] = null;
      p_15997_[49] = "minecraft:strong_regeneration";
      p_15997_[50] = "minecraft:strong_swiftness";
      p_15997_[51] = "minecraft:fire_resistance";
      p_15997_[52] = "minecraft:strong_poison";
      p_15997_[53] = "minecraft:strong_healing";
      p_15997_[54] = "minecraft:night_vision";
      p_15997_[55] = null;
      p_15997_[56] = "minecraft:weakness";
      p_15997_[57] = "minecraft:strong_strength";
      p_15997_[58] = "minecraft:slowness";
      p_15997_[59] = "minecraft:strong_leaping";
      p_15997_[60] = "minecraft:strong_harming";
      p_15997_[61] = "minecraft:water_breathing";
      p_15997_[62] = "minecraft:invisibility";
      p_15997_[63] = null;
      p_15997_[64] = "minecraft:mundane";
      p_15997_[65] = "minecraft:long_regeneration";
      p_15997_[66] = "minecraft:long_swiftness";
      p_15997_[67] = "minecraft:long_fire_resistance";
      p_15997_[68] = "minecraft:long_poison";
      p_15997_[69] = "minecraft:healing";
      p_15997_[70] = "minecraft:long_night_vision";
      p_15997_[71] = null;
      p_15997_[72] = "minecraft:long_weakness";
      p_15997_[73] = "minecraft:long_strength";
      p_15997_[74] = "minecraft:long_slowness";
      p_15997_[75] = "minecraft:long_leaping";
      p_15997_[76] = "minecraft:harming";
      p_15997_[77] = "minecraft:long_water_breathing";
      p_15997_[78] = "minecraft:long_invisibility";
      p_15997_[79] = null;
      p_15997_[80] = "minecraft:awkward";
      p_15997_[81] = "minecraft:long_regeneration";
      p_15997_[82] = "minecraft:long_swiftness";
      p_15997_[83] = "minecraft:long_fire_resistance";
      p_15997_[84] = "minecraft:long_poison";
      p_15997_[85] = "minecraft:healing";
      p_15997_[86] = "minecraft:long_night_vision";
      p_15997_[87] = null;
      p_15997_[88] = "minecraft:long_weakness";
      p_15997_[89] = "minecraft:long_strength";
      p_15997_[90] = "minecraft:long_slowness";
      p_15997_[91] = "minecraft:long_leaping";
      p_15997_[92] = "minecraft:harming";
      p_15997_[93] = "minecraft:long_water_breathing";
      p_15997_[94] = "minecraft:long_invisibility";
      p_15997_[95] = null;
      p_15997_[96] = "minecraft:thick";
      p_15997_[97] = "minecraft:regeneration";
      p_15997_[98] = "minecraft:swiftness";
      p_15997_[99] = "minecraft:long_fire_resistance";
      p_15997_[100] = "minecraft:poison";
      p_15997_[101] = "minecraft:strong_healing";
      p_15997_[102] = "minecraft:long_night_vision";
      p_15997_[103] = null;
      p_15997_[104] = "minecraft:long_weakness";
      p_15997_[105] = "minecraft:strength";
      p_15997_[106] = "minecraft:long_slowness";
      p_15997_[107] = "minecraft:leaping";
      p_15997_[108] = "minecraft:strong_harming";
      p_15997_[109] = "minecraft:long_water_breathing";
      p_15997_[110] = "minecraft:long_invisibility";
      p_15997_[111] = null;
      p_15997_[112] = null;
      p_15997_[113] = "minecraft:regeneration";
      p_15997_[114] = "minecraft:swiftness";
      p_15997_[115] = "minecraft:long_fire_resistance";
      p_15997_[116] = "minecraft:poison";
      p_15997_[117] = "minecraft:strong_healing";
      p_15997_[118] = "minecraft:long_night_vision";
      p_15997_[119] = null;
      p_15997_[120] = "minecraft:long_weakness";
      p_15997_[121] = "minecraft:strength";
      p_15997_[122] = "minecraft:long_slowness";
      p_15997_[123] = "minecraft:leaping";
      p_15997_[124] = "minecraft:strong_harming";
      p_15997_[125] = "minecraft:long_water_breathing";
      p_15997_[126] = "minecraft:long_invisibility";
      p_15997_[127] = null;
   });
   public static final String DEFAULT = "minecraft:water";

   public ItemPotionFix(Schema p_15990_, boolean p_15991_) {
      super(p_15990_, p_15991_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder<?> opticfinder1 = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemPotionFix", type, (p_15995_) -> {
         Optional<Pair<String, String>> optional = p_15995_.getOptional(opticfinder);
         if (optional.isPresent() && Objects.equals(optional.get().getSecond(), "minecraft:potion")) {
            Dynamic<?> dynamic = p_15995_.get(DSL.remainderFinder());
            Optional<? extends Typed<?>> optional1 = p_15995_.getOptionalTyped(opticfinder1);
            short short1 = dynamic.get("Damage").asShort((short)0);
            if (optional1.isPresent()) {
               Typed<?> typed = p_15995_;
               Dynamic<?> dynamic1 = optional1.get().get(DSL.remainderFinder());
               Optional<String> optional2 = dynamic1.get("Potion").asString().result();
               if (!optional2.isPresent()) {
                  String s = POTIONS[short1 & 127];
                  Typed<?> typed1 = optional1.get().set(DSL.remainderFinder(), dynamic1.set("Potion", dynamic1.createString(s == null ? "minecraft:water" : s)));
                  typed = p_15995_.set(opticfinder1, typed1);
                  if ((short1 & 16384) == 16384) {
                     typed = typed.set(opticfinder, Pair.of(References.ITEM_NAME.typeName(), "minecraft:splash_potion"));
                  }
               }

               if (short1 != 0) {
                  dynamic = dynamic.set("Damage", dynamic.createShort((short)0));
               }

               return typed.set(DSL.remainderFinder(), dynamic);
            }
         }

         return p_15995_;
      });
   }
}