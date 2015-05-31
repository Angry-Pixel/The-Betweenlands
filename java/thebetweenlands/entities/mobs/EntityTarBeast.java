package thebetweenlands.entities.mobs;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.items.*;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;

public class EntityTarBeast extends EntityMob implements IEntityBL {

	public EntityTarBeast(World world) {
		super(world);
		setSize(1.25F, 2.5F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(5.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected String getLivingSound() {
		int randomSound = rand.nextInt(3) + 1;
		return "thebetweenlands:tarBeastLiving" + randomSound;
	}

	@Override
	protected String getHurtSound() {
		if (rand.nextBoolean())
			return "thebetweenlands:tarBeastHurt1";
		else
			return "thebetweenlands:tarBeastHurt2";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:tarBeastDeath";
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) { // playStepSound
		int randomSound = rand.nextInt(3) + 1;
		playSound("thebetweenlands:tarBeastStep" + randomSound, 1F, 1F);
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		entityDropItem(ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR), 0F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getSourceOfDamage() instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
			ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
			if (heldItem != null)
				if (heldItem.getItem() instanceof SwordBL || heldItem.getItem() instanceof AxeBL || heldItem.getItem() instanceof PickaxeBL || heldItem.getItem() instanceof SpadeBL) {
					return super.attackEntityFrom(source, damage);
				} else {
					return super.attackEntityFrom(source, MathHelper.ceiling_float_int((float) damage * 0.5F));
				}
		}
		return super.attackEntityFrom(source, damage);
	}

}
