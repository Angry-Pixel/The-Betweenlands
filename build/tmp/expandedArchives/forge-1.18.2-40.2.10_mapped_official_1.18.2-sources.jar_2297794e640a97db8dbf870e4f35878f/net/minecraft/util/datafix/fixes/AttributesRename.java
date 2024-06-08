package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;

public class AttributesRename extends DataFix {
   private static final Map<String, String> RENAMES = ImmutableMap.<String, String>builder().put("generic.maxHealth", "generic.max_health").put("Max Health", "generic.max_health").put("zombie.spawnReinforcements", "zombie.spawn_reinforcements").put("Spawn Reinforcements Chance", "zombie.spawn_reinforcements").put("horse.jumpStrength", "horse.jump_strength").put("Jump Strength", "horse.jump_strength").put("generic.followRange", "generic.follow_range").put("Follow Range", "generic.follow_range").put("generic.knockbackResistance", "generic.knockback_resistance").put("Knockback Resistance", "generic.knockback_resistance").put("generic.movementSpeed", "generic.movement_speed").put("Movement Speed", "generic.movement_speed").put("generic.flyingSpeed", "generic.flying_speed").put("Flying Speed", "generic.flying_speed").put("generic.attackDamage", "generic.attack_damage").put("generic.attackKnockback", "generic.attack_knockback").put("generic.attackSpeed", "generic.attack_speed").put("generic.armorToughness", "generic.armor_toughness").build();

   public AttributesRename(Schema p_14671_) {
      super(p_14671_, false);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<?> opticfinder = type.findField("tag");
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("Rename ItemStack Attributes", type, (p_14674_) -> {
         return p_14674_.updateTyped(opticfinder, AttributesRename::fixItemStackTag);
      }), this.fixTypeEverywhereTyped("Rename Entity Attributes", this.getInputSchema().getType(References.ENTITY), AttributesRename::fixEntity), this.fixTypeEverywhereTyped("Rename Player Attributes", this.getInputSchema().getType(References.PLAYER), AttributesRename::fixEntity));
   }

   private static Dynamic<?> fixName(Dynamic<?> p_14678_) {
      return DataFixUtils.orElse(p_14678_.asString().result().map((p_14680_) -> {
         return RENAMES.getOrDefault(p_14680_, p_14680_);
      }).map(p_14678_::createString), p_14678_);
   }

   private static Typed<?> fixItemStackTag(Typed<?> p_14676_) {
      return p_14676_.update(DSL.remainderFinder(), (p_14694_) -> {
         return p_14694_.update("AttributeModifiers", (p_145080_) -> {
            return DataFixUtils.orElse(p_145080_.asStreamOpt().result().map((p_145074_) -> {
               return p_145074_.map((p_145082_) -> {
                  return p_145082_.update("AttributeName", AttributesRename::fixName);
               });
            }).map(p_145080_::createList), p_145080_);
         });
      });
   }

   private static Typed<?> fixEntity(Typed<?> p_14684_) {
      return p_14684_.update(DSL.remainderFinder(), (p_14686_) -> {
         return p_14686_.update("Attributes", (p_145076_) -> {
            return DataFixUtils.orElse(p_145076_.asStreamOpt().result().map((p_145072_) -> {
               return p_145072_.map((p_145078_) -> {
                  return p_145078_.update("Name", AttributesRename::fixName);
               });
            }).map(p_145076_::createList), p_145076_);
         });
      });
   }
}