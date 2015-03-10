package thebetweenlands.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DoubleHeightPlant extends BlockDoublePlant implements IShearable {
	@SideOnly(Side.CLIENT)
	private IIcon top, bottom;
	private final String name;

	public DoubleHeightPlant(String name) {
		this(name, 1);
	}

	public DoubleHeightPlant(String name, float width) {
		this.name = name;
		setCreativeTab(ModCreativeTabs.plants);
		setStepSound(Block.soundTypeGrass);
		float w = (1F - width) / 2F;
		setBlockBounds(w, 0, w, width + w, 1, width + w);
		setBlockName("thebetweenlands." + name.substring(0, 1).toLowerCase() + name.substring(1));
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		if (world.rand.nextInt(8) != 0)
			return drops;

		if ("Sundew".equals(name))
			drops.add(new ItemStack(Items.glowstone_dust, 1 + fortune));
		else if ("WeepingBlue".equals(name))
			drops.add(ItemMaterialsBL.createStack(EnumMaterialsBL.WEEPING_BLUE_PETAL, 1 + fortune));
		else if ("SwampReed".equals(name))
			drops.add(ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED, 1 + fortune));
		else {
			ItemStack seed = ForgeHooks.getGrassSeed(world);
			if (seed != null)
				drops.add(seed);
		}

		return drops;
	}

	@Override	
    protected boolean canPlaceBlockOn(Block block) {
        return block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland || block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.swampGrass;
    }

	@Override
	public int getRenderType() {
		return BlockRenderIDs.DOUBLE_PLANTS.id();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return top;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return func_149887_c(world.getBlockMetadata(x, y, z)) ? top : bottom;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		top = reg.registerIcon("thebetweenlands:doublePlant" + name + "Top");
		bottom = reg.registerIcon("thebetweenlands:doublePlant" + name + "Bottom");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess access, int x, int y, int z) {
		return 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item));
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(new ItemStack(this, 1, world.getBlockMetadata(x, y, z) & 7));
		return ret;
	}
}