package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.capability.decay.DecayStats;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.entity.projectiles.EntitySludgeWallJet;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

//TODO Loot tables
public class EntityWallLamprey extends EntityMovingWallFace implements IMob {
	public static final byte EVENT_START_THE_SUCC = 80;

	private static final DataParameter<Boolean> HIDDEN = EntityDataManager.createKey(EntityWallLamprey.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Float> LOOK_X = EntityDataManager.createKey(EntityWallLamprey.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> LOOK_Y = EntityDataManager.createKey(EntityWallLamprey.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> LOOK_Z = EntityDataManager.createKey(EntityWallLamprey.class, DataSerializers.FLOAT);

	private float prevHiddenPercent = 1.0F;
	private float hiddenPercent = 1.0F;

	private Vec3d prevHeadLook = Vec3d.ZERO;
	private Vec3d headLook = Vec3d.ZERO;

	private boolean clientHeadLookChanged = false;

	private int suckTimer = 0;

	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite wallSprite;

	public EntityWallLamprey(World world) {
		super(world);
		this.lookMoveSpeedMultiplier = 15.0F;
		this.experienceValue = 5;
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataManager.register(HIDDEN, true);
		this.dataManager.register(LOOK_X, 0.0F);
		this.dataManager.register(LOOK_Y, 0.0F);
		this.dataManager.register(LOOK_Z, 0.0F);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.targetTasks.addTask(0, new EntityAIHurtByTargetImproved(this, false));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true).setUnseenMemoryTicks(120));

		this.tasks.addTask(0, new AITrackTargetLamprey(this, true, 28.0D));
		this.tasks.addTask(1, new AIAttackMelee(this, 1, true));
		this.tasks.addTask(2, new AISuck(this));
		this.tasks.addTask(3, new AISpit(this, 3.0F));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.08D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);

		if(key == LOOK_X || key == LOOK_Y || key == LOOK_Z) {
			this.clientHeadLookChanged = true;
		}
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || this.isSucking();
	}

	@Override
	public void onUpdate() {
		this.prevHeadLook = this.headLook;

		super.onUpdate();

		if(this.clientHeadLookChanged) {
			this.headLook = new Vec3d(this.dataManager.get(LOOK_X), this.dataManager.get(LOOK_Y), this.dataManager.get(LOOK_Z));
			this.clientHeadLookChanged = false;
		}

		if(!this.world.isRemote) {
			EntityLivingBase attackTarget = this.getAttackTarget();

			this.dataManager.set(HIDDEN, attackTarget == null);

			if(attackTarget != null) {
				this.setHeadLook(attackTarget.getPositionEyes(1).subtract(this.getPositionEyes(1)));
			} else {
				this.setHeadLook(new Vec3d(this.getFacing().getDirectionVec()));
			}
		} else {
			this.prevHiddenPercent = this.hiddenPercent;

			if(this.dataManager.get(HIDDEN)) {
				if(this.hiddenPercent < 1.0F) {
					this.hiddenPercent += 0.01F;
					if(this.hiddenPercent > 1.0F) {
						this.hiddenPercent = 1.0F;
					}
				}
			} else {
				if(this.hiddenPercent > 0.0F) {
					this.hiddenPercent -= 0.04F;
					if(this.hiddenPercent < 0.0F) {
						this.hiddenPercent = 0.0F;
					}
				}
			}

			this.updateWallSprite();
		}

		if(this.isSucking()) {
			this.suckTimer--;

			if(!this.world.isRemote) {
				List<Entity> affectedEntities = (List<Entity>)this.world.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(6.0F, 6.0F, 6.0F));
				for(Entity e : affectedEntities) {
					if(e == this || e.getDistance(this) > 6.0F || !this.canEntityBeSeen(e) || e instanceof IEntityBL) {
						continue;
					}
					Vec3d vec = new Vec3d(this.posX - e.posX, this.posY - e.posY, this.posZ - e.posZ);
					vec = vec.normalize();
					float dst = e.getDistance(this);
					float mod = (float) Math.pow(1.0F - dst / 7.0F, 1.2D);
					if(e instanceof EntityPlayer) {
						if(((EntityPlayer)e).isActiveItemStackBlocking()) mod *= 0.18F;
					}
					e.motionX += vec.x * 0.05F * mod;
					e.motionY += vec.y * 0.05F * mod;
					e.motionZ += vec.z * 0.05F * mod;
					e.velocityChanged = true;
				}
			} else {
				Vec3d fwd = this.getHeadLook(1);
				Vec3d up = new Vec3d(this.getFacingUp().getDirectionVec());
				Vec3d right = fwd.crossProduct(up);

				Vec3d front = this.getFrontCenter().add(fwd.scale(0.3D)).add(up.scale(-0.3D));

				for(int i = 0; i < 3; i++) {
					Random rnd = this.world.rand;

					Vec3d vec = fwd.scale(rnd.nextFloat() * 5).add(up.scale((rnd.nextFloat() - 0.5F) * 1.2F)).add(right.scale((rnd.nextFloat() - 0.5F) * 1.2F));

					float rx = (float)vec.x;
					float ry = (float)vec.y;
					float rz = (float)vec.z;

					vec = vec.normalize();

					this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, front.x + rx, front.y + ry, front.z + rz, -vec.x * 0.5F, -vec.y * 0.5F, -vec.z * 0.5F);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void updateWallSprite() {
		this.wallSprite = null;

		BlockPos pos = this.getPosition();

		IBlockState state = this.world.getBlockState(pos);
		state = state.getActualState(this.world, pos);

		if(state.isFullCube()) {
			this.wallSprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
		}
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getWallSprite() {
		return this.wallSprite;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		boolean hasAttacked = false;

		IDecayCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
		if(cap != null && cap.isDecayEnabled()) {
			if(EntityAIAttackOnCollide.useStandardAttack(this, entity, 0.001F)) {
				hasAttacked = true;

				DecayStats stats = cap.getDecayStats();

				stats.addDecayAcceleration((float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 2.0F);
			}
		} else {
			hasAttacked = super.attackEntityAsMob(entity);
		}

		if(hasAttacked) {
			this.playSound(SoundRegistry.WALL_LAMPREY_ATTACK, 1, 1);
		}

		return hasAttacked;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.WALL_LAMPREY;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, EnumFacing facing, EnumFacing facingUp) {
		return this.isValidBlockForMovement(pos, this.world.getBlockState(pos)) && this.isValidBlockForMovement(pos.offset(facingUp.getOpposite()), this.world.getBlockState(pos.offset(facingUp.getOpposite())));
	}

	@Override
	public int checkAnchorAt(BlockPos anchor, EnumFacing facing, EnumFacing facingUp, int checks) {
		int violations = super.checkAnchorAt(anchor, facing, facingUp, checks);

		//Check "below" (relative to facingUp) for entities
		if((checks & AnchorChecks.ENTITIES) != 0) {
			if(!this.world.getEntitiesWithinAABB(EntityWallFace.class, this.getEntityBoundingBox().offset(anchor.subtract(this.getAnchor()).offset(facingUp.getOpposite())).expand(facing.getXOffset() * this.getPeek(), facing.getYOffset() * this.getPeek(), facing.getZOffset() * this.getPeek()), e -> e != this).isEmpty()) {
				violations |= AnchorChecks.ENTITIES;
			}
		}

		return violations;
	}

	@Override
	protected boolean isValidBlockForMovement(BlockPos pos, IBlockState state) {
		return state.isOpaqueCube() && state.isNormalCube() && state.isFullCube() && state.getBlockHardness(this.world, pos) > 0 && state.getMaterial() == Material.ROCK;
	}

	@Override
	public Vec3d getOffset(float movementProgress) {
		return super.getOffset(1.0F);
	}

	public float getHoleDepthPercent(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	public float getLampreyHiddenPercent(float partialTicks) {
		return 1 - (1 - this.easeInOut(this.prevHiddenPercent + (this.hiddenPercent - this.prevHiddenPercent) * partialTicks)) * this.getHoleDepthPercent(partialTicks);
	}

	private float easeInOut(float percent) {
		float sq = percent * percent;
		return sq / (2.0f * (sq - percent) + 1.0f);
	}

	public void setHeadLook(Vec3d look) {
		look = look.normalize();
		Vec3d curr = this.headLook;
		if(Math.abs(curr.x - look.x) >= 0.01F || Math.abs(curr.y - look.y) >= 0.01F || Math.abs(curr.z - look.z) >= 0.01F) {
			if(!this.world.isRemote) {
				this.dataManager.set(LOOK_X, (float) look.x);
				this.dataManager.set(LOOK_Y, (float) look.y);
				this.dataManager.set(LOOK_Z, (float) look.z);
			}
			this.headLook = look;
		}
	}

	public Vec3d getHeadLook(float partialTicks) {
		return new Vec3d(
				this.prevHeadLook.x + (this.headLook.x - this.prevHeadLook.x) * partialTicks,
				this.prevHeadLook.y + (this.headLook.y - this.prevHeadLook.y) * partialTicks,
				this.prevHeadLook.z + (this.headLook.z - this.prevHeadLook.z) * partialTicks
				);
	}

	public float[] getRelativeHeadLookAngles(float partialTicks) {
		Vec3d headLook = this.getHeadLook(partialTicks);

		Vec3d fwdAxis = new Vec3d(this.getFacing().getDirectionVec());
		Vec3d upAxis = new Vec3d(this.getFacingUp().getDirectionVec());
		Vec3d rightAxis = fwdAxis.crossProduct(upAxis);

		double fwd = fwdAxis.dotProduct(headLook);
		double up = upAxis.dotProduct(headLook);
		double right = rightAxis.dotProduct(headLook);

		return new float[] {(float)Math.toDegrees(Math.atan2(right, fwd)), (float)Math.toDegrees(Math.atan2(fwd, up)) * (float)Math.signum(fwd) - 90.0F};
	}

	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_START_THE_SUCC) {
			this.startSucking();
		}
	}

	public void startSucking() {
		if(!this.world.isRemote) {
			this.world.setEntityState(this, EVENT_START_THE_SUCC);
			this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.WALL_LAMPREY_SUCK, SoundCategory.HOSTILE, 0.8F, this.world.rand.nextFloat() * 0.3F + 0.8F);
		}
		this.suckTimer = 30;
	}

	public boolean isSucking() {
		return this.suckTimer > 0;
	}

	public void startSpit(float spitDamage) {
		Entity target = this.getAttackTarget();
		if(target != null) {
			EnumFacing facing = this.getFacing();

			EntitySludgeWallJet jet = new EntitySludgeWallJet(this.world, this);
			jet.setPosition(this.posX + facing.getXOffset() * (this.width / 2 + 0.1F), this.posY + this.height / 2.0F + facing.getYOffset() * (this.height / 2 + 0.1F), this.posZ + facing.getZOffset() * (this.width / 2 + 0.1F));

			double dx = target.posX - jet.posX;
			double dy = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - jet.posY;
			double dz = target.posZ - jet.posZ;
			double dist = (double)MathHelper.sqrt(dx * dx + dz * dz);
			jet.shoot(dx, dy + dist * 0.2D, dz, 1, 1);

			this.world.spawnEntity(jet);
		}
	}

	public static class AITrackTargetLamprey extends AITrackTarget<EntityWallLamprey> {
		public AITrackTargetLamprey(EntityWallLamprey entity, boolean stayInRange, double maxRange) {
			super(entity, stayInRange, maxRange);
		}

		public AITrackTargetLamprey(EntityWallLamprey entity) {
			super(entity);
		}

		@Override
		protected boolean canMove() {
			return !this.entity.isSucking();
		}
	}

	protected static class AISuck extends EntityAIBase {
		protected final EntityWallLamprey entity;
		protected int minCooldown;
		protected int maxCooldown;

		protected int cooldown = 0;

		public AISuck(EntityWallLamprey entity) {
			this(entity, 50, 170);
		}

		public AISuck(EntityWallLamprey entity, int minCooldown, int maxCooldown) {
			this.entity = entity;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
			this.setMutexBits(0);
		}

		@Override
		public boolean shouldExecute() {
			return this.entity.getFacing() != EnumFacing.DOWN && !this.entity.isSucking() && !this.entity.isMoving() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() &&
					this.entity.getEntitySenses().canSee(this.entity.getAttackTarget()) && this.entity.getDistance(this.entity.getAttackTarget()) < 6.0F;
		}

		@Override
		public void startExecuting() {
			this.cooldown = 20 + this.entity.rand.nextInt(40);
		}

		@Override
		public void updateTask() {
			if(!this.entity.isSucking()) {
				if(this.cooldown <= 0) {
					this.cooldown = this.minCooldown + this.entity.rand.nextInt(this.maxCooldown - this.minCooldown + 1);
					this.entity.startSucking();
				}
				this.cooldown--;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.shouldExecute();
		}
	}

	protected static class AISpit extends EntityAIBase {
		protected final EntityWallLamprey entity;
		protected int minCooldown;
		protected int maxCooldown;

		protected int cooldown = 0;

		protected float spitDamage;

		public AISpit(EntityWallLamprey entity, float spitDamage) {
			this(entity, spitDamage, 50, 170);
		}

		public AISpit(EntityWallLamprey entity, float spitDamage, int minCooldown, int maxCooldown) {
			this.entity = entity;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
			this.spitDamage = spitDamage;
			this.setMutexBits(0);
		}

		protected boolean isInRange(EntityLivingBase target) {
			final Vec3d down = new Vec3d(0, -1, 0);
			Vec3d dir = target.getPositionVector().subtract(this.entity.getPositionVector()).normalize();
			return Math.acos(down.dotProduct(dir)) < 0.733D /*~42°*/;
		}

		@Override
		public boolean shouldExecute() {
			return this.entity.getFacing() == EnumFacing.DOWN && !this.entity.isSucking() && !this.entity.isMoving() && this.entity.getAttackTarget() != null && this.entity.getAttackTarget().isEntityAlive() &&
					this.entity.getEntitySenses().canSee(this.entity.getAttackTarget()) && this.isInRange(this.entity.getAttackTarget());
		}

		@Override
		public void startExecuting() {
			this.cooldown = 20 + this.entity.rand.nextInt(40);
		}

		@Override
		public void updateTask() {
			if(!this.entity.isSucking()) {
				if(this.cooldown <= 0) {
					this.cooldown = this.minCooldown + this.entity.rand.nextInt(this.maxCooldown - this.minCooldown + 1);
					this.entity.startSpit(this.getSpitDamage());
				}
				this.cooldown--;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.shouldExecute();
		}

		protected float getSpitDamage() {
			return this.spitDamage;
		}
	}
}
