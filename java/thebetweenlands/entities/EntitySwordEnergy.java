package thebetweenlands.entities;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.utils.AnimationMathHelper;

public class EntitySwordEnergy extends Entity {
	public float pulseFloat;
	AnimationMathHelper pulse = new AnimationMathHelper();
	public EntitySwordEnergy(World world) {
		super(world);
		setSize(0.5F, 0.5F);
	}

	@Override
	protected void entityInit() {
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		pulseFloat = pulse.swing(0.3F, 0.2F, false);
		motionY = 0;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
	}

}

