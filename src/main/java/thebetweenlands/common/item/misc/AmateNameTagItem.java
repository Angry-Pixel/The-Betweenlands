package thebetweenlands.common.item.misc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.network.clientbound.OpenRenameScreenPacket;

public class AmateNameTagItem extends NameTagItem {
	public AmateNameTagItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		 // TODO move renaming to an event handler
		if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) { // Don't rename if in offhand, because that renames the mainhand item instead
			if (level.isClientSide()) {
				return InteractionResultHolder.success(stack);
			} else {
				PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenRenameScreenPacket(stack));
				return InteractionResultHolder.consume(stack);
			}
		}
		
		return super.use(level, player, hand);
	}
}
