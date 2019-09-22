package thebetweenlands.common.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.client.audio.MovingWallSound;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityMovingWall extends Entity implements IEntityScreenShake {
	private static final DataParameter<Boolean> IS_NEW_SPAWN = EntityDataManager.createKey(EntityMovingWall.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> HOLD_STILL = EntityDataManager.createKey(EntityMovingWall.class, DataSerializers.BOOLEAN);

	public Entity ignoreEntity;
	private int ignoreTime;
	private int holdCount;
	public boolean playSlideSound = true;
	private int prev_shake_timer;
	private int shake_timer;
	private boolean shaking = false;
	private int shakingTimerMax = 20;

	private final ItemStack renderStack1 = new ItemStack(BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().getBlock(), 1, 8);
	private final ItemStack renderStack2 = new ItemStack(BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().getBlock(), 1, 2);
	private final ItemStack renderStack3 = new ItemStack(BlockRegistry.MUD_BRICKS_CARVED.getDefaultState().getBlock(), 1, 12);

	public static final Set<Block> UNBREAKABLE_BLOCKS = new HashSet<Block>();

	static {
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_ALCOVE);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.WORM_DUNGEON_PILLAR);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_TILES);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_TILES_WATER);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_STAIRS);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_STAIRS_DECAY_1);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_STAIRS_DECAY_2);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_STAIRS_DECAY_3);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_STAIRS_DECAY_4);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SLAB);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SLAB_DECAY_1);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SLAB_DECAY_2);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SLAB_DECAY_3);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SLAB_DECAY_4);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICKS);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICKS_CARVED);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_SPIKE_TRAP);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_TILES_SPIKE_TRAP);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICKS_CLIMBABLE);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.DUNGEON_DOOR_COMBINATION);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.DUNGEON_DOOR_RUNES);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.DUNGEON_DOOR_RUNES_MIMIC);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.DUNGEON_DOOR_RUNES_CRAWLER);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.DUNGEON_DOOR_RUNES_CRAWLER);
		UNBREAKABLE_BLOCKS.add(BlockRegistry.MUD_BRICK_WALL);
	}

	public EntityMovingWall(World world) {
		super(world);
		setSize(1F, 1F);
	}

	@Override
	protected void entityInit() {
		dataManager.register(IS_NEW_SPAWN, true);
		dataManager.register(HOLD_STILL, false);
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote) {
			if (getEntityWorld().getDifficulty() == EnumDifficulty.PEACEFUL)
				setDead();
			if(ticksExisted == 1 && isNewSpawn())
				checkSpawnArea();
			
			if(ticksExisted == 2) //needs to have moved 1 tick for direction to work
				doJankSafetyCheck();

			if(isHoldingStill()) {
				holdCount--;
				if (holdCount <= 0) {
					setHoldStill(false);
					holdCount = 20;
				}
			}
		}

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

		if(!isHoldingStill()) {
			posX += motionX;
			posY += motionY;
			posZ += motionZ;

			this.pushEntitiesAway();
		}

		rotationYaw = (float) (MathHelper.atan2(-motionX, motionZ) * (180D / Math.PI));
		setPosition(posX, posY, posZ);
		setEntityBoundingBox(getCollisionBoundingBox());

		if (isShaking())
			shake(10);

		if (getEntityWorld().isRemote) {
			if (isHoldingStill())
				if (!playSlideSound)
					playSlideSound = true;

			if (!isHoldingStill())
				if (playSlideSound) {
					playChainSound(getEntityWorld(), getPosition());
					playSlideSound = false;
				}
		}
	}

	protected void pushEntitiesAway() {
		boolean collision = false;
		
		double maxReverseX = -1;
		double maxReverseZ = -1;
		
		AxisAlignedBB collisionAABB = this.getCollisionBoundingBox();
		if(collisionAABB != null) {
			List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, collisionAABB);

			for(Entity entity : entities) {
				if(entity.canBeCollidedWith()) {
					if(!entity.canBePushed() && entity instanceof EntityMovingWall == false) {
						collision = true;
					} else {
						AxisAlignedBB entityAABB = entity.getEntityBoundingBox();
						
						boolean squished = false;
		
						double dx = Math.max(collisionAABB.minX - entityAABB.maxX, entityAABB.minX - collisionAABB.maxX);
						double dz = Math.max(collisionAABB.minZ - entityAABB.maxZ, entityAABB.minZ - collisionAABB.maxZ);
		
						if(Math.abs(dz) < Math.abs(dx)) {
							entity.move(MoverType.PISTON, 0, 0, (dz - 0.005D) * Math.signum(this.posZ - entity.posZ));
		
							entityAABB = entity.getEntityBoundingBox();
							dz = Math.max(collisionAABB.minZ - entityAABB.maxZ, entityAABB.minZ - collisionAABB.maxZ);
		
							if(-dz > 0.025D) {
								squished = true;
		
								maxReverseZ = Math.max(-dz, maxReverseZ);
							}
						} else {
							entity.move(MoverType.PISTON, (dx - 0.005D) * Math.signum(this.posX - entity.posX), 0, 0);
		
							entityAABB = entity.getEntityBoundingBox();
							dx = Math.max(collisionAABB.minX - entityAABB.maxX, entityAABB.minX - collisionAABB.maxX);
		
							if(-dx > 0.025D) {
								squished = true;
		
								maxReverseX = Math.max(-dx, maxReverseX);
							}
						}
		
						//Move slightly towards ground to update onGround state etc.
						entity.move(MoverType.PISTON, 0, -0.01D, 0);
		
						if(squished) {
							collision = true;
		
							if(!this.world.isRemote) {
								entity.attackEntityFrom(DamageSource.IN_WALL, 10F);
		
								setHoldStill(true);
								holdCount = 20;
								getEntityWorld().playSound(null, getPosition(), SoundRegistry.MUD_DOOR_LOCK, SoundCategory.BLOCKS, 2F, 0.75F);
							}
						}
					}
				}
			}
		}
		
		if(collision) {
			if(maxReverseZ > 0) {
				this.posZ -= (maxReverseZ + 0.05D) * Math.signum(this.motionZ);
			}
			if(maxReverseX > 0) {
				this.posX -= (maxReverseX + 0.05D) * Math.signum(this.motionX);
			}
			
			shaking = true;
			shake_timer = 0;
			
			if(!this.world.isRemote) {
				motionX *= -1D;
				motionZ *= -1D;
				velocityChanged = true;
			}
		}
	}

	private void checkSpawnArea() {
		BlockPos posEntity = getPosition();
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(posEntity.add(-1F, -1F, -1F), posEntity.add(1F, 1F, 1F));
		for (BlockPos pos : blocks) {
			if (isUnbreakableBlock(getEntityWorld().getBlockState(pos).getBlock())) {
				setDead();
			}
		}
		if(isUnbreakableBlock(getEntityWorld().getBlockState(posEntity.add(2, 0, 0)).getBlock()) && isUnbreakableBlock(getEntityWorld().getBlockState(posEntity.add(-2, 0, 0)).getBlock())) {
			motionZ = 0.05F;
			setIsNewSpawn(false);
		}
		else if(isUnbreakableBlock(getEntityWorld().getBlockState(posEntity.add(0, 0, 2)).getBlock()) && isUnbreakableBlock(getEntityWorld().getBlockState(posEntity.add(0, 0, -2)).getBlock())) {
			motionX = 0.05F;
			setIsNewSpawn(false);
		}	
		else {
			setDead();
		}
	}

	private void doJankSafetyCheck() {
		EnumFacing facing = getHorizontalFacing();
		Vec3d vec3d = new Vec3d(getPosition());
		Vec3d vec3d1 = new Vec3d(getPosition().offset(facing, 28)); //should be long enough
		RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d, vec3d1);
		if (raytraceresult != null) {
			vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
			BlockPos hitpos = new BlockPos(vec3d1);
			AxisAlignedBB rayBox = new AxisAlignedBB(getPosition(), hitpos);
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, rayBox);
			for (int entityCount = 0; entityCount < list.size(); ++entityCount) {
				Entity entity = list.get(entityCount);
				if (entity != null && entity instanceof EntityMovingWall)
					entity.setDead();
			}
		}
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
			if (isUnbreakableBlock(state.getBlock())) { // not sure of all the different states so default will do
				if (result.sideHit.getIndex() == 2 || result.sideHit.getIndex() == 3) {
					shaking = true;
					shake_timer = 0;
					motionZ *= -1D;
					velocityChanged = true;
					if(!getEntityWorld().isRemote) {
						setHoldStill(true);
						holdCount = 20;
						getEntityWorld().playSound(null, getPosition(), SoundRegistry.MUD_DOOR_LOCK, SoundCategory.BLOCKS, 2F, 0.75F);
					}
				} else if (result.sideHit.getIndex() == 4 || result.sideHit.getIndex() == 5) {
					shaking = true;
					shake_timer = 0;
					motionX *= -1D;
					velocityChanged = true;
					if(!getEntityWorld().isRemote) {
						setHoldStill(true);
						holdCount = 20;
						getEntityWorld().playSound(null, getPosition(), SoundRegistry.MUD_DOOR_LOCK, SoundCategory.BLOCKS, 2F, 0.75F);
					}
				}
			}
			else {
				if (state.getBlock() != Blocks.BEDROCK) {
					getEntityWorld().destroyBlock(result.getBlockPos(), false);
					getEntityWorld().notifyNeighborsOfStateChange(result.getBlockPos(), state.getBlock(), true);
				}
			}
		}
	}

	public void playChainSound(World world, BlockPos pos) {
		ISound wall_slide = new MovingWallSound(this);
		Minecraft.getMinecraft().getSoundHandler().playSound(wall_slide);
	}
	
	@Override
	public void move(MoverType type, double x, double y, double z) {
		//No regular moving
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
	public void addVelocity(double x, double y, double z) {
		if (isHoldingStill()) {
			motionX = 0;
			motionY = 0;
			motionZ = 0;
		}
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

	public void setIsNewSpawn(boolean new_spawn) {
		dataManager.set(IS_NEW_SPAWN, new_spawn);
	}

	public boolean isNewSpawn() {
		return dataManager.get(IS_NEW_SPAWN);
	}

	private void setHoldStill(boolean hold_still) {
		dataManager.set(HOLD_STILL, hold_still);
	}

	public boolean isHoldingStill() {
		return dataManager.get(HOLD_STILL);
	}

	public boolean isMoving() {
		return !isHoldingStill();
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

	public boolean isUnbreakableBlock(Block block) {
		return UNBREAKABLE_BLOCKS.contains(block);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("new_spawn", Constants.NBT.TAG_BYTE))
			setIsNewSpawn(nbt.getBoolean("new_spawn"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("new_spawn", isNewSpawn());
	}

	public void shake(int shakeTimerMax) {
		shakingTimerMax = shakeTimerMax;
		prev_shake_timer = shake_timer;
		if(shake_timer == 0) {
			shaking = true;
			shake_timer = 1;
		}
		if(shake_timer > 0)
			shake_timer++;

		if(shake_timer >= shakingTimerMax)
			shaking = false;
		else
			shaking = true;
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(isShaking()) {
			double dist = getDistance(viewer);
			float shakeMult = (float) (1.0F - dist / 16.0F);
			if(dist >= 16.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(getShakingProgress(partialTicks) * Math.PI) + 0.1F) * 0.075F * shakeMult);
		} else {
			return 0.0F;
		}
	}

	public float getShakeDistance(Entity entity) {
		float distX = (float)(getPosition().getX() - entity.getPosition().getX());
		float distY = (float)(getPosition().getY() - entity.getPosition().getY());
		float distZ = (float)(getPosition().getZ() - entity.getPosition().getZ());
		return MathHelper.sqrt(distX * distX + distY * distY + distZ * distZ);
	}

	public boolean isShaking() {
		return shaking;
	}

	public float getShakingProgress(float delta) {
		return 1.0F / shakingTimerMax * (prev_shake_timer + (shake_timer - prev_shake_timer) * delta);
	}

}
