package thebetweenlands.blocks.container;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.tileentities.TileEntityTarLootPot3;

public class BlockTarLootPot3 extends BlockTarLootPot1 {

	public BlockTarLootPot3() {
		super();
		setBlockName("thebetweenlands.tarLootPot3");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityTarLootPot3();
	}

}
