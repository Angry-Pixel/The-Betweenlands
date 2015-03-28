package thebetweenlands.blocks.plants; import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockHanger extends BlockBush  implements IShearable {
	
	public BlockHanger() {
    	super(Material.plants);
        setHardness(0.0F);
		setCreativeTab(ModCreativeTabs.plants);
		setBlockName("thebetweenlands.hanger");
		setBlockTextureName("thebetweenlands:hanger");
		setStepSound(Block.soundTypeGrass);
    	setTickRandomly(true);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int yy = y - 1;
		if (world.isAirBlock(x, yy, z) && canBlockStay(world, x, yy, z))
			world.setBlock(x, yy, z, this);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(int id, Random random, int fortune) {
		return null;
	}

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(this, 1));
        return ret;
    }

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return isValidBlock(world.getBlock(x, y + 1, z)) && canBlockStay(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return isValidBlock(world.getBlock(x, y + 1, z));
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
		int meta = world.getBlockMetadata(x, y, z);
		ItemStack item = null;
		if (world.isAirBlock(x, y + 1, z)) {
			world.setBlockToAir(x, y, z);
		}
		canBlockStay(world, x, y, z);
	}

	private boolean isValidBlock(Block block) {
		return block.getMaterial().blocksMovement() || block == BLBlockRegistry.weedWoodLeaves || block == this;
	}

}
