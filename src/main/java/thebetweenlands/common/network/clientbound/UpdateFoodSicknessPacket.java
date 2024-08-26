package thebetweenlands.common.network.clientbound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.FoodSicknessData;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.util.FoodSickness;

import java.util.HashMap;

public record UpdateFoodSicknessPacket(HashMap<Item, Integer> hatredMap, FoodSickness lastSickness) implements CustomPacketPayload {

	public static final Type<UpdateFoodSicknessPacket> TYPE = new Type<>(TheBetweenlands.prefix("update_food_sickness"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateFoodSicknessPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.map(HashMap::new, ByteBufCodecs.fromCodec(BuiltInRegistries.ITEM.byNameCodec()), ByteBufCodecs.INT), UpdateFoodSicknessPacket::hatredMap,
		FoodSickness.STREAM_CODEC, UpdateFoodSicknessPacket::lastSickness,
		UpdateFoodSicknessPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateFoodSicknessPacket message, IPayloadContext context) {
		context.enqueueWork(() -> context.player().setData(AttachmentRegistry.FOOD_SICKNESS, new FoodSicknessData(message.hatredMap(), message.lastSickness())));
	}
}
