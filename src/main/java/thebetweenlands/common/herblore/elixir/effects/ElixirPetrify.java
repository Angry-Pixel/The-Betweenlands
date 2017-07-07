package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ElixirPetrify extends ElixirEffect {
	public ElixirPetrify(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(EntityLivingBase livingBase, int strength) {
		livingBase.setLocationAndAngles(MathHelper.floor(livingBase.posX) + 0.5, livingBase.onGround ? MathHelper.floor(livingBase.getEntityBoundingBox().minY) : livingBase.getEntityBoundingBox().minY, MathHelper.floor(livingBase.posZ) + 0.5, 0, 0);
		livingBase.motionX = livingBase.motionZ = 0.0;
		livingBase.motionY = -1F;
		livingBase.isSwingInProgress = false;
		livingBase.limbSwing = 0F;
		livingBase.limbSwingAmount = 0F;
		livingBase.swingProgressInt = 0;
		livingBase.moveStrafing = 0.0F;
		livingBase.moveForward = 0.0F;
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		return true;
	}
}
