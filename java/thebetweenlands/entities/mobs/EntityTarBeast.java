package thebetweenlands.entities.mobs;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.AxeBL;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.items.PickaxeBL;
import thebetweenlands.items.SpadeBL;
import thebetweenlands.items.SwordBL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityTarBeast extends EntityMob implements IEntityBL {

	public EntityTarBeast(World world) {
		super(world);
		setSize(1.25F, 2F);
		stepHeight = 2.0F;
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
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && worldObj.getBlock((int)posX, (int)posY, (int)posZ) == BLBlockRegistry.tarFluid;
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
					return super.attackEntityFrom(source, MathHelper.ceiling_float_int(damage * 0.5F));
				}
		}
		return super.attackEntityFrom(source, damage);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.isRemote && worldObj.getWorldTime()%5 == 0) 
			renderParticles(worldObj, posX, posY + rand.nextDouble() * 2.1D, posZ, rand);
	}

	@SideOnly(Side.CLIENT)
	public void renderParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 3; ++count) {
			double velX = 0.0D;
			double velY = 0.0D;
			double velZ = 0.0D;
			int motionX = rand.nextInt(2) * 2 - 1;
			int motionZ = rand.nextInt(2) * 2 - 1;
			velY = (rand.nextFloat() - 0.5D) * 0.125D;
			velZ = rand.nextFloat() * 0.5F * motionZ;
			velX = rand.nextFloat() * 0.5F * motionX;
			TheBetweenlands.proxy.spawnCustomParticle("splashTarBeast", world , x, y, z, velX * 0.15D, velY * 0.1D, velZ * 0.15D, 0);
		}
	}

}
