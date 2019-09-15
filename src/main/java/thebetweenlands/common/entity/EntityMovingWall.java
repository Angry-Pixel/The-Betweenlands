package thebetweenlands.common.entity;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityMovingWall extends Entity {
    public Entity ignoreEntity;
    private int ignoreTime;
	private final ItemStack renderStack1 = new ItemStack(BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().getBlock(), 1, 8);
	private final ItemStack renderStack2 = new ItemStack(BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().getBlock(), 1, 2);
	private final ItemStack renderStack3 = new ItemStack(BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().getBlock(), 1, 12);

	public EntityMovingWall(World world) {
		super(world);
		setSize(1F, 1F);
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote)
			if (getEntityWorld().getDifficulty() == EnumDifficulty.PEACEFUL)
				setDead();

		calculateAllCollisions(posX, posY, posZ);
		calculateAllCollisions(posX, posY - 1D, posZ);
		calculateAllCollisions(posX, posY + 1D, posZ);

		if (getHorizontalFacing() == EnumFacing.NORTH || getHorizontalFacing() == EnumFacing.SOUTH) {
			calculateAllCollisions(posX - 1D, posY, posZ);
			calculateAllCollisions(posX - 1D, posY - 1D, posZ);
			calculateAllCollisions(posX - 1D, posY + 1D, posZ);
			calculateAllCollisions(posX + 1D, posY, posZ);
			calculateAllCollisions(posX + 1D, posY - 1D, posZ);
			calculateAllCollisions(posX + 1D, posY + 1D, posZ);
		}
		else {
			calculateAllCollisions(posX, posY, posZ - 1D);
			calculateAllCollisions(posX, posY - 1D, posZ - 1D);
			calculateAllCollisions(posX, posY + 1D, posZ - 1D);
			calculateAllCollisions(posX, posY, posZ + 1D);
			calculateAllCollisions(posX, posY - 1D, posZ + 1D);
			calculateAllCollisions(posX, posY + 1D, posZ + 1D);
		}

		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		rotationYaw = (float) (MathHelper.atan2(-motionX, motionZ) * (180D / Math.PI));
		setPosition(posX, posY, posZ);
	    setEntityBoundingBox(getCollisionBoundingBox());
	}

	public void calculateAllCollisions(double posX, double posY, double posZ) {
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        Vec3d vec3d1 = new Vec3d(posX + motionX * 12D, posY + motionY, posZ + motionZ * 12D); //adjust multiplier higher for slower speeds
        RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d, vec3d1);
        vec3d = new Vec3d(posX, posY, posZ);
        vec3d1 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);

        if (raytraceresult != null)
            vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);

        Entity entity = null;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getCollisionBoundingBox().expand(motionX, motionY, motionZ).grow(1.0D));
        double d0 = 0.0D;
        boolean ignore = false;

		for (int entityCount = 0; entityCount < list.size(); ++entityCount) {
			Entity entity1 = list.get(entityCount);

			if (entity1.canBeCollidedWith()) {
				if (entity1 == ignoreEntity)
					ignore = true;
				else if (ticksExisted < 2 && ignoreEntity == null) {
					ignoreEntity = entity1;
					ignore = true;
				} else {
					ignore = false;
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
					RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

					if (raytraceresult1 != null) {
						double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

						if (d1 < d0 || d0 == 0.0D) {
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}
		}

		if (ignoreEntity != null) {
			if (ignore)
				ignoreTime = 2;
			else if (ignoreTime-- <= 0)
				ignoreEntity = null;
		}

		if (entity != null)
			raytraceresult = new RayTraceResult(entity);

		if (raytraceresult != null)
			onImpact(raytraceresult);
	}
	
	protected void onImpact(RayTraceResult result) {
		if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			IBlockState state = getEntityWorld().getBlockState(result.getBlockPos());
			if (state.getBlock().canCollideCheck(state, false)) {
				if (result.sideHit.getIndex() == 2 || result.sideHit.getIndex() == 3) {
					motionZ *= -1D;
					velocityChanged = true;
				} else if (result.sideHit.getIndex() == 4 || result.sideHit.getIndex() == 5) {
					motionX *= -1D;
					velocityChanged = true;
				}
			}
		}
		if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
			if (result.entityHit instanceof EntityLivingBase) {
				((EntityLivingBase) result.entityHit).attackEntityFrom(DamageSource.GENERIC, 4F);
				if (!getEntityWorld().isRemote)
					getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundRegistry.REJECTED, SoundCategory.HOSTILE, 1F, 1F);
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
    public AxisAlignedBB getEntityBoundingBox() {
		if (getHorizontalFacing() == EnumFacing.NORTH || getHorizontalFacing() == EnumFacing.SOUTH)
			return new AxisAlignedBB(posX - 0.5D, posY - 0.5D, posZ - 0.5D, posX + 0.5D, posY + 0.5D, posZ + 0.5D).grow(1D, 1D, 0D).offset(0D, 0.5D, 0D);
		return new AxisAlignedBB(posX - 0.5D, posY - 0.5D, posZ - 0.5D, posX + 0.5D, posY + 0.5D, posZ + 0.5D).grow(0D, 1D, 1D).offset(0D, 0.5D, 0D);
    }

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return getEntityBoundingBox();
	}

	public ItemStack cachedStackTop() {
		return renderStack1;
	}

	public ItemStack cachedStackMid() {
		return renderStack2;
	}

	public ItemStack cachedStackBot() {
		return renderStack3;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}

}
