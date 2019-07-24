package thebetweenlands.common.block.structure;

import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;

public class BlockMudTiles extends Block implements ICustomItemBlock, ISubtypeItemBlockModelDefinition {

	public static final PropertyEnum<EnumMudTileType> VARIANT = PropertyEnum.<EnumMudTileType>create("variant", EnumMudTileType.class);
    public static final PropertyBool CONNECTED_DOWN = PropertyBool.create("connected_down");
    public static final PropertyBool CONNECTED_UP = PropertyBool.create("connected_up");
    public static final PropertyBool CONNECTED_NORTH = PropertyBool.create("connected_north");
    public static final PropertyBool CONNECTED_SOUTH = PropertyBool.create("connected_south");
    public static final PropertyBool CONNECTED_WEST = PropertyBool.create("connected_west");
    public static final PropertyBool CONNECTED_EAST = PropertyBool.create("connected_east");

	public BlockMudTiles() {
		super(Material.ROCK);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumMudTileType.MUD_TILES).withProperty(CONNECTED_DOWN, Boolean.FALSE).withProperty(CONNECTED_EAST, Boolean.FALSE).withProperty(CONNECTED_NORTH, Boolean.FALSE).withProperty(CONNECTED_SOUTH, Boolean.FALSE).withProperty(CONNECTED_UP, Boolean.FALSE).withProperty(CONNECTED_WEST, Boolean.FALSE));
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (EnumMudTileType type : EnumMudTileType.values())
			list.add(new ItemStack(this, 1, type.ordinal()));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, ((EnumMudTileType)state.getValue(VARIANT)).getMetadata());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumMudTileType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumMudTileType)state.getValue(VARIANT)).getMetadata();
	}

    @Override
    public IBlockState getActualState (IBlockState state, IBlockAccess world, BlockPos position) {
        return state.withProperty(CONNECTED_DOWN, this.isSideConnectable(world, position, EnumFacing.DOWN)).withProperty(CONNECTED_EAST, this.isSideConnectable(world, position, EnumFacing.EAST)).withProperty(CONNECTED_NORTH, this.isSideConnectable(world, position, EnumFacing.NORTH)).withProperty(CONNECTED_SOUTH, this.isSideConnectable(world, position, EnumFacing.SOUTH)).withProperty(CONNECTED_UP, this.isSideConnectable(world, position, EnumFacing.UP)).withProperty(CONNECTED_WEST, this.isSideConnectable(world, position, EnumFacing.WEST));
    }

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {VARIANT, CONNECTED_DOWN, CONNECTED_UP, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST});
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumMudTileType)state.getValue(VARIANT)).getMetadata();
	}

    private boolean isSideConnectable (IBlockAccess world, BlockPos pos, EnumFacing side) {
    	final IBlockState state = world.getBlockState(pos);
    	final IBlockState stateConnection = world.getBlockState(pos.offset(side));
    	if(stateConnection.getBlock() == this && state.getBlock() == this) {
    		if(state.getValue(VARIANT) == EnumMudTileType.MUD_TILES || state.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED)
    			if(stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES_DECAY || stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED_DECAY || stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES || stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED)
    				return false;
    		if(state.getValue(VARIANT) == EnumMudTileType.MUD_TILES_DECAY || state.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED_DECAY)
    			if(stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES || stateConnection.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED)
    				return false; 
    	}
        return (stateConnection == null) ? false : stateConnection.getBlock() == this;
    }

	@Override
    public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity entity) {
		if(state.getValue(VARIANT) == EnumMudTileType.MUD_TILES_DECAY || state.getValue(VARIANT) == EnumMudTileType.MUD_TILES_CRACKED_DECAY)
			return 0.98F;
		return 0.6F;
    }

	public static enum EnumMudTileType implements IStringSerializable {
		MUD_TILES,
		MUD_TILES_DECAY,
		MUD_TILES_CRACKED,
		MUD_TILES_CRACKED_DECAY;

		private final String name;

		private EnumMudTileType() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumMudTileType byMetadata(int metadata) {
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
		return ItemBlockEnum.create(this, EnumMudTileType.class);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumMudTileType.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return EnumMudTileType.values()[meta].getName();
	}

}