package net.minecraft.client.model.geom;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelLayers {
   private static final String DEFAULT_LAYER = "main";
   private static final Set<ModelLayerLocation> ALL_MODELS = Sets.newHashSet();
   public static final ModelLayerLocation ARMOR_STAND = register("armor_stand");
   public static final ModelLayerLocation ARMOR_STAND_INNER_ARMOR = registerInnerArmor("armor_stand");
   public static final ModelLayerLocation ARMOR_STAND_OUTER_ARMOR = registerOuterArmor("armor_stand");
   public static final ModelLayerLocation AXOLOTL = register("axolotl");
   public static final ModelLayerLocation BANNER = register("banner");
   public static final ModelLayerLocation BAT = register("bat");
   public static final ModelLayerLocation BED_FOOT = register("bed_foot");
   public static final ModelLayerLocation BED_HEAD = register("bed_head");
   public static final ModelLayerLocation BEE = register("bee");
   public static final ModelLayerLocation BELL = register("bell");
   public static final ModelLayerLocation BLAZE = register("blaze");
   public static final ModelLayerLocation BOOK = register("book");
   public static final ModelLayerLocation CAT = register("cat");
   public static final ModelLayerLocation CAT_COLLAR = register("cat", "collar");
   public static final ModelLayerLocation CAVE_SPIDER = register("cave_spider");
   public static final ModelLayerLocation CHEST = register("chest");
   public static final ModelLayerLocation CHEST_MINECART = register("chest_minecart");
   public static final ModelLayerLocation CHICKEN = register("chicken");
   public static final ModelLayerLocation COD = register("cod");
   public static final ModelLayerLocation COMMAND_BLOCK_MINECART = register("command_block_minecart");
   public static final ModelLayerLocation CONDUIT_CAGE = register("conduit", "cage");
   public static final ModelLayerLocation CONDUIT_EYE = register("conduit", "eye");
   public static final ModelLayerLocation CONDUIT_SHELL = register("conduit", "shell");
   public static final ModelLayerLocation CONDUIT_WIND = register("conduit", "wind");
   public static final ModelLayerLocation COW = register("cow");
   public static final ModelLayerLocation CREEPER = register("creeper");
   public static final ModelLayerLocation CREEPER_ARMOR = register("creeper", "armor");
   public static final ModelLayerLocation CREEPER_HEAD = register("creeper_head");
   public static final ModelLayerLocation DOLPHIN = register("dolphin");
   public static final ModelLayerLocation DONKEY = register("donkey");
   public static final ModelLayerLocation DOUBLE_CHEST_LEFT = register("double_chest_left");
   public static final ModelLayerLocation DOUBLE_CHEST_RIGHT = register("double_chest_right");
   public static final ModelLayerLocation DRAGON_SKULL = register("dragon_skull");
   public static final ModelLayerLocation DROWNED = register("drowned");
   public static final ModelLayerLocation DROWNED_INNER_ARMOR = registerInnerArmor("drowned");
   public static final ModelLayerLocation DROWNED_OUTER_ARMOR = registerOuterArmor("drowned");
   public static final ModelLayerLocation DROWNED_OUTER_LAYER = register("drowned", "outer");
   public static final ModelLayerLocation ELDER_GUARDIAN = register("elder_guardian");
   public static final ModelLayerLocation ELYTRA = register("elytra");
   public static final ModelLayerLocation ENDERMAN = register("enderman");
   public static final ModelLayerLocation ENDERMITE = register("endermite");
   public static final ModelLayerLocation ENDER_DRAGON = register("ender_dragon");
   public static final ModelLayerLocation END_CRYSTAL = register("end_crystal");
   public static final ModelLayerLocation EVOKER = register("evoker");
   public static final ModelLayerLocation EVOKER_FANGS = register("evoker_fangs");
   public static final ModelLayerLocation FOX = register("fox");
   public static final ModelLayerLocation FURNACE_MINECART = register("furnace_minecart");
   public static final ModelLayerLocation GHAST = register("ghast");
   public static final ModelLayerLocation GIANT = register("giant");
   public static final ModelLayerLocation GIANT_INNER_ARMOR = registerInnerArmor("giant");
   public static final ModelLayerLocation GIANT_OUTER_ARMOR = registerOuterArmor("giant");
   public static final ModelLayerLocation GLOW_SQUID = register("glow_squid");
   public static final ModelLayerLocation GOAT = register("goat");
   public static final ModelLayerLocation GUARDIAN = register("guardian");
   public static final ModelLayerLocation HOGLIN = register("hoglin");
   public static final ModelLayerLocation HOPPER_MINECART = register("hopper_minecart");
   public static final ModelLayerLocation HORSE = register("horse");
   public static final ModelLayerLocation HORSE_ARMOR = register("horse_armor");
   public static final ModelLayerLocation HUSK = register("husk");
   public static final ModelLayerLocation HUSK_INNER_ARMOR = registerInnerArmor("husk");
   public static final ModelLayerLocation HUSK_OUTER_ARMOR = registerOuterArmor("husk");
   public static final ModelLayerLocation ILLUSIONER = register("illusioner");
   public static final ModelLayerLocation IRON_GOLEM = register("iron_golem");
   public static final ModelLayerLocation LEASH_KNOT = register("leash_knot");
   public static final ModelLayerLocation LLAMA = register("llama");
   public static final ModelLayerLocation LLAMA_DECOR = register("llama", "decor");
   public static final ModelLayerLocation LLAMA_SPIT = register("llama_spit");
   public static final ModelLayerLocation MAGMA_CUBE = register("magma_cube");
   public static final ModelLayerLocation MINECART = register("minecart");
   public static final ModelLayerLocation MOOSHROOM = register("mooshroom");
   public static final ModelLayerLocation MULE = register("mule");
   public static final ModelLayerLocation OCELOT = register("ocelot");
   public static final ModelLayerLocation PANDA = register("panda");
   public static final ModelLayerLocation PARROT = register("parrot");
   public static final ModelLayerLocation PHANTOM = register("phantom");
   public static final ModelLayerLocation PIG = register("pig");
   public static final ModelLayerLocation PIGLIN = register("piglin");
   public static final ModelLayerLocation PIGLIN_BRUTE = register("piglin_brute");
   public static final ModelLayerLocation PIGLIN_BRUTE_INNER_ARMOR = registerInnerArmor("piglin_brute");
   public static final ModelLayerLocation PIGLIN_BRUTE_OUTER_ARMOR = registerOuterArmor("piglin_brute");
   public static final ModelLayerLocation PIGLIN_INNER_ARMOR = registerInnerArmor("piglin");
   public static final ModelLayerLocation PIGLIN_OUTER_ARMOR = registerOuterArmor("piglin");
   public static final ModelLayerLocation PIG_SADDLE = register("pig", "saddle");
   public static final ModelLayerLocation PILLAGER = register("pillager");
   public static final ModelLayerLocation PLAYER = register("player");
   public static final ModelLayerLocation PLAYER_HEAD = register("player_head");
   public static final ModelLayerLocation PLAYER_INNER_ARMOR = registerInnerArmor("player");
   public static final ModelLayerLocation PLAYER_OUTER_ARMOR = registerOuterArmor("player");
   public static final ModelLayerLocation PLAYER_SLIM = register("player_slim");
   public static final ModelLayerLocation PLAYER_SLIM_INNER_ARMOR = registerInnerArmor("player_slim");
   public static final ModelLayerLocation PLAYER_SLIM_OUTER_ARMOR = registerOuterArmor("player_slim");
   public static final ModelLayerLocation PLAYER_SPIN_ATTACK = register("spin_attack");
   public static final ModelLayerLocation POLAR_BEAR = register("polar_bear");
   public static final ModelLayerLocation PUFFERFISH_BIG = register("pufferfish_big");
   public static final ModelLayerLocation PUFFERFISH_MEDIUM = register("pufferfish_medium");
   public static final ModelLayerLocation PUFFERFISH_SMALL = register("pufferfish_small");
   public static final ModelLayerLocation RABBIT = register("rabbit");
   public static final ModelLayerLocation RAVAGER = register("ravager");
   public static final ModelLayerLocation SALMON = register("salmon");
   public static final ModelLayerLocation SHEEP = register("sheep");
   public static final ModelLayerLocation SHEEP_FUR = register("sheep", "fur");
   public static final ModelLayerLocation SHIELD = register("shield");
   public static final ModelLayerLocation SHULKER = register("shulker");
   public static final ModelLayerLocation SHULKER_BULLET = register("shulker_bullet");
   public static final ModelLayerLocation SILVERFISH = register("silverfish");
   public static final ModelLayerLocation SKELETON = register("skeleton");
   public static final ModelLayerLocation SKELETON_HORSE = register("skeleton_horse");
   public static final ModelLayerLocation SKELETON_INNER_ARMOR = registerInnerArmor("skeleton");
   public static final ModelLayerLocation SKELETON_OUTER_ARMOR = registerOuterArmor("skeleton");
   public static final ModelLayerLocation SKELETON_SKULL = register("skeleton_skull");
   public static final ModelLayerLocation SLIME = register("slime");
   public static final ModelLayerLocation SLIME_OUTER = register("slime", "outer");
   public static final ModelLayerLocation SNOW_GOLEM = register("snow_golem");
   public static final ModelLayerLocation SPAWNER_MINECART = register("spawner_minecart");
   public static final ModelLayerLocation SPIDER = register("spider");
   public static final ModelLayerLocation SQUID = register("squid");
   public static final ModelLayerLocation STRAY = register("stray");
   public static final ModelLayerLocation STRAY_INNER_ARMOR = registerInnerArmor("stray");
   public static final ModelLayerLocation STRAY_OUTER_ARMOR = registerOuterArmor("stray");
   public static final ModelLayerLocation STRAY_OUTER_LAYER = register("stray", "outer");
   public static final ModelLayerLocation STRIDER = register("strider");
   public static final ModelLayerLocation STRIDER_SADDLE = register("strider", "saddle");
   public static final ModelLayerLocation TNT_MINECART = register("tnt_minecart");
   public static final ModelLayerLocation TRADER_LLAMA = register("trader_llama");
   public static final ModelLayerLocation TRIDENT = register("trident");
   public static final ModelLayerLocation TROPICAL_FISH_LARGE = register("tropical_fish_large");
   public static final ModelLayerLocation TROPICAL_FISH_LARGE_PATTERN = register("tropical_fish_large", "pattern");
   public static final ModelLayerLocation TROPICAL_FISH_SMALL = register("tropical_fish_small");
   public static final ModelLayerLocation TROPICAL_FISH_SMALL_PATTERN = register("tropical_fish_small", "pattern");
   public static final ModelLayerLocation TURTLE = register("turtle");
   public static final ModelLayerLocation VEX = register("vex");
   public static final ModelLayerLocation VILLAGER = register("villager");
   public static final ModelLayerLocation VINDICATOR = register("vindicator");
   public static final ModelLayerLocation WANDERING_TRADER = register("wandering_trader");
   public static final ModelLayerLocation WITCH = register("witch");
   public static final ModelLayerLocation WITHER = register("wither");
   public static final ModelLayerLocation WITHER_ARMOR = register("wither", "armor");
   public static final ModelLayerLocation WITHER_SKELETON = register("wither_skeleton");
   public static final ModelLayerLocation WITHER_SKELETON_INNER_ARMOR = registerInnerArmor("wither_skeleton");
   public static final ModelLayerLocation WITHER_SKELETON_OUTER_ARMOR = registerOuterArmor("wither_skeleton");
   public static final ModelLayerLocation WITHER_SKELETON_SKULL = register("wither_skeleton_skull");
   public static final ModelLayerLocation WITHER_SKULL = register("wither_skull");
   public static final ModelLayerLocation WOLF = register("wolf");
   public static final ModelLayerLocation ZOGLIN = register("zoglin");
   public static final ModelLayerLocation ZOMBIE = register("zombie");
   public static final ModelLayerLocation ZOMBIE_HEAD = register("zombie_head");
   public static final ModelLayerLocation ZOMBIE_HORSE = register("zombie_horse");
   public static final ModelLayerLocation ZOMBIE_INNER_ARMOR = registerInnerArmor("zombie");
   public static final ModelLayerLocation ZOMBIE_OUTER_ARMOR = registerOuterArmor("zombie");
   public static final ModelLayerLocation ZOMBIE_VILLAGER = register("zombie_villager");
   public static final ModelLayerLocation ZOMBIE_VILLAGER_INNER_ARMOR = registerInnerArmor("zombie_villager");
   public static final ModelLayerLocation ZOMBIE_VILLAGER_OUTER_ARMOR = registerOuterArmor("zombie_villager");
   public static final ModelLayerLocation ZOMBIFIED_PIGLIN = register("zombified_piglin");
   public static final ModelLayerLocation ZOMBIFIED_PIGLIN_INNER_ARMOR = registerInnerArmor("zombified_piglin");
   public static final ModelLayerLocation ZOMBIFIED_PIGLIN_OUTER_ARMOR = registerOuterArmor("zombified_piglin");

   private static ModelLayerLocation register(String p_171294_) {
      return register(p_171294_, "main");
   }

   private static ModelLayerLocation register(String p_171296_, String p_171297_) {
      ModelLayerLocation modellayerlocation = createLocation(p_171296_, p_171297_);
      if (!ALL_MODELS.add(modellayerlocation)) {
         throw new IllegalStateException("Duplicate registration for " + modellayerlocation);
      } else {
         return modellayerlocation;
      }
   }

   private static ModelLayerLocation createLocation(String p_171301_, String p_171302_) {
      return new ModelLayerLocation(new ResourceLocation("minecraft", p_171301_), p_171302_);
   }

   private static ModelLayerLocation registerInnerArmor(String p_171299_) {
      return register(p_171299_, "inner_armor");
   }

   private static ModelLayerLocation registerOuterArmor(String p_171304_) {
      return register(p_171304_, "outer_armor");
   }

   public static ModelLayerLocation createBoatModelName(Boat.Type p_171290_) {
      return createLocation("boat/" + p_171290_.getName(), "main");
   }

   public static ModelLayerLocation createSignModelName(WoodType p_171292_) {
      ResourceLocation location = new ResourceLocation(p_171292_.name());
      return new ModelLayerLocation(new ResourceLocation(location.getNamespace(), "sign/" + location.getPath()), "main");
   }

   public static Stream<ModelLayerLocation> getKnownLocations() {
      return ALL_MODELS.stream();
   }
}
