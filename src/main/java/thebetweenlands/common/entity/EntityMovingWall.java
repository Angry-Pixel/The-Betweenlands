package thebetweenlands.common.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
	public final Map<Block, Boolean> UNBREAKABLE_BLOCKS = new HashMap<Block, Boolean>();

	public EntityMovingWall(World world) {
		super(world);
		setSize(1F, 1F);
		initUnBreakableBlockMap();// using map for this atm because I don't know if we'll add a load of stuff or not
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

		calculateAllCollisions(posX, posY + 0.5D, posZ);
		calculateAllCollisions(posX, posY - 0.5D, posZ);
		calculateAllCollisions(posX, posY + 1.5D, posZ);

		if (getHorizontalFacing() == EnumFacing.NORTH || getHorizontalFacing() == EnumFacing.SOUTH) {
			calculateAllCollisions(posX - 1D, posY + 0.5D, posZ);
			calculateAllCollisions(posX - 1D, posY - 0.5D, posZ);
			calculateAllCollisions(posX - 1D, posY + 1.5D, posZ);
			calculateAllCollisions(posX + 1D, posY + 0.5D, posZ);
			calculateAllCollisions(posX + 1D, posY - 0.5D, posZ);
			calculateAllCollisions(posX + 1D, posY + 1.5D, posZ);
		}
		else {
			calculateAllCollisions(posX, posY + 0.5D, posZ - 1D);
			calculateAllCollisions(posX, posY - 0.5D, posZ - 1D);
			calculateAllCollisions(posX, posY + 1.5D, posZ - 1D);
			calculateAllCollisions(posX, posY + 0.5D, posZ + 1D);
			calculateAllCollisions(posX, posY - 0.5D, posZ + 1D);
			calculateAllCollisions(posX, posY + 1.5D, posZ + 1D);
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
			if (isUnBreakableBlock(state.getBlock())) { // not sure of all the different states so default will do
				if (result.sideHit.getIndex() == 2 || result.sideHit.getIndex() == 3) {
					motionZ *= -1D;
					velocityChanged = true;
				} else if (result.sideHit.getIndex() == 4 || result.sideHit.getIndex() == 5) {
					motionX *= -1D;
					velocityChanged = true;
				}
			}
			else {
				if (state.getBlock() != Blocks.BEDROCK) {
					getEntityWorld().destroyBlock(result.getBlockPos(), false);
					getEntityWorld().notifyNeighborsOfStateChange(result.getBlockPos(), state.getBlock(), true);
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

	public boolean isUnBreakableBlock(Block block) {
		return UNBREAKABLE_BLOCKS.get(block) != null;
	}

	//TODO - May want to move to a config one day - add blocks not to break here. Removed state sensitivity because its a pita and would need all states
	private void initUnBreakableBlockMap() {
		if (UNBREAKABLE_BLOCKS.isEmpty()) {
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_ALCOVE, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.WORM_DUNGEON_PILLAR, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_TILES, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_TILES_WATER, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_STAIRS, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_STAIRS_DECAY_1, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_STAIRS_DECAY_2, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_STAIRS_DECAY_3, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_STAIRS_DECAY_4, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_SLAB, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_SLAB_DECAY_1, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_SLAB_DECAY_2, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_SLAB_DECAY_3, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_SLAB_DECAY_4, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICKS, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICKS_CARVED, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICK_SPIKE_TRAP, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_TILES_SPIKE_TRAP, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.MUD_BRICKS_CLIMBABLE, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.DUNGEON_DOOR_COMBINATION, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.DUNGEON_DOOR_RUNES, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.DUNGEON_DOOR_RUNES_MIMIC, true);
			UNBREAKABLE_BLOCKS.put(BlockRegistry.DUNGEON_DOOR_RUNES_CRAWLER, true);
		}
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
