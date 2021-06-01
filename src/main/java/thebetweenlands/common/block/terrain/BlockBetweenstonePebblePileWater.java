package thebetweenlands.common.block.terrain;

import java.util.Locale;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockBetweenstonePebblePileWater extends BlockSwampWater implements IStateMappedBlock, ICustomItemBlock {

	public static final PropertyEnum<EnumPileType> PILE_TYPE = PropertyEnum.<EnumPileType>create("pile_type", EnumPileType.class);
	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D);

	public BlockBetweenstonePebblePileWater() {
		this(FluidRegistry.SWAMP_WATER, Material.WATER);
		setHardness(0.5F);
	}

	public BlockBetweenstonePebblePileWater(Fluid fluid, Material materialIn) {
		super(fluid, materialIn);
		setHardness(0.5F);
		setResistance(2.0F);
		setSoundType(SoundType.STONE);
		setUnderwaterBlock(true);
		setDefaultState(getDefaultState().withProperty(PILE_TYPE, EnumPileType.ONE).withProperty(LEVEL, 0));
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(LEVEL);
	}

	@Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumPileType.ONE.getMetadata()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;

		if (!world.isRemote) {
			if (!player.getHeldItem(hand).isEmpty() && player.getHeldItem(hand).getItem() == EnumItemMisc.BETWEENSTONE_PEBBLE.getItem() && player.getHeldItem(hand).getItemDamage() == EnumItemMisc.BETWEENSTONE_PEBBLE.getID()) {
				if(state.getValue(PILE_TYPE) == EnumPileType.FOUR || state.getValue(PILE_TYPE) == EnumPileType.FOUR_PLANT)
					return false;
				ItemStack stack = player.getHeldItem(hand).splitStack(1);
				if (!stack.isEmpty()) {
					IBlockState stateNew = state.cycleProperty(PILE_TYPE);
					world.setBlockState(pos, stateNew);
					return true;
				}
			} else if (player.getHeldItem(hand).isEmpty()) {
				ItemStack extracted = EnumItemMisc.BETWEENSTONE_PEBBLE.create(1);
				EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
				item.motionX = item.motionY = item.motionZ = 0D;
				world.spawnEntity(item);

				if(state.getValue(PILE_TYPE) == EnumPileType.FOUR)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.THREE));
				if(state.getValue(PILE_TYPE) == EnumPileType.THREE)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.TWO));
				if(state.getValue(PILE_TYPE) == EnumPileType.TWO)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.ONE));
				if(state.getValue(PILE_TYPE) == EnumPileType.ONE)
					world.setBlockToAir(pos);

				if(state.getValue(PILE_TYPE) == EnumPileType.FOUR_PLANT)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.THREE_PLANT));
				if(state.getValue(PILE_TYPE) == EnumPileType.THREE_PLANT)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.TWO_PLANT));
				if(state.getValue(PILE_TYPE) == EnumPileType.TWO_PLANT)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.ONE_PLANT));
				if(state.getValue(PILE_TYPE) == EnumPileType.ONE_PLANT)
					world.setBlockToAir(pos);
				return true;
			}
		}
		return false;
	}

	@Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().isReplaceable(world, pos) && world.getBlockState(pos.down()).isFullBlock();
    }

	@Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		if (!(world.getBlockState(pos).getBlock() instanceof BlockBetweenstonePebblePileWater)) {
				if (world.rand.nextBoolean())
					return getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer) .withProperty(PILE_TYPE, EnumPileType.ONE_PLANT);
				else
					return getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer) .withProperty(PILE_TYPE, EnumPileType.ONE);
		}
    	return getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		int dropCount = 0;
		int meta = state.getValue(PILE_TYPE).getMetadata();
		if(meta <= 3)
			dropCount = meta;
		if(meta >= 4 && meta <= 7)
			dropCount = meta - 4;
		drops.add(EnumItemMisc.BETWEENSTONE_PEBBLE.create(1 + dropCount));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return EnumItemMisc.BETWEENSTONE_PEBBLE.create(1);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(PILE_TYPE, EnumPileType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumPileType)state.getValue(PILE_TYPE)).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(LEVEL).add(new IProperty[] { PILE_TYPE }).add(FLUID_RENDER_PROPS.toArray(new IUnlistedProperty<?>[0])).build();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumPileType)state.getValue(PILE_TYPE)).getMetadata();
	}

	public static enum EnumPileType implements IStringSerializable {
		ONE,
		TWO,
		THREE,
		FOUR,
		ONE_PLANT,
		TWO_PLANT,
		THREE_PLANT,
		FOUR_PLANT;

		private final String name;

		private EnumPileType() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumPileType byMetadata(int metadata) {
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}

		@Override
		public String getName() {
			return this.name;
		}
	}

	@Override
	public ItemBlock getItemBlock() {
		return null;
	}

}
