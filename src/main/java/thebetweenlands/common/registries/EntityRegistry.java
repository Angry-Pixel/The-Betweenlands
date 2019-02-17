package thebetweenlands.common.registries;

import java.util.function.Consumer;

import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import thebetweenlands.common.entity.EntityAngryPebble;
import thebetweenlands.common.entity.EntityLurkerSkinRaft;
import thebetweenlands.common.entity.EntityRootGrabber;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.entity.EntityShockwaveSwordItem;
import thebetweenlands.common.entity.EntitySpikeWave;
import thebetweenlands.common.entity.EntitySpiritTreeFaceMask;
import thebetweenlands.common.entity.EntitySwordEnergy;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;
import thebetweenlands.common.entity.mobs.EntityBoulderSprite;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityDreadfulMummy;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityFortressBoss;
import thebetweenlands.common.entity.mobs.EntityFortressBossBlockade;
import thebetweenlands.common.entity.mobs.EntityFortressBossProjectile;
import thebetweenlands.common.entity.mobs.EntityFortressBossSpawner;
import thebetweenlands.common.entity.mobs.EntityFortressBossTeleporter;
import thebetweenlands.common.entity.mobs.EntityFortressBossTurret;
import thebetweenlands.common.entity.mobs.EntityFrog;
import thebetweenlands.common.entity.mobs.EntityGasCloud;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.entity.mobs.EntityGiantToad;
import thebetweenlands.common.entity.mobs.EntityGreebling;
import thebetweenlands.common.entity.mobs.EntityLeech;
import thebetweenlands.common.entity.mobs.EntityLurker;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntityMummyArm;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.entity.mobs.EntityPyrad;
import thebetweenlands.common.entity.mobs.EntityPyradFlame;
import thebetweenlands.common.entity.mobs.EntityRootSprite;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.entity.mobs.EntitySludge;
import thebetweenlands.common.entity.mobs.EntitySmollSludge;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceLarge;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceSmall;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.mobs.EntityTamedSpiritTreeFace;
import thebetweenlands.common.entity.mobs.EntityTarBeast;
import thebetweenlands.common.entity.mobs.EntityTarminion;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.entity.mobs.EntityVolatileSoul;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.entity.projectiles.EntityElixir;
import thebetweenlands.common.entity.projectiles.EntitySapSpit;
import thebetweenlands.common.entity.projectiles.EntitySludgeBall;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.entity.projectiles.EntityThrownTarminion;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.lib.ModInfo;

public class EntityRegistry {
	@SubscribeEvent
	public static void register(RegistryEvent.Register<EntityType<?>> event) {
		final IForgeRegistry<EntityType<?>> registry = event.getRegistry();

		register(new RegistryHelper<EntityType.Builder<?>>() {
			@Override
			public <F extends EntityType.Builder<?>> F reg(String regName, F obj, Consumer<F> callback) {
				EntityType<?> type = obj.build(ModInfo.ID + ":" + regName);
				type.setRegistryName(ModInfo.ID, regName);
				registry.register(type);
				return obj;
			}
		});
	}

	private static void register(RegistryHelper<EntityType.Builder<?>> reg) {
		//TODO 1.13 Spawn eggs are now flattened, need to register a spawn egg for each entity!

		reg.reg("dark_druid", EntityType.Builder.create(EntityDarkDruid.class, EntityDarkDruid::new)); //0x000000, 0xFF0000
		reg.reg("angler", EntityType.Builder.create(EntityAngler.class, EntityAngler::new)); //0x243B0B, 0x00FFFF
		reg.reg("sludge", EntityType.Builder.create(EntitySludge.class, EntitySludge::new)); //0x3A2F0B, 0x5F4C0B
		reg.reg("swamp_hag", EntityType.Builder.create(EntitySwampHag.class, EntitySwampHag::new)); //0x0B3B0B, 0xDBA901
		reg.reg("wight", EntityType.Builder.create(EntityWight.class, EntityWight::new)); //0xECF8E0, 0x243B0B
		reg.reg("firefly", EntityType.Builder.create(EntityFirefly.class, EntityFirefly::new)); //0xFFB300, 0xFFD000
		reg.reg("sporeling", EntityType.Builder.create(EntitySporeling.class, EntitySporeling::new).tracker(64, 1 /*TODO 1.13 Does sporeling really need to send update each tick?...*/, true)); //0x696144, 0xFFFB00
		reg.reg("leech", EntityType.Builder.create(EntityLeech.class, EntityLeech::new)); //0x804E3D, 0x635940
		reg.reg("dragonfly", EntityType.Builder.create(EntityDragonFly.class, EntityDragonFly::new)); //0x31B53C, 0x779E3C
		reg.reg("blood_snail", EntityType.Builder.create(EntityBloodSnail.class, EntityBloodSnail::new)); //0x8E9456, 0xB3261E
		reg.reg("mire_snail", EntityType.Builder.create(EntityMireSnail.class, EntityMireSnail::new)); //0x8E9456, 0xF2FA96
		reg.reg("bl_arrow", EntityType.Builder.create(EntityBLArrow.class, EntityBLArrow::new).tracker(64, 20, true));
		reg.reg("snail_poison_jet", EntityType.Builder.create(EntitySnailPoisonJet.class, EntitySnailPoisonJet::new));
		reg.reg("lurker", EntityType.Builder.create(EntityLurker.class, EntityLurker::new)); //0x283320, 0x827856
		reg.reg("gecko", EntityType.Builder.create(EntityGecko.class, EntityGecko::new).tracker(64, 1, true)); //0xFF8000, 0x22E0B1
		reg.reg("termite", EntityType.Builder.create(EntityTermite.class, EntityTermite::new)); //0xD9D7A7, 0xD99830
		reg.reg("toad", EntityType.Builder.create(EntityGiantToad.class, EntityGiantToad::new)); //0x405C3B, 0x7ABA45
		reg.reg("blind_cave_fish", EntityType.Builder.create(EntityBlindCaveFish.class, EntityBlindCaveFish::new)); //0xD0D1C2, 0xECEDDF
		reg.reg("chiromaw", EntityType.Builder.create(EntityChiromaw.class, EntityChiromaw::new)); //0x3F5A69, 0xA16A77
		reg.reg("frog", EntityType.Builder.create(EntityFrog.class, EntityFrog::new).tracker(64, 20, true)); //0x559653, 0xC72C2C
		reg.reg("sword_energy", EntityType.Builder.create(EntitySwordEnergy.class, EntitySwordEnergy::new));
		reg.reg("shockwave_sword_item", EntityType.Builder.create(EntityShockwaveSwordItem.class, EntityShockwaveSwordItem::new));
		reg.reg("shockwave_block", EntityType.Builder.create(EntityShockwaveBlock.class, EntityShockwaveBlock::new));
		reg.reg("gas_cloud", EntityType.Builder.create(EntityGasCloud.class, EntityGasCloud::new)); //0xFFB300, 0xFFD000
		reg.reg("volatile_soul", EntityType.Builder.create(EntityVolatileSoul.class, EntityVolatileSoul::new));
		reg.reg("tar_beast", EntityType.Builder.create(EntityTarBeast.class, EntityTarBeast::new)); //0x000000, 0x202020
		reg.reg("silt_crab", EntityType.Builder.create(EntitySiltCrab.class, EntitySiltCrab::new)); //0x086A87, 0xB43104
		reg.reg("pyrad", EntityType.Builder.create(EntityPyrad.class, EntityPyrad::new).tracker(64, 3, true)); //0x5E4726, 0x2D4231
		reg.reg("pyrad_flame", EntityType.Builder.create(EntityPyradFlame.class, EntityPyradFlame::new));
		reg.reg("peat_mummy", EntityType.Builder.create(EntityPeatMummy.class, EntityPeatMummy::new).tracker(64, 1, true)); //0x524D3A, 0x69463F
		reg.reg("tarminion", EntityType.Builder.create(EntityTarminion.class, EntityTarminion::new).tracker(64, 1, true)); //0x000000, 0x2E2E2E
		reg.reg("thrown_tarminion", EntityType.Builder.create(EntityThrownTarminion.class, EntityThrownTarminion::new).tracker(64, 10, true));
		reg.reg("rope_node", EntityType.Builder.create(EntityRopeNode.class, EntityRopeNode::new).tracker(64, 1, true));
		reg.reg("mummy_arm", EntityType.Builder.create(EntityMummyArm.class, EntityMummyArm::new).tracker(64, 20, false));
		reg.reg("angry_pebble", EntityType.Builder.create(EntityAngryPebble.class, EntityAngryPebble::new));
		reg.reg("fortress_boss", EntityType.Builder.create(EntityFortressBoss.class, EntityFortressBoss::new).tracker(64, 1, true)); //0x000000, 0x00FFFA
		reg.reg("fortress_boss_spawner", EntityType.Builder.create(EntityFortressBossSpawner.class, EntityFortressBossSpawner::new).tracker(64, 20, false));
		reg.reg("fortress_boss_blockade", EntityType.Builder.create(EntityFortressBossBlockade.class, EntityFortressBossBlockade::new).tracker(64, 20, false));
		reg.reg("fortress_boss_projectile", EntityType.Builder.create(EntityFortressBossProjectile.class, EntityFortressBossProjectile::new).tracker(64, 5, true));
		reg.reg("fortress_boss_turret", EntityType.Builder.create(EntityFortressBossTurret.class, EntityFortressBossTurret::new).tracker(64, 20, false));
		reg.reg("fortress_boss_teleporter", EntityType.Builder.create(EntityFortressBossTeleporter.class, EntityFortressBossTeleporter::new).tracker(64, 5, false));
		reg.reg("weedwood_rowboat", EntityType.Builder.create(EntityWeedwoodRowboat.class, EntityWeedwoodRowboat::new));
		reg.reg("bl_elexir", EntityType.Builder.create(EntityElixir.class, EntityElixir::new).tracker(64, 20, true));
		reg.reg("dreadful_mummy", EntityType.Builder.create(EntityDreadfulMummy.class, EntityDreadfulMummy::new).tracker(64, 1, true)); //0x000000, 0x591E08
		reg.reg("sludge_ball", EntityType.Builder.create(EntitySludgeBall.class, EntitySludgeBall::new).tracker(64, 20, true));
		reg.reg("smoll_sludge", EntityType.Builder.create(EntitySmollSludge.class, EntitySmollSludge::new)); //0x3A2F0B, 0x5F4C0B
		reg.reg("greebling", EntityType.Builder.create(EntityGreebling.class, EntityGreebling::new)); //0xD9D7A7, 0xD99830
		reg.reg("boulder_sprite", EntityType.Builder.create(EntityBoulderSprite.class, EntityBoulderSprite::new)); //0x6f7784, 0x535559
		reg.reg("spirit_tree_face_small", EntityType.Builder.create(EntitySpiritTreeFaceSmall.class, EntitySpiritTreeFaceSmall::new));
		reg.reg("spirit_tree_face_large", EntityType.Builder.create(EntitySpiritTreeFaceLarge.class, EntitySpiritTreeFaceLarge::new));
		reg.reg("tamed_spirit_tree_face", EntityType.Builder.create(EntityTamedSpiritTreeFace.class, EntityTamedSpiritTreeFace::new));
		reg.reg("sap_spit", EntityType.Builder.create(EntitySapSpit.class, EntitySapSpit::new).tracker(64, 20, true));
		reg.reg("spike_wave", EntityType.Builder.create(EntitySpikeWave.class, EntitySpikeWave::new));
		reg.reg("root_grabber", EntityType.Builder.create(EntityRootGrabber.class, EntityRootGrabber::new));
		reg.reg("spirit_tree_face_mask", EntityType.Builder.create(EntitySpiritTreeFaceMask.class, EntitySpiritTreeFaceMask::new).tracker(64, 20, false));
		reg.reg("root_sprite", EntityType.Builder.create(EntityRootSprite.class, EntityRootSprite::new)); //0x686868, 0x9fe530
		reg.reg("lurker_skin_raft", EntityType.Builder.create(EntityLurkerSkinRaft.class, EntityLurkerSkinRaft::new));
	}
}