package thebetweenlands.blocks.terrain;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
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

public class BlockGenericStone
        extends Block
        implements ISubBlocksBlock
{
	public static final String[] iconPaths = new String[] { "corruptBetweenstone", "cragRock" }; //more room here for subtypes..
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

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

		int i = 0;
		for (String path : iconPaths)
            this.icons[i++] = reg.registerIcon("thebetweenlands:" + path);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta < 0 || meta >= this.icons.length)
			return null;
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
}
