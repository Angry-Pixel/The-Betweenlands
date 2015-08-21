package thebetweenlands.blocks.plants;

import java.util.Random;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.stalactite.StalactiteData;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCaveMoss extends BlockBLHanger {
	private IIcon lower;

	public BlockCaveMoss() {
		super("caveMoss");
	}

	@Override
	public int getRenderType() {
		return 6;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if (world.getBlock(x, y - 1, z) != this) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		world.setBlockMetadataWithNotify(x, y, z, world.getBlock(x, y - 1, z) != this ? 1 : 0, 2);
		super.onNeighborBlockChange(world, x, y, z, neighbor);
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		lower = iconRegister.registerIcon(textureName + "Lower");
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		return metadata == 1 ? lower : blockIcon;
	}

	@Override
	protected boolean isValidBlock(Block block) {
		return block == BLBlockRegistry.betweenstone || block == this;
	}
}
