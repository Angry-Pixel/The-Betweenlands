package thebetweenlands.blocks.terrain;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.items.armor.ItemRubberBoots;

public class BlockPeat
extends Block
{
	public BlockPeat() {
		super(Material.ground);
		setHardness(0.5F);
		setStepSound(soundTypeGravel);
		setHarvestLevel("shovel", 0);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.peat");
		setBlockTextureName("thebetweenlands:peat");
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		float f = 0.125F;
		return AxisAlignedBB.getBoundingBox((double)x, (double)y, (double)z, (double)(x + 1), (double)((float)(y + 1) - f), (double)(z + 1));
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		boolean canWalk = entity instanceof EntityPlayer && ((EntityPlayer)entity).inventory.armorInventory[0] != null && 
				((EntityPlayer)entity).inventory.armorInventory[0].getItem() instanceof ItemRubberBoots;
		if(!canWalk) {
			entity.motionX *= 0.85D;
			entity.motionY *= 0.85D;
			entity.motionZ *= 0.85D;
		}
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(world.rand.nextInt(60) == 0) {
			BLParticle.FLY.spawn(world, x, y + 1, z);
		}
	}
}
