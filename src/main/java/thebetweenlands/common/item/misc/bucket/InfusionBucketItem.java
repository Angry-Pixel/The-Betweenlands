package thebetweenlands.common.item.misc.bucket;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.component.item.InfusionBucketData;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.registries.DataComponentRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class InfusionBucketItem extends Item {
	public InfusionBucketItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getPlayer().isShiftKeyDown()) {
			Level level = context.getLevel();
			BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
			level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, context.getLevel().getRandom().nextFloat() * 0.4F + 0.8F);
			for (int i = 0; i < 50; i++) {
				context.getLevel().addParticle(ParticleTypes.SMOKE, pos.getX() + level.getRandom().nextFloat(), pos.getY() + level.getRandom().nextFloat(), pos.getZ() + level.getRandom().nextFloat(), 0, 0, 0);
			}
			context.getPlayer().setItemInHand(context.getHand(), context.getItemInHand().getCraftingRemainingItem());
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return super.useOn(context);
	}

	@Nullable
	public static Holder<ElixirRecipe> getInfusionElixirRecipe(ItemStack stack, HolderLookup.Provider registries) {
		return ElixirRecipe.getFromAspects(getInfusingAspects(stack, registries), registries);
	}

	public static List<Holder<AspectType>> getInfusingAspects(ItemStack stack, HolderLookup.Provider registries) {
		List<Holder<AspectType>> infusingAspects = new ArrayList<>();
		List<ItemStack> stacks = stack.getOrDefault(DataComponentRegistry.INFUSION_BUCKET_DATA, InfusionBucketData.EMPTY).ingredients();
		for (ItemStack currentStack : stacks) {
			for (Aspect aspect : AspectContents.getAllAspectsForItem(currentStack, registries, AspectManager.get(BetweenlandsClient.getClientLevel()))) {
				infusingAspects.add(aspect.type());
			}
			//infusingAspects.addAll(AspectManager.get(TheBetweenlands.proxy.getClientWorld()).getDiscoveredAspectTypes(AspectManager.getAspectItem(ingredient), null));
		}
		return infusingAspects;
	}

	public static int getInfusionTime(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.INFUSION_BUCKET_DATA, InfusionBucketData.EMPTY).infusionTime();
	}

	public static int getColor(ItemStack stack) {
		Holder<ElixirRecipe> holder = getInfusionElixirRecipe(stack, BetweenlandsClient.getClientLevel().registryAccess());
		int infusionTime = getInfusionTime(stack);
		//Infusion liquid
		if (holder != null) {
			ElixirRecipe recipe = holder.value();
			if (infusionTime > recipe.idealInfusionTime() + recipe.infusionTimeVariation()) {
				return recipe.infusionFailedColor();
			} else if (infusionTime > recipe.idealInfusionTime() - recipe.infusionTimeVariation()
				&& infusionTime < recipe.idealInfusionTime() + recipe.infusionTimeVariation()) {
				return recipe.infusionFinishedColor();
			} else {
				float startR = 0.2F;
				float startG = 0.6F;
				float startB = 0.4F;
				float startA = 0.9F;
				float[] targetColor = recipe.getRGBA(recipe.infusionGradient());
				int targetTime = recipe.idealInfusionTime() - recipe.infusionTimeVariation();
				float infusingPercentage = (float) infusionTime / (float) targetTime;
				float interpR = startR + (targetColor[0] - startR) * infusingPercentage;
				float interpG = startG + (targetColor[1] - startG) * infusingPercentage;
				float interpB = startB + (targetColor[2] - startB) * infusingPercentage;
				float interpA = startA + (targetColor[3] - startA) * infusingPercentage;
				return FastColor.ARGB32.colorFromFloat(interpA, interpR, interpG, interpB);
			}
		} else {
			return FastColor.ARGB32.colorFromFloat(1.0F, 0.8F, 0.0F, 0.8F);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		InfusionBucketData data = stack.get(DataComponentRegistry.INFUSION_BUCKET_DATA);
		if (data != null) {
			data.addTooltipInformation(context, tooltip, flag);
		} else {
			tooltip.add(Component.translatable("item.thebetweenlands.infusion_bucket.empty").withStyle(ChatFormatting.GRAY));
		}
	}
}
