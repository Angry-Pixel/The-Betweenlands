package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityWallLivingRoot extends EntityMovingWallFace implements IMob, IEntityMultiPart {
	public static final byte EVENT_DEATH = 3;
	public static final byte EVENT_HURT_SOUND = 82;

	public static final IAttribute MAX_ARM_LENGTH = (new RangedAttribute(null, "bl.maxRootArmLength", 2.5D, 0.0D, 16.0D)).setDescription("Maximum length of root arm").setShouldWatch(true);

	public static class ArmSegment {
		public Vec3d motion = Vec3d.ZERO;

		public Vec3d prevPos, pos;

		public final float[] offsetX, offsetY, offsetZ;

		private final float[][] armCrossSection;

		public ArmSegment(EntityWallLivingRoot root) {
			this.armCrossSection = root.getArmCrossSection();
			this.offsetX = new float[this.armCrossSection.length];
			this.offsetY = new float[this.armCrossSection.length];
			this.offsetZ = new float[this.armCrossSection.length];
		}

		public void updatePrev() {
			if(this.pos == null) {
				this.prevPos = Vec3d.ZERO; 
			} else {
				this.prevPos = this.pos;
			}
		}

		public void update(Vec3d quadUp, Vec3d pos, Vec3d dir) {
			this.pos = pos;

			Vec3d right = dir.crossProduct(quadUp).normalize();
			Vec3d up = right.crossProduct(dir).normalize();

			int i = 0;
			for(float[] hullCrossSection : this.armCrossSection) {
				float hullX = hullCrossSection[0];
				float hullY = hullCrossSection[1];

				this.offsetX[i] = (float) (right.x * hullX + up.x * hullY);
				this.offsetY[i] = (float) (right.y * hullX + up.y * hullY);
				this.offsetZ[i] = (float) (right.z * hullX + up.z * hullY);

				i++;
			}
		}
	}

	private static final DataParameter<Integer> REL_TIP_X = EntityDataManager.createKey(EntityWallLivingRoot.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> REL_TIP_Y = EntityDataManager.createKey(EntityWallLivingRoot.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> REL_TIP_Z = EntityDataManager.createKey(EntityWallLivingRoot.class, DataSerializers.VARINT);

	private boolean rootTipPositionSet = false;

	public final MultiPartEntityPart rootTip;
	private MultiPartEntityPart[] parts;

	private EnumFacing segmentsFacing = EnumFacing.NORTH;

	public List<ArmSegment> armSegments = new ArrayList<>();

	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite wallSprite;

	protected int armMovementTicks;

	public EntityWallLivingRoot(World world) {
		super(world);

		this.lookMoveSpeedMultiplier = 8.0F;
		this.experienceValue = 7;

		this.parts = new MultiPartEntityPart[this.getNumSegments() + 1];
		this.parts[0] = this.rootTip = new MultiPartEntityPart(this, "rootTip", this.getNodeSize(0), this.getNodeSize(0));
		for(int i = 0; i < this.getNumSegments(); i++) {
			this.parts[i + 1] = new MultiPartEntityPart(this, "rootNode" + i, this.getNodeSize(this.getNumSegments() - i + 1), this.getNodeSize(this.getNumSegments() - i + 1));
		}
	}

	protected float getNodeSize(int node) {
		return 0.3F;
	}

	protected float[][] getArmCrossSection() {
		float width = this.getFullArmWidth();
		return new float[][] {
			{-width, width},
			{-width, -width},
			{width, -width},
			{width, width},
		};
	}

	protected int getNumSegments() {
		return 8;
	}

	protected float getFullArmWidth() {
		return 0.2F;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.armMovementTicks = this.world.rand.nextInt(10000);
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(REL_TIP_X, 0);
		this.dataManager.register(REL_TIP_Y, 0);
		this.dataManager.register(REL_TIP_Z, 0);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.targetTasks.addTask(0, new EntityAIHurtByTargetImproved(this, true) {
			@Override
			protected double getTargetDistance() {
				return 8.0D;
			}
		});
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 0, true, false, null).setUnseenMemoryTicks(120));

		this.tasks.addTask(0, new AITrackTarget<EntityWallLivingRoot>(this, true, 28.0D) {
			@Override
			protected boolean canMove() {
				return true;
			}
		});
		this.tasks.addTask(1, new AIArmAttack(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.08D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.5D);
		this.getAttributeMap().registerAttribute(MAX_ARM_LENGTH);
	}

	public Vec3d getTipPos() {
		return new Vec3d(this.posX + this.dataManager.get(REL_TIP_X) / 512.0f, this.posY + this.dataManager.get(REL_TIP_Y) / 512.0f, this.posZ + this.dataManager.get(REL_TIP_Z) / 512.0f);
	}

	public void setTipPos(Vec3d pos) {
		this.dataManager.set(REL_TIP_X, (int)((pos.x - this.posX) * 512));
		this.dataManager.set(REL_TIP_Y, (int)((pos.y - this.posY) * 512));
		this.dataManager.set(REL_TIP_Z, (int)((pos.z - this.posZ) * 512));
	}

	protected Vec3d updateTargetTipPos(Vec3d armStartWorld, float maxArmLength, Vec3d dirFwd, Vec3d dirUp) {
		float flailingStrength = this.isSwingInProgress ? (1 - this.swingProgress) : this.hurtTime > 0 ? (this.hurtTime / (float)this.maxHurtTime) * 0.5F : 0.0f;

		this.armMovementTicks += 1 + (int)(flailingStrength * 10);

		float idleX = MathHelper.cos(this.armMovementTicks / 9.0f) * 0.75F;
		float idleY = MathHelper.sin(this.armMovementTicks / 7.0f) * 0.75F;
		float idleZ = (MathHelper.cos(this.armMovementTicks / 15.0f) + 1) * 0.25f;

		Vec3d targetTipPos = armStartWorld.add(dirFwd.scale(maxArmLength));

		EntityLivingBase target = this.getAttackTarget();
		if(target != null) {
			targetTipPos = target.getPositionVector().add(0, target.height / 2, 0);
		}

		float forwardPos = (float) dirFwd.dotProduct(targetTipPos.subtract(armStartWorld));
		float offsetZ = 0.0f;
		if(forwardPos < 1.0F) {
			offsetZ = 1.0F - forwardPos;
		}

		//Idle movement
		targetTipPos = targetTipPos.add(dirUp.scale(idleY)).add(dirFwd.crossProduct(dirUp).scale(idleX)).add(dirFwd.scale(offsetZ - idleZ));

		Vec3d tipPos = this.rootTip.getPositionVector();

		Vec3d tipDiff = targetTipPos.subtract(tipPos);
		targetTipPos = tipPos.add(tipDiff.normalize().scale(Math.min(tipDiff.length(), 0.1D + flailingStrength * 0.9D)));

		return targetTipPos;
	}

	protected float getArmLengthSlack() {
		return 0.0f;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
			this.setDead();
		}

		float maxArmLength = (float)this.getEntityAttribute(MAX_ARM_LENGTH).getAttributeValue() * this.getArmSize(1);

		float segmentLength = maxArmLength / (float)(this.getNumSegments() - 2);

		Vec3d dirFwd = new Vec3d(this.getFacing().getXOffset(), this.getFacing().getYOffset(), this.getFacing().getZOffset());;
		Vec3d dirUp = new Vec3d(this.getFacingUp().getXOffset(), this.getFacingUp().getYOffset(), this.getFacingUp().getZOffset());

		Vec3d armStart = new Vec3d(0, this.height / 2, 0).add(-dirFwd.x * (this.width / 2 - 0.1f), -dirFwd.y * (this.height / 2 - 0.1f), -dirFwd.z * (this.width / 2 - 0.1f));
		Vec3d ikArmStart = new Vec3d(0, this.height / 2, 0).add(dirFwd.scale(0.1f));

		for(MultiPartEntityPart part : this.parts) {
			part.onUpdate();
		}

		if(!this.rootTipPositionSet) {
			Vec3d tipPos = this.getPositionVector().add(armStart.add(dirFwd.scale(maxArmLength)).add(0, -this.rootTip.height / 2, 0));
			this.setTipPos(tipPos);
			this.rootTip.setPosition(tipPos.x, tipPos.y, tipPos.z);
			this.rootTipPositionSet = true;
		}

		Vec3d armEnd = this.rootTip.getPositionVector().add(0, this.rootTip.height / 2, 0).subtract(this.getPositionVector());

		if(!this.world.isRemote) {
			Vec3d armStartWorld = this.getPositionVector().add(ikArmStart);

			Vec3d tipPos = this.updateTargetTipPos(armStartWorld, maxArmLength, dirFwd, dirUp);

			//Clamp to max reach sphere
			tipPos = armStartWorld.add(tipPos.subtract(armStartWorld).normalize().scale(Math.min(tipPos.subtract(armStartWorld).length(), maxArmLength + this.getArmLengthSlack())));

			this.setTipPos(tipPos);
			this.rootTip.setPosition(tipPos.x, tipPos.y, tipPos.z);
		} else {
			Vec3d tipPos = this.getTipPos();
			this.rootTip.setPosition(tipPos.x, tipPos.y, tipPos.z);

			this.updateWallSprite();
		}

		if(this.armSegments.size() != this.getNumSegments() || this.getFacing() != this.segmentsFacing) {
			this.armSegments.clear();

			for(int i = 0; i < this.getNumSegments(); i++) {
				ArmSegment segment = new ArmSegment(this);
				float dist = maxArmLength / (float)(this.getNumSegments() - 1) * i;
				segment.update(dirUp, ikArmStart.add(dirFwd.x * dist, dirFwd.y * dist, dirFwd.z * dist), dirFwd);
				this.armSegments.add(segment);
			}

			this.segmentsFacing = this.getFacing();
		}

		for(ArmSegment segment : this.armSegments) {
			segment.updatePrev();

			segment.pos = segment.pos.add(segment.motion);
		}

		for(int i = this.getNumSegments() - 2; i >= 2; i--) {
			ArmSegment segment = this.armSegments.get(i);

			Vec3d target;
			if(i == this.getNumSegments() - 2) {
				target = armEnd;
			} else {
				target = this.armSegments.get(i + 1).pos;
			}

			Vec3d dir = segment.pos.subtract(target).normalize();

			segment.update(dirUp, target.add(dir.scale(segmentLength)), dir.scale(-1));
		}

		for(int i = 2; i < this.getNumSegments(); i++) {
			ArmSegment segment = this.armSegments.get(i);

			Vec3d target;
			if(i == 0) {
				target = ikArmStart;
			} else {
				target = this.armSegments.get(i - 1).pos;
			}

			Vec3d dir = segment.pos.subtract(target).normalize();

			segment.update(dirUp, target.add(dir.scale(segmentLength)), dir.scale(-1));
		}

		ArmSegment startSegment = this.armSegments.get(0);
		startSegment.update(dirUp, armStart, new Vec3d(-dirFwd.x, -dirFwd.y, -dirFwd.z));

		ArmSegment startSegment2 = this.armSegments.get(1);
		startSegment2.update(dirUp, ikArmStart, new Vec3d(-dirFwd.x, -dirFwd.y, -dirFwd.z));

		ArmSegment endSegment = this.armSegments.get(this.armSegments.size() - 1);
		endSegment.update(dirUp, armEnd, this.armSegments.get(this.armSegments.size() - 2).pos.subtract(armEnd).normalize());

		for(int i = 0; i < this.getNumSegments(); i++) {
			ArmSegment segment = this.armSegments.get(i);
			Vec3d pos = segment.pos;
			this.parts[i + 1].setPosition(this.posX + pos.x, this.posY + pos.y - this.parts[i + 1].height / 2.0f, this.posZ + pos.z);
		}
	}

	@Override
	protected void updateMovement() {
		if(!this.world.isRemote && this.isMoving() && this.getMoveReason() != MoveReason.LOOK) {
			boolean wasFirstHalf = this.getMovementProgress(1) < 0.5F;

			super.updateMovement();

			if(this.getMovementProgress(1) >= 0.5F && wasFirstHalf) {
				this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.WALL_LIVING_ROOT_EMERGE, SoundCategory.HOSTILE, 1, 1);
			}
		} else {
			super.updateMovement();
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.updateArmSwingProgress();
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
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.armMovementTicks = nbt.getInteger("armMovementTicks");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("armMovementTicks", this.armMovementTicks);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.LIVING_ROOT_DEATH;
	}

	@Override
	protected void playHurtSound(DamageSource source) {
		this.world.setEntityState(this, EVENT_HURT_SOUND);
	}

	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_DEATH)
			this.world.playSound(this.posX, this.posY, this.posZ, SoundRegistry.LIVING_ROOT_DEATH, getSoundCategory(), 0.75F, 0.8F, false);
	
		if(id == EVENT_HURT_SOUND)
			this.world.playSound(this.posX, this.posY, this.posZ, SoundRegistry.LIVING_ROOT_HURT, getSoundCategory(), 0.75F, 0.5F, false);

	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.WALL_LIVING_ROOT;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, EnumFacing facing, EnumFacing facingUp) {
		return this.isValidBlockForMovement(pos, this.world.getBlockState(pos));
	}

	@Override
	protected boolean isValidBlockForMovement(BlockPos pos, IBlockState state) {
		return state.isOpaqueCube() && state.isNormalCube() && state.isFullCube() && state.getBlockHardness(this.world, pos) > 0 && (state.getMaterial() == Material.ROCK || state.getMaterial() == Material.WOOD);
	}

	@Override
	public Vec3d getOffset(float movementProgress) {
		return super.getOffset(1.0F);
	}

	public float getArmSize(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	public float getHoleDepthPercent(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public Entity[] getParts() {
		return this.parts;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		EntityLivingBase attacker = source.getImmediateSource() instanceof EntityLivingBase ? (EntityLivingBase)source.getImmediateSource() : null;
		if(attacker != null && attacker.getActiveHand() != null) {
			ItemStack item = attacker.getHeldItem(attacker.getActiveHand());
			if(!item.isEmpty() && item.getItem().getToolClasses(item).contains("axe")) {
				amount *= 2.0F;
			}
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
		return this.attackEntityFrom(source, damage);
	}

	protected static class AIArmAttack extends EntityAIBase {
		protected final EntityWallLivingRoot entity;
		protected int attackTicks;

		public AIArmAttack(EntityWallLivingRoot entity) {
			this.entity = entity;
		}

		@Override
		public boolean shouldExecute() {
			return this.entity.getAttackTarget() != null;
		}

		@Override
		public void updateTask() {
			Entity target = this.entity.getAttackTarget();

			if(this.attackTicks > 0) {
				this.attackTicks--;
			} else if(target != null && target.getEntityBoundingBox().intersects(this.entity.rootTip.getEntityBoundingBox())) {
				this.entity.attackEntityAsMob(target);
				this.entity.swingArm(EnumHand.MAIN_HAND);
				this.attackTicks = 20;
			}
		}
	}
}
