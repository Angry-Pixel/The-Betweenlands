package thebetweenlands.common.block.plant;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;

public class BlockMouldHornMushroom extends Block implements ICustomItemBlock, ISubtypeItemBlockModelDefinition {
	public static final PropertyEnum<EnumMouldHorn> MOULD_HORN_TYPE = PropertyEnum.<EnumMouldHorn>create("type", EnumMouldHorn.class);

	public BlockMouldHornMushroom() {
		super(Material.WOOD);
		setSoundType(SoundType.CLOTH);
		setTickRandomly(true);
		setHardness(0.2F);
		setDefaultState(blockState.getBaseState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_MYCELIUM));
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos) == this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_MYCELIUM);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_MYCELIUM.ordinal()));
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_STALK_THIN.ordinal()));
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_CAP_THIN.ordinal()));
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_STALK_FULL.ordinal()));
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_CAP_FULL.ordinal()));
		list.add(new ItemStack(this, 1, EnumMouldHorn.MOULD_HORN_CAP_FULL_WARTS.ordinal()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
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
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return NULL_AABB;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		float widthMin = 0, heightMin = 0, depthMin = 0;
		float widthMax = 0, heightMax = 0, depthMax = 0;
		switch (state.getValue(MOULD_HORN_TYPE)) {
		case MOULD_HORN_MYCELIUM:
			widthMin = 0F;
			heightMin = 0F;
			depthMin = 0F;
			widthMax = 1F;
			heightMax = 0.5F;
			depthMax = 1F;
			break;
		case MOULD_HORN_STALK_THIN:
		case MOULD_HORN_CAP_THIN:
			widthMin = 0.25F;
			heightMin = 0F;
			depthMin = 0.25F;
			widthMax = 0.75F;
			heightMax = 1F;
			depthMax = 0.75F;
			break;
		case MOULD_HORN_STALK_FULL:
		case MOULD_HORN_CAP_FULL:
		case MOULD_HORN_CAP_FULL_WARTS:
			widthMin = 0F;
			heightMin = 0F;
			depthMin = 0F;
			widthMax = 1F;
			heightMax = 1F;
			depthMax = 1F;
			break;
		}
		return new AxisAlignedBB(widthMin, heightMin, depthMin, widthMax, heightMax, depthMax);
	}
	
	@Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.byMetadata(meta));
    }

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(new IProperty[] { MOULD_HORN_TYPE }).build();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.byMetadata(meta));
	}

	private boolean checkForAdjacentMyceliumBlock(World world, BlockPos pos, IBlockState state) {
		for(EnumFacing facing : EnumFacing.values()) {
			if(facing != EnumFacing.UP || facing != EnumFacing.DOWN)
				if (isMyceliumBlockAdjacent(world, pos, facing))
					return true;
		}
		return false;
	}

	private boolean isMyceliumBlockAdjacent(World world, BlockPos pos, EnumFacing facing) {
        BlockPos blockposAdj = pos.offset(facing.getOpposite());
        IBlockState state = world.getBlockState(blockposAdj);
        IBlockState mycelium = this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_MYCELIUM);
        return state == mycelium;
	}

	private boolean checkForAdjacentThinMushroomBlock(World world, BlockPos pos, IBlockState state) {
		for(EnumFacing facing : EnumFacing.values()) {
			if(facing != EnumFacing.UP || facing != EnumFacing.DOWN)
				if (isThinMushroomBlockAdjacent(world, pos, facing))
					return true;
		}
		return false;
	}

	private boolean isThinMushroomBlockAdjacent(World world, BlockPos pos, EnumFacing facing) {
        BlockPos blockposAdj = pos.offset(facing.getOpposite());
        IBlockState state = world.getBlockState(blockposAdj);
        IBlockState smallStalk = this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_THIN);
        IBlockState smallCap = this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_CAP_THIN);
        return state == smallStalk || state == smallCap;
	}

	private boolean checkForAdjacentFullMushroomBlock(World world, BlockPos pos, IBlockState state) {
		for(EnumFacing facing : EnumFacing.values()) {
			if(facing != EnumFacing.UP || facing != EnumFacing.DOWN)
				if (isFullMushroomBlockAdjacent(world, pos, facing))
					return true;
		}
		return false;
	}

	private boolean isFullMushroomBlockAdjacent(World world, BlockPos pos, EnumFacing facing) {
        BlockPos blockposAdj = pos.offset(facing.getOpposite());
        IBlockState state = world.getBlockState(blockposAdj);
        IBlockState fullStalk = this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_FULL);
        IBlockState fullCap = this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_CAP_FULL);
        return state == fullCap || state == fullStalk;
	}

	@Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
	/*	if (world.isRemote)
			return;
			if (!checkForAdjacentBigBlocks(world, pos, state)) {
				randomTick(world, pos, state, random);
				world.scheduleUpdate(pos, this, 100);
		}*/
	}

	@Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (world.isRemote)
			return;
		//if(world.rand.nextInt(4) == 0) {
			EnumMouldHorn stage = state.getValue(MOULD_HORN_TYPE);
	//BASIC GROWTH TICK TESTING - can make more varied later
			switch (stage) {
			case MOULD_HORN_MYCELIUM:
				if(!checkForAdjacentFullMushroomBlock(world, pos, state) && !checkForAdjacentThinMushroomBlock(world, pos, state))
					world.setBlockState(pos, getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_CAP_THIN), 2);
				break;
			case MOULD_HORN_CAP_THIN:
				if(world.getBlockState(pos.down()) != this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_THIN) && world.isAirBlock(pos.up())) {
					world.setBlockState(pos, getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_THIN), 2);
					world.setBlockState(pos.up(), getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_CAP_THIN), 2);
				}
				if(!checkForAdjacentFullMushroomBlock(world, pos, state))
					if(world.getBlockState(pos.down()) == this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_THIN))
						world.setBlockState(pos, getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_CAP_FULL), 2);
				break;
			case MOULD_HORN_STALK_THIN:
				if(!checkForAdjacentFullMushroomBlock(world, pos, state))
					if(world.getBlockState(pos.up()) == this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_CAP_FULL))
						world.setBlockState(pos, getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_FULL), 2);
				break;
			case MOULD_HORN_CAP_FULL: 
				if(world.getBlockState(pos.down()) == this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_THIN)) {
					world.setBlockState(pos.down(), getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_FULL), 2);
				}// test size atm
				else if(world.getBlockState(pos.down(3)) != this.getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_FULL) && world.isAirBlock(pos.up())) {
					world.setBlockState(pos, getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_STALK_FULL), 2);
					if(random.nextBoolean())
						world.setBlockState(pos.up(), getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_CAP_FULL), 2);
					else
						world.setBlockState(pos.up(), getDefaultState().withProperty(MOULD_HORN_TYPE, EnumMouldHorn.MOULD_HORN_CAP_FULL_WARTS), 2);
				}
				break;
			default:
				break;
			}
		//}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumMouldHorn)state.getValue(MOULD_HORN_TYPE)).getMetadata();
	}

	public static enum EnumMouldHorn implements IStringSerializable {
		MOULD_HORN_MYCELIUM,
		MOULD_HORN_STALK_THIN,
		MOULD_HORN_CAP_THIN,
		MOULD_HORN_STALK_FULL,
		MOULD_HORN_CAP_FULL,
		MOULD_HORN_CAP_FULL_WARTS;

		private final String name;

		private EnumMouldHorn() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumMouldHorn byMetadata(int metadata) {
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
		return ItemBlockEnum.create(this, EnumMouldHorn.class);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumMouldHorn.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return EnumMouldHorn.values()[meta].getName();
	}
}
