package thebetweenlands.common.block.terrain;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;

public class BlockBetweenstonePebblePile extends BasicBlock implements ICustomItemBlock, ISubtypeItemBlockModelDefinition {

	public static final PropertyEnum<EnumPileType> PILE_TYPE = PropertyEnum.<EnumPileType>create("pile_type", EnumPileType.class);

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0D, 0.0D, 0D, 1D, 0.5D, 1D);
	public BlockBetweenstonePebblePile() {
		super(Material.ROCK);
		this.setDefaultState(this.getDefaultState().withProperty(PILE_TYPE, EnumPileType.ONE));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
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
    public Block.EnumOffsetType getOffsetType() {
        return Block.EnumOffsetType.XZ;
    }

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;

		if (!world.isRemote) {
			if (!player.getHeldItem(hand).isEmpty() && player.getHeldItem(hand).getItem() == EnumItemMisc.BETWEENSTONE_PEBBLE.getItem() && player.getHeldItem(hand).getItemDamage() == EnumItemMisc.BETWEENSTONE_PEBBLE.getID()) {
				if(state.getValue(PILE_TYPE) == EnumPileType.FOUR || state.getValue(PILE_TYPE) == EnumPileType.FOUR_WATER || state.getValue(PILE_TYPE) == EnumPileType.FOUR_PLANT || state.getValue(PILE_TYPE) == EnumPileType.FOUR_PLANT_WATER)
					return false;
				ItemStack stack = player.getHeldItem(hand).splitStack(1);
				if (!stack.isEmpty()) {
					IBlockState stateNew = state.cycleProperty(PILE_TYPE);
					world.setBlockState(pos, stateNew);
					return true;
				}
			} else if (player.getHeldItem(hand).isEmpty()) {
				ItemStack extracted = EnumItemMisc.BETWEENSTONE_PEBBLE.create(1);
				EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
				item.motionX = item.motionY = item.motionZ = 0D;
				world.spawnEntity(item);
				if(state.getValue(PILE_TYPE) == EnumPileType.FOUR)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.THREE));
				if(state.getValue(PILE_TYPE) == EnumPileType.THREE)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.TWO));
				if(state.getValue(PILE_TYPE) == EnumPileType.TWO)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.ONE));
				if(state.getValue(PILE_TYPE) == EnumPileType.ONE)
					world.setBlockToAir(pos);
				
				if(state.getValue(PILE_TYPE) == EnumPileType.FOUR_WATER)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.THREE_WATER));
				if(state.getValue(PILE_TYPE) == EnumPileType.THREE_WATER)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.TWO_WATER));
				if(state.getValue(PILE_TYPE) == EnumPileType.TWO_WATER)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.ONE_WATER));
				if(state.getValue(PILE_TYPE) == EnumPileType.ONE_WATER)
					world.setBlockToAir(pos);
				
				if(state.getValue(PILE_TYPE) == EnumPileType.FOUR_PLANT)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.THREE_PLANT));
				if(state.getValue(PILE_TYPE) == EnumPileType.THREE_PLANT)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.TWO_PLANT));
				if(state.getValue(PILE_TYPE) == EnumPileType.TWO_PLANT)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.ONE_PLANT));
				if(state.getValue(PILE_TYPE) == EnumPileType.ONE_PLANT)
					world.setBlockToAir(pos);
				
				if(state.getValue(PILE_TYPE) == EnumPileType.FOUR_PLANT_WATER)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.THREE_PLANT_WATER));
				if(state.getValue(PILE_TYPE) == EnumPileType.THREE_PLANT_WATER)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.TWO_PLANT_WATER));
				if(state.getValue(PILE_TYPE) == EnumPileType.TWO_PLANT_WATER)
					world.setBlockState(pos, state.withProperty(PILE_TYPE, EnumPileType.ONE_PLANT_WATER));
				if(state.getValue(PILE_TYPE) == EnumPileType.ONE_PLANT_WATER)
					world.setBlockToAir(pos);
				return true;
			}
		}
		return false;
	}

	@Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    	boolean inWater = world.getBlockState(pos).getMaterial() == Material.WATER;
		if (!(world.getBlockState(pos).getBlock() instanceof BlockBetweenstonePebblePile)) {
			if (!inWater) {
				if (world.rand.nextBoolean())
					return getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer) .withProperty(PILE_TYPE, EnumPileType.ONE_PLANT);
				else
					return getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer) .withProperty(PILE_TYPE, EnumPileType.ONE);
			}
			else {
				if (world.rand.nextBoolean())
					return getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer) .withProperty(PILE_TYPE, EnumPileType.ONE_PLANT_WATER);
				else
					return getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer) .withProperty(PILE_TYPE, EnumPileType.ONE_WATER);
			}
		}
    	return getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, ((EnumPileType)state.getValue(PILE_TYPE)).getMetadata());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(PILE_TYPE, EnumPileType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumPileType)state.getValue(PILE_TYPE)).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { PILE_TYPE });
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumPileType)state.getValue(PILE_TYPE)).getMetadata();
	}

	public static enum EnumPileType implements IStringSerializable {
		ONE,
		TWO,
		THREE,
		FOUR,
		ONE_WATER,
		TWO_WATER,
		THREE_WATER,
		FOUR_WATER,
		ONE_PLANT,
		TWO_PLANT,
		THREE_PLANT,
		FOUR_PLANT,
		ONE_PLANT_WATER,
		TWO_PLANT_WATER,
		THREE_PLANT_WATER,
		FOUR_PLANT_WATER;

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
