package thebetweenlands.common.block.misc;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityGroundItem;

import javax.annotation.Nullable;

public class BlockGroundItem extends Block implements BlockRegistry.ICustomItemBlock {

    public static AxisAlignedBB BOUNDING_AABB = new AxisAlignedBB(0.15F, 0.0F, 0.15F, 0.85F, 0.45F, 0.85F);

    public BlockGroundItem() {
        super(Material.GROUND);
        setSoundType(SoundType.GROUND);
        setHardness(0.1F);
        setDefaultState(this.blockState.getBaseState());
    }

    public static void create(World world, BlockPos pos, ItemStack stack) {
        Block block = BlockRegistry.GROUND_ITEM;
        if (!world.isRemote && block.canPlaceBlockAt(world, pos)) {
            world.setBlockState(pos, block.getDefaultState());
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityGroundItem) {
                ((TileEntityGroundItem) tileEntity).setStack(stack);
            }
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Nullable
    @Override
    public ItemBlock getItemBlock() {
        return null;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityGroundItem) {
            return ((TileEntityGroundItem) tileEntity).getStack();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityGroundItem && !worldIn.isRemote) {
            ItemStack itemStack = ((TileEntityGroundItem) tileEntity).getStack();
            ItemHandlerHelper.giveItemToPlayer(playerIn, itemStack);
            worldIn.setBlockToAir(pos);
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    public boolean canStay(World world, BlockPos pos) {
        IBlockState downState = world.getBlockState(pos);
        return downState.isSideSolid(world, pos, EnumFacing.UP) || downState.getBlockFaceShape(world, pos, EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canStay(worldIn, pos.down())){
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            harvestBlock(worldIn, null, pos, state, tileEntity, ItemStack.EMPTY);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (te instanceof TileEntityGroundItem && !worldIn.isRemote && !((TileEntityGroundItem) te).getStack().isEmpty()) {
            spawnAsEntity(worldIn, pos, ((TileEntityGroundItem) te).getStack());
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this);
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && canStay(worldIn, pos.down());
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityGroundItem();
    }
}
