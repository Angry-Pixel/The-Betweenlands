package thebetweenlands.event.player;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class OverworldItemEventHandler {
	@SubscribeEvent
	public void onPlayerTorchPlacement(PlaceEvent event) {
		ItemStack itemstack = event.player.inventory.getCurrentItem();
		if (itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.torch)) {
			if (event.player.dimension == ConfigHandler.DIMENSION_ID) {
				for(int x = -2; x <= 2; x++) {
					for(int y = -2; y <= 2; y++) {
						for(int z = -2; z <= 2; z++) {
							Block block = event.world.getBlock(event.x + x, event.y + y, event.z + z);
							if(block == Blocks.torch) {
								int meta = event.world.getBlockMetadata(event.x + x, event.y + y, event.z + z);
								event.world.setBlock(event.x + x, event.y + y, event.z + z, BLBlockRegistry.dampTorch, meta, 3);
								event.world.playSoundEffect(event.x, event.y + 1, event.z, "random.fizz", 1.0F, 1.0F);
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onUseItem(PlayerInteractEvent event) {
		if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = event.entityPlayer.getHeldItem();
			if(item != null && event.entityPlayer.dimension == ConfigHandler.DIMENSION_ID) {
				if(item.getItem() == Items.flint_and_steel) {
					event.useItem = Result.DENY;
					event.setCanceled(true);
					if(event.world.isRemote) {
						event.entityPlayer.addChatMessage(new ChatComponentTranslation("chat.flintandsteel", new ChatComponentTranslation(item.getUnlocalizedName() + ".name")));
					}
				}
			}
		}
	}

	private static final ImmutableList<Item> EXCEPTION_INSTS;

	static {
		List<Item> items = new ArrayList<Item>();
		items.add(BLItemRegistry.rottenFood);
		items.add(Items.rotten_flesh);

		items.addAll(BLItemRegistry.ITEMS);

		EXCEPTION_INSTS = ImmutableList.copyOf(items);
	}

	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event) {
		if(event.entity instanceof EntityPlayer && !((EntityPlayer)event.entity).capabilities.isCreativeMode) {
			this.updatePlayerInventory((EntityPlayer)event.entity);
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if(!event.player.worldObj.isRemote && event.player.ticksExisted % 5 == 0 && !event.player.capabilities.isCreativeMode) {
			this.updatePlayerInventory(event.player);
		}
	}

	private void updatePlayerInventory(EntityPlayer player) {
		int invCount = player.inventory.getSizeInventory();
		if(player.dimension == ModInfo.DIMENSION_ID) {
			//Set to rotten food/tainted potion
			for(int i = 0; i < invCount; i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(stack != null) {
					if(this.isRotting(stack)) {
						ItemStack rottenFoodStack = new ItemStack(BLItemRegistry.rottenFood, stack.stackSize);
						stack.stackSize = 1;
						BLItemRegistry.rottenFood.setOriginalStack(rottenFoodStack, stack);
						player.inventory.setInventorySlotContents(i, rottenFoodStack);
					} else if(this.isTainting(stack)) {
						ItemStack taintedPotionStack = new ItemStack(BLItemRegistry.taintedPotion, stack.stackSize);
						stack.stackSize = 1;
						BLItemRegistry.taintedPotion.setOriginalStack(taintedPotionStack, stack);
						player.inventory.setInventorySlotContents(i, taintedPotionStack);
					}
				}
			}
		} else {
			//Revert rotten food/tainted potion
			for(int i = 0; i < invCount; i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(stack != null) {
					if(stack.getItem() == BLItemRegistry.rottenFood) {
						ItemStack originalStack = BLItemRegistry.rottenFood.getOriginalStack(stack);
						if(originalStack != null) {
							originalStack.stackSize = stack.stackSize;
							player.inventory.setInventorySlotContents(i, originalStack);
						} else {
							player.inventory.setInventorySlotContents(i, null);
						}
					} else if(stack.getItem() == BLItemRegistry.taintedPotion) {
						ItemStack originalStack = BLItemRegistry.taintedPotion.getOriginalStack(stack);
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
	public void onItemPickup(EntityItemPickupEvent event) {
		if(event.entityPlayer != null && !event.entityPlayer.worldObj.isRemote && !event.entityPlayer.capabilities.isCreativeMode) {
			ItemStack stack = event.item.getEntityItem();
			if(stack != null) {
				if(event.entityPlayer.dimension == ModInfo.DIMENSION_ID) {
					if(this.isRotting(stack)) {
						ItemStack rottenFoodStack = new ItemStack(BLItemRegistry.rottenFood, stack.stackSize);
						BLItemRegistry.rottenFood.setOriginalStack(rottenFoodStack, stack);
						event.item.setEntityItemStack(rottenFoodStack);
					} else if(this.isTainting(stack)) {
						ItemStack taintedPotionStack = new ItemStack(BLItemRegistry.taintedPotion, stack.stackSize);
						BLItemRegistry.taintedPotion.setOriginalStack(taintedPotionStack, stack);
						event.item.setEntityItemStack(taintedPotionStack);
					}
				} else if(stack.getItem() == BLItemRegistry.rottenFood) {
					ItemStack originalStack = BLItemRegistry.rottenFood.getOriginalStack(stack);
					if(originalStack != null) {
						event.item.setEntityItemStack(originalStack);
					} else {
						event.item.setDead();
						event.setCanceled(true);
					}
				} else if(stack.getItem() == BLItemRegistry.taintedPotion) {
					ItemStack originalStack = BLItemRegistry.taintedPotion.getOriginalStack(stack);
					if(originalStack != null) {
						event.item.setEntityItemStack(originalStack);
					} else {
						event.item.setDead();
						event.setCanceled(true);
					}
				}
			}
		}
	}

	public boolean isRotting(ItemStack stack) {
		return stack.getItem() instanceof ItemFood && !EXCEPTION_INSTS.contains(stack.getItem());
	}

	public boolean isTainting(ItemStack stack) {
		return stack.getItem() instanceof ItemPotion && !EXCEPTION_INSTS.contains(stack.getItem());
	}
}