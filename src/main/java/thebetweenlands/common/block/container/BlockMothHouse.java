package thebetweenlands.common.block.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityMothHouse;

import javax.annotation.Nullable;

public class BlockMothHouse  extends BlockContainer {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockMothHouse() {
        super(Material.WOOD);
        setHardness(2.0F);
        setResistance(5.0F);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
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
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
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
                    int slotToTakeFrom = 1;

                    if(player.isSneaking()) {
                        slotToTakeFrom = 0;
                    }

                    ItemStack itemStack = tile.getStackInSlot(slotToTakeFrom);

                    if(itemStack != ItemStack.EMPTY) {
                        player.addItemStackToInventory(itemStack.copy());
                        tile.setInventorySlotContents(slotToTakeFrom, ItemStack.EMPTY);
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
