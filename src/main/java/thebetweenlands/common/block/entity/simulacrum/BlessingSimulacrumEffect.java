package thebetweenlands.common.block.entity.simulacrum;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.block.SimulacrumEffect;
import thebetweenlands.common.block.entity.OfferingTableBlockEntity;
import thebetweenlands.common.component.entity.BlessingData;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlessingSimulacrumEffect implements SimulacrumEffect {
	@Override
	public void executeEffect(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity) {
		if (level.getGameTime() % 4 == 0) {
			Player player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5D, pos.getZ() + 0.5D, 4, e -> {
				if (!e.isSpectator() && e.hasData(AttachmentRegistry.BLESSING)) {
					BlessingData cap = e.getData(AttachmentRegistry.BLESSING);
					return (!cap.isBlessed() || cap.getBlessingDimension() != e.level().dimension() || !pos.equals(cap.getBlessingLocation()));
				}
				return false;
			});

			if (player != null) {
				OfferingTableBlockEntity offering = SimulacrumBlockEntity.getClosestActiveTile(OfferingTableBlockEntity.class, null, level, player.getX(), player.getY(), player.getZ(), 2.5f, null, stack -> !stack.isEmpty() && stack.is(ItemRegistry.SPIRIT_FRUIT));

				if (offering != null) {
					if (!level.isClientSide() && level.getRandom().nextInt(40) == 0) {
						BlessingData cap = player.getData(AttachmentRegistry.BLESSING);
						ItemStack stack = offering.getTheItem();
						stack.shrink(1);
						offering.setTheItem(stack);
						cap.setBlessed(player.level().dimension(), pos);
						player.displayClientMessage(Component.translatable("chat.simulacrum.blessed"), true);
					} else if (level.isClientSide()) {
						this.spawnBlessingParticles(level.getGameTime() * 0.025f, offering.getBlockPos().getX() + 0.5f, offering.getBlockPos().getY() + 0.4f, offering.getBlockPos().getZ() + 0.5f);
					}
				}
			}
		}
	}

	private void spawnBlessingParticles(float rot, float x, float y, float z) {
		float step = Mth.PI * 2 / 20;
		for (int i = 0; i < 20; i++) {
			float dx = (float) Math.cos(rot + step * i);
			float dz = (float) Math.sin(rot + step * i);

//			BLParticles.CORRUPTED.spawn(this.getLevel(), x, y, z, ParticleArgs.get().withMotion(dx * 0.05f, 0.2f, dz * 0.05f).withData(80, true, 0.1f, true));
		}
	}
}
