package thebetweenlands.blocks.terrain;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockFallenLeaves extends BlockBush {
    public BlockFallenLeaves() {
    	setHardness(0.1F);
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.fallenLeaves");
		setBlockTextureName("thebetweenlands:fallenLeaves");
        setBlockBounds(0, 0.0F, 0, 1.0F, 0.05F, 1.0F);
    }
    
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    	entity.motionX *= 0.9D;
        entity.motionZ *= 0.9D;
    }
    
    @Override
    protected boolean canPlaceBlockOn(Block block) {
        return block.isOpaqueCube();
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return world.getBlock(x, y - 1, z).isOpaqueCube();
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
}