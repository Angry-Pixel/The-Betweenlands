package thebetweenlands.entities.mobs;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;

public class EntityLurker extends EntityMob implements IEntityBL {
	private ChunkCoordinates movementTarget;
	
	public EntityLurker(World world) {
		super(world);
		
		this.setSize(1.5F, 0.9F);
		
		this.tasks.taskEntries.clear();
		this.targetTasks.taskEntries.clear();
		
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityAngler.class, 0.5D, false));
		this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityAngler.class, 8.0F));
		this.tasks.addTask(4, new EntityAILookIdle(this));

		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityAngler.class, 0, true));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.5D);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
		this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0D);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.isInWater()) {
			if (this.getAttackTarget() != null &&
					this.getAir() >= 80) {
				this.faceEntity(this.getAttackTarget(), this.rotationPitch, 360F);
				this.moveForward += 1F;
				this.motionY += Math.signum(this.getAttackTarget().posY - this.posY) * 0.03D;
			} else {
				this.motionY += 0.03D;
			}
		}
	}
	
	@Override
	public boolean getCanSpawnHere() {
		return this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL &&
				this.worldObj.getBlock((int) this.posX, (int) this.posY, (int) this.posZ) == BLBlockRegistry.swampWater;
	}
	
	@Override
	protected boolean isAIEnabled() {
		return true;
	}
}