package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DoubleHeightPlant extends BlockDoublePlant implements IShearable {
	@SideOnly(Side.CLIENT)
	public IIcon topIcon, bottomIcon;
	private final String name;
	Random rnd = new Random();

	private int renderType = -1;
	
	public DoubleHeightPlant(String name) {
		this(name, 1);
	}

	public DoubleHeightPlant(String name, float width) {
		this.name = name;
		setCreativeTab(ModCreativeTabs.plants);
		setStepSound(Block.soundTypeGrass);
		this.setTickRandomly(true);
		float w = (1F - width) / 2F;
		setBlockBounds(w, 0, w, width + w, 1, width + w);
		setBlockName("thebetweenlands." + name.substring(0, 1).toLowerCase() + name.substring(1));
	}

	public DoubleHeightPlant setRenderType(int renderType) {
		this.renderType = renderType;
		return this;
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
			drops.add(new ItemStack(this, 1));
		
		if ("CardinalFlower".equals(name))
			drops.add(new ItemStack(this, 1));
		
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		if (!"DoubleSwampTallgrass".equals(name)) ret.add(new ItemStack(this));
		if (!"Phragmites".equals(name)) ret.add(new ItemStack(this));
		if (!"TallCattail".equals(name)) ret.add(new ItemStack(this));
		if (!"Broomsedge".equals(name)) ret.add(new ItemStack(this));
		return drops;
	}

	@Override
	protected boolean canPlaceBlockOn(Block block) {
		return block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland || block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.swampGrass || block == BLBlockRegistry.deadGrass;
	}

	@Override
	public int getRenderType() {
		return this.renderType == -1 ? BlockRenderIDs.DOUBLE_PLANTS.id() : this.renderType;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return topIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return func_149887_c(world.getBlockMetadata(x, y, z)) ? topIcon : bottomIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		topIcon = reg.registerIcon("thebetweenlands:doublePlant" + name + "Top");
		if(!name.equals("Sundew"))
			bottomIcon = reg.registerIcon("thebetweenlands:doublePlant" + name + "Bottom");
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
