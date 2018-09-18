package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;

public abstract class EntityWallFace extends EntityCreature implements IMob, IEntityBL {
	private static final DataParameter<EnumFacing> FACING = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.FACING);
	private static final DataParameter<EnumFacing> FACING_UP = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.FACING);
	private static final DataParameter<BlockPos> ANCHOR = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.BLOCK_POS);

	private EnumFacing targetFacing;
	private EnumFacing targetFacingUp;
	private BlockPos targetAnchor;

	private static final DataParameter<Boolean> MOVING = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Float> MOVE_SPEED = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.FLOAT);
	private static final DataParameter<EnumFacing> MOVE_FACING = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.FACING);
	private static final DataParameter<EnumFacing> MOVE_FACING_UP = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.FACING);
	private static final DataParameter<BlockPos> MOVE_ANCHOR = EntityDataManager.createKey(EntityWallFace.class, DataSerializers.BLOCK_POS);

	protected final LookHelper lookHelper;

	private float lastMoveTicks = 0;
	private float moveTicks = 0;

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
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
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
		EnumFacing facing = this.getFacing();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(MathHelper.floor(this.posX) + facing.getFrontOffsetX(), 0, MathHelper.floor(this.posZ) + facing.getFrontOffsetZ());

		if (this.world.isBlockLoaded(pos)) {
			pos.setY(MathHelper.floor(this.posY + (double)this.getEyeHeight()) + facing.getFrontOffsetY());
			return this.world.getCombinedLight(pos, 0);
		} else {
			return 0;
		}
	}

	@Override
	public boolean canEntityBeSeen(Entity entity) {
		EnumFacing facing = this.getFacing();
		return this.world.rayTraceBlocks(new Vec3d(this.posX + facing.getFrontOffsetX() * this.width / 2, this.posY + this.height / 2 + facing.getFrontOffsetY() * this.height / 2, this.posZ + facing.getFrontOffsetZ() * this.width / 2), new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ), false, true, false) == null;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		nbt.setInteger("facing", this.getFacing().getIndex());
		nbt.setInteger("facingUp", this.getFacing().getIndex());
		nbt.setLong("anchor", this.getAnchor().toLong());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		this.dataManager.set(FACING, EnumFacing.getFront(nbt.getInteger("facing")));
		this.dataManager.set(FACING_UP, EnumFacing.getFront(nbt.getInteger("facingUp")));
		this.dataManager.set(ANCHOR, BlockPos.fromLong(nbt.getLong("anchor")));
	}

	@Override
	public void onUpdate() {
		if(!this.world.isRemote) {
			if(!this.isActive()) {
				this.setEntityInvulnerable(true);
			} else {
				this.setEntityInvulnerable(false);
			}
		}

		this.fallDistance = 0;
		this.onGround = true;
		this.setNoGravity(true);

		double px = this.posX, py = this.posY, pz = this.posZ;

		super.onUpdate();

		this.posX = this.prevPosX = this.lastTickPosX = px;
		this.posY = this.prevPosY = this.lastTickPosY = py;
		this.posZ = this.prevPosZ = this.lastTickPosZ = pz;

		this.motionX = this.motionY = this.motionZ = 0;

		this.updatePositioning();

		this.updateMovement();
	}

	protected void updatePositioning() {
		EnumFacing facing = this.getFacing();
		EnumFacing facingUp = this.getFacingUp();

		if(facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
			this.prevRotationPitch = this.rotationPitch = facing == EnumFacing.UP ? -90.0F : 90.0F;
			this.prevRenderYawOffset = this.renderYawOffset = facingUp.getHorizontalAngle();
		} else {
			this.prevRotationPitch = this.rotationPitch = 0;
			this.prevRenderYawOffset = this.renderYawOffset = facing.getHorizontalAngle();
		}

		if(!this.isMoving()) {
			if(!this.world.isRemote) {
				if(this.isActive() && (this.targetFacing != null || this.targetAnchor != null)) {
					EnumFacing targetFacing = this.targetFacing != null ? this.targetFacing : facing;
					EnumFacing targetFacingUp = this.targetFacingUp != null ? this.targetFacingUp : facingUp;
					BlockPos targetAnchor = this.targetAnchor != null ? this.targetAnchor : this.getAnchor();

					if(facing != targetFacing || facingUp != targetFacingUp || !this.getAnchor().equals(targetAnchor)) {
						if(this.canAnchorAt(targetAnchor, targetFacing, targetFacingUp)) {
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

				if(!this.canStayHere()) {
					this.fixUnsuitablePosition();
				}
			}

			Vec3d offset = this.getOffset(1);
			Vec3d position = this.getCenter().add(offset);
			this.setPosition(position.x, position.y - this.height / 2.0D, position.z);
		}
	}

	protected void updateMovement() {
		this.lastMoveTicks = this.moveTicks;

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
			if(this.moveTicks >= 1.0F) {
				this.dataManager.set(MOVING, false);
				this.lastMoveTicks = this.moveTicks = 0;
			} else {
				this.moveTicks += 0.05F * (this.getAIMoveSpeed() + 0.05F);
			}
		} else {
			this.moveTicks = this.lastMoveTicks = 0;
		}
	}

	public float getPeek() {
		return this.peek;
	}

	public float getHalfMovementProgress(float partialTicks) {
		float movementProgress = this.getMovementProgress(partialTicks);
		float halfProgress;
		if(movementProgress < 0.5F) {
			halfProgress = (0.5F - movementProgress) / 0.5F;
		} else {
			halfProgress = (movementProgress - 0.5F) / 0.5F;
		}
		return halfProgress;
	}

	public float getMovementProgress(float partialTicks) {
		return MathHelper.clamp(this.lastMoveTicks + (this.moveTicks - this.lastMoveTicks) * partialTicks, 0, 1);
	}

	public Vec3d getOffset(float movementProgress) {
		float offsetLength = this.getHalfMovementProgress(1);
		return new Vec3d(this.getFacing().getDirectionVec()).scale(this.getPeek() + (this.getFacing().getAxis().isHorizontal() ? (this.getBlockWidth() - this.width) : (this.getBlockHeight() - this.height)) / 2.0D).scale(offsetLength);
	}

	public int getBlockWidth() {
		return MathHelper.ceil(this.width);
	}

	public int getBlockHeight() {
		return MathHelper.ceil(this.height);
	}

	public boolean isActive() {
		return true;
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
		return new Vec3d(this.getAnchor()).addVector(this.getBlockWidth() / 2.0D, this.getBlockHeight() / 2.0D, this.getBlockWidth() / 2.0D);
	}

	public Vec3d getFrontCenter() {
		EnumFacing facing = this.getFacing();
		Vec3d center = this.getCenter();
		return center.add(this.getOffset(this.getMovementProgress(1))).addVector(facing.getFrontOffsetX() * this.width / 2.0F, facing.getFrontOffsetY() * this.height / 2.0F, facing.getFrontOffsetZ() * this.width / 2.0F);
	}

	public EnumFacing[] getFacingForLookDir(Vec3d lookDir) {
		EnumFacing[] facing = new EnumFacing[2];
		EnumFacing dir = EnumFacing.getFacingFromVector((float)lookDir.x, (float)lookDir.y, (float)lookDir.z);
		facing[0] = dir;
		if(dir == EnumFacing.DOWN || dir == EnumFacing.UP) {
			facing[1] = EnumFacing.getFacingFromVector((float)lookDir.x, 0, (float)lookDir.z);
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
		this.lastMoveTicks = this.moveTicks = 0;

		this.updatePositioning();
	}

	public boolean canAnchorAt(Vec3d pos, Vec3d lookPos) {
		EnumFacing[] facing = this.getFacingForLookDir(lookPos.subtract(this.getCenter()));
		BlockPos anchor = new BlockPos(pos.x - (this.getBlockWidth() / 2), pos.y - (this.getBlockHeight() / 2), pos.z - (this.getBlockWidth() / 2));
		return this.canAnchorAt(anchor, facing[0], facing[1]);
	}

	public boolean canAnchorAt(BlockPos anchor, EnumFacing facing, EnumFacing facingUp) {
		if(!this.world.getEntitiesWithinAABB(EntityWallFace.class, this.getEntityBoundingBox().offset(anchor.subtract(this.getAnchor())).expand(facing.getFrontOffsetX() * this.getPeek(), facing.getFrontOffsetY() * this.getPeek(), facing.getFrontOffsetZ() * this.getPeek()), e -> e != this).isEmpty()) {
			return false;
		}
		MutableBlockPos pos = new MutableBlockPos();
		for(int xo = 0; xo < this.getBlockWidth(); xo++) {
			for(int yo = 0; yo < this.getBlockHeight(); yo++) {
				for(int zo = 0; zo < this.getBlockWidth(); zo++) {
					pos.setPos(anchor.getX() + xo, anchor.getY() + yo, anchor.getZ() + zo);
					if(!this.canResideInBlock(pos)) {
						return false;
					}
				}
			}
		}
		if(facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
			int y = facing == EnumFacing.UP ? this.getBlockHeight() : -1;
			for(int xo = 0; xo < this.getBlockWidth(); xo++) {
				for(int zo = 0; zo < this.getBlockWidth(); zo++) {
					for(int yo = 0; yo < MathHelper.ceil(this.getPeek()); yo++) {
						pos.setPos(anchor.getX() + xo, anchor.getY() + y + facing.getFrontOffsetY() * yo, anchor.getZ() + zo);
						if(!this.canMoveFaceInto(pos)) {
							return false;
						}
					}
				}
			}
		} else if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
			int z = facing == EnumFacing.NORTH ? -1 : this.getBlockWidth();
			for(int xo = 0; xo < this.getBlockWidth(); xo++) {
				for(int yo = 0; yo < this.getBlockHeight(); yo++) {
					for(int zo = 0; zo < MathHelper.ceil(this.getPeek()); zo++) {
						pos.setPos(anchor.getX() + xo, anchor.getY() + yo, anchor.getZ() + z + facing.getFrontOffsetZ() * zo);
						if(!this.canMoveFaceInto(pos)) {
							return false;
						}
					}
				}
			}
		} else if(facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
			int x = facing == EnumFacing.WEST ? -1 : this.getBlockWidth();
			for(int zo = 0; zo < this.getBlockWidth(); zo++) {
				for(int yo = 0; yo < this.getBlockHeight(); yo++) {
					for(int xo = 0; xo < MathHelper.ceil(this.getPeek()); xo++) {
						pos.setPos(anchor.getX() + x + facing.getFrontOffsetX() * xo, anchor.getY() + yo, anchor.getZ() + zo);
						if(!this.canMoveFaceInto(pos)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	protected boolean canStayHere() {
		return this.canAnchorAt(this.getAnchor(), this.getFacing(), this.getFacingUp());
	}

	public abstract boolean canResideInBlock(BlockPos pos);

	public abstract boolean canMoveFaceInto(BlockPos pos);

	protected void fixUnsuitablePosition() {

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
				this.face.targetFacing = facing[0];
				this.face.targetFacingUp = facing[1];
			} else if(this.lookingMode == 2) {
				EnumFacing[] facing = this.face.getFacingForLookDir(new Vec3d(this.x, this.y, this.z));
				this.face.targetFacing = facing[0];
				this.face.targetFacingUp = facing[1];
			}
			this.lookingMode = 0;
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
				this.face.targetAnchor = this.face.getAnchor().add(horDir.getX() * strafeDir, horDir.getY() * strafeDir, horDir.getZ() * strafeDir);
				this.face.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
			} else if(this.action == EntityMoveHelper.Action.MOVE_TO) {
				this.face.targetAnchor = new BlockPos(this.posX, this.posY, this.posZ).add(-(this.face.getBlockWidth() / 2),  -(this.face.getBlockHeight() / 2),  -(this.face.getBlockWidth() / 2));
				this.face.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
			}
			this.action = EntityMoveHelper.Action.WAIT;
		}
	}
}
