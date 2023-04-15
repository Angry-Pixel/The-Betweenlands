package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.sound.BLSoundEvent;

public class SoundRegistry {
    public static final List<SoundEvent> SOUNDS = new ArrayList<>();
    // Music
    public static final BLSoundEvent BL_MUSIC_MENU = reg("bl_menu");
    public static final BLSoundEvent BL_MUSIC_DIMENSION = reg("bl_dimension");
    public static final BLSoundEvent GREEBLING_MUSIC_1 = reg("greebling_music_1");
    public static final BLSoundEvent GREEBLING_MUSIC_2 = reg("greebling_music_2");

    // Records
    public static final BLSoundEvent _16612 = reg("16612");
    public static final BLSoundEvent ANCIENT = reg("ancient");
    public static final BLSoundEvent ASTATOS = reg("astatos");
    public static final BLSoundEvent BENEATH_A_GREEN_SKY = reg("beneath_a_green_sky");
    public static final BLSoundEvent BETWEEN_YOU_AND_ME = reg("between_you_and_me");
    public static final BLSoundEvent CHRISTMAS_ON_THE_MARSH = reg("christmas_on_the_marsh");
    public static final BLSoundEvent DJ_WIGHTS_MIXTAPE = reg("dj_wights_mixtape");
    public static final BLSoundEvent HAG_DANCE = reg("hag_dance");
    public static final BLSoundEvent LONELY_FIRE = reg("lonely_fire");
    public static final BLSoundEvent ONWARD = reg("onwards");
    public static final BLSoundEvent STUCK_IN_THE_MUD = reg("stuck_in_the_mud");
    public static final BLSoundEvent THE_EXPLORER = reg("the_explorer");
    public static final BLSoundEvent WANDERING_WISPS = reg("wandering_wisps");
    public static final BLSoundEvent WATERLOGGED = reg("waterlogged");
    public static final BLSoundEvent DEEP_WATER_THEME = reg("deep_water_theme");

    // Ambience
    public static final BLSoundEvent AMBIENT_BLOOD_SKY_ROAR = reg("ambient_blood_sky_roar");
    public static final BLSoundEvent AMBIENT_BLOOD_SKY = reg("ambient_blood_sky");
    public static final BLSoundEvent AMBIENT_CAVE = reg("ambient_cave");
    public static final BLSoundEvent AMBIENT_CAVE_SPOOK = reg("ambient_cave_spook");
    public static final BLSoundEvent AMBIENT_WIGHT_FORTRESS = reg("ambient_wight_fortress");
    public static final BLSoundEvent AMBIENT_SPOOPY = reg("ambient_spoopy");
    public static final BLSoundEvent AMBIENT_SWAMP = reg("ambient_swamp");
    public static final BLSoundEvent AMBIENT_SWAMP_DENSE_FOG = reg("ambient_swamp_dense_fog");
    public static final BLSoundEvent AMBIENT_DEEP_WATERS = reg("ambient_deep_waters");
    public static final BLSoundEvent AMBIENT_WATER = reg("ambient_water");
    public static final BLSoundEvent AMBIENT_FROSTY = reg("ambient_frosty");
    public static final BLSoundEvent AMBIENT_SNOWFALL = reg("ambient_snowfall");
    public static final BLSoundEvent AMBIENT_SLUDGE_WORM_DUNGEON = reg("ambient_sludge_worm_dungeon");
    public static final BLSoundEvent AMBIENT_FLOATING_ISLAND = reg("ambient_floating_island");
    public static final BLSoundEvent LIGHTNING = reg("lightning");
    public static final BLSoundEvent THUNDER = reg("thunder");
    public static final BLSoundEvent RAIN_STRONG = reg("rain_strong");
    public static final BLSoundEvent RAIN_MEDIUM = reg("rain_medium");
    public static final BLSoundEvent RAIN_WEAK = reg("rain_weak");
    public static final BLSoundEvent RAIN_DRIPPING = reg("rain_dripping");
    public static final BLSoundEvent RAIN_MOUNT = reg("rain_mount");

    // Miscellaneous
    public static final BLSoundEvent CRUMBLE = reg("crumble");
    public static final BLSoundEvent FIG = reg("fig");
    public static final BLSoundEvent SPIKE = reg("spike");
    public static final BLSoundEvent POSSESSED_SCREAM = reg("possessed_scream");
    public static final BLSoundEvent SORRY = reg("sorry");
    public static final BLSoundEvent PEBBLE_HISS = reg("pebble_hiss");
    public static final BLSoundEvent REJECTED = reg("rejected");
    public static final BLSoundEvent SHOCKWAVE_SWORD = reg("shockwave_sword");
    public static final BLSoundEvent SQUISH = reg("squish");
    public static final BLSoundEvent CRUNCH = reg("crunch");
    public static final BLSoundEvent IGNITE = reg("ignite");
    public static final BLSoundEvent DAMAGE_REDUCTION = reg("damage_reduction");
    public static final BLSoundEvent RIFT_OPEN = reg("rift_open");
    public static final BLSoundEvent RIFT_CREAK = reg("rift_creak");
    public static final BLSoundEvent ROOF_COLLAPSE = reg("roof_collapse");
    public static final BLSoundEvent BRAZIER_LIGHT = reg("brazier_light");
    public static final BLSoundEvent BEAM_ACTIVATE = reg("beam_activate");
    public static final BLSoundEvent PLUG_HIT = reg("plug_hit");
    public static final BLSoundEvent PLUG_LOCK = reg("plug_lock");
    public static final BLSoundEvent CHAIN = reg("chain");
    public static final BLSoundEvent CHAIN_LONG = reg("chain_long");
    public static final BLSoundEvent GEARS = reg("gears");
    public static final BLSoundEvent WALL_SLIDE = reg("wall_slide");
    public static final BLSoundEvent WALL_SLAM = reg("wall_slam");
    public static final BLSoundEvent POOP_JET = reg("poop_jet");
    public static final BLSoundEvent PIT_FALL = reg("pit_fall");
    public static final BLSoundEvent DRAETON_TURN = reg("draeton_turn");
    public static final BLSoundEvent DRAETON_DAMAGE = reg("draeton_damage");
    public static final BLSoundEvent DRAETON_BURNER = reg("draeton_burner");
    public static final BLSoundEvent DRAETON_PULLEY = reg("draeton_pulley");
    public static final BLSoundEvent DRAETON_ANCHOR = reg("draeton_anchor");
    public static final BLSoundEvent DRAETON_LEAK_START = reg("draeton_leak_start");
    public static final BLSoundEvent DRAETON_LEAK_LOOP = reg("draeton_leak_loop");
    public static final BLSoundEvent DRAETON_PLUG = reg("draeton_plug");
    public static final BLSoundEvent DRAETON_POP = reg("draeton_pop");
    public static final BLSoundEvent GREEBLING_FALL = reg("greebling_fall");
    public static final BLSoundEvent GREEBLING_HEY = reg("greebling_hey");
    public static final BLSoundEvent GREEBLING_GIGGLE = reg("greebling_giggle");
    public static final BLSoundEvent CHIROMAW_GREEBLING_RIDER_LIVING = reg("chirowmaw_greebling_rider_living");
    public static final BLSoundEvent SLINGSHOT_CHARGE = reg("slingshot_charge");
    public static final BLSoundEvent SLINGSHOT_HIT = reg("slingshot_hit");
    public static final BLSoundEvent SLINGSHOT_SHOOT = reg("slingshot_shoot");
    public static final BLSoundEvent ZAP = reg("zap");
    public static final BLSoundEvent CHIROBARB_ERUPTER = reg("chirobarb_erupter");
    public static final BLSoundEvent SIMULACRUM_BREAK = reg("simulacrum_break");
    public static final BLSoundEvent GREEBLING_HUM = reg("greebling_hum");
    public static final BLSoundEvent CORACLE_SINK = reg("coracle_sink");
    public static final BLSoundEvent SPEAR_THROW = reg("spear_throw");
    public static final BLSoundEvent SPEAR_LAND = reg("spear_land");
    public static final BLSoundEvent SPEAR_RETURN_1 = reg("spear_return_1");
    public static final BLSoundEvent SPEAR_RETURN_2 = reg("spear_return_2");
    public static final BLSoundEvent INFECTION_HEARTBEAT = reg("infection_heartbeat");
    public static final BLSoundEvent INFECTION_SPREAD = reg("infection_spread");

    // Hostiles
    public static final BLSoundEvent ANGLER_ATTACK = reg("angler_attack");
    public static final BLSoundEvent ASHSPRITE_HURT = reg("ashsprite_hurt");
    public static final BLSoundEvent ASHSPRITE_DEATH = reg("ashsprite_death");
    public static final BLSoundEvent ASHSPRITE_LIVING = reg("ashsprite_living");
    public static final BLSoundEvent SLUDGE_TURRET_HURT = reg("sludge_turret_hurt");
    public static final BLSoundEvent SLUDGE_TURRET_DEATH = reg("sludge_turret_death");
    public static final BLSoundEvent SLUDGE_TURRET_LIVING = reg("sludge_turret_living");
    public static final BLSoundEvent DARK_DRUID_DEATH = reg("dark_druid_death");
    public static final BLSoundEvent DARK_DRUID_HIT = reg("dark_druid_hit");
    public static final BLSoundEvent DARK_DRUID_LIVING = reg("dark_druid_living");
    public static final BLSoundEvent DRUID_CHANT = reg("druid_chant");
    public static final BLSoundEvent DRUID_TELEPORT = reg("druid_teleport");
    public static final BLSoundEvent DREADFUL_PEAT_MUMMY_BITE = reg("dreadful_peat_mummy_bite");
    public static final BLSoundEvent DREADFUL_PEAT_MUMMY_DEATH = reg("dreadful_peat_mummy_death");
    public static final BLSoundEvent DREADFUL_PEAT_MUMMY_EMERGE = reg("dreadful_peat_mummy_emerge");
    public static final BLSoundEvent DREADFUL_PEAT_MUMMY_HURT = reg("dreadful_peat_mummy_hurt");
    public static final BLSoundEvent DREADFUL_PEAT_MUMMY_LICK = reg("dreadful_peat_mummy_lick");
    public static final BLSoundEvent DREADFUL_PEAT_MUMMY_LIVING = reg("dreadful_peat_mummy_living");
    public static final BLSoundEvent DREADFUL_PEAT_MUMMY_LOOP = reg("dreadful_peat_mummy_loop");
    public static final BLSoundEvent DREADFUL_PEAT_MUMMY_RETCH = reg("dreadful_peat_mummy_retch");
    public static final BLSoundEvent DREADFUL_PEAT_MUMMY_SCREAM = reg("dreadful_peat_mummy_scream");
    public static final BLSoundEvent DREADFUL_PEAT_MUMMY_SWIPE = reg("dreadful_peat_mummy_swipe");
    public static final BLSoundEvent PEAT_MUMMY_CHARGE = reg("peat_mummy_charge");
    public static final BLSoundEvent PEAT_MUMMY_DEATH = reg("peat_mummy_death");
    public static final BLSoundEvent PEAT_MUMMY_EMERGE = reg("peat_mummy_emerge");
    public static final BLSoundEvent PEAT_MUMMY_HURT = reg("peat_mummy_hurt");
    public static final BLSoundEvent PEAT_MUMMY_LIVING = reg("peat_mummy_living");
    public static final BLSoundEvent FLYING_FIEND_DEATH = reg("flying_fiend_death");
    public static final BLSoundEvent FLYING_FIEND_HURT = reg("flying_fiend_hurt");
    public static final BLSoundEvent FLYING_FIEND_LIVING = reg("flying_fiend_living");
    public static final BLSoundEvent FORTRESS_BOSS_DEATH = reg("fortress_boss_death");
    public static final BLSoundEvent FORTRESS_BOSS_HURT = reg("fortress_boss_hurt");
    public static final BLSoundEvent FORTRESS_BOSS_LIVING = reg("fortress_boss_living");
    public static final BLSoundEvent FORTRESS_BOSS_LOOP = reg("fortress_boss_loop");
    public static final BLSoundEvent FORTRESS_BOSS_NOPE = reg("fortress_boss_nope");
    public static final BLSoundEvent FORTRESS_BOSS_SHIELD_DOWN = reg("fortress_boss_shield_down");
    public static final BLSoundEvent FORTRESS_BOSS_SUMMON_PROJECTILES = reg("fortress_boss_summon_projectiles");
    public static final BLSoundEvent FORTRESS_BOSS_TELEPORT = reg("fortress_boss_teleport");
    public static final BLSoundEvent FORTRESS_PUZZLE_CAGE_BREAK = reg("fortress_puzzle_cage_break");
    public static final BLSoundEvent FORTRESS_PUZZLE_ORB = reg("fortress_puzzle_orb");
    public static final BLSoundEvent FORTRESS_PUZZLE_SWORD = reg("fortress_puzzle_sword");
    public static final BLSoundEvent FORTRESS_TELEPORT = reg("fortress_teleport");
    public static final BLSoundEvent PYRAD_DEATH = reg("pyrad_death");
    public static final BLSoundEvent PYRAD_HURT = reg("pyrad_hurt");
    public static final BLSoundEvent PYRAD_LIVING = reg("pyrad_living");
    public static final BLSoundEvent PYRAD_SHOOT = reg("pyrad_shoot");
    public static final BLSoundEvent LURKER_HURT = reg("lurker_hurt");
    public static final BLSoundEvent LURKER_LIVING = reg("lurker_living");
    public static final BLSoundEvent LURKER_DEATH = reg("lurker_death");
    public static final BLSoundEvent SWAMP_HAG_DEATH = reg("swamp_hag_death");
    public static final BLSoundEvent SWAMP_HAG_HURT = reg("swamp_hag_hurt");
    public static final BLSoundEvent SWAMP_HAG_LIVING = reg("swamp_hag_living");
    public static final BLSoundEvent SWAMP_HAG_LIVING_1 = reg("swamp_hag_living_1");
    public static final BLSoundEvent SWAMP_HAG_LIVING_2 = reg("swamp_hag_living_2");
    public static final BLSoundEvent SWAMP_HAG_LIVING_3 = reg("swamp_hag_living_3");
    public static final BLSoundEvent SWAMP_HAG_LIVING_4 = reg("swamp_hag_living_4");
    public static final BLSoundEvent TAR_BEAST_DEATH = reg("tar_beast_death");
    public static final BLSoundEvent TAR_BEAST_HURT = reg("tar_beast_hurt");
    public static final BLSoundEvent TAR_BEAST_LIVING = reg("tar_beast_living");
    public static final BLSoundEvent TAR_BEAST_STEP = reg("tar_beast_step");
    public static final BLSoundEvent TAR_BEAST_SUCK = reg("tar_beast_suck");
    public static final BLSoundEvent TEMPLE_GUARDIAN_BESERKER_CHARGE = reg("temple_guardian_berserker_charge");
    public static final BLSoundEvent TEMPLE_GUARDIAN_BESERKER_IMPACT = reg("temple_guardian_berserker_impact");
    public static final BLSoundEvent TEMPLE_GUARDIAN_BESERKER_LIVING = reg("temple_guardian_berserker_living");
    public static final BLSoundEvent TEMPLE_GUARDIAN_DEATH = reg("temple_guardian_death");
    public static final BLSoundEvent TEMPLE_GUARDIAN_HURT = reg("temple_guardian_hurt");
    public static final BLSoundEvent TEMPLE_GUARDIAN_MELEE_LIVING = reg("temple_guardian_melee_living");
    public static final BLSoundEvent TEMPLE_GUARDIAN_STEP = reg("temple_guardian_step");
    public static final BLSoundEvent WIGHT_HURT = reg("wight_hurt");
    public static final BLSoundEvent WIGHT_MOAN = reg("wight_moan");
    public static final BLSoundEvent WIGHT_DEATH = reg("wight_death");
    public static final BLSoundEvent WIGHT_ATTACK = reg("wight_attack");
    public static final BLSoundEvent CRAB_SNIP = reg("crab_snip");
    public static final BLSoundEvent GAS_CLOUD_LIVING = reg("gas_cloud_living");
    public static final BLSoundEvent GAS_CLOUD_HURT = reg("gas_cloud_hurt");
    public static final BLSoundEvent GAS_CLOUD_DEATH = reg("gas_cloud_death");
    public static final BLSoundEvent BOULDER_SPRITE_LIVING = reg("boulder_sprite_living");
    public static final BLSoundEvent BOULDER_SPRITE_HURT = reg("boulder_sprite_hurt");
    public static final BLSoundEvent BOULDER_SPRITE_DEATH = reg("bounder_sprite_death");
    public static final BLSoundEvent BOULDER_SPRITE_ROLL = reg("bounder_sprite_roll");
    public static final BLSoundEvent SPIRIT_TREE_FACE_SMALL_SPIT = reg("spirit_tree_face_small_spit");
    public static final BLSoundEvent SPIRIT_TREE_FACE_SMALL_LIVING = reg("spirit_tree_face_small_living");
    public static final BLSoundEvent SPIRIT_TREE_FACE_LARGE_SPIT = reg("spirit_tree_face_large_spit");
    public static final BLSoundEvent SPIRIT_TREE_FACE_LARGE_DEATH = reg("spirit_tree_face_large_death");
    public static final BLSoundEvent SPIRIT_TREE_FACE_LARGE_LIVING = reg("spirit_tree_face_large_living");
    public static final BLSoundEvent SPIRIT_TREE_FACE_SMALL_EMERGE = reg("spirit_tree_face_small_emerge");
    public static final BLSoundEvent SPIRIT_TREE_FACE_LARGE_EMERGE = reg("spirit_tree_face_large_emerge");
    public static final BLSoundEvent SPIRIT_TREE_FACE_SUCK = reg("spirit_tree_face_suck");
    public static final BLSoundEvent SPIRIT_TREE_FACE_SPIT_ROOT_SPIKES = reg("spirit_tree_face_spit_root_spikes");
    public static final BLSoundEvent SPIRIT_TREE_SPIKE_TRAP = reg("spirit_tree_spike_trap");
    public static final BLSoundEvent SPIRIT_TREE_SPIKE_TRAP_EMERGE = reg("spirit_tree_spike_trap_emerge");
    public static final BLSoundEvent SPIRIT_TREE_SPIKES = reg("spirit_tree_spikes");
    public static final BLSoundEvent ROOT_SPIKE_PARTICLE_HIT = reg("root_spike_particle_hit");
    public static final BLSoundEvent SHAMBLER_DEATH = reg("shambler_death");
    public static final BLSoundEvent SHAMBLER_HURT = reg("shambler_hurt");
    public static final BLSoundEvent SHAMBLER_LIVING = reg("shambler_living");
    public static final BLSoundEvent SHAMBLER_LICK = reg("shambler_lick");
    public static final BLSoundEvent CRYPT_CRAWLER_DEATH = reg("crypt_crawler_death");
    public static final BLSoundEvent CRYPT_CRAWLER_HURT = reg("crypt_crawler_hurt");
    public static final BLSoundEvent CRYPT_CRAWLER_LIVING = reg("crypt_crawler_living");
    public static final BLSoundEvent CRYPT_CRAWLER_DIG = reg("crypt_crawler_dig");
    public static final BLSoundEvent WALL_LAMPREY_SUCK = reg("wall_lamprey_suck");
    public static final BLSoundEvent WALL_LAMPREY_ATTACK = reg("wall_lamprey_attack");
    public static final BLSoundEvent WALL_LIVING_ROOT_EMERGE = reg("wall_living_root_emerge");
    public static final BLSoundEvent WORM_EMERGE = reg("worm_emerge");
    public static final BLSoundEvent WORM_PLOP = reg("worm_plop");
    public static final BLSoundEvent WORM_HURT = reg("worm_hurt");
    public static final BLSoundEvent WORM_LIVING = reg("worm_living");
    public static final BLSoundEvent WORM_DEATH = reg("worm_death");
    public static final BLSoundEvent WORM_SPLAT = reg("worm_splat");
    public static final BLSoundEvent WORM_EGG_SAC_LIVING = reg("worm_egg_sac_living");
    public static final BLSoundEvent WORM_EGG_SAC_SQUISH = reg("worm_egg_sac_squish");
    public static final BLSoundEvent SPLODESHROOM_WINDDOWN = reg("splodeshroom_winddown");
    public static final BLSoundEvent SPLODESHROOM_WINDUP = reg("splodeshroom_windup");
    public static final BLSoundEvent SPLODESHROOM_POP = reg("splodeshroom_pop");
    public static final BLSoundEvent PIT_OF_DECAY_LOOP = reg("pit_of_decay_loop");
    public static final BLSoundEvent EMBERLING_FLAMES = reg("emberling_flames");
    public static final BLSoundEvent EMBERLING_JUMP = reg("emberling_jump");
    public static final BLSoundEvent EMBERLING_HURT = reg("emberling_hurt");
    public static final BLSoundEvent EMBERLING_LIVING = reg("emberling_living");
    public static final BLSoundEvent EMBERLING_DEATH = reg("emberling_death");
    public static final BLSoundEvent BARRISHEE_HURT = reg("barrishee_hurt");
    public static final BLSoundEvent BARRISHEE_LIVING = reg("barrishee_living");
    public static final BLSoundEvent BARRISHEE_DEATH = reg("barrishee_death");
    public static final BLSoundEvent BARRISHEE_STEP = reg("barrishee_step");
    public static final BLSoundEvent BARRISHEE_SCREAM = reg("barrishee_scream");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_BARB_FIRE = reg("chiromaw_matriarch_barb_fire");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_BARB_HIT = reg("chiromaw_matriarch_barb_hit");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_LIVING = reg("chiromaw_matriarch_living");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_HURT = reg("chiromaw_matriarch_hurt");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_DEATH = reg("chiromaw_matriarch_death");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_POOP = reg("chiromaw_matriarch_poop");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_SPLAT = reg("chiromaw_matriarch_splat");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_LAND = reg("chiromaw_matriarch_land");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_FLAP = reg("chiromaw_matriarch_flap");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_RELEASE = reg("chiromaw_matriarch_release");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_GRAB = reg("chiromaw_matriarch_grab");
    public static final BLSoundEvent CHIROMAW_MATRIARCH_ROAR = reg("chiromaw_matriarch_roar");
    public static final BLSoundEvent CHIROMAW_HATCHLING_LIVING = reg("chiromaw_hatchling_living");
    public static final BLSoundEvent CHIROMAW_HATCHLING_HUNGRY_SHORT = reg("chiromaw_hatchling_hungry_short");
    public static final BLSoundEvent CHIROMAW_HATCHLING_HUNGRY_LONG = reg("chiromaw_hatchling_hungry_long");
    public static final BLSoundEvent CHIROMAW_HATCHLING_EAT = reg("chiromaw_hatchling_eat");
    public static final BLSoundEvent CHIROMAW_HATCHLING_NO = reg("chiromaw_hatchling_no");
    public static final BLSoundEvent CHIROMAW_HATCH = reg("chiromaw_hatch");
    public static final BLSoundEvent CHIROMAW_HATCHLING_TRANSFORM = reg("chiromaw_hatchling_transform");
    public static final BLSoundEvent CHIROMAW_HATCHLING_INSIDE_EGG = reg("chiromaw_hatchling_inside_egg");
    public static final BLSoundEvent STALKER_SCREAM = reg("stalker_scream");
    public static final BLSoundEvent STALKER_SCREECH = reg("stalker_screech");
    public static final BLSoundEvent STALKER_STEP = reg("stalker_step");
    public static final BLSoundEvent STALKER_LIVING = reg("stalker_living");
    public static final BLSoundEvent STALKER_HURT = reg("stalker_hurt");
    public static final BLSoundEvent STALKER_DEATH = reg("stalker_death");
    public static final BLSoundEvent BARRISHEE_THEME = reg("barrishee_theme");
    public static final BLSoundEvent SWARM_ATTACK = reg("swarm_attack");
    public static final BLSoundEvent SWARM_IDLE = reg("swarm_idle");
    public static final BLSoundEvent ROCK_SNOT_ATTACH = reg("rock_snot_attach");
    public static final BLSoundEvent ROCK_SNOT_ATTACK = reg("rock_snot_attack");
    public static final BLSoundEvent ROCK_SNOT_DIGEST= reg("rock_snot_digest");
    public static final BLSoundEvent ROCK_SNOT_SPIT = reg("rock_snot_spit");
    public static final BLSoundEvent ROCK_SNOT_EAT = reg("rock_snot_eat");
    public static final BLSoundEvent BUBBLER_SPIT = reg("bubbler_spit");
    public static final BLSoundEvent BUBBLER_LAND = reg("bubbler_land");
    public static final BLSoundEvent BUBBLER_POP = reg("bubbler_pop");
    public static final BLSoundEvent SLUDGE_ATTACK = reg("sludge_attack");
    public static final BLSoundEvent SLUDGE_JUMP = reg("sludge_jump");
    public static final BLSoundEvent LIVING_ROOT_HURT = reg("living_root_hurt");
    public static final BLSoundEvent LIVING_ROOT_DEATH = reg("living_root_death");

    // Generic Mob sounds
    public static final BLSoundEvent FISH_HURT = reg("fish_hurt");
    public static final BLSoundEvent FISH_DEATH = reg("fish_death");
    public static final BLSoundEvent FISH_FLOP = reg("fish_flop");

    // Passives
    public static final BLSoundEvent DRAGONFLY = reg("dragonfly");
    public static final BLSoundEvent FROG_DEATH = reg("frog_death");
    public static final BLSoundEvent FROG_HURT = reg("frog_hurt");
    public static final BLSoundEvent FROG_LIVING = reg("frog_living");
    public static final BLSoundEvent GECKO_DEATH = reg("gecko_death");
    public static final BLSoundEvent GECKO_HIDE = reg("gecko_hide");
    public static final BLSoundEvent GECKO_HURT = reg("gecko_hurt");
    public static final BLSoundEvent GECKO_LIVING = reg("gecko_living");
    public static final BLSoundEvent GIANT_TOAD_DEATH = reg("giant_toad_death");
    public static final BLSoundEvent GIANT_TOAD_HURT = reg("giant_toad_hurt");
    public static final BLSoundEvent GIANT_TOAD_LIVING = reg("giant_toad_living");
    public static final BLSoundEvent TARMINION_DEATH = reg("tarminion_death");
    public static final BLSoundEvent TARMINION_HURT = reg("tarminion_hurt");
    public static final BLSoundEvent SNAIL_DEATH = reg("snail_death");
    public static final BLSoundEvent SNAIL_HURT = reg("snail_hurt");
    public static final BLSoundEvent SNAIL_LIVING = reg("snail_living");
    public static final BLSoundEvent LEECH_DEATH = reg("leech_death");
    public static final BLSoundEvent LEECH_HURT = reg("leech_hurt");
    public static final BLSoundEvent LEECH_LIVING = reg("leech_living");
    public static final BLSoundEvent OLM_HURT = reg("olm_hurt");
    public static final BLSoundEvent OLM_DEATH = reg("olm_death");
    public static final BLSoundEvent SLUDGE_MENACE_DEATH = reg("sludge_menace_death");
    public static final BLSoundEvent SLUDGE_MENACE_HURT = reg("sludge_menace_hurt");
    public static final BLSoundEvent SLUDGE_MENACE_LIVING = reg("sludge_menace_living");
    public static final BLSoundEvent SLUDGE_MENACE_ATTACK = reg("sludge_menace_attack");
    public static final BLSoundEvent SLUDGE_MENACE_SPIT = reg("sludge_menace_spit");
    public static final BLSoundEvent SLUDGE_MENACE_SPAWN = reg("sludge_menace_spawn");
    public static final BLSoundEvent SPORELING_DEATH = reg("sporeling_death");
    public static final BLSoundEvent SPORELING_HURT = reg("sporeling_hurt");
    public static final BLSoundEvent SPORELING_LIVING = reg("sporeling_living");
    public static final BLSoundEvent TERMITE_LIVING = reg("termite_living");
    public static final BLSoundEvent ROOT_SPRITE_DEATH = reg("root_sprite_death");
    public static final BLSoundEvent ROOT_SPRITE_LIVING = reg("root_sprite_living");
    public static final BLSoundEvent ROOT_SPRITE_HURT = reg("root_sprite_hurt");
    public static final BLSoundEvent GREEBLING_VANISH = reg("greebling_vanish");
    public static final BLSoundEvent JELLYFISH_HURT = reg("jellyfish_hurt");
    public static final BLSoundEvent JELLYFISH_DEATH = reg("jellyfish_death");
    public static final BLSoundEvent JELLYFISH_SWIM = reg("jellyfish_swim");
    public static final BLSoundEvent JELLYFISH_ZAP = reg("jellyfish_zap");
    public static final BLSoundEvent URCHIN_SHOOT = reg("urchin_shoot");
    public static final BLSoundEvent ANADIA_TREASURE_COLLECTED = reg("anadia_treasure_collected");
    public static final BLSoundEvent ANADIA_LOST = reg("anadia_lost");
    public static final BLSoundEvent ANADIA_WON = reg("anadia_won");
    public static final BLSoundEvent FISHING_CRAB = reg("fishing_crab");

    // Items
    public static final BLSoundEvent ROWBOAT_ROW_STARBOARD = reg("rowboat_row_starboard");
    public static final BLSoundEvent ROWBOAT_ROW_PORT = reg("rowboat_row_port");
    public static final BLSoundEvent ROWBOAT_ROW_START_STARBOARD = reg("rowboat_row_start_starboard");
    public static final BLSoundEvent ROWBOAT_ROW_START_PORT = reg("rowboat_row_start_port");
    public static final BLSoundEvent VOODOO_DOLL = reg("voodoo_doll");
    public static final BLSoundEvent GEM_SINGER = reg("gem_singer");
    public static final BLSoundEvent GEM_SINGER_ECHO = reg("gem_singer_echo");
    public static final BLSoundEvent ROPE_THROW = reg("rope_throw");
    public static final BLSoundEvent ROPE_PULL = reg("rope_pull");
    public static final BLSoundEvent ROPE_SWING = reg("rope_swing");
    public static final BLSoundEvent ROPE_GRAB = reg("rope_grab");
    public static final BLSoundEvent RING_OF_DISPERSION_TELEPORT = reg("ring_of_dispersion_teleport");
    public static final BLSoundEvent LONG_SWING = reg("long_swing");
    public static final BLSoundEvent LONG_SLICE = reg("long_slice");
    public static final BLSoundEvent BL_FISHING_CAST = reg("bl_fishing_cast");
    public static final BLSoundEvent BL_FISHING_REEL = reg("bl_fishing_reel");
    public static final BLSoundEvent BL_FISHING_ROD_CREAK = reg("bl_fishing_rod_creak");
    public static final BLSoundEvent MIST_STAFF_CAST = reg("mist_staff_cast");
    public static final BLSoundEvent MIST_STAFF_VANISH = reg("mist_staff_vanish");
    public static final BLSoundEvent SILKY_PEBBLE_THROW = reg("silky_pebble_throw");

    // Blocks
    public static final BLSoundEvent GRIND = reg("grind");
    public static final BLSoundEvent INFUSER_FINISHED = reg("infuser_finished");
    public static final BLSoundEvent PURIFIER = reg("purifier");
    public static final BLSoundEvent ANIMATOR = reg("animator");
    public static final BLSoundEvent PORTAL = reg("portal");
    public static final BLSoundEvent PORTAL_ACTIVATE = reg("portal_activate");
    public static final BLSoundEvent PORTAL_TRAVEL = reg("portal_travel");
    public static final BLSoundEvent PORTAL_TRIGGER = reg("portal_trigger");
    public static final BLSoundEvent PUFF_SHROOM = reg("puff_shroom");
    public static final BLSoundEvent MUD_DOOR_1 = reg("mud_door_1");
    public static final BLSoundEvent MUD_DOOR_2 = reg("mud_door_2");
    public static final BLSoundEvent MUD_DOOR_LOCK = reg("mud_door_lock");
    public static final BLSoundEvent MUD_DOOR_TRAP = reg("mud_door_trap");
    public static final BLSoundEvent BEAM_SWITCH = reg("beam_switch");
    public static final BLSoundEvent RESURRECTION = reg("resurrection");
    public static final BLSoundEvent WORM_THROW = reg("worm_throw");
    public static final BLSoundEvent CHIMES_WIND = reg("chimes_wind");
    public static final BLSoundEvent CHIMES_AURORAS = reg("chimes_auroras");
    public static final BLSoundEvent CHIMES_BLOOD_SKY = reg("chimes_blood_sky");
    public static final BLSoundEvent CHIMES_DENSE_FOG = reg("chimes_dense_fog");
    public static final BLSoundEvent CHIMES_HEAVY_RAIN = reg("chimes_heavy_rain");
    public static final BLSoundEvent CHIMES_RIFT = reg("chimes_rift");
    public static final BLSoundEvent CHIMES_THUNDERSTORM = reg("chimes_thunderstorm");
    public static final BLSoundEvent CHIMES_SNOWFALL = reg("chimes_snowfall");
    public static final BLSoundEvent FISHING_TACKLE_BOX_OPEN = reg("fishing_tackle_box_open");
    public static final BLSoundEvent FISHING_TACKLE_BOX_CLOSE = reg("fishing_tackle_box_close");
    public static final BLSoundEvent FISH_CHOP = reg("fish_chop");
    public static final BLSoundEvent LYESTONE_FIZZ = reg("lyestone_fizz");
    public static final BLSoundEvent GRUB_HUB_MIST = reg("grub_hub_mist");
    public static final BLSoundEvent GRUB_HUB_SUCK = reg("grub_hub_suck");

    private SoundRegistry() {
    }

    private static BLSoundEvent reg(String name) {
        BLSoundEvent event = new BLSoundEvent(name);

        SOUNDS.add(event);
        return event;
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> register) {
        try {
            for (Field field : SoundRegistry.class.getDeclaredFields()) {
                Object obj = field.get(null);
                if (obj instanceof BLSoundEvent) {
                    register.getRegistry().register((BLSoundEvent)obj);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void preInit() {
        assert !SOUNDS.isEmpty();
    }
}
