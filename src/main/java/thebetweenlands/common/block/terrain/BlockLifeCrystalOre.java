package thebetweenlands.common.block.terrain;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.models.block.LifeCrystalOreModel;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomModelSupplier;
import thebetweenlands.common.registries.BlockRegistry.IStateMapped;

public class BlockLifeCrystalOre extends BasicBlock implements BlockRegistry.IHasCustomItem, ICustomModelSupplier, IStateMapped {
	public static final PropertyEnum<EnumLifeCrystalType> VARIANT = PropertyEnum.<EnumLifeCrystalType>create("variant", EnumLifeCrystalType.class);

	public BlockLifeCrystalOre(Material materialIn) {
		super(materialIn);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumLifeCrystalType.DEFAULT));
		this.setTickRandomly(true);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumLifeCrystalType.DEFAULT.getMetadata()));
		list.add(new ItemStack(this, 1, EnumLifeCrystalType.ORE.getMetadata()));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, ((EnumLifeCrystalType)state.getValue(VARIANT)).getMetadata());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumLifeCrystalType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumLifeCrystalType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumLifeCrystalType)state.getValue(VARIANT)).getMetadata();
	}

	public static enum EnumLifeCrystalType implements IStringSerializable {
		DEFAULT(0, "default"),
		ORE(1, "ore");

		private static final EnumLifeCrystalType[] METADATA_LOOKUP = new EnumLifeCrystalType[values().length];
		private final int metadata;
		private final String name;

		private EnumLifeCrystalType(int metadataIn, String nameIn) {
			this.metadata = metadataIn;
			this.name = nameIn;
		}

		public int getMetadata() {
			return this.metadata;
		}

		public String toString() {
			return this.name;
		}

		public static EnumLifeCrystalType byMetadata(int metadata) {
			if (metadata < 0 || metadata >= METADATA_LOOKUP.length) {
				metadata = 0;
			}
			return METADATA_LOOKUP[metadata];
		}

		public String getName() {
			return this.name;
		}

		static {
			for (EnumLifeCrystalType type : values()) {
				METADATA_LOOKUP[type.getMetadata()] = type;
			}
		}
	}

	@Override
	public ItemBlock getItemBlock() {
		return ItemBlockEnum.create(this, EnumLifeCrystalType.class);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IModel getCustomModel() {
		return new LifeCrystalOreModel();
	}

	@Override
	public void setStateMapper() {
		ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(new IProperty[] { VARIANT }).build());		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
