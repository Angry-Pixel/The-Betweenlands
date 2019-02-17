package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockDentrothyst extends BasicBlock implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeItemBlockModelDefinition {
	public static final EnumProperty<EnumDentrothyst> TYPE = EnumProperty.create("type", EnumDentrothyst.class);

	public BlockDentrothyst(Material materialIn) {
		super(materialIn);
		setDefaultState(this.blockState.getBaseState().with(TYPE, EnumDentrothyst.GREEN));
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().with(TYPE, meta == 0 ? EnumDentrothyst.GREEN : EnumDentrothyst.ORANGE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.get(TYPE).meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
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
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumDentrothyst.GREEN.getMeta()));
		list.add(new ItemStack(this, 1, EnumDentrothyst.ORANGE.getMeta()));
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return state.get(TYPE) == EnumDentrothyst.ORANGE ? ItemRegistry.DENTROTHYST_SHARD_ORANGE : ItemRegistry.DENTROTHYST_SHARD_GREEN;
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		return 4;
	}

	public static enum EnumDentrothyst implements IStringSerializable {
		GREEN(0),
		ORANGE(1);

		int meta;

		EnumDentrothyst(int meta) {
			this.meta = meta;
		}

		@Override
		public String toString() {
			return this.getName();
		}

		public int getMeta() {
			return this.meta;
		}

		@Override
		public String getName() {
			return this == GREEN ? "green" : "orange";
		}
	}
}
