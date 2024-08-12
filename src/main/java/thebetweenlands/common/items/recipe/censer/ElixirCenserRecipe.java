package thebetweenlands.common.items.recipe.censer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.block.Censer;
import thebetweenlands.api.recipes.CenserRecipe;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

import com.google.common.collect.Lists;

public class ElixirCenserRecipe extends AbstractCenserRecipe<ElixirCenserRecipe.CenserRecipeElixirContext> {

	@Override
	public boolean matchesInput(ItemStack stack) {
//		if(stack.is(ItemRegistry.ELIXIR)) {
//			ElixirEffect effect = ItemRegistry.ELIXIR.get().getElixirFromItem(stack);
//			return !effect.getElixirEffect().value().isInstantenous();
//		}
		return false;
	}

	@Override
	public CenserRecipeElixirContext createContext(ItemStack stack) {
		return new CenserRecipeElixirContext(stack);
	}

	private List<LivingEntity> getAffectedEntities(Level level, BlockPos pos, CenserRecipeElixirContext context) {
//		int amplifier = ItemRegistry.ELIXIR.get().createPotionEffect(context.elixir, 1).getAmplifier();
//
//		int xzRange = 35 + amplifier * 15;
//		int yRange = 12 + amplifier * 4;

//		return level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(xzRange, 1, xzRange).expandTowards(0, yRange, 0));
		return Lists.newArrayList();
	}

	@Override
	public int update(CenserRecipeElixirContext context, Censer censer) {
//		Level level = censer.getLevel();
//
//		if(level.getGameTime() % 100 == 0) {
//			Holder<MobEffect> potion = ItemRegistry.ELIXIR.get().getElixirFromItem(context.elixir).getPotionEffect();
//
//			int maxDuration = ItemRegistry.ELIXIR.get().createPotionEffect(context.elixir, 0.25D).getDuration();
//
//			BlockPos pos = censer.getBlockPos();
//
//			List<LivingEntity> affected = this.getAffectedEntities(level, pos, context);
//
//			if(!level.isClientSide()) {
//				for(LivingEntity living : affected) {
//					living.addEffect(new MobEffectInstance(potion, Math.min(maxDuration, 300), 0, true, false));
//				}
//			}
//
//			context.setConsuming(!affected.isEmpty());
//		}

		return 0;
	}

	private int getEffectiveDuration(CenserRecipeElixirContext context) {
//		MobEffectInstance effect = ItemRegistry.ELIXIR.get().createPotionEffect(context.elixir, 1.0D);
//		return 20 + effect.getDuration() * 8;
		return 0;
	}

	@Override
	public int getConsumptionDuration(CenserRecipeElixirContext context, Censer censer) {
		return Math.max(1, Mth.floor(this.getEffectiveDuration(context) / 1000.0f));
	}

	@Override
	public int getConsumptionAmount(CenserRecipeElixirContext context, Censer censer) {
		if(!context.isConsuming()) {
			return 0;
		}
		return 1 + Mth.floor(1000.0f / this.getEffectiveDuration(context));
	}

	@Override
	public int getEffectColor(CenserRecipeElixirContext context, Censer censer, CenserRecipe.EffectColorType type) {
		return 0;//((ElixirItem) context.elixir.getItem()).getColorMultiplier(context.elixir, 0);
	}

	public static class CenserRecipeElixirContext {
		public final ItemStack elixir;

		private boolean isConsuming = false;

		CenserRecipeElixirContext(ItemStack elixir) {
			this.elixir = elixir;
		}

		public void setConsuming(boolean consuming) {
			this.isConsuming = consuming;
		}

		public boolean isConsuming() {
			return this.isConsuming;
		}
	}
}
