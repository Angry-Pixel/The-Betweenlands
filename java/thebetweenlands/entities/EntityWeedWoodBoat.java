package thebetweenlands.entities;

import net.minecraft.entity.item.EntityBoat;
import net.minecraft.world.World;

/**
 * Created by Bart on 3-10-2015.
 */
public class EntityWeedWoodBoat extends EntityBoat {
    public EntityWeedWoodBoat(World world) {
        super(world);
    }

    public EntityWeedWoodBoat(World world, double x, double y, double z){
        super(world, x, y, z);
    }
}
