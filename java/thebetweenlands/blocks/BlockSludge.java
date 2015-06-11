package thebetweenlands.blocks;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.entities.mobs.EntitySludge;

import java.util.Random;

public class BlockSludge extends Block {
    public BlockSludge() {
    	super(Material.ground);
    	setHardness(0.1F);
		setStepSound(soundTypeGravel);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.sludge");
		setBlockTextureName("thebetweenlands:sludge");
        setBlockBounds(0, 0.0F, 0, 1.0F, 0.125F, 1.0F);
    }
    
    /**
     * Sets the block at the given position. Block will be removed after 1 minute.
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public void generateBlockTemporary(World world, int x, int y, int z) {
        world.setBlock(x, y, z, this);
        world.scheduleBlockUpdate(x, y, z, this, 20*60);
    }
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random rnd) {
    	world.setBlock(x, y, z, Blocks.air);
    }
    
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    	if(entity instanceof EntitySludge == false && entity.onGround) {
    		entity.setInWeb();
    	}
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return 0;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
    	if(blockAccess.getBlock(x, y, z) == this) {
    		return false;
    	}
        return super.shouldSideBeRendered(blockAccess, x, y, z, side);
    }
    
    @Override
	public boolean isOpaqueCube() {
		return false;
	}
    
    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return null;
    }
    
    @Override
    public boolean isNormalCube() {
    	return false;
    }
    
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(world, x, y-1, z);
    }
    
    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
    	return World.doesBlockHaveSolidTopSurface(world, x, y-1, z);
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
    	if(!World.doesBlockHaveSolidTopSurface(world, x, y-1, z)) {
    		world.setBlock(x, y, z, Blocks.air);
    	}
    }
}