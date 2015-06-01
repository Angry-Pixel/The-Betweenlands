package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;

import java.util.Random;

public class BlockBLSlabStone extends BlockSlab {

	private final String textureName;
	private Block dropsThis;
	
	public BlockBLSlabStone(boolean fullBlock, Material material, String name, Block blockDrops) {
		super(fullBlock, material);
		textureName = "thebetweenlands:" + name;
		dropsThis = blockDrops;
		setHardness(2.0F);
		setLightOpacity(0);
		setHarvestLevel("pickaxe", 0);
		if(dropsThis == null)
			setCreativeTab(ModCreativeTabs.blocks);
		setStepSound(Block.soundTypeStone);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		setBlockName("thebetweenlands." + name + "Slab");
	}
	
	@Override
	public String func_150002_b(int metadata) {
	   return getUnlocalizedName();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister icon) {
		blockIcon = icon.registerIcon(textureName);
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		if(dropsThis == null)
			return Item.getItemFromBlock(this);
		return Item.getItemFromBlock(dropsThis);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return this.field_150004_a ? 2 : 1;
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (block == null)
	    	return null;
	    int meta = world.getBlockMetadata(x, y, z) & 7;
	    return new ItemStack(this, 1, meta);
	}
}