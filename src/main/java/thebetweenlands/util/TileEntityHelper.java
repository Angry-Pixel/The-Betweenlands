package thebetweenlands.util;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;

public class TileEntityHelper {
	/**
	 * Reads a TileEntity in a thread safe manner for {@link Block#getActualState(net.minecraft.block.state.IBlockState, IBlockAccess, BlockPos)} or 
	 * {@link Block#getExtendedState(net.minecraft.block.state.IBlockState, IBlockAccess, BlockPos)}
	 * @param world
	 * @param pos
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends TileEntity> T getTileEntityThreadSafe(IBlockAccess world, BlockPos pos, Class<T> cls) {
		TileEntity te;

		if(world instanceof ChunkCache) {
			te = ((ChunkCache)world).func_190300_a(pos, EnumCreateEntityType.CHECK);
		} else {
			te = world.getTileEntity(pos);
		}

		if(cls.isInstance(te)) {
			return (T) te;
		}

		return null;
	}
}
