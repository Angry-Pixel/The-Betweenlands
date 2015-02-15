package thebetweenlands.blocks;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class BlockSpawner extends BlockMobSpawner {
	private final String mobName;

	public BlockSpawner(String mobName) {
		disableStats();
		setHardness(5.0F);
		this.mobName = mobName;
		setStepSound(soundTypeMetal);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityMobSpawner tile = new TileEntityMobSpawner();
		tile.func_145881_a().setEntityName(mobName);
		return tile;
	}
}