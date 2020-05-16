package thebetweenlands.common.entity.projectiles;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.BlockRegistry;

public class EntitySludgeWallJet extends EntityThrowable {

	private boolean playedSound = false;
	private static final byte EVENT_TRAIL_PARTICLES = 105;
	private static final byte EVENT_HIT_PARTICLES = 106;

	public EntitySludgeWallJet(World world) {
		super(world);
		setSize(0.2F, 0.2F);
	}

	public EntitySludgeWallJet(World world, EntityLiving entity) {
		super(world, entity);
	}

	public EntitySludgeWallJet(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntitySludgeWallJet(World world, EntityPlayer player) {
		super(world, player);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(!getEntityWorld().isRemote)
			getEntityWorld().setEntityState(this, EVENT_TRAIL_PARTICLES);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		if(id == EVENT_TRAIL_PARTICLES) {
			for(int i = 0; i < 8; ++i) {
				double velX = 0.0D;
				double velY = 0.0D;
				double velZ = 0.0D;
				int motionX = rand.nextInt(2) * 2 - 1;
				int motionZ = rand.nextInt(2) * 2 - 1;
				velY = (rand.nextFloat() - 0.5D) * 0.125D;
				velZ = rand.nextFloat() * 0.1F * motionZ;
				velX = rand.nextFloat() * 0.1F * motionX;
				BLParticles.ITEM_BREAKING.spawn(getEntityWorld(), posX, posY, posZ, ParticleArgs.get().withMotion(velX, velY, velZ).withData(new ItemStack(BlockRegistry.MUD_BRICK_STAIRS_DECAY_3)));
			}
		}

		if(id == EVENT_HIT_PARTICLES)
			for(int i = 0; i < 16; ++i)
				BLParticles.ITEM_BREAKING.spawn(getEntityWorld(), posX + (getEntityWorld().rand.nextDouble() - 0.5D), posY + 2D + getEntityWorld().rand.nextDouble(), posZ + (getEntityWorld().rand.nextDouble() - 0.5D), ParticleArgs.get().withData(new ItemStack(BlockRegistry.MUD_BRICK_STAIRS_DECAY_3)));
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_BOBBER_SPLASH;
	}

	@Override
	protected void onImpact(RayTraceResult mop) {
		if (!getEntityWorld().isRemote) {
			if (!playedSound) {
				getEntityWorld().playSound((EntityPlayer) null, getPosition(), getSplashSound(), SoundCategory.HOSTILE, 0.25F, 2.0F);
				playedSound = true;
			}
			if (mop.typeOfHit != null && mop.typeOfHit == RayTraceResult.Type.BLOCK)
				setDead();
			if (mop.entityHit != null) {
				if (mop.typeOfHit != null && mop.typeOfHit == RayTraceResult.Type.ENTITY && mop.entityHit != thrower) {
					mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 2F);
					getEntityWorld().setEntityState(this, EVENT_HIT_PARTICLES);
					setDead();
				}
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	public boolean attackEntityFrom(DamageSource source, int amount) {
		return false;
	}
}