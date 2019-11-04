package thebetweenlands.common.block.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class BlockWeedwoodWorkbench extends BlockContainer {
    public BlockWeedwoodWorkbench() {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        setTranslationKey("thebetweenlands.weedwoodCraftingTable");
        setHardness(2.5F);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if (!world.isRemote) {
            player.openGui(TheBetweenlands.instance, CommonProxy.GUI_WEEDWOOD_CRAFT, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        ((TileEntityWeedwoodWorkbench) world.getTileEntity(pos)).rotation = (byte) (((MathHelper.floor((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 1) % 4);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote)
            return;
        TileEntityWeedwoodWorkbench tile = (TileEntityWeedwoodWorkbench) world.getTileEntity(pos);
        for (ItemStack stack : tile.craftingSlots) {
            if (!stack.isEmpty()) {
                float offset = 0.7F;
                double offsetX = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5D;
                double offsetY = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5D;
                double offsetZ = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5D;
                EntityItem item = new EntityItem(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, stack);
                item.setDefaultPickupDelay();
                world.spawnEntity(item);
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityWeedwoodWorkbench();
    }
}
