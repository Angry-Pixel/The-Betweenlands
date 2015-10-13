package thebetweenlands.entities;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.world.World;

public class EntityWeedwoodRowboat extends EntityBoat {
    public EntityWeedwoodRowboat(World world) {
        super(world);
    }

    public EntityWeedwoodRowboat(World world, double x, double y, double z){
        super(world, x, y, z);
    }
}
