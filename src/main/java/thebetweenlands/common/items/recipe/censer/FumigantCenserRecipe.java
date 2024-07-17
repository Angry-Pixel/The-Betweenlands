package thebetweenlands.common.items.recipe.censer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.block.Censer;
import thebetweenlands.common.component.entity.RotSmellData;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

public class FumigantCenserRecipe extends AbstractCenserRecipe<Void> {

	@Override
	public boolean matchesInput(ItemStack stack) {
		return stack.is(ItemRegistry.FUMIGANT);
	}

	private List<LivingEntity> getAffectedEntities(Level level, BlockPos pos) {
		return level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(32, 1, 32).expandTowards(0, 16, 0));
	}

	@Override
	public int update(Void context, Censer censer) {
		Level level = censer.getLevel();

		if(!world.isClientSide() && level.getGameTime() % 100 == 0) {
			boolean applied = false;

			BlockPos pos = censer.getBlockPos();

			List<LivingEntity> affected = this.getAffectedEntities(world, pos);
			for(LivingEntity living : affected) {
				RotSmellData cap = living.getData(AttachmentRegistry.ROT_SMELL);

				if (cap.getRemainingSmellyTicks(living) > 0) {
					cap.setNotSmellingBad(living);
					cap.setImmune(Math.max(cap.getRemainingImmunityTicks(living), 600));
					applied = true;
				}
			}

			if(applied) {
				return 30;
			}
		}

		return 0;
	}

	@Override
	public int getConsumptionAmount(Void context, Censer censer) {
		return 0;
	}

	@Override
	public int getEffectColor(Void context, Censer censer, EffectColorType type) {
		return 0xFFEDF2F0;
	}
}
