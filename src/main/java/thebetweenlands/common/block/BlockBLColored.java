package thebetweenlands.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.EnumBLDyeColor;

public class BlockBLColored extends Block {
	public static final PropertyEnum<EnumBLDyeColor> COLOR = PropertyEnum.<EnumBLDyeColor>create("color", EnumBLDyeColor.class);

	public BlockBLColored(Material materialIn, SoundType soundType) {
		super(materialIn);
		this.setSoundType(soundType);
		this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, EnumBLDyeColor.PEWTER_GREY));
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumBLDyeColor) state.getValue(COLOR)).getMetadata();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (EnumBLDyeColor enumdyecolor : EnumBLDyeColor.values())
			items.add(new ItemStack(this, 1, enumdyecolor.getMetadata()));
	}

	// @Override
	// public MapColor getMapColor(IBlockState state, IBlockAccess worldIn,
	// BlockPos pos)
	// {
	// return MapColor.getBlockColor((EnumBLDyeColor)state.getValue(COLOR));
	// }

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(COLOR, EnumBLDyeColor.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumBLDyeColor) state.getValue(COLOR)).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { COLOR });
	}
}