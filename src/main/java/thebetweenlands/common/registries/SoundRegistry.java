package thebetweenlands.common.registries;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.client.sound.BLSoundEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SoundRegistry {
    public static List<SoundEvent> SOUNDS = new ArrayList<>();
    public static BLSoundEvent ACIENT = new BLSoundEvent("ancient");
    public static BLSoundEvent AMBIENT_BLOOD_SKY = new BLSoundEvent("ambient_blood_sky");
    public static BLSoundEvent AMBIENT_CAVE = new BLSoundEvent("ambient_cave");
    public static BLSoundEvent AMBIENT_SIGHT_FORTRESS = new BLSoundEvent("ambient_wight_fortress");
    public static BLSoundEvent AMBIENT_SPOOPY = new BLSoundEvent("ambient_spoopy");
    public static BLSoundEvent AMBIENT_SWAMP = new BLSoundEvent("ambient_swamp");
    public static BLSoundEvent AMBIENT_WATER = new BLSoundEvent("ambient_water");
    public static BLSoundEvent ANGLER_ATTACK = new BLSoundEvent("angler_attack");
    public static BLSoundEvent ANGLER_DEATH = new BLSoundEvent("angler_death");
    public static BLSoundEvent ASTATOS = new BLSoundEvent("astatos");
    public static BLSoundEvent BENEATH_A_GREEN_SKY = new BLSoundEvent("beneath_a_green_sky");
    public static BLSoundEvent BETWEEN_YOU_AND_ME = new BLSoundEvent("between_you_and_me");
    public static BLSoundEvent BLOOD_SKY_ROAR = new BLSoundEvent("blood_sky_roar");
    public static BLSoundEvent BL_DIMENSION = new BLSoundEvent("bl_dimension");
    public static BLSoundEvent BL_MUSIC = new BLSoundEvent("bl_menu");
    public static BLSoundEvent CHRISTMAS_ON_THE_MARSH = new BLSoundEvent("christmas_on_the_marsh");
    public static BLSoundEvent CRAB_SNIP = new BLSoundEvent("crab_snip");
    public static BLSoundEvent CRUMBLE = new BLSoundEvent("crumble");
    public static BLSoundEvent DARK_DRUID_DEATH = new BLSoundEvent("dark_druid_death");
    public static BLSoundEvent DARK_DRUID_HIT = new BLSoundEvent("dark_druid_hit");
    public static BLSoundEvent DARK_DRUID_LIVING = new BLSoundEvent("dark_druid_living");
    public static BLSoundEvent DJ_WIGHTS_MIXTAPE = new BLSoundEvent("dj_wights_mixtape");
    public static BLSoundEvent DRAGONFLY = new BLSoundEvent("dragonfly");
    public static BLSoundEvent DREADFUL_PEAT_MUMMY_BITE = new BLSoundEvent("dreadful_peat_mummy_bite");
    public static BLSoundEvent DREADFUL_PEAT_MUMMY_DEATH = new BLSoundEvent("dreadful_peat_mummy_death");
    public static BLSoundEvent DREADFUL_PEAT_MUMMY_EMERGE = new BLSoundEvent("dreadful_peat_mummy_emerge");
    public static BLSoundEvent DREADFUL_PEAT_MUMMY_HURT = new BLSoundEvent("dreadful_peat_mummy_hurt");
    public static BLSoundEvent DREADFUL_PEAT_MUMMY_LICK = new BLSoundEvent("dreadful_peat_mummy_lick");
    public static BLSoundEvent DREADFUL_PEAT_MUMMY_LIVING = new BLSoundEvent("dreadful_peat_mummy_living");
    public static BLSoundEvent DREADFUL_PEAT_MUMMY_LOOP = new BLSoundEvent("dreadful_peat_mummy_loop");
    public static BLSoundEvent DREADFUL_PEAT_MUMMY_RETCH = new BLSoundEvent("dreadful_peat_mummy_retch");
    public static BLSoundEvent DREADFUL_PEAT_MUMMY_SCREAM = new BLSoundEvent("dreadful_peat_mummy_scream");
    public static BLSoundEvent DREADFUL_PEAT_MUMMY_SWIPE = new BLSoundEvent("dreadful_peat_mummy_swipe");
    public static BLSoundEvent DRUID_CHANT = new BLSoundEvent("druid_chant");
    public static BLSoundEvent DRUID_TELEPORT = new BLSoundEvent("druid_teleport");
    public static BLSoundEvent FIG = new BLSoundEvent("fig");
    public static BLSoundEvent FLYING_FIEND_DEATH = new BLSoundEvent("flying_fiend_death");
    public static BLSoundEvent FLYING_FIEND_HURT = new BLSoundEvent("flying_fiend_hurt");
    public static BLSoundEvent FLYING_FIEND_LIVING = new BLSoundEvent("flying_fiend_living");
    public static BLSoundEvent FORTRESS_BOSS_DEATH = new BLSoundEvent("fortress_boss_death");
    public static BLSoundEvent FORTRESS_BOSS_HURT = new BLSoundEvent("fortress_boss_hurt");
    public static BLSoundEvent FORTRESS_BOSS_LIVING = new BLSoundEvent("fortress_boss_living");
    public static BLSoundEvent FORTRESS_BOSS_LOOP = new BLSoundEvent("fortress_boss_loop");
    public static BLSoundEvent FORTRESS_BOSS_NOPE = new BLSoundEvent("fortress_boss_nope");
    public static BLSoundEvent FORTRESS_BOSS_SHIELD_DOWN = new BLSoundEvent("fortress_boss_shield_down");
    public static BLSoundEvent FORTRESS_BOSS_SUMMON_PROJECTILES = new BLSoundEvent("fortress_boss_summon_projectiles");
    public static BLSoundEvent FORTRESS_BOSS_TELEPORT = new BLSoundEvent("fortress_boss_teleport");
    public static BLSoundEvent FORTRESS_PUZZLE_CAGE_BREAK = new BLSoundEvent("fortress_puzzle_cage_break");
    public static BLSoundEvent FORTRESS_PUZZLE_ORB = new BLSoundEvent("fortress_puzzle_orb");
    public static BLSoundEvent FORTRESS_PUZZLE_SWORD = new BLSoundEvent("fortress_puzzle_sword");
    public static BLSoundEvent FORTRESS_TELEPORT = new BLSoundEvent("fortress_teleport");
    public static BLSoundEvent FROG_DEATH = new BLSoundEvent("frog_death");
    public static BLSoundEvent FROG_HURT = new BLSoundEvent("frog_hurt");
    public static BLSoundEvent FROG_LIVING = new BLSoundEvent("frog_living");
    public static BLSoundEvent GECKO_DEATH = new BLSoundEvent("gecko_death");
    public static BLSoundEvent GECKO_HIDE = new BLSoundEvent("gecko_hide");
    public static BLSoundEvent GECKO_HURT = new BLSoundEvent("gecko_hurt");
    public static BLSoundEvent GECKO_LIVING = new BLSoundEvent("gecko_living");
    public static BLSoundEvent GIANT_TOAD_DEATH = new BLSoundEvent("giant_toad_death");
    public static BLSoundEvent GIANT_TOAD_HURT = new BLSoundEvent("giant_toad_hurt");
    public static BLSoundEvent GIANT_TOAD_LIVING = new BLSoundEvent("giant_toad_living");
    public static BLSoundEvent GRIND = new BLSoundEvent("grind");
    public static BLSoundEvent HAG_DANCE = new BLSoundEvent("hag_dance");
    public static BLSoundEvent INFUSER_FINISHED = new BLSoundEvent("infuser_finished");
    public static BLSoundEvent LONELY_FIRE = new BLSoundEvent("lonely_fire");
    public static BLSoundEvent LURKER_HURT = new BLSoundEvent("lurker_hurt");
    public static BLSoundEvent LURKER_LIVING = new BLSoundEvent("lurker_living");
    public static BLSoundEvent LURKER_DEATH = new BLSoundEvent("lurker_death");
    public static BLSoundEvent ONWARD = new BLSoundEvent("onwards");
    public static BLSoundEvent PEAT_MUMMY_CHARGE = new BLSoundEvent("peat_mummy_charge");
    public static BLSoundEvent PEAT_MUMMY_DEATH = new BLSoundEvent("peat_mummy_death");
    public static BLSoundEvent PEAT_MUMMY_EMERGE = new BLSoundEvent("peat_mummy_emerge");
    public static BLSoundEvent PEAT_MUMMY_HURT = new BLSoundEvent("peat_mummy_hurt");
    public static BLSoundEvent PEAT_MUMMY_LIVING = new BLSoundEvent("peat_mummy_living");
    public static BLSoundEvent PORTAL = new BLSoundEvent("portal");
    public static BLSoundEvent PORTAL_ACTIVATE = new BLSoundEvent("portal_activate");
    public static BLSoundEvent PORTAL_TRAVEL = new BLSoundEvent("portal_travel");
    public static BLSoundEvent PORTAL_TRIGGER = new BLSoundEvent("portal_trigger");
    public static BLSoundEvent POSSESSED_SCREAM = new BLSoundEvent("possessed_scream");
    public static BLSoundEvent PURIFIER = new BLSoundEvent("purifier");
    public static BLSoundEvent PYRAD_DEATH = new BLSoundEvent("pyrad_death");
    public static BLSoundEvent PYRAD_HURT = new BLSoundEvent("pyrad_hurt");
    public static BLSoundEvent PYRAD_LIVING = new BLSoundEvent("pyrad_living");
    public static BLSoundEvent REJECTED = new BLSoundEvent("rejected");
    public static BLSoundEvent ROWBOAT_ROW = new BLSoundEvent("rowboat_row");
    public static BLSoundEvent ROWBOAT_ROW_START = new BLSoundEvent("rowboat_row_start");
    public static BLSoundEvent SHOCKWAVE_SWORD = new BLSoundEvent("shockwave_sword");
    public static BLSoundEvent SNAIL_DEATH = new BLSoundEvent("snail_death");
    public static BLSoundEvent SNAIL_HURT = new BLSoundEvent("snail_hurt");
    public static BLSoundEvent SNAIL_LIVING = new BLSoundEvent("snail_living");
    public static BLSoundEvent SORRY = new BLSoundEvent("sorry");
    public static BLSoundEvent SPIKE = new BLSoundEvent("spike");
    public static BLSoundEvent SPORELING_DEATH = new BLSoundEvent("sporeling_death");
    public static BLSoundEvent SPORELING_HURT = new BLSoundEvent("sporeling_hurt");
    public static BLSoundEvent SPORELING_LIVING = new BLSoundEvent("sporeling_living");
    public static BLSoundEvent SQUISH = new BLSoundEvent("squish");
    public static BLSoundEvent STUCK_IN_THE_MUD = new BLSoundEvent("stuck_in_the_mud");
    public static BLSoundEvent SWAMP_HAG_DEATH = new BLSoundEvent("swamp_hag_death");
    public static BLSoundEvent SWAMP_HAG_HURT = new BLSoundEvent("swamp_hag_hurt");
    public static BLSoundEvent SWAMP_HAG_LIVING = new BLSoundEvent("swamp_hag_living");
    public static BLSoundEvent SWAMP_HAG_LIVING_1 = new BLSoundEvent("swamp_hag_living_1");
    public static BLSoundEvent SWAMP_HAG_LIVING_2 = new BLSoundEvent("swamp_hag_living_2");
    public static BLSoundEvent SWAMP_HAG_LIVING_3 = new BLSoundEvent("swamp_hag_living_3");
    public static BLSoundEvent SWAMP_HAG_LIVING_4 = new BLSoundEvent("swamp_hag_living_4");
    public static BLSoundEvent TAR_BEAST_DEATH = new BLSoundEvent("tar_beast_death");
    public static BLSoundEvent TAR_BEAST_HURT = new BLSoundEvent("tar_beast_hurt");
    public static BLSoundEvent TAR_BEAST_LIVING = new BLSoundEvent("tar_beast_living");
    public static BLSoundEvent TAR_BEAST_STEP = new BLSoundEvent("tar_beast_step");
    public static BLSoundEvent TAR_BEAST_SUCK = new BLSoundEvent("tar_beast_suck");
    public static BLSoundEvent TEMPLE_GUARDIAN_BESERKER_CHARGE = new BLSoundEvent("temple_guardian_berserker_charge");
    public static BLSoundEvent TEMPLE_GUARDIAN_BESERKER_IMPACT = new BLSoundEvent("temple_guardian_berserker_impact");
    public static BLSoundEvent TEMPLE_GUARDIAN_BESERKER_LIVING = new BLSoundEvent("temple_guardian_berserker_living");
    public static BLSoundEvent TEMPLE_GUARDIAN_DEATH = new BLSoundEvent("temple_guardian_death");
    public static BLSoundEvent TEMPLE_GUARDIAN_HURT = new BLSoundEvent("temple_guardian_hurt");
    public static BLSoundEvent TEMPLE_GUARDIAN_MELEE_LIVING = new BLSoundEvent("temple_guardian_melee_living");
    public static BLSoundEvent TEMPLE_GUARDIAN_STEP = new BLSoundEvent("temple_guardian_step");
    public static BLSoundEvent TERMITE_LIVING = new BLSoundEvent("termite_living");
    public static BLSoundEvent THE_EXPLORER = new BLSoundEvent("the_explorer");
    public static BLSoundEvent VOODOO_DOLL = new BLSoundEvent("voodoo_doll");
    public static BLSoundEvent WANDERING_WISPS = new BLSoundEvent("wandering_wisps");
    public static BLSoundEvent WATERLOGGED = new BLSoundEvent("waterlogged");
    public static BLSoundEvent WIGHT_HURT = new BLSoundEvent("wight_hurt");
    public static BLSoundEvent WIGHT_MOAN = new BLSoundEvent("wight_moan");
    public static BLSoundEvent _16612 = new BLSoundEvent("16612");


    public void init() {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.getType().isAssignableFrom(SoundEvent.class)) {
                    SoundEvent sound = (SoundEvent) field.get(this);
                    GameRegistry.register(sound);
                    SOUNDS.add(sound);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
