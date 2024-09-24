package thebetweenlands.common.item.recipe.censer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.api.block.Censer;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

public class SwampWaterCenserRecipe extends AbstractCenserRecipe<SwampWaterCenserRecipe.CenserRecipeSwampWaterContext> {

	@Override
	public boolean matchesInput(FluidStack stack) {
		return stack.is(FluidRegistry.SWAMP_WATER_STILL);
	}

	@Override
	public boolean matchesSecondaryInput(ItemStack stack) {
		return stack.is(ItemRegistry.BARK_AMULET);
	}

	@Override
	public CenserRecipeSwampWaterContext createContext(FluidStack stack) {
		return new CenserRecipeSwampWaterContext();
	}

	@Override
	public int getEffectColor(CenserRecipeSwampWaterContext context, Censer censer, EffectColorType type) {
		return 0xFFEEEEEE;
	}

	private List<Player> getAffectedEntities(Level level, BlockPos pos) {
		return level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(45, 1, 45).expandTowards(0, 16, 0));
	}

	@Override
	public int update(CenserRecipeSwampWaterContext context, Censer censer) {
		ItemStack inputStack = censer.getInputStack();

		if(!inputStack.isEmpty() && inputStack.is(ItemRegistry.BARK_AMULET)) {
			Level level = censer.getLevel();

			if(level.getGameTime() % 100 == 0) {
				BlockPos pos = censer.getBlockPos();

				List<Player> affected = this.getAffectedEntities(level, pos);

				if(!level.isClientSide()) {
					for(Player player : affected) {
						player.addEffect(new MobEffectInstance(ElixirEffectRegistry.ENLIGHTENED, 200, 0, true, false));
					}
				}

				context.setConsuming(!affected.isEmpty());
			}
		} else {
			context.setConsuming(false);
		}

		return 0;
	}

	@Override
	public int getConsumptionDuration(CenserRecipeSwampWaterContext context, Censer censer) {
		//7.5min / bucket
		return 9;
	}

	@Override
	public int getConsumptionAmount(CenserRecipeSwampWaterContext context, Censer censer) {
		return context.isConsuming() ? 1 : 0;
	}

	public static class CenserRecipeSwampWaterContext {
		private boolean isConsuming = false;

		public void setConsuming(boolean consuming) {
			this.isConsuming = consuming;
		}

		public boolean isConsuming() {
			return this.isConsuming;
		}
	}
}
