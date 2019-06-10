package thebetweenlands.util;

import java.util.Optional;

import javax.annotation.Nullable;

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

public class StatePropertyHelper {
	/**
	 * Returns an {@link Optional} wrapping the value that is mapped to the specified property. If the specified block state is null then an empty Optional is returned.
	 * @param state
	 * @param property
	 * @return
	 */
	public static <P extends IProperty<T>, T extends Comparable<T>> Optional<T> getPropertyOptional(@Nullable IBlockState state, P property) {
		if(state != null) {
			return Optional.of(state.getValue(property));
		}
		return Optional.empty();
	}
	
	/**
	 * Returns an {@link Optional} wrapping the value that is mapped to the specified property. Unlike {@link IExtendedBlockState#getValue(IUnlistedProperty)} this
	 * will not thrown an exception when the property or value is not present but instead return an empty Optional. If the specified block state is not an {@link IExtendedBlockState} then
	 * an empty Optional is returned.
	 * @param state
	 * @param property
	 * @return
	 */
	public static <P extends IUnlistedProperty<T>, T> Optional<T> getPropertyOptional(@Nullable IBlockState state, P property) {
		if(state instanceof IExtendedBlockState) {
			return ((IExtendedBlockState) state).getUnlistedProperties().get(property).map(o -> property.getType().cast(o));
		}
		return Optional.empty();
	}
	
	/**
	 * Safely gets a block state property of the block at the TileEntity
	 * @param te
	 * @param property
	 * @param defaultVal
	 * @return
	 */
	public static <P extends IUnlistedProperty<T>, T> T getStatePropertySafely(@Nullable TileEntity te, Class<? extends Block> block, P property, T defaultVal) {
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
	public static <P extends IUnlistedProperty<T>, T> T getStatePropertySafely(@Nullable TileEntity te, Class<? extends Block> block, P property, T defaultVal, boolean actual, boolean extended) {
		if(te == null || te.getWorld() == null) {
			return defaultVal;
		}
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if(block.isInstance(state.getBlock())) {
			if(actual) {
				state = state.getActualState(te.getWorld(), te.getPos());
			}
			if(extended) {
				state = state.getBlock().getExtendedState(state, te.getWorld(), te.getPos());
			}
			if(state instanceof IExtendedBlockState) {
				return ((IExtendedBlockState)state).getUnlistedProperties().get(property).map(o -> property.getType().cast(o)).orElse(defaultVal);
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
	public static <P extends IProperty<T>, T extends Comparable<T>> T getStatePropertySafely(@Nullable TileEntity te, Class<? extends Block> block, P property, T defaultVal) {
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
	public static <P extends IProperty<T>, T extends Comparable<T>> T getStatePropertySafely(@Nullable TileEntity te, Class<? extends Block> block, P property, T defaultVal, boolean actual, boolean extended) {
		if(te == null || te.getWorld() == null) {
			return defaultVal;
		}
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
			te = ((ChunkCache)world).getTileEntity(pos, EnumCreateEntityType.CHECK);
		} else {
			te = world.getTileEntity(pos);
		}

		if(cls.isInstance(te)) {
			return (T) te;
		}

		return null;
	}
}
