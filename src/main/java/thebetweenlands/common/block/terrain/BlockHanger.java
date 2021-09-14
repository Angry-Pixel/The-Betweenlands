package thebetweenlands.common.block.terrain;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockHanger extends Block implements IShearable, ISickleHarvestable, IStateMappedBlock, ISubtypeItemBlockModelDefinition, ICustomItemBlock, ITintedBlock {
	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);

	public static final PropertyBool CAN_GROW = PropertyBool.create("can_grow");
	public static final PropertyBool SEEDED = PropertyBool.create("seeded");

	public BlockHanger() {
		super(Material.PLANTS);
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.1F);
		this.setCreativeTab(BLCreativeTabs.PLANTS);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(CAN_GROW, true).withProperty(SEEDED, false));
	}

	@Override
	public void getSubBlocks(CreativeTabs item, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 3));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return null;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState stateAbove = worldIn.getBlockState(pos.up());
		boolean canHangOn = stateAbove.getMaterial() == Material.LEAVES || stateAbove.isSideSolid(worldIn, pos.up(), EnumFacing.DOWN) || stateAbove.getBlock() == this;
		return super.canPlaceBlockAt(worldIn, pos) && canHangOn;
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
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		IBlockState stateAbove = worldIn.getBlockState(pos.up());
		boolean canHangOn = stateAbove.getMaterial() == Material.LEAVES || stateAbove.isSideSolid(worldIn, pos.up(), EnumFacing.DOWN) || stateAbove.getBlock() == this;
		if (!canHangOn) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(rand.nextInt(16) == 0 && state.getValue(CAN_GROW) && worldIn.isAirBlock(pos.down())) {
			worldIn.setBlockState(pos.down(), this.getDefaultState());
		}
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS || item.getItem() == ItemRegistry.SILT_CRAB_CLAW;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(EnumItemPlantDrop.HANGER_ITEM.create(1));
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(CAN_GROW).ignore(SEEDED).withPropertySuffixTrue(SEEDED, "seeded");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {CAN_GROW, SEEDED});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = this.getDefaultState();
		state = state.withProperty(CAN_GROW, (meta & 1) != 0);
		state = state.withProperty(SEEDED, (meta & 2) != 0);
		return state;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		if(state.getValue(CAN_GROW)) {
			meta |= 1;
		}
		if(state.getValue(SEEDED)) {
			meta |= 2;
		}
		return meta;
	}

	@Override
	public int getSubtypeNumber() {
		return 2;
	}

	@Override
	public int getSubtypeMeta(int subtype) {
		switch(subtype) {
		default:
		case 0:
			return 0;
		case 1:
			return 3;
		}
	}

	@Override
	public String getSubtypeName(int meta) {
		switch(meta) {
		default:
		case 0:
			return "%s";
		case 3:
			return "%s_seeded";
		}
	}

	@Override
	public ItemBlock getItemBlock() {
		ItemBlock item = new ItemBlock(this) {
			@Override
			public String getTranslationKey(ItemStack stack) {
				IBlockState state = this.block.getStateFromMeta(this.getMetadata(stack.getItemDamage()));
				return this.block.getTranslationKey() + (state.getValue(SEEDED) ? "_seeded" : "");
			}

			@Override
			public int getMetadata(int damage) {
				return damage;
			}
		};
		item.setHasSubtypes(true);
		return item;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(SEEDED) ? 3 : 0);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		if(state.getValue(SEEDED)) {
			drops.add(new ItemStack(ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS));
			return;
		}
		super.getDrops(drops, world, pos, state, fortune);
	}
	
	@Override
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : -1;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(CAN_GROW, true);
	}
}
