package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

import java.util.ArrayList;
import java.util.Random;

public class BlockWeepingBlue extends BlockDoubleHeightPlant {
	public IIcon modelTexture1;

	public BlockWeepingBlue() {
		super("weepingBlue");
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.MODEL_PLANT.id();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		modelTexture1 = reg.registerIcon("thebetweenlands:weepingBlue");
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(BLItemRegistry.weepingBluePetal, 1 + world.rand.nextInt(3) + world.rand.nextInt(fortune + 1)));
		return drops;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(world.getBlock(x, y - 1, z) != this) {
			world.spawnParticle("dripWater", x + 0.6F + rand.nextFloat()/3F*(rand.nextInt(2) == 0 ? 1 : -1), y + 1.6F + rand.nextFloat()/2F, z + 0.6F + rand.nextFloat()/3F*(rand.nextInt(2) == 0 ? 1 : -1), 0D, 0D, 0D);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return modelTexture1;
	}
	
	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return false;
	}
}
