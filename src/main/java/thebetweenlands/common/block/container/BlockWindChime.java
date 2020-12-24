package thebetweenlands.common.block.container;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.TileEntityWindChime;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class BlockWindChime extends BlockContainer {
	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.15D, 0.0D, 0.15D, 0.85D, 1.0D, 0.85D);

	public BlockWindChime() {
		super(Material.WOOD);
		this.setSoundType(SoundType.WOOD);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		setHardness(0.5F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityWindChime();
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && (worldIn.isSideSolid(pos.up(), EnumFacing.DOWN) || worldIn.getBlockState(pos.up()).getBlockFaceShape(worldIn, pos.up(), EnumFacing.DOWN) != BlockFaceShape.UNDEFINED);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if(!worldIn.isSideSolid(pos.up(), EnumFacing.DOWN) && worldIn.getBlockState(pos.up()).getBlockFaceShape(worldIn, pos.up(), EnumFacing.DOWN) == BlockFaceShape.UNDEFINED) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(hand == EnumHand.MAIN_HAND && playerIn.isSneaking()) {

			if(!worldIn.isRemote) {
				TileEntity tile = worldIn.getTileEntity(pos);

				if(tile instanceof TileEntityWindChime) {
					TileEntityWindChime chime = (TileEntityWindChime) tile;

					ResourceLocation newAttunement = chime.cycleAttunedEvent();

					IEnvironmentEvent attunedEvent;
					if(newAttunement != null) {
						attunedEvent = BetweenlandsWorldStorage.forWorld(worldIn).getEnvironmentEventRegistry().getEvent(newAttunement);
					} else {
						attunedEvent = null;
					}

					if(newAttunement != null) {
						playerIn.sendStatusMessage(new TextComponentTranslation("chat.wind_chime.changed_attunement", new TextComponentTranslation(attunedEvent.getLocalizationEventName())), true);
					} else {
						playerIn.sendStatusMessage(new TextComponentTranslation("chat.wind_chime.removed_attunement"), true);
					}
				}
			}

			playerIn.swingArm(hand);

			return true;
		}

		return false;
	}
}
