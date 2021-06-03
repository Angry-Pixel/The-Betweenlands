package thebetweenlands.common.block.farming;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
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
import thebetweenlands.common.block.terrain.BlockSwampWater;
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
		setDefaultState(blockState.getBaseState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.NORTH_ONE).withProperty(LEVEL, 0));
		setCreativeTab(BLCreativeTabs.BLOCKS);
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
		list.add(new ItemStack(this, 1, EnumBarnacleTypeEarly.NORTH_ONE.ordinal()));
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

		EnumBarnacleTypeEarly newFacing = EnumBarnacleTypeEarly.NORTH_ONE;
		if (facing == EnumFacing.UP)
			newFacing = EnumBarnacleTypeEarly.DOWN_ONE;

		else if (facing == EnumFacing.DOWN)
				newFacing = EnumBarnacleTypeEarly.UP_ONE;
		else {
			switch (facing) {
			case SOUTH:
				newFacing = EnumBarnacleTypeEarly.NORTH_ONE;
				break;
			case EAST:
				newFacing = EnumBarnacleTypeEarly.WEST_ONE;
				break;
			case NORTH:
				newFacing = EnumBarnacleTypeEarly.SOUTH_ONE;
				break;
			case WEST:
				newFacing = EnumBarnacleTypeEarly.EAST_ONE;
				break;
			}
		}

		return getDefaultState().withProperty(BARNACLE_TYPE_EARLY, newFacing);
    }

	@Override
	public int damageDropped(IBlockState state) {
		return EnumBarnacleTypeEarly.NORTH_ONE.ordinal();
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
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (world.isRemote)
			return;
		IBlockState LATE_BARNACLE_BLOCK = BlockRegistry.BARNACLE_3_4.getDefaultState();
		EnumBarnacleTypeEarly stage = state.getValue(BARNACLE_TYPE_EARLY);

		switch (stage) {
		case DOWN_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.DOWN_TWO), 2);
			break;
		case DOWN_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.DOWN_THREE), 2);
			break;
		case UP_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.UP_TWO), 2);
			break;
		case UP_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.UP_THREE), 2);
			break;
		case EAST_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.EAST_TWO), 2);
			break;
		case EAST_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.EAST_THREE));
			break;
		case NORTH_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.NORTH_TWO), 2);
			break;
		case NORTH_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.NORTH_THREE));
			break;
		case SOUTH_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.SOUTH_TWO), 2);
			break;
		case SOUTH_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.SOUTH_THREE));
			break;
		case WEST_ONE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_EARLY, EnumBarnacleTypeEarly.WEST_TWO), 2);
			break;
		case WEST_TWO:
			world.setBlockState(pos, LATE_BARNACLE_BLOCK.withProperty(BlockBarnacle_3_4.BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.WEST_THREE));
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
		
		DOWN_ONE,
		UP_ONE,
		NORTH_ONE,
		SOUTH_ONE,
		WEST_ONE,
		EAST_ONE,
		DOWN_TWO,
		UP_TWO,
		NORTH_TWO,
		SOUTH_TWO,
		WEST_TWO,
		EAST_TWO;

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
		return new ItemBlock(this);
	}
}
