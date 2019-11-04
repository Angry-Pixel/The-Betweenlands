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
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;

public class BlockCarvedMudBrick extends BasicBlock implements ICustomItemBlock, ISubtypeItemBlockModelDefinition {

	public static final PropertyEnum<EnumCarvedMudBrickType> VARIANT = PropertyEnum.<EnumCarvedMudBrickType>create("variant", EnumCarvedMudBrickType.class);
	
	public BlockCarvedMudBrick() {
		super(Material.ROCK);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumCarvedMudBrickType.MUD_BRICKS_CARVED));
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (EnumCarvedMudBrickType type : EnumCarvedMudBrickType.values())
			list.add(new ItemStack(this, 1, type.ordinal()));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, ((EnumCarvedMudBrickType)state.getValue(VARIANT)).getMetadata());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumCarvedMudBrickType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumCarvedMudBrickType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumCarvedMudBrickType)state.getValue(VARIANT)).getMetadata();
	}

	public static enum EnumCarvedMudBrickType implements IStringSerializable {
		MUD_BRICKS_CARVED,
		MUD_BRICKS_CARVED_DECAY_1,
		MUD_BRICKS_CARVED_DECAY_2,
		MUD_BRICKS_CARVED_DECAY_3,
		MUD_BRICKS_CARVED_DECAY_4,
		MUD_BRICKS_CARVED_EDGE,
		MUD_BRICKS_CARVED_EDGE_DECAY_1,
		MUD_BRICKS_CARVED_EDGE_DECAY_2,
		MUD_BRICKS_CARVED_EDGE_DECAY_3,
		MUD_BRICKS_CARVED_EDGE_DECAY_4,
		MUD_BRICKS_DECAY_1,
		MUD_BRICKS_DECAY_2,
		MUD_BRICKS_DECAY_3,
		MUD_BRICKS_DECAY_4;

		private final String name;

		private EnumCarvedMudBrickType() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumCarvedMudBrickType byMetadata(int metadata) {
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
		return ItemBlockEnum.create(this, EnumCarvedMudBrickType.class);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumCarvedMudBrickType.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return EnumCarvedMudBrickType.values()[meta].getName();
	}
}