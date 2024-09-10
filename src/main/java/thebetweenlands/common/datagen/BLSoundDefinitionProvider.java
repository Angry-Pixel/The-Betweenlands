package thebetweenlands.common.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class BLSoundDefinitionProvider extends SoundDefinitionsProvider {

	public BLSoundDefinitionProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, TheBetweenlands.ID, helper);
	}

	@Override
	public void registerSounds() {
		// Music
		this.add(SoundRegistry.BL_MUSIC_MENU, definition().with(
			sound("menu/the_adventure_begins").stream(),
			sound("menu/a_foreboding_welcome").stream(),
			sound("menu/this_is_where_it_starts").stream()
		));
		this.add(SoundRegistry.BL_MUSIC_DIMENSION, definition().with(
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
		this.add(SoundRegistry.GREEBLING_MUSIC_1, definition().with(sound("music/greebling_music_1").preload()).subtitle("subtitles.thebetweenlands.entity.greebling_music"));
		this.add(SoundRegistry.GREEBLING_MUSIC_2, definition().with(sound("music/greebling_music_2").preload()).subtitle("subtitles.thebetweenlands.entity.greebling_music"));

		this.makeMusicDisc(SoundRegistry.BARRISHEE_THEME);
		this.makeMusicDisc(SoundRegistry.DREADFUL_PEAT_MUMMY_LOOP);
		this.makeMusicDisc(SoundRegistry.FORTRESS_BOSS_LOOP);
		this.makeMusicDisc(SoundRegistry.PIT_OF_DECAY_LOOP);

		// Records
		this.makeMusicDisc(SoundRegistry._16612);
		this.makeMusicDisc(SoundRegistry.ANCIENT);
		this.makeMusicDisc(SoundRegistry.ASTATOS);
		this.makeMusicDisc(SoundRegistry.BENEATH_A_GREEN_SKY);
		this.makeMusicDisc(SoundRegistry.BETWEEN_YOU_AND_ME);
		this.makeMusicDisc(SoundRegistry.CHRISTMAS_ON_THE_MARSH);
		this.makeMusicDisc(SoundRegistry.DJ_WIGHTS_MIXTAPE);
		this.makeMusicDisc(SoundRegistry.HAG_DANCE);
		this.makeMusicDisc(SoundRegistry.LONELY_FIRE);
		this.makeMusicDisc(SoundRegistry.ONWARD);
		this.makeMusicDisc(SoundRegistry.STUCK_IN_THE_MUD);
		this.makeMusicDisc(SoundRegistry.THE_EXPLORER);
		this.makeMusicDisc(SoundRegistry.WANDERING_WISPS);
		this.makeMusicDisc(SoundRegistry.WATERLOGGED);
		this.makeMusicDisc(SoundRegistry.DEEP_WATER_THEME);

		//Ambience
		this.generateNewSoundWithSubtitle(SoundRegistry.AMBIENT_BLOOD_SKY_ROAR, 1, "Distant roar");
		this.generateNewSound(SoundRegistry.AMBIENT_BLOOD_SKY, 1);
		this.generateNewSound(SoundRegistry.AMBIENT_CAVE, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.AMBIENT_CAVE_SPOOK, 3, "Eerie Noise");
		this.generateNewSound(SoundRegistry.AMBIENT_WIGHT_FORTRESS, 1);
		this.generateNewSound(SoundRegistry.AMBIENT_SPOOPY, 1);
		this.generateNewSound(SoundRegistry.AMBIENT_SWAMP, 1);
		this.generateNewSound(SoundRegistry.AMBIENT_SWAMP_DENSE_FOG, 1);
		this.generateNewSound(SoundRegistry.AMBIENT_DEEP_WATERS, 1);
		this.generateNewSound(SoundRegistry.AMBIENT_WATER, 1);
		this.generateNewSound(SoundRegistry.AMBIENT_FROSTY, 1);
		this.generateNewSound(SoundRegistry.AMBIENT_SNOWFALL, 1);
		this.generateNewSound(SoundRegistry.AMBIENT_SLUDGE_WORM_DUNGEON, 1);
		this.generateNewSound(SoundRegistry.AMBIENT_FLOATING_ISLAND, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.LIGHTNING, 3, "Lightning strikes");
		this.generateNewSoundWithSubtitle(SoundRegistry.THUNDER, 5, "Thunder rumbles");
		this.generateNewSound(SoundRegistry.RAIN_STRONG, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.RAIN_MEDIUM, 1, "Rain falls");
		this.generateNewSound(SoundRegistry.RAIN_WEAK, 1);
		this.generateNewSound(SoundRegistry.RAIN_DRIPPING, 1);
		this.generateNewSound(SoundRegistry.RAIN_MOUNT, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.RIFT_CREAK, 3, "Sky creaks");
		this.generateNewSoundWithSubtitle(SoundRegistry.RIFT_OPEN, 1, "Rift opens");

		this.generateNewSoundWithSubtitle(SoundRegistry.ANGLER_ATTACK, 2, "Angler bites");
		this.generateNewSoundWithSubtitle(SoundRegistry.ASHSPRITE_DEATH, 1, "Ash Sprite dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.ASHSPRITE_HURT, 2, "Ash Sprite hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.ASHSPRITE_LIVING, 3, "Ash Sprite whispers");
		this.generateNewSoundWithSubtitle(SoundRegistry.BARRISHEE_DEATH, 1, "Barrishee dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.BARRISHEE_HURT, 2, "Barrishee hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.BARRISHEE_LIVING, 1, "Barrishee clunks");
		this.generateNewSoundWithSubtitle(SoundRegistry.BARRISHEE_SCREAM, 1, "Barrishee shrieks");
		this.makeNewStepSound(SoundRegistry.BARRISHEE_STEP, "entity/barrishee/step", 4);
		this.generateNewSoundWithSubtitle(SoundRegistry.BOULDER_SPRITE_DEATH, 1, "Boulder Sprite dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.BOULDER_SPRITE_HURT, 2, "Boulder Sprite hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.BOULDER_SPRITE_LIVING, 3, "Boulder Sprite mutters");
		this.generateNewSoundWithSubtitle(SoundRegistry.BOULDER_SPRITE_ROLL, 1, "Boulder Sprite rolls");
		this.generateNewSoundWithSubtitle(SoundRegistry.BUBBLER_LAND, 3, "Bubble lands");
		this.generateNewSoundWithSubtitle(SoundRegistry.BUBBLER_POP, 3, "Bubble pops");
		this.generateNewSoundWithSubtitle(SoundRegistry.BUBBLER_SPIT, 3, "Bubbler Crab blows a bubble");
		this.generateNewSoundWithSubtitle(SoundRegistry.FLYING_FIEND_DEATH, 1, "Chiromaw dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.FLYING_FIEND_HURT, 2, "Chiromaw hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.FLYING_FIEND_LIVING, 4, "Chiromaw chatters");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_HATCHLING_EAT, 3, "Chiromaw Hatchling eats");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_HATCHLING_INSIDE_EGG, 5, "Chiromaw Hatchling egg stirs");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_HATCH, 3, "Chiromaw Hatchling hatches");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_HATCHLING_TRANSFORM, 1, "Chiromaw Hatchling grows");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_HATCHLING_HUNGRY_LONG, 5, "Chiromaw Hatchling begs");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_HATCHLING_HUNGRY_SHORT, 5, "Chiromaw Hatchling begs");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_HATCHLING_LIVING, 4, "Chiromaw Hatchling cheeps");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_HATCHLING_NO, 1, "Chiromaw Hatchling refuses");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE, 3, "Chiromaw Matriarch shoots barbs");
		this.generateNewSound(SoundRegistry.CHIROMAW_MATRIARCH_BARB_HIT, 3);
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_MATRIARCH_DEATH, 1, "Chiromaw Matriarch dies");
		this.generateNewSound(SoundRegistry.CHIROMAW_MATRIARCH_FLAP, 3);
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_MATRIARCH_GRAB, 1, "Chiromaw Matriarch grabs");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_MATRIARCH_HURT, 2, "Chiromaw Matriarch hurts");
		this.generateNewSound(SoundRegistry.CHIROMAW_MATRIARCH_LAND, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_MATRIARCH_LIVING, 4, "Chiromaw Matriarch chatters");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_MATRIARCH_POOP, 3, "Chiromaw Matriarch poops");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_MATRIARCH_RELEASE, 1, "Chiromaw Matriarch releases");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROMAW_MATRIARCH_ROAR, 1, "Chiromaw Matriarch roars");
		this.generateNewSound(SoundRegistry.CHIROMAW_MATRIARCH_SPLAT, 3);
		this.generateNewSoundWithSubtitle(SoundRegistry.CRAB_SNIP, 1, "Crab snips");
		this.generateNewSoundWithSubtitle(SoundRegistry.CRYPT_CRAWLER_DEATH, 1, "Crypt Crawler dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.CRYPT_CRAWLER_DIG, 1, "Crypt Crawler digs");
		this.generateNewSoundWithSubtitle(SoundRegistry.CRYPT_CRAWLER_HURT, 2, "Crypt Crawler hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.CRYPT_CRAWLER_LIVING, 3, "Crypt Crawler snarls");
		this.generateNewSoundWithSubtitle(SoundRegistry.DARK_DRUID_DEATH, 1, "Druid dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.DARK_DRUID_HURT, 1, "Druid hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.DARK_DRUID_LIVING, 2, "Druid murmurs");
		this.generateNewSoundWithSubtitle(SoundRegistry.DRUID_CHANT, 1, "Mysterious chanting");
		this.generateNewSoundWithSubtitle(SoundRegistry.DRUID_TELEPORT, 1, "Druid teleports");
		this.generateNewSoundWithSubtitle(SoundRegistry.DRAETON_ANCHOR, 1, "Anchor stops");
		this.generateNewSound(SoundRegistry.DRAETON_BURNER, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.DRAETON_DAMAGE, 4, "Draeton takes damage");
		this.generateNewSound(SoundRegistry.DRAETON_LEAK_LOOP, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.DRAETON_LEAK_START, 1, "Draeton starts leaking");
		this.generateNewSoundWithSubtitle(SoundRegistry.DRAETON_PLUG, 1, "Leak is plugged");
		this.generateNewSoundWithSubtitle(SoundRegistry.DRAETON_POP, 1, "Draeton pops");
		this.generateNewSoundWithSubtitle(SoundRegistry.DRAETON_PULLEY, 1, "Pulley moves anchor");
		this.generateNewSoundWithSubtitle(SoundRegistry.DRAETON_TURN, 5, "Draeton turns");
		this.generateNewSoundWithSubtitle(SoundRegistry.DRAGONFLY, 1, "Dragonfly flutters");
		this.generateNewSoundWithSubtitle(SoundRegistry.DREADFUL_PEAT_MUMMY_BITE, 1, "Dreadful Peat Mummy bites");
		this.generateNewSoundWithSubtitle(SoundRegistry.DREADFUL_PEAT_MUMMY_DEATH, 1, "Dreadful Peat Mummy dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.DREADFUL_PEAT_MUMMY_EMERGE, 1, "Dreadful Peat Mummy emerges");
		this.generateNewSoundWithSubtitle(SoundRegistry.DREADFUL_PEAT_MUMMY_HURT, 5, "Dreadful Peat Mummy hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.DREADFUL_PEAT_MUMMY_LICK, 1, "Dreadful Peat Mummy licks");
		this.generateNewSoundWithSubtitle(SoundRegistry.DREADFUL_PEAT_MUMMY_LIVING, 5, "Dreadful Peat Mummy snarls");
		this.generateNewSoundWithSubtitle(SoundRegistry.DREADFUL_PEAT_MUMMY_RETCH, 1, "Dreadful Peat Mummy retches");
		this.generateNewSoundWithSubtitle(SoundRegistry.DREADFUL_PEAT_MUMMY_SCREAM, 1, "Dreadful Peat Mummy screams");
		this.generateNewSoundWithSubtitle(SoundRegistry.DREADFUL_PEAT_MUMMY_SWIPE, 1, "Dreadful Peat Mummy swipes");
		this.generateNewSoundWithSubtitle(SoundRegistry.EMBERLING_DEATH, 1, "Emberling dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.EMBERLING_FLAMES, 1, "Emberling shoots flames");
		this.generateNewSoundWithSubtitle(SoundRegistry.EMBERLING_HURT, 2, "Emberling hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.EMBERLING_JUMP, 1, "Emberling jumps");
		this.generateNewSoundWithSubtitle(SoundRegistry.EMBERLING_LIVING, 5, "Emberling clicks");
		this.generateNewSound(SoundRegistry.ENERGY_SWORD_ORB, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.ENERGY_SWORD, 1, "Shockwave Sword appears");
		this.generateNewSoundWithSubtitle(SoundRegistry.FISH_DEATH, 1, "Fish dies");
		this.generateNewSoundMC(SoundRegistry.FISH_FLOP, "mob/guardian/flop", 4, "Fish flops");
		this.generateNewSoundWithSubtitle(SoundRegistry.FISH_HURT, 3, "Fish hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.FROG_DEATH, 1, "Frog dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.FROG_HURT, 3, "Frog hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.FROG_LIVING, 4, "Frog ribbits");
		this.generateNewSoundWithSubtitle(SoundRegistry.GECKO_DEATH, 1, "Gecko dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.GECKO_HIDE, 1, "Leaves rustle");
		this.generateNewSoundWithSubtitle(SoundRegistry.GECKO_HURT, 1, "Gecko hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.GECKO_LIVING, 4, "Gecko chirps");
		this.generateNewSoundWithSubtitle(SoundRegistry.GIANT_TOAD_DEATH, 1, "Giant Toad dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.GIANT_TOAD_HURT, 3, "Giant Toad hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.GIANT_TOAD_LIVING, 4, "Giant Toad croaks");
		this.generateNewSoundWithSubtitle(SoundRegistry.GREEBLING_CORACLE_SINK, 1, "Coracle sinks");
		this.generateNewSoundWithSubtitle(SoundRegistry.GREEBLING_FALL, 1, "Greebling falls");
		this.generateNewSoundWithSubtitle(SoundRegistry.GREEBLING_GIGGLE, 1, "Greebling giggles");
		this.generateNewSoundWithSubtitle(SoundRegistry.GREEBLING_HEY, 1, "Greebling whistles");
		this.generateNewSoundWithSubtitle(SoundRegistry.GREEBLING_HUM, 1, "Greebling hums");
		this.generateNewSoundWithSubtitle(SoundRegistry.GREEBLING_VANISH, 1, "Greebling vanishes");
		this.generateNewSound(SoundRegistry.JELLYFISH_DEATH, "entity/jellyfish/hurt", 4, "Jellyfish dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.JELLYFISH_HURT, 4, "Jellyfish hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.JELLYFISH_SWIM, 5, "Jellyfish swims");
		this.generateNewSoundWithSubtitle(SoundRegistry.JELLYFISH_ZAP, 3, "Jellyfish stings");
		this.generateNewSound(SoundRegistry.LEECH_DEATH, "entity/snail/death", 1, "Leech dies");
		this.generateNewSound(SoundRegistry.LEECH_HURT, "entity/snail/hurt", 1, "Leech hurts");
		this.generateNewSound(SoundRegistry.LEECH_LIVING, "entity/snail/idle", 2, "Leech squelches");
		this.generateNewSoundMC(SoundRegistry.LIVING_ROOT_DEATH, "dig/wood", 4, "Living Root dies");
		this.generateNewSoundMC(SoundRegistry.LIVING_ROOT_HURT, "step/wood", 6, "Living Root hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.WALL_LIVING_ROOT_EMERGE, 1, "Living Root emerges");
		this.generateNewSoundWithSubtitle(SoundRegistry.LURKER_DEATH, 1, "Lurker dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.LURKER_HURT, 4, "Lurker hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.LURKER_LIVING, 5, "Lurker growls");
		this.generateNewSoundWithSubtitle(SoundRegistry.WALL_SLAM, 1, "Wall slams");
		this.generateNewSoundWithSubtitle(SoundRegistry.WALL_SLIDE, 1, "Wall slides");
		this.generateNewSoundWithSubtitle(SoundRegistry.OLM_DEATH, 1, "Olm dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.OLM_HURT, 3, "Olm hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.PEAT_MUMMY_CHARGE, 1, "Peat Mummy charges");
		this.generateNewSoundWithSubtitle(SoundRegistry.PEAT_MUMMY_DEATH, 1, "Peat Mummy dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.PEAT_MUMMY_EMERGE, 1, "Peat Mummy emerges");
		this.generateNewSoundWithSubtitle(SoundRegistry.PEAT_MUMMY_HURT, 5, "Peat Mummy hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.PEAT_MUMMY_LIVING, 5, "Peat Mummy snarls");
		this.generateNewSoundWithSubtitle(SoundRegistry.FORTRESS_BOSS_DEATH, 1, "Primordial Malevolence dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.FORTRESS_BOSS_HURT, 4, "Primordial Malevolence hurts");
		this.generateNewSound(SoundRegistry.FORTRESS_BOSS_LIVING, 3);
		this.generateNewSoundWithSubtitle(SoundRegistry.FORTRESS_BOSS_NOPE, 1, "Primordial Malevolence is protected");
		this.generateNewSoundWithSubtitle(SoundRegistry.FORTRESS_BOSS_SHIELD_DOWN, 1, "Primordial Malevolence loses a shield");
		this.generateNewSoundWithSubtitle(SoundRegistry.FORTRESS_BOSS_SUMMON_PROJECTILES, 1, "Primordial Malevolence summons projectiles");
		this.generateNewSoundWithSubtitle(SoundRegistry.FORTRESS_BOSS_TELEPORT, 1, "Primordial Malevolence teleports");
		this.generateNewSoundWithSubtitle(SoundRegistry.PYRAD_DEATH, 1, "Pyrad dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.PYRAD_HURT, 3, "Pyrad hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.PYRAD_LIVING, 3, "Pyrad breathes");
		this.generateNewSoundMC(SoundRegistry.PYRAD_SHOOT, "mob/ghast/fireball4", 1, "Pyrad shoots");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROCK_SNOT_ATTACH, 1, "Rocksnot grabs something");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROCK_SNOT_ATTACK, 1, "Rocksnot shoots snot tendril");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROCK_SNOT_DIGEST, 1, "Rocksnot grumbles");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROCK_SNOT_EAT, 5, "Rocksnot chomps");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROCK_SNOT_SPIT, 1, "Rocksnot burps");
		this.generateNewSound(SoundRegistry.ROOT_SPIKE_PARTICLE_HIT, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.ROOT_SPRITE_DEATH, 1, "Root Sprite dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROOT_SPRITE_HURT, 3, "Root Sprite hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROOT_SPRITE_LIVING, 3, "Root Sprite whines");
		this.generateNewSoundWithSubtitle(SoundRegistry.GAS_CLOUD_DEATH, 1, "Shallowbreath dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.GAS_CLOUD_HURT, 2, "Shallowbreath hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.GAS_CLOUD_LIVING, 2, "Shallowbreath whispers");
		this.generateNewSoundWithSubtitle(SoundRegistry.SHAMBLER_DEATH, 1, "Shambler dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.SHAMBLER_HURT, 2, "Shambler hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.SHAMBLER_LICK, 1, "Shambler licks");
		this.generateNewSoundWithSubtitle(SoundRegistry.SHAMBLER_LIVING, 4, "Shambler grunts");
		this.generateNewSoundMC(SoundRegistry.SLUDGE_ATTACK, "mob/slime/attack", 2, "Sludge attacks");
		this.generateNewSoundMC(SoundRegistry.SLUDGE_JUMP, "mob/slime/big", 4, "Sludge jumps");
		this.generateNewSoundWithSubtitle(SoundRegistry.SLUDGE_MENACE_ATTACK, 5, "Sludge Menace attacks");
		this.generateNewSoundWithSubtitle(SoundRegistry.SLUDGE_MENACE_DEATH, 1, "Sludge Menace dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.SLUDGE_MENACE_HURT, 3, "Sludge Menace hurts");
		this.generateNewSound(SoundRegistry.SLUDGE_MENACE_LIVING, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.SLUDGE_MENACE_SPAWN, 1, "Sludge Menace emerges");
		this.generateNewSoundWithSubtitle(SoundRegistry.SLUDGE_MENACE_SPIT, 3, "Sludge Menace spits");
		this.generateNewSoundWithSubtitle(SoundRegistry.SLUDGE_TURRET_DEATH, 1, "Sludge Turret dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.SLUDGE_TURRET_HURT, 2, "Sludge Turret hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.SLUDGE_TURRET_LIVING, 4, "Sludge Turret whirrs");
		this.generateNewSoundWithSubtitle(SoundRegistry.SNAIL_DEATH, 1, "Snail dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.SNAIL_HURT, 1, "Snail hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.SNAIL_LIVING, 2, "Snail squelches");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPIRIT_TREE_FACE_LARGE_DEATH, 1, "Spirit Tree dies");
		this.generateNewSound(SoundRegistry.SPIRIT_TREE_FACE_LARGE_EMERGE, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.SPIRIT_TREE_FACE_LARGE_LIVING, 4, "Spirit Tree creaks");
		this.generateNewSound(SoundRegistry.SPIRIT_TREE_FACE_LARGE_SPIT, "entity/spirit_tree/face_spit", 1, "Spirit Tree spits");
		this.generateNewSound(SoundRegistry.SPIRIT_TREE_FACE_SMALL_EMERGE, 1);
		this.generateNewSound(SoundRegistry.SPIRIT_TREE_FACE_SMALL_LIVING, "entity/spirit_tree/large_face_idle", 4, "Spirit Tree creaks");
		this.generateNewSound(SoundRegistry.SPIRIT_TREE_FACE_SMALL_SPIT, "entity/spirit_tree/face_spit", 1, "Spirit Tree spits");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPIRIT_TREE_FACE_SPIT_ROOT_SPIKES, 1, "Spirit Tree spits spikes");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPIRIT_TREE_FACE_SUCK, 1, "Spirit Tree inhales");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPIRIT_TREE_SPIKE_TRAP_EMERGE, 1, "Spike trap emerges");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPIRIT_TREE_SPIKES, 1, "Spikes shoot out");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPLODESHROOM_POP, 1, "Splodeshroom pops");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPLODESHROOM_WINDDOWN, 1, "Splodeshroom winds down");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPLODESHROOM_WINDUP, 1, "Splodeshroom winds up");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPORELING_DEATH, 1, "Sporeling dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPORELING_HURT, 1, "Sporeling hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPORELING_LIVING, 1, "Sporeling mutters");
		this.generateNewSoundWithSubtitle(SoundRegistry.STALKER_DEATH, 1, "Stalker dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.STALKER_HURT, 3, "Stalker hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.STALKER_LIVING, 3, "Stalker growls");
		this.generateNewSoundWithSubtitle(SoundRegistry.STALKER_SCREAM, 1, "Stalker screeches");
		this.generateNewSoundWithSubtitle(SoundRegistry.STALKER_SCREECH, 1, "Stalker calls for help");
		this.generateNewSoundWithSubtitle(SoundRegistry.STALKER_STEP, 10, "Something is nearby");
		this.generateNewSoundWithSubtitle(SoundRegistry.SWAMP_HAG_DEATH, 1, "Swamp Hag dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.SWAMP_HAG_HURT, 3, "Swamp Hag hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.SWAMP_HAG_LIVING_1, 1, "Swamp Hag groans");
		this.generateSoundWithExistingSubtitle(SoundRegistry.SWAMP_HAG_LIVING_2, "entity/swamp_hag/idle2", 1, "subtitles.thebetweenlands.entity.swamp_hag.idle1");
		this.generateSoundWithExistingSubtitle(SoundRegistry.SWAMP_HAG_LIVING_3, "entity/swamp_hag/idle3", 1, "subtitles.thebetweenlands.entity.swamp_hag.idle1");
		this.generateSoundWithExistingSubtitle(SoundRegistry.SWAMP_HAG_LIVING_4, "entity/swamp_hag/idle4", 1, "subtitles.thebetweenlands.entity.swamp_hag.idle1");
		this.generateNewSoundWithSubtitle(SoundRegistry.SWARM_ATTACK, 1, "Infestation munches");
		this.generateNewSoundWithSubtitle(SoundRegistry.SWARM_IDLE, 1, "Infestation skitters");
		this.generateNewSound(SoundRegistry.WORM_THROW, 3);
		this.generateNewSound(SoundRegistry.TARMINION_DEATH, "entity/snail/death", 1, "Tarminion dies");
		this.generateNewSound(SoundRegistry.TARMINION_HURT, "entity/snail/hurt", 1, "Tarminion hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.TAR_BEAST_DEATH, 1, "Tar Beast dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.TAR_BEAST_HURT, 2, "Tar Beast hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.TAR_BEAST_LIVING, 3, "Tar Beast gurgles");
		this.makeNewStepSound(SoundRegistry.TAR_BEAST_STEP, "entity/tar_beast/step", 3);
		this.generateNewSoundWithSubtitle(SoundRegistry.TAR_BEAST_SUCK, 1, "Tar Beast inhales");
		this.generateNewSoundWithSubtitle(SoundRegistry.TERMITE_LIVING, 2, "Termite chitters");
		this.generateNewSoundWithSubtitle(SoundRegistry.URCHIN_SHOOT, 3, "Urchin attacks");
		this.generateNewSoundWithSubtitle(SoundRegistry.WALL_LAMPREY_ATTACK, 3, "Wall Lamprey chomps");
		this.generateNewSoundWithSubtitle(SoundRegistry.WALL_LAMPREY_SUCK, 1, "Wall Lamprey sucks");
		this.generateNewSoundWithSubtitle(SoundRegistry.WIGHT_ATTACK, 5, "Wight wails");
		this.generateNewSoundWithSubtitle(SoundRegistry.WIGHT_DEATH, 1, "Wight dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.WIGHT_HURT, 2, "Wight hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.WIGHT_MOAN, 4, "Wight moans");
		this.generateNewSoundWithSubtitle(SoundRegistry.WORM_DEATH, 1, "Worm dies");
		this.generateNewSoundWithSubtitle(SoundRegistry.WORM_EGG_SAC_LIVING, 3, "Worm Egg Sac writhes");
		this.generateNewSoundWithSubtitle(SoundRegistry.WORM_EGG_SAC_SQUISH, 1, "Worm Egg Sac squishes");
		this.generateNewSoundWithSubtitle(SoundRegistry.WORM_EMERGE, 1, "Worm emerges");
		this.generateNewSoundWithSubtitle(SoundRegistry.WORM_HURT, 2, "Worm hurts");
		this.generateNewSoundWithSubtitle(SoundRegistry.WORM_LIVING, 3, "Worm squirms");
		this.generateNewSound(SoundRegistry.WORM_PLOP, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.WORM_SPLAT, 1, "Worm splats");

		this.generateNewSoundWithSubtitle(SoundRegistry.ANIMATOR, 1, "Animator hums");
		this.generateNewSoundWithSubtitle(SoundRegistry.BEAM_ACTIVATE, 1, "Beam activates");
		this.generateNewSoundWithSubtitle(SoundRegistry.BEAM_SWITCH, 1, "Beam switches");
		this.generateNewSoundWithSubtitle(SoundRegistry.BRAZIER_LIGHT, 1, "Brazier lights");
		this.generateNewSound(SoundRegistry.CHIMES_AURORAS, 1);
		this.generateNewSound(SoundRegistry.CHIMES_BLOOD_SKY, 1);
		this.generateNewSound(SoundRegistry.CHIMES_DENSE_FOG, 1);
		this.generateNewSound(SoundRegistry.CHIMES_HEAVY_RAIN, 1);
		this.generateNewSound(SoundRegistry.CHIMES_RIFT, 1);
		this.generateNewSound(SoundRegistry.CHIMES_SNOWFALL, 1);
		this.generateNewSound(SoundRegistry.CHIMES_THUNDERSTORM, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIMES_WIND, 3, "Wind blows the chimes");
		this.generateNewSoundWithSubtitle(SoundRegistry.CRUMBLE, 1, "Block crumbles");
		this.generateNewSoundWithSubtitle(SoundRegistry.FISHING_TACKLE_BOX_CLOSE, 1, "Fishing Tackle Box closes");
		this.generateNewSoundWithSubtitle(SoundRegistry.FISHING_TACKLE_BOX_OPEN, 1, "Fishing Tackle Box opens");
		this.generateNewSoundWithSubtitle(SoundRegistry.FISH_CHOP, 1, "Trimming Table chops");
		this.generateNewSoundWithSubtitle(SoundRegistry.GRIND, 1, "Mortar and Pestle grind");
		this.generateNewSoundWithSubtitle(SoundRegistry.GRUB_HUB_MIST, 1, "Mist sprays");
		this.generateNewSoundWithSubtitle(SoundRegistry.GRUB_HUB_SUCK, 1, "Grub Hub sucks");
		this.generateNewSoundWithSubtitle(SoundRegistry.INFUSER_FINISHED, 1, "Infuser finishes");
		this.generateNewSoundWithSubtitle(SoundRegistry.ITEM_CAGE_BREAK, 1, "Cage breaks");
		this.generateNewSoundWithSubtitle(SoundRegistry.LYESTONE_FIZZ, 1, "Lyestone fizzes");
		this.generateNewSoundWithSubtitle(SoundRegistry.MUD_DOOR_1, 1, "Door unlocks");
		this.generateNewSound(SoundRegistry.MUD_DOOR_2, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.MUD_DOOR_LOCK, 1, "Puzzle changes");
		this.generateNewSoundWithSubtitle(SoundRegistry.MUD_DOOR_TRAP, 1, "Door falls down");
		this.generateNewSound(SoundRegistry.PIT_FALL, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.PLUG_HIT, 1, "Loud bashing");
		this.generateNewSoundWithSubtitle(SoundRegistry.PLUG_LOCK, 1, "Plug falls");
		this.generateNewSoundWithSubtitle(SoundRegistry.POOP_JET, 1, "Sludge shoots out");
		this.generateNewSoundWithSubtitle(SoundRegistry.PORTAL, 1, "Portal wooshes");
		this.generateNewSoundWithSubtitle(SoundRegistry.PORTAL_ACTIVATE, 1, "Portal activates");
		this.generateNewSoundWithSubtitle(SoundRegistry.PORTAL_TRAVEL, 1, "Portal noise fades");
		this.generateNewSoundWithSubtitle(SoundRegistry.PORTAL_TRIGGER, 1, "Portal noise intensifies");
		this.generateNewSoundWithSubtitle(SoundRegistry.POSSESSED_SCREAM, 1, "Ghost screams");
		this.generateNewSoundWithSubtitle(SoundRegistry.PUFF_SHROOM, 1, "Puffshroom puffs");
		this.generateNewSoundWithSubtitle(SoundRegistry.PURIFIER, 1, "Purifier bubbles");
		this.generateNewSound(SoundRegistry.SIMULACRUM_BREAK, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.SPIKE, 1, "Spikes activate");

		this.generateNewSoundWithSubtitle(SoundRegistry.BL_FISHING_CAST, 1, "Fishing line cast");
		this.generateNewSoundWithSubtitle(SoundRegistry.BL_FISHING_REEL, 1, "Fishing line reeled");
		this.generateNewSoundWithSubtitle(SoundRegistry.BL_FISHING_ROD_CREAK, 3, "Fishing Rod creaks");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHIROBARB_ERUPTER, 1, "Chirobarb Erupter shoots barbs");
		this.generateNewSoundWithSubtitle(SoundRegistry.GEM_SINGER, 1, "Gem Singer rings out");
		this.generateNewSoundWithSubtitle(SoundRegistry.GEM_SINGER_ECHO, 1, "Middle Gem echoes back");
		this.generateNewSound(SoundRegistry.LONG_SLICE, 3);
		this.generateNewSound(SoundRegistry.LONG_SWING, 3);
		this.generateNewSoundWithSubtitle(SoundRegistry.MIST_STAFF_CAST, 1, "Mist Bridge appears");
		this.generateNewSoundWithSubtitle(SoundRegistry.MIST_STAFF_VANISH, 1, "Mist Bridge disappears");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROPE_GRAB, 1, "Grapple attaches");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROPE_PULL, 1, "Rope climbed");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROPE_SWING, 1, "Rope swings");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROPE_THROW, 1, "Grapple is thrown");
		this.generateNewSoundWithSubtitle(SoundRegistry.ROWBOAT_ROW_PORT, 1, "Oars splash");
		this.generateSoundWithExistingSubtitle(SoundRegistry.ROWBOAT_ROW_STARBOARD, "item/rowboat_row_starboard", 1, "subtitles.thebetweenlands.item.rowboat_row_port");
		this.generateSoundWithExistingSubtitle(SoundRegistry.ROWBOAT_ROW_START_PORT, "item/rowboat_row_start_port", 1, "subtitles.thebetweenlands.item.rowboat_row_port");
		this.generateSoundWithExistingSubtitle(SoundRegistry.ROWBOAT_ROW_START_STARBOARD, "item/rowboat_row_start_starboard", 1, "subtitles.thebetweenlands.item.rowboat_row_port");
		this.generateNewSoundWithSubtitle(SoundRegistry.SHOCKWAVE_SWORD, 1, "Sword emits shockwave");
		this.generateNewSoundWithSubtitle(SoundRegistry.SILKY_PEBBLE_THROW, 1, "Silky Pebble muffled mumbles");
		this.generateNewSoundWithSubtitle(SoundRegistry.SLINGSHOT_CHARGE, 1, "Slingshot charges");
		this.generateNewSoundWithSubtitle(SoundRegistry.SLINGSHOT_HIT, 3, "Slingshot hits");
		this.generateNewSoundWithSubtitle(SoundRegistry.SLINGSHOT_SHOOT, 1, "Slingshot fires");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPEAR_LAND, 1, "Fishing Spear lands nearby");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPEAR_RETURN_1, 1, "Fishing Spear returns");
		this.generateSoundWithExistingSubtitle(SoundRegistry.SPEAR_RETURN_2, "item/spear_return_2", 1, "subtitles.thebetweenlands.item.spear_return_1");
		this.generateNewSoundWithSubtitle(SoundRegistry.SPEAR_THROW, 1, "Fishing Spear thrown");
		this.generateNewSoundWithSubtitle(SoundRegistry.VOODOO_DOLL, 1, "Voodoo Doll activates");

		this.generateNewSound(SoundRegistry.ANADIA_LOST, 1);
		this.generateNewSound(SoundRegistry.ANADIA_TREASURE_COLLECTED, 1);
		this.generateNewSound(SoundRegistry.ANADIA_WON, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.CHAIN, 1, "Chain moves");
		this.generateNewSoundWithSubtitle(SoundRegistry.CHAIN_LONG, 1, "Chain moves");
		this.generateNewSoundWithSubtitle(SoundRegistry.CRUNCH, 3, "Critter crunches");
		this.generateNewSound(SoundRegistry.DAMAGE_REDUCTION, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.FIG, 1, "Ominous sound");
		this.generateNewSound(SoundRegistry.FISHING_CRAB, 1);
		this.generateNewSoundWithSubtitle(SoundRegistry.FORTRESS_TELEPORT, 1, "Teleport upwards");
		this.generateNewSoundWithSubtitle(SoundRegistry.GEARS, 1, "Gears turn");
		this.generateNewSound(SoundRegistry.IGNITE, 1);
		this.generateNewSoundMC(SoundRegistry.PEBBLE_HISS, "random/fuse", 1, "Angry Pebble hisses");
		this.generateNewSoundWithSubtitle(SoundRegistry.REJECTED, 1, "Evil Laughter");
		this.generateNewSound(SoundRegistry.RESURRECTION, 1);
		this.generateNewSound(SoundRegistry.RING_OF_DISPERSION_TELEPORT, "entity/primordial_malevolence/teleport", 1, null);
		this.generateNewSoundWithSubtitle(SoundRegistry.ROOF_COLLAPSE, 1, "Block collapses");
		this.generateNewSoundWithSubtitle(SoundRegistry.SORRY, 1, "Angry Pebble screams");
		this.generateNewSoundWithSubtitle(SoundRegistry.SQUISH, 1, "Critter squishes");
		this.generateNewSoundWithSubtitle(SoundRegistry.ZAP, 8, "Electrical Zap");
	}

	protected static SoundDefinition.Sound sound(final String name) {
		return sound(TheBetweenlands.prefix(name));
	}

	public void generateNewSoundWithSubtitle(DeferredHolder<SoundEvent, SoundEvent> event, int numberOfSounds, String subtitle) {
		this.generateNewSound(event, event.getId().getPath().replace('.', '/'), numberOfSounds, subtitle);
	}

	public void generateNewSound(DeferredHolder<SoundEvent, SoundEvent> event, int numberOfSounds) {
		this.generateNewSound(event, event.getId().getPath().replace('.', '/'), numberOfSounds, null);
	}

	public void generateNewSound(DeferredHolder<SoundEvent, SoundEvent> event, String baseSoundDirectory, int numberOfSounds, @Nullable String subtitle) {
		SoundDefinition definition = SoundDefinition.definition();
		if (subtitle != null) {
			this.createSubtitleAndLangEntry(event, definition, subtitle);
		}
		for (int i = 1; i <= numberOfSounds; i++) {
			definition.with(SoundDefinition.Sound.sound(TheBetweenlands.prefix(baseSoundDirectory + (numberOfSounds > 1 ? i : "")), SoundDefinition.SoundType.SOUND));
		}
		this.add(event, definition);
	}

	public void generateNewSoundMC(DeferredHolder<SoundEvent, SoundEvent> event, String baseSoundDirectory, int numberOfSounds, @Nullable String subtitle) {
		SoundDefinition definition = SoundDefinition.definition();
		if (subtitle != null) {
			this.createSubtitleAndLangEntry(event, definition, subtitle);
		}
		for (int i = 1; i <= numberOfSounds; i++) {
			definition.with(SoundDefinition.Sound.sound(ResourceLocation.withDefaultNamespace(baseSoundDirectory + (numberOfSounds > 1 ? i : "")), SoundDefinition.SoundType.SOUND));
		}
		this.add(event, definition);
	}

	public void generateSoundWithExistingSubtitle(DeferredHolder<SoundEvent, SoundEvent> event, String baseSoundDirectory, int numberOfSounds, String subtitleKey) {
		SoundDefinition definition = SoundDefinition.definition().subtitle(subtitleKey);
		for (int i = 1; i <= numberOfSounds; i++) {
			definition.with(SoundDefinition.Sound.sound(TheBetweenlands.prefix(baseSoundDirectory + (numberOfSounds > 1 ? i : "")), SoundDefinition.SoundType.SOUND));
		}
		this.add(event, definition);
	}

	public void makeNewStepSound(DeferredHolder<SoundEvent, SoundEvent> event, String baseSoundDirectory, int numberOfSounds) {
		SoundDefinition definition = SoundDefinition.definition();
		for (int i = 1; i <= numberOfSounds; i++) {
			definition.with(SoundDefinition.Sound.sound(TheBetweenlands.prefix(baseSoundDirectory + (numberOfSounds > 1 ? i : "")), SoundDefinition.SoundType.SOUND));
		}
		this.add(event, definition.subtitle("subtitles.block.generic.footsteps"));
	}

	public void makeMusicDisc(DeferredHolder<SoundEvent, SoundEvent> event) {
		this.add(event, SoundDefinition.definition()
			.with(SoundDefinition.Sound.sound(TheBetweenlands.prefix(event.getId().getPath().replace('.', '/')), SoundDefinition.SoundType.SOUND).volume(2.0F).stream()));
	}

	private void createSubtitleAndLangEntry(DeferredHolder<SoundEvent, SoundEvent> event, SoundDefinition definition, String subtitle) {
		String[] splitSoundName = event.getId().getPath().split("\\.");
		String subtitleKey = "subtitles.thebetweenlands." + splitSoundName[0] + "." + splitSoundName[1];
		if (splitSoundName.length > 2) subtitleKey += "." + splitSoundName[2];
		definition.subtitle(subtitleKey);
		BLLanguageProvider.SUBTITLE_GENERATOR.put(subtitleKey, subtitle);
	}
}
