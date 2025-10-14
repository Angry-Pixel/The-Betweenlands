package thebetweenlands.common.item.food;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.item.misc.HoverTextItem;
import thebetweenlands.common.registries.SoundRegistry;

public class ForbiddenFigItem extends HoverTextItem {
	public ForbiddenFigItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide() && entity instanceof Player player) {
			player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".eat"), true);
			level.playSound(player, player.blockPosition(), SoundRegistry.FIG.get(), SoundSource.AMBIENT, 0.7F, 0.8F);
		}
		return super.finishUsingItem(stack, level, entity);
	}
}
