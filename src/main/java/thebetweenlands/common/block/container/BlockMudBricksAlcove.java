package thebetweenlands.common.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.tile.TileEntityLootInventory;
import thebetweenlands.common.tile.TileEntityMudBricksAlcove;

public class BlockMudBricksAlcove extends BasicBlock implements ITileEntityProvider {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockMudBricksAlcove() {
		this(Material.GLASS);
	}

	public BlockMudBricksAlcove(Material material) {
		super(material);
		setHardness(0.4f);
		setSoundType(SoundType.GLASS);
		setHarvestLevel("pickaxe", 0);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	
	@Nullable
	public static TileEntityMudBricksAlcove getTileEntity(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityMudBricksAlcove) {
			return (TileEntityMudBricksAlcove) tile;
		}
		return null;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMudBricksAlcove();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		meta = meta | ((EnumFacing) state.getValue(FACING)).getIndex();
		return meta;
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		TileEntityMudBricksAlcove tile = getTileEntity(world, pos);
		if (tile instanceof TileEntityMudBricksAlcove) {
			if (!tile.greebled)
				tile.setUpGreeble();
			((TileEntityMudBricksAlcove) tile).setLootTable(LootTableRegistry.ANIMATOR_SCROLL, world.rand.nextLong());
			if (tile.has_urn) {
				IInventory tileInv = (IInventory) tile;
				if (tileInv != null)
					((TileEntityLootInventory) tileInv).fillInventoryWithLoot(null);
			}
		}
		world.notifyBlockUpdate(pos, state, state, 3);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntityMudBricksAlcove tile = getTileEntity(world, pos);
			if (tile instanceof TileEntityMudBricksAlcove) {
				if (tile.has_urn && facing == state.getValue(FACING)) {
					BlockPos offsetPos = pos.offset(facing);
					IInventory tileInv = (IInventory) tile;
					if (tileInv != null)
						InventoryHelper.dropInventoryItems(world, offsetPos, tileInv);
					if (world.rand.nextInt(3) == 0) {
						EntityChicken entity = new EntityChicken(world); //ash sprite here :P
						entity.setLocationAndAngles(offsetPos.getX() + 0.5D, offsetPos.getY(), offsetPos.getZ() + 0.5D, 0.0F, 0.0F);
						world.spawnEntity(entity);
					}
					world.playSound(null, pos, blockSoundType.getBreakSound(), SoundCategory.BLOCKS, 0.5F, 1F);
					world.playEvent(null, 2001, pos, Block.getIdFromBlock(BlockRegistry.MUD_FLOWER_POT)); //this will do unless we want specific particles
					tile.has_urn = false;
					world.notifyBlockUpdate(pos, state, state, 3);
					return true;
				}
			}
		} else 
			return true;
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
