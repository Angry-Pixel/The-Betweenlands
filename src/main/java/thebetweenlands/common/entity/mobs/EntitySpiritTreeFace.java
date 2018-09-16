package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.IMob;
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
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.registries.BlockRegistry;

public class EntitySpiritTreeFace extends EntityCreature implements IMob {
	private static final DataParameter<EnumFacing> FACING = EntityDataManager.createKey(EntitySpiritTreeFace.class, DataSerializers.FACING);
	private static final DataParameter<EnumFacing> FACING_UP = EntityDataManager.createKey(EntitySpiritTreeFace.class, DataSerializers.FACING);
	private static final DataParameter<BlockPos> ANCHOR = EntityDataManager.createKey(EntitySpiritTreeFace.class, DataSerializers.BLOCK_POS);

	private EnumFacing targetFacing;
	private EnumFacing targetFacingUp;
	private BlockPos targetAnchor;

	private static final DataParameter<Boolean> MOVING = EntityDataManager.createKey(EntitySpiritTreeFace.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Float> MOVE_SPEED = EntityDataManager.createKey(EntitySpiritTreeFace.class, DataSerializers.FLOAT);
	private static final DataParameter<EnumFacing> MOVE_FACING = EntityDataManager.createKey(EntitySpiritTreeFace.class, DataSerializers.FACING);
	private static final DataParameter<EnumFacing> MOVE_FACING_UP = EntityDataManager.createKey(EntitySpiritTreeFace.class, DataSerializers.FACING);
	private static final DataParameter<BlockPos> MOVE_ANCHOR = EntityDataManager.createKey(EntitySpiritTreeFace.class, DataSerializers.BLOCK_POS);

	private final LookHelper lookHelper;

	private static final int MAX_MOVE_TICKS = 40;
	private int moveTicks = 0;

	private int blockWidth, blockHeight;

	protected float peek = 0.25F;

	public EntitySpiritTreeFace(World world) {
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
	public EntityLookHelper getLookHelper() {
		return this.lookHelper;
	}

	@Override
	public void onLivingUpdate() {
		this.lookHelper.onUpdateLook();

		super.onLivingUpdate();
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
		//No knockback
	}

	@Override
	protected void setSize(float width, float height) {
		super.setSize(width, height);
		this.blockWidth = MathHelper.ceil(width);
		this.blockHeight = MathHelper.ceil(height);
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

		double px = this.posX, py = this.posY, pz = this.posZ;

		super.onUpdate();

		this.posX = this.prevPosX = this.lastTickPosX = px;
		this.posY = this.prevPosY = this.lastTickPosY = py;
		this.posZ = this.prevPosZ = this.lastTickPosZ = pz;

		this.motionX = this.motionY = this.motionZ = 0;

		if(!this.isMoving()) {
			if(!this.world.isRemote) {
				if(this.isActive() && (this.targetFacing != null || this.targetAnchor != null)) {
					EnumFacing targetFacing = this.targetFacing != null ? this.targetFacing : this.getFacing();
					EnumFacing targetFacingUp = this.targetFacingUp != null ? this.targetFacingUp : this.getFacingUp();
					BlockPos targetAnchor = this.targetAnchor != null ? this.targetAnchor : this.getAnchor();

					if(this.getFacing() != targetFacing || this.getFacingUp() != targetFacingUp || !this.getAnchor().equals(targetAnchor)) {
						if(this.canAnchorAt(targetAnchor, targetFacing, targetFacingUp)) {
							this.dataManager.set(MOVING, true);
							this.dataManager.set(MOVE_SPEED, (float)this.moveHelper.getSpeed());
							this.dataManager.set(MOVE_FACING, targetFacing);
							this.dataManager.set(MOVE_FACING_UP, targetFacingUp);
							this.dataManager.set(MOVE_ANCHOR, targetAnchor);
							this.targetFacing = null;
							this.targetFacingUp = null;
							this.targetAnchor = null;
						}
					}
				}

				if(!this.canStay()) {
					this.fixUnsuitablePosition();
				}
			}

			Vec3d offset = this.getOffset(1);
			this.setPosition(this.getAnchor().getX() + this.blockWidth / 2.0D + offset.x, this.getAnchor().getY() + this.blockHeight / 2.0D - this.height / 2.0D + offset.y, this.getAnchor().getZ() + this.blockWidth / 2.0D + offset.z);
		} else {
			this.updateMovement();
		}
	}

	protected void updateMovement() {
		float movementProgress = this.getMovementProgress();
		if(movementProgress < 0.5F) {
			Vec3d offset = this.getOffset(movementProgress);
			this.setPosition(this.getAnchor().getX() + this.blockWidth / 2.0D + offset.x, this.getAnchor().getY() + this.blockHeight / 2.0D - this.height / 2.0D + offset.y, this.getAnchor().getZ() + this.blockWidth / 2.0D + offset.z);
		} else {
			this.dataManager.set(ANCHOR, this.dataManager.get(MOVE_ANCHOR));
			this.dataManager.set(FACING, this.dataManager.get(MOVE_FACING));
			this.dataManager.set(FACING_UP, this.dataManager.get(MOVE_FACING_UP));
			Vec3d offset = this.getOffset(movementProgress);
			double px = this.posX;
			double py = this.posY;
			double pz = this.posZ;
			this.setPosition(this.getAnchor().getX() + this.blockWidth / 2.0D + offset.x, this.getAnchor().getY() + this.blockHeight / 2.0D - this.height / 2.0D + offset.y, this.getAnchor().getZ() + this.blockWidth / 2.0D + offset.z);
			if((this.posX - px) * (this.posX - px) + (this.posY - py) * (this.posY - py) + (this.posZ - pz) * (this.posZ - pz) >= 1.0D) {
				this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
			}
		}
		if(this.moveTicks >= (int)(MAX_MOVE_TICKS / 2.0F / (this.getAIMoveSpeed() + 0.05F))) {
			this.dataManager.set(MOVING, false);
			this.moveTicks = 0;
		} else {
			this.moveTicks++;
		}
	}

	public float getMovementProgress() {
		return MathHelper.clamp(this.moveTicks / (MAX_MOVE_TICKS / 2.0F / (this.getAIMoveSpeed() + 0.05F)), 0, 1);
	}

	public Vec3d getOffset(float movementProgress) {
		float offsetLength;
		if(movementProgress < 0.5F) {
			offsetLength = (0.5F - movementProgress) / 0.5F;
		} else {
			offsetLength = (movementProgress - 0.5F) / 0.5F;
		}
		return new Vec3d(this.getFacing().getDirectionVec()).scale(this.peek + (this.getFacing().getAxis().isHorizontal() ? (this.blockWidth - this.width) : (this.blockHeight - this.height)) / 2.0D).scale(offsetLength);
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

	public boolean canAnchorAt(BlockPos anchor, EnumFacing facing, EnumFacing facingUp) {
		if(!this.world.getEntitiesWithinAABB(EntitySpiritTreeFace.class, this.getEntityBoundingBox().offset(anchor.subtract(this.getAnchor())).expand(facing.getFrontOffsetX() * this.peek, facing.getFrontOffsetY() * this.peek, facing.getFrontOffsetZ() * this.peek), e -> e != this).isEmpty()) {
			return false;
		}
		MutableBlockPos pos = new MutableBlockPos();
		for(int xo = 0; xo < this.blockWidth; xo++) {
			for(int yo = 0; yo < this.blockHeight; yo++) {
				for(int zo = 0; zo < this.blockWidth; zo++) {
					pos.setPos(anchor.getX() + xo, anchor.getY() + yo, anchor.getZ() + zo);
					if(!this.isSuitableWood(pos)) {
						return false;
					}
				}
			}
		}
		if(facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
			int y = facing == EnumFacing.UP ? this.blockHeight : -1;
			for(int xo = 0; xo < this.blockWidth; xo++) {
				for(int zo = 0; zo < this.blockWidth; zo++) {
					for(int yo = 0; yo < MathHelper.ceil(this.peek); yo++) {
						pos.setPos(anchor.getX() + xo, anchor.getY() + y + facing.getFrontOffsetY() * yo, anchor.getZ() + zo);
						if(!this.canMoveInto(pos)) {
							return false;
						}
					}
				}
			}
		} else if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
			int z = facing == EnumFacing.NORTH ? -1 : this.blockWidth;
			for(int xo = 0; xo < this.blockWidth; xo++) {
				for(int yo = 0; yo < this.blockHeight; yo++) {
					for(int zo = 0; zo < MathHelper.ceil(this.peek); zo++) {
						pos.setPos(anchor.getX() + xo, anchor.getY() + yo, anchor.getZ() + z + facing.getFrontOffsetZ() * zo);
						if(!this.canMoveInto(pos)) {
							return false;
						}
					}
				}
			}
		} else if(facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
			int x = facing == EnumFacing.WEST ? -1 : this.blockWidth;
			for(int zo = 0; zo < this.blockWidth; zo++) {
				for(int yo = 0; yo < this.blockHeight; yo++) {
					for(int xo = 0; xo < MathHelper.ceil(this.peek); xo++) {
						pos.setPos(anchor.getX() + x + facing.getFrontOffsetX() * xo, anchor.getY() + yo, anchor.getZ() + zo);
						if(!this.canMoveInto(pos)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	protected boolean canStay() {
		return this.canAnchorAt(this.getAnchor(), this.getFacing(), this.getFacingUp());
	}

	public boolean isSuitableWood(BlockPos pos) {
		return this.world.getBlockState(pos).getBlock() == BlockRegistry.LOG_SPIRIT_TREE;
	}

	public boolean canMoveInto(BlockPos pos) {
		return this.world.isAirBlock(pos);
	}

	protected void fixUnsuitablePosition() {
		if(this.ticksExisted % 10 == 0) {
			for(int i = 0; i < 160; i++) {
				float rx = this.world.rand.nextFloat() * 2 - 1;
				float ry = this.world.rand.nextFloat() * 2 - 1;
				float rz = this.world.rand.nextFloat() * 2 - 1;
				EnumFacing rndDir = EnumFacing.getFacingFromVector(rx, ry, rz);
				BlockPos rndPos = new BlockPos(this.posX + this.world.rand.nextInt(8) - 4, this.posY + this.height / 2 + this.world.rand.nextInt(8) - 4, this.posZ + this.world.rand.nextInt(8) - 4);
				if(this.canAnchorAt(rndPos, rndDir, EnumFacing.UP)) {
					this.lookHelper.setLookPosition(this.posX + rx, this.posY + this.getEyeHeight() + ry, this.posZ + rz, 0, 0);
					this.moveHelper.setMoveTo(rndPos.getX(), rndPos.getY(), rndPos.getZ(), 1);
				}
			}
		}
	}

	private static final class LookHelper extends EntityLookHelper {
		private final EntitySpiritTreeFace face;

		private boolean isLooking;
		private double posX, posY, posZ;

		public LookHelper(EntitySpiritTreeFace entity) {
			super(entity);
			this.face = entity;
		}

		@Override
		public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch) {
			this.posX = entityIn.posX;

			if (entityIn instanceof EntityLivingBase) {
				this.posY = entityIn.posY + (double)entityIn.getEyeHeight();
			} else {
				this.posY = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0D;
			}

			this.posZ = entityIn.posZ;
			this.isLooking = true;
		}

		@Override
		public void setLookPosition(double x, double y, double z, float deltaYaw, float deltaPitch) {
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			this.isLooking = true;
		}

		@Override
		public boolean getIsLooking() {
			return this.isLooking;
		}

		@Override
		public double getLookPosX() {
			return this.posX;
		}

		@Override
		public double getLookPosY() {
			return this.posY;
		}

		@Override
		public double getLookPosZ() {
			return this.posZ;
		}

		@Override
		public void onUpdateLook() {
			if(this.isLooking) {
				this.isLooking = false;
				this.face.targetFacing = EnumFacing.getFacingFromVector((float)(this.posX - this.face.posX), (float)(this.posY - (this.face.posY + this.face.getEyeHeight())), (float)(this.posZ - this.face.posZ));
				if(this.face.targetFacing == EnumFacing.DOWN || this.face.targetFacing == EnumFacing.UP) {
					this.face.targetFacingUp = EnumFacing.getFacingFromVector((float)(this.posX - this.face.posX), 0, (float)(this.posZ - this.face.posZ)).getOpposite();
				} else {
					this.face.targetFacingUp = EnumFacing.UP;
				}
			}
		}
	}

	private static class MoveHelper extends EntityMoveHelper {
		private final EntitySpiritTreeFace face;

		public MoveHelper(EntitySpiritTreeFace entity) {
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
				this.face.targetAnchor = new BlockPos(this.posX, this.posY, this.posZ);
				this.face.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
			}
			this.action = EntityMoveHelper.Action.WAIT;
		}
	}
}
