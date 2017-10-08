package thebetweenlands.common.handler;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
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

	public static final Set<Item> WHITELIST = new HashSet<Item>();

	static {
		WHITELIST.add(Items.ROTTEN_FLESH);
		WHITELIST.addAll(ItemRegistry.ITEMS);
	}

	@SubscribeEvent
	public static void onPlayerTorchPlacement(PlaceEvent event) {
		ItemStack mainHand = event.getPlayer().getHeldItemMainhand();
		ItemStack offHand = event.getPlayer().getHeldItemMainhand();
		boolean isHoldingTorchMainhand = !mainHand.isEmpty() && Block.getBlockFromItem(mainHand.getItem()) instanceof BlockTorch && !BlockRegistry.BLOCKS.contains(Block.getBlockFromItem(mainHand.getItem())) && !WHITELIST.contains(mainHand.getItem());
		boolean isHoldingTorchOffhand = !offHand.isEmpty() && Block.getBlockFromItem(offHand.getItem()) instanceof BlockTorch && !BlockRegistry.BLOCKS.contains(Block.getBlockFromItem(offHand.getItem())) && !WHITELIST.contains(offHand.getItem());
		if (isHoldingTorchMainhand || isHoldingTorchOffhand) {
			if (event.getPlayer().dimension == ConfigHandler.dimensionId) {
				for(int x = -2; x <= 2; x++) {
					for(int y = -2; y <= 2; y++) {
						for(int z = -2; z <= 2; z++) {
							IBlockState block = event.getWorld().getBlockState(event.getPos().add(x, y, z));
							if(block.getBlock() == Blocks.TORCH && !BlockRegistry.BLOCKS.contains(block.getBlock()) && !WHITELIST.contains(Item.getItemFromBlock(block.getBlock()))) {
								EnumFacing facing = block.getValue(BlockTorch.FACING);
								IBlockState dampTorch = BlockRegistry.DAMP_TORCH.getDefaultState().withProperty(BlockDampTorch.FACING, facing);
								event.getWorld().setBlockState(event.getPos().add(x, y, z), dampTorch);
								event.getWorld().playSound(null, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.2F, 1.0F);
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
		if(!item.isEmpty() && event.getEntityPlayer().dimension == ConfigHandler.dimensionId) {
			if(item.getItem() instanceof ItemFlintAndSteel && !WHITELIST.contains(item.getItem())) {
				event.setUseItem(Result.DENY);
				event.setCanceled(true);
				if(event.getWorld().isRemote) {
					event.getEntityPlayer().sendMessage(new TextComponentTranslation("chat.flintandsteel", new TextComponentTranslation(item.getUnlocalizedName() + ".name")));
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBonemeal(BonemealEvent event) {
		if(event.getEntityPlayer().dimension == ConfigHandler.dimensionId) {
			ItemStack stack = event.getEntityPlayer().getHeldItem(event.getHand());
			if(!stack.isEmpty() && stack.getItem() == Items.DYE && !WHITELIST.contains(stack.getItem())) {
				event.setResult(Result.DENY);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onEntitySpawn(EntityJoinWorldEvent event) {
		if(event.getEntity() instanceof EntityPlayer && !((EntityPlayer)event.getEntity()).capabilities.isCreativeMode) {
			updatePlayerInventory((EntityPlayer)event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if(!event.player.world.isRemote && event.player.ticksExisted % 5 == 0 && !event.player.capabilities.isCreativeMode) {
			updatePlayerInventory(event.player);
		}
	}

	private static void updatePlayerInventory(EntityPlayer player) {
		int invCount = player.inventory.getSizeInventory();
		if(player.dimension == ConfigHandler.dimensionId) {
			//Set to rotten food/tainted potion
			for(int i = 0; i < invCount; i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(!stack.isEmpty()) {
					if(isRotting(stack)) {
						ItemStack rottenFoodStack = new ItemStack(ItemRegistry.ROTTEN_FOOD, stack.getCount());
						stack.setCount(1);
						ItemRegistry.ROTTEN_FOOD.setOriginalStack(rottenFoodStack, stack);
						player.inventory.setInventorySlotContents(i, rottenFoodStack);
					} else if(isTainting(stack)) {
						ItemStack taintedPotionStack = new ItemStack(ItemRegistry.TAINTED_POTION, stack.getCount());
						stack.setCount(1);
						ItemRegistry.TAINTED_POTION.setOriginalStack(taintedPotionStack, stack);
						player.inventory.setInventorySlotContents(i, taintedPotionStack);
					}
				}
			}
		} else {
			//Revert rotten food/tainted potion
			for(int i = 0; i < invCount; i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(!stack.isEmpty()) {
					if(stack.getItem() == ItemRegistry.ROTTEN_FOOD) {
						ItemStack originalStack = ItemRegistry.ROTTEN_FOOD.getOriginalStack(stack);
						if(!originalStack.isEmpty()) {
							originalStack.setCount(stack.getCount());
							player.inventory.setInventorySlotContents(i, originalStack);
						} else {
							player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
						}
					} else if(stack.getItem() == ItemRegistry.TAINTED_POTION) {
						ItemStack originalStack = ItemRegistry.TAINTED_POTION.getOriginalStack(stack);
						if(!originalStack.isEmpty()) {
							originalStack.setCount(stack.getCount());
							player.inventory.setInventorySlotContents(i, originalStack);
						} else {
							player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onItemPickup(EntityItemPickupEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if(player != null && !player.world.isRemote && !player.capabilities.isCreativeMode) {
			ItemStack stack = event.getItem().getItem();
			if(!stack.isEmpty()) {
				if(player.dimension == ConfigHandler.dimensionId) {
					if(isRotting(stack)) {
						ItemStack rottenFoodStack = new ItemStack(ItemRegistry.ROTTEN_FOOD, stack.getCount());
						ItemRegistry.ROTTEN_FOOD.setOriginalStack(rottenFoodStack, stack);
						event.getItem().setItem(rottenFoodStack);
					} else if(isTainting(stack)) {
						ItemStack taintedPotionStack = new ItemStack(ItemRegistry.TAINTED_POTION, stack.getCount());
						ItemRegistry.TAINTED_POTION.setOriginalStack(taintedPotionStack, stack);
						event.getItem().setItem(taintedPotionStack);
					}
				} else if(stack.getItem() == ItemRegistry.ROTTEN_FOOD) {
					ItemStack originalStack = ItemRegistry.ROTTEN_FOOD.getOriginalStack(stack);
					if(!originalStack.isEmpty()) {
						event.getItem().setItem(originalStack);
					} else {
						event.getItem().setDead();
						event.setCanceled(true);
					}
				} else if(stack.getItem() == ItemRegistry.TAINTED_POTION) {
					ItemStack originalStack = ItemRegistry.TAINTED_POTION.getOriginalStack(stack);
					if(!originalStack.isEmpty()) {
						event.getItem().setItem(originalStack);
					} else {
						event.getItem().setDead();
						event.setCanceled(true);
					}
				}
			}
		}
	}

	public static boolean isRotting(ItemStack stack) {
		return stack.getItem() instanceof ItemFood && !WHITELIST.contains(stack.getItem());
	}

	public static boolean isTainting(ItemStack stack) {
		return stack.getItem() instanceof ItemPotion && !WHITELIST.contains(stack.getItem());
	}
}