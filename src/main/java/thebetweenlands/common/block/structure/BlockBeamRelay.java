package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityBeamRelay;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockBeamRelay extends BlockDirectional implements ITileEntityProvider, IStateMappedBlock {
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockBeamRelay() {
		super(Material.ROCK);
		setDefaultState(this.getBlockState().getBaseState().withProperty(POWERED, false));
		setHardness(10.0F);
		setResistance(2000.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);
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
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBeamRelay();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		meta = meta | ((EnumFacing) state.getValue(FACING)).getIndex();

		if (((Boolean) state.getValue(POWERED)).booleanValue())
			meta |= 8;

		return meta;
	}

	@Override
	 public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, getFacingFromEntity(pos, placer)).withProperty(POWERED, world.isBlockPowered(pos));
	}

	public static EnumFacing getFacingFromEntity(BlockPos pos, EntityLivingBase entity) {
		if (MathHelper.abs((float) entity.posX - (float) pos.getX()) < 2.0F && MathHelper.abs((float) entity.posZ - (float) pos.getZ()) < 2.0F) {
			double eyeHeight = entity.posY + (double) entity.getEyeHeight();
			if (eyeHeight - (double) pos.getY() > 2.0D)
				return EnumFacing.UP;
			if ((double) pos.getY() - eyeHeight > 0.0D)
				return EnumFacing.DOWN;
		}
		return entity.getHorizontalFacing().getOpposite();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, POWERED });
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;
		if (world.getTileEntity(pos) instanceof TileEntityBeamRelay) {
			TileEntityBeamRelay tile = (TileEntityBeamRelay) world.getTileEntity(pos);
			tile.deactivateBlock();
		}
		state = state.cycleProperty(FACING);
		world.setBlockState(pos, state, 3);
		world.playSound((EntityPlayer)null, pos, SoundRegistry.BEAM_SWITCH, SoundCategory.BLOCKS, 0.5F, 1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		return true;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (world.getTileEntity(pos) instanceof TileEntityBeamRelay) {
			TileEntityBeamRelay tile = (TileEntityBeamRelay) world.getTileEntity(pos);
			tile.deactivateBlock();
		}
    }

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(new IProperty[] {POWERED}).build();
	}
}
