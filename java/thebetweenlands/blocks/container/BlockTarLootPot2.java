package thebetweenlands.blocks.container;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.tileentities.TileEntityTarLootPot2;

public class BlockTarLootPot2 extends BlockTarLootPot1 {

	public BlockTarLootPot2() {
		super();
		setBlockName("thebetweenlands.tarLootPot2");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityTarLootPot2();
	}

}
