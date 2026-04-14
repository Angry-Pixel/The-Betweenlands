package thebetweenlands.api.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.capability.BLCapabilities;
import thebetweenlands.api.capability.corrosion.ICorrosionHandler;
import thebetweenlands.api.capability.corrosion.ICorrosionHandlerModifiable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.datagen.tags.BLBiomeTagProvider;
import thebetweenlands.common.datagen.tags.BLDimensionTypeTagProvider;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.AttributeRegistry;

public class CorrosionHelper {

	/**
	 * The number of corrosion stages, used for the item model overrides
	 */
	public static final int CORROSION_STAGE_COUNT = 6;

	/**
	 * The maximum number of corrosion points a tool can have
	 */
	public static final int MAX_CORROSION = 255;

	/**
	 * The maximum number of coating points a tool can have
	 */
	public static final int MAX_COATING = 600;


	/**
	 * Returns the maximum corrosion of the specified item, or -1 if it can't be corroded.
	 * @param stack
	 * @return
	 */
	public static int getMaximumCorrosion(ItemStack stack) {
		final ICorrosionHandler handler;
		if(!isCorrodible(stack) || (handler = getCorrosionHandler(stack)) == null) {
			return -1;
		}

		return handler.getMaxCorrosion();
	}

	/**
	 * Returns the current corrosion of the specified item, or -1 if it can't be corroded.
	 * @param stack
	 * @return
	 */
	public static int getCorrosion(ItemStack stack) {
		final ICorrosionHandler handler;
		if(!isCorrodible(stack) || (handler = getCorrosionHandler(stack)) == null) {
			return -1;
		}

		return handler.getCorrosion();
	}

	/**
	 * Returns the maximum coating of the specified item, or -1 if it can't be corroded.
	 * @param stack
	 * @return
	 */
	public static int getMaximumCoating(ItemStack stack) {
		final ICorrosionHandler handler;
		if(!isCorrodible(stack) || (handler = getCorrosionHandler(stack)) == null) {
			return -1;
		}

		return handler.getMaxCoating();
	}

	/**
	 * Returns the current coating of the specified item, or -1 if it can't be corroded.
	 * @param stack
	 * @return
	 */
	public static int getCoating(ItemStack stack) {
		final ICorrosionHandler handler;
		if(!isCorrodible(stack) || (handler = getCorrosionHandler(stack)) == null) {
			return -1;
		}

		return handler.getCoating();
	}


	/**
	 * Sets the corrosion on a stack if possible.
	 * @param stack
	 * @param corrosion
	 * @return true if the corrosion could be directly set
	 */
	public static boolean setCorrosion(ItemStack stack, int corrosion) {
		final ICorrosionHandler handler;
		if(!isCorrodible(stack) || (handler = getCorrosionHandler(stack)) == null) {
			return false;
		}

		return setCorrosion(handler, corrosion);
	}
	/**
	 * Sets the coating on a stack.
	 * @param stack
	 * @param coating
	 * @return true if the coating could be directly set
	 */
	public static boolean setCoating(ItemStack stack, int coating) {
		final ICorrosionHandler handler;
		if(!isCorrodible(stack) || (handler = getCorrosionHandler(stack)) == null) {
			return false;
		}

		if(handler instanceof ICorrosionHandlerModifiable modifiable) {
			modifiable.setCoating(coating);
			return true;
		}
		
		return false;
	}

	/**
	 * Attempts to directly set the corrosion on a corrosion handler if possible.
	 * @param handler
	 * @param corrosion
	 * @return true if the corrosion could be directly set
	 */
	public static boolean setCorrosion(ICorrosionHandler handler, int corrosion) {
		if(handler instanceof ICorrosionHandlerModifiable modifiable) {
			modifiable.setCorrosion(corrosion);
			return true;
		}
		
		return false;
	}

	/**
	 * Attempts to directly set the coating on a corrosion handler if possible.
	 * @param handler
	 * @param coating
	 * @return true if the coating could be directly set
	 */
	public static boolean setCoating(ICorrosionHandler handler, int coating) {
		if(handler instanceof ICorrosionHandlerModifiable modifiable) {
			modifiable.setCorrosion(coating);
			return true;
		}
		
		return false;
	}


	/**
	 * Returns a general modifier at the amount corrosion of the specified item
	 * @param stack
	 * @return
	 */
	public static float getModifier(ItemStack stack) {
		final ICorrosionHandler handler;
		if(!isCorrodible(stack) || (handler = getCorrosionHandler(stack)) == null) {
			return 1.0F;
		}

		final int corrosion = handler.getCorrosion();
		final int maxCorrosion = handler.getMaxCorrosion();
		
		int oneStage = maxCorrosion / CORROSION_STAGE_COUNT;
		return (-0.7F * Math.max(0, ((corrosion - oneStage) / (float)(maxCorrosion - oneStage))) + 1);
	}

	public static boolean isCorrodible(ItemStack stack) {
		return !stack.isEmpty() && stack.is(BLItemTagProvider.CORRODIBLE);
	}

	public static @Nullable ICorrosionHandler getCorrosionHandler(ItemStack stack) {
		if(isCorrodible(stack)) {
			return stack.getCapability(BLCapabilities.CorrosionHandler.ITEM);
		}
		return null;
	}

	/**
	 * Returns the dig speed of an item at the amount of corrosion of the specified item
	 * @param normalStrength
	 * @param itemStack
	 * @param blockState
	 * @return
	 */
	public static float getDestroySpeed(float normalStrength, ItemStack itemStack, BlockState blockState) {
		return normalStrength * getModifier(itemStack);
	}

	/**
	 * Returns whether corrosion is enabled
	 * @return
	 */
	public static boolean isCorrosionEnabled(@Nullable Level level) {
		if (level == null) return false;
		return level.getGameRules().getBoolean(TheBetweenlands.CORROSION_GAMERULE) && BetweenlandsConfig.useCorrosion;
	}


	/**
	 * Returns whether an entity should have items in its inventory corroded
	 * @return
	 */
	public static boolean shouldEntityCorrode(Entity entity) {
		// If corrosion is disabled: false
		Level level = entity.level();
		if(!isCorrosionEnabled(level))
			return false;

		// If player is in creative: false
		if(entity instanceof Player && ((Player)entity).isCreative())
			return false;

		Holder<Biome> biome = level.getBiome(entity.blockPosition());
		// If the biome disables corrosion: false
		if(biome.is(BLBiomeTagProvider.DISABLE_CORROSION))
			return false;

		// If the biome enables corrosion (outside of the Betweenlands): true
		if(biome.is(BLBiomeTagProvider.CORRODING_AURA))
			return true;

		// If the dimension enables corrosion: true
		return level.dimensionTypeRegistration().is(BLDimensionTypeTagProvider.CORRODING_AURA);
	}

	/**
	 * Calculates the probability of an item increasing in corrosion
	 * @param stack
	 * @param world
	 * @param holder
	 * @param isHeldItem
	 * @return the probability of the stack's corrosion increasing
	 */
	public static float getCorrosionProbability(@Nullable ItemStack stack, @Nullable Level world, @Nullable Entity holder, boolean isHeldItem) {
		if(holder == null) {
			return 0.0007F;
		}
		float probability = holder.isInWater() ? 0.0014F : 0.0007F;
		if (holder instanceof Player player) {
			probability *= (isHeldItem && !player.getMainHandItem().isEmpty() ? 2.8F : 1.0F);
			float playerCorruption = player.getData(AttachmentRegistry.DECAY).getDecayLevel(player) / 20.0F;
			probability *= (float) (1 - Math.pow(playerCorruption, 2) * 0.9F);
		}
		if (holder instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(AttributeRegistry.CORROSION_RESISTANCE)) {
			probability *= 1.0 - livingEntity.getAttributeValue(AttributeRegistry.CORROSION_RESISTANCE);
		}
		return probability;
	}

	/**
	 * Updates the corrosion on the specified item
	 * @param stack
	 * @param world
	 * @param holder
	 * @param slot
	 * @param isHeldItem
	 */
	public static void updateCorrosion(ItemStack stack, Level world, Entity holder, int slot, boolean isHeldItem) {
		if (world.isClientSide()) {
			return;
		}
		
		if(!shouldEntityCorrode(holder) || !isCorrodible(stack)) {
			return;
		}
		
		ICorrosionHandler handler = stack.getCapability(BLCapabilities.CorrosionHandler.ITEM);
		if(handler == null) {
			return;
		}
		
		int corrosion = handler.getCorrosion();
		if(!isCorrosionEnabled(world)) {
			if(corrosion != 0) {
				boolean directlySetCorrosion = setCorrosion(handler, 0);
				if(!directlySetCorrosion) {
					handler.removeCorrosion(corrosion, false);
				}
			}
		} else if (corrosion < handler.getMaxCorrosion()) {
			float probability = getCorrosionProbability(stack, world, holder, isHeldItem);
			if (world.getRandom().nextFloat() < probability) {
				handler.corrode(1, false);
			}
		}
	}

	/**
	 * Adds the corrosion tooltips
	 * @param stack
	 * @param lines
	 * @param tooltipFlags
	 * @param tooltipContext
	 */
	public static void addCorrosionTooltips(ItemStack stack, List<Component> lines, TooltipFlag tooltipFlags, TooltipContext tooltipContext) {
		if(!isCorrodible(stack)) {
			return;
		}

		boolean advancedItemTooltips = tooltipFlags.isAdvanced();

		int tooltipIndex = 1;
		if(isCorrosionEnabled(tooltipContext.level())) {
			MutableComponent mutableComponent = MutableComponent.create(Component.translatable("item.thebetweenlands.corrosion." + getCorrosionStage(stack)).getContents());
			if (advancedItemTooltips) {
				String corrosionInfo = " (" +
					getCorrosion(stack) +
					"/" + getMaximumCorrosion(stack) + ")";
				mutableComponent.append(corrosionInfo);
			}
			lines.add(tooltipIndex++, mutableComponent.withStyle(ChatFormatting.GRAY));
		}

		int coating = getCoating(stack);
		if(coating > 0 || advancedItemTooltips) {
			MutableComponent mutableComponent = MutableComponent.create(Component.translatable("item.thebetweenlands.coated." + getCoatingStage(stack)).getContents());
			if (advancedItemTooltips) {
				String corrosionInfo = " (" +
					coating +
					"/" + getMaximumCoating(stack) + ")";
				mutableComponent.append(corrosionInfo);
			}
			lines.add(tooltipIndex, mutableComponent.withStyle(ChatFormatting.GRAY));
		}
	}

	/**
	 * Returns the corrosion stage of the specified item. Ranges from [0, 5]
	 * @param stack
	 * @return
	 */
	public static int getCorrosionStage(ItemStack stack) {
		int corrosion = getCorrosion(stack);
		int maxCorrosion = getMaximumCorrosion(stack);
		if(corrosion != -1 && maxCorrosion != -1)
			return Math.min(5, (int)Math.floor((float)corrosion / (float)maxCorrosion * 6));
		return 0;
	}

	/**
	 * Returns the coating stage of the specified item. Ranges from [0, 5]
	 * @param stack
	 * @return
	 */
	public static int getCoatingStage(ItemStack stack) {
		int coating = getCoating(stack);
		int maxCoating = getMaximumCoating(stack);
		if(coating != -1 && maxCoating != -1)
			return Math.min(5, (int)Math.floor((float)coating / (float)maxCoating * 6));
		return 0;
	}


}
