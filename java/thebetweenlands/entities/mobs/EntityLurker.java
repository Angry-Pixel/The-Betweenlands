package thebetweenlands.entities.mobs;

import thebetweenlands.blocks.BLBlockRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityLurker extends EntityMob implements IEntityBL {
	public EntityLurker(World world) {
		super(world);
		
		this.setSize(2F, 0.9F);
		
		this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIWander(this, 1D));
		this.tasks.addTask(2, new EntityAILookIdle(this));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(0.5D);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
			this.setDead();
		}
	}
	
	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
	}
	
	@Override
    public boolean isInWater() {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
    }
	
	@Override
	public boolean getCanSpawnHere() {
		return this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL &&
				this.worldObj.getBlock((int) this.posX, (int) this.posY, (int) this.posZ) == BLBlockRegistry.swampWater;
	}
	
	@Override
	public boolean isAIEnabled() {
		return true;
	}
}