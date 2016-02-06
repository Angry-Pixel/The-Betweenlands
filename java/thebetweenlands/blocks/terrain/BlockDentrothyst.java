package thebetweenlands.blocks.terrain;

import java.util.List;

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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLBlockRegistry.ISubBlocksBlock;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.items.block.ItemBlockGeneric;

/**
 * Created by Bart on 21-6-2015.
 */
public class BlockDentrothyst extends Block implements ISubBlocksBlock {

	public static final String[] iconPaths = new String[] { "dentrothyst1", "dentrothyst2" };

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public BlockDentrothyst() {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.dentrothyst");
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
	public Class<? extends ItemBlock> getItemBlockClass() {
		return ItemBlockGeneric.class;
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}


	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderBlockPass ()
	{
		return 1;
	}

	@Override
	public boolean shouldSideBeRendered (IBlockAccess iblockaccess, int x, int y, int z, int side) {
		Block block = iblockaccess.getBlock(x, y, z);
		int meta = iblockaccess.getBlockMetadata(x, y, z);
		return block != BLBlockRegistry.dentrothyst;
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}
}
