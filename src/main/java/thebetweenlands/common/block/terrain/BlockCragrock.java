package thebetweenlands.common.block.terrain;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockCragrock extends BasicBlock implements BlockRegistry.ICustomItemBlock, BlockRegistry.ISubtypeItemBlockModelDefinition {
	public static final PropertyEnum<EnumCragrockType> VARIANT = PropertyEnum.<EnumCragrockType>create("variant", EnumCragrockType.class);
	public static final PropertyBool IS_BOTTOM = PropertyBool.create("is_bottom");

	public BlockCragrock(Material materialIn) {
		super(materialIn);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumCragrockType.DEFAULT));
		this.setTickRandomly(true);
		this.setHardness(1.5F);
		this.setResistance(10.0F);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random){
		if (!world.isRemote && state.getValue(VARIANT) != EnumCragrockType.DEFAULT) {
			BlockPos newPos = pos.add(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1);
			if(newPos.getY() >= 0 && newPos.getY() < 256 && world.isBlockLoaded(newPos)) {
				IBlockState blockState = world.getBlockState(newPos);
				Block block = world.getBlockState(newPos).getBlock();
				if (block == this && blockState.getValue(VARIANT) == EnumCragrockType.DEFAULT) {
					if (world.getBlockState(newPos.up()).getBlock() == this 
							&& world.getBlockState(newPos.up(2)).getBlock() == Blocks.AIR 
							&& blockState.getValue(VARIANT) != EnumCragrockType.MOSSY_2) {
						world.setBlockState(newPos, state.withProperty(VARIANT, EnumCragrockType.MOSSY_2));
					} else if (world.getBlockState(newPos).getBlock() == this 
							&& world.getBlockState(newPos.up()).getBlock() == Blocks.AIR) {
						world.setBlockState(newPos, state.withProperty(VARIANT, EnumCragrockType.MOSSY_1), 2);
					}
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumCragrockType.DEFAULT.getMetadata()));
		list.add(new ItemStack(this, 1, EnumCragrockType.MOSSY_1.getMetadata()));
		list.add(new ItemStack(this, 1, EnumCragrockType.MOSSY_2.getMetadata()));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, ((EnumCragrockType)state.getValue(VARIANT)).getMetadata());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumCragrockType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumCragrockType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		IBlockState stateBelow = worldIn.getBlockState(pos.down());
		return state.withProperty(IS_BOTTOM, stateBelow.getBlock() != this || stateBelow.getValue(VARIANT) != state.getValue(VARIANT));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT, IS_BOTTOM });
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumCragrockType)state.getValue(VARIANT)).getMetadata();
	}

	public static enum EnumCragrockType implements IStringSerializable {
		DEFAULT,
		MOSSY_1,
		MOSSY_2;

		private final String name;

		private EnumCragrockType() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumCragrockType byMetadata(int metadata) {
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
		return ItemBlockEnum.create(this, EnumCragrockType.class);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumCragrockType.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + EnumCragrockType.values()[meta].getName();
	}
	
	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		if(super.canSustainPlant(state, world, pos, direction, plantable)) {
			return true;
		}

		if(state.getValue(VARIANT) != EnumCragrockType.DEFAULT) {
			EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));
	
			switch(plantType) {
			case Beach:
				boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
				world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
				world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
				world.getBlockState(pos.south()).getMaterial() == Material.WATER);
				return hasWater;
			case Plains:
				return true;
			default:
				return false;
			}
		}
		return false;
	}
}
