package thebetweenlands.common.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityFishBait extends EntityItem {

	public EntityFishBait(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityFishBait(World world, double x, double y, double z, ItemStack stack) {
		super(world, x, y, z, stack);
	}

	public EntityFishBait(World world) {
		super(world);
	}

	@Override
    public void onUpdate() {
		super.onUpdate();
		if(isInWater())
			motionY =- 0.00001;
    }
}