package thebetweenlands.common.block.farming;

import java.util.List;
import java.util.Random;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockMeta;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeBlock;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.util.AdvancedStateMap;
import thebetweenlands.util.BlockStatePropertiesMatcher;

public abstract class BlockGenericDugSoil extends BasicBlock implements ITileEntityProvider, ISubtypeBlock, IStateMappedBlock, ICustomItemBlock {
	//-1, +1, -1, quadrant 0
	public static final PropertyInteger TOP_NORTH_WEST_INDEX = PropertyInteger.create("top_north_west_index", 0, 4);
	//+1, +1, -1, quadrant 1
	public static final PropertyInteger TOP_NORTH_EAST_INDEX = PropertyInteger.create("top_north_east_index", 0, 4);
	//-1, +1, +1, quadrant 2
	public static final PropertyInteger TOP_SOUTH_WEST_INDEX = PropertyInteger.create("top_south_west_index", 0, 4);
	//+1, +1, +1, quadrant 3
	public static final PropertyInteger TOP_SOUTH_EAST_INDEX = PropertyInteger.create("top_south_east_index", 0, 4);

	public static final PropertyBool COMPOSTED = PropertyBool.create("composted");
	public static final PropertyBool DECAYED = PropertyBool.create("decayed");

	public final boolean purified;

	public BlockGenericDugSoil(Material material) {
		this(material, false);
	}

	public BlockGenericDugSoil(Material material, boolean purified) {
		super(material);
		this.setTickRandomly(true);
		this.setSoundType(SoundType.GROUND);
		this.setHardness(0.5F);
		this.setHarvestLevel("shovel", 0);
		this.setDefaultState(this.getBlockState().getBaseState().withProperty(COMPOSTED, false).withProperty(DECAYED, false));
		this.purified = purified;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{TOP_NORTH_WEST_INDEX, TOP_NORTH_EAST_INDEX, TOP_SOUTH_WEST_INDEX, TOP_SOUTH_EAST_INDEX, COMPOSTED, DECAYED});
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return !this.purified && state.getValue(DECAYED) ? 2 : state.getValue(COMPOSTED) ? 1 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		switch(meta) {
		default:
		case 0:
			return this.getDefaultState();
		case 1:
			return this.getDefaultState().withProperty(COMPOSTED, true);
		case 2:
			if(this.purified)
				return this.getDefaultState();
			return this.getDefaultState().withProperty(DECAYED, true);
		}
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		boolean[] connectionArray = getConnectionArray(worldIn, pos, EnumFacing.UP, BlockStatePropertiesMatcher.forBlockState(state, COMPOSTED, DECAYED));
		int[] quadrantIndices = getQuadrantIndices(connectionArray);
		state = state.withProperty(TOP_NORTH_WEST_INDEX, quadrantIndices[0]);
		state = state.withProperty(TOP_NORTH_EAST_INDEX, quadrantIndices[1]);
		state = state.withProperty(TOP_SOUTH_WEST_INDEX, quadrantIndices[2]);
		state = state.withProperty(TOP_SOUTH_EAST_INDEX, quadrantIndices[3]);
		return state;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	/**
	 * Calculates an index for the given coordinates and the matrix width
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param width Matrix width
	 * @return Index
	 */
	private static int getIndex(int x, int y, int width) {
		return x % width + y * width;
	}

	/**
	 * Returns the quadrant indices<p>
	 * <p>
	 * @param connectionArray
	 * <p>Connection states, index 4 is the center:
	 * <pre>
	 *  -------
	 * | 0 1 2 |
	 * | 3 4 5 |
	 * | 6 7 8 |
	 *  ------- </pre>
	 * @return
	 * <p>Returned index positions:
	 * <pre>
	 *  -------> +x
	 * |  -----
	 * | | 0 1 |
	 * | | 2 3 |
	 * |  -----
	 * \/
	 * +z
	 * </pre>
	 * <p>Texture segment indices (arrangement depends on the texture size):
	 * <pre>
	 *  -------
	 * | 0 1 2 |
	 * | 3 4 . |
	 * | . . . |
	 *  ------- </pre>
	 * <ol start = "0">
	 * <li>No connections</li>
	 * <li>Straight connection to the left and right</li>
	 * <li>Straight connection to the top and bottom</li>
	 * <li>Sharp corner</li>
	 * <li>Smooth corner</li>
	 * </ol>
	 */
	private static int[] getQuadrantIndices(boolean[] connectionArray) {
		int tls = 0;
		int trs = 0;
		int bls = 0;
		int brs = 0;
		for(int xo = 0; xo <= 2; xo++) {
			for(int zo = 0; zo <= 2; zo++) {
				boolean currentNeighbourState = connectionArray[getIndex(xo, zo, 3)];
				if((xo != 1 && zo == 1) || (xo == 1 && zo != 1)) {
					//Adjacent neighbour
					if(currentNeighbourState) {
						if(xo == 0) {
							if(!connectionArray[getIndex(1, 2, 3)]) bls = 1;
							if(!connectionArray[getIndex(1, 0, 3)]) tls = 1;
						} else if (xo == 2){
							if(!connectionArray[getIndex(1, 2, 3)]) brs = 1;
							if(!connectionArray[getIndex(1, 0, 3)]) trs = 1;
						} else if(zo == 0) {
							if(!connectionArray[getIndex(0, 1, 3)]) tls = 2;
							if(!connectionArray[getIndex(2, 1, 3)]) trs = 2;
						} else if (zo == 2){
							if(!connectionArray[getIndex(0, 1, 3)]) bls = 2;
							if(!connectionArray[getIndex(2, 1, 3)]) brs = 2;
						}
					}
				} else if(xo != 1 && zo != 1) {
					//Diagonal neighbour
					if(connectionArray[getIndex(xo, 1, 3)] && connectionArray[getIndex(1, zo, 3)]) {
						int segment;
						if(currentNeighbourState) {
							//Full sharp corner
							segment = 3;
						} else {
							//Smooth half corner
							segment = 4;
						}
						if(xo == 2 && zo == 0) {
							trs = segment;
						} else if(xo == 2 && zo == 2) {
							brs = segment;
						} else if(xo == 0 && zo == 2) {
							bls = segment;
						} else {
							tls = segment;
						}
					}
				}
			}
		}
		return new int[]{tls, trs, bls, brs};
	}

	/**
	 * Creates the connection array
	 * @param blockAccess Block access
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param z Z Coordinate
	 * @param dir Face
	 * @param ignoreMeta Ignore metadata
	 * @return Connection array
	 */
	public static boolean[] getConnectionArray(IBlockAccess blockAccess, BlockPos pos, EnumFacing dir, Predicate<IBlockState> matcher) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		MutableBlockPos checkPos = new MutableBlockPos();
		boolean xp = true;
		boolean yp = true;
		boolean xr = false;
		boolean yr = false;
		boolean zr = false;
		boolean[] connectionArray = new boolean[9];
		switch(dir) {
		case DOWN:
			xp = false;
		case UP:
			xr = true;
			zr = true;
			break;
		case NORTH:
			yp = false;
		case SOUTH:
			xr = true;
			yr = true;
			break;
		case EAST:
			xp = false;
		case WEST:
			zr = true;
			yr = true;
			break;
		default:
			return connectionArray;
		}
		for(int xo = xr ? -1 : 0; xo <= (xr ? 1 : 0); xo++) {
			for(int yo = yr ? -1 : 0; yo <= (yr ? 1 : 0); yo++) {
				for(int zo = zr ? -1 : 0; zo <= (zr ? 1 : 0); zo++) {
					int mx = (xr ? xo : yo) + 1;
					int my = (zr ? zo : (xr ? yo : zo)) + 1;
					int blockIndex = getIndex(xp ? mx : 2 - mx, yp ? my : 2 - my, 3);
					connectionArray[blockIndex] = matcher.apply(blockAccess.getBlockState(checkPos.setPos(x+xo, y+yo, z+zo)));
				}
			}
		}
		return connectionArray;
	}

	@Override
	public int getSubtypeNumber() {
		return this.purified ? 2 : 3;
	}

	@Override
	public String getSubtypeName(int meta) {
		switch(meta) {
		default:
		case 0:
			return "%s";
		case 1:
			return "%s_composted";
		case 2:
			return "%s_decayed";
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
		if(!this.purified)
			list.add(new ItemStack(this, 1, 2));
	}

	@Override
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(COMPOSTED).ignore(DECAYED).withPropertySuffix(COMPOSTED, null, "composted")
		.withPropertySuffixExclusions((map) -> {
			//Exclude COMPOSTED && DECAYED because that will never be used
			if(map.getValue(COMPOSTED) && map.getValue(DECAYED))
				return ImmutableList.of(COMPOSTED, DECAYED);
			return ImmutableList.of();
		});
		if(!this.purified) {
			builder.withPropertySuffixTrue(DECAYED, "decayed");
		}
	}

	@Override
	public ItemBlock getItemBlock() {
		return new ItemBlockMeta(this);
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, this.getMetaFromState(state));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityDugSoil) {
			TileEntityDugSoil te = (TileEntityDugSoil) tile;
			if(state.getValue(COMPOSTED)) {
				te.setCompost(3);
			}
			if(state.getValue(DECAYED)) {
				te.setDecay(20);
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityDugSoil();
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if(!world.isRemote) {
			TileEntityDugSoil te = getTile(world, pos);

			if(te != null) {
				if(!this.purified && !te.isFullyDecayed() && rand.nextInt(10) == 0) {
					te.setDecay(te.getDecay() + 1);
				}

				if(te.isFullyDecayed() && rand.nextInt(20) == 0) {
					for(int xo = -1; xo <= 1; xo++) {
						for(int zo = -1; zo <= 1; zo++) {
							if((xo == 0 && zo == 0) || (zo != 0 && xo != 0) || rand.nextInt(3) != 0) {
								continue;
							}
							BlockPos offset = pos.add(xo, 0, zo);
							IBlockState offsetState = world.getBlockState(offset);
							if(offsetState.getBlock() instanceof BlockGenericDugSoil) {
								BlockGenericDugSoil dugDirt = (BlockGenericDugSoil) offsetState.getBlock();
								if(!dugDirt.purified) {
									TileEntityDugSoil offsetTe = getTile(world, offset);
									if(offsetTe != null && !offsetTe.isFullyDecayed()) {
										offsetTe.setDecay(offsetTe.getDecay() + 1);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static TileEntityDugSoil getTile(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityDugSoil) {
			return (TileEntityDugSoil) te;
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(stateIn.getValue(DECAYED)) {
			BLParticles.DIRT_DECAY.spawn(worldIn, pos.getX() + rand.nextFloat(), pos.getY() + 1.0F, pos.getZ() + 0.5F);
		}
	}
}
