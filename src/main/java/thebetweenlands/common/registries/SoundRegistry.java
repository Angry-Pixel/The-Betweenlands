package thebetweenlands.common.registries;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.common.sound.BLSoundEvent;

public class SoundRegistry {
	private SoundRegistry() { }
	
    public static final List<SoundEvent> SOUNDS = new ArrayList<>();

    // Music
    public static final BLSoundEvent BL_MUSIC_MENU = reg("bl_menu");
    public static final BLSoundEvent BL_MUSIC_DIMENSION = reg("bl_dimension");

    // Records
    public static final BLSoundEvent _16612 = reg("16612");
    public static final BLSoundEvent ACIENT = reg("ancient");
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

    // Ambience
    public static final BLSoundEvent AMBIENT_BLOOD_SKY_ROAR = reg("ambient_blood_sky_roar");
    public static final BLSoundEvent AMBIENT_BLOOD_SKY = reg("ambient_blood_sky");
    public static final BLSoundEvent AMBIENT_CAVE = reg("ambient_cave");
    public static final BLSoundEvent AMBIENT_WIGHT_FORTRESS = reg("ambient_wight_fortress");
    public static final BLSoundEvent AMBIENT_SPOOPY = reg("ambient_spoopy");
    public static final BLSoundEvent AMBIENT_SWAMP = reg("ambient_swamp");
    public static final BLSoundEvent AMBIENT_WATER = reg("ambient_water");

    // Miscellaneous
    public static final BLSoundEvent CRUMBLE = reg("crumble");
    public static final BLSoundEvent FIG = reg("fig");
    public static final BLSoundEvent SPIKE = reg("spike");
    public static final BLSoundEvent POSSESSED_SCREAM = reg("possessed_scream");
    public static final BLSoundEvent SORRY = reg("sorry");
    public static final BLSoundEvent REJECTED = reg("rejected");
    public static final BLSoundEvent SHOCKWAVE_SWORD = reg("shockwave_sword");
    public static final BLSoundEvent SQUISH = reg("squish");

    // Hostiles
    public static final BLSoundEvent ANGLER_ATTACK = reg("angler_attack");
    public static final BLSoundEvent ANGLER_DEATH = reg("angler_death");

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

    public static final BLSoundEvent SNAIL_DEATH = reg("snail_death");
    public static final BLSoundEvent SNAIL_HURT = reg("snail_hurt");
    public static final BLSoundEvent SNAIL_LIVING = reg("snail_living");

    public static final BLSoundEvent SPORELING_DEATH = reg("sporeling_death");
    public static final BLSoundEvent SPORELING_HURT = reg("sporeling_hurt");
    public static final BLSoundEvent SPORELING_LIVING = reg("sporeling_living");
    
    public static final BLSoundEvent TERMITE_LIVING = reg("termite_living");

    // Items
    public static final BLSoundEvent ROWBOAT_ROW = reg("rowboat_row");
    public static final BLSoundEvent ROWBOAT_ROW_START = reg("rowboat_row_start");
    
    public static final BLSoundEvent VOODOO_DOLL = reg("voodoo_doll");

    // Blocks
    public static final BLSoundEvent GRIND = reg("grind");

    public static final BLSoundEvent INFUSER_FINISHED = reg("infuser_finished");

    public static final BLSoundEvent PURIFIER = reg("purifier");

    public static final BLSoundEvent PORTAL = reg("portal");
    public static final BLSoundEvent PORTAL_ACTIVATE = reg("portal_activate");
    public static final BLSoundEvent PORTAL_TRAVEL = reg("portal_travel");
    public static final BLSoundEvent PORTAL_TRIGGER = reg("portal_trigger");

    private static BLSoundEvent reg(String name) {
        BLSoundEvent event = new BLSoundEvent(name);
        GameRegistry.register(event);
        SOUNDS.add(event);
        return event;
    }
    
    public static void preInit() {
    	assert !SOUNDS.isEmpty();
    }
}
