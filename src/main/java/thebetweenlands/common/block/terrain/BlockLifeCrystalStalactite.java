package thebetweenlands.common.block.terrain;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.block.property.PropertyBoolUnlisted;
import thebetweenlands.common.block.property.PropertyIntegerUnlisted;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.item.misc.ItemGemSinger;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.storage.BetweenlandsChunkStorage;
import thebetweenlands.util.AdvancedStateMap;

public class BlockLifeCrystalStalactite extends BlockSwampWater implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeItemBlockModelDefinition, IStateMappedBlock {
	public static final PropertyEnum<EnumLifeCrystalType> VARIANT = PropertyEnum.<EnumLifeCrystalType>create("variant", EnumLifeCrystalType.class);
	public static final PropertyBoolUnlisted NO_BOTTOM = new PropertyBoolUnlisted("no_bottom");
	public static final PropertyBoolUnlisted NO_TOP = new PropertyBoolUnlisted("no_top");
	public static final PropertyIntegerUnlisted DIST_UP = new PropertyIntegerUnlisted("dist_up");
	public static final PropertyIntegerUnlisted DIST_DOWN = new PropertyIntegerUnlisted("dist_down");
	public static final PropertyIntegerUnlisted POS_X = new PropertyIntegerUnlisted("pos_x");
	public static final PropertyIntegerUnlisted POS_Y = new PropertyIntegerUnlisted("pos_x");
	public static final PropertyIntegerUnlisted POS_Z = new PropertyIntegerUnlisted("pos_z");

	public BlockLifeCrystalStalactite(Fluid fluid, Material materialIn) {
		super(fluid, materialIn);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumLifeCrystalType.DEFAULT));
		this.setHardness(2.5F);
		this.setResistance(10.0F);
		this.setUnderwaterBlock(true);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, EnumLifeCrystalType.DEFAULT.getMetadata()));
		items.add(new ItemStack(this, 1, EnumLifeCrystalType.ORE.getMetadata()));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, ((EnumLifeCrystalType)state.getValue(VARIANT)).getMetadata());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumLifeCrystalType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumLifeCrystalType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer((ExtendedBlockState) super.createBlockState(), 
				new IProperty<?>[]{ VARIANT }, 
				new IUnlistedProperty<?>[] {
					POS_X,
					POS_Y,
					POS_Z,
					NO_BOTTOM,
					NO_TOP,
					DIST_UP,
					DIST_DOWN
				});
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, ((EnumLifeCrystalType)state.getValue(VARIANT)).getMetadata());
	}
	
	public static enum EnumLifeCrystalType implements IStringSerializable {
		DEFAULT,
		ORE;

		private final String name;

		private EnumLifeCrystalType() {
			this.name = this.name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumLifeCrystalType byMetadata(int metadata) {
			if (metadata < 0 || metadata >= EnumLifeCrystalType.values().length) {
				metadata = 0;
			}
			return EnumLifeCrystalType.values()[metadata];
		}

		@Override
		public String getName() {
			return this.name;
		}
	}

	@Override
	public ItemBlock getItemBlock() {
		return ItemBlockEnum.create(this, EnumLifeCrystalType.class);
	}

	@Override
	public boolean isBlockNormalCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) super.getExtendedState(oldState, worldIn, pos);

		final int maxLength = 32;
		int distUp = 0;
		int distDown = 0;
		boolean noTop = false;
		boolean noBottom = false;

		IBlockState blockState;
		//Block block;
		for(distUp = 0; distUp < maxLength; distUp++) {
			blockState = worldIn.getBlockState(pos.add(0, 1 + distUp, 0));
			if(blockState.getBlock() == this)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.isOpaqueCube())
				noTop = true;
			break;
		}
		for(distDown = 0; distDown < maxLength; distDown++)
		{
			blockState = worldIn.getBlockState(pos.add(0, -(1 + distDown), 0));
			if(blockState.getBlock() == this)
				continue;
			if(blockState.getBlock() == Blocks.AIR || !blockState.isOpaqueCube())
				noBottom = true;
			break;
		}

		return state.withProperty(POS_X, pos.getX()).withProperty(POS_Y, pos.getY()).withProperty(POS_Z, pos.getZ()).withProperty(DIST_UP, distUp).withProperty(DIST_DOWN, distDown).withProperty(NO_TOP, noTop).withProperty(NO_BOTTOM, noBottom);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(VARIANT);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumLifeCrystalType.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + EnumLifeCrystalType.values()[meta].getName();
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(VARIANT) == EnumLifeCrystalType.ORE ? ItemRegistry.LIFE_CRYSTAL : null;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		return world.setBlockState(pos, BlockRegistry.SWAMP_WATER.getDefaultState(), world.isRemote ? 11 : 3);
	}	
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return (int)((state.getValue(VARIANT) == EnumLifeCrystalType.ORE ? 0.4F : 0.0F) * 15.0F);
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);

		if(state.getValue(VARIANT) == EnumLifeCrystalType.ORE) {
			BetweenlandsChunkStorage.markGem(worldIn, pos, ItemGemSinger.GemSingerTarget.LIFE_CRYSTAL);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);

		if(state.getValue(VARIANT) == EnumLifeCrystalType.ORE) {
			BetweenlandsChunkStorage.unmarkGem(worldIn, pos, ItemGemSinger.GemSingerTarget.LIFE_CRYSTAL);
		}
	}
}
