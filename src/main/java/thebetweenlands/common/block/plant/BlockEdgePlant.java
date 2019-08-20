package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockEdgePlant extends BlockBush implements IShearable {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    protected static final AxisAlignedBB PLANT_AABB_NORTH = new AxisAlignedBB(0D, 0D, 0.5D, 1D, 0.25D, 1D);
    protected static final AxisAlignedBB PLANT_AABB_SOUTH = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.25D, 0.5D);
    protected static final AxisAlignedBB PLANT_AABB_EAST = new AxisAlignedBB(0.0D, 0D, 0D, 0.5D, 0.25D, 1D);
    protected static final AxisAlignedBB PLANT_AABB_WEST = new AxisAlignedBB(0.5D, 0D, 0D, 1D, 0.25D, 1D);
    public BlockEdgePlant() {
        super(Material.PLANTS);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setSoundType(SoundType.PLANT);
        setHardness(0.1F);
        setCreativeTab(BLCreativeTabs.PLANTS);
    }

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		//No solid collision
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(FACING)) {
		case DOWN:
		case UP:
		case NORTH:
			return PLANT_AABB_NORTH;
		case SOUTH:
			return PLANT_AABB_SOUTH;
		case EAST:
			return PLANT_AABB_EAST;
		case WEST:
			return PLANT_AABB_WEST;
		default:
			return PLANT_AABB_NORTH;
		}
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return SoilHelper.canSustainPlant(state);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return super.canPlaceBlockAt(world, pos) && world.isAirBlock(pos.up()) && this.canBlockStay(world, pos, world.getBlockState(pos));
	}

	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
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
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

}