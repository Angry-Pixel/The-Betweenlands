package thebetweenlands.common.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.aspect.ItemAspectContainer;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityInfuser;

public class BlockInfuser extends BlockContainer {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockInfuser() {
		super(Material.IRON);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING});
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int rotation = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		state = state.withProperty(FACING, EnumFacing.getHorizontal(rotation));
		worldIn.setBlockState(pos, state, 3);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && world.getTileEntity(pos) instanceof TileEntityInfuser) {
			TileEntityInfuser tile = (TileEntityInfuser) world.getTileEntity(pos);

			final IFluidHandler fluidHandler = getFluidHandler(world, pos);
			if (fluidHandler != null && FluidUtil.interactWithFluidHandler(heldItem, fluidHandler, player)) {
				return true;
			}

			if (!player.isSneaking()) {
				if (tile != null && heldItem == null && tile.getStirProgress() >= 90) {
					tile.setStirProgress(0);
					return true;
				}
				if (heldItem != null && !tile.hasInfusion()) {
					ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(heldItem, AspectManager.get(world));
					if(aspectContainer.getAspects().size() > 0) {
						ItemStack ingredient = heldItem;
						for (int i = 0; i < TileEntityInfuser.MAX_INGREDIENTS; i++) {
							if(tile.getStackInSlot(i) == null) {
								ItemStack singleIngredient = ingredient.copy();
								singleIngredient.stackSize = 1;
								tile.setInventorySlotContents(i, singleIngredient);
								tile.updateInfusingRecipe();
								if (!player.capabilities.isCreativeMode) 
									heldItem.stackSize--;
								world.notifyBlockUpdate(pos, state, state, 2);
								return true;
							}
						}
					}
				}

				if(heldItem != null && heldItem.getItem() == ItemRegistry.LIFE_CRYSTAL) {
					if(tile.getStackInSlot(TileEntityInfuser.MAX_INGREDIENTS + 1) == null) {
						tile.setInventorySlotContents(TileEntityInfuser.MAX_INGREDIENTS + 1, heldItem);
						tile.updateInfusingRecipe();
						if (!player.capabilities.isCreativeMode) player.setHeldItem(hand, null);
					}
					return true;
				}
			}

			if(player.isSneaking() && !tile.hasInfusion()) {
				for (int i = TileEntityInfuser.MAX_INGREDIENTS; i >= 0; i--) {
					if(tile.getStackInSlot(i) != null) {
						EntityItem itemEntity = player.dropItem(tile.getStackInSlot(i).copy(), false);
						if(itemEntity != null) itemEntity.setPickupDelay(0);
						tile.setInventorySlotContents(i, null);
						tile.updateInfusingRecipe();
						world.notifyBlockUpdate(pos, state, state, 2);
						return true;
					}
				}
			}

			if(player.isSneaking()) {
				if(tile.getStackInSlot(TileEntityInfuser.MAX_INGREDIENTS + 1) != null) {
					EntityItem itemEntity = player.dropItem(tile.getStackInSlot(TileEntityInfuser.MAX_INGREDIENTS + 1).copy(), false);
					if(itemEntity != null) itemEntity.setPickupDelay(0);
					tile.setInventorySlotContents(TileEntityInfuser.MAX_INGREDIENTS + 1, null);
					tile.updateInfusingRecipe();
					world.notifyBlockUpdate(pos, state, state, 2);
					return true;
				}
			}
		}
		return true;
	}

	@Nullable
	private IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos) {
		TileEntity tileentity = (TileEntity) world.getTileEntity(pos);
		return tileentity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if(!world.isRemote) {
			IInventory tileInventory = (IInventory) world.getTileEntity(pos);
			TileEntityInfuser tile = (TileEntityInfuser) world.getTileEntity(pos);
			if (tileInventory != null && !tile.hasInfusion()) {
				for (int i = 0; i <= TileEntityInfuser.MAX_INGREDIENTS + 1; i++) {
					ItemStack stack = tileInventory.getStackInSlot(i);
					if (stack != null) {
						if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops")) {
							float f = 0.7F;
							double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
							double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
							double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
							EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
							entityitem.setDefaultPickupDelay();
							world.spawnEntityInWorld(entityitem);
						}
					}
				}
			} else if (tileInventory != null && tile.hasInfusion()) {
				ItemStack stack = tileInventory.getStackInSlot(TileEntityInfuser.MAX_INGREDIENTS + 1);
				if (stack != null) {
					if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops")) {
						float f = 0.7F;
						double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
						entityitem.setDefaultPickupDelay();
						world.spawnEntityInWorld(entityitem);
					}
				}
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (world.getTileEntity(pos) instanceof TileEntityInfuser) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			TileEntityInfuser infuser = (TileEntityInfuser) world.getTileEntity(pos);
			if (infuser.getWaterAmount() > 0  && infuser.getTemperature() > 0) {
				int amount = infuser.waterTank.getFluidAmount();
				int capacity = infuser.waterTank.getCapacity();
				float size = 1F / capacity * amount;
				float xx = (float) x + 0.5F;
				float yy = (float) (y + 0.35F + size * 0.5F);
				float zz = (float) z + 0.5F;
				float fixedOffset = 0.25F;
				float randomOffset = rand.nextFloat() * 0.6F - 0.3F;
				if(rand.nextInt((101 - infuser.getTemperature()))/4 == 0) {
					BLParticles.BUBBLE_INFUSION.spawn(world, xx, yy, zz);
					if (rand.nextInt(10) == 0 && infuser.getTemperature() > 70)
						world.playSound(xx, yy, zz, SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 1.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.5F, false);
				}
				if (infuser.getTemperature() >= 100) {
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx - fixedOffset), (double) y + 0.75D, (double) (zz + randomOffset));
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + fixedOffset), (double) y + 0.75D, (double) (zz + randomOffset));
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) y + 0.75D, (double) (zz - fixedOffset));
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) y + 0.75D, (double) (zz + fixedOffset));
				}
			}
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityInfuser();
	}
}