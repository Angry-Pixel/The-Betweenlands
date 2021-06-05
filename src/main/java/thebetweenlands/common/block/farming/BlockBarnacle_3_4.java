package thebetweenlands.common.block.farming;

import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
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
		setDefaultState(blockState.getBaseState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_DOWN_THREE));
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

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(BlockRegistry.BARNACLE_1_2);
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
    
    @Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    	List<ItemStack> ret = new ArrayList<ItemStack>();
        int age = getMetaFromState(state);
        Random rand = world instanceof World ? ((World) world).rand : RANDOM;

        if (age >= 6 && age <= 11) {
			if (rand.nextBoolean())
				ret.add(getLarvae());
			ret.add(getLarvae());
            ret.add(new ItemStack(ItemRegistry.BARNACLE, 1, 0));
        }
		else
			ret.add(getLarvae());
        return ret;
    }

    protected ItemStack getLarvae() {
        return new ItemStack(BlockRegistry.BARNACLE_1_2);
    }

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
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
		case BARNACLE_DOWN_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_DOWN_FOUR), 2);
			break;
		case BARNACLE_UP_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_UP_FOUR), 2);
			break;
		case BARNACLE_EAST_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_EAST_FOUR), 2);
			break;
		case BARNACLE_NORTH_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_NORTH_FOUR), 2);
			break;
		case BARNACLE_SOUTH_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_SOUTH_FOUR), 2);
			break;
		case BARNACLE_WEST_THREE:
			world.setBlockState(pos, getDefaultState().withProperty(BARNACLE_TYPE_LATE, EnumBarnacleTypeLate.BARNACLE_WEST_FOUR), 2);
			break;
		case BARNACLE_DOWN_FOUR:
		case BARNACLE_UP_FOUR:
		case BARNACLE_EAST_FOUR:
		case BARNACLE_NORTH_FOUR:
		case BARNACLE_SOUTH_FOUR:
		case BARNACLE_WEST_FOUR:
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
		
		BARNACLE_DOWN_THREE,
		BARNACLE_UP_THREE,
		BARNACLE_NORTH_THREE,
		BARNACLE_SOUTH_THREE,
		BARNACLE_WEST_THREE,
		BARNACLE_EAST_THREE,
		BARNACLE_DOWN_FOUR,
		BARNACLE_UP_FOUR,
		BARNACLE_NORTH_FOUR,
		BARNACLE_SOUTH_FOUR,
		BARNACLE_WEST_FOUR,
		BARNACLE_EAST_FOUR;

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
		return null;
	}
}
