package thebetweenlands.blocks.container;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.tileentities.TileEntityLootPot2;

public class BlockLootPot2 extends BlockLootPot1 {

	public BlockLootPot2() {
		super();
		setBlockName("thebetweenlands.lootPot2");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLootPot2();
	}

}
