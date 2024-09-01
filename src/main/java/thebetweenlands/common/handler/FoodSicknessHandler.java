package thebetweenlands.common.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.DecayData;
import thebetweenlands.common.component.entity.FoodSicknessData;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.items.datamaps.DecayFood;
import thebetweenlands.common.network.ShowFoodSicknessPacket;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.util.FoodSickness;

public class FoodSicknessHandler {
	private static final InteractionHand lastHand = InteractionHand.MAIN_HAND;
	private static ItemStack lastUsedItem = ItemStack.EMPTY;
	private static FoodSickness lastSickness = null;

	public static boolean isFoodSicknessEnabled(Level level) {
		if (level.getGameRules().getBoolean(TheBetweenlands.FOOD_SICKNESS_GAMERULE)) {
			if (level.dimension() == DimensionRegistries.DIMENSION_KEY && BetweenlandsConfig.useFoodSicknessInBetweenlands) {
				return true;
			} else return level.dimension() != DimensionRegistries.DIMENSION_KEY && BetweenlandsConfig.useFoodSicknessOutsideBetweenlands;
		}

		return false;
	}

	public static void tickSicknessClient(ClientTickEvent.Pre event) {
		Player player = BetweenlandsClient.getClientPlayer();
		if (player != null) {
			if (!lastUsedItem.isEmpty() && (player.getItemInHand(lastHand).isEmpty() || !ItemStack.isSameItem(player.getItemInHand(lastHand), lastUsedItem))) {
				lastUsedItem = ItemStack.EMPTY;
				lastSickness = null;
			}
		}
	}

	protected static void addSicknessMessage(Player player, ItemStack item, FoodSickness sickness) {
		if (lastUsedItem.isEmpty() || !ItemStack.isSameItem(lastUsedItem, item) || lastSickness == null || lastSickness != sickness) {
			player.displayClientMessage(sickness.getRandomLine(player.getRandom()), true);
		}
		lastUsedItem = item;
		lastSickness = sickness;
	}

	static void modifyEatingStart(LivingEntityUseItemEvent.Start event) {
		Player player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
		ItemStack itemStack = event.getItem();

		if (player != null && !itemStack.isEmpty() && FoodSicknessHandler.isFoodSicknessEnabled(event.getEntity().level()) && itemStack.is(BLItemTagProvider.GIVES_FOOD_SICKNESS)) {
			FoodSickness sickness = player.getData(AttachmentRegistry.FOOD_SICKNESS).getSickness(itemStack.getItem());

			if (player.level().isClientSide() && sickness == FoodSickness.SICK) {
				addSicknessMessage(player, itemStack, sickness);
			}
		}
	}

	static void modifyEatingSpeed(LivingEntityUseItemEvent.Tick event) {
		//Check if item will be consumed this tick
		if (!event.getEntity().level().isClientSide() && event.getDuration() <= 1) {
			Player player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
			ItemStack itemStack = event.getItem();

			if (player != null && !itemStack.isEmpty() && FoodSicknessHandler.isFoodSicknessEnabled(event.getEntity().level()) && itemStack.is(BLItemTagProvider.GIVES_FOOD_SICKNESS)) {
				FoodSicknessData data = player.getData(AttachmentRegistry.FOOD_SICKNESS);
				Item item = itemStack.getItem();

				FoodSickness lastSickness = data.getLastSickness();

				int prevFoodHatred = data.getFoodHatred(item);
				FoodSickness currentSickness = data.getSickness(item);

				if (player.level().isClientSide()) {
					if (currentSickness != lastSickness && lastSickness == FoodSickness.SICK) {
						addSicknessMessage(player, itemStack, currentSickness);
					}
				}

				int sicknessIncrease = 5;

				if (currentSickness == FoodSickness.SICK) {
					if (item.getFoodProperties(itemStack, player) != null) {
						int foodLevel = item.getFoodProperties(itemStack, player).nutrition();
						double foodLoss = 1.0D / 3.0D * 2.0;

						if (player.level().isClientSide()) {
							//Remove all gained food on client side and wait for sync
							player.getFoodData().eat(-Math.min(Mth.ceil(foodLevel * foodLoss), foodLevel), 0.0F);
						} else {
							int minFoodGain = player.level().getRandom().nextInt(4) == 0 ? 1 : 0;
							player.getFoodData().eat(-Math.min(Mth.ceil(foodLevel * foodLoss), Math.max(foodLevel - minFoodGain, 0)), 0.0F);
						}
					}

					DecayFood decayFoodStats = itemStack.getItem().builtInRegistryHolder().getData(DataMapRegistry.DECAY_FOOD);
					if (decayFoodStats != null) {
						DecayData decayData = player.getData(AttachmentRegistry.DECAY);
						int decayLevel = decayFoodStats.decay();
						double decayLoss = 1.0D / 3.0D * 2.0;

						if (player.level().isClientSide()) {
							//Remove all gained decay on client side and wait for sync
							decayData.addStats(player, -Math.min(Mth.ceil(decayLevel * decayLoss), decayLevel), 0.0F);
						} else {
							int minDecayGain = player.level().getRandom().nextInt(4) == 0 ? 1 : 0;
							decayData.addStats(player, -Math.min(Mth.ceil(decayLevel * decayLoss), Math.max(decayLevel - minDecayGain, 0)), 0.0F);
						}
					}

					if (!player.level().isClientSide()) {
						data.increaseFoodHatred(player, item, sicknessIncrease, 0);
					}
				} else {
					if (!player.level().isClientSide()) {
						data.increaseFoodHatred(player, item, sicknessIncrease, prevFoodHatred <= 2 * 5 ? 4 : 3);
					}
				}

				FoodSickness newSickness = data.getSickness(item);

				if (!player.level().isClientSide() && player instanceof ServerPlayer sp) {
					if (newSickness != lastSickness) {
						PacketDistributor.sendToPlayer(sp, new ShowFoodSicknessPacket(newSickness));
					}
				}

				data.setLastSickness(newSickness);
			}
		}
	}
}
