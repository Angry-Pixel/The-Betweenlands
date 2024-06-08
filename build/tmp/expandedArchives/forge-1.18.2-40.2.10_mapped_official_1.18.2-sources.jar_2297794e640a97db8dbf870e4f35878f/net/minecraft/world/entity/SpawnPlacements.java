package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

public class SpawnPlacements {
   private static final Map<EntityType<?>, SpawnPlacements.Data> DATA_BY_TYPE = Maps.newHashMap();

   public static <T extends Mob> void register(EntityType<T> p_21755_, SpawnPlacements.Type p_21756_, Heightmap.Types p_21757_, SpawnPlacements.SpawnPredicate<T> p_21758_) {
      SpawnPlacements.Data spawnplacements$data = DATA_BY_TYPE.put(p_21755_, new SpawnPlacements.Data(p_21757_, p_21756_, p_21758_));
      if (spawnplacements$data != null) {
         throw new IllegalStateException("Duplicate registration for type " + Registry.ENTITY_TYPE.getKey(p_21755_));
      }
   }

   public static SpawnPlacements.Type getPlacementType(EntityType<?> p_21753_) {
      SpawnPlacements.Data spawnplacements$data = DATA_BY_TYPE.get(p_21753_);
      return spawnplacements$data == null ? SpawnPlacements.Type.NO_RESTRICTIONS : spawnplacements$data.placement;
   }

   public static Heightmap.Types getHeightmapType(@Nullable EntityType<?> p_21766_) {
      SpawnPlacements.Data spawnplacements$data = DATA_BY_TYPE.get(p_21766_);
      return spawnplacements$data == null ? Heightmap.Types.MOTION_BLOCKING_NO_LEAVES : spawnplacements$data.heightMap;
   }

   public static <T extends Entity> boolean checkSpawnRules(EntityType<T> p_21760_, ServerLevelAccessor p_21761_, MobSpawnType p_21762_, BlockPos p_21763_, Random p_21764_) {
      SpawnPlacements.Data spawnplacements$data = DATA_BY_TYPE.get(p_21760_);
      return spawnplacements$data == null || spawnplacements$data.predicate.test((EntityType)p_21760_, p_21761_, p_21762_, p_21763_, p_21764_);
   }

   static {
      register(EntityType.AXOLOTL, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Axolotl::checkAxolotlSpawnRules);
      register(EntityType.COD, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
      register(EntityType.DOLPHIN, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
      register(EntityType.DROWNED, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Drowned::checkDrownedSpawnRules);
      register(EntityType.GUARDIAN, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Guardian::checkGuardianSpawnRules);
      register(EntityType.PUFFERFISH, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
      register(EntityType.SALMON, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
      register(EntityType.SQUID, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
      register(EntityType.TROPICAL_FISH, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TropicalFish::checkTropicalFishSpawnRules);
      register(EntityType.BAT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Bat::checkBatSpawnRules);
      register(EntityType.BLAZE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
      register(EntityType.CAVE_SPIDER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.CHICKEN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.COW, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.CREEPER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.DONKEY, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.ENDERMAN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.ENDERMITE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Endermite::checkEndermiteSpawnRules);
      register(EntityType.ENDER_DRAGON, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.GHAST, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Ghast::checkGhastSpawnRules);
      register(EntityType.GIANT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.GLOW_SQUID, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GlowSquid::checkGlowSquideSpawnRules);
      register(EntityType.GOAT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Goat::checkGoatSpawnRules);
      register(EntityType.HORSE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.HUSK, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Husk::checkHuskSpawnRules);
      register(EntityType.IRON_GOLEM, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.LLAMA, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.MAGMA_CUBE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MagmaCube::checkMagmaCubeSpawnRules);
      register(EntityType.MOOSHROOM, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MushroomCow::checkMushroomSpawnRules);
      register(EntityType.MULE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.OCELOT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Ocelot::checkOcelotSpawnRules);
      register(EntityType.PARROT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Parrot::checkParrotSpawnRules);
      register(EntityType.PIG, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.HOGLIN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Hoglin::checkHoglinSpawnRules);
      register(EntityType.PIGLIN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Piglin::checkPiglinSpawnRules);
      register(EntityType.PILLAGER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PatrollingMonster::checkPatrollingMonsterSpawnRules);
      register(EntityType.POLAR_BEAR, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PolarBear::checkPolarBearSpawnRules);
      register(EntityType.RABBIT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Rabbit::checkRabbitSpawnRules);
      register(EntityType.SHEEP, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.SILVERFISH, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Silverfish::checkSilverfishSpawnRules);
      register(EntityType.SKELETON, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.SKELETON_HORSE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.SLIME, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Slime::checkSlimeSpawnRules);
      register(EntityType.SNOW_GOLEM, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.SPIDER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.STRAY, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Stray::checkStraySpawnRules);
      register(EntityType.STRIDER, SpawnPlacements.Type.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Strider::checkStriderSpawnRules);
      register(EntityType.TURTLE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Turtle::checkTurtleSpawnRules);
      register(EntityType.VILLAGER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.WITCH, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.WITHER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.WITHER_SKELETON, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.WOLF, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Wolf::checkWolfSpawnRules);
      register(EntityType.ZOMBIE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.ZOMBIE_HORSE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.ZOMBIFIED_PIGLIN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ZombifiedPiglin::checkZombifiedPiglinSpawnRules);
      register(EntityType.ZOMBIE_VILLAGER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.CAT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.ELDER_GUARDIAN, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Guardian::checkGuardianSpawnRules);
      register(EntityType.EVOKER, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.FOX, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Fox::checkFoxSpawnRules);
      register(EntityType.ILLUSIONER, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.PANDA, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.PHANTOM, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.RAVAGER, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.SHULKER, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.TRADER_LLAMA, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.VEX, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.VINDICATOR, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.WANDERING_TRADER, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
   }

   static class Data {
      final Heightmap.Types heightMap;
      final SpawnPlacements.Type placement;
      final SpawnPlacements.SpawnPredicate<?> predicate;

      public Data(Heightmap.Types p_21771_, SpawnPlacements.Type p_21772_, SpawnPlacements.SpawnPredicate<?> p_21773_) {
         this.heightMap = p_21771_;
         this.placement = p_21772_;
         this.predicate = p_21773_;
      }
   }

   @FunctionalInterface
   public interface SpawnPredicate<T extends Entity> {
      boolean test(EntityType<T> p_21781_, ServerLevelAccessor p_21782_, MobSpawnType p_21783_, BlockPos p_21784_, Random p_21785_);
   }

   public static enum Type implements net.minecraftforge.common.IExtensibleEnum {
      ON_GROUND,
      IN_WATER,
      NO_RESTRICTIONS,
      IN_LAVA;

      public static Type create(String name, net.minecraftforge.common.util.TriPredicate<net.minecraft.world.level.LevelReader, BlockPos, EntityType<? extends Mob>> predicate) {
          throw new IllegalStateException("Enum not extended");
      }

      private net.minecraftforge.common.util.TriPredicate<net.minecraft.world.level.LevelReader, BlockPos, EntityType<?>> predicate;
      private Type() { this(null); }
      private Type(net.minecraftforge.common.util.TriPredicate<net.minecraft.world.level.LevelReader, BlockPos, EntityType<?>> predicate) {
          this.predicate = predicate;
      }

      public boolean canSpawnAt(net.minecraft.world.level.LevelReader world, BlockPos pos, EntityType<?> type) {
          if (this == NO_RESTRICTIONS) return true;
          if (predicate == null) return net.minecraft.world.level.NaturalSpawner.canSpawnAtBody(this, world, pos, type);
          return predicate.test(world, pos, type);
      }
   }
}
