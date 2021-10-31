package thebetweenlands.common.block.container;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.tile.TileEntityMudLootPot1;
import thebetweenlands.common.tile.TileEntityMudLootPot2;
import thebetweenlands.common.tile.TileEntityMudLootPot3;

public class BlockMudLootPot extends BlockLootPot {
	public BlockMudLootPot() {
		super(Material.ROCK);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		IBlockState state = this.getStateFromMeta(meta);
		switch(state.getValue(VARIANT)) {
		default:
		case POT_1:
			return new TileEntityMudLootPot1();
		case POT_2:
			return new TileEntityMudLootPot2();
		case POT_3:
			return new TileEntityMudLootPot3();
		}
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.SOLID;
    }
}
