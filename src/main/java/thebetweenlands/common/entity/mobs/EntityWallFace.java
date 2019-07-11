package thebetweenlands.common.entity.mobs;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;

public abstract class EntityWallFace extends EntityCreature implements  IEntityBL {
	private static final DataParameter<EnumFacing> FACING = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.FACING);
	private static final DataParameter<EnumFacing> FACING_UP = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.FACING);
	private static final DataParameter<BlockPos> ANCHOR = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.BLOCK_POS);

	private int targetFacingTimeout = 40;
	private EnumFacing targetFacing;
	private EnumFacing targetFacingUp;

	private int targetAnchorTimeout = 40;
	private BlockPos targetAnchor;

	private static final DataParameter<Boolean> MOVING = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Float> MOVE_SPEED = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.FLOAT);
	private static final DataParameter<EnumFacing> MOVE_FACING = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.FACING);
	private static final DataParameter<EnumFacing> MOVE_FACING_UP = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.FACING);
	private static final DataParameter<BlockPos> MOVE_ANCHOR = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Boolean> ANCHORED = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.BOOLEAN);

	protected final LookHelper lookHelper;

	private float lastMoveProgress = 0;
	private float moveProgress = 0;

	protected float peek = 0.25F;

	public EntityWallFace(World world) {
		super(world);
		this.lookHelper = new LookHelper(this);
		this.moveHelper = new MoveHelper(this);
		this.setSize(0.9F, 0.9F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataManager.register(FACING, EnumFacing.NORTH);
		this.dataManager.register(FACING_UP, EnumFacing.UP);
		this.dataManager.register(ANCHOR, BlockPos.ORIGIN);

		this.dataManager.register(MOVING, false);
		this.dataManager.register(MOVE_SPEED, 1.0F);
		this.dataManager.register(MOVE_FACING, EnumFacing.NORTH);
		this.dataManager.register(MOVE_FACING_UP, EnumFacing.UP);
		this.dataManager.register(MOVE_ANCHOR, BlockPos.ORIGIN);

		this.dataManager.register(ANCHORED, true);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.dataManager.set(ANCHOR, new BlockPos(this));
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return this.isEntityInvulnerable(source) ? false : super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return source == DamageSource.IN_WALL || super.isEntityInvulnerable(source);
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		return EntityAIAttackOnCollide.useStandardAttack(this, target);
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.5F;
	}

	@Override
	protected boolean canDropLoot() {
		return true;
	}

	@Override
	public LookHelper getLookHelper() {
		return this.lookHelper;
	}

	@Override
	public MoveHelper getMoveHelper() {
		return (MoveHelper) this.moveHelper;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if(this.isServerWorld() && !this.isMovementBlocked()) {
			this.lookHelper.onUpdateLook();
		}
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
		//No knockback
	}

	@Override
	protected void setSize(float width, float height) {
		super.setSize(width, height);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getEntityBoundingBox();
	}

	@Override
	protected boolean isMovementBlocked() {
		return this.isMoving();
	}

	@Override
	public void setAIMoveSpeed(float speedIn) {
		this.dataManager.set(MOVE_SPEED, speedIn);
	}

	@Override
	public float getAIMoveSpeed() {
		return this.dataManager.get(MOVE_SPEED);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender() {
		if(!this.isAnchored()) {
			return super.getBrightnessForRender();
		}

		EnumFacing facing = this.getFacing();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(MathHelper.floor(this.posX) + facing.getXOffset(), 0, MathHelper.floor(this.posZ) + facing.getZOffset());

		if (this.world.isBlockLoaded(pos)) {
			pos.setY(MathHelper.floor(this.posY + (double)this.getEyeHeight()) + facing.getYOffset());
			return this.world.getCombinedLight(pos, 0);
		} else {
			return 0;
		}
	}

	@Override
	public EntityItem entityDropItem(ItemStack stack, float offsetY) {
		if (stack.isEmpty()) {
			return null;
		} else {
			EntityItem entityItem = new EntityItem(this.world, this.posX, this.posY + (double)offsetY, this.posZ, stack);

			EnumFacing facing = this.getFacing();
			Vec3d dropPos = this.getFrontCenter().add(facing.getXOffset() * entityItem.width, facing.getYOffset() * entityItem.height, facing.getZOffset() * entityItem.width);

			entityItem.setPosition(dropPos.x, dropPos.y, dropPos.z);

			entityItem.setDefaultPickupDelay();
			if(this.captureDrops) {
				this.capturedDrops.add(entityItem);
			} else {
				this.world.spawnEntity(entityItem);
			}
			return entityItem;
		}
	}

	@Override
	public boolean canEntityBeSeen(Entity entity) {
		Vec3d frontCenter = this.getFrontCenter();
		return this.world.rayTraceBlocks(frontCenter, new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ), false, true, false) == null;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		nbt.setInteger("facing", this.getFacing().getIndex());
		nbt.setInteger("facingUp", this.getFacingUp().getIndex());
		nbt.setLong("anchor", this.getAnchor().toLong());
		nbt.setBoolean("anchored", this.isAnchored());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		this.dataManager.set(FACING, EnumFacing.byIndex(nbt.getInteger("facing")));
		this.dataManager.set(FACING_UP, EnumFacing.byIndex(nbt.getInteger("facingUp")));
		this.dataManager.set(ANCHOR, BlockPos.fromLong(nbt.getLong("anchor")));
		if(!nbt.hasKey("anchored", Constants.NBT.TAG_BYTE)) {
			this.setAnchored(true);
		} else {
			this.setAnchored(nbt.getBoolean("anchored"));
		}
	}

	@Override
	public void onUpdate() {
		boolean isAnchored = this.dataManager.get(ANCHORED);

		if(isAnchored) {
			this.fallDistance = 0;
			this.onGround = true;
			this.setNoGravity(true);

			double px = this.posX, py = this.posY, pz = this.posZ;

			super.onUpdate();

			this.posX = this.prevPosX = this.lastTickPosX = px;
			this.posY = this.prevPosY = this.lastTickPosY = py;
			this.posZ = this.prevPosZ = this.lastTickPosZ = pz;

			this.motionX = this.motionY = this.motionZ = 0;
		} else {
			this.setNoGravity(false);

			super.onUpdate();
		}

		this.updatePositioning(isAnchored);

		this.updateMovement();
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		if(this.isAnchored()) {
			this.collided = this.collidedHorizontally = this.collidedVertically = true;
		} else {
			super.move(type, x, y, z);
		}
	}

	@Override
	public void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn) {
		if(!this.isAnchored()) {
			super.moveToBlockPosAndAngles(pos, rotationYawIn, rotationPitchIn);
		}
	}

	@Override
	public void moveRelative(float strafe, float up, float forward, float friction) {
		if(!this.isAnchored()) {
			super.moveRelative(strafe, up, forward, friction);
		}
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
		this.fallDistance = 0;
	}

	@Override
	public boolean canTrample(World world, Block block, BlockPos pos, float fallDistance) {
		return false;
	}

	protected void updatePositioning(boolean isAnchored) {
		EnumFacing facing = this.getFacing();
		EnumFacing facingUp = this.getFacingUp();

		if(isAnchored) {
			if(facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
				this.prevRotationPitch = this.rotationPitch = facing == EnumFacing.UP ? -90.0F : 90.0F;
				this.prevRenderYawOffset = this.renderYawOffset = facingUp.getHorizontalAngle() + (facing == EnumFacing.DOWN ? 0.0F : 180.0F);
			} else {
				this.prevRotationPitch = this.rotationPitch = 0;
				this.prevRenderYawOffset = this.renderYawOffset = facing.getHorizontalAngle();
			}
		} else {
			if(this.onGround && Math.abs(this.rotationPitch + 90.0f) > 1) {
				if(this.rotationPitch > -90.0f) {
					this.rotationPitch -= 25.0f;
					if(this.rotationPitch < -90.0f) {
						this.rotationPitch = -90.0f;
					}
				} else {
					this.rotationPitch += 25.0f;
					if(this.rotationPitch > -90.0f) {
						this.rotationPitch = -90.0f;
					}
				}
			}
		}

		if(!this.world.isRemote) {
			if(!this.isMoving()) {
				if(this.targetFacingTimeout > 0) {
					this.targetFacingTimeout--;
				} else {
					this.targetFacingUp = null;
					this.targetFacing = null;
				}

				if(this.targetAnchorTimeout > 0) {
					this.targetAnchorTimeout--;
				} else {
					this.targetAnchor = null;
				}

				if(!this.isMovementBlocked() && !this.isMoving() && (this.targetFacing != null || this.targetAnchor != null)) {
					EnumFacing targetFacing = this.targetFacing != null ? this.targetFacing : facing;
					EnumFacing targetFacingUp = this.targetFacingUp != null ? this.targetFacingUp : facingUp;
					BlockPos targetAnchor = this.targetAnchor != null ? this.targetAnchor : this.getAnchor();

					if(facing != targetFacing || facingUp != targetFacingUp || !this.getAnchor().equals(targetAnchor)) {
						if(this.checkAnchorAt(targetAnchor, targetFacing, targetFacingUp, AnchorChecks.ALL) == 0) {
							this.dataManager.set(MOVING, true);
							this.dataManager.set(MOVE_FACING, targetFacing);
							this.dataManager.set(MOVE_FACING_UP, targetFacingUp);
							this.dataManager.set(MOVE_ANCHOR, targetAnchor);
							this.targetFacing = null;
							this.targetFacingUp = null;
							this.targetAnchor = null;
						}
					}
				}
			}

			int violatedChecks = this.checkAnchorHere(AnchorChecks.ALL);
			if(violatedChecks != 0) {
				this.fixUnsuitablePosition(violatedChecks);
			}
		}

		if(!this.isMoving() && isAnchored) {
			Vec3d offset = this.getOffset(1);
			Vec3d position = this.getCenter().add(offset);
			this.setPosition(position.x, position.y - this.height / 2.0D, position.z);
		}
	}

	protected void updateMovement() {
		this.lastMoveProgress = this.moveProgress;

		if(this.isMoving()) {
			float movementProgress = this.getMovementProgress(1);
			if(movementProgress < 0.5F) {
				Vec3d offset = this.getOffset(movementProgress);
				Vec3d position = this.getCenter().add(offset);
				this.setPosition(position.x, position.y - this.height / 2.0D, position.z);
			} else {
				this.dataManager.set(ANCHOR, this.dataManager.get(MOVE_ANCHOR));
				this.dataManager.set(FACING, this.dataManager.get(MOVE_FACING));
				this.dataManager.set(FACING_UP, this.dataManager.get(MOVE_FACING_UP));
				this.setAnchored(true);
				Vec3d offset = this.getOffset(movementProgress);
				Vec3d position = this.getCenter().add(offset);
				double px = this.posX;
				double py = this.posY;
				double pz = this.posZ;
				this.setPosition(position.x, position.y - this.height / 2.0D, position.z);
				if((this.posX - px) * (this.posX - px) + (this.posY - py) * (this.posY - py) + (this.posZ - pz) * (this.posZ - pz) >= 1.0D) {
					this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
				}
			}
			if(this.moveProgress >= 1.0F) {
				this.dataManager.set(MOVING, false);
				this.lastMoveProgress = this.moveProgress = 0;
			} else {
				this.moveProgress += 0.05F * (this.getAIMoveSpeed() + 0.05F);
			}
		} else {
			this.moveProgress = this.lastMoveProgress = 0;
		}
	}

	public float getPeek() {
		return this.peek;
	}

	private float getHalfMovementProgressFromRegular(float movementProgress) {
		float halfProgress;
		if(movementProgress < 0.5F) {
			halfProgress = (0.5F - movementProgress) / 0.5F;
		} else {
			halfProgress = (movementProgress - 0.5F) / 0.5F;
		}
		return halfProgress;
	}

	public float getHalfMovementProgress(float partialTicks) {
		return this.getHalfMovementProgressFromRegular(this.getMovementProgress(partialTicks));
	}

	public float getMovementProgress(float partialTicks) {
		return MathHelper.clamp(this.lastMoveProgress + (this.moveProgress - this.lastMoveProgress) * partialTicks, 0, 1);
	}

	public Vec3d getOffset(float movementProgress) {
		float offsetLength = this.getHalfMovementProgressFromRegular(movementProgress);
		return new Vec3d(this.getFacing().getDirectionVec()).scale(this.getPeek() + (this.getFacing().getAxis().isHorizontal() ? (this.getBlockWidth() - this.width) : (this.getBlockHeight() - this.height)) / 2.0D).scale(offsetLength);
	}

	public int getBlockWidth() {
		return MathHelper.ceil(this.width);
	}

	public int getBlockHeight() {
		return MathHelper.ceil(this.height);
	}

	public EnumFacing getFacing() {
		return this.dataManager.get(FACING);
	}

	public EnumFacing getFacingUp() {
		return this.dataManager.get(FACING_UP);
	}

	public BlockPos getAnchor() {
		return this.dataManager.get(ANCHOR);
	}

	public boolean isMoving() {
		return this.dataManager.get(MOVING);
	}

	public Vec3d getCenter() {
		return new Vec3d(this.getAnchor()).add(this.getBlockWidth() / 2.0D, this.getBlockHeight() / 2.0D, this.getBlockWidth() / 2.0D);
	}

	public Vec3d getFrontCenter() {
		EnumFacing facing = this.getFacing();
		Vec3d center = this.getCenter();
		return center.add(this.getOffset(this.getMovementProgress(1))).add(facing.getXOffset() * this.width / 2.0F, facing.getYOffset() * this.height / 2.0F, facing.getZOffset() * this.width / 2.0F);
	}

	public boolean isAnchored() {
		return this.dataManager.get(ANCHORED);
	}

	public void setAnchored(boolean anchored) {
		this.dataManager.set(ANCHORED, anchored);
	}

	public void stopMovement() {
		this.dataManager.set(MOVING, false);
		this.lastMoveProgress = this.moveProgress = 0;
	}

	public EnumFacing[] getFacingForLookDir(Vec3d lookDir) {
		EnumFacing[] facing = new EnumFacing[2];
		EnumFacing dir = EnumFacing.getFacingFromVector((float)lookDir.x, (float)lookDir.y, (float)lookDir.z);
		facing[0] = dir;
		if(dir == EnumFacing.DOWN || dir == EnumFacing.UP) {
			facing[1] = EnumFacing.getFacingFromVector((float)lookDir.x, 0, (float)lookDir.z);
			if(dir == EnumFacing.UP) {
				facing[1] = facing[1].getOpposite();
			}
		} else {
			facing[1] = EnumFacing.UP;
		}
		return facing;
	}

	public void setPositionToAnchor(BlockPos anchor, EnumFacing facing, EnumFacing facingUp) {
		this.dataManager.set(ANCHOR, anchor);
		this.dataManager.set(FACING, facing);
		this.dataManager.set(FACING_UP, facingUp);

		this.dataManager.set(MOVING, false);
		this.lastMoveProgress = this.moveProgress = 0;

		this.setAnchored(true);
		this.stopMovement();

		this.updatePositioning(true);
	}

	public static class AnchorChecks {
		/**
		 * Checks whether the blocks around the anchor are valid
		 */
		public static final int ANCHOR_BLOCKS = 0b001;

		/**
		 * Checks whether the blocks at the entity's face at the anchor are valid
		 */
		public static final int FACE_BLOCKS = 0b010;

		/**
		 * Checks whether the entities around the anchor and the entity's face at the anchor are valid
		 */
		public static final int ENTITIES = 0b100;

		public static final int BLOCKS = ANCHOR_BLOCKS | FACE_BLOCKS;
		public static final int ALL = BLOCKS | ENTITIES;
	}

	public int checkAnchorAt(Vec3d pos, Vec3d lookDir, int checks) {
		EnumFacing[] facing = this.getFacingForLookDir(lookDir);
		BlockPos anchor = new BlockPos(pos.x - (this.getBlockWidth() / 2), pos.y - (this.getBlockHeight() / 2), pos.z - (this.getBlockWidth() / 2));
		return this.checkAnchorAt(anchor, facing[0], facing[1], checks);
	}

	public int checkAnchorAt(BlockPos anchor, EnumFacing facing, EnumFacing facingUp, int checks) {
		int violations = 0;

		if((checks & AnchorChecks.ENTITIES) != 0) {
			if(!this.world.getEntitiesWithinAABB(EntityWallFace.class, this.getEntityBoundingBox().offset(anchor.subtract(this.getAnchor())).expand(facing.getXOffset() * this.getPeek(), facing.getYOffset() * this.getPeek(), facing.getZOffset() * this.getPeek()), e -> e != this).isEmpty()) {
				violations |= AnchorChecks.ENTITIES;
			}
		}

		MutableBlockPos pos = new MutableBlockPos();

		if((checks & AnchorChecks.ANCHOR_BLOCKS) != 0) {
			outer: for(int xo = 0; xo < this.getBlockWidth(); xo++) {
				for(int yo = 0; yo < this.getBlockHeight(); yo++) {
					for(int zo = 0; zo < this.getBlockWidth(); zo++) {
						pos.setPos(anchor.getX() + xo, anchor.getY() + yo, anchor.getZ() + zo);
						if(!this.canResideInBlock(pos, facing, facingUp)) {
							violations |= AnchorChecks.ANCHOR_BLOCKS;
							break outer;
						}
					}
				}
			}
		}

		if((checks & AnchorChecks.FACE_BLOCKS) != 0) {
			if(facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
				int y = facing == EnumFacing.UP ? this.getBlockHeight() : -1;
				outer: for(int xo = 0; xo < this.getBlockWidth(); xo++) {
					for(int zo = 0; zo < this.getBlockWidth(); zo++) {
						for(int yo = 0; yo < MathHelper.ceil(this.getPeek()); yo++) {
							pos.setPos(anchor.getX() + xo, anchor.getY() + y + facing.getYOffset() * yo, anchor.getZ() + zo);
							if(!this.canMoveFaceInto(pos, facing, facingUp)) {
								violations |= AnchorChecks.FACE_BLOCKS;
								break outer;
							}
						}
					}
				}
			} else if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
				int z = facing == EnumFacing.NORTH ? -1 : this.getBlockWidth();
				outer: for(int xo = 0; xo < this.getBlockWidth(); xo++) {
					for(int yo = 0; yo < this.getBlockHeight(); yo++) {
						for(int zo = 0; zo < MathHelper.ceil(this.getPeek()); zo++) {
							pos.setPos(anchor.getX() + xo, anchor.getY() + yo, anchor.getZ() + z + facing.getZOffset() * zo);
							if(!this.canMoveFaceInto(pos, facing, facingUp)) {
								violations |= AnchorChecks.FACE_BLOCKS;
								break outer;
							}
						}
					}
				}
			} else if(facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
				int x = facing == EnumFacing.WEST ? -1 : this.getBlockWidth();
				outer: for(int zo = 0; zo < this.getBlockWidth(); zo++) {
					for(int yo = 0; yo < this.getBlockHeight(); yo++) {
						for(int xo = 0; xo < MathHelper.ceil(this.getPeek()); xo++) {
							pos.setPos(anchor.getX() + x + facing.getXOffset() * xo, anchor.getY() + yo, anchor.getZ() + zo);
							if(!this.canMoveFaceInto(pos, facing, facingUp)) {
								violations |= AnchorChecks.FACE_BLOCKS;
								break outer;
							}
						}
					}
				}
			}
		}

		return violations;
	}

	protected int checkAnchorHere(int checks) {
		return this.checkAnchorAt(this.getAnchor(), this.getFacing(), this.getFacingUp(), checks);
	}

	public abstract boolean canResideInBlock(BlockPos pos, EnumFacing facing, EnumFacing facingUp);

	public abstract boolean canMoveFaceInto(BlockPos pos, EnumFacing facing, EnumFacing facingUp);

	protected void fixUnsuitablePosition(int violatedChecks) {

	}

	public static final class LookHelper extends EntityLookHelper {
		private final EntityWallFace face;

		private int lookingMode = 0;

		private double x, y, z;

		private LookHelper(EntityWallFace entity) {
			super(entity);
			this.face = entity;
		}

		@Override
		public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch) {
			this.x = entityIn.posX;

			if (entityIn instanceof EntityLivingBase) {
				this.y = entityIn.posY + (double)entityIn.getEyeHeight();
			} else {
				this.y = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0D;
			}

			this.z = entityIn.posZ;
			this.lookingMode = 1;
		}

		@Override
		public void setLookPosition(double x, double y, double z, float deltaYaw, float deltaPitch) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.lookingMode = 1;
		}

		public void setLookDirection(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.lookingMode = 2;
		}

		@Override
		public boolean getIsLooking() {
			return this.lookingMode != 0;
		}

		@Override
		public double getLookPosX() {
			return this.x;
		}

		@Override
		public double getLookPosY() {
			return this.y;
		}

		@Override
		public double getLookPosZ() {
			return this.z;
		}

		@Override
		public void onUpdateLook() {
			if(this.lookingMode == 1) {
				Vec3d center = this.face.getCenter();
				EnumFacing[] facing = this.face.getFacingForLookDir(new Vec3d(this.x - center.x, this.y - center.y, this.z - center.z));
				this.face.targetFacingTimeout = 30 + this.face.world.rand.nextInt(30);
				this.face.targetFacing = facing[0];
				this.face.targetFacingUp = facing[1];
			} else if(this.lookingMode == 2) {
				EnumFacing[] facing = this.face.getFacingForLookDir(new Vec3d(this.x, this.y, this.z));
				this.face.targetFacingTimeout = 30 + this.face.world.rand.nextInt(30);
				this.face.targetFacing = facing[0];
				this.face.targetFacingUp = facing[1];
			}
			this.lookingMode = 0;
		}

		public void setSpeed(double speed) {
			this.face.setAIMoveSpeed((float) speed);
		}
	}

	public static class MoveHelper extends EntityMoveHelper {
		private final EntityWallFace face;

		private MoveHelper(EntityWallFace entity) {
			super(entity);
			this.face = entity;
		}

		@Override
		public void onUpdateMoveHelper() {
			if(this.action == EntityMoveHelper.Action.STRAFE && this.moveStrafe != 0) {
				Vec3i horDir = this.face.getFacing().getDirectionVec().crossProduct(this.face.getFacingUp().getDirectionVec());
				int strafeDir = -(int)Math.signum(this.moveStrafe);
				this.face.targetAnchorTimeout = 30 + this.entity.world.rand.nextInt(30);
				this.face.targetAnchor = this.face.getAnchor().add(horDir.getX() * strafeDir, horDir.getY() * strafeDir, horDir.getZ() * strafeDir);
				this.setSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
			} else if(this.action == EntityMoveHelper.Action.MOVE_TO) {
				this.face.targetAnchorTimeout = 30 + this.entity.world.rand.nextInt(30);
				this.face.targetAnchor = new BlockPos(this.posX - this.face.getBlockWidth() / 2.0D, this.posY - this.face.getBlockHeight() / 2.0D, this.posZ - this.face.getBlockWidth() / 2.0D);
				this.setSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
			}
			this.action = EntityMoveHelper.Action.WAIT;
		}

		public void setSpeed(double speed) {
			this.face.setAIMoveSpeed((float) speed);
		}
	}
}
