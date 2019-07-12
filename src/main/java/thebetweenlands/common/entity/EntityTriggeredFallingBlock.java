package thebetweenlands.common.entity;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityTriggeredFallingBlock extends EntityProximitySpawner {

	public EntityTriggeredFallingBlock(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setNoGravity(true);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote && getEntityWorld().getTotalWorldTime()%5 == 0)
			checkArea();
		
			if (getEntityWorld().isRemote)
				dustParticles();
	}

	public void dustParticles() {
		if (rand.nextInt(16) == 0) {
			BlockPos blockpos = getPosition().down();
			if (canFallThrough(getEntityWorld().getBlockState(blockpos))) {
				double d0 = (double) ((float) getPosition().getX() + rand.nextFloat());
				double d1 = (double) getPosition().getY() - 0.05D;
				double d2 = (double) ((float) getPosition().getZ() + rand.nextFloat());
				getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(getBlockType(getEntityWorld(), getPosition())));
			}
		}
	}

    public static boolean canFallThrough(IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }

	@Override
	protected void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		((EntityFallingBlock)entitySpawned).setHurtEntities(true);
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
    public boolean canBeCollidedWith() {
        return false;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if(sourceEntity instanceof EntityPlayer && ((EntityPlayer) sourceEntity).isCreative()) {
				this.setDead();
			}
		}
		return false;
	}

	@Override
	protected float getProximityHorizontal() {
		return 1F;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
	}

	protected AxisAlignedBB proximityBox() {
		return new AxisAlignedBB(getPosition()).grow(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal()).offset(0D, - getProximityVertical() * 2, 0D);
	}

	@Override
	protected boolean canSneakPast() {
		return true;
	}

	@Override
	protected boolean checkSight() {
		return false;
	}

	@Override
	protected Entity getEntitySpawned() {
		if(getBlockType(getEntityWorld(), getPosition()).getBlock() != null) {
			EntityFallingBlock entity = new EntityFallingBlock(getEntityWorld(), posX, posY, posZ, getBlockType(getEntityWorld(), getPosition()));
			return entity;
		}
		return null;
	}

	private IBlockState getBlockType(World world, BlockPos pos) {
		return world.getBlockState(pos);
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}
}