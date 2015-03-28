package thebetweenlands.blocks.tree;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTreeFungus extends Block {

	private IIcon sideIcon;

	public BlockTreeFungus() {
		super(Material.wood);
		setHardness(0.2F);
		setCreativeTab(ModCreativeTabs.plants);
		setStepSound(Block.soundTypeWood);
		setBlockName("thebetweenlands.treeFungus");
		setBlockTextureName("thebetweenlands:treeFungus");
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return Item.getItemFromBlock(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = reg.registerIcon(getTextureName());
		sideIcon = reg.registerIcon(getTextureName() + "Side");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return side > 1 ? sideIcon : blockIcon;
	}

}
