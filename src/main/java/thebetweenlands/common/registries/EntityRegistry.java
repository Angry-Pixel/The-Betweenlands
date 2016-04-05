package thebetweenlands.common.registries;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntityMireSnailEgg;

public class EntityRegistry {
	private static void registerEntity(int id, Class<? extends Entity> entityClass, String name, int trackingRange, int trackingFrequency, boolean velocityUpdates) {
		net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(entityClass, name, id, TheBetweenlands.instance, trackingRange, trackingFrequency, velocityUpdates);
	}

	private static void registerEntity(int id, Class<? extends Entity> entityClass, String name) {
		net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(entityClass, name, id, TheBetweenlands.instance, 64, 3, true);
	}

	private static void registerEntity(int id, Class<? extends EntityLiving> entityClass, String name, int eggBackgroundColor, int eggForegroundColor, int trackingRange, int trackingFrequency, boolean velocityUpdates) {
		registerEntity(id, entityClass, name, trackingRange, trackingFrequency, velocityUpdates);
		net.minecraftforge.fml.common.registry.EntityRegistry.registerEgg(entityClass, eggBackgroundColor, eggForegroundColor);
	}

	private static void registerEntity(int id, Class<? extends EntityLiving> entityClass, String name, int eggBackgroundColor, int eggForegroundColor) {
		registerEntity(id, entityClass, name);
		net.minecraftforge.fml.common.registry.EntityRegistry.registerEgg(entityClass, eggBackgroundColor, eggForegroundColor);
	}

	public void preInit() {
		registerEntity(1, EntityAngler.class, "angler", 0x243B0B, 0x00FFFF);
		registerEntity(11, EntityMireSnail.class, "mireSnail", 0x8E9456, 0xF2FA96);
		registerEntity(12, EntityMireSnailEgg.class, "mireSnailEgg");
		registerEntity(24, EntityBlindCaveFish.class, "blindCaveFish", 0xD0D1C2, 0xECEDDF);
	}
}
