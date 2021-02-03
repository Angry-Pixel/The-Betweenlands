package thebetweenlands.common.block.terrain;

import java.util.Locale;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;

public class BlockBetweenstonePebblePile extends BasicBlock implements ICustomItemBlock, ISubtypeItemBlockModelDefinition {

	public static final PropertyEnum<EnumPileType> STONE_COUNT = PropertyEnum.<EnumPileType>create("stone_count", EnumPileType.class);
	
	public BlockBetweenstonePebblePile() {
		super(Material.ROCK);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumPileType.ONE.getMetadata()));
		list.add(new ItemStack(this, 1, EnumPileType.TWO.getMetadata()));
		list.add(new ItemStack(this, 1, EnumPileType.THREE.getMetadata()));
		list.add(new ItemStack(this, 1, EnumPileType.FOUR.getMetadata()));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;

		if (!world.isRemote) {
			if (!player.getHeldItem(hand).isEmpty() && player.getHeldItem(hand).getItem() == EnumItemMisc.BETWEENSTONE_PEBBLE.getItem() && player.getHeldItem(hand).getItemDamage() == EnumItemMisc.BETWEENSTONE_PEBBLE.getID()) {
				if(state.getValue(STONE_COUNT) == EnumPileType.FOUR)
					return false;
				ItemStack stack = player.getHeldItem(hand).splitStack(1);
				if (!stack.isEmpty()) {
					IBlockState stateNew = state.cycleProperty(STONE_COUNT);
					world.setBlockState(pos, stateNew);
					return true;
				}
			} else if (player.getHeldItem(hand).isEmpty()) {
				ItemStack extracted = EnumItemMisc.BETWEENSTONE_PEBBLE.create(1);
				EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
				item.motionX = item.motionY = item.motionZ = 0D;
				world.spawnEntity(item);
				if(state.getValue(STONE_COUNT) == EnumPileType.FOUR)
					world.setBlockState(pos, state.withProperty(STONE_COUNT, EnumPileType.THREE));
				if(state.getValue(STONE_COUNT) == EnumPileType.THREE)
					world.setBlockState(pos, state.withProperty(STONE_COUNT, EnumPileType.TWO));
				if(state.getValue(STONE_COUNT) == EnumPileType.TWO)
					world.setBlockState(pos, state.withProperty(STONE_COUNT, EnumPileType.ONE));
				if(state.getValue(STONE_COUNT) == EnumPileType.ONE)
					world.setBlockToAir(pos);
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, ((EnumPileType)state.getValue(STONE_COUNT)).getMetadata());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(STONE_COUNT, EnumPileType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumPileType)state.getValue(STONE_COUNT)).getMetadata();
	}

	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { STONE_COUNT });
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumPileType)state.getValue(STONE_COUNT)).getMetadata();
	}
	
	public static enum EnumPileType implements IStringSerializable {
		ONE,
		TWO,
		THREE,
		FOUR;

		private final String name;

		private EnumPileType() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumPileType byMetadata(int metadata) {
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
		return ItemBlockEnum.create(this, EnumPileType.class);
	}

	@Override
	public int getSubtypeNumber() {
		return EnumPileType.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + EnumPileType.values()[meta].getName();
	}

}
