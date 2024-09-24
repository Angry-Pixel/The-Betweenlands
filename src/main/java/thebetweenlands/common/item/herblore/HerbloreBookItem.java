package thebetweenlands.common.item.herblore;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.network.clientbound.OpenHerbloreBookPacket;

public class HerbloreBookItem extends Item {
	public HerbloreBookItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!level.isClientSide()) {
			PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenHerbloreBookPacket(stack));
			return InteractionResultHolder.consume(stack);
		}
		return InteractionResultHolder.success(stack);
	}
}
