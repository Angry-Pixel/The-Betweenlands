package thebetweenlands.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnergyBarrier extends Block {

	public BlockEnergyBarrier() {
		super(Material.glass);
		setStepSound(soundTypeGlass);
		setBlockName("thebetweenlands:energyBarrier");
		setBlockTextureName("thebetweenlands:barrier");
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockUnbreakable();
		setResistance(6000000.0F);
		setLightLevel(0.8F);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return world.getBlock(x, y, z) == this ? false : super.shouldSideBeRendered(world, x, y, z, side);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getCurrentEquippedItem();
		if (stack != null && stack.getItem() == BLItemRegistry.shockwaveSword) {
			if (!world.isRemote) {
				world.playAuxSFXAtEntity(null, 2001, x, y + 1, z, Block.getIdFromBlock(world.getBlock(x, y, z)));
				world.setBlockToAir(x, y, z);
			}
			return true;
		}
		return false;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		for( int i = 0; i < 4; i++ ) {
			double particleX = x + rand.nextFloat();
			double particleY = y + rand.nextFloat();
			double particleZ = z + rand.nextFloat();
			double motionX;
			double motionY;
			double motionZ;
			int multi = rand.nextInt(2) * 2 - 1;

			motionX = (rand.nextFloat() - 0.5D) * 0.5D;
			motionY = (rand.nextFloat() - 0.5D) * 0.5D;
			motionZ = (rand.nextFloat() - 0.5D) * 0.5D;

			if( world.getBlock(x - 1, y, z) != this && world.getBlock(x + 1, y, z) != this ) {
				particleX = x + 0.5D + 0.25D * multi;
				motionX = rand.nextFloat() * 2.0F * multi;
			} else {
				particleZ = z + 0.5D + 0.25D * multi;
				motionZ = rand.nextFloat() * 2.0F * multi;
			}
				BLParticle.PORTAL.spawn(world, particleX, particleY, particleZ, motionX, motionY, motionZ, 0);
			}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		double twoPixels = 0.125D;
		return AxisAlignedBB.getBoundingBox(x + twoPixels, y, z + twoPixels, x + 1 - twoPixels, y + 1, z + 1 - twoPixels);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			int Knockback = 1;
			entity.attackEntityFrom(DamageSource.cactus, 1);
			entity.addVelocity(MathHelper.sin(entity.rotationYaw * 3.141593F / 180.0F) * Knockback * 0.1F, 0.08D, -MathHelper.cos(entity.rotationYaw * 3.141593F / 180.0F) * Knockback * 0.1F);
			entity.worldObj.playSoundAtEntity(entity, "mob.ghast.scream", 1F, 0.5F);
		}
	}
}