package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
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
	public final MultiPartEntityPart rootTip;
	private MultiPartEntityPart[] parts;

	protected static final float ARM_FULL_WIDTH = 0.2F;
	protected static final float[][] ARM_CROSS_SECTION = new float[][] {
		{-ARM_FULL_WIDTH, ARM_FULL_WIDTH},
		{-ARM_FULL_WIDTH, -ARM_FULL_WIDTH},
		{ARM_FULL_WIDTH, -ARM_FULL_WIDTH},
		{ARM_FULL_WIDTH, ARM_FULL_WIDTH},
	};

	public static class ArmSegment {
		//TODO Base on facing
		private static final Vec3d WORLD_UP = new Vec3d(0, 1, 0);

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

		public void update(Vec3d pos, Vec3d dir) {
			this.pos = pos;

			Vec3d right = dir.crossProduct(WORLD_UP).normalize();
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

	public List<ArmSegment> armSegments = new ArrayList<>();

	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite wallSprite;

	public EntityWallLivingRoot(World world) {
		super(world);

		this.lookMoveSpeedMultiplier = 15.0F;
		this.experienceValue = 5;

		this.parts = new MultiPartEntityPart[] {
				this.rootTip = new MultiPartEntityPart(this, "rootTip", 0.3F, 0.3F)
		};
	}

	@Override
	protected void entityInit() {
		super.entityInit();
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

	@Override
	public void onUpdate() {
		super.onUpdate();

		float maxArmLength = 2.0F;

		int numSegments = 5;

		float segmentLength = maxArmLength / (float)numSegments;

		Vec3d armStart = this.getPositionVector().add(0, this.height / 2, 0).subtract(this.getPositionVector());
		Vec3d armEnd = this.rootTip.getPositionVector().add(0, this.rootTip.height / 2, 0).subtract(this.getPositionVector());

		//TODO Clear and fill first time or when facing has changed

		if(this.armSegments.isEmpty()) {
			for(int i = 0; i < numSegments; i++) {
				ArmSegment segment = new ArmSegment();
				segment.update(armStart.add(0, 0, -maxArmLength / (float)(numSegments - 1) * i), new Vec3d(0, 0, -1));
				this.armSegments.add(segment);
			}
		}

		for(ArmSegment segment : this.armSegments) {
			segment.updatePrev();
		}

		for(int i = numSegments - 1; i >= 0; i--) {
			ArmSegment segment = this.armSegments.get(i);

			Vec3d target;
			if(i == numSegments - 1) {
				target = armEnd;
			} else {
				target = this.armSegments.get(i + 1).pos;
			}

			Vec3d dir = segment.pos.subtract(target).normalize();

			segment.update(target.add(dir.scale(segmentLength)), dir.scale(-1));
		}

		for(int i = 0; i < numSegments; i++) {
			ArmSegment segment = this.armSegments.get(i);

			Vec3d target;
			if(i == 0) {
				target = armStart;
			} else {
				target = this.armSegments.get(i - 1).pos;
			}

			Vec3d dir = segment.pos.subtract(target).normalize();

			segment.update(target.add(dir.scale(segmentLength)), dir.scale(-1));
		}

		this.rootTip.onUpdate();

		float armX = MathHelper.cos(this.ticksExisted / 10.0f);
		float armY = MathHelper.sin(this.ticksExisted / 8.0f);
		float armZ = (MathHelper.cos(this.ticksExisted / 15.0f) + 1) * 0.5f;

		Vec3d tipPos = this.getPositionVector().add(this.getFacing().getXOffset() * maxArmLength + armX, this.height / 2 - this.rootTip.height / 2 + this.getFacing().getYOffset() * maxArmLength + armY, this.getFacing().getZOffset() * maxArmLength + armZ);
		this.rootTip.setPosition(tipPos.x, tipPos.y, tipPos.z);

		if(this.world.isRemote) {
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
		// TODO Auto-generated method stub
		return false;
	}
}
