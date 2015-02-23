package thebetweenlands.blocks.terrain;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGenericOre extends Block {
	private String type;
	private EnumMaterialsBL oreDrops;
	@SideOnly(Side.CLIENT)
	private IIcon sideIcon, sideIconActive;

	public BlockGenericOre(String blockName, EnumMaterialsBL blockDrops) {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(ModCreativeTabs.blocks);
        type = blockName;
        oreDrops = blockDrops;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	private ItemStack getOreDropped() {
		return ItemMaterialsBL.createStack(oreDrops);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		
		if(oreDrops != null)
			for(int dropFortune = 0; dropFortune < 1 + fortune; dropFortune++)
				drops.add(getOreDropped());
		else
			drops.add(new ItemStack(Item.getItemFromBlock(this)));
		
		return drops;
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands." + type));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
