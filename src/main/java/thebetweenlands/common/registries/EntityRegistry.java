package thebetweenlands.common.registries;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.EntityAngryPebble;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.entity.EntityShockwaveSwordItem;
import thebetweenlands.common.entity.EntitySwordEnergy;
import thebetweenlands.common.entity.mobs.*;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.entity.projectiles.EntityThrownTarminion;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.lib.ModInfo;

public class EntityRegistry {
	private EntityRegistry() {
	}

	public static void preInit() {
		registerEntity(EntityDarkDruid.class, "dark_druid", 0x000000, 0xFF0000);
		registerEntity(EntityAngler.class, "angler", 0x243B0B, 0x00FFFF);
		registerEntity(EntitySludge.class, "sludge", 0x3A2F0B, 0x5F4C0B);
		registerEntity(EntitySwampHag.class, "swamp_hag", 0x0B3B0B, 0xDBA901);
		registerEntity(EntityWight.class, "wight", 0xECF8E0, 0x243B0B);
		registerEntity(EntityFirefly.class, "firefly", 0xFFB300, 0xFFD000);
		registerEntity(EntitySporeling.class, "sporeling", 0x696144, 0xFFFB00, 64, 1, true);
		registerEntity(EntityLeech.class, "leech", 0x804E3D, 0x635940);
		registerEntity(EntityDragonFly.class, "dragonfly", 0x31B53C, 0x779E3C);
		registerEntity(EntityBloodSnail.class, "blood_snail", 0x8E9456, 0xB3261E);
		registerEntity(EntityMireSnail.class, "mire_snail", 0x8E9456, 0xF2FA96);
		registerEntity(EntityMireSnailEgg.class, "mire_snail_egg");
		registerEntity(EntityBLArrow.class, "bl_arrow", 64, 20, true);
		registerEntity(EntitySnailPoisonJet.class, "snail_poison_jet");
		registerEntity(EntityLurker.class, "lurker", 0x283320, 0x827856);
		registerEntity(EntityGecko.class, "gecko", 0xFF8000, 0x22E0B1, 64, 1, true);
		registerEntity(EntityTermite.class, "termite", 0xD9D7A7, 0xD99830);
		registerEntity(EntityGiantToad.class, "toad", 0x405C3B, 0x7ABA45);
		registerEntity(EntityBlindCaveFish.class, "blind_cave_fish", 0xD0D1C2, 0xECEDDF);
		registerEntity(EntityChiromaw.class, "chiromaw", 0x3F5A69, 0xA16A77);
		registerEntity(EntityFrog.class, "frog", 0x559653, 0xC72C2C, 64, 20, true);
		registerEntity(EntitySwordEnergy.class, "sword_energy");
		registerEntity(EntityShockwaveSwordItem.class, "shockwave_sword_item");
		registerEntity(EntityShockwaveBlock.class, "shockwave_block");
		registerEntity(EntityGasCloud.class, "gas_cloud", 0xFFB300, 0xFFD000);
		registerEntity(EntityVolatileSoul.class, "volatile_soul");
		registerEntity(EntityTarBeast.class, "tar_beast", 0x000000, 0x202020);
		registerEntity(EntitySiltCrab.class, "silt_crab", 0x086A87, 0xB43104);
		registerEntity(EntityPyrad.class, "pyrad", 0x5E4726, 0x2D4231, 64, 3, true);
		registerEntity(EntityPyradFlame.class, "pyrad_flame");
		registerEntity(EntityPeatMummy.class, "peat_mummy", 0x524D3A, 0x69463F, 64, 1, true);
		registerEntity(EntityTarminion.class, "tarminion", 0x000000, 0x2E2E2E, 64, 1, true);
		registerEntity(EntityThrownTarminion.class, "thrown_tarminion", 64, 10, true);
		registerEntity(EntityRopeNode.class, "rope_node", 64, 1, true);
		registerEntity(EntityMummyArm.class, "mummy_arm", 64, 20, false);
		registerEntity(EntityAngryPebble.class, "angry_pebble");
		registerEntity(EntityFortressBoss.class, "fortress_boss", 0x000000, 0x00FFFA, 64, 1, true);
		registerEntity(EntityFortressBossSpawner.class, "fortress_boss_spawner", 64, 20, false);
		registerEntity(EntityFortressBossBlockade.class, "fortress_boss_blockade", 64, 20, false);
		registerEntity(EntityFortressBossProjectile.class, "fortress_boss_projectile", 64, 5, true);
		registerEntity(EntityFortressBossTurret.class, "fortress_boss_turret", 64, 20, false);
		registerEntity(EntityFortressBossTeleporter.class, "fortress_boss_teleporter", 64, 5, false);
        registerEntity(EntityWeedwoodRowboat.class, "weedwood_rowboat");
	}

	private static int id = 0;

	private static void registerEntity(Class<? extends Entity> entityClass, String name, int trackingRange, int trackingFrequency, boolean velocityUpdates) {
		net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(new ResourceLocation(ModInfo.ID, name), entityClass, name, id, TheBetweenlands.INSTANCE, trackingRange, trackingFrequency, velocityUpdates);
		id++;
	}

	private static void registerEntity(Class<? extends Entity> entityClass, String name) {
		net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(new ResourceLocation(ModInfo.ID, name), entityClass, name, id, TheBetweenlands.INSTANCE, 64, 3, true);
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