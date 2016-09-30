package thebetweenlands.common.registries;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.message.BLMessage;
import thebetweenlands.common.message.clientbound.MessageDruidAltarProgress;
import thebetweenlands.common.message.clientbound.MessageSyncStaticAspects;
import thebetweenlands.common.message.clientbound.MessageSyncChunkData;
import thebetweenlands.common.message.clientbound.MessageSyncEntityCapabilities;
import thebetweenlands.common.message.clientbound.MessageSyncEnvironmentEvent;
import thebetweenlands.common.message.clientbound.MessageWeedwoodBushRustle;

public class MessageRegistry {
	private MessageRegistry() { }
	
    private static byte nextMessageId = 0;

    public static void preInit() {
        registerMessage(MessageDruidAltarProgress.class, Side.CLIENT);
        registerMessage(MessageSyncChunkData.class, Side.CLIENT);
        registerMessage(MessageSyncEnvironmentEvent.class, Side.CLIENT);
		registerMessage(MessageWeedwoodBushRustle.class, Side.CLIENT);
		registerMessage(MessageSyncEntityCapabilities.class, Side.CLIENT);
		registerMessage(MessageSyncStaticAspects.class, Side.CLIENT);
    }

	private static void registerMessage(Class<? extends BLMessage> messageType, Side toSide) {
		TheBetweenlands.networkWrapper.registerMessage(getHandler(messageType, toSide), messageType, MessageRegistry.nextMessageId++, toSide);
	}

	private static IMessageHandler<BLMessage, IMessage> getHandler(Class<? extends BLMessage> messageType, Side toSide) {
		if (toSide == Side.CLIENT) {
			return new ClientboundHandler();
		}
		return new ServerboundHandler();
	}

	private static class ServerboundHandler implements IMessageHandler<BLMessage, IMessage> {
		@Override
		public IMessage onMessage(BLMessage message, MessageContext ctx) {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			try {
				return server.callFromMainThread(() -> message.process(ctx)).get();
			} catch (Exception e) {
				e.printStackTrace(); // TODO: proper logging
			}
			return null;
		}
	}

	private static class ClientboundHandler implements IMessageHandler<BLMessage, IMessage> {
		@Override
		public IMessage onMessage(BLMessage message, MessageContext ctx) {
			Minecraft mc = FMLClientHandler.instance().getClient();
			try {
				return mc.addScheduledTask(() -> message.process(ctx)).get();
			} catch (Exception e) {
				e.printStackTrace(); // TODO: proper logging
			}
			return null;
		}
	}
}
