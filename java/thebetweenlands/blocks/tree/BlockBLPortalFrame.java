package thebetweenlands.blocks.tree;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLBlockRegistry.ISubBlocksBlock;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.items.block.ItemBlockGeneric;

import java.util.ArrayList;
import java.util.List;

public class BlockBLPortalFrame extends Block implements ISubBlocksBlock {
	
	public static final String[] iconPaths = new String[] { "portalCornertopleft", "portalTop", "portalCornertopright", "portalSideleft", "portalSideright", "portalCornerbottomleft", "portalBottom", "portalCornerbottomright" };
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public BlockBLPortalFrame() {
		super(Material.wood);
		setHardness(2.0F);
        setStepSound(soundTypeWood);
		setCreativeTab(BLCreativeTabs.plants);
		setBlockName("thebetweenlands.portalBarkFrame");
		setBlockTextureName("thebetweenlands:portalBark");
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands.portalBarkFrame"));
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(Item.getItemFromBlock(BLBlockRegistry.portalBark)));
		return drops;
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		icons = new IIcon[iconPaths.length];
		blockIcon = reg.registerIcon(getTextureName());
		int i = 0;
		for (String path : iconPaths)
			icons[i++] = reg.registerIcon("thebetweenlands:" + path);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		// a little hacky because of magic numbers needed to flip textures - will do for now until I make a renderer
		switch(meta) {
		case 0:
			return side == 3 ? icons[meta] : side == 2 ? icons[meta + 2] : blockIcon;
		case 1:
			return side == 3 || side == 2 ? icons[meta] : blockIcon;
		case 2:
			return side == 3 ? icons[meta] : side == 2 ? icons[meta - 2] : blockIcon;
		case 3:
			return side == 3 ? icons[meta] : side == 2 ? icons[meta + 1] : blockIcon;
		case 4:
			return side == 3 ? icons[meta] : side == 2 ? icons[meta - 1] : blockIcon;
		case 5:
			return side == 3 ? icons[meta] : side == 2 ? icons[meta + 2] : blockIcon;
		case 6:
			return side == 3 || side == 2 ? icons[meta] : blockIcon;
		case 7:
			return side == 3 ? icons[meta] : side == 2 ? icons[meta - 2] : blockIcon;
		case 8:
			return side == 4 ? icons[meta - 8] : side == 5 ? icons[meta - 6 ] :blockIcon;
		case 9:
			return side == 4 || side == 5 ? icons[meta - 8] : blockIcon;
		case 10:
			return side == 4 ? icons[meta - 8] : side == 5 ? icons[meta - 10 ] :blockIcon;
		case 11:
			return side == 4 ? icons[meta - 8] : side == 5 ? icons[meta - 7 ] :blockIcon;
		case 12:
			return side == 4 ? icons[meta - 8] : side == 5 ? icons[meta - 9 ] :blockIcon;
		case 13:
			return side == 4 ? icons[meta - 8] : side == 5 ? icons[meta - 6 ] :blockIcon;
		case 14:
			return side == 4 || side == 5 ? icons[meta - 8] : blockIcon;
		case 15:
			return side == 4 ? icons[meta - 8] : side == 5 ? icons[meta - 10 ] :blockIcon;
		default :
			return blockIcon;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(Item id, CreativeTabs tab, List list) {
		for (int i = 0; i < 16; i++)
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