package thebetweenlands.common.block.container;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.EnumBLDyeColor;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityBoilingPot;

public class BlockBoilingPot extends Block implements ITileEntityProvider {

	public BlockBoilingPot() {
		super(Material.WOOD);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
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
		return new TileEntityBoilingPot();
	}

	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityBoilingPot tile = (TileEntityBoilingPot) world.getTileEntity(pos);
		if (tile != null) {
			InventoryHelper.dropInventoryItems(world, pos, tile);
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		final IFluidHandler fluidHandler = getFluidHandler(world, pos);

		if (fluidHandler != null && FluidUtil.getFluidHandler(heldItem) != null) {
			FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);
			return FluidUtil.getFluidHandler(heldItem) != null;
		}

		if (!world.isRemote && world.getTileEntity(pos) instanceof TileEntityBoilingPot) {
			TileEntityBoilingPot tile = (TileEntityBoilingPot) world.getTileEntity(pos);

			if (!player.isSneaking()) {
				if (!heldItem.isEmpty() && heldItem.getItem() == ItemRegistry.SILK_BUNDLE) {
					if (tile.getStackInSlot(0).isEmpty()) {
						ItemStack ingredient = heldItem.copy();
						ingredient.setCount(1);
						tile.setInventorySlotContents(0, ingredient);
						if (!player.capabilities.isCreativeMode)
							heldItem.shrink(1);
						tile.setHeatProgress(0);
						if(tile.tank.getFluid() != null)
							world.playSound((EntityPlayer) null, pos, SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.BLOCKS, 0.75F, 2F);
						world.notifyBlockUpdate(pos, state, state, 3);
						return true;
					}
				}
				
			}

			if (player.isSneaking()) {
				if (!tile.getStackInSlot(0).isEmpty()) {
					if (!player.inventory.addItemStackToInventory(tile.getStackInSlot(0)))
						ForgeHooks.onPlayerTossEvent(player, tile.getStackInSlot(0), false);
					tile.setInventorySlotContents(0, ItemStack.EMPTY);
					tile.setHeatProgress(0);
					world.playSound((EntityPlayer) null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5F, 2F);
					world.notifyBlockUpdate(pos, state, state, 3);
					return true;
				}
			}
		}
		return false;
	}

	@Nullable
	private IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos) {
		TileEntityBoilingPot tileentity = (TileEntityBoilingPot) world.getTileEntity(pos);
		return tileentity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (world.getTileEntity(pos) instanceof TileEntityBoilingPot) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			TileEntityBoilingPot pot = (TileEntityBoilingPot) world.getTileEntity(pos);
			if (pot.getTankFluidAmount() > 0  && pot.getHeatProgress() > 80) {
				int amount = pot.tank.getFluidAmount();
				int capacity = pot.tank.getCapacity();
				float size = 1F / capacity * amount;
				float xx = (float) x + 0.5F;
				float yy = (float) (y + 0.35F + size * 0.25F);
				float zz = (float) z + 0.5F;
				float fixedOffset = 0.25F;
				float randomOffset = rand.nextFloat() * 0.6F - 0.3F;
				int colour = pot.tank.getFluid().getFluid().getColor();
				if(pot.tank.getFluid().tag != null && pot.tank.getFluid().tag.hasKey("color"))
					colour =  EnumBLDyeColor.byMetadata(pot.tank.getFluid().tag.getInteger("color")).getColorValue() | 0xFF000000;

				BLParticles.BUBBLE_INFUSION.spawn(world, xx + 0.3F - rand.nextFloat() * 0.6F, yy, zz + 0.3F - rand.nextFloat() * 0.6F, ParticleArgs.get().withScale(0.3F).withColor(colour));
	
				if (pot.getHeatProgress() >= 100) {
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx - fixedOffset), (double) y + 0.75D, (double) (zz + randomOffset), ParticleArgs.get().withScale(0.3F).withColor(colour));
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + fixedOffset), (double) y + 0.75D, (double) (zz + randomOffset), ParticleArgs.get().withScale(0.3F).withColor(colour));
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) y + 0.75D, (double) (zz - fixedOffset), ParticleArgs.get().withScale(0.3F).withColor(colour));
					BLParticles.STEAM_PURIFIER.spawn(world, (double) (xx + randomOffset), (double) y + 0.75D, (double) (zz + fixedOffset), ParticleArgs.get().withScale(0.3F).withColor(colour));
				}
			}
		}
	}
}
