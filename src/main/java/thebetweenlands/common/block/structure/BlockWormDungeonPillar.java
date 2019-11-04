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

public class BlockWormDungeonPillar extends BasicBlock implements ICustomItemBlock, ISubtypeItemBlockModelDefinition {

	public static final PropertyEnum<EnumWormPillarType> VARIANT = PropertyEnum.<EnumWormPillarType>create("variant", EnumWormPillarType.class);
	
	public BlockWormDungeonPillar() {
		super(Material.ROCK);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumWormPillarType.WORM_PILLAR_VERTICAL));
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setDefaultCreativeTab();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (EnumWormPillarType type : EnumWormPillarType.values())
			list.add(new ItemStack(this, 1, type.ordinal()));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, ((EnumWormPillarType)state.getValue(VARIANT)).getMetadata());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumWormPillarType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumWormPillarType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumWormPillarType)state.getValue(VARIANT)).getMetadata();
	}

	public static enum EnumWormPillarType implements IStringSerializable {
		WORM_PILLAR_VERTICAL,
		WORM_PILLAR_VERTICAL_DECAY_1,
		WORM_PILLAR_VERTICAL_DECAY_2,
		WORM_PILLAR_VERTICAL_DECAY_3,
		WORM_PILLAR_VERTICAL_DECAY_4,
		WORM_PILLAR_VERTICAL_DECAY_FULL,
		WORM_PILLAR_TOP,
		WORM_PILLAR_TOP_DECAY_1,
		WORM_PILLAR_TOP_DECAY_2,
		WORM_PILLAR_TOP_DECAY_3,
		WORM_PILLAR_TOP_DECAY_4,
		WORM_PILLAR_TOP_DECAY_FULL;

		private final String name;

		private EnumWormPillarType() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumWormPillarType byMetadata(int metadata) {
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
		return ItemBlockEnum.create(this, EnumWormPillarType.class);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumWormPillarType.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return EnumWormPillarType.values()[meta].getName();
	}
}