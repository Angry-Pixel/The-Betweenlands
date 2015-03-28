package thebetweenlands.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.tileentities.TileEntityWisp;

public class BlockWisp
        extends BlockContainer
{
	public BlockWisp() {
		super(Material.rock);
		setStepSound(soundTypeStone);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.wisp");
		setHardness(0.0f);
		setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		setBlockTextureName("thebetweenlands:wisp");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityWisp();
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
    @Override
	public void onBlockAdded(World world, int x, int y, int z) {
    	world.setBlock(x, y, z, this, world.rand.nextInt(this.colors.length / 2) * 2, 2);
    }
    
    @Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int i) {
        return null;
    }
    
    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return true;
    }
    
    //Colors can be added here, always add a pair of colors for outer color and inner color
    public int[] colors = new int[] {
    		0xFF7F1659, 0xFFFFFFFF, //Pink/White
    		0xFF0707C8, 0xFFC8077B, //Blue/Pink
    		0xFF0E2E0B, 0xFFC8077B, //Green/Yellow/White
    		0xFF9A6908, 0xFF4F0303  //Red/Yellow/White
    };
    
    /**
     * Sets the block at the giving position to a wisp block with a random color
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public void generateBlock(World world, int x, int y, int z) {
    	world.setBlock(x, y, z, this, world.rand.nextInt(this.colors.length / 2) * 2, 2);
    }
}
