package thebetweenlands.common.block.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.BlockLog;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;

public class BlockPortalFrame extends BasicBlock implements ICustomItemBlock, ISubtypeItemBlockModelDefinition {
	public static final PropertyEnum<EnumPortalFrame> FRAME_POSITION = PropertyEnum.create("frame_position", EnumPortalFrame.class);
	public static final PropertyBool X_AXIS = PropertyBool.create("x_axis");

	public BlockPortalFrame() {
		super(Material.WOOD);
		setHardness(2.0F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().withProperty(FRAME_POSITION, EnumPortalFrame.CORNER_TOP_LEFT).withProperty(X_AXIS, false));
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		IBlockState dropBlock = BlockRegistry.LOG_PORTAL.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.NONE);
		drops.add(new ItemStack(Item.getItemFromBlock(dropBlock.getBlock()), 1, dropBlock.getBlock().getMetaFromState(dropBlock)));
		return drops;
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (EnumPortalFrame type : EnumPortalFrame.values())
			list.add(new ItemStack(this, 1, type.ordinal()));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FRAME_POSITION, X_AXIS);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FRAME_POSITION, EnumPortalFrame.values()[meta > 7 ? meta - 8 : meta]).withProperty(X_AXIS, meta > 7);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		EnumPortalFrame type = state.getValue(FRAME_POSITION);
		return type.ordinal() + (state.getValue(X_AXIS) ? 8 : 0);
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return super.getSilkTouchDrop(this.getDefaultState().withProperty(FRAME_POSITION, state.getValue(FRAME_POSITION))); //Remove facing
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return ItemBlockEnum.create(this, EnumPortalFrame.class);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if (placer.getHorizontalFacing().getAxis() == EnumFacing.Axis.X)
			worldIn.setBlockState(pos, state.withProperty(X_AXIS, true));
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 0;
    }
	
	public enum EnumPortalFrame implements IStringSerializable {
		CORNER_TOP_LEFT,
		TOP,
		CORNER_TOP_RIGHT,
		SIDE_RIGHT,
		SIDE_LEFT,
		CORNER_BOTTOM_LEFT,
		BOTTOM,
		CORNER_BOTTOM_RIGHT;

		private final String name;

		private EnumPortalFrame() {
		    this.name = this.name().toLowerCase(Locale.ENGLISH);
        }

		@Override
		public String getName() {
			return name;
		}
	}

	@Override
	public int getSubtypeNumber() {
		return EnumPortalFrame.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + EnumPortalFrame.values()[meta].getName();
	}
}
