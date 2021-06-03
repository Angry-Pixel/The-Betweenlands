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
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockBarnacle_3_4 extends BlockSwampWater implements IStateMappedBlock, ICustomItemBlock {
	public static final PropertyEnum<EnumBarnacleTypeLate> BARNACLE_TYPE_LATE = PropertyEnum.<EnumBarnacleTypeLate>create("barnacle_type_late", EnumBarnacleTypeLate.class);

	public BlockBarnacle_3_4() {
		this(FluidRegistry.SWAMP_WATER, Material.WATER);
		setHardness(0.2F);
	}
	
	public BlockBarnacle_3_4(Fluid fluid, Material materialIn) {
		super(fluid, materialIn);
		setTickRandomly(true);
		setHardness(0.2F);
		setUnderwaterBlock(true);
		setDefaultState(blockState.getBaseState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.NORTH_THREE));
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
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumBarnacleTypeLate.NORTH_THREE.ordinal()));
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

		EnumBarnacleTypeLate newFacing = EnumBarnacleTypeLate.NORTH_THREE;
		if (facing == EnumFacing.UP)
			newFacing = EnumBarnacleTypeLate.DOWN_THREE;

		else if (facing == EnumFacing.DOWN)
				newFacing = EnumBarnacleTypeLate.DOWN_THREE;
		else {
			switch (facing) {
			case SOUTH:
				newFacing = EnumBarnacleTypeLate.NORTH_THREE;
				break;
			case EAST:
				newFacing = EnumBarnacleTypeLate.WEST_THREE;
				break;
			case NORTH:
				newFacing = EnumBarnacleTypeLate.SOUTH_THREE;
				break;
			case WEST:
				newFacing = EnumBarnacleTypeLate.EAST_THREE;
				break;
			}
		}

		return getDefaultState().withProperty(BARNACLE_TYPE_LATE, newFacing);
    }

	@Override
	public int damageDropped(IBlockState state) {
		return EnumBarnacleTypeLate.NORTH_THREE.ordinal();
	}

	@Override
	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer.Builder(this).add(LEVEL).add(new IProperty[] { BARNACLE_TYPE_LATE }).add(FLUID_RENDER_PROPS.toArray(new IUnlistedProperty<?>[0])).build();
	}



	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.byMetadata(meta));
	}

	@Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (world.isRemote)
			return;

		EnumBarnacleTypeLate stage = state.getValue(BARNACLE_TYPE_LATE);

		switch (stage) {
		case DOWN_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.DOWN_FOUR), 2);
			break;
		case UP_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.UP_FOUR), 2);
			break;
		case EAST_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.EAST_FOUR), 2);
			break;
		case NORTH_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.NORTH_FOUR), 2);
			break;
		case SOUTH_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.SOUTH_FOUR), 2);
			break;
		case WEST_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.WEST_FOUR), 2);
			break;
		case DOWN_FOUR:
		case UP_FOUR:
		case EAST_FOUR:
		case NORTH_FOUR:
		case SOUTH_FOUR:
		case WEST_FOUR:
			break;
		default:
			break;
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumBarnacleTypeLate)state.getValue(BARNACLE_TYPE_LATE)).getMetadata();
	}
	
	public static enum EnumBarnacleTypeLate implements IStringSerializable {
		
		DOWN_THREE,
		UP_THREE,
		NORTH_THREE,
		SOUTH_THREE,
		WEST_THREE,
		EAST_THREE,
		DOWN_FOUR,
		UP_FOUR,
		NORTH_FOUR,
		SOUTH_FOUR,
		WEST_FOUR,
		EAST_FOUR;

		private final String name;

		private EnumBarnacleTypeLate() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumBarnacleTypeLate byMetadata(int metadata) {
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
