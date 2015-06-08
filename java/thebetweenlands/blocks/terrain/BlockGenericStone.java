package thebetweenlands.blocks.terrain;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLBlockRegistry.ISubBlocksBlock;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemBlockGeneric;

import java.util.List;
import java.util.Random;

public class BlockGenericStone
        extends Block
        implements ISubBlocksBlock
{
	public static final String[] iconPaths = new String[] { "corruptBetweenstone", "cragrock", "mossyCragrockSide1", "mossyCragrockSide2" }; //more room here for subtypes..
	
	public static final int META_CRAGROCK = 1;
	public static final int META_MOSSYCRAGROCK1 = 2;
	public static final int META_MOSSYCRAGROCK2 = 3;
	
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	private IIcon mosTop;

	public BlockGenericStone() {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.genericStone");
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		icons = new IIcon[iconPaths.length];
		mosTop = reg.registerIcon("thebetweenlands:" + "mossyCragrockTop");
		int i = 0;
		for (String path : iconPaths)
            this.icons[i++] = reg.registerIcon("thebetweenlands:" + path);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta < 0 || meta >= this.icons.length)
			return null;
		if(meta == 2){
			if(side == 0)
				return this.icons[1];
			else if(side == 1)
				return this.mosTop;
			else
				return this.icons[meta];
		}
		return this.icons[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(Item id, CreativeTabs tab, List list) {
		for (int i = 0; i < this.icons.length; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	public int damageDropped(int meta) {
		//What's the point of this? Drops items with wrong damage value
		//return meta == 0 ? 1 : meta;
		return meta;
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public Class<? extends ItemBlock> getItemBlockClass() {
		return ItemBlockGeneric.class;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		if (!world.isRemote) {
			if (world.getBlockMetadata(x, y, z) == META_MOSSYCRAGROCK1) {
				int i1 = x + random.nextInt(3) - 1;
				int j1 = y + random.nextInt(4) - 3;
				int k1 = z + random.nextInt(3) - 1;
				
				Block block = world.getBlock(i1, j1, k1);
				int meta = world.getBlockMetadata(i1, j1, k1);
				if (block instanceof BlockGenericStone && meta == META_CRAGROCK) {
					if (world.getBlock(i1, j1 + 1, k1) instanceof BlockGenericStone && (world.getBlockMetadata(i1, j1 + 1, k1) == META_MOSSYCRAGROCK1 || world.getBlockMetadata(i1, j1 + 1, k1) == META_CRAGROCK))
						world.setBlockMetadataWithNotify(i1, j1, k1, META_MOSSYCRAGROCK2, 2);
					else if (world.getBlock(i1, j1, k1) == Blocks.air)
						world.setBlockMetadataWithNotify(i1, j1, k1, META_MOSSYCRAGROCK1, 2);
				}
			}
		}
	}
}
