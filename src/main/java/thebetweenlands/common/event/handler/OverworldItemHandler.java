package thebetweenlands.common.event.handler;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import thebetweenlands.common.block.misc.BlockDampTorch;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.config.ConfigHandler;

public class OverworldItemHandler {
	private OverworldItemHandler() { }

	@SubscribeEvent
	public void onPlayerTorchPlacement(PlaceEvent event) {
		ItemStack itemstack = event.getPlayer().inventory.getCurrentItem();
		if (itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.TORCH)) {
			if (event.getPlayer().dimension == ConfigHandler.dimensionId) {
				for(int x = -2; x <= 2; x++) {
					for(int y = -2; y <= 2; y++) {
						for(int z = -2; z <= 2; z++) {
							IBlockState block = event.getWorld().getBlockState(event.getPos().add(x, y, z));
							if(block.getBlock() == Blocks.TORCH) {
								EnumFacing facing = block.getValue(BlockTorch.FACING);
								IBlockState dampTorch = BlockRegistry.DAMP_TORCH.getDefaultState().withProperty(BlockDampTorch.FACING, facing);
								event.getWorld().setBlockState(event.getPos().add(x, y, z), dampTorch);
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onUseItem(PlayerInteractEvent.RightClickBlock event) {
		ItemStack item = event.getItemStack();
		if(item != null && event.getEntityPlayer().dimension == ConfigHandler.dimensionId) {
			if(item.getItem() == Items.FLINT_AND_STEEL) {
				event.setUseItem(Result.DENY);
				event.setCanceled(true);
				if(event.getWorld().isRemote) {
					event.getEntityPlayer().addChatMessage(new TextComponentTranslation("chat.flintandsteel", new TextComponentTranslation(item.getUnlocalizedName() + ".name")));
				}
			}
		}
	}

	private static final ImmutableList<Item> EXCEPTION_INSTS;

	static {
		List<Item> items = new ArrayList<Item>();
		items.add(Items.ROTTEN_FLESH);
		items.addAll(ItemRegistry.ITEMS);

		EXCEPTION_INSTS = ImmutableList.copyOf(items);
	}

	@SubscribeEvent
	public static void onEntitySpawn(EntityJoinWorldEvent event) {
		if(event.getEntity() instanceof EntityPlayer && !((EntityPlayer)event.getEntity()).capabilities.isCreativeMode) {
			updatePlayerInventory((EntityPlayer)event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if(!event.player.worldObj.isRemote && event.player.ticksExisted % 5 == 0 && !event.player.capabilities.isCreativeMode) {
			updatePlayerInventory(event.player);
		}
	}

	private static void updatePlayerInventory(EntityPlayer player) {
		int invCount = player.inventory.getSizeInventory();
		if(player.dimension == ConfigHandler.dimensionId) {
			//Set to rotten food/tainted potion
			for(int i = 0; i < invCount; i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(stack != null) {
					if(isRotting(stack)) {
						ItemStack rottenFoodStack = new ItemStack(ItemRegistry.ROTTEN_FOOD, stack.stackSize);
						stack.stackSize = 1;
						ItemRegistry.ROTTEN_FOOD.setOriginalStack(rottenFoodStack, stack);
						player.inventory.setInventorySlotContents(i, rottenFoodStack);
					} else if(isTainting(stack)) {
						ItemStack taintedPotionStack = new ItemStack(ItemRegistry.TAINTED_POTION, stack.stackSize);
						stack.stackSize = 1;
						ItemRegistry.TAINTED_POTION.setOriginalStack(taintedPotionStack, stack);
						player.inventory.setInventorySlotContents(i, taintedPotionStack);
					}
				}
			}
		} else {
			//Revert rotten food/tainted potion
			for(int i = 0; i < invCount; i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(stack != null) {
					if(stack.getItem() == ItemRegistry.ROTTEN_FOOD) {
						ItemStack originalStack = ItemRegistry.ROTTEN_FOOD.getOriginalStack(stack);
						if(originalStack != null) {
							originalStack.stackSize = stack.stackSize;
							player.inventory.setInventorySlotContents(i, originalStack);
						} else {
							player.inventory.setInventorySlotContents(i, null);
						}
					} else if(stack.getItem() == ItemRegistry.TAINTED_POTION) {
						ItemStack originalStack = ItemRegistry.TAINTED_POTION.getOriginalStack(stack);
						if(originalStack != null) {
							originalStack.stackSize = stack.stackSize;
							player.inventory.setInventorySlotContents(i, originalStack);
						} else {
							player.inventory.setInventorySlotContents(i, null);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onItemPickup(EntityItemPickupEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if(player != null && !player.worldObj.isRemote && !player.capabilities.isCreativeMode) {
			ItemStack stack = event.getItem().getEntityItem();
			if(stack != null) {
				if(player.dimension == ConfigHandler.dimensionId) {
					if(isRotting(stack)) {
						ItemStack rottenFoodStack = new ItemStack(ItemRegistry.ROTTEN_FOOD, stack.stackSize);
						ItemRegistry.ROTTEN_FOOD.setOriginalStack(rottenFoodStack, stack);
						event.getItem().setEntityItemStack(rottenFoodStack);
					} else if(isTainting(stack)) {
						ItemStack taintedPotionStack = new ItemStack(ItemRegistry.TAINTED_POTION, stack.stackSize);
						ItemRegistry.TAINTED_POTION.setOriginalStack(taintedPotionStack, stack);
						event.getItem().setEntityItemStack(taintedPotionStack);
					}
				} else if(stack.getItem() == ItemRegistry.ROTTEN_FOOD) {
					ItemStack originalStack = ItemRegistry.ROTTEN_FOOD.getOriginalStack(stack);
					if(originalStack != null) {
						event.getItem().setEntityItemStack(originalStack);
					} else {
						event.getItem().setDead();
						event.setCanceled(true);
					}
				} else if(stack.getItem() == ItemRegistry.TAINTED_POTION) {
					ItemStack originalStack = ItemRegistry.TAINTED_POTION.getOriginalStack(stack);
					if(originalStack != null) {
						event.getItem().setEntityItemStack(originalStack);
					} else {
						event.getItem().setDead();
						event.setCanceled(true);
					}
				}
			}
		}
	}

	public static boolean isRotting(ItemStack stack) {
		return stack.getItem() instanceof ItemFood && !EXCEPTION_INSTS.contains(stack.getItem());
	}

	public static boolean isTainting(ItemStack stack) {
		return stack.getItem() instanceof ItemPotion && !EXCEPTION_INSTS.contains(stack.getItem());
	}
}