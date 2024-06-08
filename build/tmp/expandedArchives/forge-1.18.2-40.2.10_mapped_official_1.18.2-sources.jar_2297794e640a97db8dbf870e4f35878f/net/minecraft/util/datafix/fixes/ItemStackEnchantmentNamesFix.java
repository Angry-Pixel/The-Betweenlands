package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;

public class ItemStackEnchantmentNamesFix extends DataFix {
   private static final Int2ObjectMap<String> MAP = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), (p_16075_) -> {
      p_16075_.put(0, "minecraft:protection");
      p_16075_.put(1, "minecraft:fire_protection");
      p_16075_.put(2, "minecraft:feather_falling");
      p_16075_.put(3, "minecraft:blast_protection");
      p_16075_.put(4, "minecraft:projectile_protection");
      p_16075_.put(5, "minecraft:respiration");
      p_16075_.put(6, "minecraft:aqua_affinity");
      p_16075_.put(7, "minecraft:thorns");
      p_16075_.put(8, "minecraft:depth_strider");
      p_16075_.put(9, "minecraft:frost_walker");
      p_16075_.put(10, "minecraft:binding_curse");
      p_16075_.put(16, "minecraft:sharpness");
      p_16075_.put(17, "minecraft:smite");
      p_16075_.put(18, "minecraft:bane_of_arthropods");
      p_16075_.put(19, "minecraft:knockback");
      p_16075_.put(20, "minecraft:fire_aspect");
      p_16075_.put(21, "minecraft:looting");
      p_16075_.put(22, "minecraft:sweeping");
      p_16075_.put(32, "minecraft:efficiency");
      p_16075_.put(33, "minecraft:silk_touch");
      p_16075_.put(34, "minecraft:unbreaking");
      p_16075_.put(35, "minecraft:fortune");
      p_16075_.put(48, "minecraft:power");
      p_16075_.put(49, "minecraft:punch");
      p_16075_.put(50, "minecraft:flame");
      p_16075_.put(51, "minecraft:infinity");
      p_16075_.put(61, "minecraft:luck_of_the_sea");
      p_16075_.put(62, "minecraft:lure");
      p_16075_.put(65, "minecraft:loyalty");
      p_16075_.put(66, "minecraft:impaling");
      p_16075_.put(67, "minecraft:riptide");
      p_16075_.put(68, "minecraft:channeling");
      p_16075_.put(70, "minecraft:mending");
      p_16075_.put(71, "minecraft:vanishing_curse");
   });

   public ItemStackEnchantmentNamesFix(Schema p_16065_, boolean p_16066_) {
      super(p_16065_, p_16066_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<?> opticfinder = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemStackEnchantmentFix", type, (p_16069_) -> {
         return p_16069_.updateTyped(opticfinder, (p_145419_) -> {
            return p_145419_.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }

   private Dynamic<?> fixTag(Dynamic<?> p_16073_) {
      Optional<? extends Dynamic<?>> optional = p_16073_.get("ench").asStreamOpt().map((p_16081_) -> {
         return p_16081_.map((p_145425_) -> {
            return p_145425_.set("id", p_145425_.createString(MAP.getOrDefault(p_145425_.get("id").asInt(0), "null")));
         });
      }).map(p_16073_::createList).result();
      if (optional.isPresent()) {
         p_16073_ = p_16073_.remove("ench").set("Enchantments", optional.get());
      }

      return p_16073_.update("StoredEnchantments", (p_16079_) -> {
         return DataFixUtils.orElse(p_16079_.asStreamOpt().map((p_145421_) -> {
            return p_145421_.map((p_145423_) -> {
               return p_145423_.set("id", p_145423_.createString(MAP.getOrDefault(p_145423_.get("id").asInt(0), "null")));
            });
         }).map(p_16079_::createList).result(), p_16079_);
      });
   }
}