package thebetweenlands.common.block.terrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.MessageSyncDeathSpread;
import thebetweenlands.common.network.clientbound.MessageSyncDeathSpread.SpreadEntry;

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
				List<SpreadEntry> spreadEntries = new ArrayList<SpreadEntry>();
				
				boolean spread = false;
				for(int i = 0; i < 16; ++i) {
					BlockPos target = pos.add(rand.nextInt(3) - 1, rand.nextInt(3) - 1, rand.nextInt(3) - 1);

					if(world.isBlockLoaded(target)) {
						IBlockState offsetState = world.getBlockState(target);

						if(offsetState.getBlock() != this && this.canSpreadInto(world, pos, state, target, offsetState)) {
							this.spreadInto(world, pos, state, target, offsetState);
							if(this.getSpreadingBiome() != null) {
								spreadEntries.add(new SpreadEntry(target.getX(), target.getZ(), Biome.getIdForBiome(this.getSpreadingBiome())));
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
									spreadEntries.add(new SpreadEntry(target.getX(), target.getZ(), Biome.getIdForBiome(this.getSpreadingBiome())));
									this.convertBiome(world, target, this.getSpreadingBiome());
								}
								spread = true;
							}
						}
					}
				}

				if(spread) //send biome update packet
					sendUpdatePacket(world, pos, spreadEntries);
				
				int spreadTime = this.getScheduledSpreadTime(world, pos, state);
				if(spreadTime > 0) {
					world.scheduleUpdate(pos, this, spreadTime);
				}
			}

			if(world.rand.nextInt(6) == 0) {
				world.setBlockState(pos, state.withProperty(INACTIVE, true));
			}

			if(this.getSpreadingBiome() != null && rand.nextInt(3) == 0 && world.getBiomeForCoordsBody(pos) != this.getSpreadingBiome()) {
				sendUpdatePacket(world, pos, Arrays.asList(new SpreadEntry(pos.getX(), pos.getZ(), Biome.getIdForBiome(this.getSpreadingBiome())))); //replicate to clients
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

	//send biome update packet
	protected void sendUpdatePacket(World world, BlockPos pos, List<SpreadEntry> entries) {
		if(world.isRemote) { return; }
		
		MessageSyncDeathSpread message = new MessageSyncDeathSpread(entries);
		TargetPoint point = new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), -1);
		TheBetweenlands.networkWrapper.sendToAllTracking(
			message,
			point
		);
	}
	
	protected void checkAndRevertBiome(World world, BlockPos pos) {
		if(this.getPreviousBiome() != null && this.getSpreadingBiome() != null && world.getBiomeForCoordsBody(pos) == this.getSpreadingBiome()) {
			sendUpdatePacket(world, pos, Arrays.asList(new SpreadEntry(pos.getX(), pos.getZ(), Biome.getIdForBiome(this.getPreviousBiome())))); //replicate to clients
			this.convertBiome(world, pos, this.getPreviousBiome());
		}
	}

	protected void convertBiome(World world, BlockPos pos, Biome biome) {
		Chunk chunk = world.getChunk(pos);
		int index = (pos.getZ() & 15) << 4 | (pos.getX() & 15);
		byte[] biomes = chunk.getBiomeArray();
		biomes[index] = (byte) (Biome.getIdForBiome(biome) & 255);
		chunk.markDirty();
	}
}
