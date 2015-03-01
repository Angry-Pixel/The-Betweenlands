package thebetweenlands.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.entities.mobs.EntitySludge;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.items.SpawnEggs;
import cpw.mods.fml.common.registry.EntityRegistry;

public class BLEntityRegistry
{
    public static void init() {
        // This is an outdated way of doing it
        // EntityRegistry.registerModEntity(EntityDarkDruid.class, "darkDruid", 2, TheBetweenlands.instance, 30, 3, true);

        // Entity registrations can be with or without spawn egg for non mob entites eg. projectiles etc.
        registerEntity(0, EntityDarkDruid.class, "darkDruid", -1251634, -13032944);
        registerEntity(1, EntityAngler.class, "angler", -1251634, -13032944);
        registerEntity(2, EntitySludge.class, "sludge", -1251634, -13032944);
        registerEntity(3, EntitySwampHag.class, "swampHag", -1251634, -13032944);
        registerEntity(4, EntityTarBeast.class, "tarBeast", -1251634, -13032944);
        registerEntity(5, EntityWight.class, "wight", -1251634, -13032944);
    }

    private static final void registerEntity(int id, Class<? extends Entity> entityClass, String name) {
        EntityRegistry.registerModEntity(entityClass, name, id, TheBetweenlands.instance, 256, 1, true);
    }

    private static final void registerEntity(int id, Class<? extends EntityLiving> entityClass, String name, int eggBackgroundColor, int eggForegroundColor) {
        registerEntity(id, entityClass, name);
        SpawnEggs.registerSpawnEgg(entityClass, name, id, eggBackgroundColor, eggForegroundColor);
    }
}
