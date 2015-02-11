package thebetweenlands.entities;

import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BLEntityRegistry {
	public static void init() {
		EntityRegistry.registerModEntity(EntityDarkDruid.class, "darkDruid", 2, TheBetweenlands.instance, 30, 3, true);
	}

	public static void addNames() {
		//TODO: Fix
		//LanguageRegistry.instance().addStringLocalization("entity.DarkDruid.name", "en_US","DarkDruid");
	}
}