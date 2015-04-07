package thebetweenlands.entities.mobs;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;

public class EntityAngler extends EntityMob implements IEntityBL {

	public EntityAngler(World world) {
		super(world);
		setSize(1F, 1F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	@Override
	protected String getHurtSound() {
		if (rand.nextBoolean())
			return "thebetweenlands:anglerAttack1";
		else
			return "thebetweenlands:anglerAttack2";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:anglerDeath";
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		int amount = rand.nextInt(3) + rand.nextInt(1 + looting);
		int count;

		for (count = 0; count < amount; ++count) {
			dropItem(Items.leather, 1);
		}
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.getBlock((int) posX, (int) posY, (int) posZ) == BLBlockRegistry.swampWater;
	}

}
