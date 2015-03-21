package thebetweenlands.entities.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;

public class EntityAngler extends EntityWaterMob implements IEntityBL {
	public EntityAngler(World par1World) {
		super(par1World);
		setSize(1F, 1F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	protected String getHurtSound() {
		if (rand.nextBoolean())
			return "thebetweenlands:anglerAttack1";
		else
			return "thebetweenlands:anglerAttack2";
	}

	protected String getDeathSound() {
		return "thebetweenlands:anglerDeath";
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	protected void dropFewItems(boolean par1, int par2) {
		int var3 = rand.nextInt(3) + rand.nextInt(1 + par2);
		int var4;

		for (var4 = 0; var4 < var3; ++var4) {
			dropItem(Items.leather, 1);
		}
	}

	public boolean getCanSpawnHere() {
		return worldObj.getBlock((int) posX, (int) posY, (int) posZ) == BLBlockRegistry.swampWater;
	}

	protected Entity getTarget() {
		return entityToAttack;
	}

}
