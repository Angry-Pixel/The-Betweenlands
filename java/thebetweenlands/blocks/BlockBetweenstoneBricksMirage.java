package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockBetweenstoneBricksMirage extends Block {

	public BlockBetweenstoneBricksMirage(Material material) {
		super(material);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.betweenstoneBricksMirage");
		setBlockTextureName("thebetweenlands:betweenstoneBricks");
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    public boolean isOpaqueCube() {
        return false;
    }
}