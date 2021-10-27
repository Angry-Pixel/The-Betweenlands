package thebetweenlands.common.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.item.misc.ItemLifeCrystal;
import thebetweenlands.common.item.tools.ItemBLBucket;
import thebetweenlands.common.registries.FluidRegistry;
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
		return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
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
		int rotation = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		state = state.withProperty(FACING, EnumFacing.byHorizontalIndex(rotation));
		worldIn.setBlockState(pos, state, 3);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		TileEntity tileEntity = world.getTileEntity(pos);
		if (!world.isRemote && tileEntity instanceof TileEntityInfuser) {
			TileEntityInfuser tile = (TileEntityInfuser) tileEntity;

			final IFluidHandler fluidHandler = getFluidHandler(world, pos);
			if (fluidHandler != null && FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) {
				return true;
			}

			if (!player.isSneaking()) {
				if (heldItem.isEmpty() && tile.getStirProgress() >= 90) {
					tile.setStirProgress(0);
					return true;
				}
				if (!heldItem.isEmpty() && !tile.hasInfusion()) {
					ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(heldItem, AspectManager.get(world));
					if(aspectContainer.getAspects().size() > 0) {
						ItemStack ingredient = heldItem;
						for (int i = 0; i < TileEntityInfuser.MAX_INGREDIENTS; i++) {
							if(tile.getStackInSlot(i).isEmpty()) {
								ItemStack singleIngredient = ingredient.copy();
								singleIngredient.setCount(1);
								tile.setInventorySlotContents(i, singleIngredient);
								tile.updateInfusingRecipe();
								if (!player.capabilities.isCreativeMode) 
									heldItem.shrink(1);
								world.notifyBlockUpdate(pos, state, state, 2);
								if(tile.getWaterAmount() > 0) {
									world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.3f, 0.9f + world.rand.nextFloat() * 0.3f);
								} else {
									world.playSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_ADD_ITEM, SoundCategory.BLOCKS, 0.3f, 0.9f + world.rand.nextFloat() * 0.3f);
								}
								return true;
							}
						}
					}
				}

				if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemLifeCrystal) {
					if(tile.getStackInSlot(TileEntityInfuser.MAX_INGREDIENTS + 1).isEmpty()) {
						tile.setInventorySlotContents(TileEntityInfuser.MAX_INGREDIENTS + 1, heldItem);
						tile.updateInfusingRecipe();
						if (!player.capabilities.isCreativeMode) player.setHeldItem(hand, ItemStack.EMPTY);
					}
					return true;
				}
			}

			if(player.isSneaking() && !tile.hasInfusion()) {
				for (int i = TileEntityInfuser.MAX_INGREDIENTS; i >= 0; i--) {
					if(!tile.getStackInSlot(i).isEmpty()) {
						EntityItem itemEntity = player.dropItem(tile.getStackInSlot(i).copy(), false);
						if(itemEntity != null) itemEntity.setPickupDelay(0);
						tile.setInventorySlotContents(i, ItemStack.EMPTY);
						tile.updateInfusingRecipe();
						world.notifyBlockUpdate(pos, state, state, 2);
						return true;
					}
				}
			}

			if(player.isSneaking()) {
				if (heldItem.getItem() instanceof ItemBLBucket && ((ItemBLBucket) heldItem.getItem()).getFluid(heldItem) == null && tile.hasInfusion() && tile.getWaterAmount() >= Fluid.BUCKET_VOLUME) {
					ItemStack infusionBucket = new ItemStack(ItemRegistry.BL_BUCKET_INFUSION, 1, heldItem.getMetadata());
					NBTTagCompound nbtCompound = new NBTTagCompound();
					infusionBucket.setTagCompound(nbtCompound);
					nbtCompound.setString("infused", "Infused");
					NBTTagList nbtList = new NBTTagList();
					for (int i = 0; i < tile.getSizeInventory() - 1; i++) {
						ItemStack stackInSlot = tile.getStackInSlot(i);
						if (!stackInSlot.isEmpty()) {
							nbtList.appendTag(stackInSlot.writeToNBT(new NBTTagCompound()));
						}
					}
					nbtCompound.setTag("ingredients", nbtList);
					nbtCompound.setInteger("infusionTime", tile.getInfusionTime());
					tile.extractFluids(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME));
					if (heldItem.getCount() == 1) {
						player.setHeldItem(hand, infusionBucket.copy());
						return true;
					} else {
						if (!player.addItemStackToInventory(infusionBucket.copy()))
							player.dropItem(infusionBucket.copy(), false);
						heldItem.shrink(1);
						return true;
					}
				}

				if(!tile.getStackInSlot(TileEntityInfuser.MAX_INGREDIENTS + 1).isEmpty()) {
					EntityItem itemEntity = player.dropItem(tile.getStackInSlot(TileEntityInfuser.MAX_INGREDIENTS + 1).copy(), false);
					if(itemEntity != null) itemEntity.setPickupDelay(0);
					tile.setInventorySlotContents(TileEntityInfuser.MAX_INGREDIENTS + 1, ItemStack.EMPTY);
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
					if (!stack.isEmpty()) {
						float f = 0.7F;
						double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
						entityitem.setDefaultPickupDelay();
						world.spawnEntity(entityitem);
					}
				}
			} else if (tileInventory != null && tile.hasInfusion()) {
				ItemStack stack = tileInventory.getStackInSlot(TileEntityInfuser.MAX_INGREDIENTS + 1);
				if (!stack.isEmpty()) {
					float f = 0.7F;
					double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
					double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
					double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
					entityitem.setDefaultPickupDelay();
					world.spawnEntity(entityitem);
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
					float colors[] = infuser.currentInfusionColor;
					BLParticles.BUBBLE_INFUSION.spawn(world, xx + 0.3F - rand.nextFloat() * 0.6F, yy, zz + 0.3F - rand.nextFloat() * 0.6F, ParticleArgs.get().withScale(0.3F).withColor(colors[0], colors[1], colors[2], 1));
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
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}
	
	@Override
	public void fillWithRain(World world, BlockPos pos) {
		if (world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && world.getTileEntity(pos) instanceof TileEntityInfuser) {
			TileEntityInfuser tile = (TileEntityInfuser) world.getTileEntity(pos);
			
			if(tile != null) {
				tile.fill(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME), true);
			}
		}
	}
}