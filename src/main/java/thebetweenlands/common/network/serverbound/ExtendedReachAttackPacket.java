package thebetweenlands.common.network.serverbound;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.api.item.IExtendedReach;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.EntityIdentifierList;

public class ExtendedReachAttackPacket implements CustomPacketPayload {
	
	public static final Type<ExtendedReachAttackPacket> TYPE = new Type<>(TheBetweenlands.prefix("extended_reach_attack"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ExtendedReachAttackPacket> STREAM_CODEC = StreamCodec.composite(
			EntityIdentifierList.STREAM_CODEC, ExtendedReachAttackPacket::getEntities,
			ExtendedReachAttackPacket::new
		);
	
	// ---- for the codec ----
	protected EntityIdentifierList entityList;

	protected ExtendedReachAttackPacket(EntityIdentifierList entityIdList) {
		this.entityList = entityIdList;
	}
	
	protected EntityIdentifierList getEntities() {
		return this.entityList;
	}
	
	// ---- exposed methods ----
	public ExtendedReachAttackPacket(List<Entity> entities) {
		this.entityList = new EntityIdentifierList(entities);
	}
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public static void handle(ExtendedReachAttackPacket message, IPayloadContext ctx) {
		if (ctx.flow().isServerbound()) {
			
			ctx.enqueueWork(() -> {
				Player player = ctx.player();
				
				ItemStack heldItem = player.getWeaponItem();
				
				if(!heldItem.isEmpty() && heldItem.getItem() instanceof IExtendedReach extendedReach) {
					List<Entity> entities = message.entityList.getEntities();
					
					if(!extendedReach.onSwing(player, heldItem, entities)) return;

					for(Entity entity : entities) {
						if (entity != null && entity.isAlive()) {
							double reach = extendedReach.getReach(player, heldItem);
							// Inflation is the actual correct way to check if a player would realistically be able to reach it
							if (player.isCreative() || entity.getBoundingBox().inflate(entity.getPickRadius() + reach).contains(player.getEyePosition())) {
								player.attack(entity);
							}
						}
					}
				}
			});
			
		}
	}
	
}
