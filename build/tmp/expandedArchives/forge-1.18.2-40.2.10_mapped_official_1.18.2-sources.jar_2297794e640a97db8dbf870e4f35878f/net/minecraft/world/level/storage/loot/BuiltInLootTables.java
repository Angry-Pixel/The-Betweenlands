package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class BuiltInLootTables {
   private static final Set<ResourceLocation> LOCATIONS = Sets.newHashSet();
   private static final Set<ResourceLocation> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);
   public static final ResourceLocation EMPTY = new ResourceLocation("empty");
   public static final ResourceLocation SPAWN_BONUS_CHEST = register("chests/spawn_bonus_chest");
   public static final ResourceLocation END_CITY_TREASURE = register("chests/end_city_treasure");
   public static final ResourceLocation SIMPLE_DUNGEON = register("chests/simple_dungeon");
   public static final ResourceLocation VILLAGE_WEAPONSMITH = register("chests/village/village_weaponsmith");
   public static final ResourceLocation VILLAGE_TOOLSMITH = register("chests/village/village_toolsmith");
   public static final ResourceLocation VILLAGE_ARMORER = register("chests/village/village_armorer");
   public static final ResourceLocation VILLAGE_CARTOGRAPHER = register("chests/village/village_cartographer");
   public static final ResourceLocation VILLAGE_MASON = register("chests/village/village_mason");
   public static final ResourceLocation VILLAGE_SHEPHERD = register("chests/village/village_shepherd");
   public static final ResourceLocation VILLAGE_BUTCHER = register("chests/village/village_butcher");
   public static final ResourceLocation VILLAGE_FLETCHER = register("chests/village/village_fletcher");
   public static final ResourceLocation VILLAGE_FISHER = register("chests/village/village_fisher");
   public static final ResourceLocation VILLAGE_TANNERY = register("chests/village/village_tannery");
   public static final ResourceLocation VILLAGE_TEMPLE = register("chests/village/village_temple");
   public static final ResourceLocation VILLAGE_DESERT_HOUSE = register("chests/village/village_desert_house");
   public static final ResourceLocation VILLAGE_PLAINS_HOUSE = register("chests/village/village_plains_house");
   public static final ResourceLocation VILLAGE_TAIGA_HOUSE = register("chests/village/village_taiga_house");
   public static final ResourceLocation VILLAGE_SNOWY_HOUSE = register("chests/village/village_snowy_house");
   public static final ResourceLocation VILLAGE_SAVANNA_HOUSE = register("chests/village/village_savanna_house");
   public static final ResourceLocation ABANDONED_MINESHAFT = register("chests/abandoned_mineshaft");
   public static final ResourceLocation NETHER_BRIDGE = register("chests/nether_bridge");
   public static final ResourceLocation STRONGHOLD_LIBRARY = register("chests/stronghold_library");
   public static final ResourceLocation STRONGHOLD_CROSSING = register("chests/stronghold_crossing");
   public static final ResourceLocation STRONGHOLD_CORRIDOR = register("chests/stronghold_corridor");
   public static final ResourceLocation DESERT_PYRAMID = register("chests/desert_pyramid");
   public static final ResourceLocation JUNGLE_TEMPLE = register("chests/jungle_temple");
   public static final ResourceLocation JUNGLE_TEMPLE_DISPENSER = register("chests/jungle_temple_dispenser");
   public static final ResourceLocation IGLOO_CHEST = register("chests/igloo_chest");
   public static final ResourceLocation WOODLAND_MANSION = register("chests/woodland_mansion");
   public static final ResourceLocation UNDERWATER_RUIN_SMALL = register("chests/underwater_ruin_small");
   public static final ResourceLocation UNDERWATER_RUIN_BIG = register("chests/underwater_ruin_big");
   public static final ResourceLocation BURIED_TREASURE = register("chests/buried_treasure");
   public static final ResourceLocation SHIPWRECK_MAP = register("chests/shipwreck_map");
   public static final ResourceLocation SHIPWRECK_SUPPLY = register("chests/shipwreck_supply");
   public static final ResourceLocation SHIPWRECK_TREASURE = register("chests/shipwreck_treasure");
   public static final ResourceLocation PILLAGER_OUTPOST = register("chests/pillager_outpost");
   public static final ResourceLocation BASTION_TREASURE = register("chests/bastion_treasure");
   public static final ResourceLocation BASTION_OTHER = register("chests/bastion_other");
   public static final ResourceLocation BASTION_BRIDGE = register("chests/bastion_bridge");
   public static final ResourceLocation BASTION_HOGLIN_STABLE = register("chests/bastion_hoglin_stable");
   public static final ResourceLocation RUINED_PORTAL = register("chests/ruined_portal");
   public static final ResourceLocation SHEEP_WHITE = register("entities/sheep/white");
   public static final ResourceLocation SHEEP_ORANGE = register("entities/sheep/orange");
   public static final ResourceLocation SHEEP_MAGENTA = register("entities/sheep/magenta");
   public static final ResourceLocation SHEEP_LIGHT_BLUE = register("entities/sheep/light_blue");
   public static final ResourceLocation SHEEP_YELLOW = register("entities/sheep/yellow");
   public static final ResourceLocation SHEEP_LIME = register("entities/sheep/lime");
   public static final ResourceLocation SHEEP_PINK = register("entities/sheep/pink");
   public static final ResourceLocation SHEEP_GRAY = register("entities/sheep/gray");
   public static final ResourceLocation SHEEP_LIGHT_GRAY = register("entities/sheep/light_gray");
   public static final ResourceLocation SHEEP_CYAN = register("entities/sheep/cyan");
   public static final ResourceLocation SHEEP_PURPLE = register("entities/sheep/purple");
   public static final ResourceLocation SHEEP_BLUE = register("entities/sheep/blue");
   public static final ResourceLocation SHEEP_BROWN = register("entities/sheep/brown");
   public static final ResourceLocation SHEEP_GREEN = register("entities/sheep/green");
   public static final ResourceLocation SHEEP_RED = register("entities/sheep/red");
   public static final ResourceLocation SHEEP_BLACK = register("entities/sheep/black");
   public static final ResourceLocation FISHING = register("gameplay/fishing");
   public static final ResourceLocation FISHING_JUNK = register("gameplay/fishing/junk");
   public static final ResourceLocation FISHING_TREASURE = register("gameplay/fishing/treasure");
   public static final ResourceLocation FISHING_FISH = register("gameplay/fishing/fish");
   public static final ResourceLocation CAT_MORNING_GIFT = register("gameplay/cat_morning_gift");
   public static final ResourceLocation ARMORER_GIFT = register("gameplay/hero_of_the_village/armorer_gift");
   public static final ResourceLocation BUTCHER_GIFT = register("gameplay/hero_of_the_village/butcher_gift");
   public static final ResourceLocation CARTOGRAPHER_GIFT = register("gameplay/hero_of_the_village/cartographer_gift");
   public static final ResourceLocation CLERIC_GIFT = register("gameplay/hero_of_the_village/cleric_gift");
   public static final ResourceLocation FARMER_GIFT = register("gameplay/hero_of_the_village/farmer_gift");
   public static final ResourceLocation FISHERMAN_GIFT = register("gameplay/hero_of_the_village/fisherman_gift");
   public static final ResourceLocation FLETCHER_GIFT = register("gameplay/hero_of_the_village/fletcher_gift");
   public static final ResourceLocation LEATHERWORKER_GIFT = register("gameplay/hero_of_the_village/leatherworker_gift");
   public static final ResourceLocation LIBRARIAN_GIFT = register("gameplay/hero_of_the_village/librarian_gift");
   public static final ResourceLocation MASON_GIFT = register("gameplay/hero_of_the_village/mason_gift");
   public static final ResourceLocation SHEPHERD_GIFT = register("gameplay/hero_of_the_village/shepherd_gift");
   public static final ResourceLocation TOOLSMITH_GIFT = register("gameplay/hero_of_the_village/toolsmith_gift");
   public static final ResourceLocation WEAPONSMITH_GIFT = register("gameplay/hero_of_the_village/weaponsmith_gift");
   public static final ResourceLocation PIGLIN_BARTERING = register("gameplay/piglin_bartering");

   private static ResourceLocation register(String p_78768_) {
      return register(new ResourceLocation(p_78768_));
   }

   private static ResourceLocation register(ResourceLocation p_78770_) {
      if (LOCATIONS.add(p_78770_)) {
         return p_78770_;
      } else {
         throw new IllegalArgumentException(p_78770_ + " is already a registered built-in loot table");
      }
   }

   public static Set<ResourceLocation> all() {
      return IMMUTABLE_LOCATIONS;
   }
}