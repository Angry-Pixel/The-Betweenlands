package thebetweenlands.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;

public class BLSoundDefinitionProvider extends SoundDefinitionsProvider {

	public BLSoundDefinitionProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, TheBetweenlands.ID, helper);
	}

	@Override
	public void registerSounds() {
		// Music
		add(SoundRegistry.BL_MUSIC_MENU, definition().with(
			sound("menu/the_adventure_begins").stream(),
			sound("menu/a_foreboding_welcome").stream(),
			sound("menu/this_is_where_it_starts").stream()
		));
		add(SoundRegistry.BL_MUSIC_DIMENSION, definition().with(
			sound("music/enter_the_mire").stream(),
			sound("music/numbskull").stream(),
			sound("music/the_quietus").stream(),
			sound("music/emerald_embers").stream(),
			sound("music/barrow_mounds").stream(),
			sound("music/dont_follow_the_whisps").stream(),
			sound("music/ghostfaces").stream(),
			sound("music/incantation").stream(),
			sound("music/murk_beneath_a_twinkling_void").stream(),
			sound("music/crocodile_tears").stream(),
			sound("music/in_between").stream(),
			sound("music/leech_love_part_1").stream(),
			sound("music/leech_love_part_2").stream(),
			sound("music/rip_in_the_fold").stream(),
			sound("music/spore_ballad").stream(),
			sound("music/the_other_world").stream()
		));
		add(SoundRegistry.GREEBLING_MUSIC_1, definition().with(
			sound("music/greebling_music_1").preload()
		).subtitle("blsubtitles.entity.greebling_music"));
		add(SoundRegistry.GREEBLING_MUSIC_2, definition().with(
			sound("music/greebling_music_2").preload()
		).subtitle("blsubtitles.entity.greebling_music"));

		// Records
		add(SoundRegistry._16612, definition().with(
			sound("records/16612").stream()
		));
		add(SoundRegistry.ANCIENT, definition().with(
			sound("records/ancient").stream()
		));
		add(SoundRegistry.BENEATH_A_GREEN_SKY, definition().with(
			sound("records/beneath_a_green_sky").stream()
		));
		add(SoundRegistry.BETWEEN_YOU_AND_ME, definition().with(
			sound("records/between_you_and_me").stream()
		));
		add(SoundRegistry.CHRISTMAS_ON_THE_MARSH, definition().with(
			sound("records/christmas_on_the_marsh").stream()
		));
		add(SoundRegistry.DJ_WIGHTS_MIXTAPE, definition().with(
			sound("records/dj_wights_mixtape").stream()
		));
		add(SoundRegistry.HAG_DANCE, definition().with(
			sound("records/hag_dance").stream()
		));
		add(SoundRegistry.LONELY_FIRE, definition().with(
			sound("records/lonely_fire").stream()
		));
		add(SoundRegistry.ONWARD, definition().with(
			sound("records/onwards").stream()
		));
		add(SoundRegistry.STUCK_IN_THE_MUD, definition().with(
			sound("records/stuck_in_the_mud").stream()
		));
		add(SoundRegistry.THE_EXPLORER, definition().with(
			sound("records/the_explorer").stream()
		));
		add(SoundRegistry.WANDERING_WISPS, definition().with(
			sound("records/wandering_wisps").stream()
		));
		add(SoundRegistry.WATERLOGGED, definition().with(
			sound("records/waterlogged").stream()
		));
		add(SoundRegistry.DEEP_WATER_THEME, definition().with(
			sound("records/deep_water_theme").stream()
		));

		//Ambience
		add(SoundRegistry.AMBIENT_BLOOD_SKY_ROAR, definition().with(
			sound("ambience/ambient_blood_sky_roar")
		).subtitle("blsubtitles.ambient.blood_sky"));
		add(SoundRegistry.AMBIENT_BLOOD_SKY, definition().with(
			sound("ambience/ambient_blood_sky").stream()
		));
		add(SoundRegistry.AMBIENT_CAVE, definition().with(
			sound("ambience/ambient_cave").stream()
		));
		add(SoundRegistry.AMBIENT_CAVE_SPOOK, definition().with(
			sound("ambience/ambient_cave_spook_1"),
			sound("ambience/ambient_cave_spook_2"),
			sound("ambience/ambient_cave_spook_3")
		).subtitle("blsubtitles.ambient.cave_spook"));
		add(SoundRegistry.AMBIENT_WIGHT_FORTRESS, definition().with(
			sound("ambience/wight_fortress_ambience").stream()
		));
		add(SoundRegistry.AMBIENT_SPOOPY, definition().with(
			sound("ambience/ambient_spoopy").stream()
		));
		add(SoundRegistry.AMBIENT_SWAMP, definition().with(
			sound("ambience/ambient_swamp").stream()
		));
		add(SoundRegistry.AMBIENT_SWAMP_DENSE_FOG, definition().with(
			sound("ambience/ambient_swamp_dense_fog").stream()
		));
		add(SoundRegistry.AMBIENT_DEEP_WATERS, definition().with(
			sound("ambience/ambient_deep_waters").stream()
		));
		add(SoundRegistry.AMBIENT_WATER, definition().with(
			sound("ambience/water_ambience").stream()
		));
		add(SoundRegistry.AMBIENT_FROSTY, definition().with(
			sound("ambience/ambient_frosty").stream()
		));
		add(SoundRegistry.AMBIENT_SNOWFALL, definition().with(
			sound("ambience/ambient_snowfall").stream()
		));
		add(SoundRegistry.AMBIENT_SLUDGE_WORM_DUNGEON, definition().with(
			sound("ambience/sludge_worm_dungeon_ambience").stream()
		));
		add(SoundRegistry.AMBIENT_FLOATING_ISLAND, definition().with(
			sound("ambience/ambient_floating_island").stream()
		));
		add(SoundRegistry.LIGHTNING, definition().with(
			sound("ambience/lightning_1"),
			sound("ambience/lightning_2"),
			sound("ambience/lightning_3")
		).subtitle("blsubtitles.ambient.lightning"));
		add(SoundRegistry.THUNDER, definition().with(
			sound("ambience/thunder_1"),
			sound("ambience/thunder_2"),
			sound("ambience/thunder_3"),
			sound("ambience/thunder_4"),
			sound("ambience/thunder_5")
		).subtitle("blsubtitles.ambient.thunder"));
		add(SoundRegistry.RAIN_STRONG, definition().with(
			sound("ambience/rain_strong")
		));
		add(SoundRegistry.RAIN_MEDIUM, definition().with(
			sound("ambience/rain_medium")
		).subtitle("blsubtitles.ambient.rain"));
		add(SoundRegistry.RAIN_WEAK, definition().with(
			sound("ambience/rain_weak")
		));
		add(SoundRegistry.RAIN_DRIPPING, definition().with(
			sound("ambience/rain_dripping")
		));
		add(SoundRegistry.RAIN_MOUNT, definition().with(
			sound("ambience/rain_mount")
		));

		// Miscellaneous
		add(SoundRegistry.CRUMBLE, definition().with(
			sound("crumble")
		).subtitle("blsubtitles.misc.crumble"));
		add(SoundRegistry.FIG, definition().with(
			sound("fig")
		).subtitle("blsubtitles.misc.fig"));
		add(SoundRegistry.SPIKE, definition().with(
			sound("spike")
		).subtitle("blsubtitles.misc.spike"));
		add(SoundRegistry.POSSESSED_SCREAM, definition().with(
			sound("possessed_scream")
		).subtitle("blsubtitles.misc.possessed_scream"));
		add(SoundRegistry.SORRY, definition().with(
			sound("sorry")
		).subtitle("blsubtitles.misc.angry_pebble"));
		add(SoundRegistry.PEBBLE_HISS, definition().with(
			vanillaSound("random/fuse")
		).subtitle("blsubtitles.misc.angry_pebble.hiss"));
		add(SoundRegistry.REJECTED, definition().with(
			sound("rejected")
		).subtitle("blsubtitles.misc.rejected"));
		add(SoundRegistry.SHOCKWAVE_SWORD, definition().with(
			sound("shockwave_sword")
		).subtitle("blsubtitles.misc.shockwave"));
		add(SoundRegistry.SQUISH, definition().with(
			sound("squish")
		).subtitle("blsubtitles.entity.squish"));
		add(SoundRegistry.CRUNCH, definition().with(
			sound("crunch_1"),
			sound("crunch_2"),
			sound("crunch_3")
		).subtitle("blsubtitles.entity.crunch"));
		add(SoundRegistry.IGNITE, definition().with(
			sound("ignite")
		));
		add(SoundRegistry.DAMAGE_REDUCTION, definition().with(
			sound("damage_reduction")
		));
		add(SoundRegistry.RIFT_OPEN, definition().with(
			sound("rift_open")
		).subtitle("blsubtitles.ambient.rift_open"));
		add(SoundRegistry.RIFT_CREAK, definition().with(
			sound("rift_creak_1"),
			sound("rift_creak_2"),
			sound("rift_creak_3")
		).subtitle("blsubtitles.ambient.rift_creak"));
		add(SoundRegistry.ROOF_COLLAPSE, definition().with(
			sound("roof_collapse")
		).subtitle("blsubtitles.misc.roof_collapse"));
		add(SoundRegistry.BRAZIER_LIGHT,definition().with(
			sound("brazier_light")
		).subtitle("blsubtitles.misc.brazier_light"));
		add(SoundRegistry.BEAM_ACTIVATE, definition().with(
			sound("beam_activate")
		).subtitle("blsubtitles.misc.beam_activate"));
		add(SoundRegistry.PLUG_HIT, definition().with(
			sound("plug_hit")
		).subtitle("blsubtitles.misc.plug_hit"));
		add(SoundRegistry.PLUG_LOCK, definition().with(
			sound("plug_lock")
		).subtitle("blsubtitles.misc.plug_lock"));
		add(SoundRegistry.CHAIN, definition().with(
			sound("chain")
		).subtitle("blsubtitles.misc_chain"));
		add(SoundRegistry.CHAIN_LONG, definition().with(
			sound("chain_long")
		));
		add(SoundRegistry.GEARS, definition().with(
			sound("gears")
		).subtitle("blsubtitles.misc.gears"));
		add(SoundRegistry.WALL_SLIDE, definition().with(
			sound("wall_slide")
		).subtitle("blsubtitles.misc.wall_slide"));
		add(SoundRegistry.WALL_SLAM, definition().with(
			sound("wall_slam")
		).subtitle("blsubtitles.misc.wall_slam"));
		add(SoundRegistry.POOP_JET, definition().with(
			sound("poop_jet")
		).subtitle("blsubtitles.misc.poopjet"));
		add(SoundRegistry.PIT_FALL, definition().with(
			sound("pit_fall")
		));
		add(SoundRegistry.DRAETON_TURN, definition().with(
			sound("draeton_turn_1"),
			sound("draeton_turn_2"),
			sound("draeton_turn_3"),
			sound("draeton_turn_4"),
			sound("draeton_turn_5")
		).subtitle("blsubtitles.misc.draeton_turn"));
		add(SoundRegistry.DRAETON_DAMAGE, definition().with(
			sound("draeton_damage_1"),
			sound("draeton_damage_2"),
			sound("draeton_damage_3"),
			sound("draeton_damage_4")
		).subtitle("blsubtitles.misc.draeton_damage"));
		add(SoundRegistry.DRAETON_BURNER, definition().with(
			sound("draeton_burner")
		));
		add(SoundRegistry.DRAETON_PULLEY, definition().with(
			sound("draeton_pulley")
		).subtitle("blsubtitles.misc.draeton_pulley"));
		add(SoundRegistry.DRAETON_ANCHOR, definition().with(
			sound("draeton_anchor")
		).subtitle("blsubtitles.misc.draeton_anchor"));
		// TODO no idea why this breaks runData
//		add(SoundRegistry.DRAETON_LEAK_START, definition().with(
//			sound("thebeweenlands:draeton_leak_1")
//		).subtitle("blsubtitles.misc.draeton_leak"));
		add(SoundRegistry.DRAETON_LEAK_LOOP, definition().with(
			sound("draeton_leak_2")
		));
		add(SoundRegistry.DRAETON_PLUG, definition().with(
			sound("draeton_plug")
		).subtitle("blsubtitles.misc.draeton_plug"));
		add(SoundRegistry.DRAETON_POP, definition().with(
			sound("draeton_pop")
		));
		add(SoundRegistry.GREEBLING_FALL, definition().with(
			sound("greebling_fall")
		).subtitle("blsubtitles.entity.greebling.fall"));
		add(SoundRegistry.GREEBLING_HEY, definition().with(
			sound("greebling_hey")
		).subtitle("blsubtitles.entity.greebling.whistle"));
		add(SoundRegistry.GREEBLING_GIGGLE, definition().with(
			sound("greebling_giggle"),
			sound("greebling_laugh")
		).subtitle("blsubtitles.entity.greebling.giggle"));
		add(SoundRegistry.CHIROMAW_GREEBLING_RIDER_LIVING, definition().with(
			sound("flying_fiend_living_1"),
			sound("flying_fiend_living_2"),
			sound("flying_fiend_living_3"),
			sound("flying_fiend_living_4")
		).subtitle("blsubtitles.entity.flying_fiend.living"));
		add(SoundRegistry.SLINGSHOT_CHARGE, definition().with(
			sound("slingshot_charge")
		).subtitle("blsubtitles.misc.slingshot_charge"));
		add(SoundRegistry.SLINGSHOT_HIT, definition().with(
			sound("slingshot_hit_1"),
			sound("slingshot_hit_2"),
			sound("slingshot_hit_3")
		).subtitle("blsubtitles.misc.slingshot_hit"));
		add(SoundRegistry.SLINGSHOT_SHOOT, definition().with(
			sound("slingshot_shoot")
		).subtitle("blsubtitles.misc.slingshot_shoot"));
		add(SoundRegistry.ZAP, definition().with(
			sound("zap_1"),
			sound("zap_2"),
			sound("zap_3"),
			sound("zap_4"),
			sound("zap_5"),
			sound("zap_6"),
			sound("zap_7"),
			sound("zap_8")
		).subtitle("blsubtitles.misc.zap"));
		add(SoundRegistry.CHIROBARB_ERUPTER, definition().with(
			sound("chirobarb_erupter")
		).subtitle("blsubtitles.misc.chriobarb_erupter"));
		add(SoundRegistry.SIMULACRUM_BREAK, definition().with(
			sound("simulacrum_break")
		).subtitle("blsubtitles.misc.simulacrum_break"));
		add(SoundRegistry.GREEBLING_HUM, definition().with(
			sound("greebling_hum")
		).subtitle("blsubtitles.entity.greebling.hum"));
		add(SoundRegistry.CORACLE_SINK, definition().with(
			sound("coracle_sink")
		).subtitle("blsubtitles.entity.greebling.coracle_sink"));
		add(SoundRegistry.SPEAR_THROW, definition().with(
			sound("spear_throw")
		).subtitle("blsubtitles.misc.spear.throw"));
		add(SoundRegistry.SPEAR_LAND, definition().with(
			sound("spear_land")
		).subtitle("blsubtitles.misc.spear.lands"));
		add(SoundRegistry.SPEAR_RETURN_1, definition().with(
			sound("spear_return_1")
		).subtitle("blsubtitles.misc.spear.return"));
		add(SoundRegistry.SPEAR_RETURN_2, definition().with(
			sound("spear_return_2")
		).subtitle("blsubtitles.misc.spear.return"));

		// Hostiles
		add(SoundRegistry.ANGLER_ATTACK, definition().with(
			sound("angler_attack_1"),
			sound("angler_attack_2")
		).subtitle("blsubtitles.entity.angler.attack"));
		add(SoundRegistry.ASHSPRITE_HURT, definition().with(
			sound("ashsprite_hurt_1")
		).subtitle("blsubtitles.entity.ashprite.hurt"));
		add(SoundRegistry.ASHSPRITE_DEATH, definition().with(
			sound("ashsprite_death")
		).subtitle("blsubtitles.entity.ashprite.death"));
		add(SoundRegistry.ASHSPRITE_LIVING, definition().with(
			sound("ashsprite_living_1"),
			sound("ashsprite_living_2"),
			sound("ashsprite_living_3")
		).subtitle("blsubtitles.entity.ashprite.living"));
		add(SoundRegistry.SLUDGE_TURRET_HURT, definition().with(
			sound("sludge_turret_hurt_1"),
			sound("sludge_turret_hurt_2")
		).subtitle("blsubtitles.entity.sludge_turret.hurt"));
		add(SoundRegistry.SLUDGE_TURRET_DEATH, definition().with(
			sound("sludge_turret_death")
		).subtitle("blsubtitles.entity.sludge_turret.death"));
		add(SoundRegistry.SLUDGE_TURRET_LIVING, definition().with(
			sound("sludge_turret_living_1"),
			sound("sludge_turret_living_2"),
			sound("sludge_turret_living_3"),
			sound("sludge_turret_living_4")
		).subtitle("blsubtitles.entity.sludge_turret.living"));
		add(SoundRegistry.DARK_DRUID_DEATH, definition().with(
			sound("dark_druid_death")
		).subtitle("blsubtitles.entity.druid.death"));
		add(SoundRegistry.DARK_DRUID_HIT, definition().with(
			sound("dark_druid_hit")
		).subtitle("blsubtitles.entity.druid.hurt"));
		add(SoundRegistry.DARK_DRUID_LIVING, definition().with(
			sound("dark_druid_living_1"),
			sound("dark_druid_living_2")
		).subtitle("blsubtitles.entity.druid.living"));
		add(SoundRegistry.DRUID_CHANT, definition().with(
			sound("druid_chant")
		).subtitle("blsubtitles.misc.druid_chant"));
		add(SoundRegistry.DRUID_TELEPORT, definition().with(
			sound("druid_teleport")
		).subtitle("blsubtitles.entity.druid.teleport"));
		add(SoundRegistry.DREADFUL_PEAT_MUMMY_BITE, definition().with(
			sound("dreadful_peat_mummy_bite")
		).subtitle("blsubtitles.entity.dreadful_peat_mummy.bite"));
		add(SoundRegistry.DREADFUL_PEAT_MUMMY_DEATH, definition().with(
			sound("dreadful_peat_mummy_death")
		).subtitle("blsubtitles.entity.dreadful_peat_mummy.death"));
		add(SoundRegistry.DREADFUL_PEAT_MUMMY_EMERGE, definition().with(
			sound("dreadful_peat_mummy_emerge")
		).subtitle("blsubtitles.entity.dreadful_peat_mummy.emerge"));
		add(SoundRegistry.DREADFUL_PEAT_MUMMY_HURT, definition().with(
			sound("dreadful_peat_mummy_hurt_1"),
			sound("dreadful_peat_mummy_hurt_2"),
			sound("dreadful_peat_mummy_hurt_3"),
			sound("dreadful_peat_mummy_hurt_4"),
			sound("dreadful_peat_mummy_hurt_5")
		).subtitle("blsubtitles.entity.dreadful_peat_mummy.hurt"));
		add(SoundRegistry.DREADFUL_PEAT_MUMMY_LICK, definition().with(
			sound("dreadful_peat_mummy_lick")
		).subtitle("blsubtitles.entity.dreadful_peat_mummy.lick"));
		add(SoundRegistry.DREADFUL_PEAT_MUMMY_LIVING, definition().with(
			sound("dreadful_peat_mummy_living_1"),
			sound("dreadful_peat_mummy_living_2"),
			sound("dreadful_peat_mummy_living_3"),
			sound("dreadful_peat_mummy_living_4"),
			sound("dreadful_peat_mummy_living_5")
		).subtitle("blsubtitles.entity.dreadful_peat_mummy.living"));
		add(SoundRegistry.DREADFUL_PEAT_MUMMY_LOOP, definition().with(
			sound("boss/dreadful_peat_mummy_loop").stream()
		));
		add(SoundRegistry.DREADFUL_PEAT_MUMMY_RETCH, definition().with(
			sound("dreadful_peat_mummy_retch")
		).subtitle("blsubtitles.entity.dreadful_peat_mummy.retch"));
		add(SoundRegistry.DREADFUL_PEAT_MUMMY_SCREAM, definition().with(
			sound("dreadful_peat_mummy_scream")
		).subtitle("blsubtitles.entity.dreadful_peat_mummy.scream"));
		add(SoundRegistry.DREADFUL_PEAT_MUMMY_SWIPE, definition().with(
			sound("dreadful_peat_mummy_swipe")
		).subtitle("blsubtitles.entity.dreadful_peat_mummy.swipe"));
		add(SoundRegistry.PEAT_MUMMY_CHARGE, definition().with(
			sound("peat_mummy_charge")
		).subtitle("blsubtitles.entity.peat_mummy.scream"));
		add(SoundRegistry.PEAT_MUMMY_DEATH, definition().with(
			sound("peat_mummy_death")
		).subtitle("blsubtitles.entity.peat_mummy.death"));
		add(SoundRegistry.PEAT_MUMMY_EMERGE, definition().with(
			sound("peat_mummy_emerge")
		).subtitle("blsubtitles.entity.peat_mummy.emerge"));
		add(SoundRegistry.PEAT_MUMMY_HURT, definition().with(
			sound("peat_mummy_hurt_1"),
			sound("peat_mummy_hurt_2"),
			sound("peat_mummy_hurt_3"),
			sound("peat_mummy_hurt_4"),
			sound("peat_mummy_hurt_5")
		).subtitle("blsubtitles.entity.peat_mummy.hurt"));
		add(SoundRegistry.PEAT_MUMMY_LIVING, definition().with(
			sound("peat_mummy_living_1"),
			sound("peat_mummy_living_2"),
			sound("peat_mummy_living_3"),
			sound("peat_mummy_living_4"),
			sound("peat_mummy_living_5")
		).subtitle("blsubtitles.entity.peat_mummy.living"));
		add(SoundRegistry.FLYING_FIEND_DEATH, definition().with(
			sound("flying_fiend_death")
		).subtitle("blsubtitles.entity.flying_fiend.death"));
		add(SoundRegistry.FLYING_FIEND_HURT, definition().with(
			sound("flying_fiend_hurt_1"),
			sound("flying_fiend_hurt_2")
		).subtitle("blsubtitles.entity.flying_fiend.hurt"));
		add(SoundRegistry.FLYING_FIEND_LIVING, definition().with(
			sound("flying_fiend_living_1"),
			sound("flying_fiend_living_2"),
			sound("flying_fiend_living_3"),
			sound("flying_fiend_living_4")
		).subtitle("blsubtitles.entity.flying_fiend.living"));
		add(SoundRegistry.FORTRESS_BOSS_DEATH, definition().with(
			sound("fortress_boss_death")
		).subtitle("blsubtitles.entity.fortress_boss.death"));
		add(SoundRegistry.FORTRESS_BOSS_HURT, definition().with(
			sound("fortress_boss_hurt_1"),
			sound("fortress_boss_hurt_2"),
			sound("fortress_boss_hurt_3"),
			sound("fortress_boss_hurt_4")
		).subtitle("blsubtitles.fortress_boss.hurt"));
		add(SoundRegistry.FORTRESS_BOSS_LIVING, definition().with(
			sound("fortress_boss_living_1"),
			sound("fortress_boss_living_2"),
			sound("fortress_boss_living_3")
		));
		add(SoundRegistry.FORTRESS_BOSS_LOOP, definition().with(
			sound("boss/fortress_boss_loop").stream()
		));
		add(SoundRegistry.FORTRESS_BOSS_NOPE, definition().with(
			sound("fortress_boss_nope")
		).subtitle("blsubtitles.entity.fortress_boss.nope"));
		add(SoundRegistry.FORTRESS_BOSS_SHIELD_DOWN, definition().with(
			sound("fortress_boss_shield_down")
		).subtitle("blsubtitles.entity.fortress_boss.shield_down"));
		add(SoundRegistry.FORTRESS_BOSS_SUMMON_PROJECTILES, definition().with(
			sound("fortress_boss_summon_projectiles")
		).subtitle("blsubtitles.entity.fortress_boss.projectiles"));
		add(SoundRegistry.FORTRESS_BOSS_TELEPORT, definition().with(
			sound("fortress_boss_teleport")
		).subtitle("blsubtitles.entity.fortress_boss.teleport"));
		add(SoundRegistry.FORTRESS_PUZZLE_CAGE_BREAK, definition().with(
			sound("fortress_puzzle_cage_break")
		).subtitle("blsubtitles.misc.fortress_puzzle_cage_break"));
		add(SoundRegistry.FORTRESS_PUZZLE_ORB, definition().with(
			sound("fortress_puzzle_orb")
		));
		add(SoundRegistry.FORTRESS_PUZZLE_SWORD, definition().with(
			sound("fortress_puzzle_sword")
		).subtitle("blsubtitles.misc.fortress_puzzle_sword"));
		add(SoundRegistry.FORTRESS_TELEPORT, definition().with(
			sound("fortress_teleport")
		).subtitle("blsubtitles.misc.fortress_teleport"));
		add(SoundRegistry.PYRAD_DEATH, definition().with(
			sound("pyrad_death")
		).subtitle("blsubtitles.entity.pyrad.death"));
		add(SoundRegistry.PYRAD_HURT, definition().with(
			sound("pyrad_hurt_1"),
			sound("pyrad_hurt_2"),
			sound("pyrad_hurt_3")
		).subtitle("blsubtitles.entity.pyrad_hurt"));
		add(SoundRegistry.PYRAD_LIVING, definition().with(
			sound("pyrad_living_1"),
			sound("pyrad_living_2"),
			sound("pyrad_living_3")
		).subtitle("blsubtitles.entity.pyrad.living"));
		add(SoundRegistry.PYRAD_SHOOT, definition().with(
			vanillaSound("mob/ghast/fireball4")
		).subtitle("blsubtitles.entity.pyrad.shoot"));
		add(SoundRegistry.LURKER_HURT, definition().with(
			sound("lurker_hurt_1"),
			sound("lurker_hurt_2"),
			sound("lurker_hurt_3"),
			sound("lurker_hurt_4")
		).subtitle("blsubtitles.entity.lurker.hurt"));
		add(SoundRegistry.LURKER_LIVING, definition().with(
			sound("lurker_living_1"),
			sound("lurker_living_2"),
			sound("lurker_living_3"),
			sound("lurker_living_4"),
			sound("lurker_living_5")
		).subtitle("blsubtitles.entity.lurker.living"));
		add(SoundRegistry.LURKER_DEATH, definition().with(
			sound("lurker_death")
		).subtitle("blsubtitles.entity.lurker.death"));
		add(SoundRegistry.SWAMP_HAG_DEATH, definition().with(
			sound("swamp_hag_death")
		).subtitle("blsubtitles.entity.swamp_hag.death"));
		add(SoundRegistry.SWAMP_HAG_HURT, definition().with(
			sound("swamp_hag_hurt_1"),
			sound("swamp_hag_hurt_2"),
			sound("swamp_hag_hurt_3")
		).subtitle("blsubtitles.entity.swamp_hag.hurt"));
		add(SoundRegistry.SWAMP_HAG_LIVING, definition().with(
			sound("swamp_hag_living_1"),
			sound("swamp_hag_living_2"),
			sound("swamp_hag_living_3"),
			sound("swamp_hag_living_4")
		).subtitle("blsubtitles.entity.swamp_hag.living"));
		add(SoundRegistry.SWAMP_HAG_LIVING_1, definition().with(
			sound("swamp_hag_living_1")
		).subtitle("blsubtitles.entity.swamp_hag.living"));
		add(SoundRegistry.SWAMP_HAG_LIVING_2, definition().with(
			sound("swamp_hag_living_2")
		).subtitle("blsubtitles.entity.swamp_hag.living"));
		add(SoundRegistry.SWAMP_HAG_LIVING_3, definition().with(
			sound("swamp_hag_living_3")
		).subtitle("blsubtitles.entity.swamp_hag.living"));
		add(SoundRegistry.SWAMP_HAG_LIVING_4, definition().with(
			sound("swamp_hag_living_4")
		).subtitle("blsubtitles.entity.swamp_hag.living"));
		add(SoundRegistry.TAR_BEAST_DEATH, definition().with(
			sound("tar_beast_death")
		).subtitle("blsubtitles.entity.tar_beast.death"));
		add(SoundRegistry.TAR_BEAST_HURT, definition().with(
			sound("tar_beast_hurt_1"),
			sound("tar_beast_hurt_2")
		).subtitle("blsubtitles.entity.tar_beast.hurt"));
		add(SoundRegistry.TAR_BEAST_LIVING, definition().with(
			sound("tar_beast_living_1"),
			sound("tar_beast_living_2"),
			sound("tar_beast_living_3")
		).subtitle("blsubtitles.entity.tar_beast_living"));
		add(SoundRegistry.TAR_BEAST_STEP, definition().with(
			sound("tar_beast_step_1"),
			sound("tar_beast_step_2"),
			sound("tar_beast_step_3")
		));
		add(SoundRegistry.TAR_BEAST_SUCK, definition().with(
			sound("tar_beast_suck")
		).subtitle("blsubtitles.entity.tar_beast_suck"));
		add(SoundRegistry.TEMPLE_GUARDIAN_BERSERKER_CHARGE, definition().with(
			sound("temple_guardian_berserker_charge")
		));
		add(SoundRegistry.TEMPLE_GUARDIAN_BERSERKER_IMPACT, definition().with(
			sound("temple_guardian_berserker_impact")
		));
		add(SoundRegistry.TEMPLE_GUARDIAN_BERSERKER_LIVING, definition().with(
			sound("temple_guardian_berserker_living_1"),
			sound("temple_guardian_berserker_living_2"),
			sound("temple_guardian_berserker_living_3"),
			sound("temple_guardian_berserker_living_4")
		));
		add(SoundRegistry.TEMPLE_GUARDIAN_DEATH, definition().with(
			sound("temple_guardian_death")
		));
		add(SoundRegistry.TEMPLE_GUARDIAN_HURT, definition().with(
			sound("temple_guardian_hurt_1"),
			sound("temple_guardian_hurt_2"),
			sound("temple_guardian_hurt_3")
		));
		add(SoundRegistry.TEMPLE_GUARDIAN_MELEE_LIVING, definition().with(
			sound("temple_guardian_melee_living_1"),
			sound("temple_guardian_melee_living_2"),
			sound("temple_guardian_melee_living_3"),
			sound("temple_guardian_melee_living_4")
		));
		add(SoundRegistry.TEMPLE_GUARDIAN_STEP, definition().with(
			sound("temple_guardian_step_1"),
			sound("temple_guardian_step_2"),
			sound("temple_guardian_step_3")
		));
		add(SoundRegistry.WIGHT_HURT, definition().with(
			sound("wight_hurt_1"),
			sound("wight_hurt_2")
		).subtitle("blsubtitles.entity.wight.hurt"));
		add(SoundRegistry.WIGHT_MOAN, definition().with(
			sound("wight_moan_1"),
			sound("wight_moan_2"),
			sound("wight_moan_3"),
			sound("wight_moan_4")
		).subtitle("blsubtitles.entity.wight.living"));
		add(SoundRegistry.WIGHT_DEATH, definition().with(
			sound("wight_death")
		).subtitle("blsubtitles.entity.wight.death"));
		add(SoundRegistry.WIGHT_ATTACK, definition().with(
			sound("wight_attack_1"),
			sound("wight_attack_2"),
			sound("wight_attack_3"),
			sound("wight_attack_4")
		).subtitle("blsubtitles.entity.wight.scream"));
		add(SoundRegistry.CRAB_SNIP, definition().with(
			sound("crab_snip")
		).subtitle("blsubtitles.entity.crab.snip"));
		add(SoundRegistry.GAS_CLOUD_LIVING, definition().with(
			sound("gas_cloud_living_1"),
			sound("gas_cloud_living_2")
		).subtitle("blsubtitles.entity.gas_cloud.living"));
		add(SoundRegistry.GAS_CLOUD_HURT, definition().with(
			sound("gas_cloud_hurt_1"),
			sound("gas_cloud_hurt_2")
		).subtitle("blsubtitles.entity.gas_cloud.hurt"));
		add(SoundRegistry.GAS_CLOUD_DEATH, definition().with(
			sound("gas_cloud_death")
		).subtitle("blsubtitles.entity.gas_cloud.death"));
		add(SoundRegistry.BOULDER_SPRITE_LIVING, definition().with(
			sound("boulder_sprite_living_1"),
			sound("boulder_sprite_living_2"),
			sound("boulder_sprite_living_3")
		).subtitle("blsubtitles.entity.boulder_sprite.living"));
		add(SoundRegistry.BOULDER_SPRITE_HURT, definition().with(
			sound("boulder_sprite_hurt_1"),
			sound("boulder_sprite_hurt_2")
		).subtitle("blsubtitles.entity.boulder_sprite.hurt"));
		add(SoundRegistry.BOULDER_SPRITE_DEATH, definition().with(
			sound("boulder_sprite_death")
		).subtitle("blsubtitles.entity.boulder_sprite.death"));
		add(SoundRegistry.BOULDER_SPRITE_ROLL, definition().with(
			sound("boulder_sprite_roll")
		).subtitle("blsubtitles.entity.boulder_sprite.roll"));
		add(SoundRegistry.SPIRIT_TREE_FACE_SMALL_SPIT, definition().with(
			sound("spirit_tree_face_spit")
		).subtitle("blsubtitles.entity.spirit_tree_face_small.spit"));
		add(SoundRegistry.SPIRIT_TREE_FACE_SMALL_LIVING, definition().with(
			sound("spirit_tree_face_large_living_1"),
			sound("spirit_tree_face_large_living_2"),
			sound("spirit_tree_face_large_living_3"),
			sound("spirit_tree_face_large_living_4")
		).subtitle("blsubtitles.entity.spirit_tree_face_small.living"));
		add(SoundRegistry.SPIRIT_TREE_FACE_LARGE_SPIT, definition().with(
			sound("spirit_tree_face_spit")
		).subtitle("blsubtitles.entity.spirit_tree_face_large.spit"));
		add(SoundRegistry.SPIRIT_TREE_FACE_LARGE_DEATH, definition().with(
			sound("spirit_tree_face_large_death")
		).subtitle("blsubtitles.entity.spirit_tree_face_large.death"));
		add(SoundRegistry.SPIRIT_TREE_FACE_LARGE_LIVING, definition().with(
			sound("spirit_tree_face_large_living_1"),
			sound("spirit_tree_face_large_living_2"),
			sound("spirit_tree_face_large_living_3"),
			sound("spirit_tree_face_large_living_4")
		).subtitle("blsubtitles.entity.spirit_tree_face_small.living"));
		add(SoundRegistry.SPIRIT_TREE_FACE_SMALL_EMERGE, definition().with(
			sound("spirit_tree_face_small_emerge")
		));
		add(SoundRegistry.SPIRIT_TREE_FACE_LARGE_EMERGE, definition().with(
			sound("spirit_tree_face_large_emerge")
		));
		add(SoundRegistry.SPIRIT_TREE_FACE_SUCK, definition().with(
			sound("spirit_tree_face_suck")
		));
		add(SoundRegistry.SPIRIT_TREE_FACE_SPIT_ROOT_SPIKES, definition().with(
			sound("spirit_tree_face_spit_root_spikes")
		).subtitle("blsubtitles.entity.spirit_tree_face_large.spit_root_spikes"));
		add(SoundRegistry.SPIRIT_TREE_SPIKE_TRAP, definition().with(
			sound("spirit_tree_spikes")
		).subtitle("blsubtitles.entity.spirit_tree.spikes"));
		add(SoundRegistry.SPIRIT_TREE_SPIKE_TRAP_EMERGE, definition().with(
			sound("spirit_tree_spike_trap_emerge")
		).subtitle("blsubtitles.entity.spirit_tree.spike_trap_emerge"));
		add(SoundRegistry.SPIRIT_TREE_SPIKES, definition().with(
			sound("spirit_tree_spikes")
		).subtitle("blsubtitles.entity.spirit_tree.spikes"));
		add(SoundRegistry.ROOT_SPIKE_PARTICLE_HIT, definition().with(
			sound("root_spike_particle_hit")
		));
		add(SoundRegistry.SHAMBLER_DEATH, definition().with(
			sound("shambler_death")
		).subtitle("blsubtitles.entity.shambler.death"));
		add(SoundRegistry.SHAMBLER_HURT, definition().with(
			sound("shambler_hurt_1"),
			sound("shambler_hurt_2")
		).subtitle("blsubtitles.entity.shambler.hurt"));
		add(SoundRegistry.SHAMBLER_LIVING, definition().with(
			sound("shambler_living_1"),
			sound("shambler_living_2"),
			sound("shambler_living_3"),
			sound("shambler_living_4")
		).subtitle("blsubtitles.entity.shambler.living"));
		add(SoundRegistry.SHAMBLER_LICK, definition().with(
			sound("shambler_lick")
		).subtitle("blsubtitles.entity.shambler.lick"));
		add(SoundRegistry.CRYPT_CRAWLER_DEATH, definition().with(
			sound("crypt_crawler_death")
		).subtitle("blsubtitles.entity.crypt_crawler.death"));
		add(SoundRegistry.CRYPT_CRAWLER_HURT, definition().with(
			sound("crypt_crawler_hurt_1"),
			sound("crypt_crawler_hurt_2")
		).subtitle("blsubtitles.entity.crypt_crawler.hurt"));
		add(SoundRegistry.CRYPT_CRAWLER_LIVING, definition().with(
			sound("crypt_crawler_living_1"),
			sound("crypt_crawler_living_2"),
			sound("crypt_crawler_living_3")
		).subtitle("blsubtitles.entity.crypt_crawler.living"));
		add(SoundRegistry.CRYPT_CRAWLER_DIG, definition().with(
			sound("crypt_crawler_dig")
		).subtitle("blsubtitles.entity.crypt_crawler.dig"));
		add(SoundRegistry.WALL_LAMPREY_SUCK, definition().with(
			sound("wall_lamprey_suck")
		).subtitle("blsubtitles.entity.wall_lamprey.suck"));
		add(SoundRegistry.WALL_LAMPREY_ATTACK, definition().with(
			sound("wall_lamprey_attack_1"),
			sound("wall_lamprey_attack_2"),
			sound("wall_lamprey_attack_3")
		).subtitle("blsubtitles.entity.wall_lamprey.attack"));
		add(SoundRegistry.WALL_LIVING_ROOT_EMERGE, definition().with(
			sound("wall_living_root_emerge")
		).subtitle("blsubtitles.entity.wall_living_root.emerge"));
		add(SoundRegistry.WORM_EMERGE, definition().with(
			sound("worm_emerge")
		).subtitle("blsubtitles.entity.worm.emerge"));
		add(SoundRegistry.WORM_PLOP, definition().with(
			sound("worm_plop")
		));
		add(SoundRegistry.WORM_HURT, definition().with(
			sound("worm_hurt_1"),
			sound("worm_hurt_2")
		).subtitle("blsubtitles.entity.worm.hurt"));
		add(SoundRegistry.WORM_LIVING, definition().with(
			sound("worm_living_1"),
			sound("worm_living_2"),
			sound("worm_living_3")
		).subtitle("blsubtitles.entity.worm.living"));
		add(SoundRegistry.WORM_DEATH, definition().with(
			sound("worm_death")
		).subtitle("blsubtitles.entity.worm.death"));
		add(SoundRegistry.WORM_SPLAT, definition().with(
			sound("worm_splat")
		).subtitle("blsubtitles.entity.worm.splat"));
		add(SoundRegistry.WORM_EGG_SAC_LIVING, definition().with(
			sound("worm_egg_sac_living_1"),
			sound("worm_egg_sac_living_2"),
			sound("worm_egg_sac_living_3")
		).subtitle("blsubtitles.entity.worm_egg_sac.living"));
		add(SoundRegistry.WORM_EGG_SAC_SQUISH, definition().with(
			sound("worm_egg_sac_squish")
		).subtitle("blsubtitles.entity.worm_egg_sac.squish"));
		add(SoundRegistry.SPLODESHROOM_WINDDOWN, definition().with(
			sound("splodeshroom_winddown")
		).subtitle("blsubtitles.entity.splodeshroom.winddown"));
		add(SoundRegistry.SPLODESHROOM_WINDUP, definition().with(
			sound("splodeshroom_windup")
		).subtitle("blsubtitles.entity.splodeshroom.windup"));
		add(SoundRegistry.SPLODESHROOM_POP, definition().with(
			sound("splodeshroom_pop")
		).subtitle("blsubtitles.entity.splodeshroom.pop"));
		add(SoundRegistry.PIT_OF_DECAY_LOOP, definition().with( //TODO move to music category?
			sound("boss/pit_of_decay_loop").stream()
		));
		add(SoundRegistry.EMBERLING_FLAMES, definition().with(
			sound("emberling_flames")
		).subtitle("blsubtitles.entity.emberling.flames"));
		add(SoundRegistry.EMBERLING_JUMP, definition().with(
			sound("emberling_jump")
		).subtitle("blsubtitles.entity.emberling.jump"));
		add(SoundRegistry.EMBERLING_HURT, definition().with(
			sound("emberling_hurt_1"),
			sound("emberling_hurt_2")
		).subtitle("blsubtitles.entity.emberling.hurt"));
		add(SoundRegistry.EMBERLING_LIVING, definition().with(
			sound("emberling_living_1"),
			sound("emberling_living_2"),
			sound("emberling_living_3"),
			sound("emberling_living_4"),
			sound("emberling_living_5")
		).subtitle("blsubtitles.entity.emberling.living"));
		add(SoundRegistry.EMBERLING_DEATH, definition().with(
			sound("emberling_death")
		).subtitle("blsubtitles.entity.emberling.death"));
		add(SoundRegistry.BARRISHEE_HURT, definition().with(
			sound("barrishee_hurt_1"),
			sound("barrishee_hurt_2")
		).subtitle("blsubtitles.entity.barrishee.hurt"));
		add(SoundRegistry.BARRISHEE_LIVING, definition().with(
			sound("barrishee_living")
		).subtitle("blsubtitles.entity.barrishee.living"));
		add(SoundRegistry.BARRISHEE_DEATH, definition().with(
			sound("barrishee_death")
		).subtitle("blsubtitles.entity.barrishee.death"));
		add(SoundRegistry.BARRISHEE_STEP, definition().with(
			sound("barrishee_step_1"),
			sound("barrishee_step_2"),
			sound("barrishee_step_3"),
			sound("barrishee_step_4")
		));
		add(SoundRegistry.BARRISHEE_SCREAM, definition().with(
			sound("barrishee_scream")
		).subtitle("blsubtitles.entity.barrishee.scream"));
		add(SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE, definition().with(
			sound("chiromaw_matriarch_barb_fire_1"),
			sound("chiromaw_matriarch_barb_fire_2"),
			sound("chiromaw_matriarch_barb_fire_3")
		).subtitle("blsubtitles.entity.chiromaw_matriarch.barbs"));
		add(SoundRegistry.CHIROMAW_MATRIARCH_BARB_HIT, definition().with(
			sound("chiromaw_matriarch_barb_hit_1"),
			sound("chiromaw_matriarch_barb_hit_2"),
			sound("chiromaw_matriarch_barb_hit_3")
		));
		add(SoundRegistry.CHIROMAW_MATRIARCH_LIVING, definition().with(
			sound("chiromaw_matriarch_living_1"),
			sound("chiromaw_matriarch_living_2"),
			sound("chiromaw_matriarch_living_3"),
			sound("chiromaw_matriarch_living_4")
		).subtitle("blsubtitles.entity.chiromaw_matriarch.living"));
		add(SoundRegistry.CHIROMAW_MATRIARCH_HURT, definition().with(
			sound("chiromaw_matriarch_hurt_1"),
			sound("chiromaw_matriarch_hurt_2")
		).subtitle("blsubtitles.entity.chiromaw_matriarch.hurt"));
		add(SoundRegistry.CHIROMAW_MATRIARCH_DEATH, definition().with(
			sound("chiromaw_matriarch_death")
		).subtitle("blsubtitles.entity.chiromaw_matriarch.death"));
		add(SoundRegistry.CHIROMAW_MATRIARCH_POOP, definition().with(
			sound("chiromaw_matriarch_poop_1"),
			sound("chiromaw_matriarch_poop_2"),
			sound("chiromaw_matriarch_poop_3")
		).subtitle("blsubtitles.entity.chiromaw_matriarch.poop"));
		add(SoundRegistry.CHIROMAW_MATRIARCH_SPLAT, definition().with(
			sound("chiromaw_matriarch_splat_1"),
			sound("chiromaw_matriarch_splat_2"),
			sound("chiromaw_matriarch_splat_3")
		));
		add(SoundRegistry.CHIROMAW_MATRIARCH_LAND, definition().with(
			sound("chiromaw_matriarch_land")
		));
		add(SoundRegistry.CHIROMAW_MATRIARCH_FLAP, definition().with(
			sound("chiromaw_matriarch_flap_1"),
			sound("chiromaw_matriarch_flap_2"),
			sound("chiromaw_matriarch_flap_3")
		));
		add(SoundRegistry.CHIROMAW_MATRIARCH_RELEASE, definition().with(
			sound("chiromaw_matriarch_release")
		).subtitle("blsubtitles.entity.chiromaw_matriarch.release"));
		add(SoundRegistry.CHIROMAW_MATRIARCH_GRAB, definition().with(
			sound("chiromaw_matriarch_grab")
		).subtitle("blsubtitles.entity.chiromaw_matriarch.grab"));
		add(SoundRegistry.CHIROMAW_MATRIARCH_ROAR, definition().with(
			sound("chiromaw_matriarch_roar")
		).subtitle("blsubtitles.entity.chiromaw_matriarch.roar"));
		add(SoundRegistry.CHIROMAW_HATCHLING_LIVING, definition().with(
			sound("chirobaby_living_1"),
			sound("chirobaby_living_2"),
			sound("chirobaby_living_3"),
			sound("chirobaby_living_4")
		).subtitle("blsubtitles.entity.chiromaw_hatchling.living"));
		add(SoundRegistry.CHIROMAW_HATCHLING_HUNGRY_SHORT, definition().with(
			sound("chirobaby_hungry_1"),
			sound("chirobaby_hungry_2"),
			sound("chirobaby_hungry_3"),
			sound("chirobaby_hungry_4"),
			sound("chirobaby_hungry_5")
		).subtitle("blsubtitles.entity.chiromaw_hatchling.hungry"));
		add(SoundRegistry.CHIROMAW_HATCHLING_HUNGRY_LONG, definition().with(
			sound("chirobaby_hungry_6"),
			sound("chirobaby_hungry_7"),
			sound("chirobaby_hungry_8"),
			sound("chirobaby_hungry_9"),
			sound("chirobaby_hungry_10")
		).subtitle("blsubtitles.entity.chiromaw_hatchling.hungry"));
		add(SoundRegistry.CHIROMAW_HATCHLING_EAT, definition().with(
			sound("chirobaby_eat_1"),
			sound("chirobaby_eat_2"),
			sound("chirobaby_eat_3")
		).subtitle("blsubtitles.entity.chiromaw_hatchling.eat"));
		add(SoundRegistry.CHIROMAW_HATCHLING_NO, definition().with(
			sound("chirobaby_no")
		).subtitle("blsubtitles.entity.chiromaw_hatchling.refuse"));
		add(SoundRegistry.CHIROMAW_HATCH, definition().with(
			sound("chirobaby_hatch_1"),
			sound("chirobaby_hatch_2"),
			sound("chirobaby_hatch_3")
		).subtitle("blsubtitles.entity.chiromaw_hatchling.hatch"));
		add(SoundRegistry.CHIROMAW_HATCHLING_TRANSFORM, definition().with(
			sound("chirobaby_evolve")
		).subtitle("blsubtitles.entity.chiromaw_hatchling.grow"));
		add(SoundRegistry.CHIROMAW_HATCHLING_INSIDE_EGG, definition().with(
			sound("chirobaby_egg_1"),
			sound("chirobaby_egg_2"),
			sound("chirobaby_egg_3"),
			sound("chirobaby_egg_4"),
			sound("chirobaby_egg_5")
		).subtitle("blsubtitles.entity.chiromaw_hatchling.inside_egg"));
		//TODO rest of sounds
	}

	protected static SoundDefinition.Sound sound(final String name) {
		return sound(TheBetweenlands.prefix(name));
	}

	protected static SoundDefinition.Sound vanillaSound(final String name) {
		return sound(ResourceLocation.withDefaultNamespace(name));
	}
}
