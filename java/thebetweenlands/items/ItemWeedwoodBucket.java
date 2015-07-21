package thebetweenlands.items;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.tileentities.TileEntityInfuser;
import cpw.mods.fml.common.eventhandler.Event;

public class ItemWeedwoodBucket extends Item {

	private final Block fluid;

	public ItemWeedwoodBucket() {
		this(Blocks.air);
		setUnlocalizedName("thebetweenlands.weedwoodBucket");
		setTextureName("thebetweenlands:weedwoodBucket");
	}

	public ItemWeedwoodBucket(Block fluid) {
		this.fluid = fluid;
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		boolean flag = fluid == Blocks.air;
		MovingObjectPosition pos = getMovingObjectPositionFromPlayer(world, player, flag);

		if (pos == null)
			return stack;
		else {
			FillBucketEvent event = new FillBucketEvent(player, stack, world, pos);
			if (MinecraftForge.EVENT_BUS.post(event))
				return stack;

			if (event.getResult() == Event.Result.ALLOW) {
				if (player.capabilities.isCreativeMode)
					return stack;

				if (--stack.stackSize <= 0)
					return event.result;

				if (!player.inventory.addItemStackToInventory(event.result))
					player.dropPlayerItemWithRandomChoice(event.result, false);

				return stack;
			}
			if (pos.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				int x = pos.blockX;
				int y = pos.blockY;
				int z = pos.blockZ;

				if (!world.canMineBlock(player, x, y, z))
					return stack;

				if (flag) {
					if (!player.canPlayerEdit(x, y, z, pos.sideHit, stack))
						return stack;

					Block block = world.getBlock(x, y, z);
					int meta = world.getBlockMetadata(x, y, z);

					if (block == BLBlockRegistry.rubberTreeLog && meta == 1 && pos.sideHit >= 2 && !world.isRemote) {
						int tx = x + (pos.sideHit == 4 ? -1 : (pos.sideHit == 5 ? 1 : 0));
						int tz = z + (pos.sideHit == 2 ? -1 : (pos.sideHit == 3 ? 1 : 0));
						world.setBlock(tx, y, tz, BLBlockRegistry.rubberTap, 0, 0);
						stack.stackSize--;
						return stack;
					}

					if (block == BLBlockRegistry.tarFluid && meta == 0) {
						world.setBlockToAir(x, y, z);
						return addBucketToPlayer(stack, player, BLItemRegistry.weedwoodBucketTar);
					}

					if (block == BLBlockRegistry.swampWater && meta == 0) {
						world.setBlockToAir(x, y, z);
						return addBucketToPlayer(stack, player, BLItemRegistry.weedwoodBucketWater);
					}

					if (block == BLBlockRegistry.infuser && player.isSneaking()) {
						TileEntityInfuser tile = (TileEntityInfuser) world.getTileEntity(x, y, z);
						if(tile != null && !tile.hasInfusion && tile.getWaterAmount() >= FluidContainerRegistry.BUCKET_VOLUME) {
							tile.extractFluids(new FluidStack(BLFluidRegistry.swampWater, FluidContainerRegistry.BUCKET_VOLUME));
							return addBucketToPlayer(stack, player, BLItemRegistry.weedwoodBucketWater);
						}
						 if(tile != null && tile.hasInfusion && tile.getWaterAmount() >= FluidContainerRegistry.BUCKET_VOLUME) {
							 ItemStack infusionBucket = new ItemStack(BLItemRegistry.weedwoodBucketInfusion);
							 infusionBucket.setTagCompound(new NBTTagCompound());
							 infusionBucket.getTagCompound().setString("Infused", "Infused");
							 for (int i = 0; i < tile.getSizeInventory(); i++) {
								 ItemStack stackInSlot = tile.getStackInSlot(i);
								 //TODO this will eventually be properties not the item name
								 if (stackInSlot != null)
									 infusionBucket.getTagCompound().setString("crushedItem" + i, tile.getStackInSlot(i).getDisplayName());
							 }
							 tile.extractFluids(new FluidStack(BLFluidRegistry.swampWater, FluidContainerRegistry.BUCKET_VOLUME));
							 return infusionBucket;
						 }
						else
							return stack;
					}

				} else {
					if (pos.sideHit == 0)
						y--;
					if (pos.sideHit == 1)
						y++;
					if (pos.sideHit == 2)
						z--;
					if (pos.sideHit == 3)
						z++;
					if (pos.sideHit == 4)
						x--;
					if (pos.sideHit == 5)
						x++;

					if (!player.canPlayerEdit(x, y, z, pos.sideHit, stack))
						return stack;

					if (tryPlaceContainedLiquid(world, x, y, z) && !player.capabilities.isCreativeMode) {
						stack.stackSize--;
						if (stack.stackSize <= 0)
							return new ItemStack(BLItemRegistry.weedwoodBucket);
						else
							player.inventory.addItemStackToInventory(new ItemStack(BLItemRegistry.weedwoodBucket));
						return stack;
					}
				}
			}
			return stack;
		}
	}

	private ItemStack addBucketToPlayer(ItemStack stack, EntityPlayer player, Item item) {
		if (player.capabilities.isCreativeMode)
			return stack;
		else if (--stack.stackSize <= 0)
			return new ItemStack(item);
		else {
			if (!player.inventory.addItemStackToInventory(new ItemStack(item)))
				player.dropPlayerItemWithRandomChoice(new ItemStack(item), false);

			return stack;
		}
	}

	private boolean tryPlaceContainedLiquid(World world, int x, int y, int z) {
		if (fluid == Blocks.air)
			return false;
		else {
			Material material = world.getBlock(x, y, z).getMaterial();
			boolean flag = !material.isSolid();

			if (!world.isAirBlock(x, y, z) && !flag)
				return false;
			else {
				if (world.provider.isHellWorld && fluid == BLBlockRegistry.tarFluid || world.provider.isHellWorld && fluid == BLBlockRegistry.swampWater) {
					world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

					for (int i = 0; i < 8; i++)
						world.spawnParticle("largesmoke", x + Math.random(), y + Math.random(), z + Math.random(), 0.0D, 0.0D, 0.0D);
				} else {
					if (!world.isRemote && flag && !material.isLiquid())
						world.func_147480_a(x, y, z, true);

					world.setBlock(x, y, z, fluid, 0, 3);
				}

				return true;
			}
		}
	}
}