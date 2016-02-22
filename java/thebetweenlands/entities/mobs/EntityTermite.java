package thebetweenlands.entities.mobs;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

public class EntityTermite extends EntityMob implements IEntityBL {

	public EntityTermite(World world) {
		super(world);
		setSize(0.9F, 0.6F);
		tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1D, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) {
		playSound("mob.spider.step", 0.15F, 1.0F);
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:squish";
	}

	@Override
	public String pageName() {
		return "termite";
	}
}
