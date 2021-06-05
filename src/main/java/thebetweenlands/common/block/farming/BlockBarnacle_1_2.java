package thebetweenlands.common.block.farming;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.farming.BlockBarnacle_3_4.EnumBarnacleTypeLate;
import thebetweenlands.common.block.terrain.BlockHearthgroveLog;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.item.ItemBlockBarnacle;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockBarnacle_1_2 extends BlockSwampWater implements IStateMappedBlock, ICustomItemBlock {
	public static final PropertyEnum<EnumBarnacleTypeEarly> BARNACLE_TYPE_EARLY = PropertyEnum.<EnumBarnacleTypeEarly>create("barnacle_type_early", EnumBarnacleTypeEarly.class);

	public BlockBarnacle_1_2() {
		this(FluidRegistry.SWAMP_WATER, Material.WATER);
		setHardness(0.2F);
	}
	
	public BlockBarnacle_1_2(Fluid fluid, Material materialIn) {
		super(fluid, materialIn);
		setTickRandomly(true);
		setHardness(0.2F);
		setUnderwaterBlock(true);
		setDefaultState(blockState.getBaseState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.BARNACLE_DOWN_ONE).withProperty(LEVEL, 0));
		setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(LEVEL);
	}

	@Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumBarnacleTypeEarly.BARNACLE_DOWN_ONE.ordinal()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return canPlaceBlock(world, pos, side);
    }

	@Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.values())
            if (canPlaceBlock(world, pos, enumfacing))
                return true;
        return false;
    }

    protected static boolean canPlaceBlock(World world, BlockPos pos, EnumFacing direction) {
        BlockPos blockpos = pos.offset(direction.getOpposite());
        IBlockState iblockstate = world.getBlockState(blockpos);
        boolean flag = iblockstate.getBlockFaceShape(world, blockpos, direction) == BlockFaceShape.SOLID;
        Block block = iblockstate.getBlock();

        return world.isBlockNormalCube(blockpos, true) && block.isOpaqueCube(iblockstate) && flag;
    }

	@SuppressWarnings("incomplete-switch")
	@Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {

		EnumBarnacleTypeEarly newFacing = EnumBarnacleTypeEarly.BARNACLE_DOWN_ONE;

			switch (facing) {
			case DOWN:
				newFacing = EnumBarnacleTypeEarly.BARNACLE_UP_ONE;
				break;
			case UP:
				newFacing = EnumBarnacleTypeEarly.BARNACLE_DOWN_ONE;
				break;
			case SOUTH:
				newFacing = EnumBarnacleTypeEarly.BARNACLE_NORTH_ONE;
				break;
			case EAST:
				newFacing = EnumBarnacleTypeEarly.BARNACLE_WEST_ONE;
				break;
			case NORTH:
				newFacing = EnumBarnacleTypeEarly.BARNACLE_SOUTH_ONE;
				break;
			case WEST:
				newFacing = EnumBarnacleTypeEarly.BARNACLE_EAST_ONE;
				break;
		}

		return getDefaultState().withProperty(BARNACLE_TYPE_EARLY, newFacing);
    }

	public EnumFacing getFacingForAttachedSide(EnumBarnacleTypeEarly type) {
		switch (type) {
		case BARNACLE_UP_ONE:
		case BARNACLE_UP_TWO:
			return EnumFacing.UP;
		case BARNACLE_DOWN_ONE:
		case BARNACLE_DOWN_TWO:
			return EnumFacing.DOWN;
		case BARNACLE_NORTH_ONE:
		case BARNACLE_NORTH_TWO:
			return EnumFacing.NORTH;
		case BARNACLE_WEST_ONE:
		case BARNACLE_WEST_TWO:
			return EnumFacing.WEST;
		case BARNACLE_SOUTH_ONE:
		case BARNACLE_SOUTH_TWO:
			return EnumFacing.SOUTH;
		case BARNACLE_EAST_ONE:
		case BARNACLE_EAST_TWO:
			return EnumFacing.EAST;
		}
		return EnumFacing.DOWN;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		boolean flag = false;

		if (world.isSideSolid(pos.offset(getFacingForAttachedSide(state.getValue(BARNACLE_TYPE_EARLY))), getFacingForAttachedSide(state.getValue(BARNACLE_TYPE_EARLY)).getOpposite()))
			flag = true;

		if (!flag) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
		super.neighborChanged(state, world, pos, block, fromPos);
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(LEVEL).add(new IProperty[] { BARNACLE_TYPE_EARLY }).add(FLUID_RENDER_PROPS.toArray(new IUnlistedProperty<?>[0])).build();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.byMetadata(meta));
	}

	@Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
    	if (!world.isRemote)
    		if(checkForLog (world, pos, state))
    			world.scheduleUpdate(pos, this, 400);
    }

	private boolean checkForLog(World world, BlockPos pos, IBlockState state) {
		IBlockState offsetState = world.getBlockState(pos.offset(getFacingForAttachedSide(state.getValue(BARNACLE_TYPE_EARLY))));
		Block offsetBlock = offsetState.getBlock();
		if (offsetBlock instanceof BlockHearthgroveLog)
			if (offsetState.getValue(BlockHearthgroveLog.TARRED))
				return true;
		return false;
	}

	@Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (world.isRemote)
			return;
			if (checkForLog(world, pos, state)) {
				randomTick(world, pos, state, random);
				world.scheduleUpdate(pos, this, 400);
		}
	}

	@Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (world.isRemote)
			return;
		IBlockState LATE_BARNACLE_BLOCK = BlockRegistry.BARNACLE_3_4.getDefaultState();
		EnumBarnacleTypeEarly stage = state.getValue(BARNACLE_TYPE_EARLY);

		switch (stage) {
		case BARNACLE_DOWN_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.BARNACLE_DOWN_TWO), 2);
			break;
		case BARNACLE_DOWN_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_DOWN_THREE), 2);
			break;
		case BARNACLE_UP_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.BARNACLE_UP_TWO), 2);
			break;
		case BARNACLE_UP_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_UP_THREE), 2);
			break;
		case BARNACLE_EAST_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.BARNACLE_EAST_TWO), 2);
			break;
		case BARNACLE_EAST_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_EAST_THREE));
			break;
		case BARNACLE_NORTH_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.BARNACLE_NORTH_TWO), 2);
			break;
		case BARNACLE_NORTH_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_NORTH_THREE));
			break;
		case BARNACLE_SOUTH_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.BARNACLE_SOUTH_TWO), 2);
			break;
		case BARNACLE_SOUTH_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_SOUTH_THREE));
			break;
		case BARNACLE_WEST_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.BARNACLE_WEST_TWO), 2);
			break;
		case BARNACLE_WEST_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_WEST_THREE));
			break;
		default:
			break;
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumBarnacleTypeEarly)state.getValue(BARNACLE_TYPE_EARLY)).getMetadata();
	}

	public static enum EnumBarnacleTypeEarly implements IStringSerializable {

		BARNACLE_DOWN_ONE,
		BARNACLE_UP_ONE,
		BARNACLE_NORTH_ONE,
		BARNACLE_SOUTH_ONE,
		BARNACLE_WEST_ONE,
		BARNACLE_EAST_ONE,
		BARNACLE_DOWN_TWO,
		BARNACLE_UP_TWO,
		BARNACLE_NORTH_TWO,
		BARNACLE_SOUTH_TWO,
		BARNACLE_WEST_TWO,
		BARNACLE_EAST_TWO;

		private final String name;

		private EnumBarnacleTypeEarly() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumBarnacleTypeEarly byMetadata(int metadata) {
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}

		@Override
		public String getName() {
			return this.name;
		}
	}

	@Override
	public ItemBlock getItemBlock() {
		return new ItemBlockBarnacle(this);
	}
}
