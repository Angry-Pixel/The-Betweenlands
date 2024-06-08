package net.minecraft.world.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class EntityType<T extends Entity> extends net.minecraftforge.registries.ForgeRegistryEntry<EntityType<?>> implements EntityTypeTest<Entity, T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String ENTITY_TAG = "EntityTag";
   private final Holder.Reference<EntityType<?>> builtInRegistryHolder = Registry.ENTITY_TYPE.createIntrusiveHolder(this);
   private static final float MAGIC_HORSE_WIDTH = 1.3964844F;
   public static final EntityType<AreaEffectCloud> AREA_EFFECT_CLOUD = register("area_effect_cloud", EntityType.Builder.<AreaEffectCloud>of(AreaEffectCloud::new, MobCategory.MISC).fireImmune().sized(6.0F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<ArmorStand> ARMOR_STAND = register("armor_stand", EntityType.Builder.<ArmorStand>of(ArmorStand::new, MobCategory.MISC).sized(0.5F, 1.975F).clientTrackingRange(10));
   public static final EntityType<Arrow> ARROW = register("arrow", EntityType.Builder.<Arrow>of(Arrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<Axolotl> AXOLOTL = register("axolotl", EntityType.Builder.<Axolotl>of(Axolotl::new, MobCategory.AXOLOTLS).sized(0.75F, 0.42F).clientTrackingRange(10));
   public static final EntityType<Bat> BAT = register("bat", EntityType.Builder.<Bat>of(Bat::new, MobCategory.AMBIENT).sized(0.5F, 0.9F).clientTrackingRange(5));
   public static final EntityType<Bee> BEE = register("bee", EntityType.Builder.<Bee>of(Bee::new, MobCategory.CREATURE).sized(0.7F, 0.6F).clientTrackingRange(8));
   public static final EntityType<Blaze> BLAZE = register("blaze", EntityType.Builder.<Blaze>of(Blaze::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(8));
   public static final EntityType<Boat> BOAT = register("boat", EntityType.Builder.<Boat>of(Boat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10));
   public static final EntityType<Cat> CAT = register("cat", EntityType.Builder.<Cat>of(Cat::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8));
   public static final EntityType<CaveSpider> CAVE_SPIDER = register("cave_spider", EntityType.Builder.<CaveSpider>of(CaveSpider::new, MobCategory.MONSTER).sized(0.7F, 0.5F).clientTrackingRange(8));
   public static final EntityType<Chicken> CHICKEN = register("chicken", EntityType.Builder.<Chicken>of(Chicken::new, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10));
   public static final EntityType<Cod> COD = register("cod", EntityType.Builder.<Cod>of(Cod::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
   public static final EntityType<Cow> COW = register("cow", EntityType.Builder.<Cow>of(Cow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10));
   public static final EntityType<Creeper> CREEPER = register("creeper", EntityType.Builder.<Creeper>of(Creeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8));
   public static final EntityType<Dolphin> DOLPHIN = register("dolphin", EntityType.Builder.<Dolphin>of(Dolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F));
   public static final EntityType<Donkey> DONKEY = register("donkey", EntityType.Builder.<Donkey>of(Donkey::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F).clientTrackingRange(10));
   public static final EntityType<DragonFireball> DRAGON_FIREBALL = register("dragon_fireball", EntityType.Builder.<DragonFireball>of(DragonFireball::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<Drowned> DROWNED = register("drowned", EntityType.Builder.<Drowned>of(Drowned::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<ElderGuardian> ELDER_GUARDIAN = register("elder_guardian", EntityType.Builder.<ElderGuardian>of(ElderGuardian::new, MobCategory.MONSTER).sized(1.9975F, 1.9975F).clientTrackingRange(10));
   public static final EntityType<EndCrystal> END_CRYSTAL = register("end_crystal", EntityType.Builder.<EndCrystal>of(EndCrystal::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<EnderDragon> ENDER_DRAGON = register("ender_dragon", EntityType.Builder.<EnderDragon>of(EnderDragon::new, MobCategory.MONSTER).fireImmune().sized(16.0F, 8.0F).clientTrackingRange(10));
   public static final EntityType<EnderMan> ENDERMAN = register("enderman", EntityType.Builder.<EnderMan>of(EnderMan::new, MobCategory.MONSTER).sized(0.6F, 2.9F).clientTrackingRange(8));
   public static final EntityType<Endermite> ENDERMITE = register("endermite", EntityType.Builder.<Endermite>of(Endermite::new, MobCategory.MONSTER).sized(0.4F, 0.3F).clientTrackingRange(8));
   public static final EntityType<Evoker> EVOKER = register("evoker", EntityType.Builder.<Evoker>of(Evoker::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<EvokerFangs> EVOKER_FANGS = register("evoker_fangs", EntityType.Builder.<EvokerFangs>of(EvokerFangs::new, MobCategory.MISC).sized(0.5F, 0.8F).clientTrackingRange(6).updateInterval(2));
   public static final EntityType<ExperienceOrb> EXPERIENCE_ORB = register("experience_orb", EntityType.Builder.<ExperienceOrb>of(ExperienceOrb::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20));
   public static final EntityType<EyeOfEnder> EYE_OF_ENDER = register("eye_of_ender", EntityType.Builder.<EyeOfEnder>of(EyeOfEnder::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(4));
   public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register("falling_block", EntityType.Builder.<FallingBlockEntity>of(FallingBlockEntity::new, MobCategory.MISC).sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20));
   public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = register("firework_rocket", EntityType.Builder.<FireworkRocketEntity>of(FireworkRocketEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<Fox> FOX = register("fox", EntityType.Builder.<Fox>of(Fox::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8).immuneTo(Blocks.SWEET_BERRY_BUSH));
   public static final EntityType<Ghast> GHAST = register("ghast", EntityType.Builder.<Ghast>of(Ghast::new, MobCategory.MONSTER).fireImmune().sized(4.0F, 4.0F).clientTrackingRange(10));
   public static final EntityType<Giant> GIANT = register("giant", EntityType.Builder.<Giant>of(Giant::new, MobCategory.MONSTER).sized(3.6F, 12.0F).clientTrackingRange(10));
   public static final EntityType<GlowItemFrame> GLOW_ITEM_FRAME = register("glow_item_frame", EntityType.Builder.<GlowItemFrame>of(GlowItemFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<GlowSquid> GLOW_SQUID = register("glow_squid", EntityType.Builder.<GlowSquid>of(GlowSquid::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(10));
   public static final EntityType<Goat> GOAT = register("goat", EntityType.Builder.<Goat>of(Goat::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10));
   public static final EntityType<Guardian> GUARDIAN = register("guardian", EntityType.Builder.<Guardian>of(Guardian::new, MobCategory.MONSTER).sized(0.85F, 0.85F).clientTrackingRange(8));
   public static final EntityType<Hoglin> HOGLIN = register("hoglin", EntityType.Builder.<Hoglin>of(Hoglin::new, MobCategory.MONSTER).sized(1.3964844F, 1.4F).clientTrackingRange(8));
   public static final EntityType<Horse> HORSE = register("horse", EntityType.Builder.<Horse>of(Horse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
   public static final EntityType<Husk> HUSK = register("husk", EntityType.Builder.<Husk>of(Husk::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<Illusioner> ILLUSIONER = register("illusioner", EntityType.Builder.<Illusioner>of(Illusioner::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<IronGolem> IRON_GOLEM = register("iron_golem", EntityType.Builder.<IronGolem>of(IronGolem::new, MobCategory.MISC).sized(1.4F, 2.7F).clientTrackingRange(10));
   public static final EntityType<ItemEntity> ITEM = register("item", EntityType.Builder.<ItemEntity>of(ItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20));
   public static final EntityType<ItemFrame> ITEM_FRAME = register("item_frame", EntityType.Builder.<ItemFrame>of(ItemFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<LargeFireball> FIREBALL = register("fireball", EntityType.Builder.<LargeFireball>of(LargeFireball::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<LeashFenceKnotEntity> LEASH_KNOT = register("leash_knot", EntityType.Builder.<LeashFenceKnotEntity>of(LeashFenceKnotEntity::new, MobCategory.MISC).noSave().sized(0.375F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<LightningBolt> LIGHTNING_BOLT = register("lightning_bolt", EntityType.Builder.<LightningBolt>of(LightningBolt::new, MobCategory.MISC).noSave().sized(0.0F, 0.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<Llama> LLAMA = register("llama", EntityType.Builder.<Llama>of(Llama::new, MobCategory.CREATURE).sized(0.9F, 1.87F).clientTrackingRange(10));
   public static final EntityType<LlamaSpit> LLAMA_SPIT = register("llama_spit", EntityType.Builder.<LlamaSpit>of(LlamaSpit::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<MagmaCube> MAGMA_CUBE = register("magma_cube", EntityType.Builder.<MagmaCube>of(MagmaCube::new, MobCategory.MONSTER).fireImmune().sized(2.04F, 2.04F).clientTrackingRange(8));
   public static final EntityType<Marker> MARKER = register("marker", EntityType.Builder.<Marker>of(Marker::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(0));
   public static final EntityType<Minecart> MINECART = register("minecart", EntityType.Builder.<Minecart>of(Minecart::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<MinecartChest> CHEST_MINECART = register("chest_minecart", EntityType.Builder.<MinecartChest>of(MinecartChest::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<MinecartCommandBlock> COMMAND_BLOCK_MINECART = register("command_block_minecart", EntityType.Builder.<MinecartCommandBlock>of(MinecartCommandBlock::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<MinecartFurnace> FURNACE_MINECART = register("furnace_minecart", EntityType.Builder.<MinecartFurnace>of(MinecartFurnace::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<MinecartHopper> HOPPER_MINECART = register("hopper_minecart", EntityType.Builder.<MinecartHopper>of(MinecartHopper::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<MinecartSpawner> SPAWNER_MINECART = register("spawner_minecart", EntityType.Builder.<MinecartSpawner>of(MinecartSpawner::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<MinecartTNT> TNT_MINECART = register("tnt_minecart", EntityType.Builder.<MinecartTNT>of(MinecartTNT::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<Mule> MULE = register("mule", EntityType.Builder.<Mule>of(Mule::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(8));
   public static final EntityType<MushroomCow> MOOSHROOM = register("mooshroom", EntityType.Builder.<MushroomCow>of(MushroomCow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10));
   public static final EntityType<Ocelot> OCELOT = register("ocelot", EntityType.Builder.<Ocelot>of(Ocelot::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(10));
   public static final EntityType<Painting> PAINTING = register("painting", EntityType.Builder.<Painting>of(Painting::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<Panda> PANDA = register("panda", EntityType.Builder.<Panda>of(Panda::new, MobCategory.CREATURE).sized(1.3F, 1.25F).clientTrackingRange(10));
   public static final EntityType<Parrot> PARROT = register("parrot", EntityType.Builder.<Parrot>of(Parrot::new, MobCategory.CREATURE).sized(0.5F, 0.9F).clientTrackingRange(8));
   public static final EntityType<Phantom> PHANTOM = register("phantom", EntityType.Builder.<Phantom>of(Phantom::new, MobCategory.MONSTER).sized(0.9F, 0.5F).clientTrackingRange(8));
   public static final EntityType<Pig> PIG = register("pig", EntityType.Builder.<Pig>of(Pig::new, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10));
   public static final EntityType<Piglin> PIGLIN = register("piglin", EntityType.Builder.<Piglin>of(Piglin::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<PiglinBrute> PIGLIN_BRUTE = register("piglin_brute", EntityType.Builder.<PiglinBrute>of(PiglinBrute::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<Pillager> PILLAGER = register("pillager", EntityType.Builder.<Pillager>of(Pillager::new, MobCategory.MONSTER).canSpawnFarFromPlayer().sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<PolarBear> POLAR_BEAR = register("polar_bear", EntityType.Builder.<PolarBear>of(PolarBear::new, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.4F, 1.4F).clientTrackingRange(10));
   public static final EntityType<PrimedTnt> TNT = register("tnt", EntityType.Builder.<PrimedTnt>of(PrimedTnt::new, MobCategory.MISC).fireImmune().sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10));
   public static final EntityType<Pufferfish> PUFFERFISH = register("pufferfish", EntityType.Builder.<Pufferfish>of(Pufferfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.7F).clientTrackingRange(4));
   public static final EntityType<Rabbit> RABBIT = register("rabbit", EntityType.Builder.<Rabbit>of(Rabbit::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8));
   public static final EntityType<Ravager> RAVAGER = register("ravager", EntityType.Builder.<Ravager>of(Ravager::new, MobCategory.MONSTER).sized(1.95F, 2.2F).clientTrackingRange(10));
   public static final EntityType<Salmon> SALMON = register("salmon", EntityType.Builder.<Salmon>of(Salmon::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).clientTrackingRange(4));
   public static final EntityType<Sheep> SHEEP = register("sheep", EntityType.Builder.<Sheep>of(Sheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10));
   public static final EntityType<Shulker> SHULKER = register("shulker", EntityType.Builder.<Shulker>of(Shulker::new, MobCategory.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(1.0F, 1.0F).clientTrackingRange(10));
   public static final EntityType<ShulkerBullet> SHULKER_BULLET = register("shulker_bullet", EntityType.Builder.<ShulkerBullet>of(ShulkerBullet::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(8));
   public static final EntityType<Silverfish> SILVERFISH = register("silverfish", EntityType.Builder.<Silverfish>of(Silverfish::new, MobCategory.MONSTER).sized(0.4F, 0.3F).clientTrackingRange(8));
   public static final EntityType<Skeleton> SKELETON = register("skeleton", EntityType.Builder.<Skeleton>of(Skeleton::new, MobCategory.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8));
   public static final EntityType<SkeletonHorse> SKELETON_HORSE = register("skeleton_horse", EntityType.Builder.<SkeletonHorse>of(SkeletonHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
   public static final EntityType<Slime> SLIME = register("slime", EntityType.Builder.<Slime>of(Slime::new, MobCategory.MONSTER).sized(2.04F, 2.04F).clientTrackingRange(10));
   public static final EntityType<SmallFireball> SMALL_FIREBALL = register("small_fireball", EntityType.Builder.<SmallFireball>of(SmallFireball::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<SnowGolem> SNOW_GOLEM = register("snow_golem", EntityType.Builder.<SnowGolem>of(SnowGolem::new, MobCategory.MISC).immuneTo(Blocks.POWDER_SNOW).sized(0.7F, 1.9F).clientTrackingRange(8));
   public static final EntityType<Snowball> SNOWBALL = register("snowball", EntityType.Builder.<Snowball>of(Snowball::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<SpectralArrow> SPECTRAL_ARROW = register("spectral_arrow", EntityType.Builder.<SpectralArrow>of(SpectralArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<Spider> SPIDER = register("spider", EntityType.Builder.<Spider>of(Spider::new, MobCategory.MONSTER).sized(1.4F, 0.9F).clientTrackingRange(8));
   public static final EntityType<Squid> SQUID = register("squid", EntityType.Builder.<Squid>of(Squid::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(8));
   public static final EntityType<Stray> STRAY = register("stray", EntityType.Builder.<Stray>of(Stray::new, MobCategory.MONSTER).sized(0.6F, 1.99F).immuneTo(Blocks.POWDER_SNOW).clientTrackingRange(8));
   public static final EntityType<Strider> STRIDER = register("strider", EntityType.Builder.<Strider>of(Strider::new, MobCategory.CREATURE).fireImmune().sized(0.9F, 1.7F).clientTrackingRange(10));
   public static final EntityType<ThrownEgg> EGG = register("egg", EntityType.Builder.<ThrownEgg>of(ThrownEgg::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<ThrownEnderpearl> ENDER_PEARL = register("ender_pearl", EntityType.Builder.<ThrownEnderpearl>of(ThrownEnderpearl::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<ThrownExperienceBottle> EXPERIENCE_BOTTLE = register("experience_bottle", EntityType.Builder.<ThrownExperienceBottle>of(ThrownExperienceBottle::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<ThrownPotion> POTION = register("potion", EntityType.Builder.<ThrownPotion>of(ThrownPotion::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<ThrownTrident> TRIDENT = register("trident", EntityType.Builder.<ThrownTrident>of(ThrownTrident::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<TraderLlama> TRADER_LLAMA = register("trader_llama", EntityType.Builder.<TraderLlama>of(TraderLlama::new, MobCategory.CREATURE).sized(0.9F, 1.87F).clientTrackingRange(10));
   public static final EntityType<TropicalFish> TROPICAL_FISH = register("tropical_fish", EntityType.Builder.<TropicalFish>of(TropicalFish::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.4F).clientTrackingRange(4));
   public static final EntityType<Turtle> TURTLE = register("turtle", EntityType.Builder.<Turtle>of(Turtle::new, MobCategory.CREATURE).sized(1.2F, 0.4F).clientTrackingRange(10));
   public static final EntityType<Vex> VEX = register("vex", EntityType.Builder.<Vex>of(Vex::new, MobCategory.MONSTER).fireImmune().sized(0.4F, 0.8F).clientTrackingRange(8));
   public static final EntityType<Villager> VILLAGER = register("villager", EntityType.Builder.<Villager>of(Villager::new, MobCategory.MISC).sized(0.6F, 1.95F).clientTrackingRange(10));
   public static final EntityType<Vindicator> VINDICATOR = register("vindicator", EntityType.Builder.<Vindicator>of(Vindicator::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<WanderingTrader> WANDERING_TRADER = register("wandering_trader", EntityType.Builder.<WanderingTrader>of(WanderingTrader::new, MobCategory.CREATURE).sized(0.6F, 1.95F).clientTrackingRange(10));
   public static final EntityType<Witch> WITCH = register("witch", EntityType.Builder.<Witch>of(Witch::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<WitherBoss> WITHER = register("wither", EntityType.Builder.<WitherBoss>of(WitherBoss::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.9F, 3.5F).clientTrackingRange(10));
   public static final EntityType<WitherSkeleton> WITHER_SKELETON = register("wither_skeleton", EntityType.Builder.<WitherSkeleton>of(WitherSkeleton::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.7F, 2.4F).clientTrackingRange(8));
   public static final EntityType<WitherSkull> WITHER_SKULL = register("wither_skull", EntityType.Builder.<WitherSkull>of(WitherSkull::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<Wolf> WOLF = register("wolf", EntityType.Builder.<Wolf>of(Wolf::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));
   public static final EntityType<Zoglin> ZOGLIN = register("zoglin", EntityType.Builder.<Zoglin>of(Zoglin::new, MobCategory.MONSTER).fireImmune().sized(1.3964844F, 1.4F).clientTrackingRange(8));
   public static final EntityType<Zombie> ZOMBIE = register("zombie", EntityType.Builder.<Zombie>of(Zombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<ZombieHorse> ZOMBIE_HORSE = register("zombie_horse", EntityType.Builder.<ZombieHorse>of(ZombieHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
   public static final EntityType<ZombieVillager> ZOMBIE_VILLAGER = register("zombie_villager", EntityType.Builder.<ZombieVillager>of(ZombieVillager::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<ZombifiedPiglin> ZOMBIFIED_PIGLIN = register("zombified_piglin", EntityType.Builder.<ZombifiedPiglin>of(ZombifiedPiglin::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<Player> PLAYER = register("player", EntityType.Builder.<Player>createNothing(MobCategory.MISC).noSave().noSummon().sized(0.6F, 1.8F).clientTrackingRange(32).updateInterval(2));
   public static final EntityType<FishingHook> FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.<FishingHook>of(FishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
   private final EntityType.EntityFactory<T> factory;
   private final MobCategory category;
   private final ImmutableSet<Block> immuneTo;
   private final boolean serialize;
   private final boolean summon;
   private final boolean fireImmune;
   private final boolean canSpawnFarFromPlayer;
   private final int clientTrackingRange;
   private final int updateInterval;
   @Nullable
   private String descriptionId;
   @Nullable
   private Component description;
   @Nullable
   private ResourceLocation lootTable;
   private final EntityDimensions dimensions;

   private final java.util.function.Predicate<EntityType<?>> velocityUpdateSupplier;
   private final java.util.function.ToIntFunction<EntityType<?>> trackingRangeSupplier;
   private final java.util.function.ToIntFunction<EntityType<?>> updateIntervalSupplier;
   private final java.util.function.BiFunction<net.minecraftforge.network.PlayMessages.SpawnEntity, Level, T> customClientFactory;

   private static <T extends Entity> EntityType<T> register(String p_20635_, EntityType.Builder<T> p_20636_) {
      return Registry.register(Registry.ENTITY_TYPE, p_20635_, p_20636_.build(p_20635_));
   }

   public static ResourceLocation getKey(EntityType<?> p_20614_) {
      return Registry.ENTITY_TYPE.getKey(p_20614_);
   }

   public static Optional<EntityType<?>> byString(String p_20633_) {
      return Registry.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(p_20633_));
   }

   public EntityType(EntityType.EntityFactory<T> p_20574_, MobCategory p_20575_, boolean p_20576_, boolean p_20577_, boolean p_20578_, boolean p_20579_, ImmutableSet<Block> p_20580_, EntityDimensions p_20581_, int p_20582_, int p_20583_) {
       this(p_20574_, p_20575_, p_20576_, p_20577_, p_20578_, p_20579_, p_20580_, p_20581_, p_20582_, p_20583_, EntityType::defaultVelocitySupplier, EntityType::defaultTrackingRangeSupplier, EntityType::defaultUpdateIntervalSupplier, null);
   }
   public EntityType(EntityType.EntityFactory<T> p_20574_, MobCategory p_20575_, boolean p_20576_, boolean p_20577_, boolean p_20578_, boolean p_20579_, ImmutableSet<Block> p_20580_, EntityDimensions p_20581_, int p_20582_, int p_20583_, final java.util.function.Predicate<EntityType<?>> velocityUpdateSupplier, final java.util.function.ToIntFunction<EntityType<?>> trackingRangeSupplier, final java.util.function.ToIntFunction<EntityType<?>> updateIntervalSupplier, final java.util.function.BiFunction<net.minecraftforge.network.PlayMessages.SpawnEntity, Level, T> customClientFactory) {
      this.factory = p_20574_;
      this.category = p_20575_;
      this.canSpawnFarFromPlayer = p_20579_;
      this.serialize = p_20576_;
      this.summon = p_20577_;
      this.fireImmune = p_20578_;
      this.immuneTo = p_20580_;
      this.dimensions = p_20581_;
      this.clientTrackingRange = p_20582_;
      this.updateInterval = p_20583_;
      this.velocityUpdateSupplier = velocityUpdateSupplier;
      this.trackingRangeSupplier = trackingRangeSupplier;
      this.updateIntervalSupplier = updateIntervalSupplier;
      this.customClientFactory = customClientFactory;
   }

   @Nullable
   public Entity spawn(ServerLevel p_20593_, @Nullable ItemStack p_20594_, @Nullable Player p_20595_, BlockPos p_20596_, MobSpawnType p_20597_, boolean p_20598_, boolean p_20599_) {
      return this.spawn(p_20593_, p_20594_ == null ? null : p_20594_.getTag(), p_20594_ != null && p_20594_.hasCustomHoverName() ? p_20594_.getHoverName() : null, p_20595_, p_20596_, p_20597_, p_20598_, p_20599_);
   }

   @Nullable
   public T spawn(ServerLevel p_20601_, @Nullable CompoundTag p_20602_, @Nullable Component p_20603_, @Nullable Player p_20604_, BlockPos p_20605_, MobSpawnType p_20606_, boolean p_20607_, boolean p_20608_) {
      T t = this.create(p_20601_, p_20602_, p_20603_, p_20604_, p_20605_, p_20606_, p_20607_, p_20608_);
      if (t != null) {
         p_20601_.addFreshEntityWithPassengers(t);
      }

      return t;
   }

   @Nullable
   public T create(ServerLevel p_20656_, @Nullable CompoundTag p_20657_, @Nullable Component p_20658_, @Nullable Player p_20659_, BlockPos p_20660_, MobSpawnType p_20661_, boolean p_20662_, boolean p_20663_) {
      T t = this.create(p_20656_);
      if (t == null) {
         return (T)null;
      } else {
         double d0;
         if (p_20662_) {
            t.setPos((double)p_20660_.getX() + 0.5D, (double)(p_20660_.getY() + 1), (double)p_20660_.getZ() + 0.5D);
            d0 = getYOffset(p_20656_, p_20660_, p_20663_, t.getBoundingBox());
         } else {
            d0 = 0.0D;
         }

         t.moveTo((double)p_20660_.getX() + 0.5D, (double)p_20660_.getY() + d0, (double)p_20660_.getZ() + 0.5D, Mth.wrapDegrees(p_20656_.random.nextFloat() * 360.0F), 0.0F);
         if (t instanceof Mob) {
            Mob mob = (Mob)t;
            mob.yHeadRot = mob.getYRot();
            mob.yBodyRot = mob.getYRot();
            if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(mob, p_20656_, (float) mob.getX(), (float) mob.getY(), (float) mob.getZ(), null, p_20661_))
            mob.finalizeSpawn(p_20656_, p_20656_.getCurrentDifficultyAt(mob.blockPosition()), p_20661_, (SpawnGroupData)null, p_20657_);
            mob.playAmbientSound();
         }

         if (p_20658_ != null && t instanceof LivingEntity) {
            t.setCustomName(p_20658_);
         }

         updateCustomEntityTag(p_20656_, p_20659_, t, p_20657_);
         return t;
      }
   }

   protected static double getYOffset(LevelReader p_20626_, BlockPos p_20627_, boolean p_20628_, AABB p_20629_) {
      AABB aabb = new AABB(p_20627_);
      if (p_20628_) {
         aabb = aabb.expandTowards(0.0D, -1.0D, 0.0D);
      }

      Iterable<VoxelShape> iterable = p_20626_.getCollisions((Entity)null, aabb);
      return 1.0D + Shapes.collide(Direction.Axis.Y, p_20629_, iterable, p_20628_ ? -2.0D : -1.0D);
   }

   public static void updateCustomEntityTag(Level p_20621_, @Nullable Player p_20622_, @Nullable Entity p_20623_, @Nullable CompoundTag p_20624_) {
      if (p_20624_ != null && p_20624_.contains("EntityTag", 10)) {
         MinecraftServer minecraftserver = p_20621_.getServer();
         if (minecraftserver != null && p_20623_ != null) {
            if (p_20621_.isClientSide || !p_20623_.onlyOpCanSetNbt() || p_20622_ != null && minecraftserver.getPlayerList().isOp(p_20622_.getGameProfile())) {
               CompoundTag compoundtag = p_20623_.saveWithoutId(new CompoundTag());
               UUID uuid = p_20623_.getUUID();
               compoundtag.merge(p_20624_.getCompound("EntityTag"));
               p_20623_.setUUID(uuid);
               p_20623_.load(compoundtag);
            }
         }
      }
   }

   public boolean canSerialize() {
      return this.serialize;
   }

   public boolean canSummon() {
      return this.summon;
   }

   public boolean fireImmune() {
      return this.fireImmune;
   }

   public boolean canSpawnFarFromPlayer() {
      return this.canSpawnFarFromPlayer;
   }

   public MobCategory getCategory() {
      return this.category;
   }

   public String getDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("entity", Registry.ENTITY_TYPE.getKey(this));
      }

      return this.descriptionId;
   }

   public Component getDescription() {
      if (this.description == null) {
         this.description = new TranslatableComponent(this.getDescriptionId());
      }

      return this.description;
   }

   public String toString() {
      return this.getDescriptionId();
   }

   public String toShortString() {
      int i = this.getDescriptionId().lastIndexOf(46);
      return i == -1 ? this.getDescriptionId() : this.getDescriptionId().substring(i + 1);
   }

   public ResourceLocation getDefaultLootTable() {
      if (this.lootTable == null) {
         ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(this);
         this.lootTable = new ResourceLocation(resourcelocation.getNamespace(), "entities/" + resourcelocation.getPath());
      }

      return this.lootTable;
   }

   public float getWidth() {
      return this.dimensions.width;
   }

   public float getHeight() {
      return this.dimensions.height;
   }

   @Nullable
   public T create(Level p_20616_) {
      return this.factory.create(this, p_20616_);
   }

   @Nullable
   public static Entity create(int p_20590_, Level p_20591_) {
      return create(p_20591_, Registry.ENTITY_TYPE.byId(p_20590_));
   }

   public static Optional<Entity> create(CompoundTag p_20643_, Level p_20644_) {
      return Util.ifElse(by(p_20643_).map((p_185998_) -> {
         return p_185998_.create(p_20644_);
      }), (p_185990_) -> {
         p_185990_.load(p_20643_);
      }, () -> {
         LOGGER.warn("Skipping Entity with id {}", (Object)p_20643_.getString("id"));
      });
   }

   @Nullable
   private static Entity create(Level p_20618_, @Nullable EntityType<?> p_20619_) {
      return p_20619_ == null ? null : p_20619_.create(p_20618_);
   }

   public AABB getAABB(double p_20586_, double p_20587_, double p_20588_) {
      float f = this.getWidth() / 2.0F;
      return new AABB(p_20586_ - (double)f, p_20587_, p_20588_ - (double)f, p_20586_ + (double)f, p_20587_ + (double)this.getHeight(), p_20588_ + (double)f);
   }

   public boolean isBlockDangerous(BlockState p_20631_) {
      if (this.immuneTo.contains(p_20631_.getBlock())) {
         return false;
      } else if (!this.fireImmune && WalkNodeEvaluator.isBurningBlock(p_20631_)) {
         return true;
      } else {
         return p_20631_.is(Blocks.WITHER_ROSE) || p_20631_.is(Blocks.SWEET_BERRY_BUSH) || p_20631_.is(Blocks.CACTUS) || p_20631_.is(Blocks.POWDER_SNOW);
      }
   }

   public EntityDimensions getDimensions() {
      return this.dimensions;
   }

   public static Optional<EntityType<?>> by(CompoundTag p_20638_) {
      return Registry.ENTITY_TYPE.getOptional(new ResourceLocation(p_20638_.getString("id")));
   }

   @Nullable
   public static Entity loadEntityRecursive(CompoundTag p_20646_, Level p_20647_, Function<Entity, Entity> p_20648_) {
      return loadStaticEntity(p_20646_, p_20647_).map(p_20648_).map((p_185995_) -> {
         if (p_20646_.contains("Passengers", 9)) {
            ListTag listtag = p_20646_.getList("Passengers", 10);

            for(int i = 0; i < listtag.size(); ++i) {
               Entity entity = loadEntityRecursive(listtag.getCompound(i), p_20647_, p_20648_);
               if (entity != null) {
                  entity.startRiding(p_185995_, true);
               }
            }
         }

         return p_185995_;
      }).orElse((Entity)null);
   }

   public static Stream<Entity> loadEntitiesRecursive(final List<? extends Tag> p_147046_, final Level p_147047_) {
      final Spliterator<? extends Tag> spliterator = p_147046_.spliterator();
      return StreamSupport.stream(new Spliterator<Entity>() {
         public boolean tryAdvance(Consumer<? super Entity> p_147066_) {
            return spliterator.tryAdvance((p_147059_) -> {
               EntityType.loadEntityRecursive((CompoundTag)p_147059_, p_147047_, (p_147062_) -> {
                  p_147066_.accept(p_147062_);
                  return p_147062_;
               });
            });
         }

         public Spliterator<Entity> trySplit() {
            return null;
         }

         public long estimateSize() {
            return (long)p_147046_.size();
         }

         public int characteristics() {
            return 1297;
         }
      }, false);
   }

   private static Optional<Entity> loadStaticEntity(CompoundTag p_20670_, Level p_20671_) {
      try {
         return create(p_20670_, p_20671_);
      } catch (RuntimeException runtimeexception) {
         LOGGER.warn("Exception loading entity: ", (Throwable)runtimeexception);
         return Optional.empty();
      }
   }

   public int clientTrackingRange() {
      return trackingRangeSupplier.applyAsInt(this);
   }
   private int defaultTrackingRangeSupplier() {
      return this.clientTrackingRange;
   }

   public int updateInterval() {
      return updateIntervalSupplier.applyAsInt(this);
   }
   private int defaultUpdateIntervalSupplier() {
      return this.updateInterval;
   }

   public boolean trackDeltas() {
      return velocityUpdateSupplier.test(this);
   }
   private boolean defaultVelocitySupplier() {
      return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != GLOW_ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
   }

   public boolean is(TagKey<EntityType<?>> p_204040_) {
      return this.builtInRegistryHolder.is(p_204040_);
   }

   @Nullable
   public T tryCast(Entity p_147042_) {
      return (T)(p_147042_.getType() == this ? p_147042_ : null);
   }

   public Class<? extends Entity> getBaseClass() {
      return Entity.class;
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference<EntityType<?>> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public T customClientSpawn(net.minecraftforge.network.PlayMessages.SpawnEntity packet, Level world) {
       if (customClientFactory == null) return this.create(world);
       return customClientFactory.apply(packet, world);
   }
   public Stream<TagKey<EntityType<?>>> getTags() {return this.builtInRegistryHolder().tags();}

   public static class Builder<T extends Entity> {
      private final EntityType.EntityFactory<T> factory;
      private final MobCategory category;
      private ImmutableSet<Block> immuneTo = ImmutableSet.of();
      private boolean serialize = true;
      private boolean summon = true;
      private boolean fireImmune;
      private boolean canSpawnFarFromPlayer;
      private int clientTrackingRange = 5;
      private int updateInterval = 3;
      private EntityDimensions dimensions = EntityDimensions.scalable(0.6F, 1.8F);

      private java.util.function.Predicate<EntityType<?>> velocityUpdateSupplier = EntityType::defaultVelocitySupplier;
      private java.util.function.ToIntFunction<EntityType<?>> trackingRangeSupplier = EntityType::defaultTrackingRangeSupplier;
      private java.util.function.ToIntFunction<EntityType<?>> updateIntervalSupplier = EntityType::defaultUpdateIntervalSupplier;
      private java.util.function.BiFunction<net.minecraftforge.network.PlayMessages.SpawnEntity, Level, T> customClientFactory;

      private Builder(EntityType.EntityFactory<T> p_20696_, MobCategory p_20697_) {
         this.factory = p_20696_;
         this.category = p_20697_;
         this.canSpawnFarFromPlayer = p_20697_ == MobCategory.CREATURE || p_20697_ == MobCategory.MISC;
      }

      public static <T extends Entity> EntityType.Builder<T> of(EntityType.EntityFactory<T> p_20705_, MobCategory p_20706_) {
         return new EntityType.Builder<>(p_20705_, p_20706_);
      }

      public static <T extends Entity> EntityType.Builder<T> createNothing(MobCategory p_20711_) {
         return new EntityType.Builder<>((p_20708_, p_20709_) -> {
            return (T)null;
         }, p_20711_);
      }

      public EntityType.Builder<T> sized(float p_20700_, float p_20701_) {
         this.dimensions = EntityDimensions.scalable(p_20700_, p_20701_);
         return this;
      }

      public EntityType.Builder<T> noSummon() {
         this.summon = false;
         return this;
      }

      public EntityType.Builder<T> noSave() {
         this.serialize = false;
         return this;
      }

      public EntityType.Builder<T> fireImmune() {
         this.fireImmune = true;
         return this;
      }

      public EntityType.Builder<T> immuneTo(Block... p_20715_) {
         this.immuneTo = ImmutableSet.copyOf(p_20715_);
         return this;
      }

      public EntityType.Builder<T> canSpawnFarFromPlayer() {
         this.canSpawnFarFromPlayer = true;
         return this;
      }

      public EntityType.Builder<T> clientTrackingRange(int p_20703_) {
         this.clientTrackingRange = p_20703_;
         return this;
      }

      public EntityType.Builder<T> updateInterval(int p_20718_) {
         this.updateInterval = p_20718_;
         return this;
      }

      public EntityType.Builder<T> setUpdateInterval(int interval) {
          this.updateIntervalSupplier = t->interval;
          return this;
      }

      public EntityType.Builder<T> setTrackingRange(int range) {
          this.trackingRangeSupplier = t->range;
          return this;
      }

      public EntityType.Builder<T> setShouldReceiveVelocityUpdates(boolean value) {
          this.velocityUpdateSupplier = t->value;
          return this;
      }

      /**
       * By default, entities are spawned clientside via {@link EntityType#create(Level)}}.
       * If you need finer control over the spawning process, use this to get read access to the spawn packet.
       */
      public EntityType.Builder<T> setCustomClientFactory(java.util.function.BiFunction<net.minecraftforge.network.PlayMessages.SpawnEntity, Level, T> customClientFactory) {
          this.customClientFactory = customClientFactory;
          return this;
      }

      public EntityType<T> build(String p_20713_) {
         if (this.serialize) {
            Util.fetchChoiceType(References.ENTITY_TREE, p_20713_);
         }

         return new EntityType<>(this.factory, this.category, this.serialize, this.summon, this.fireImmune, this.canSpawnFarFromPlayer, this.immuneTo, this.dimensions, this.clientTrackingRange, this.updateInterval, velocityUpdateSupplier, trackingRangeSupplier, updateIntervalSupplier, customClientFactory);
      }
   }

   public interface EntityFactory<T extends Entity> {
      T create(EntityType<T> p_20722_, Level p_20723_);
   }
}
