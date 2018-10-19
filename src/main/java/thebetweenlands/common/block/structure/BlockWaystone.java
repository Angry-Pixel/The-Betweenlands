package thebetweenlands.common.block.structure;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.TileEntityWaystone;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class BlockWaystone extends BlockContainer {
	public static final PropertyEnum<Part> PART = PropertyEnum.create("part", Part.class);
	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	public BlockWaystone() {
		super(Material.ROCK);
		this.setHardness(25.0F);
		this.setResistance(10000.0F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(PART, Part.BOTTOM).withProperty(ACTIVE, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, PART, ACTIVE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(PART).getMeta() | (state.getValue(ACTIVE) ? 0b100 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(PART, Part.fromMeta(meta & 0b11)).withProperty(ACTIVE, (meta & 0b100) != 0);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return super.canPlaceBlockAt(world, pos) && super.canPlaceBlockAt(world, pos.up()) && super.canPlaceBlockAt(world, pos.up(2));
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		if(!worldIn.isRemote) {
			IBlockState stateTop = this.getDefaultState().withProperty(PART, Part.TOP);
			IBlockState stateMiddle = this.getDefaultState().withProperty(PART, Part.MIDDLE);

			worldIn.setBlockState(pos.up(2), stateTop, 3);
			worldIn.setBlockState(pos.up(), stateMiddle, 3);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);

		ILocalStorageHandler localStorageHandler = BetweenlandsWorldStorage.forWorld(worldIn).getLocalStorageHandler();
		List<LocationStorage> waystoneLocations = localStorageHandler.getLocalStorages(LocationStorage.class, new AxisAlignedBB(pos), storage -> storage.getType() == EnumLocationType.WAYSTONE);
		for(LocationStorage waystoneLocation : waystoneLocations) {
			localStorageHandler.removeLocalStorage(waystoneLocation);
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if(!this.isValidWaystone(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	public boolean isValidWaystone(World world, BlockPos pos, IBlockState state) {
		switch(state.getValue(PART)) {
		case TOP: {
			IBlockState down1 = world.getBlockState(pos.down());
			IBlockState down2 = world.getBlockState(pos.down(2));
			return down1.getBlock() == this && down1.getValue(PART) == Part.MIDDLE && down2.getBlock() == this && down2.getValue(PART) == Part.BOTTOM;
		}
		case MIDDLE: {
			IBlockState down1 = world.getBlockState(pos.down());
			IBlockState up1 = world.getBlockState(pos.up());
			return down1.getBlock() == this && down1.getValue(PART) == Part.BOTTOM && up1.getBlock() == this && up1.getValue(PART) == Part.TOP;
		}
		default:
		case BOTTOM: {
			IBlockState up1 = world.getBlockState(pos.up());
			IBlockState up2 = world.getBlockState(pos.up(2));
			return up1.getBlock() == this && up1.getValue(PART) == Part.MIDDLE && up2.getBlock() == this && up2.getValue(PART) == Part.TOP;
		}
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		//Only drop once
		if(state.getValue(PART) == Part.BOTTOM) {
			super.getDrops(drops, world, pos, state, fortune);
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if(!worldIn.isRemote) {
			return new TileEntityWaystone(worldIn.rand.nextFloat() * 360.0F);
		}
		return new TileEntityWaystone();
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public int getLightValue(IBlockState state) {
		return state.getValue(ACTIVE) ? 8 : 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		for(int i = 0; i < 16; i++) {
			worldIn.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, pos.getX() + 0.5D + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 16, pos.getY() + 0.5D + rand.nextFloat() * 6 - 3, pos.getZ() + 0.5D + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 16, 0, 0.2D, 0);
		}
	}

	public static enum Part implements IStringSerializable {
		TOP("top", 0), MIDDLE("middle", 1), BOTTOM("bottom", 2);

		private final String name;
		private final int meta;

		private Part(String name, int meta) {
			this.name = name;
			this.meta = meta;
		}

		public int getMeta() {
			return this.meta;
		}

		public static Part fromMeta(int meta) {
			for(Part part : values()) {
				if(part.meta == meta) {
					return part;
				}
			}
			return null;
		}

		@Override
		public String getName() {
			return this.name;
		}
	}
}
