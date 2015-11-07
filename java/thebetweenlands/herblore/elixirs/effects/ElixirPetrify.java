package thebetweenlands.herblore.elixirs.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class ElixirPetrify extends ElixirEffect {
	public ElixirPetrify(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(EntityLivingBase livingBase, int strength) {
		livingBase.posX = MathHelper.floor_double(livingBase.posX) + 0.5;
		livingBase.posY = MathHelper.floor_double(livingBase.posY);
		livingBase.posZ = MathHelper.floor_double(livingBase.posZ) + 0.5;
		livingBase.rotationYaw = livingBase.prevRotationYaw = 0F;
		livingBase.renderYawOffset = livingBase.prevRenderYawOffset = 0F;
		livingBase.motionX = livingBase.motionY = livingBase.motionZ = 0.0;
		livingBase.isSwingInProgress = false;
		livingBase.limbSwing = 0F;
		livingBase.limbSwingAmount = 0F;
		livingBase.swingProgressInt = 0;


		int x = MathHelper.floor_double(livingBase.posX);
		int y = MathHelper.floor_double(livingBase.posY) - 1;
		int z = MathHelper.floor_double(livingBase.posZ);

		livingBase.moveStrafing = 0.0F;
		livingBase.moveForward = 0.0F;
		if (livingBase.worldObj.getBlock(x, y, z) == null)
			livingBase.posY -= 1;
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		return true;
	}
}
