package thebetweenlands.common.item.recipe.censer;

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
import thebetweenlands.common.component.item.ElixirContents;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.List;

public class ElixirCenserRecipe extends AbstractCenserRecipe<ElixirCenserRecipe.CenserRecipeElixirContext> {

	@Override
	public boolean matchesInput(ItemStack stack) {
		ElixirContents contents = stack.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY);
		if (contents.elixir().isPresent()) {
			return !contents.createEffect(contents.elixir().get(), 1.0D).getEffect().value().isInstantenous();
		}
		return false;
	}

	@Override
	public CenserRecipeElixirContext createContext(ItemStack stack) {
		return new CenserRecipeElixirContext(stack);
	}

	private List<LivingEntity> getAffectedEntities(Level level, BlockPos pos, CenserRecipeElixirContext context) {
		int amplifier = context.elixir.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY).strength();

		int xzRange = 35 + amplifier * 15;
		int yRange = 12 + amplifier * 4;

		return level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(xzRange, 1, xzRange).expandTowards(0, yRange, 0));
	}

	@Override
	public int update(CenserRecipeElixirContext context, Censer censer) {
		Level level = censer.getLevel();

		if (level.getGameTime() % 100 == 0) {
			Holder<MobEffect> potion = context.elixir.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY).elixir().get().value().getElixirEffect();

			int maxDuration = context.elixir.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY).duration();

			BlockPos pos = censer.getBlockPos();

			List<LivingEntity> affected = this.getAffectedEntities(level, pos, context);

			if (!level.isClientSide()) {
				for (LivingEntity living : affected) {
					living.addEffect(new MobEffectInstance(potion, Math.min(maxDuration, 300), 0, true, false));
				}
			}

			context.setConsuming(!affected.isEmpty());
		}

		return 0;
	}

	private int getEffectiveDuration(CenserRecipeElixirContext context) {
		return 20 + context.elixir.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY).duration() * 8;
	}

	@Override
	public int getConsumptionDuration(CenserRecipeElixirContext context, Censer censer) {
		return Math.max(1, Mth.floor(this.getEffectiveDuration(context) / 1000.0f));
	}

	@Override
	public int getConsumptionAmount(CenserRecipeElixirContext context, Censer censer) {
		if (!context.isConsuming()) {
			return 0;
		}
		return 1 + Mth.floor(1000.0f / this.getEffectiveDuration(context));
	}

	@Override
	public int getEffectColor(CenserRecipeElixirContext context, Censer censer, CenserRecipe.EffectColorType type) {
		return context.elixir.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY).getElixirColor();
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
