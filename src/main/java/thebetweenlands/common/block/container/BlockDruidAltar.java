package thebetweenlands.common.block.container;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
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
import thebetweenlands.common.tile.TileEntityDruidAltar;

public class BlockDruidAltar extends BasicBlock implements ITileEntityProvider {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockDruidAltar() {
        super(Material.ROCK);
        setBlockUnbreakable();
        setResistance(100.0F);
        setSoundType(SoundType.STONE);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ACTIVE, meta != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 1 : 0;
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return false;

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityDruidAltar) {
            TileEntityDruidAltar altar = (TileEntityDruidAltar) tile;
            if (altar.craftingProgress == 0) {
                player.openGui(TheBetweenlands.INSTANCE, CommonProxy.GUI_DRUID_ALTAR, world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }

        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
