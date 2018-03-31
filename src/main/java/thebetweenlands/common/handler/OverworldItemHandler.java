package thebetweenlands.common.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.event.ArmSwingSpeedEvent;
import thebetweenlands.common.block.misc.BlockDampTorch;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.item.tools.ItemBLAxe;
import thebetweenlands.common.item.tools.ItemBLPickaxe;
import thebetweenlands.common.item.tools.ItemBLShovel;
import thebetweenlands.common.item.tools.ItemBLSword;
import thebetweenlands.common.item.tools.bow.ItemBLBow;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class OverworldItemHandler {
	private OverworldItemHandler() { }

	public static interface ITorchPlaceHandler {
		/**
		 * Returns the ID of this handler
		 * @return
		 */
		public ResourceLocation getID();

		/**
		 * Returns whether the specified item is a torch that should be destroyed or replaced
		 * @param stack
		 * @return
		 */
		public boolean isTorchItem(ItemStack stack);

		/**
		 * Called when a torch item is placed
		 * @param world
		 * @param pos
		 * @param stack
		 * @param player
		 * @return Return true if the placing was handled
		 */
		public default boolean onTorchItemPlaced(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
			return false;
		}

		/**
		 * Returns whether the specified block is a torch block that should be destroyed or replaced
		 * @param world
		 * @param pos
		 * @param state
		 * @param stack
		 * @param player
		 * @return
		 */
		public boolean isTorchBlock(World world, BlockPos pos, IBlockState state, ItemStack stack, EntityPlayer player);

		/**
		 * Called when a torch is placed, by default replaces the block with a damp torch
		 * @param world
		 * @param pos
		 * @param block
		 * @param player
		 */
		public default void onTorchBlockPlaced(World world, BlockPos pos, IBlockState state, ItemStack stack, EntityPlayer player) {

		}
	}

	//TODO Add configs for all blacklists/whitelists and torch place handlers?

	public static final Map<ResourceLocation, Predicate<ItemStack>> ROTTING_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> ROTTING_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> TAINTING_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> TAINTING_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> FLINT_AND_STEEL_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> FLINT_AND_STEEL_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> BONEMEAL_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> BONEMEAL_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> TOOL_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> TOOL_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, ITorchPlaceHandler> TORCH_PLACE_HANDLERS = new HashMap<>();

	static {
		ROTTING_WHITELIST.put(new ResourceLocation(ModInfo.ID, "config_whitelist"), stack -> BetweenlandsConfig.isFoodConfigWhitelisted(stack));
		ROTTING_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "default_blacklist"), stack -> {
			if(stack.getItem() == Items.CAKE || Block.getBlockFromItem(stack.getItem()) == Blocks.CAKE) {
				return true;
			}
			if(stack.getItem() instanceof ItemFood && stack.getItem() != Items.ROTTEN_FLESH && !ItemRegistry.ITEMS.contains(stack.getItem())) {
				return true;
			}
			return false;
		});

		TAINTING_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "default_blacklist"), stack -> stack.getItem() instanceof ItemPotion);

		FLINT_AND_STEEL_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "default_blacklist"), stack -> stack.getItem() instanceof ItemFlintAndSteel);

		BONEMEAL_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "default_blacklist"), stack -> stack.getItem() == Items.DYE);

		TOOL_BLACKLIST.put(new ResourceLocation(ModInfo.ID, "default_blacklist"), stack -> 
				(stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow) &&
				stack.getItem() instanceof ItemBLSword == false && 
				stack.getItem() instanceof ItemBLAxe == false && 
				stack.getItem() instanceof ItemBLPickaxe == false && 
				stack.getItem() instanceof ItemBLShovel == false &&
				stack.getItem() instanceof ItemBLBow == false &&
				!ItemRegistry.ITEMS.contains(stack.getItem()));

		ITorchPlaceHandler vanillaTorchPlaceHandler = new ITorchPlaceHandler() {
			@Override
			public ResourceLocation getID() {
				return new ResourceLocation(ModInfo.ID, "vanilla_torch");
			}

			@Override
			public boolean isTorchItem(ItemStack stack) {
				Block block = Block.getBlockFromItem(stack.getItem());
				return block instanceof BlockTorch && !BlockRegistry.BLOCKS.contains(block);
			}

			@Override
			public boolean isTorchBlock(World world, BlockPos pos, IBlockState state, ItemStack stack, EntityPlayer player) {
				return state.getBlock() instanceof BlockTorch && !BlockRegistry.BLOCKS.contains(state.getBlock());
			}

			@Override
			public void onTorchBlockPlaced(World world, BlockPos pos, IBlockState state, ItemStack stack, EntityPlayer player) {
				EnumFacing facing = state.getValue(BlockTorch.FACING);
				IBlockState dampTorch = BlockRegistry.DAMP_TORCH.getDefaultState().withProperty(BlockDampTorch.FACING, facing);
				world.setBlockState(pos, dampTorch);
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.2F, 1.0F);
				if(player instanceof EntityPlayerMP) {
					AdvancementCriterionRegistry.DAMP_TORCH_PLACED.trigger((EntityPlayerMP) player);
				}
			}
		};
		TORCH_PLACE_HANDLERS.put(vanillaTorchPlaceHandler.getID(), vanillaTorchPlaceHandler);
	}

	@SubscribeEvent
	public static void onPlayerTorchPlacement(PlaceEvent event) {
		if (event.getPlayer().dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			ItemStack held = event.getPlayer().getHeldItem(event.getHand());
			if(!held.isEmpty()) {
				for(ITorchPlaceHandler handler : TORCH_PLACE_HANDLERS.values()) {
					if(handler.isTorchItem(held)) {
						if(!handler.onTorchItemPlaced(event.getWorld(), event.getPos(), held, event.getPlayer())) {
							for(int x = -2; x <= 2; x++) {
								for(int y = -2; y <= 2; y++) {
									for(int z = -2; z <= 2; z++) {
										BlockPos offset = event.getPos().add(x, y, z);
										IBlockState state = event.getWorld().getBlockState(offset);
										if(handler.isTorchBlock(event.getWorld(), offset, state, held, event.getPlayer())) {
											handler.onTorchBlockPlaced(event.getWorld(), offset, state, held, event.getPlayer());
										}
									}
								}
							}
						} else {
							break;
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onUseItem(PlayerInteractEvent.RightClickBlock event) {
		ItemStack item = event.getItemStack();
		if(!item.isEmpty() && event.getEntityPlayer().dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			if(isFlintAndSteelBlocked(item)) {
				event.setUseItem(Result.DENY);
				event.setCanceled(true);
				if(event.getWorld().isRemote) {
					event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("chat.flintandsteel", new TextComponentTranslation(item.getUnlocalizedName() + ".name")), true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBonemeal(BonemealEvent event) {
		if(event.getEntityPlayer().dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			ItemStack stack = event.getEntityPlayer().getHeldItem(event.getHand());
			if(!stack.isEmpty() && isBonemealBlocked(stack)) {
				event.setResult(Result.DENY);
				event.setCanceled(true);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onArmSwingSpeed(ArmSwingSpeedEvent event) {
		if(event.getEntityLiving().dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			ItemStack tool = event.getEntityLiving().getHeldItemMainhand();
			if (!tool.isEmpty() && isToolWeakened(tool)) {
				event.setSpeed(event.getSpeed() * 0.3F);
			}
		}
	}
	
	@SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if(event.getEntityPlayer().dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			ItemStack tool = event.getEntityPlayer().getHeldItemMainhand();
			if (!tool.isEmpty() && isToolWeakened(tool)) {
				event.setNewSpeed(event.getNewSpeed() * 0.3F);
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
		if(player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
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
					if(BetweenlandsConfig.GENERAL.reverseRottenFood && stack.getItem() == ItemRegistry.ROTTEN_FOOD) {
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
				if(player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
					if(isRotting(stack)) {
						ItemStack rottenFoodStack = new ItemStack(ItemRegistry.ROTTEN_FOOD, stack.getCount());
						ItemRegistry.ROTTEN_FOOD.setOriginalStack(rottenFoodStack, stack);
						event.getItem().setItem(rottenFoodStack);
					} else if(isTainting(stack)) {
						ItemStack taintedPotionStack = new ItemStack(ItemRegistry.TAINTED_POTION, stack.getCount());
						ItemRegistry.TAINTED_POTION.setOriginalStack(taintedPotionStack, stack);
						event.getItem().setItem(taintedPotionStack);
					}
				} else if(BetweenlandsConfig.GENERAL.reverseRottenFood && stack.getItem() == ItemRegistry.ROTTEN_FOOD) {
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
		if(!BetweenlandsConfig.GENERAL.useRottenFood) {
			return false;
		}
		for(Predicate<ItemStack> whitelistPredicate : ROTTING_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : ROTTING_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTainting(ItemStack stack) {
		for(Predicate<ItemStack> whitelistPredicate : TAINTING_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : TAINTING_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFlintAndSteelBlocked(ItemStack stack) {
		for(Predicate<ItemStack> whitelistPredicate : FLINT_AND_STEEL_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : FLINT_AND_STEEL_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBonemealBlocked(ItemStack stack) {
		for(Predicate<ItemStack> whitelistPredicate : BONEMEAL_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : BONEMEAL_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isToolWeakened(ItemStack stack) {
		for(Predicate<ItemStack> whitelistPredicate : TOOL_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : TOOL_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}
}