package thebetweenlands.common.entity.mobs;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
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
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;

public class EntitySmolSludgeWorm extends EntityMob implements IEntityMultiPart, IMob, IEntityBL
{
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

	public EntitySmolSludgeWorm(World par1World) {
		super(par1World);
		setSize(0.3125F, 0.3125F);
		experienceValue = 10;
		isImmuneToFire = true;
		maxHurtResistantTime = 40;
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 1D, false));
		sludge_worm_Array = new MultiPartEntityPart[] { 
				sludge_worm_1 = new MultiPartEntityPart(this, "part1", 0.3125F, 0.3125F), 
				sludge_worm_2 = new MultiPartEntityPart(this, "part2", 0.3125F, 0.3125F), 
				sludge_worm_3 = new MultiPartEntityPart(this, "part3", 0.3125F, 0.3125F),
				sludge_worm_4 = new MultiPartEntityPart(this, "part4", 0.3125F, 0.3125F),
				sludge_worm_5 = new MultiPartEntityPart(this, "part5", 0.3125F, 0.3125F),
				sludge_worm_6 = new MultiPartEntityPart(this, "part6", 0.3125F, 0.3125F), 
				sludge_worm_7 = new MultiPartEntityPart(this, "part7", 0.3125F, 0.3125F), 
				sludge_worm_8 = new MultiPartEntityPart(this, "part8", 0.3125F, 0.3125F),
				sludge_worm_9 = new MultiPartEntityPart(this, "part9", 0.3125F, 0.3125F)
			};
		//tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(3, new EntityAIWander(this, 1D, 1));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, true));

	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.5D);
	}

	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	//stuns the mob
    @Override
    public boolean isMovementBlocked() {
    //	if (hurtResistantTime > 0){
    //		return true;
    //	} else {
   	return false;
    //	}
    }

	public void onLivingUpdate() {
		super.onLivingUpdate();

		//moveHelper.setMoveTo(posX + ranPosX, posY, posZ + ranPosZ, 0.70D);
		setMoveForward(0.2F);
		setHitBoxes();
		
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float dmg) {
		if (part == sludge_worm_5){
			Damage(source , dmg);
			return true;	
		} else {
			dmg = 0;
			return false;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source instanceof EntityDamageSource && ((EntityDamageSource) source).getIsThornsDamage()) {
			Damage(source, amount);
		}
		return true;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		return EntitySmolSludgeWorm.class != entity;
	}

	protected boolean Damage(DamageSource Source, float DMGAmmount) {
		return super.attackEntityFrom(Source, DMGAmmount);
	}

	@Override
	public Entity[] getParts() {
		return sludge_worm_Array;
	}

	private void setHitBoxes() {
		
		if(ticksExisted == 1){
			sludge_worm_2.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
			sludge_worm_3.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
			sludge_worm_4.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
			sludge_worm_5.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
			sludge_worm_6.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
			sludge_worm_7.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
			sludge_worm_8.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
			sludge_worm_9.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
		}

		sludge_worm_1.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
    	movePiecePos(sludge_worm_2, sludge_worm_1, 2.75F, 4);
    	movePiecePos(sludge_worm_3, sludge_worm_2, 2.75F, 4);
    	movePiecePos(sludge_worm_4, sludge_worm_3, 2.75F, 4);
    	movePiecePos(sludge_worm_5, sludge_worm_4, 2.75F, 4);
    	movePiecePos(sludge_worm_6, sludge_worm_5, 2.75F, 4);
    	movePiecePos(sludge_worm_7, sludge_worm_6, 2.75F, 4);
    	movePiecePos(sludge_worm_8, sludge_worm_7, 2.75F, 3);
    	movePiecePos(sludge_worm_9, sludge_worm_8, 2.75F, 3);
	}

	public static void movePiecePos(MultiPartEntityPart targetPart, MultiPartEntityPart destinationPart, float speed, float yawSpeed){
		targetPart.posX += ((destinationPart.posX - targetPart.posX) / speed);
		targetPart.posY += ((destinationPart.posY - targetPart.posY) / speed);
		targetPart.posZ += ((destinationPart.posZ - targetPart.posZ) / speed);
		targetPart.rotationYaw += ((destinationPart.rotationYaw - targetPart.rotationYaw) / yawSpeed);
		
		targetPart.setLocationAndAngles(targetPart.posX, targetPart.posY, targetPart.posZ, targetPart.rotationYaw, 0);
	}

	@Override
	public World getWorld() {
		return getEntityWorld();
	}

}
