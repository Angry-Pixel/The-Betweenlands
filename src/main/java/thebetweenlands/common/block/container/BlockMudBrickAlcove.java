package thebetweenlands.common.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.property.PropertyBoolUnlisted;
import thebetweenlands.common.block.property.PropertyIntegerUnlisted;
import thebetweenlands.common.entity.mobs.EntityAshSprite;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityLootInventory;
import thebetweenlands.common.tile.TileEntityMudBrickAlcove;
import thebetweenlands.util.StatePropertyHelper;

public class BlockMudBrickAlcove extends BasicBlock implements ITileEntityProvider {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public static final IUnlistedProperty<Integer> LEVEL = new PropertyIntegerUnlisted("level");
	public static final IUnlistedProperty<Boolean> TOP_COBWEB = new PropertyBoolUnlisted("top_cobweb");
	public static final IUnlistedProperty<Boolean> BOTTOM_COBWEB = new PropertyBoolUnlisted("bottom_cobweb");
	public static final IUnlistedProperty<Boolean> SMALL_CANDLE = new PropertyBoolUnlisted("small_candle");
	public static final IUnlistedProperty<Boolean> BIG_CANDLE = new PropertyBoolUnlisted("big_candle");
	
	public static final IProperty<Boolean> HAS_URN = PropertyBool.create("urn");
	
	public BlockMudBrickAlcove() {
		this(Material.ROCK);
	}

	public BlockMudBrickAlcove(Material material) {
		super(material);
		setLightOpacity(255);
		setHardness(0.4f);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HAS_URN, true));
	}
	
	@Nullable
	public static TileEntityMudBrickAlcove getTileEntity(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityMudBrickAlcove) {
			return (TileEntityMudBrickAlcove) tile;
		}
		return null;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] {FACING, HAS_URN}, new IUnlistedProperty[] {LEVEL, TOP_COBWEB, BOTTOM_COBWEB, SMALL_CANDLE, BIG_CANDLE});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntityMudBrickAlcove tile = StatePropertyHelper.getTileEntityThreadSafe(worldIn, pos, TileEntityMudBrickAlcove.class);
		if(tile != null) {
			state = state.withProperty(HAS_URN, tile.has_urn);
		}
		return state;
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
		IExtendedBlockState extended = (IExtendedBlockState) oldState;
		
		TileEntityMudBrickAlcove tile = StatePropertyHelper.getTileEntityThreadSafe(worldIn, pos, TileEntityMudBrickAlcove.class);
		if(tile != null) {
			extended = extended.withProperty(TOP_COBWEB, tile.top_web)
					.withProperty(BOTTOM_COBWEB, tile.bottom_web)
					.withProperty(SMALL_CANDLE, tile.small_candle)
					.withProperty(BIG_CANDLE, tile.big_candle)
					.withProperty(LEVEL, tile.dungeon_level);
		}
		
		return extended;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMudBrickAlcove();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	 public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
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
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		TileEntityMudBrickAlcove tile = getTileEntity(world, pos);
		if (tile instanceof TileEntityMudBrickAlcove) {
			if (!tile.greebled)
				tile.setUpGreeble();
			((TileEntityMudBrickAlcove) tile).setLootTable(LootTableRegistry.ANIMATOR_SCROLL, world.rand.nextLong());
			if (tile.has_urn) {
				IInventory tileInv = (IInventory) tile;
				if (tileInv != null)
					((TileEntityLootInventory) tileInv).fillInventoryWithLoot(null);
			}
			if (tile.facing <= 1)
				tile.setFacing(state.getValue(FACING).getIndex());
		}
		world.notifyBlockUpdate(pos, state, state, 3);
	}
	
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		if (!world.isRemote) {
			TileEntityMudBrickAlcove tile = getTileEntity(world, pos);
			if (tile instanceof TileEntityMudBrickAlcove) {
				if (tile.has_urn) {
					IBlockState state = world.getBlockState(pos);
					RayTraceResult ray = this.rayTrace(pos, player.getPositionEyes(1), player.getPositionEyes(1).add(player.getLookVec().scale(10)), state.getBoundingBox(world, pos));
					if(ray != null && state.getValue(FACING) == ray.sideHit) {
						BlockPos offsetPos = pos.offset(ray.sideHit);
						IInventory tileInv = (IInventory) tile;
						if (tileInv != null)
							InventoryHelper.dropInventoryItems(world, offsetPos, tileInv);
						if (world.rand.nextInt(3) == 0) {
							EntityAshSprite entity = new EntityAshSprite (world); //ash sprite here :P
							entity.setLocationAndAngles(offsetPos.getX() + 0.5D, offsetPos.getY(), offsetPos.getZ() + 0.5D, 0.0F, 0.0F);
							entity.setBoundOrigin(offsetPos);
							world.spawnEntity(entity);
						}
						world.playSound(null, pos, blockSoundType.getBreakSound(), SoundCategory.BLOCKS, 0.5F, 1F);
						world.playEvent(null, 2001, pos, Block.getIdFromBlock(BlockRegistry.MUD_FLOWER_POT)); //this will do unless we want specific particles
						tile.has_urn = false;
						world.notifyBlockUpdate(pos, state, state, 2);
					}
				}
			}
		}
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		EnumFacing facing = state.getValue(FACING);
    	return facing.getOpposite() == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		EnumFacing facing = base_state.getValue(FACING);
    	return facing.getOpposite() == side;
	}
}
