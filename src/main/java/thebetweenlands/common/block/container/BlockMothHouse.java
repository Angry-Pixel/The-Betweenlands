package thebetweenlands.common.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityMothHouse;

public class BlockMothHouse  extends BlockContainer {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool ON_WALL = PropertyBool.create("on_wall");

    public static final AxisAlignedBB AABB_NS = new AxisAlignedBB(0.125, 0.0, 0.25, 0.875, 1.0, 0.75);
    public static final AxisAlignedBB AABB_EW = new AxisAlignedBB(0.25, 0.0, 0.125, 0.75, 1.0, 0.875);
    public static final AxisAlignedBB AABB_N_WALL = new AxisAlignedBB(0.125, 0.0, 0.0, 0.875, 1.0, 0.375);
    public static final AxisAlignedBB AABB_S_WALL = new AxisAlignedBB(0.125, 0.0, 0.625, 0.875, 1.0, 1.0);
    public static final AxisAlignedBB AABB_E_WALL = new AxisAlignedBB(0.625, 0.0, 0.125, 1.0, 1.0, 0.875);
    public static final AxisAlignedBB AABB_W_WALL = new AxisAlignedBB(0.0, 0.0, 0.125, 0.375, 1.0, 0.875);
    
    public BlockMothHouse() {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setHardness(1.5F);
        setResistance(5.0F);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setTickRandomly(true);
    }

    @SuppressWarnings("deprecation")
	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    	EnumFacing facing = state.getValue(FACING);
    	boolean onWall = state.getValue(ON_WALL);
    	
    	if(onWall) {
    		switch(facing) {
    		case NORTH:
    			return AABB_N_WALL;
    		case EAST:
    			return AABB_E_WALL;
    		case SOUTH:
    			return AABB_S_WALL;
    		case WEST:
    			return AABB_W_WALL;
    		}
    	} else {
    		switch(facing) {
    		case NORTH:
    		case SOUTH:
    			return AABB_NS;
    		case EAST:
    		case WEST:
    			return AABB_EW;
    		}	
    	}
    	
    	return super.getBoundingBox(state, source, pos);
    }

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}


	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    	if(facing.getAxis() != EnumFacing.Axis.Y) {
    		return this.getDefaultState().withProperty(FACING, facing.getOpposite()).withProperty(ON_WALL, true);
    	} else {
    		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(ON_WALL, false);
    	}
    }
    
    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
    	if(super.canPlaceBlockOnSide(worldIn, pos, side)) {
    		BlockPos offsetPos;
    		EnumFacing offsetFacing;
    		if(side.getAxis() == EnumFacing.Axis.Y) {
    			offsetPos = pos.down();
    			offsetFacing = EnumFacing.UP;
    			BlockFaceShape shape = worldIn.getBlockState(offsetPos).getBlockFaceShape(worldIn, offsetPos, offsetFacing);
    			return (shape != BlockFaceShape.BOWL && shape != BlockFaceShape.UNDEFINED) || worldIn.isSideSolid(offsetPos, offsetFacing);
    		} else {
    			offsetPos = pos.offset(side.getOpposite());
    			offsetFacing = side;
    			BlockFaceShape shape = worldIn.getBlockState(offsetPos).getBlockFaceShape(worldIn, offsetPos, offsetFacing);
    			return shape == BlockFaceShape.SOLID || worldIn.isSideSolid(offsetPos, offsetFacing);
    		}
    	}
    	return false;
    }
    
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
    	boolean onWall = state.getValue(ON_WALL);
    	EnumFacing facing = state.getValue(FACING);

    	if(onWall) {
    		BlockPos offsetPos = pos.offset(facing);
    		BlockFaceShape shape = worldIn.getBlockState(offsetPos).getBlockFaceShape(worldIn, offsetPos, facing.getOpposite());
    		return shape == BlockFaceShape.SOLID || worldIn.isSideSolid(offsetPos, facing.getOpposite());
    	} else {
    		BlockPos offsetPos = pos.down();
    		BlockFaceShape shape = worldIn.getBlockState(offsetPos).getBlockFaceShape(worldIn, offsetPos, EnumFacing.UP);
    		return (shape != BlockFaceShape.BOWL && shape != BlockFaceShape.UNDEFINED) || worldIn.isSideSolid(offsetPos, EnumFacing.UP);
    	}
    }

    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
    	if (!this.canBlockStay(worldIn, pos, state)) {
    		this.dropBlockAsItem(worldIn, pos, state, 0);
    		
    		TileEntity tile = worldIn.getTileEntity(pos);
    		if(tile instanceof TileEntityMothHouse) {
    			InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityMothHouse) tile);
    		}
    		
    		worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    	}
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
    	this.checkAndDropBlock(worldIn, pos, state);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    	this.checkAndDropBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!world.isRemote) {
			if (world.getTileEntity(pos) instanceof TileEntityMothHouse) {
				TileEntityMothHouse tile = (TileEntityMothHouse) world.getTileEntity(pos);
				if(placer instanceof EntityPlayer)
					tile.setPlacer((EntityPlayer) placer);
				tile.markForUpdate();
			}
		}
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityMothHouse();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ON_WALL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 0b11)).withProperty(ON_WALL, (meta & 0b100) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(FACING).getIndex() & 0b11) | (state.getValue(ON_WALL) ? 0b100 : 0);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);

        if (!world.isRemote) {
            if (world.getTileEntity(pos) instanceof TileEntityMothHouse) {
                TileEntityMothHouse tile = (TileEntityMothHouse) world.getTileEntity(pos);

                if(tile == null)
                    return false;

                if(heldItem.isEmpty()) {
                	// Extract silk first so that grubs are destroyed
                    extractItems(world, pos, state, player, hand, tile, false);

                    if(player.isSneaking()) {
                        extractItems(world, pos, state, player, hand, tile, true);
                    }
                    
                    return true;
                } else if(heldItem.getItem() == ItemRegistry.SILK_GRUB) {
                    int grubCount = tile.addGrubs(heldItem);

                    if (!player.capabilities.isCreativeMode) {
                        player.getHeldItem(hand).shrink(grubCount);
                    }

                    tile.markForUpdate();
                }
            }
        }

        return true;
    }
    
    protected void extractItems(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, TileEntityMothHouse tile, boolean grubs) {
    	ItemStack itemStack = tile.getStackInSlot(grubs ? TileEntityMothHouse.SLOT_GRUBS : TileEntityMothHouse.SLOT_SILK);

        if(itemStack != ItemStack.EMPTY) {
            player.addItemStackToInventory(itemStack.copy());
            tile.setInventorySlotContents(grubs ? TileEntityMothHouse.SLOT_GRUBS : TileEntityMothHouse.SLOT_SILK, ItemStack.EMPTY);
        }
    }


    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityMothHouse tile = (TileEntityMothHouse) world.getTileEntity(pos);

        if (tile != null) {
            InventoryHelper.dropInventoryItems(world, pos, tile);
            world.removeTileEntity(pos);
        }

        super.breakBlock(world, pos, state);
    }
}
