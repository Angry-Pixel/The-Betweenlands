package thebetweenlands.common.entity.mobs;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

//TODO Loot tables
public class EntityTinySludgeWorm extends EntitySludgeWorm {
	public static final byte EVENT_SQUASHED = 80;

	protected boolean isSquashed = false;
	
	public EntityTinySludgeWorm(World world) {
		super(world);
		setSize(0.3125F, 0.3125F);
		isImmuneToFire = true;
		this.parts = new MultiPartEntityPart[] {
				new MultiPartEntityPart(this, "part1", 0.1875F, 0.1875F),
				new MultiPartEntityPart(this, "part2", 0.1875F, 0.1875F),
				new MultiPartEntityPart(this, "part3", 0.1875F, 0.1875F),
				new MultiPartEntityPart(this, "part4", 0.1875F, 0.1875F),
				new MultiPartEntityPart(this, "part5", 0.1875F, 0.1875F),
				new MultiPartEntityPart(this, "part6", 0.1875F, 0.1875F),
		};
	}
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAILeapAtTarget(this, 0.4F));
		tasks.addTask(2, new EntityAIAttackMelee(this, 0.5D, false));
		tasks.addTask(3, new EntityAIWander(this, 0.5D, 1));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.1D);
	}

	// stuns the mob - dunno if we want this
	@Override
	public boolean isMovementBlocked() {
		// if (hurtResistantTime > 0){
		// return true;
		// } else {
		return false;
		// }
	}

	@Override
	protected double getMaxPieceDistance() {
		return 0.2D;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if (canEntityBeSeen(entity) && entity.onGround)
			if (super.attackEntityAsMob(entity))
				return true;
		return false;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		byte duration = 0;
		if (!getEntityWorld().isRemote) {
			for (MultiPartEntityPart part : this.parts) {
				if (player.getEntityBoundingBox().maxY >= part.getEntityBoundingBox().minY
						&& player.getEntityBoundingBox().minY <= part.getEntityBoundingBox().maxY
						&& player.getEntityBoundingBox().maxX >= part.getEntityBoundingBox().minX
						&& player.getEntityBoundingBox().minX <= part.getEntityBoundingBox().maxX
						&& player.getEntityBoundingBox().maxZ >= part.getEntityBoundingBox().minZ
						&& player.getEntityBoundingBox().minZ <= part.getEntityBoundingBox().maxZ
						&& player.prevPosY > player.posY) {
					if (getEntityWorld().getDifficulty() == EnumDifficulty.NORMAL) {
						duration = 7;
					} else if (getEntityWorld().getDifficulty() == EnumDifficulty.HARD) {
						duration = 15;
					}
					
					if (duration > 0) {
						player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration * 20, 0));
					}
					
					this.isSquashed = true;
				}
			}
			
			if (this.isSquashed) {
				this.world.setEntityState(this, EVENT_SQUASHED);
				
				this.world.playSound(null, this.posX, this.posY, this.posZ, getJumpedOnSound(), SoundCategory.NEUTRAL, 1.0F, 0.5F);
				this.world.playSound(null, this.posX, this.posY, this.posZ, getDeathSound(), SoundCategory.NEUTRAL, 1.0F, 0.5F);
				
				this.damageWorm(DamageSource.causePlayerDamage(player), this.getHealth());
			}
		}
	}

	public boolean isSquashed() {
		return this.isSquashed;
	}
	
	@Override
	public void onDeathUpdate() {
		if (this.isSquashed) {
			this.deathTime = 19;
		}
		
		super.onDeathUpdate();
	}
	
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		
		if(id == EVENT_SQUASHED) {
			for(int i = 0; i < 100; i++) {
				Random rnd = this.world.rand;
				float rx = rnd.nextFloat() * 1.0F - 0.5F;
				float ry = rnd.nextFloat() * 1.0F - 0.5F;
				float rz = rnd.nextFloat() * 1.0F - 0.5F;
				Vec3d vec = new Vec3d(rx, ry, rz);
				vec = vec.normalize();
				BLParticles.SPLASH_TAR.spawn(getEntityWorld(), this.posX + rx + 0.1F, this.posY + ry + 0.1F, this.posZ + rz + 0.1F, ParticleArgs.get().withMotion(vec.x * 0.4F, vec.y * 0.4F, vec.z * 0.4F)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			}
		}
	}

	protected SoundEvent getJumpedOnSound() {
		return SoundRegistry.SQUISH;
	}

	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() * 1.5F;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.TINY_SLUDGE_WORM;
	}
}
