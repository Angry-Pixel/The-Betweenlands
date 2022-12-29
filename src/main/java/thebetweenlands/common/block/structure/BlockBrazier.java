package thebetweenlands.common.block.structure;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockBrazier extends Block {

	public static final PropertyEnum<EnumBrazierHalf> HALF = PropertyEnum.<EnumBrazierHalf>create("half", EnumBrazierHalf.class);
	//protected static final AxisAlignedBB BRAZIER_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);

	public BlockBrazier() {
		super(Material.WOOD);
		setHardness(1.5F);
		setResistance(10.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().withProperty(HALF, EnumBrazierHalf.LOWER));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
		IBlockState state = world.getBlockState(pos);
        if (state.getValue(HALF) == EnumBrazierHalf.UPPER && side == EnumFacing.UP)
            return true;
        return false;
    }

	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos.up(), getDefaultState().withProperty(HALF, EnumBrazierHalf.UPPER), 2);
    }

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos.down());
		if (state.getBlock() == null || !world.isAirBlock(pos.up()))
			return false;
		return state.getMaterial().blocksMovement();
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		dropBrazierIfCantStay(world, state, pos);
	}

	protected boolean dropBrazierIfCantStay(World world, IBlockState state, BlockPos pos) {
		if (world.isAirBlock(pos.down())) {
			world.setBlockToAir(pos);
			if (state.getValue(HALF) == EnumBrazierHalf.LOWER)
				dropBlockAsItem(world, pos, state, 0);
			return false;
		}
		return true;
	}

	@Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (state.getValue(HALF) == EnumBrazierHalf.UPPER) {
            if (world.getBlockState(pos.down()).getBlock() == this) {
                if (player.capabilities.isCreativeMode)
                    world.setBlockToAir(pos.down());
                else {
                    world.destroyBlock(pos.down(), true);
                    if (world.isRemote)
                        world.setBlockToAir(pos.down());
                }
            }
        }
        super.onBlockHarvested(world, pos, state, player);
    }

	@Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (state.getValue(HALF) == EnumBrazierHalf.UPPER)
            return Items.AIR;
        else {
            return super.getItemDropped(state, rand, fortune);
        }
    }

	@Override
	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
    public IBlockState getStateFromMeta(int meta) {
        return meta > 0 ? getDefaultState().withProperty(HALF, EnumBrazierHalf.UPPER) : getDefaultState().withProperty(HALF, EnumBrazierHalf.LOWER);
    }

	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getValue(HALF) == EnumBrazierHalf.UPPER) {
            IBlockState iblockstate = world.getBlockState(pos.down());
            if (iblockstate.getBlock() == this)
            	return state;
        }
        return state;
    }

	@Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HALF) == EnumBrazierHalf.UPPER ? 1 : 0;
    }

	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {HALF});
    }
	
	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.UP && state.getValue(HALF) == EnumBrazierHalf.UPPER;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.UP && state.getValue(HALF) == EnumBrazierHalf.UPPER ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
	
    public static enum EnumBrazierHalf implements IStringSerializable {
        UPPER,
        LOWER;

    	@Override
        public String toString() {
            return getName();
        }

    	@Override
        public String getName() {
            return this == UPPER ? "upper" : "lower";
        }
    }
}