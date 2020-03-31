package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityGreeblingVolarpadFloater extends EntityThrowable {
	protected float prevFloatingRotationTicks = 0;
	protected float floatingRotationTicks = 0;

	public EntityGreeblingVolarpadFloater(World world) {
		super(world);
		setSize(1F, 1F);
		setEntityInvulnerable(true);
	}

	public EntityGreeblingVolarpadFloater(World world, double x, double y, double z) {
		super(world);
		this.setSize(1F, 1F);
		this.setPosition(x, y, z);
		setEntityInvulnerable(true);
		motionX = 0.25D - world.rand.nextDouble();
		motionZ = 0.25D - world.rand.nextDouble();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (motionY < 0.0D)
			motionY *= 0.5D;

		if (!getEntityWorld().isRemote) {
			if(ticksExisted > 120)
				setDead();
		}

		prevFloatingRotationTicks = this.floatingRotationTicks;
		floatingRotationTicks += 5;
		float wrap = MathHelper.wrapDegrees(this.floatingRotationTicks) - this.floatingRotationTicks;
		floatingRotationTicks +=wrap;
		prevFloatingRotationTicks += wrap;
	}

    public float smoothedAngle(float partialTicks) {
        return this.prevFloatingRotationTicks + (this.floatingRotationTicks - this.prevFloatingRotationTicks) * partialTicks;
    }

	@Override
    protected float getGravityVelocity() {
        return 0.09F;
    }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
    protected boolean canBeRidden(Entity entityIn) {
        return false;
    }

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.typeOfHit != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
			if (!getEntityWorld().isRemote) {
				setDead();
				getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.BLOCK_CLOTH_FALL, SoundCategory.BLOCKS, 2F, 0.5F);
			}
		}
	}
}
