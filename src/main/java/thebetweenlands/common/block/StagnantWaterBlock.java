package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import thebetweenlands.common.component.entity.RotSmellData;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.AttachmentRegistry;

public class StagnantWaterBlock extends LiquidBlock {
	public StagnantWaterBlock(FlowingFluid fluid, Properties properties) {
		super(fluid, properties);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof Player player && !level.isClientSide()) {
			if (!player.hasEffect(ElixirEffectRegistry.EFFECT_DECAY.get().getElixirEffect())) {
				player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.get().createEffect(60, 3));
			}

			entity.getData(AttachmentRegistry.DECAY).addDecayAcceleration(player, 0.1F);

			RotSmellData capSmell = entity.getData(AttachmentRegistry.ROT_SMELL);
			if (!capSmell.isSmellingBad(player))
				capSmell.setSmellingBad(player, Math.max(capSmell.getRemainingSmellyTicks(player), 24000));
		}
	}
}
