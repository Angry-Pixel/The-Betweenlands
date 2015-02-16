package thebetweenlands.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.items.SpawnEggs;
import cpw.mods.fml.common.registry.EntityRegistry;

public class BLEntityRegistry
{
    public static void init() {
        // This is an outdated way of doing it
        // EntityRegistry.registerModEntity(EntityDarkDruid.class, "darkDruid", 2, TheBetweenlands.instance, 30, 3, true);

        // Entity registrations can be with or without spawn egg for non mob entites eg. projectiles etc.
        registerEntity(0, EntityDarkDruid.class, "darkDruid", -1251634, -13032944);
    }

    private static final void registerEntity(int id, Class<? extends Entity> entityClass, String name) {
        EntityRegistry.registerModEntity(entityClass, name, id, TheBetweenlands.instance, 256, 1, true);
    }

    private static final void registerEntity(int id, Class<? extends EntityLiving> entityClass, String name, int eggBackgroundColor, int eggForegroundColor) {
        registerEntity(id, entityClass, name);
        SpawnEggs.registerSpawnEgg(entityClass, name, id, eggBackgroundColor, eggForegroundColor);
    }
}
