package thebetweenlands.common.block.container;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.tile.TileEntityDruidAltar;

public class BlockDruidAltar extends BasicBlock implements ITileEntityProvider {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public BlockDruidAltar() {
		super(Material.ROCK);
		setHardness(8.0F);
		setResistance(100.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().with(ACTIVE, false));
		setItemDropped(() -> null);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ACTIVE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().with(ACTIVE, meta != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.get(ACTIVE) ? 1 : 0;
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
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityDruidAltar) {
			TileEntityDruidAltar altar = (TileEntityDruidAltar) tile;
			if (altar.craftingProgress == 0) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_DRUID_ALTAR, world, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}

		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileEntity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IWorldReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
