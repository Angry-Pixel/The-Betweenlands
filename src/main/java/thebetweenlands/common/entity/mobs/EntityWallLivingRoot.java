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
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.registries.LootTableRegistry;

//TODO Loot tables
public class EntityWallLivingRoot extends EntityMovingWallFace implements IMob, IEntityMultiPart {
	protected static final float ARM_FULL_WIDTH = 0.2F;
	protected static final float[][] ARM_CROSS_SECTION = new float[][] {
		{-ARM_FULL_WIDTH, ARM_FULL_WIDTH},
		{-ARM_FULL_WIDTH, -ARM_FULL_WIDTH},
		{ARM_FULL_WIDTH, -ARM_FULL_WIDTH},
		{ARM_FULL_WIDTH, ARM_FULL_WIDTH},
	};

	public static class ArmSegment {
		public Vec3d prevPos, pos;

		public final float[] offsetX, offsetY, offsetZ;

		public ArmSegment() {
			this.offsetX = new float[ARM_CROSS_SECTION.length];
			this.offsetY = new float[ARM_CROSS_SECTION.length];
			this.offsetZ = new float[ARM_CROSS_SECTION.length];
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
			for(float[] hullCrossSection : ARM_CROSS_SECTION) {
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

	public final MultiPartEntityPart rootTip;
	private MultiPartEntityPart[] parts;

	private EnumFacing segmentsFacing = EnumFacing.NORTH;

	public List<ArmSegment> armSegments = new ArrayList<>();

	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite wallSprite;

	public EntityWallLivingRoot(World world) {
		super(world);

		this.lookMoveSpeedMultiplier = 8.0F;
		this.experienceValue = 5;

		this.parts = new MultiPartEntityPart[] {
				this.rootTip = new MultiPartEntityPart(this, "rootTip", 0.3F, 0.3F)
		};
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

		this.targetTasks.addTask(0, new EntityAIHurtByTargetImproved(this, true));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));

		this.tasks.addTask(0, new AITrackTarget<EntityWallLivingRoot>(this, true, 28.0D) {
			@Override
			protected boolean canMove() {
				return true;
			}
		});
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.08D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	public Vec3d getTipPos() {
		return new Vec3d(this.posX + this.dataManager.get(REL_TIP_X) / 512.0f, this.posY + this.dataManager.get(REL_TIP_Y) / 512.0f, this.posZ + this.dataManager.get(REL_TIP_Z) / 512.0f);
	}

	public void setTipPos(Vec3d pos) {
		this.dataManager.set(REL_TIP_X, (int)((pos.x - this.posX) * 512));
		this.dataManager.set(REL_TIP_Y, (int)((pos.y - this.posY) * 512));
		this.dataManager.set(REL_TIP_Z, (int)((pos.z - this.posZ) * 512));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		float maxArmLength = 2.0F * this.getArmSize(1);

		int numSegments = 8;

		float segmentLength = maxArmLength / (float)(numSegments - 2);

		Vec3d dirFwd = new Vec3d(this.getFacing().getXOffset(), this.getFacing().getYOffset(), this.getFacing().getZOffset());;
		Vec3d dirUp = new Vec3d(this.getFacingUp().getXOffset(), this.getFacingUp().getYOffset(), this.getFacingUp().getZOffset());

		Vec3d armStart = new Vec3d(0, this.height / 2, 0).add(-dirFwd.x * (this.width / 2 - 0.1f), -dirFwd.y * (this.height / 2 - 0.1f), -dirFwd.z * (this.width / 2 - 0.1f));
		Vec3d ikArmStart = new Vec3d(0, this.height / 2, 0).add(dirFwd.scale(0.1f));
		Vec3d armEnd = this.rootTip.getPositionVector().add(0, this.rootTip.height / 2, 0).subtract(this.getPositionVector());

		this.rootTip.onUpdate();

		if(!this.world.isRemote) {
			float idleX = MathHelper.cos(this.ticksExisted / 9.0f) * 0.75F;
			float idleY = MathHelper.sin(this.ticksExisted / 7.0f) * 0.75F;
			float idleZ = (MathHelper.cos(this.ticksExisted / 15.0f) + 1) * 0.25f;

			Vec3d armStartWorld = this.getPositionVector().add(ikArmStart);

			Vec3d tipPos = this.rootTip.getPositionVector();

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

			Vec3d tipDiff = targetTipPos.subtract(tipPos);
			tipPos = tipPos.add(tipDiff.normalize().scale(Math.min(tipDiff.length(), 0.1D)));

			//Clamp to max reach sphere
			tipPos = armStartWorld.add(tipPos.subtract(armStartWorld).normalize().scale(Math.min(tipPos.subtract(armStartWorld).length(), maxArmLength)));

			this.setTipPos(tipPos);
			this.rootTip.setPosition(tipPos.x, tipPos.y, tipPos.z);
		} else {
			Vec3d tipPos = this.getTipPos();
			this.rootTip.setPosition(tipPos.x, tipPos.y, tipPos.z);

			if(this.armSegments.size() != numSegments || this.getFacing() != this.segmentsFacing) {
				this.armSegments.clear();

				for(int i = 0; i < numSegments; i++) {
					ArmSegment segment = new ArmSegment();
					float dist = maxArmLength / (float)(numSegments - 1) * i;
					segment.update(dirUp, ikArmStart.add(dirFwd.x * dist, dirFwd.y * dist, dirFwd.z * dist), dirFwd);
					this.armSegments.add(segment);
				}

				this.segmentsFacing = this.getFacing();
			}

			for(ArmSegment segment : this.armSegments) {
				segment.updatePrev();
			}

			for(int i = numSegments - 2; i >= 2; i--) {
				ArmSegment segment = this.armSegments.get(i);

				Vec3d target;
				if(i == numSegments - 2) {
					target = armEnd;
				} else {
					target = this.armSegments.get(i + 1).pos;
				}

				Vec3d dir = segment.pos.subtract(target).normalize();

				segment.update(dirUp, target.add(dir.scale(segmentLength)), dir.scale(-1));
			}

			for(int i = 2; i < numSegments - 1; i++) {
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
			endSegment.update(dirUp, armEnd, armEnd.subtract(this.armSegments.get(this.armSegments.size() - 2).pos).normalize());

			this.updateWallSprite();
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
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.WALL_LIVING_ROOT;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, EnumFacing facing, EnumFacing facingUp) {
		return this.isValidBlockForMovement(pos, this.world.getBlockState(pos));
	}

	@Override
	protected boolean isValidBlockForMovement(BlockPos pos, IBlockState state) {
		return state.isOpaqueCube() && state.isNormalCube() && state.isFullCube() && state.getBlockHardness(this.world, pos) > 0 && state.getMaterial() == Material.ROCK;
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
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
		return this.attackEntityFrom(source, damage);
	}
}
