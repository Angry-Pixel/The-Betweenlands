package thebetweenlands.common.item.misc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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

		if (!level.isClientSide()) {
			if (player.isShiftKeyDown()) {
				PacketDistributor.sendToPlayer((ServerPlayer) player, OpenRenameScreenPacket.INSTANCE);
				return InteractionResultHolder.consume(stack);
			}
		}
		return InteractionResultHolder.success(stack);
	}
}
