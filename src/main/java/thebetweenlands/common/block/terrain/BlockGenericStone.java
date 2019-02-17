package thebetweenlands.common.block.terrain;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockGenericStone extends Block implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeItemBlockModelDefinition{
	public static final EnumProperty<EnumStoneType> VARIANT = EnumProperty.<EnumStoneType>create("variant", EnumStoneType.class);

	public BlockGenericStone() {
		super(Material.ROCK);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setDefaultState(this.blockState.getBaseState().with(VARIANT, EnumStoneType.CORRUPT_BETWEENSTONE));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		for (EnumStoneType type : EnumStoneType.values())
			list.add(new ItemStack(this, 1, type.getMetadata()));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	protected ItemStack createStackedBlock(IBlockState state) {
		return new ItemStack(Item.getItemFromBlock(this), 1, ((EnumStoneType)state.get(VARIANT)).getMetadata());
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumStoneType)state.get(VARIANT)).getMetadata();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.get(VARIANT).getMetadata();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().with(VARIANT, EnumStoneType.byMetadata(meta));
	}

	@Override
	public ItemBlock getItemBlock() {
		return ItemBlockEnum.create(this, EnumStoneType.class);
	}

	public static enum EnumStoneType implements IStringSerializable {
		CORRUPT_BETWEENSTONE;

		private final String name;

		private EnumStoneType() {
			this.name = this.name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		public static EnumStoneType byMetadata(int metadata) {
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
	public int getSubtypeNumber() {
		return EnumStoneType.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return EnumStoneType.byMetadata(meta).getName();
	}
}
