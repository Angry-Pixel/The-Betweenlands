package thebetweenlands.common.entity.mobs;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySmolSludgeWorm extends EntityMob implements IEntityMultiPart, IMob, IEntityBL {

	public MultiPartEntityPart[] sludge_worm_Array;
	public MultiPartEntityPart sludge_worm_1;
	public MultiPartEntityPart sludge_worm_2;
	public MultiPartEntityPart sludge_worm_3;
	public MultiPartEntityPart sludge_worm_4;
	public MultiPartEntityPart sludge_worm_5;
	public MultiPartEntityPart sludge_worm_6;
	public MultiPartEntityPart sludge_worm_7;
	public MultiPartEntityPart sludge_worm_8;
	public MultiPartEntityPart sludge_worm_9;

	public boolean debugHitboxes = false;

	Random rand = new Random();

	private AxisAlignedBB renderBoundingBox;
	
	public EntitySmolSludgeWorm(World world) {
		super(world);
		setSize(0.4375F, 0.3125F);
		isImmuneToFire = true;
		maxHurtResistantTime = 40;
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 0.5D, false));
		sludge_worm_Array = new MultiPartEntityPart[] {
				sludge_worm_1 = new MultiPartEntityPart(this, "part1", 0.4375F, 0.3125F),
				sludge_worm_2 = new MultiPartEntityPart(this, "part2", 0.3125F, 0.3125F),
				sludge_worm_3 = new MultiPartEntityPart(this, "part3", 0.3125F, 0.3125F),
				sludge_worm_4 = new MultiPartEntityPart(this, "part4", 0.3125F, 0.3125F),
				sludge_worm_5 = new MultiPartEntityPart(this, "part5", 0.3125F, 0.3125F),
				sludge_worm_6 = new MultiPartEntityPart(this, "part6", 0.3125F, 0.3125F),
				sludge_worm_7 = new MultiPartEntityPart(this, "part7", 0.3125F, 0.3125F),
				sludge_worm_8 = new MultiPartEntityPart(this, "part8", 0.3125F, 0.3125F),
				sludge_worm_9 = new MultiPartEntityPart(this, "part9", 0.3125F, 0.3125F) };
		// tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(3, new EntityAIWander(this, 0.5D, 1));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, true));
		
		this.renderBoundingBox = this.getEntityBoundingBox();
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.5D);
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

	public void onLivingUpdate() {
		super.onLivingUpdate();
		setMoveForward(0.2F);
		setHitBoxes();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(this.world.isRemote && this.ticksExisted % 10 == 0) {
			this.spawnParticles(this.world, this.posX, this.posY, this.posZ, this.rand);
		}
		if (ticksExisted < 20) {
			motionY *= 0.65F;
		}
		
		this.renderBoundingBox = this.getEntityBoundingBox();
		for(MultiPartEntityPart part : this.sludge_worm_Array) {
			this.renderBoundingBox = this.renderBoundingBox.union(part.getEntityBoundingBox());
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void spawnParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 10; ++count) {
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 0D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			double offSetZ = Math.cos(a) * 0D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			BLParticles.TAR_BEAST_DRIP.spawn(world , x + offSetX, y, z + offSetZ).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
		}
	}

	// can be set to any part(s) - dunno if we want this either
	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float dmg) {
		if (part != sludge_worm_1) {
			damageWorm(source, dmg);
			return true;
		} else {
			dmg *= 0.5F;
			return false;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source instanceof EntityDamageSource && ((EntityDamageSource) source).getIsThornsDamage())
			damageWorm(source, amount);
		return true;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		return EntitySmolSludgeWorm.class != entity;
	}

	protected boolean damageWorm(DamageSource source, float ammount) {
		return super.attackEntityFrom(source, ammount);
	}

	@Override
	public Entity[] getParts() {
		return sludge_worm_Array;
	}

	private void setHitBoxes() {

		if (ticksExisted == 1) {
			sludge_worm_2.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0F);
			sludge_worm_3.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0F);
			sludge_worm_4.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0F);
			sludge_worm_5.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0F);
			sludge_worm_6.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0F);
			sludge_worm_7.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0F);
			sludge_worm_8.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0F);
			sludge_worm_9.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0F);
		}

		sludge_worm_1.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
		
		for(MultiPartEntityPart part : this.sludge_worm_Array) {
			part.prevRotationYaw = part.rotationYaw;
			part.prevRotationPitch = part.rotationPitch;
			
			if(part != this.sludge_worm_1) {
				part.prevPosX = part.lastTickPosX = part.posX;
				part.prevPosY = part.lastTickPosY = part.posY;
				part.prevPosZ = part.lastTickPosZ = part.posZ;
				
				if(part.posY < this.posY && this.world.collidesWithAnyBlock(part.getEntityBoundingBox())) {
					part.move(MoverType.SELF, 0, 0.1D, 0);
					part.motionY = 0.0D;
				} else if(this.onGround) {
					part.motionY -= 0.08D;
				}
				
				part.motionY *= 0.98;
				part.move(MoverType.SELF, 0, part.motionY, 0);
			}
		}
		
		movePiecePos(sludge_worm_2, sludge_worm_1, 4.5F, 2F);
		movePiecePos(sludge_worm_3, sludge_worm_2, 4.5F, 2F);
		movePiecePos(sludge_worm_4, sludge_worm_3, 4.5F, 2F);
		movePiecePos(sludge_worm_5, sludge_worm_4, 4.5F, 2F);
		movePiecePos(sludge_worm_6, sludge_worm_5, 4.5F, 2F);
		movePiecePos(sludge_worm_7, sludge_worm_6, 4.5F, 2F);
		movePiecePos(sludge_worm_8, sludge_worm_7, 4.5F, 2F);
		movePiecePos(sludge_worm_9, sludge_worm_8, 4.5F, 2F);
	}

	public void movePiecePos(MultiPartEntityPart targetPart, MultiPartEntityPart destinationPart, float speed, float yawSpeed) {
		//TODO make this better and use the parent entities motionY 
		if (destinationPart.posY - targetPart.posY < -0.5D)
			speed = 1.5F;
		
		double movementTolerance = 0.05D;
		double maxDist = 0.3D;
		
		boolean correctY = false;
		
		for(int i = 0; i < 5; i++) {
			Vec3d diff = destinationPart.getPositionVector().subtract(targetPart.getPositionVector());
			double len = diff.lengthVector();
			
			if(len > maxDist) {
				Vec3d correction = diff.scale(1.0D / len * (len - maxDist));
				targetPart.posX += correction.x;
				targetPart.posZ += correction.z;
				
				targetPart.setPosition(targetPart.posX, targetPart.posY, targetPart.posZ);
				
				double cy = targetPart.posY;
				
				targetPart.move(MoverType.SELF, 0, correction.y, 0);
				
				if(Math.abs((targetPart.posY - cy) - correction.y) <= movementTolerance) {
					correctY = true;
					break;
				}
			}
		}
		
		//Welp, failed to move smoothly along Y, just clip
		if(!correctY) {
			Vec3d diff = destinationPart.getPositionVector().subtract(targetPart.getPositionVector());
			double len = diff.lengthVector();
				
			if(len > maxDist) {
				Vec3d correction = diff.scale(1.0D / len * (len - maxDist));
				
				targetPart.posX += correction.x;
				targetPart.posY += correction.y;
				targetPart.posZ += correction.z;
			}
		}
		
		
		Vec3d diff = new Vec3d(destinationPart.posX, 0, destinationPart.posZ).subtract(new Vec3d(targetPart.posX, 0, targetPart.posZ));
		float destYaw = (float)Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90;
		
		double yawDiff = (destYaw - targetPart.rotationYaw) % 360.0F;
		double yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;
		
		targetPart.rotationYaw += yawInterpolant / yawSpeed;
		
		targetPart.rotationPitch = 0;
		
		targetPart.setPosition(targetPart.posX, targetPart.posY, targetPart.posZ);
	}

	@Override
	public World getWorld() {
		return getEntityWorld();
	}

	// temp Sounds until we have proper ones
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SNAIL_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.SNAIL_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SNAIL_DEATH;
	}

	@Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
       playSound(SoundRegistry.SNAIL_LIVING, 0.5F, 1F);
    }

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return this.renderBoundingBox;
	}
}
