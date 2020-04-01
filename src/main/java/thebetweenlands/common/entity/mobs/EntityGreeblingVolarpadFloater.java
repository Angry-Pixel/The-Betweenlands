package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityGreeblingVolarpadFloater extends EntityThrowable {
	
	protected static final byte EVENT_START_DISAPPEARING = 40;
	protected static final byte EVENT_DISAPPEAR = 41;
	public int disappearTimer = 0;
	protected float prevFloatingRotationTicks = 0;
	protected float floatingRotationTicks = 0;

	public EntityGreeblingVolarpadFloater(World world) {
		super(world);
		setSize(1F, 1F);
		setEntityInvulnerable(true);
	}

	public EntityGreeblingVolarpadFloater(World world, double x, double y, double z) {
		super(world);
		setSize(1F, 1F);
		setPosition(x, y, z);
		setEntityInvulnerable(true);
		motionX = 0.25D - (world.rand.nextDouble() * 0.5D);
		motionZ = 0.25D - (world.rand.nextDouble() * 0.5D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (motionY < 0.0D)
			motionY *= 0.5D;

		prevFloatingRotationTicks = floatingRotationTicks;
		floatingRotationTicks += 5;
		float wrap = MathHelper.wrapDegrees(floatingRotationTicks) - floatingRotationTicks;
		floatingRotationTicks +=wrap;
		prevFloatingRotationTicks += wrap;
		
		if (disappearTimer > 0 && disappearTimer < 8)
			disappearTimer++;
		
		if(!getEntityWorld().isRemote) {
			if (disappearTimer == 5)
				getEntityWorld().setEntityState(this, EVENT_DISAPPEAR);
			if (disappearTimer >= 8)
				setDead();
			List<EntityPlayer> nearPlayers = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().grow(4.5, 5, 4.5));
			if (disappearTimer == 0 && (!nearPlayers.isEmpty() || ticksExisted > 80))
				startVanishEvent();
		}
	}

	public void startVanishEvent() {
		disappearTimer++;
		getEntityWorld().playSound(null, posX, posY, posZ, SoundRegistry.GREEBLING_VANISH, SoundCategory.NEUTRAL, 1, 1);
		getEntityWorld().setEntityState(this, EVENT_START_DISAPPEARING);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		if(id == EVENT_START_DISAPPEARING)
			disappearTimer = 1;
		else if(id == EVENT_DISAPPEAR)
			doLeafEffects();
	}
	
	private void doLeafEffects() {
		if(world.isRemote) {
			int leafCount = 40;
			float x = (float) (posX);
			float y = (float) (posY) + 0.5F;
			float z = (float) (posZ);
			while (leafCount-- > 0) {
				float dx = getEntityWorld().rand.nextFloat() * 1 - 0.5f;
				float dy = getEntityWorld().rand.nextFloat() * 1f - 0.1F;
				float dz = getEntityWorld().rand.nextFloat() * 1 - 0.5f;
				float mag = 0.08F + getEntityWorld().rand.nextFloat() * 0.07F;
				BLParticles.WEEDWOOD_LEAF.spawn(getEntityWorld(), x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

    public float smoothedAngle(float partialTicks) {
        return prevFloatingRotationTicks + (floatingRotationTicks - prevFloatingRotationTicks) * partialTicks;
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
		if (result.typeOfHit != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
			if (!getEntityWorld().isRemote)
				startVanishEvent();
	}
}
