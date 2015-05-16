package thebetweenlands.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.entities.mobs.EntitySiltCrab;

public class BlockSilt
        extends Block
{
	public BlockSilt() {
		super(Material.sand);
		setHardness(0.5F);
		setStepSound(soundTypeGravel);
		setHarvestLevel("shovel", 0);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.silt");
		setBlockTextureName("thebetweenlands:silt");
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        float yOffset = 0.125F;
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, (y + 1) - yOffset, z + 1);
    }

	@Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if(!(entity instanceof EntitySiltCrab)) {
			entity.motionX *= 0.4D;
			entity.motionZ *= 0.4D;
		}
    }
}
