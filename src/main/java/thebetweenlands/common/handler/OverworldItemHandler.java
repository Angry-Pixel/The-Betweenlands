package thebetweenlands.common.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.item.OriginalItemData;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.ItemRegistry;

@EventBusSubscriber(modid = TheBetweenlands.ID, bus = Bus.GAME)
public class OverworldItemHandler {

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
		public default boolean onTorchItemPlaced(LevelAccessor world, BlockPos pos, ItemStack stack, Entity player) {
			return false;
		}

		/**
		 * Called when a torch is placed, by default replaces the block with a damp torch
		 * @param world
		 * @param pos
		 * @param block
		 * @param player
		 * @param blockSnapshot 
		 */
		public default boolean onTorchBlockPlaced(LevelAccessor world, BlockPos pos, BlockState state, ItemStack stack, Entity player, BlockSnapshot blockSnapshot) {
			return true;
		}
	}

	public static final Map<ResourceLocation, Predicate<ItemStack>> ROTTING_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> ROTTING_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> TAINTING_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> TAINTING_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> CORROSION_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> CORROSION_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> FIRE_TOOL_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> FIRE_TOOL_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> FERTILIZER_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> FERTILIZER_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> TOOL_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> TOOL_BLACKLIST = new HashMap<>();

	public static final Map<ResourceLocation, Predicate<ItemStack>> TORCH_WHITELIST = new HashMap<>();
	public static final Map<ResourceLocation, Predicate<ItemStack>> TORCH_BLACKLIST = new HashMap<>();
	public static final Map<ResourceLocation, ITorchPlaceHandler> TORCH_PLACE_HANDLERS = new HashMap<>();
	
	static {
		ROTTING_WHITELIST.put(TheBetweenlands.prefix("config_whitelist"), stack -> BetweenlandsConfig.Overworld.rottenFoodWhitelist.isListed(stack));
		ROTTING_BLACKLIST.put(TheBetweenlands.prefix("config_blacklist"), stack -> BetweenlandsConfig.Overworld.rottenFoodBlacklist.isListed(stack));
		ROTTING_BLACKLIST.put(TheBetweenlands.prefix("default_blacklist"), stack -> {
			final Item item = stack.getItem();
			if(item == Items.CAKE || Block.byItem(item) == Blocks.CAKE) {
				return true;
			}
			if(stack.has(DataComponents.FOOD) && !stack.is(BLItemTagProvider.DOES_NOT_ROT) && !isInRegister(ItemRegistry.ITEMS, stack.getItemHolder())) {
				return true;
			}
			return false;
		});

		TAINTING_WHITELIST.put(TheBetweenlands.prefix("config_whitelist"), stack -> BetweenlandsConfig.Overworld.taintingWhitelist.isListed(stack));
		TAINTING_BLACKLIST.put(TheBetweenlands.prefix("config_blacklist"), stack -> BetweenlandsConfig.Overworld.taintingBlacklist.isListed(stack));
		TAINTING_BLACKLIST.put(TheBetweenlands.prefix("default_blacklist"), stack -> stack.getItem() instanceof PotionItem /*item && !(item instanceof ElixirItem)*/);
		
		FIRE_TOOL_WHITELIST.put(TheBetweenlands.prefix("config_whitelist"), stack -> BetweenlandsConfig.Overworld.fireToolWhitelist.isListed(stack));
		FIRE_TOOL_BLACKLIST.put(TheBetweenlands.prefix("config_blacklist"), stack -> BetweenlandsConfig.Overworld.fireToolBlacklist.isListed(stack));
		FIRE_TOOL_BLACKLIST.put(TheBetweenlands.prefix("default_blacklist"), stack -> stack.getItem() instanceof FlintAndSteelItem);

		FERTILIZER_WHITELIST.put(TheBetweenlands.prefix("config_whitelist"), stack -> BetweenlandsConfig.Overworld.fertilizerWhitelist.isListed(stack));
		FERTILIZER_BLACKLIST.put(TheBetweenlands.prefix("config_blacklist"), stack -> BetweenlandsConfig.Overworld.fertilizerBlacklist.isListed(stack));
		FERTILIZER_BLACKLIST.put(TheBetweenlands.prefix("default_blacklist"), stack -> stack.getItem() == Items.BONE_MEAL);

		TOOL_WHITELIST.put(TheBetweenlands.prefix("config_whitelist"), stack -> BetweenlandsConfig.Overworld.toolWeaknessWhitelist.isListed(stack));
		TOOL_BLACKLIST.put(TheBetweenlands.prefix("config_blacklist"), stack -> BetweenlandsConfig.Overworld.toolWeaknessBlacklist.isListed(stack));
		TOOL_BLACKLIST.put(TheBetweenlands.prefix("default_blacklist"), stack -> {
			final Item item = stack.getItem();
			return (stack.has(DataComponents.TOOL) || item instanceof SwordItem || item instanceof ProjectileWeaponItem) &&
					!stack.is(BLItemTagProvider.IGNORES_TOOL_WEAKNESS) && 
//					stack.getItem() instanceof ItemBLSword == false && 
//					stack.getItem() instanceof ItemBLAxe == false && 
//					stack.getItem() instanceof ItemBLPickaxe == false && 
//					stack.getItem() instanceof ItemBLShovel == false &&
//					stack.getItem() instanceof ItemBLBow == false &&
					!isInRegister(ItemRegistry.ITEMS, stack.getItemHolder());
		});

		TORCH_WHITELIST.put(TheBetweenlands.prefix("config_whitelist"), stack -> BetweenlandsConfig.Overworld.torchWhitelist.isListed(stack));
		TORCH_BLACKLIST.put(TheBetweenlands.prefix("config_blacklist"), stack -> BetweenlandsConfig.Overworld.torchBlacklist.isListed(stack));
		TORCH_BLACKLIST.put(TheBetweenlands.prefix("default_blacklist"), stack -> {
			Block block = Block.byItem(stack.getItem());
			return block instanceof BaseTorchBlock && !isInRegister(BlockRegistry.BLOCKS, block.builtInRegistryHolder());
		});

		ITorchPlaceHandler vanillaTorchPlaceHandler = new ITorchPlaceHandler() {
			@Override
			public ResourceLocation getID() {
				return TheBetweenlands.prefix("vanilla_torch");
			}

			@Override
			public boolean isTorchItem(ItemStack stack) {
				return isTorchTurningDamp(stack);
			}

			@Override
			public boolean onTorchBlockPlaced(LevelAccessor world, BlockPos pos, BlockState state, ItemStack stack, Entity player, BlockSnapshot blockSnapshot) {
				if(state.getBlock() instanceof BaseTorchBlock == false) return false;
				BlockState dampTorch;
				Optional<Direction> faceValue = state.getOptionalValue(HorizontalDirectionalBlock.FACING);
				if(faceValue.isPresent()) {
					Direction facing = faceValue.get();
					dampTorch = BlockRegistry.DAMP_WALL_TORCH.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing);
				} else {
					dampTorch = BlockRegistry.DAMP_TORCH.get().defaultBlockState();
				}
//				Direction facing = null;
//				try {
//					facing = state.getValue(HorizontalDirectionalBlock.FACING);
//				} catch(Exception ex) {}
//				if(facing == null) {
////					List<Direction> dirs = new ArrayList<>();
////					Collections.addAll(dirs, Direction.values());
////					Collections.shuffle(dirs, new Random(world.getRandom().nextLong()));
////					for(Direction dir : dirs) {
////						if(dir != Direction.DOWN) {
//////							BlockState offsetState = world.getBlockState(pos.relative(dir.getOpposite()));
////							if((dir == Direction.UP && Block.canSupportCenter(world, pos.relative(dir.getOpposite()), Direction.UP)) || state.isFaceSturdy(world, pos.relative(dir.getOpposite()), dir)) {
////								facing = dir;
////								break;
////							}
////						}
////					}
//					facing = Direction.UP;
//				}
//				if(facing != null) {
//					BlockState dampTorch = BlockRegistry.DAMP_TORCH.get().defaultBlockState().setValue(BlockStateProperties.FACING, facing);
					world.setBlock(pos, dampTorch, 3);
//				} else {
////					BlockState dampTorch = BlockRegistry.DAMP_TORCH.get().defaultBlockState();
////					world.setBlock(pos, dampTorch, 4 | 16 | 32);
////					world.destroyBlock(pos, true);
//					world.removeBlock(pos, true);
//					world.addFreshEntity(new ItemEntity(player.level(), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, new ItemStack(ItemRegistry.DAMP_TORCH.get())));
//				}
				
				world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.2F, 1.0F);
				if(player instanceof ServerPlayer serverPlayer) {
					AdvancementCriteriaRegistry.DAMP_TORCH_PLACED.get().trigger(serverPlayer);
				}
				return false;
			}
		};
		TORCH_PLACE_HANDLERS.put(vanillaTorchPlaceHandler.getID(), vanillaTorchPlaceHandler);
	}
	
	private static <T> boolean isInRegister(DeferredRegister<T> register, Holder<T> holder) {
		return holder.getKey().location().getNamespace().equals(register.getNamespace());
	}

	public static boolean isBetweenlands(Level level) {
		return level.dimension().equals(DimensionRegistries.DIMENSION_KEY);
	}
	
	// Torch Handler
	@SubscribeEvent
	public static void onPlayerTorchPlacement(EntityPlaceEvent event) {
		if (isTorchDampeningEnabled(event.getEntity().level())) {
			ItemStack held = event.getEntity().getWeaponItem();
			if(held != null && !held.isEmpty()) {
				for(ITorchPlaceHandler handler : TORCH_PLACE_HANDLERS.values()) {
					if(handler.isTorchItem(held)) {
						if(!handler.onTorchItemPlaced(event.getLevel(), event.getPos(), held, event.getEntity())) {
							if(handler.onTorchBlockPlaced(event.getLevel(), event.getPos(), event.getState(), held, event.getEntity(), event.getBlockSnapshot())) {
							event.setCanceled(true);
							}
						}
						break;
					}
				}
			}
		}
	}

	public static boolean isTorchDampeningEnabled(Level level) {
		return isBetweenlands(level) && BetweenlandsConfig.useTorchBlacklist && level.getGameRules().getBoolean(TheBetweenlands.TORCH_BLACKLIST_GAMERULE);
	}
	
	public static boolean isTorchTurningDamp(ItemStack stack) {
		for(Predicate<ItemStack> whitelistPredicate : TORCH_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : TORCH_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}
	
	// Fire tool & bonemeal blacklist
	@SubscribeEvent
	public static void onUseItem(PlayerInteractEvent.RightClickBlock event) {
		final Player player = event.getEntity();
		final ItemStack stack = event.getItemStack();
		final Level level = event.getLevel();
		if(!stack.isEmpty()) {
			if(isFireToolBlockingEnabled(level) && isFireToolBlocked(stack)) {
				event.setCancellationResult(InteractionResult.FAIL);
				event.setCanceled(true);
				if(level.isClientSide()) {
					player.displayClientMessage(Component.translatable("chat.flintandsteel", Component.translatable(stack.getDescriptionId())), true);
				}
			} else if(isFertilizerBlockingEnabled(level) && isFertilizerBlocked(stack)) {
				event.setCancellationResult(InteractionResult.FAIL);
				event.setCanceled(true);
				if(level.isClientSide()) {
					player.displayClientMessage(Component.translatable("chat.fertilizer", Component.translatable(stack.getDescriptionId())), true);
				}
			}
		}
	}

	public static boolean isFireToolBlockingEnabled(Level level) {
		return isBetweenlands(level) && BetweenlandsConfig.useFireToolBlacklist && level.getGameRules().getBoolean(TheBetweenlands.FIRE_TOOL_GAMERULE);
	}
	
	public static boolean isFireToolBlocked(ItemStack stack) {
		for(Predicate<ItemStack> whitelistPredicate : FIRE_TOOL_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : FIRE_TOOL_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}
	
	// Bonemeal
	@SubscribeEvent
	public static void onBonemeal(BonemealEvent event) {
		final Player player = event.getPlayer();
		final ItemStack stack = event.getStack();
		final Level level = event.getLevel();
		if(!stack.isEmpty() && isFertilizerBlockingEnabled(level) && isFertilizerBlocked(stack)) {
			event.setCanceled(true);
			if(level.isClientSide()) {
				player.displayClientMessage(Component.translatable("chat.fertilizer", Component.translatable(stack.getDescriptionId())), true);
			}
		}
	}

	public static boolean isFertilizerBlockingEnabled(Level level) {
		return isBetweenlands(level) && BetweenlandsConfig.useFertilizerBlacklist && level.getGameRules().getBoolean(TheBetweenlands.FERTILIZER_GAMERULE);
	}
	
	public static boolean isFertilizerBlocked(ItemStack stack) {
		for(Predicate<ItemStack> whitelistPredicate : FERTILIZER_WHITELIST.values()) {
			if(whitelistPredicate.test(stack)) {
				return false;
			}
		}
		for(Predicate<ItemStack> blacklistPredicate : FERTILIZER_BLACKLIST.values()) {
			if(blacklistPredicate.test(stack)) {
				return true;
			}
		}
		return false;
	}

	// Tool Weakness
	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		final Player player = event.getEntity();
		if(player != null && isToolWeaknessEnabled(player.level()) && isToolWeakened(player.getMainHandItem())) {
			event.setNewSpeed(event.getNewSpeed() * 0.3F);
		}
	}
	
	// Tool Weakness
	// We no longer have ArmSwingSpeedEvent, so we have to use this
	@SubscribeEvent
	public static void updateArmSwingSpeed(PlayerTickEvent.Post event) {
		final Player player = event.getEntity();
//		if(player != null && isToolWeaknessEnabled(player.level())&& player.swinging && isToolWeakened(player.getItemInHand(player.swingingArm))) {
		if(player != null && isToolWeaknessEnabled(player.level()) && isToolWeakened(player.getMainHandItem())) {
			float delta = player.attackAnim - player.oAttackAnim;
			if(delta < 0) {
				delta++;
			}
			player.attackAnim = (player.oAttackAnim + delta * 0.3F) % 1;
		}
	}
	
	public static boolean isToolWeaknessEnabled(Level level) {
		return isBetweenlands(level) && BetweenlandsConfig.useToolWeakness && level.getGameRules().getBoolean(TheBetweenlands.TOOL_WEAKNESS_GAMERULE);
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
	
	// Rotten food & Tainted Potions
	@SubscribeEvent
	public static void onEntitySpawn(EntityJoinLevelEvent event) {
		if(event.getEntity() instanceof Player player) {
			updatePlayerInventory(player);
		} else if(event.getEntity() instanceof ContainerEntity containerEntity) {
			updateInventoryContents(containerEntity, event.getLevel());
		}
	}

	// Rotten food & Tainted Potions
	@SubscribeEvent
	public static void onPlayerTick(EntityTickEvent.Post event) {
		if(!event.getEntity().level().isClientSide() && event.getEntity().tickCount % 5 == 0) {
			if(event.getEntity() instanceof Player player) {
				updatePlayerInventory(player);
			} else if(event.getEntity() instanceof ContainerEntity containerEntity) {
				updateInventoryContents(containerEntity, containerEntity.level());
			}
		}
	}

	// Rotten food & Tainted Potions
	@SubscribeEvent
	public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
		final Player player = event.getPlayer();
		final ItemEntity itemEntity = event.getItemEntity();
		final Level level;
		if(player != null && itemEntity != null && !(level = player.level()).isClientSide()) {
			final ItemStack stack = itemEntity.getItem();
			if(!stack.isEmpty()) {
				if(!isBetweenlands(level) || player.isCreative()) {
					final ItemStack originalStack = getOriginalStackScaled(stack);
					if(!originalStack.isEmpty()) {
						itemEntity.setItem(originalStack);
					}
				} else {
					final Item item = stack.getItem();
					final Item ROTTEN_FOOD = ItemRegistry.ROTTEN_FOOD.get();
					final Item TAINTED_POTION = ItemRegistry.TAINTED_POTION.get();
					final boolean checkRotting = isRotEnabled(level);
					final boolean checkTainting = isPotionTaintingEnabled(level);
					
					if(item == ROTTEN_FOOD) {
						final ItemStack originalStack = getOriginalStackScaled(stack);
						if(!originalStack.isEmpty() && (!checkRotting || !isRotting(originalStack))) {
							itemEntity.setItem(originalStack);
						}
					} else if(stack.getItem() == TAINTED_POTION) {
						final ItemStack originalStack = getOriginalStackScaled(stack);
						if(!originalStack.isEmpty() && (!checkTainting || !isTainting(originalStack))) {
							itemEntity.setItem(originalStack);
						}
					} else if(isRotting(stack)) {
						ItemStack rottenFoodStack = new ItemStack(ROTTEN_FOOD, stack.getCount());
						setOriginalStack(rottenFoodStack, stack);
						itemEntity.setItem(rottenFoodStack);
					} else if(isTainting(stack)) {
						ItemStack taintedPotionStack = new ItemStack(TAINTED_POTION, stack.getCount());
						setOriginalStack(taintedPotionStack, stack);
						itemEntity.setItem(taintedPotionStack);
					}
				}
				
			}
		}
	}
	
	public static void updatePlayerInventory(Player player) {
		final Level level = player.level();
		if(isBetweenlands(level) && !player.isCreative()) {
			rotInventoryContents(player.getInventory(), level);
		} else {
			revertInventoryContents(player.getInventory());
		}
	}
	
	public static void updateInventoryContents(Container container, Level level) {
		if(isBetweenlands(level)) {
			rotInventoryContents(container, level);
		} else {
			revertInventoryContents(container);
		}
	}
	
	public static void setOriginalStack(ItemStack containerStack, ItemStack originalStack) {
		containerStack.set(DataComponentRegistry.ROTTEN_FOOD, new OriginalItemData(originalStack.copyWithCount(1)));
	}

	// "Don't pay for what you don't use" - There is a @Notnull annotation on OriginalItemData's originalStack
	public static ItemStack getOriginalStack(ItemStack containerStack) {
		return containerStack.getOrDefault(DataComponentRegistry.ROTTEN_FOOD, OriginalItemData.EMPTY).originalStack();
	}

	public static ItemStack getOriginalStackScaled(ItemStack containerStack) {
		final ItemStack originalStack = getOriginalStack(containerStack);
		return originalStack.isEmpty() ? originalStack : originalStack.copyWithCount(containerStack.getCount());
	}
	
	/**
	 * Rot food and taint potions in the specified container
	 * @param container
	 */
	private static void rotInventoryContents(Container container, Level level) {
		// Optimize by using final (there are some very large containers we could be iterating over)
		final DataComponentType<OriginalItemData> ROTTEN_FOOD_COMPONENT = DataComponentRegistry.ROTTEN_FOOD.get();
		final Item ROTTEN_FOOD = ItemRegistry.ROTTEN_FOOD.get();
		final Item TAINTED_POTION = ItemRegistry.TAINTED_POTION.get();
		final int containerSize = container.getContainerSize();
		final boolean checkRotting = isRotEnabled(level);
		final boolean checkTainting = isPotionTaintingEnabled(level);
		
		for(int slot = 0; slot < containerSize; ++slot) {
			final ItemStack stack = container.getItem(slot);
			if(stack != null && !stack.isEmpty()) {
				final int count = stack.getCount();
				final Item item = stack.getItem();
				// Revert items that shouldn't be rotted any more (most likely due to a config change)
				if(item == ROTTEN_FOOD) {
					final ItemStack originalStack = getOriginalStackScaled(stack);
					if(!originalStack.isEmpty() && (!checkRotting || !isRotting(originalStack))) {
						container.setItem(slot, originalStack);
					}
				} else if(item == TAINTED_POTION) {
					final ItemStack originalStack = getOriginalStackScaled(stack);
					if(!originalStack.isEmpty() && (!checkTainting || !isTainting(originalStack))) {
						container.setItem(slot, originalStack);
					}
				} // else so rotten food/tainted potions don't stack inside themselves forever 
				else if(checkRotting && isRotting(stack)) {
					final ItemStack rottenFoodStack = new ItemStack(ROTTEN_FOOD, count);
					rottenFoodStack.set(ROTTEN_FOOD_COMPONENT, new OriginalItemData(stack.copyWithCount(1)));
					container.setItem(slot, rottenFoodStack);
				} else if(checkTainting && isTainting(stack)) {
					final ItemStack taintedPotionStack = new ItemStack(TAINTED_POTION, count);
					taintedPotionStack.set(ROTTEN_FOOD_COMPONENT, new OriginalItemData(stack.copyWithCount(1)));
					container.setItem(slot, taintedPotionStack);
				}
			}
		}
	}

	/**
	 * Revert rotten food/tainted potions in the specified container
	 * @param container
	 */
	private static void revertInventoryContents(Container container) {
		final DataComponentType<OriginalItemData> ROTTEN_FOOD_COMPONENT = DataComponentRegistry.ROTTEN_FOOD.get();
		final int containerSize = container.getContainerSize();
		for(int slot = 0; slot < containerSize; ++slot) {
			final ItemStack stack = container.getItem(slot);
			if(stack != null && !stack.isEmpty() && stack.has(ROTTEN_FOOD_COMPONENT)) {
				container.setItem(slot, getOriginalStackScaled(stack));
			}
		}
	}
	
	public static boolean isRotEnabled(Level level) {
		return isBetweenlands(level) && BetweenlandsConfig.useRottenFood && level.getGameRules().getBoolean(TheBetweenlands.ROTTEN_FOOD_GAMERULE);
	}
	
	public static boolean isRotting(ItemStack stack) {
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

	public static boolean isPotionTaintingEnabled(Level level) {
		return isBetweenlands(level) &&BetweenlandsConfig.usePotionBlacklist && level.getGameRules().getBoolean(TheBetweenlands.POTION_GAMERULE);
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

}
