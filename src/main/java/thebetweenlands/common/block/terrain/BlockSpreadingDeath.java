package thebetweenlands.common.block.terrain;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public abstract class BlockSpreadingDeath extends Block {
	public static final PropertyBool INACTIVE = PropertyBool.create("inactive");

	public BlockSpreadingDeath(Material material) {
		super(material);
		this.setDefaultState(this.blockState.getBaseState().withProperty(INACTIVE, false));
		this.setTickRandomly(true);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { INACTIVE });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(INACTIVE) ? 1 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(INACTIVE, (meta & 1) == 1);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);

		if(!worldIn.isRemote) {
			this.checkAndRevertBiome(worldIn, pos);
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		int spreadTime = this.getScheduledSpreadTime(worldIn, pos, state);
		if(spreadTime > 0) {
			worldIn.scheduleUpdate(pos, this, spreadTime);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if(!world.isRemote) {
			if(!state.getValue(INACTIVE) && this.shouldSpread(world, pos, state)) {
				boolean spread = false;
				for(int i = 0; i < 16; ++i) {
					BlockPos target = pos.add(rand.nextInt(3) - 1, rand.nextInt(3) - 1, rand.nextInt(3) - 1);

					if(world.isBlockLoaded(target)) {
						IBlockState offsetState = world.getBlockState(target);

						if(offsetState.getBlock() != this && this.canSpreadInto(world, pos, state, target, offsetState)) {
							this.spreadInto(world, pos, state, target, offsetState);
							if(this.getSpreadingBiome() != null) {
								this.convertBiome(world, target, this.getSpreadingBiome());
							}
							spread = true;
						}
					}
				}
				if(!spread) {
					for(int i = 0; i < 16; ++i) {
						BlockPos target = pos.add(rand.nextInt(5) - 2, rand.nextInt(5) - 2, rand.nextInt(5) - 2);

						if(world.isBlockLoaded(target)) {
							IBlockState offsetState = world.getBlockState(target);

							if(offsetState.getBlock() != this && this.canSpreadInto(world, pos, state, target, offsetState)) {
								this.spreadInto(world, pos, state, target, offsetState);
								if(this.getSpreadingBiome() != null) {
									this.convertBiome(world, target, this.getSpreadingBiome());
								}
								spread = true;
							}
						}
					}
				}

				int spreadTime = this.getScheduledSpreadTime(world, pos, state);
				if(spreadTime > 0) {
					world.scheduleUpdate(pos, this, spreadTime);
				}
			}

			if(world.rand.nextInt(6) == 0) {
				world.setBlockState(pos, state.withProperty(INACTIVE, true));
			}

			if(this.getSpreadingBiome() != null && rand.nextInt(3) == 0 && world.getBiomeForCoordsBody(pos) != this.getSpreadingBiome()) {
				this.convertBiome(world, pos, this.getSpreadingBiome());
			}
		}
	}

	protected boolean shouldSpread(World world, BlockPos pos, IBlockState state) {
		return true;
	}
	
	public boolean canSpreadInto(World world, BlockPos pos, IBlockState state, BlockPos offsetPos, IBlockState offsetState) {
		IBlockState offsetStateUp = world.getBlockState(offsetPos.up());
		return offsetStateUp.getBlock() != this && !offsetStateUp.isNormalCube() && (this.getPreviousBiome() == null || world.getBiomeForCoordsBody(offsetPos) == this.getPreviousBiome());
	}

	public abstract void spreadInto(World world, BlockPos pos, IBlockState state, BlockPos offsetPos, IBlockState offsetState);

	protected int getScheduledSpreadTime(World world, BlockPos pos, IBlockState state) {
		return -1;
	}

	@Nullable
	public Biome getSpreadingBiome() {
		return null;
	}

	@Nullable
	public Biome getPreviousBiome() {
		return null;
	}

	protected void checkAndRevertBiome(World world, BlockPos pos) {
		if(this.getPreviousBiome() != null && this.getSpreadingBiome() != null && world.getBiomeForCoordsBody(pos) == this.getSpreadingBiome()) {
			this.convertBiome(world, pos, this.getPreviousBiome());
		}
	}

	protected void convertBiome(World world, BlockPos pos, Biome biome) {
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		byte[] biomes = chunk.getBiomeArray().clone();
		int index = (pos.getZ() & 15) << 4 | (pos.getX() & 15);
		biomes[index] = (byte) (Biome.getIdForBiome(biome) & 255);
		chunk.setBiomeArray(biomes);
		chunk.markDirty();
	}
}
