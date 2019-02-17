package thebetweenlands.common.block.terrain;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.Fluid;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.block.property.BooleanPropertyUnlisted;
import thebetweenlands.common.block.property.IntegerPropertyUnlisted;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.item.misc.ItemGemSinger;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistryOld.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.storage.BetweenlandsChunkStorage;
import thebetweenlands.util.AdvancedStateMap;

public class BlockLifeCrystalStalactite extends BlockSwampWater implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeItemBlockModelDefinition, IStateMappedBlock {
	public static final EnumProperty<EnumLifeCrystalType> VARIANT = EnumProperty.<EnumLifeCrystalType>create("variant", EnumLifeCrystalType.class);
	public static final BooleanPropertyUnlisted NO_BOTTOM = new BooleanPropertyUnlisted("no_bottom");
	public static final BooleanPropertyUnlisted NO_TOP = new BooleanPropertyUnlisted("no_top");
	public static final IntegerPropertyUnlisted DIST_UP = new IntegerPropertyUnlisted("dist_up");
	public static final IntegerPropertyUnlisted DIST_DOWN = new IntegerPropertyUnlisted("dist_down");
	public static final IntegerPropertyUnlisted POS_X = new IntegerPropertyUnlisted("pos_x");
	public static final IntegerPropertyUnlisted POS_Y = new IntegerPropertyUnlisted("pos_x");
	public static final IntegerPropertyUnlisted POS_Z = new IntegerPropertyUnlisted("pos_z");

	public BlockLifeCrystalStalactite(Fluid fluid, Material materialIn) {
		super(fluid, materialIn);
		this.setDefaultState(this.blockState.getBaseState().with(VARIANT, EnumLifeCrystalType.DEFAULT));
		this.setHardness(2.5F);
		this.setResistance(10.0F);
		this.setUnderwaterBlock(true);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, EnumLifeCrystalType.DEFAULT.getMetadata()));
		items.add(new ItemStack(this, 1, EnumLifeCrystalType.ORE.getMetadata()));
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, ((EnumLifeCrystalType)state.get(VARIANT)).getMetadata());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().with(VARIANT, EnumLifeCrystalType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumLifeCrystalType)state.get(VARIANT)).getMetadata();
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
		return new ItemStack(Item.getItemFromBlock(this), 1, ((EnumLifeCrystalType)state.get(VARIANT)).getMetadata());
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
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IWorldReader worldIn, BlockPos pos) {
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

		return state.with(POS_X, pos.getX()).with(POS_Y, pos.getY()).with(POS_Z, pos.getZ()).with(DIST_UP, distUp).with(DIST_DOWN, distDown).with(NO_TOP, noTop).with(NO_BOTTOM, noBottom);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
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
	public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
		return 1;
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return state.get(VARIANT) == EnumLifeCrystalType.ORE ? ItemRegistry.LIFE_CRYSTAL : Items.AIR;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		return world.setBlockState(pos, BlockRegistry.SWAMP_WATER.getDefaultState(), world.isRemote() ? 11 : 3);
	}	
	
	@Override
    public BlockFaceShape getBlockFaceShape(IWorldReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public int getLightValue(IBlockState state, IWorldReader world, BlockPos pos) {
		return (int)((state.get(VARIANT) == EnumLifeCrystalType.ORE ? 0.4F : 0.0F) * 15.0F);
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);

		if(state.get(VARIANT) == EnumLifeCrystalType.ORE) {
			BetweenlandsChunkStorage.markGem(worldIn, pos, ItemGemSinger.GemSingerTarget.LIFE_CRYSTAL);
		}
	}

	@Override
	public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
		super.onReplaced(state, worldIn, pos, newState, isMoving);

		if(state.get(VARIANT) == EnumLifeCrystalType.ORE) {
			BetweenlandsChunkStorage.unmarkGem(worldIn, pos, ItemGemSinger.GemSingerTarget.LIFE_CRYSTAL);
		}
	}
}
