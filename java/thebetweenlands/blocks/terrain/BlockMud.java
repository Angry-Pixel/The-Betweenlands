package thebetweenlands.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockMud
        extends Block
{
	public BlockMud() {
		super(Material.ground);
		setHardness(0.5F);
		setStepSound(soundTypeGravel);
		setHarvestLevel("shovel", 0);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.mud");
		setBlockTextureName("thebetweenlands:mud");
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }
	
	@Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        entity.motionX *= 0.2D;
        entity.motionY *= 0.2D;
        entity.motionZ *= 0.2D;
    }
}
