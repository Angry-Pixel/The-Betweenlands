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
		livingBase.setLocationAndAngles(MathHelper.floor_double(livingBase.posX) + 0.5, livingBase.onGround ? MathHelper.floor_double(livingBase.boundingBox.minY) : livingBase.boundingBox.minY, MathHelper.floor_double(livingBase.posZ) + 0.5, 0, 0);
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
