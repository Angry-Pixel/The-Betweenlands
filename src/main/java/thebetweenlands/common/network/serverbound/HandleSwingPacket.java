package thebetweenlands.common.network.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.api.item.BigSwingAnimation;
import thebetweenlands.common.TheBetweenlands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record HandleSwingPacket(List<Integer> hitEntities) implements CustomPacketPayload {

	public static final Type<HandleSwingPacket> TYPE = new Type<>(TheBetweenlands.prefix("handle_swing"));
	public static final StreamCodec<RegistryFriendlyByteBuf, HandleSwingPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), HandleSwingPacket::hitEntities,
		HandleSwingPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(HandleSwingPacket message, IPayloadContext ctx) {
		if (ctx.flow().isServerbound()) {

			ctx.enqueueWork(() -> {
				Player player = ctx.player();

				ItemStack heldItem = player.getWeaponItem();

				if(!heldItem.isEmpty() && heldItem.getItem() instanceof BigSwingAnimation swingAnimation) {
					swingAnimation.onLeftClick(player, player.getMainHandItem());

					List<Entity> entities = message.hitEntities().stream().map(integer -> player.level().getEntity(integer)).toList();

					Set<Entity> seen = new HashSet<>(entities.size());
					for(Entity entity : entities) {
						if (entity != null && entity.isAlive() && !seen.contains(entity)) {
							// Inflation is the actual correct way to check if a player would realistically be able to reach it
							if (player.isCreative() || entity.getBoundingBox().inflate(entity.getPickRadius() + player.entityInteractionRange()).contains(player.getEyePosition())) {
								player.attack(entity);
							}
							// Prevent one entity being hit 10000 times in a single attack or something
							seen.add(entity);
						}
					}
				}
			});
		}
	}
}
