package thebetweenlands.common.entity.projectiles;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class EntitySludgeWallJet extends EntityThrowable {

	private boolean playedSound = false;

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
		if(getEntityWorld().isRemote)
			for(int i = 0; i < 8; ++i) {
				double velX = 0.0D;
				double velY = 0.0D;
				double velZ = 0.0D;
				int motionX = rand.nextInt(2) * 2 - 1;
				int motionZ = rand.nextInt(2) * 2 - 1;
				velY = (rand.nextFloat() - 0.5D) * 0.125D;
				velZ = rand.nextFloat() * 0.1F * motionZ;
				velX = rand.nextFloat() * 0.1F * motionX;
				getEntityWorld().spawnParticle(EnumParticleTypes.ITEM_CRACK, posX, posY, posZ, velX, velY, velZ, Item.getIdFromItem(Item.getItemFromBlock(BlockRegistry.MUD_BRICK_STAIRS_DECAY_3)));
			}
		}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if(id == 3)
			for(int i = 0; i < 16; ++i)
				getEntityWorld().spawnParticle(EnumParticleTypes.ITEM_CRACK, posX, posY, posZ, 0.0D, 0.0D, 0.0D, Item.getIdFromItem(Item.getItemFromBlock(BlockRegistry.MUD_BRICK_STAIRS_DECAY_3)));
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
					mop.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 1F);
					getEntityWorld().setEntityState(this, (byte) 3);
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