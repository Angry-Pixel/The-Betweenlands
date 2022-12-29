package thebetweenlands.common.entity.mobs;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.EntityAIFollowTarget;
import thebetweenlands.common.entity.ai.EntityAIJumpRandomly;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityRootSprite extends EntityCreature implements IEntityBL, net.minecraft.entity.passive.IAnimals {
	private static final byte EVENT_STEP = 40;

	private float jumpHeightOverride = -1;

	public EntityRootSprite(World worldIn) {
		super(worldIn);
		this.experienceValue = 1;
		this.setSize(0.3F, 0.55F);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 1.0D));
		this.tasks.addTask(2, new EntityAIAvoidEntity<>(this, EntityPlayer.class, 5, 0.5F, 1.0F));
		this.tasks.addTask(3, new EntityAIFollowTarget(this, new EntityAIFollowTarget.FollowClosest(this, EntitySporeling.class, 10), 0.65D, 0.5F, 10.0F, false));
		this.tasks.addTask(4, new EntityAIJumpRandomly(this, 10, () -> !EntityRootSprite.this.world.getEntitiesWithinAABB(EntitySporeling.class, this.getEntityBoundingBox().grow(1)).isEmpty()) {
			@Override
			public void startExecuting() {
				EntityRootSprite.this.setJumpHeightOverride(0.2F);
				EntityRootSprite.this.getJumpHelper().setJumping();
			}
		});
		this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.6D));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntitySporeling.class, 8));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 10));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.ROOT_SPRITE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.ROOT_SPRITE_DEATH;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.ROOT_SPRITE_LIVING;
	}

	@Override
	protected float getSoundVolume() {
		return 0.5F;
	}

	@Override
	public int getTalkInterval() {
		return 5 * 20;
	}

	public void setJumpHeightOverride(float jumpHeightOverride) {
		this.jumpHeightOverride = jumpHeightOverride;
	}

	@Override
	protected float getJumpUpwardsMotion() {
		if(this.jumpHeightOverride > 0) {
			float height = this.jumpHeightOverride;
			this.jumpHeightOverride = -1;
			return height;
		}
		return super.getJumpUpwardsMotion();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.world.isRemote && this.rand.nextInt(20) == 0) {
			this.spawnLeafParticles();
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.ROOT_SPRITE;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		super.playStepSound(pos, blockIn);

		this.distanceWalkedOnStepModified += 0.7F;

		this.world.setEntityState(this, EVENT_STEP);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_STEP) {
			this.spawnLeafParticles();
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnLeafParticles() {
		for(int i = 0; i < 1 + this.rand.nextInt(3); i++) {
			BLParticles.WEEDWOOD_LEAF.spawn(this.world, this.posX + this.motionX, this.posY + 0.1F + this.rand.nextFloat() * 0.3F, this.posZ + this.motionZ, ParticleArgs.get()
					.withMotion(this.motionX * 0.5F + this.rand.nextFloat() * 0.1F - 0.05F, 0.05F, this.motionZ * 0.5F + this.rand.nextFloat() * 0.1F - 0.05F)
					.withScale(0.5F));
		}
	}
}
