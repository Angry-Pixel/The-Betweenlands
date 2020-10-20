package thebetweenlands.common.block.structure;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.item.ItemBlockEnum.IGenericMetaSelector;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;
import thebetweenlands.common.tile.TileEntitySimulacrum;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockSimulacrum extends BlockContainer implements IStateMappedBlock, ICustomItemBlock, ISubtypeItemBlockModelDefinition {
	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.15D, 0.0D, 0.15D, 0.85D, 1.0D, 0.85D);

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

	public BlockSimulacrum() {
		super(Material.ROCK);
		this.setHardness(25.0F);
		this.setResistance(10000.0F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, VARIANT);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, Variant.byMetadata(meta)).withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMetadata(state.getValue(FACING));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int rotation = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D + 2) & 3;
		state = state.withProperty(FACING, EnumFacing.byHorizontalIndex(rotation));
		state = state.withProperty(VARIANT, Variant.byMetadata(stack.getItemDamage()));
		worldIn.setBlockState(pos, state, 3);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState();
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if(!worldIn.isSideSolid(pos.down(), EnumFacing.UP)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
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

	/*@SideOnly(Side.CLIENT)
	@Override
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}*/

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySimulacrum();
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	/*@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		for(int i = 0; i < 16; i++) {
			worldIn.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, pos.getX() + 0.5D + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 16, pos.getY() + 0.5D + rand.nextFloat() * 6 - 3, pos.getZ() + 0.5D + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 16, 0, 0.2D, 0);
		}
	}*/

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(VARIANT).withPropertySuffix(VARIANT, v -> v.name);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, Variant.ONE.getMetadata(EnumFacing.NORTH)));
		list.add(new ItemStack(this, 1, Variant.TWO.getMetadata(EnumFacing.NORTH)));
		list.add(new ItemStack(this, 1, Variant.THREE.getMetadata(EnumFacing.NORTH)));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, state.getValue(VARIANT).getMetadata(EnumFacing.NORTH));
	}

	@Override
	public int getSubtypeNumber() {
		return Variant.values().length * 4;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + Variant.byMetadata(meta).getName();
	}

	@Override
	public ItemBlock getItemBlock() {
		return ItemBlockEnum.create(this, Variant.class);
	}

	public enum Variant implements IStringSerializable, IGenericMetaSelector {
		ONE("1"),
		TWO("2"),
		THREE("3");

		private final String name;

		private Variant(String name) {
			this.name = name.toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata(EnumFacing facing) {
			return facing.getHorizontalIndex() | (this.ordinal() << 2);
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static Variant byMetadata(int metadata) {
			metadata >>= 2;
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public boolean isMetadataMatching(int meta) {
			return byMetadata(meta) == this;
		}
	}
}
