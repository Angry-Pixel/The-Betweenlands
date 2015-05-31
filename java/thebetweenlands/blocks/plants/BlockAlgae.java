package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockAlgae extends BlockBush {
    public BlockAlgae() {
    	setHardness(0.1F);
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.algae");
		setBlockTextureName("thebetweenlands:algae");
        setBlockBounds(0, 0.0F, 0, 1.0F, 0.125F, 1.0F);
    }
    
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    	entity.motionX *= 0.8D;
        entity.motionZ *= 0.8D;
    }
    
    @Override
    protected boolean canPlaceBlockOn(Block block) {
        return block == BLBlockRegistry.swampWater;
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return y >= 0 && y < 256 ? world.getBlock(x, y - 1, z) == BLBlockRegistry.swampWater && world.getBlockMetadata(x, y - 1, z) == 0 : false;
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