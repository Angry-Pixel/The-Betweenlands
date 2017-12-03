package thebetweenlands.common.block.structure;

import java.util.Locale;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockMudTiles extends BasicBlock implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeBlockModelDefinition {

	public static final PropertyEnum<EnumMudTileType> VARIANT = PropertyEnum.<EnumMudTileType>create("variant", EnumMudTileType.class);
	
	public BlockMudTiles() {
		super(Material.ROCK);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumMudTileType.MUD_TILES));
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (tab == BLCreativeTabs.BLOCKS)
			for (EnumMudTileType type : EnumMudTileType.values())
				list.add(new ItemStack(this, 1, type.ordinal()));
	}

	@Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumMudTileType)state.getValue(VARIANT)).getMetadata();
	}

	public static enum EnumMudTileType implements IStringSerializable {
		MUD_TILES,
		MUD_TILES_DECAY_1,
		MUD_TILES_DECAY_2,
		MUD_TILES_DECAY_3,
		MUD_TILES_DECAY_4,
		MUD_TILES_CRACKED,
		MUD_TILES_CRACKED_DECAY_1,
		MUD_TILES_CRACKED_DECAY_2,
		MUD_TILES_CRACKED_DECAY_3,
		MUD_TILES_CRACKED_DECAY_4;

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