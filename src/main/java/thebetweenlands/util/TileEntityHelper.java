package thebetweenlands.util;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class TileEntityHelper {
	/**
	 * Safely gets a block state property of the block at the TileEntity
	 * @param te
	 * @param property
	 * @param defaultVal
	 * @return
	 */
	public static <P extends IUnlistedProperty<T>, T> T getStatePropertySafely(TileEntity te, Class<? extends Block> block, P property, T defaultVal) {
		return getStatePropertySafely(te, block, property, defaultVal, false, false);
	}

	/**
	 * Safely gets a block state property of the block at the TileEntity
	 * @param te
	 * @param property
	 * @param defaultVal
	 * @param actual
	 * @param extended
	 * @return
	 */
	public static <P extends IUnlistedProperty<T>, T> T getStatePropertySafely(TileEntity te, Class<? extends Block> block, P property, T defaultVal, boolean actual, boolean extended) {
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if(block.isInstance(state.getBlock())) {
			if(actual) {
				state = state.getActualState(te.getWorld(), te.getPos());
			}
			if(extended) {
				state = state.getBlock().getExtendedState(state, te.getWorld(), te.getPos());
			}
			if(state instanceof IExtendedBlockState) {
				return ((IExtendedBlockState)state).getValue(property);
			}
		}
		return defaultVal;
	}

	/**
	 * Safely gets a block state property of the block at the TileEntity
	 * @param te
	 * @param property
	 * @param defaultVal
	 * @return
	 */
	public static <P extends IProperty<T>, T extends Comparable<T>> T getStatePropertySafely(TileEntity te, Class<? extends Block> block, P property, T defaultVal) {
		return getStatePropertySafely(te, block, property, defaultVal, false, false);
	}

	/**
	 * Safely gets a block state property of the block at the TileEntity
	 * @param te
	 * @param property
	 * @param defaultVal
	 * @param actual
	 * @param extended
	 * @return
	 */
	public static <P extends IProperty<T>, T extends Comparable<T>> T getStatePropertySafely(TileEntity te, Class<? extends Block> block, P property, T defaultVal, boolean actual, boolean extended) {
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if(block.isInstance(state.getBlock())) {
			if(actual) {
				state = state.getActualState(te.getWorld(), te.getPos());
			}
			if(extended) {
				state = state.getBlock().getExtendedState(state, te.getWorld(), te.getPos());
			}
			return state.getValue(property);
		}
		return defaultVal;
	}

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
