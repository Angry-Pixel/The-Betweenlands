package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Map;

public class EntityIdFix extends DataFix {
   private static final Map<String, String> ID_MAP = DataFixUtils.make(Maps.newHashMap(), (p_15465_) -> {
      p_15465_.put("AreaEffectCloud", "minecraft:area_effect_cloud");
      p_15465_.put("ArmorStand", "minecraft:armor_stand");
      p_15465_.put("Arrow", "minecraft:arrow");
      p_15465_.put("Bat", "minecraft:bat");
      p_15465_.put("Blaze", "minecraft:blaze");
      p_15465_.put("Boat", "minecraft:boat");
      p_15465_.put("CaveSpider", "minecraft:cave_spider");
      p_15465_.put("Chicken", "minecraft:chicken");
      p_15465_.put("Cow", "minecraft:cow");
      p_15465_.put("Creeper", "minecraft:creeper");
      p_15465_.put("Donkey", "minecraft:donkey");
      p_15465_.put("DragonFireball", "minecraft:dragon_fireball");
      p_15465_.put("ElderGuardian", "minecraft:elder_guardian");
      p_15465_.put("EnderCrystal", "minecraft:ender_crystal");
      p_15465_.put("EnderDragon", "minecraft:ender_dragon");
      p_15465_.put("Enderman", "minecraft:enderman");
      p_15465_.put("Endermite", "minecraft:endermite");
      p_15465_.put("EyeOfEnderSignal", "minecraft:eye_of_ender_signal");
      p_15465_.put("FallingSand", "minecraft:falling_block");
      p_15465_.put("Fireball", "minecraft:fireball");
      p_15465_.put("FireworksRocketEntity", "minecraft:fireworks_rocket");
      p_15465_.put("Ghast", "minecraft:ghast");
      p_15465_.put("Giant", "minecraft:giant");
      p_15465_.put("Guardian", "minecraft:guardian");
      p_15465_.put("Horse", "minecraft:horse");
      p_15465_.put("Husk", "minecraft:husk");
      p_15465_.put("Item", "minecraft:item");
      p_15465_.put("ItemFrame", "minecraft:item_frame");
      p_15465_.put("LavaSlime", "minecraft:magma_cube");
      p_15465_.put("LeashKnot", "minecraft:leash_knot");
      p_15465_.put("MinecartChest", "minecraft:chest_minecart");
      p_15465_.put("MinecartCommandBlock", "minecraft:commandblock_minecart");
      p_15465_.put("MinecartFurnace", "minecraft:furnace_minecart");
      p_15465_.put("MinecartHopper", "minecraft:hopper_minecart");
      p_15465_.put("MinecartRideable", "minecraft:minecart");
      p_15465_.put("MinecartSpawner", "minecraft:spawner_minecart");
      p_15465_.put("MinecartTNT", "minecraft:tnt_minecart");
      p_15465_.put("Mule", "minecraft:mule");
      p_15465_.put("MushroomCow", "minecraft:mooshroom");
      p_15465_.put("Ozelot", "minecraft:ocelot");
      p_15465_.put("Painting", "minecraft:painting");
      p_15465_.put("Pig", "minecraft:pig");
      p_15465_.put("PigZombie", "minecraft:zombie_pigman");
      p_15465_.put("PolarBear", "minecraft:polar_bear");
      p_15465_.put("PrimedTnt", "minecraft:tnt");
      p_15465_.put("Rabbit", "minecraft:rabbit");
      p_15465_.put("Sheep", "minecraft:sheep");
      p_15465_.put("Shulker", "minecraft:shulker");
      p_15465_.put("ShulkerBullet", "minecraft:shulker_bullet");
      p_15465_.put("Silverfish", "minecraft:silverfish");
      p_15465_.put("Skeleton", "minecraft:skeleton");
      p_15465_.put("SkeletonHorse", "minecraft:skeleton_horse");
      p_15465_.put("Slime", "minecraft:slime");
      p_15465_.put("SmallFireball", "minecraft:small_fireball");
      p_15465_.put("SnowMan", "minecraft:snowman");
      p_15465_.put("Snowball", "minecraft:snowball");
      p_15465_.put("SpectralArrow", "minecraft:spectral_arrow");
      p_15465_.put("Spider", "minecraft:spider");
      p_15465_.put("Squid", "minecraft:squid");
      p_15465_.put("Stray", "minecraft:stray");
      p_15465_.put("ThrownEgg", "minecraft:egg");
      p_15465_.put("ThrownEnderpearl", "minecraft:ender_pearl");
      p_15465_.put("ThrownExpBottle", "minecraft:xp_bottle");
      p_15465_.put("ThrownPotion", "minecraft:potion");
      p_15465_.put("Villager", "minecraft:villager");
      p_15465_.put("VillagerGolem", "minecraft:villager_golem");
      p_15465_.put("Witch", "minecraft:witch");
      p_15465_.put("WitherBoss", "minecraft:wither");
      p_15465_.put("WitherSkeleton", "minecraft:wither_skeleton");
      p_15465_.put("WitherSkull", "minecraft:wither_skull");
      p_15465_.put("Wolf", "minecraft:wolf");
      p_15465_.put("XPOrb", "minecraft:xp_orb");
      p_15465_.put("Zombie", "minecraft:zombie");
      p_15465_.put("ZombieHorse", "minecraft:zombie_horse");
      p_15465_.put("ZombieVillager", "minecraft:zombie_villager");
   });

   public EntityIdFix(Schema p_15456_, boolean p_15457_) {
      super(p_15456_, p_15457_);
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<String> taggedchoicetype = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(References.ENTITY);
      TaggedChoiceType<String> taggedchoicetype1 = (TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(References.ENTITY);
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      Type<?> type1 = this.getOutputSchema().getType(References.ITEM_STACK);
      return TypeRewriteRule.seq(this.convertUnchecked("item stack entity name hook converter", type, type1), this.fixTypeEverywhere("EntityIdFix", taggedchoicetype, taggedchoicetype1, (p_15461_) -> {
         return (p_145282_) -> {
            return p_145282_.mapFirst((p_145284_) -> {
               return ID_MAP.getOrDefault(p_145284_, p_145284_);
            });
         };
      }));
   }
}