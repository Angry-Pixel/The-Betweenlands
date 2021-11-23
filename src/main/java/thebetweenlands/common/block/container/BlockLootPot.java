package thebetweenlands.common.block.container;

import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.wrapper.InvWrapper;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.entity.mobs.EntityBubblerCrab;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.item.ItemBlockEnum.IGenericMetaSelector;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;
import thebetweenlands.common.tile.TileEntityLootInventory;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockLootPot extends BasicBlock implements ITileEntityProvider, ICustomItemBlock, ISubtypeItemBlockModelDefinition, IStateMappedBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyEnum<EnumLootPot> VARIANT = PropertyEnum.create("type", EnumLootPot.class);

	public BlockLootPot() {
		this(Material.GLASS);
	}

	public BlockLootPot(Material material) {
		super(material);
		setHardness(0.4f);
		setSoundType(SoundType.GLASS);
		setHarvestLevel("pickaxe", 0);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(VARIANT, EnumLootPot.POT_1));
	}

	@Nullable
	public static TileEntityLootPot getTileEntity(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityLootPot) {
			return (TileEntityLootPot) tile;
		}
		return null;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityLootPot();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, EnumLootPot.POT_1.getMetadata(EnumFacing.SOUTH)));
		list.add(new ItemStack(this, 1, EnumLootPot.POT_2.getMetadata(EnumFacing.SOUTH)));
		list.add(new ItemStack(this, 1, EnumLootPot.POT_3.getMetadata(EnumFacing.SOUTH)));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, ((EnumLootPot) state.getValue(VARIANT)).getMetadata(EnumFacing.NORTH));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumLootPot.byMetadata(meta)).withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumLootPot) state.getValue(VARIANT)).getMetadata(state.getValue(FACING));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{VARIANT, FACING});
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int rotation = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		state = state.withProperty(FACING, EnumFacing.byHorizontalIndex(rotation));
		state = state.withProperty(VARIANT, EnumLootPot.byMetadata(stack.getItemDamage()));
		worldIn.setBlockState(pos, state, 3);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileEntityLootPot) {
			((TileEntityLootPot) tile).setModelRotationOffset(worldIn.rand.nextInt(41) - 20);
			tile.markDirty();
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote) {
			if (worldIn.getTileEntity(pos) instanceof TileEntityLootPot) {
				TileEntityLootPot tile = (TileEntityLootPot) worldIn.getTileEntity(pos);
				InvWrapper wrapper = new InvWrapper(tile);
				if (!playerIn.getHeldItem(hand).isEmpty()) {
					ItemStack stack = playerIn.getHeldItem(hand);
					ItemStack prevStack = stack.copy();
					for(int i = 0; i < wrapper.getSlots() && !stack.isEmpty(); i++) {
						stack = wrapper.insertItem(i, stack, false);
					}
					if(stack.isEmpty() || stack.getCount() != prevStack.getCount()) {
						if(!playerIn.isCreative()) {
							playerIn.setHeldItem(hand, stack);
						}
						return true;
					}
				} else if(playerIn.isSneaking() && hand == EnumHand.MAIN_HAND) {
					for(int i = 0; i < wrapper.getSlots(); i++) {
						ItemStack extracted = wrapper.extractItem(i, 1, false);
						if(!extracted.isEmpty()) {
							EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
							item.motionX = item.motionY = item.motionZ = 0D;
							worldIn.spawnEntity(item);
							return true;
						}
					}
				}
			}
		} else {
			return true;
		}
		return false;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		IInventory tile = (IInventory) worldIn.getTileEntity(pos);
		if (tile != null) {
			((TileEntityLootInventory) tile).fillInventoryWithLoot(player);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		IInventory tile = (IInventory) worldIn.getTileEntity(pos);
		if (tile != null) {
			InventoryHelper.dropInventoryItems(worldIn, pos, tile);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void onPlayerDestroy(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			EntityLivingBase entity = null;
			if (world.rand.nextInt(3) == 0) {
				if (world.getBlockState(pos.up()).getMaterial() != Material.WATER) {
					entity = new EntityTermite(world);
					entity.getEntityAttribute(EntityTermite.SMALL).setBaseValue(1);
				} else {
					if (world.rand.nextBoolean())
						entity = new EntityBubblerCrab(world);
					else
						entity = new EntitySiltCrab(world);
				}
				if (entity != null) {
					entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
					world.spawnEntity(entity);
				}
			}
		}
		super.onPlayerDestroy(world, pos, state);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	public enum EnumLootPot implements IStringSerializable, IGenericMetaSelector {
		POT_1("1"),
		POT_2("2"),
		POT_3("3");

		private final String name;

		private EnumLootPot(String name) {
			this.name = name.toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata(EnumFacing facing) {
			return facing.getHorizontalIndex() | (this.ordinal() << 2);
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static EnumLootPot byMetadata(int metadata) {
			metadata >>= 2;
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public boolean isMetadataMatching(int meta) {
			return byMetadata(meta) == this;
		}
	}

	@Override
	public int getSubtypeNumber() {
		return EnumLootPot.values().length * 4;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + EnumLootPot.byMetadata(meta).getName();
	}

	@Override
	public ItemBlock getItemBlock() {
		return ItemBlockEnum.create(this, EnumLootPot.class);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(Builder builder) {
		builder.ignore(VARIANT).withPropertySuffix(VARIANT, e -> e.getName());
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}
}
