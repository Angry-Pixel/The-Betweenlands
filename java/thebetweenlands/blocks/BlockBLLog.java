package thebetweenlands.blocks;

import java.util.ArrayList;

import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBLLog extends BlockLog {
	
	private String type;
	@SideOnly(Side.CLIENT)
	private IIcon iconSide, iconTop;

	public BlockBLLog(String blockName) {
		setCreativeTab(ModCreativeTabs.plants);
		type = blockName;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands." + type));
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		if (type.equals("sapTreeLog")) {
			ArrayList<ItemStack> drops = new ArrayList<ItemStack>();;
			for (int i = 0; i < 1 + world.rand.nextInt(2 + fortune); i++)
				drops.add(ItemMaterialsBL.createStack(EnumMaterialsBL.SAP_BALL));
			return drops;
		}
		return super.getDrops(world, x, y, z, metadata, fortune);
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return type.equals("sapTreeLog") ? true : super.canSilkHarvest(world, player, x, y, z, metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected IIcon getSideIcon(int meta) {
		return iconSide;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected IIcon getTopIcon(int meta) {
		return iconTop;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		iconSide = iconRegister.registerIcon(getTextureName());
		if(type.equals("weedWood") || type.equals("weedWoodBark") )
			iconTop = iconSide;
		else
			iconTop = iconRegister.registerIcon(getTextureName()+"Top");
	}
}