package thebetweenlands.blocks.terrain;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry.ISubBlocksBlock;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.block.ItemBlockGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFarmedDirt extends Block implements ISubBlocksBlock {

	public static final String[] iconPaths = new String[] { "purifiedSwampDirt", "dugSwampDirt", "dugSwampGrass", "dugPurifiedSwampDirt", "fertDirt", "fertGrass", "fertPurifiedSwampDirt", "fertDirtDecayed", "fertGrassDecayed" };
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	private IIcon sideIcon;
	
	public BlockFarmedDirt() {
		super(Material.ground);
		setHardness(0.5F);
		setStepSound(soundTypeGravel);
		setHarvestLevel("shovel", 0);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.farmedDirt");
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		sideIcon = reg.registerIcon("thebetweenlands:swampDirt");
		icons = new IIcon[iconPaths.length];

		int i = 0;
		for (String path : iconPaths)
			icons[i++] = reg.registerIcon("thebetweenlands:" + path);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta < 0 || meta >= icons.length)
			return null;
		
		return meta == 0 ? icons[meta] : side == 1 && meta != 0 ? icons[meta] : sideIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(Item id, CreativeTabs tab, List list) {
		for (int i = 0; i < icons.length; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	public int damageDropped(int meta) {
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

}
