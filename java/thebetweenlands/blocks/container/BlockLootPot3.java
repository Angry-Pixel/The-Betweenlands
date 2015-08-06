package thebetweenlands.blocks.container;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.tileentities.TileEntityLootPot3;

public class BlockLootPot3 extends BlockLootPot1 {

	public BlockLootPot3() {
		super();
		setBlockName("thebetweenlands.lootPot3");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLootPot3();
	}

}
