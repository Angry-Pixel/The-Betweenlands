package thebetweenlands.common.block.container;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.entity.mobs.EntityTermite;
import thebetweenlands.common.item.ItemBlockEnum;
import thebetweenlands.common.item.ItemBlockEnum.IGenericMetaSelector;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeBlock;
import thebetweenlands.common.tile.TileEntityLootInventory;
import thebetweenlands.common.tile.TileEntityLootPot;

public class BlockLootPot extends BasicBlock implements ITileEntityProvider, ICustomItemBlock, ISubtypeBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyEnum<EnumLootPot> VARIANT = PropertyEnum.create("type", EnumLootPot.class);

	public BlockLootPot() {
		super(Material.GLASS);
		setHardness(1.0f);
		setSoundType(SoundType.GLASS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(VARIANT, EnumLootPot.POT_1));
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityLootPot();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(itemIn, 1, EnumLootPot.POT_1.getMetadata(EnumFacing.NORTH)));
		list.add(new ItemStack(itemIn, 1, EnumLootPot.POT_2.getMetadata(EnumFacing.NORTH)));
		list.add(new ItemStack(itemIn, 1, EnumLootPot.POT_3.getMetadata(EnumFacing.NORTH)));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this, 1, ((EnumLootPot) state.getValue(VARIANT)).getMetadata(EnumFacing.NORTH));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumLootPot.byMetadata(meta)).withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
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
		if (worldIn.getTileEntity(pos) instanceof TileEntityLootPot) {
			TileEntityLootPot tile = (TileEntityLootPot) worldIn.getTileEntity(pos);
			if (tile != null && !worldIn.isRemote) {
				tile.setModelRotationOffset(worldIn.rand.nextInt(41) - 20);
				worldIn.notifyBlockUpdate(pos, state, state, 3);
			}
		}
		int rotation = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		state = state.withProperty(FACING, EnumFacing.getHorizontal(rotation));
		state = state.withProperty(VARIANT, EnumLootPot.byMetadata(stack.getItemDamage()));
		worldIn.setBlockState(pos, state, 3);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof TileEntityLootPot) {
			TileEntityLootPot tile = (TileEntityLootPot) worldIn.getTileEntity(pos);
			if (playerIn.getHeldItem(hand) != null) {
				ItemStack item = playerIn.getHeldItem(hand);
				ItemStack toAdd = item.copy();
				toAdd.stackSize = 1;
				for (int i = 0; i < 3; i++) {
					if (tile.getStackInSlot(i) == null) {
						if (!worldIn.isRemote) {
							tile.setInventorySlotContents(i, toAdd);
							if (!playerIn.capabilities.isCreativeMode)
								item.stackSize--;
							worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
						}
						return true;
					} else {
						ItemStack inSlot = tile.getStackInSlot(i);
						if (inSlot != null && inSlot.stackSize < inSlot.getMaxStackSize() && inSlot.getItemDamage() == toAdd.getItemDamage() && inSlot.getItem() == toAdd.getItem() && ItemStack.areItemStackTagsEqual(inSlot, toAdd)) {
							if (!worldIn.isRemote) {
								inSlot.stackSize++;
								if (!playerIn.capabilities.isCreativeMode)
									item.stackSize--;
								worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		IInventory tile = (IInventory) worldIn.getTileEntity(pos);
		if (tile != null)
			((TileEntityLootInventory) tile).fillWithLoot(player);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		IInventory tile = (IInventory) worldIn.getTileEntity(pos);
		if (tile != null) {
			for (int i = 0; i < tile.getSizeInventory(); i++) {
				ItemStack stack = tile.getStackInSlot(i);
				if (stack != null) {
					if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops")) {
						float f = 0.7F;
						double d0 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d1 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d2 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						EntityItem entityitem = new EntityItem(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
						entityitem.setPickupDelay(10);
						worldIn.spawnEntityInWorld(entityitem);
					}
				}
			}
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote)
			if (worldIn.rand.nextInt(3) == 0) {
				EntityTermite entity = new EntityTermite(worldIn);
				entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getZ(), pos.getZ() + 0.5D, 0.0F, 0.0F);
				worldIn.spawnEntityInWorld(entity);
			}
		super.onBlockDestroyedByPlayer(worldIn, pos, state);
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
		return EnumLootPot.values().length;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + EnumLootPot.byMetadata(meta).getName();
	}

	@Override
	public ItemBlock getItemBlock() {
		return ItemBlockEnum.create(this, EnumLootPot.class);
	}
}
