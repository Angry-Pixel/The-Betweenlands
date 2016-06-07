package thebetweenlands.common.block.container;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.tileentity.TileEntityDruidAltar;

import javax.annotation.Nullable;

public class BlockDruidAltar extends BasicBlock implements ITileEntityProvider {
    public BlockDruidAltar() {
        super(Material.ROCK);
        setBlockUnbreakable();
        setResistance(100.0F);
        setSoundType(SoundType.STONE);
        setCreativeTab(BLCreativeTabs.BLOCKS);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDruidAltar();
    }


    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
            return false;

        if (worldIn.getTileEntity(pos) instanceof TileEntityDruidAltar) {
            TileEntityDruidAltar altar = (TileEntityDruidAltar) worldIn.getTileEntity(pos);
            if (altar.craftingProgress == 0) {
                playerIn.openGui(TheBetweenlands.instance, CommonProxy.GUI_DRUID_ALTAR, worldIn, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }

        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
}
