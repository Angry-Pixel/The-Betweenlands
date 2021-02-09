package thebetweenlands.common.registries;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.EntityAngryPebble;
import thebetweenlands.common.entity.EntityAnimalBurrow;
import thebetweenlands.common.entity.EntityBLLightningBolt;
import thebetweenlands.common.entity.EntityCCGroundSpawner;
import thebetweenlands.common.entity.EntityDecayPitTarget;
import thebetweenlands.common.entity.EntityFalseXPOrb;
import thebetweenlands.common.entity.EntityFishBait;
import thebetweenlands.common.entity.EntityFishingTackleBoxSeat;
import thebetweenlands.common.entity.EntityGalleryFrame;
import thebetweenlands.common.entity.EntityGrapplingHookNode;
import thebetweenlands.common.entity.EntityGreeblingCorpse;
import thebetweenlands.common.entity.EntityLurkerSkinRaft;
import thebetweenlands.common.entity.EntityMovingWall;
import thebetweenlands.common.entity.EntityResurrection;
import thebetweenlands.common.entity.EntityRootGrabber;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.common.entity.EntityShock;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.entity.EntityShockwaveSwordItem;
import thebetweenlands.common.entity.EntitySpikeWave;
import thebetweenlands.common.entity.EntitySpiritTreeFaceMask;
import thebetweenlands.common.entity.EntitySplodeshroom;
import thebetweenlands.common.entity.EntitySwordEnergy;
import thebetweenlands.common.entity.EntityTinyWormEggSac;
import thebetweenlands.common.entity.EntityTriggeredFallingBlock;
import thebetweenlands.common.entity.EntityTriggeredSludgeWallJet;
import thebetweenlands.common.entity.EntityVolarkite;
import thebetweenlands.common.entity.EntityWormGroundSpawner;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.entity.draeton.EntityPullerChiromaw;
import thebetweenlands.common.entity.draeton.EntityPullerDragonfly;
import thebetweenlands.common.entity.draeton.EntityPullerFirefly;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityAshSprite;
import thebetweenlands.common.entity.mobs.EntityBarrishee;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;
import thebetweenlands.common.entity.mobs.EntityBoulderSprite;
import thebetweenlands.common.entity.mobs.EntityBubblerCrab;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityChiromawGreeblingRider;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawMatriarch;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;
import thebetweenlands.common.entity.mobs.EntityCryptCrawler;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityDreadfulMummy;
import thebetweenlands.common.entity.mobs.EntityEmberling;
import thebetweenlands.common.entity.mobs.EntityEmberlingShaman;
import thebetweenlands.common.entity.mobs.EntityEmberlingWild;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityFlameJet;
import thebetweenlands.common.entity.mobs.EntityFortressBoss;
import thebetweenlands.common.entity.mobs.EntityFortressBossBlockade;
import thebetweenlands.common.entity.mobs.EntityFortressBossProjectile;
import thebetweenlands.common.entity.mobs.EntityFortressBossSpawner;
import thebetweenlands.common.entity.mobs.EntityFortressBossTeleporter;
import thebetweenlands.common.entity.mobs.EntityFortressBossTurret;
import thebetweenlands.common.entity.mobs.EntityFreshwaterUrchin;
import thebetweenlands.common.entity.mobs.EntityFrog;
import thebetweenlands.common.entity.mobs.EntityGasCloud;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.entity.mobs.EntityGiantToad;
import thebetweenlands.common.entity.mobs.EntityGreebling;
import thebetweenlands.common.entity.mobs.EntityGreeblingVolarpadFloater;
import thebetweenlands.common.entity.mobs.EntityJellyfish;
import thebetweenlands.common.entity.mobs.EntityJellyfishCave;
import thebetweenlands.common.entity.mobs.EntityLargeSludgeWorm;
import thebetweenlands.common.entity.mobs.EntityLeech;
import thebetweenlands.common.entity.mobs.EntityLurker;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntityMireSnailEgg;
import thebetweenlands.common.entity.mobs.EntityMovingSpawnerHole;
import thebetweenlands.common.entity.mobs.EntityMultipartDummy;
import thebetweenlands.common.entity.mobs.EntityMummyArm;
import thebetweenlands.common.entity.mobs.EntityOlm;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.entity.mobs.EntityPuffin;
import thebetweenlands.common.entity.mobs.EntityPyrad;
import thebetweenlands.common.entity.mobs.EntityRockSnot;
import thebetweenlands.common.entity.mobs.EntityRockSnotTendril;
import thebetweenlands.common.entity.mobs.EntityRootSprite;
import thebetweenlands.common.entity.mobs.EntityShambler;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.entity.mobs.EntitySludge;
import thebetweenlands.common.entity.mobs.EntitySludgeJet;
import thebetweenlands.common.entity.mobs.EntitySludgeMenace;
import thebetweenlands.common.entity.mobs.EntitySludgeWorm;
import thebetweenlands.common.entity.mobs.EntitySmollSludge;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceLarge;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceSmall;
import thebetweenlands.common.entity.mobs.EntitySporeJet;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntityStalker;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.mobs.EntitySwarm;
import thebetweenlands.common.entity.mobs.EntityTamedSpiritTreeFace;
import thebetweenlands.common.entity.mobs.EntityTarBeast;
import thebetweenlands.common.entity.mobs.EntityTarminion;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWorm;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWormHelper;
import thebetweenlands.common.entity.mobs.EntityVolatileSoul;
import thebetweenlands.common.entity.mobs.EntityWallLamprey;
import thebetweenlands.common.entity.mobs.EntityWallLivingRoot;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.entity.projectiles.EntityBLFishHook;
import thebetweenlands.common.entity.projectiles.EntityBetweenstonePebble;
import thebetweenlands.common.entity.projectiles.EntityChiromawDroppings;
import thebetweenlands.common.entity.projectiles.EntityElixir;
import thebetweenlands.common.entity.projectiles.EntityFishingSpear;
import thebetweenlands.common.entity.projectiles.EntityPredatorArrowGuide;
import thebetweenlands.common.entity.projectiles.EntityPyradFlame;
import thebetweenlands.common.entity.projectiles.EntitySapSpit;
import thebetweenlands.common.entity.projectiles.EntitySludgeBall;
import thebetweenlands.common.entity.projectiles.EntitySludgeWallJet;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.entity.projectiles.EntityThrownTarminion;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.lib.ModInfo;

public class EntityRegistry {
	private EntityRegistry() {
	}

	public static void preInit() {
		registerEntity(EntityDarkDruid.class, "dark_druid", 0x000000, 0xFF0000);
		registerEntity(EntityAngler.class, "angler", 0x546332, 0x49FFFF);
		registerEntity(EntitySludge.class, "sludge", 0x726459, 0x726459);
		registerEntity(EntitySwampHag.class, "swamp_hag", 0x6E5B36, 0x226124);
		registerEntity(EntityWight.class, "wight", 0xECF8E0, 0x243B0B);
		registerEntity(EntityFirefly.class, "firefly", 0xDCFF51, 0x402A21);
		registerEntity(EntitySporeling.class, "sporeling", 0x696144, 0xFFFB00, 64, 1, true);
		registerEntity(EntityLeech.class, "leech", 0x804E3D, 0x635940);
		registerEntity(EntityDragonFly.class, "dragonfly", 0x356721, 0xC0FFEE);
		registerEntity(EntityBloodSnail.class, "blood_snail", 0x8E9456, 0xB3261E);
		registerEntity(EntityMireSnail.class, "mire_snail", 0x8E9456, 0xF2FA96);
		registerEntity(EntityMireSnailEgg.class, "mire_snail_egg");
		registerEntity(EntityBLArrow.class, "bl_arrow", 64, 20, true);
		registerEntity(EntitySnailPoisonJet.class, "snail_poison_jet");
		registerEntity(EntityLurker.class, "lurker", 0x283320, 0x827856);
		registerEntity(EntityGecko.class, "gecko", 0xFF8000, 0x22E0B1, 64, 1, true);
		registerEntity(EntityTermite.class, "termite", 0xD9D7A7, 0xD99830);
		registerEntity(EntityGiantToad.class, "toad", 0x405C3B, 0x7ABA45);
		registerEntity(EntityOlm.class, "olm", 0xD0D1C2, 0xECEDDF);
		registerEntity(EntityChiromaw.class, "chiromaw", 0x142728, 0x7A4E42);
		registerEntity(EntityFrog.class, "frog", 0x1E4921, 0x479219, 64, 20, true);
		registerEntity(EntitySwordEnergy.class, "sword_energy");
		registerEntity(EntityShockwaveSwordItem.class, "shockwave_sword_item");
		registerEntity(EntityShockwaveBlock.class, "shockwave_block");
		registerEntity(EntityGasCloud.class, "gas_cloud", 0x61988D, 0x4C7F76);
		registerEntity(EntityVolatileSoul.class, "volatile_soul");
		registerEntity(EntityTarBeast.class, "tar_beast", 0x000000, 0x202020);
		registerEntity(EntitySiltCrab.class, "silt_crab", 0x468282, 0xBC4114);
		registerEntity(EntityPyrad.class, "pyrad", 0x5E4726, 0x2D4231, 64, 3, true);
		registerEntity(EntityPyradFlame.class, "pyrad_flame");
		registerEntity(EntityPeatMummy.class, "peat_mummy", 0x524D3A, 0x69463F, 64, 1, true);
		registerEntity(EntityTarminion.class, "tarminion", 0x000000, 0x2E2E2E, 64, 1, true);
		registerEntity(EntityThrownTarminion.class, "thrown_tarminion", 64, 10, true);
		registerEntity(EntityRopeNode.class, "rope_node", 64, 1, true);
		registerEntity(EntityGrapplingHookNode.class, "grapping_hook_node", 64, 1, true);
		registerEntity(EntityVolarkite.class, "volarkite", 256, 20, false);
		registerEntity(EntityMummyArm.class, "mummy_arm", 64, 20, false);
		registerEntity(EntityAngryPebble.class, "angry_pebble");
		registerEntity(EntityFortressBoss.class, "fortress_boss", 0x000000, 0x00FFFA, 64, 1, true);
		registerEntity(EntityFortressBossSpawner.class, "fortress_boss_spawner", 64, 20, false);
		registerEntity(EntityFortressBossBlockade.class, "fortress_boss_blockade", 64, 20, false);
		registerEntity(EntityFortressBossProjectile.class, "fortress_boss_projectile", 64, 5, true);
		registerEntity(EntityFortressBossTurret.class, "fortress_boss_turret", 64, 20, false);
		registerEntity(EntityFortressBossTeleporter.class, "fortress_boss_teleporter", 64, 5, false);
        registerEntity(EntityWeedwoodRowboat.class, "weedwood_rowboat");
		registerEntity(EntityElixir.class, "bl_elexir", 64, 20, true);
		registerEntity(EntityDreadfulMummy.class, "dreadful_mummy", 0x000000, 0x591E08, 64, 1, true);
		registerEntity(EntitySludgeBall.class, "sludge_ball", 64, 20, true);
		registerEntity(EntitySporeJet.class, "spore_jet", 64, 1, true);
		//registerEntity(EntityDarkLight.class, "dark_light", 0xFFB300, 0xFFD000);
		registerEntity(EntitySmollSludge.class, "smoll_sludge", 0x726459, 0x726459);
		registerEntity(EntityGreebling.class, "greebling", 0x7CB2AA, 0xC3726C);
		registerEntity(EntityBoulderSprite.class, "boulder_sprite", 0x799679, 0x879231);
		registerEntity(EntitySpiritTreeFaceSmall.class, "spirit_tree_face_small");
		registerEntity(EntitySpiritTreeFaceLarge.class, "spirit_tree_face_large");
		registerEntity(EntityTamedSpiritTreeFace.class, "tamed_spirit_tree_face");
		registerEntity(EntitySapSpit.class, "sap_spit", 64, 20, true);
		registerEntity(EntitySpikeWave.class, "spike_wave");
		registerEntity(EntityRootGrabber.class, "root_grabber");
		registerEntity(EntitySpiritTreeFaceMask.class, "spirit_tree_face_mask", 64, 20, false);
		registerEntity(EntityRootSprite.class, "root_sprite", 0x5D533D, 0x8F952B);
		registerEntity(EntityPredatorArrowGuide.class, "predator_arrow_guide");
		registerEntity(EntitySludgeWorm.class, "small_sludge_worm", 0x6D3D39, 0x301411);
		registerEntity(EntityTinySludgeWorm.class, "tiny_sludge_worm", 0xDAC2A7, 0x5C4639);
		registerEntity(EntityLargeSludgeWorm.class, "large_sludge_worm", 0x726459, 0xE2DED9);
		registerEntity(EntityTinyWormEggSac.class, "tiny_worm_egg_sac");
		registerEntity(EntityTinySludgeWormHelper.class, "tiny_sludge_worm_helper", 0xDAC2A7, 0x5C4639);

		registerEntity(EntityLurkerSkinRaft.class, "lurker_skin_raft");
		
		registerEntity(EntityShambler.class, "shambler", 0x14331C, 0xCD5472);
		registerEntity(EntityWallLamprey.class, "wall_lamprey", 0x646B3E, 0x4B4335);
		registerEntity(EntityMovingSpawnerHole.class, "moving_spawner_hole", 0x6D3D39, 0x301411);
		registerEntity(EntityWallLivingRoot.class, "wall_living_root", 0x30251C, 0x67614A);
		registerEntity(EntityCryptCrawler.class, "crypt_crawler", 0xD9B88C, 0x8F5F44);
		registerEntity(EntityBarrishee.class, "barrishee", 0x604C30, 0xFFD92C);
		registerEntity(EntityAshSprite.class, "ash_sprite", 0x1B1F28, 0x721C16);
		registerEntity(EntityDecayPitTarget.class, "decay_pit_target");
		registerEntity(EntitySludgeJet.class, "sludge_jet", 64, 1, true);
		registerEntity(EntityTriggeredFallingBlock.class, "triggered_falling_block");
		registerEntity(EntityTriggeredSludgeWallJet.class, "triggered_sludge_wall_jet", 0x604C30, 0xFFD92C);
		registerEntity(EntitySludgeWallJet.class, "sludge_wall_jet", 64, 1, true);
		registerEntity(EntitySplodeshroom.class, "splodeshroom", 0xC8527D, 0x792B48);
		registerEntity(EntityCCGroundSpawner.class, "cc_ground_spawner");
		registerEntity(EntityEmberlingShaman.class, "emberling_shaman", 0xA43B2E, 0x391211);
		registerEntity(EntityFlameJet.class, "flame_jet", 64, 1, true);
		registerEntity(EntityMovingWall.class, "moving_wall", 64, 1, true);
		registerEntity(EntityEmberling.class, "emberling", 0xA43B2E, 0x391211);
		registerEntity(EntityEmberlingWild.class, "wild_emberling", 0x0B3B0B, 0xDBA901);
		registerEntity(EntityGalleryFrame.class, "gallery_frame");
		registerEntity(EntitySludgeMenace.class, "sludge_menace", 0x726459, 0x726459);
		registerEntity(EntitySludgeMenace.DummyPart.class, "sludge_menace_multipart_dummy", 64, 20, false);
		registerEntity(EntityShock.class, "shock", 64, 20, false);
		registerEntity(EntityBLLightningBolt.class, "lightning", 100, 20, false);

		registerEntity(EntityResurrection.class, "resurrection", 64, 6, true);
		registerEntity(EntityFalseXPOrb.class, "false_xp", 64, 20, true);
		
		registerEntity(EntityMultipartDummy.class, "multipart_dummy", 64, 20, false);

		registerEntity(EntityDraeton.class, "draeton", 80, 3, true);
		registerEntity(EntityPullerDragonfly.class, "draeton_dragonfly", 80, 3, true);
		registerEntity(EntityPullerFirefly.class, "draeton_firefly", 80, 3, true);
		registerEntity(EntityPullerChiromaw.class, "draeton_chiromaw_tamed", 80, 3, true);
		
		registerEntity(EntityChiromawGreeblingRider.class, "chiromaw_greebling_rider", 0x142728, 0x7CB2AA);
		registerEntity(EntityGreeblingVolarpadFloater.class, "greebling_volarpad_floater", 64, 20, true);
		registerEntity(EntityBetweenstonePebble.class, "betweenstone_pebble", 64, 20, true);

		registerEntity(EntityWormGroundSpawner.class, "worm_ground_spawner");
		registerEntity(EntityChiromawMatriarch.class, "chiromaw_matriarch", 0x142728, 0x4A2120, 128, 3, true);
		registerEntity(EntityChiromawDroppings.class, "chiromaw_droppings", 64, 20, true);
		registerEntity(EntityChiromawHatchling.class, "chiromaw_hatchling", 64, 3, true);
		registerEntity(EntityChiromawTame.class, "chiromaw_tame", 128, 3, true);
		registerEntity(EntityGreeblingCorpse.class, "greebling_corpse", 80, 20, false);
	
		registerEntity(EntityStalker.class, "stalker", 0xE4DCC9, 0xD58888);
		registerEntity(EntitySwarm.class, "swarm", 0x292B3B, 0xE2D698);
		
		registerEntity(EntityFishBait.class, "fish_bait_item");
		registerEntity(EntityAnadia.class, "anadia", 0x243B0B, 0x00FFFF);
		registerEntity(EntityBLFishHook.class, "betweenlands_fish_hook");
		registerEntity(EntityFishingSpear.class, "fishing_spear", 64, 20, true);
		registerEntity(EntityFishingTackleBoxSeat.class, "fishing_tackle_box_seat");
		
		//WIP Stuffs
		registerEntity(EntityRockSnot.class, "rock_snot", 0x3F5A69, 0xA16A77, 64, 20, true);
		registerEntity(EntityRockSnotTendril.class, "rock_snot_tendril", 64, 20, true);

		registerEntity(EntityPuffin.class, "puffin", 0x3F5A69, 0xA16A77, 64, 3, true);
		registerEntity(EntityAnimalBurrow.class, "animal_burrow");

		registerEntity(EntityJellyfish.class, "jellyfish", 0x3F5A69, 0xA16A77, 64, 3, true);
		registerEntity(EntityJellyfishCave.class, "jellyfish_cave", 0x3F5A69, 0xA16A77, 64, 3, true);

		registerEntity(EntityBubblerCrab.class, "bubbler_crab", 0x086A87, 0xB43104);

		registerEntity(EntityFreshwaterUrchin.class, "freshwater_urchin", 0x086A87, 0xB43104);
	}

	private static int id = 0;

	private static void registerEntity(Class<? extends Entity> entityClass, String name, int trackingRange, int trackingFrequency, boolean velocityUpdates) {
		net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(new ResourceLocation(ModInfo.ID, name), entityClass, "thebetweenlands." + name, id, TheBetweenlands.instance, trackingRange, trackingFrequency, velocityUpdates);
		id++;
	}

	private static void registerEntity(Class<? extends Entity> entityClass, String name) {
		net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(new ResourceLocation(ModInfo.ID, name), entityClass, "thebetweenlands." + name, id, TheBetweenlands.instance, 64, 3, true);
		id++;
	}

	private static void registerEntity(Class<? extends EntityLiving> entityClass, String name, int eggBackgroundColor, int eggForegroundColor, int trackingRange, int trackingFrequency, boolean velocityUpdates) {
		registerEntity(entityClass, name, trackingRange, trackingFrequency, velocityUpdates);
		net.minecraftforge.fml.common.registry.EntityRegistry.registerEgg(new ResourceLocation(ModInfo.ID, name), eggBackgroundColor, eggForegroundColor);
		id++;
	}

	private static void registerEntity(Class<? extends EntityLiving> entityClass, String name, int eggBackgroundColor, int eggForegroundColor) {
		registerEntity(entityClass, name);
		net.minecraftforge.fml.common.registry.EntityRegistry.registerEgg(new ResourceLocation(ModInfo.ID, name), eggBackgroundColor, eggForegroundColor);
		id++;
	}
}