package thebetweenlands.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.util.FoodSickness;

public record ShowFoodSicknessPacket(FoodSickness sickness) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ShowFoodSicknessPacket> TYPE = new CustomPacketPayload.Type<>(TheBetweenlands.prefix("show_food_sickness"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ShowFoodSicknessPacket> STREAM_CODEC = StreamCodec.composite(
		FoodSickness.STREAM_CODEC, ShowFoodSicknessPacket::sickness,
		ShowFoodSicknessPacket::new
	);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ShowFoodSicknessPacket message, IPayloadContext context) {
		context.enqueueWork(() -> context.player().displayClientMessage(message.sickness().getRandomLine(context.player().level().getRandom()), true));
	}
}
