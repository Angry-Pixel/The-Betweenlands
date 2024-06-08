package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public class DefaultAttributes {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<EntityType<? extends LivingEntity>, AttributeSupplier> SUPPLIERS = ImmutableMap.<EntityType<? extends LivingEntity>, AttributeSupplier>builder().put(EntityType.ARMOR_STAND, LivingEntity.createLivingAttributes().build()).put(EntityType.AXOLOTL, Axolotl.createAttributes().build()).put(EntityType.BAT, Bat.createAttributes().build()).put(EntityType.BEE, Bee.createAttributes().build()).put(EntityType.BLAZE, Blaze.createAttributes().build()).put(EntityType.CAT, Cat.createAttributes().build()).put(EntityType.CAVE_SPIDER, CaveSpider.createCaveSpider().build()).put(EntityType.CHICKEN, Chicken.createAttributes().build()).put(EntityType.COD, AbstractFish.createAttributes().build()).put(EntityType.COW, Cow.createAttributes().build()).put(EntityType.CREEPER, Creeper.createAttributes().build()).put(EntityType.DOLPHIN, Dolphin.createAttributes().build()).put(EntityType.DONKEY, AbstractChestedHorse.createBaseChestedHorseAttributes().build()).put(EntityType.DROWNED, Zombie.createAttributes().build()).put(EntityType.ELDER_GUARDIAN, ElderGuardian.createAttributes().build()).put(EntityType.ENDERMAN, EnderMan.createAttributes().build()).put(EntityType.ENDERMITE, Endermite.createAttributes().build()).put(EntityType.ENDER_DRAGON, EnderDragon.createAttributes().build()).put(EntityType.EVOKER, Evoker.createAttributes().build()).put(EntityType.FOX, Fox.createAttributes().build()).put(EntityType.GHAST, Ghast.createAttributes().build()).put(EntityType.GIANT, Giant.createAttributes().build()).put(EntityType.GLOW_SQUID, GlowSquid.createAttributes().build()).put(EntityType.GOAT, Goat.createAttributes().build()).put(EntityType.GUARDIAN, Guardian.createAttributes().build()).put(EntityType.HOGLIN, Hoglin.createAttributes().build()).put(EntityType.HORSE, AbstractHorse.createBaseHorseAttributes().build()).put(EntityType.HUSK, Zombie.createAttributes().build()).put(EntityType.ILLUSIONER, Illusioner.createAttributes().build()).put(EntityType.IRON_GOLEM, IronGolem.createAttributes().build()).put(EntityType.LLAMA, Llama.createAttributes().build()).put(EntityType.MAGMA_CUBE, MagmaCube.createAttributes().build()).put(EntityType.MOOSHROOM, Cow.createAttributes().build()).put(EntityType.MULE, AbstractChestedHorse.createBaseChestedHorseAttributes().build()).put(EntityType.OCELOT, Ocelot.createAttributes().build()).put(EntityType.PANDA, Panda.createAttributes().build()).put(EntityType.PARROT, Parrot.createAttributes().build()).put(EntityType.PHANTOM, Monster.createMonsterAttributes().build()).put(EntityType.PIG, Pig.createAttributes().build()).put(EntityType.PIGLIN, Piglin.createAttributes().build()).put(EntityType.PIGLIN_BRUTE, PiglinBrute.createAttributes().build()).put(EntityType.PILLAGER, Pillager.createAttributes().build()).put(EntityType.PLAYER, Player.createAttributes().build()).put(EntityType.POLAR_BEAR, PolarBear.createAttributes().build()).put(EntityType.PUFFERFISH, AbstractFish.createAttributes().build()).put(EntityType.RABBIT, Rabbit.createAttributes().build()).put(EntityType.RAVAGER, Ravager.createAttributes().build()).put(EntityType.SALMON, AbstractFish.createAttributes().build()).put(EntityType.SHEEP, Sheep.createAttributes().build()).put(EntityType.SHULKER, Shulker.createAttributes().build()).put(EntityType.SILVERFISH, Silverfish.createAttributes().build()).put(EntityType.SKELETON, AbstractSkeleton.createAttributes().build()).put(EntityType.SKELETON_HORSE, SkeletonHorse.createAttributes().build()).put(EntityType.SLIME, Monster.createMonsterAttributes().build()).put(EntityType.SNOW_GOLEM, SnowGolem.createAttributes().build()).put(EntityType.SPIDER, Spider.createAttributes().build()).put(EntityType.SQUID, Squid.createAttributes().build()).put(EntityType.STRAY, AbstractSkeleton.createAttributes().build()).put(EntityType.STRIDER, Strider.createAttributes().build()).put(EntityType.TRADER_LLAMA, Llama.createAttributes().build()).put(EntityType.TROPICAL_FISH, AbstractFish.createAttributes().build()).put(EntityType.TURTLE, Turtle.createAttributes().build()).put(EntityType.VEX, Vex.createAttributes().build()).put(EntityType.VILLAGER, Villager.createAttributes().build()).put(EntityType.VINDICATOR, Vindicator.createAttributes().build()).put(EntityType.WANDERING_TRADER, Mob.createMobAttributes().build()).put(EntityType.WITCH, Witch.createAttributes().build()).put(EntityType.WITHER, WitherBoss.createAttributes().build()).put(EntityType.WITHER_SKELETON, AbstractSkeleton.createAttributes().build()).put(EntityType.WOLF, Wolf.createAttributes().build()).put(EntityType.ZOGLIN, Zoglin.createAttributes().build()).put(EntityType.ZOMBIE, Zombie.createAttributes().build()).put(EntityType.ZOMBIE_HORSE, ZombieHorse.createAttributes().build()).put(EntityType.ZOMBIE_VILLAGER, Zombie.createAttributes().build()).put(EntityType.ZOMBIFIED_PIGLIN, ZombifiedPiglin.createAttributes().build()).build();

   public static AttributeSupplier getSupplier(EntityType<? extends LivingEntity> p_22298_) {
      AttributeSupplier supplier = net.minecraftforge.common.ForgeHooks.getAttributesView().get(p_22298_);
      return supplier != null ? supplier : SUPPLIERS.get(p_22298_);
   }

   public static boolean hasSupplier(EntityType<?> p_22302_) {
      return SUPPLIERS.containsKey(p_22302_) || net.minecraftforge.common.ForgeHooks.getAttributesView().containsKey(p_22302_);
   }

   public static void validate() {
      Registry.ENTITY_TYPE.stream().filter((p_22306_) -> {
         return p_22306_.getCategory() != MobCategory.MISC;
      }).filter((p_22304_) -> {
         return !hasSupplier(p_22304_);
      }).map(Registry.ENTITY_TYPE::getKey).forEach((p_22300_) -> {
         Util.logAndPauseIfInIde("Entity " + p_22300_ + " has no attributes");
      });
   }
}
