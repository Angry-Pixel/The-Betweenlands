package thebetweenlands.client.render.block;

import javax.annotation.Nullable;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

public class BlockRenderProxyWorld implements IBlockAccess {
	private final TLongObjectMap<IBlockState> blockMap = new TLongObjectHashMap<>();

	@Nullable
	private final IBlockAccess world;

	public BlockRenderProxyWorld(@Nullable IBlockAccess world) {
		this.world = world;
	}

	public void setBlockState(BlockPos pos, IBlockState state) {
		this.blockMap.put(pos.toLong(), state);
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return null;
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return 0;
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		IBlockState state = this.blockMap.get(pos.toLong());
		if(state != null) {
			return state;
		}
		return this.world == null ? Blocks.AIR.getDefaultState() : this.world.getBlockState(pos);
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {
		IBlockState state = this.blockMap.get(pos.toLong());
		if(state != null) {
			return state.getBlock().isAir(state, this, pos);
		}
		return this.world == null || this.world.isAirBlock(pos);
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		return this.world == null ? Biomes.PLAINS : this.world.getBiome(pos);
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return this.world != null ? this.world.getStrongPower(pos, direction) : 0;
	}

	@Override
	public WorldType getWorldType() {
		return this.world != null ? this.world.getWorldType() : WorldType.DEFAULT;
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		IBlockState state = this.blockMap.get(pos.toLong());
		if(state != null) {
			return state.isSideSolid(world, pos, side);
		}
		return this.world == null ? false : this.world.isSideSolid(pos, side, _default);
	}
}
