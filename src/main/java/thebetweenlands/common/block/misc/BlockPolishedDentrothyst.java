package thebetweenlands.common.block.misc;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockPolishedDentrothyst extends BlockGlassBetweenlands implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeItemBlockModelDefinition, BlockRegistry.IStateMappedBlock {
	public static final PropertyEnum<EnumDentrothyst> TYPE = PropertyEnum.create("type", EnumDentrothyst.class);

	public BlockPolishedDentrothyst() {
		super(Material.GLASS);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TYPE, meta == 0 ? EnumDentrothyst.GREEN : EnumDentrothyst.ORANGE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).getMeta();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer(super.createBlockState(), new IProperty[]{ TYPE });
	}

	@Override
	public ItemBlock getItemBlock() {
		return ItemBlockEnum.create(this, EnumDentrothyst.class);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumDentrothyst.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + EnumDentrothyst.values()[meta].getName();
	}

	@Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }
	
	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, EnumDentrothyst.GREEN.getMeta()));
		items.add(new ItemStack(this, 1, EnumDentrothyst.ORANGE.getMeta()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(Builder builder) {
		builder.ignore(TYPE).withPropertySuffix(TYPE, type -> type == EnumDentrothyst.GREEN ? "green" : "orange");
	}
}
