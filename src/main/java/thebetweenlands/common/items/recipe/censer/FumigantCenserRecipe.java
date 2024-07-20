package thebetweenlands.common.items.recipe.censer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

	private List<Player> getAffectedEntities(Level level, BlockPos pos) {
		return level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(32, 1, 32).expandTowards(0, 16, 0));
	}

	@Override
	public int update(Void context, Censer censer) {
		Level level = censer.getLevel();

		if(!level.isClientSide() && level.getGameTime() % 100 == 0) {
			boolean applied = false;

			BlockPos pos = censer.getBlockPos();

			List<Player> affected = this.getAffectedEntities(level, pos);
			for(Player player : affected) {
				RotSmellData cap = player.getData(AttachmentRegistry.ROT_SMELL);

				if (cap.getRemainingSmellyTicks(player) > 0) {
					cap.setNotSmellingBad(player);
					cap.setImmune(player, Math.max(cap.getRemainingImmunityTicks(player), 600));
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
