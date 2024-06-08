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

public class ItemSpawnEggFix extends DataFix {
   private static final String[] ID_TO_ENTITY = DataFixUtils.make(new String[256], (p_16054_) -> {
      p_16054_[1] = "Item";
      p_16054_[2] = "XPOrb";
      p_16054_[7] = "ThrownEgg";
      p_16054_[8] = "LeashKnot";
      p_16054_[9] = "Painting";
      p_16054_[10] = "Arrow";
      p_16054_[11] = "Snowball";
      p_16054_[12] = "Fireball";
      p_16054_[13] = "SmallFireball";
      p_16054_[14] = "ThrownEnderpearl";
      p_16054_[15] = "EyeOfEnderSignal";
      p_16054_[16] = "ThrownPotion";
      p_16054_[17] = "ThrownExpBottle";
      p_16054_[18] = "ItemFrame";
      p_16054_[19] = "WitherSkull";
      p_16054_[20] = "PrimedTnt";
      p_16054_[21] = "FallingSand";
      p_16054_[22] = "FireworksRocketEntity";
      p_16054_[23] = "TippedArrow";
      p_16054_[24] = "SpectralArrow";
      p_16054_[25] = "ShulkerBullet";
      p_16054_[26] = "DragonFireball";
      p_16054_[30] = "ArmorStand";
      p_16054_[41] = "Boat";
      p_16054_[42] = "MinecartRideable";
      p_16054_[43] = "MinecartChest";
      p_16054_[44] = "MinecartFurnace";
      p_16054_[45] = "MinecartTNT";
      p_16054_[46] = "MinecartHopper";
      p_16054_[47] = "MinecartSpawner";
      p_16054_[40] = "MinecartCommandBlock";
      p_16054_[48] = "Mob";
      p_16054_[49] = "Monster";
      p_16054_[50] = "Creeper";
      p_16054_[51] = "Skeleton";
      p_16054_[52] = "Spider";
      p_16054_[53] = "Giant";
      p_16054_[54] = "Zombie";
      p_16054_[55] = "Slime";
      p_16054_[56] = "Ghast";
      p_16054_[57] = "PigZombie";
      p_16054_[58] = "Enderman";
      p_16054_[59] = "CaveSpider";
      p_16054_[60] = "Silverfish";
      p_16054_[61] = "Blaze";
      p_16054_[62] = "LavaSlime";
      p_16054_[63] = "EnderDragon";
      p_16054_[64] = "WitherBoss";
      p_16054_[65] = "Bat";
      p_16054_[66] = "Witch";
      p_16054_[67] = "Endermite";
      p_16054_[68] = "Guardian";
      p_16054_[69] = "Shulker";
      p_16054_[90] = "Pig";
      p_16054_[91] = "Sheep";
      p_16054_[92] = "Cow";
      p_16054_[93] = "Chicken";
      p_16054_[94] = "Squid";
      p_16054_[95] = "Wolf";
      p_16054_[96] = "MushroomCow";
      p_16054_[97] = "SnowMan";
      p_16054_[98] = "Ozelot";
      p_16054_[99] = "VillagerGolem";
      p_16054_[100] = "EntityHorse";
      p_16054_[101] = "Rabbit";
      p_16054_[120] = "Villager";
      p_16054_[200] = "EnderCrystal";
   });

   public ItemSpawnEggFix(Schema p_16034_, boolean p_16035_) {
      super(p_16034_, p_16035_);
   }

   public TypeRewriteRule makeRule() {
      Schema schema = this.getInputSchema();
      Type<?> type = schema.getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder<String> opticfinder1 = DSL.fieldFinder("id", DSL.string());
      OpticFinder<?> opticfinder2 = type.findField("tag");
      OpticFinder<?> opticfinder3 = opticfinder2.type().findField("EntityTag");
      OpticFinder<?> opticfinder4 = DSL.typeFinder(schema.getTypeRaw(References.ENTITY));
      Type<?> type1 = this.getOutputSchema().getTypeRaw(References.ENTITY);
      return this.fixTypeEverywhereTyped("ItemSpawnEggFix", type, (p_16044_) -> {
         Optional<Pair<String, String>> optional = p_16044_.getOptional(opticfinder);
         if (optional.isPresent() && Objects.equals(optional.get().getSecond(), "minecraft:spawn_egg")) {
            Dynamic<?> dynamic = p_16044_.get(DSL.remainderFinder());
            short short1 = dynamic.get("Damage").asShort((short)0);
            Optional<? extends Typed<?>> optional1 = p_16044_.getOptionalTyped(opticfinder2);
            Optional<? extends Typed<?>> optional2 = optional1.flatMap((p_145417_) -> {
               return p_145417_.getOptionalTyped(opticfinder3);
            });
            Optional<? extends Typed<?>> optional3 = optional2.flatMap((p_145414_) -> {
               return p_145414_.getOptionalTyped(opticfinder4);
            });
            Optional<String> optional4 = optional3.flatMap((p_145406_) -> {
               return p_145406_.getOptional(opticfinder1);
            });
            Typed<?> typed = p_16044_;
            String s = ID_TO_ENTITY[short1 & 255];
            if (s != null && (!optional4.isPresent() || !Objects.equals(optional4.get(), s))) {
               Typed<?> typed1 = p_16044_.getOrCreateTyped(opticfinder2);
               Typed<?> typed2 = typed1.getOrCreateTyped(opticfinder3);
               Typed<?> typed3 = typed2.getOrCreateTyped(opticfinder4);
               Dynamic<?> dynamic1 = dynamic;
               Typed<?> typed4 = typed3.write().flatMap((p_145411_) -> {
                  return type1.readTyped(p_145411_.set("id", dynamic1.createString(s)));
               }).result().orElseThrow(() -> {
                  return new IllegalStateException("Could not parse new entity");
               }).getFirst();
               typed = p_16044_.set(opticfinder2, typed1.set(opticfinder3, typed2.set(opticfinder4, typed4)));
            }

            if (short1 != 0) {
               dynamic = dynamic.set("Damage", dynamic.createShort((short)0));
               typed = typed.set(DSL.remainderFinder(), dynamic);
            }

            return typed;
         } else {
            return p_16044_;
         }
      });
   }
}