package thebetweenlands.common.registries;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.network.clientbound.MessageAddLocalStorage;
import thebetweenlands.common.network.clientbound.MessageAmateMap;
import thebetweenlands.common.network.clientbound.MessageBlockGuardData;
import thebetweenlands.common.network.clientbound.MessageBlockGuardSectionChange;
import thebetweenlands.common.network.clientbound.MessageClearBlockGuard;
import thebetweenlands.common.network.clientbound.MessageDamageReductionParticle;
import thebetweenlands.common.network.clientbound.MessageDruidAltarProgress;
import thebetweenlands.common.network.clientbound.MessageDruidTeleportParticles;
import thebetweenlands.common.network.clientbound.MessageGemProc;
import thebetweenlands.common.network.clientbound.MessageLivingWeedwoodShieldSpit;
import thebetweenlands.common.network.clientbound.MessageMireSnailEggHatching;
import thebetweenlands.common.network.clientbound.MessagePlayEntityIdle;
import thebetweenlands.common.network.clientbound.MessagePowerRingParticles;
import thebetweenlands.common.network.clientbound.MessageRemoveLocalStorage;
import thebetweenlands.common.network.clientbound.MessageRiftSound;
import thebetweenlands.common.network.clientbound.MessageShowFoodSicknessLine;
import thebetweenlands.common.network.clientbound.MessageSoundRipple;
import thebetweenlands.common.network.clientbound.MessageSummonPeatMummyParticles;
import thebetweenlands.common.network.clientbound.MessageSyncChunkStorage;
import thebetweenlands.common.network.clientbound.MessageSyncEntityCapabilities;
import thebetweenlands.common.network.clientbound.MessageSyncEnvironmentEventData;
import thebetweenlands.common.network.clientbound.MessageSyncGameRules;
import thebetweenlands.common.network.clientbound.MessageSyncLocalStorageData;
import thebetweenlands.common.network.clientbound.MessageSyncLocalStorageReferences;
import thebetweenlands.common.network.clientbound.MessageSyncStaticAspects;
import thebetweenlands.common.network.clientbound.MessageWeedwoodBushRustle;
import thebetweenlands.common.network.clientbound.MessageWightVolatileParticles;
import thebetweenlands.common.network.serverbound.MessageConnectCavingRope;
import thebetweenlands.common.network.serverbound.MessageEquipItem;
import thebetweenlands.common.network.serverbound.MessageFlightState;
import thebetweenlands.common.network.serverbound.MessageItemNaming;
import thebetweenlands.common.network.serverbound.MessageOpenPouch;
import thebetweenlands.common.network.serverbound.MessageRow;
import thebetweenlands.common.network.serverbound.MessageUpdatePuppeteerState;
import thebetweenlands.common.network.serverbound.MessageUpdateSummoningState;

public class MessageRegistry {
	private MessageRegistry() { }

	private static byte nextMessageId = 0;

	public static void preInit() {
		registerMessage(MessageDruidAltarProgress.class, Dist.CLIENT);
		registerMessage(MessageSyncEnvironmentEventData.class, Dist.CLIENT);
		registerMessage(MessageWeedwoodBushRustle.class, Dist.CLIENT);
		registerMessage(MessageSyncEntityCapabilities.class, Dist.CLIENT);
		registerMessage(MessageSyncStaticAspects.class, Dist.CLIENT);
		registerMessage(MessageDruidTeleportParticles.class, Dist.CLIENT);
		registerMessage(MessageWightVolatileParticles.class, Dist.CLIENT);
		registerMessage(MessageGemProc.class, Dist.CLIENT);
		registerMessage(MessageMireSnailEggHatching.class, Dist.CLIENT);
		registerMessage(MessageBlockGuardSectionChange.class, Dist.CLIENT);
		registerMessage(MessageBlockGuardData.class, Dist.CLIENT);
		registerMessage(MessageClearBlockGuard.class, Dist.CLIENT);
		registerMessage(MessagePlayEntityIdle.class, Dist.CLIENT);
		registerMessage(MessagePowerRingParticles.class, Dist.CLIENT);
		registerMessage(MessageRemoveLocalStorage.class, Dist.CLIENT);
		registerMessage(MessageAddLocalStorage.class, Dist.CLIENT);
		registerMessage(MessageSyncLocalStorageData.class, Dist.CLIENT);
		registerMessage(MessageSyncChunkStorage.class, Dist.CLIENT);
		registerMessage(MessageSyncLocalStorageReferences.class, Dist.CLIENT);
		registerMessage(MessageSummonPeatMummyParticles.class, Dist.CLIENT);
		registerMessage(MessageShowFoodSicknessLine.class, Dist.CLIENT);
		registerMessage(MessageDamageReductionParticle.class, Dist.CLIENT);
		registerMessage(MessageRiftSound.class, Dist.CLIENT);
		registerMessage(MessageLivingWeedwoodShieldSpit.class, Dist.CLIENT);
		registerMessage(MessageAmateMap.class, Dist.CLIENT);
		registerMessage(MessageSoundRipple.class, Dist.CLIENT);
		registerMessage(MessageSyncGameRules.class, Dist.CLIENT);

		registerMessage(MessageEquipItem.class, Dist.DEDICATED_SERVER);
		registerMessage(MessageOpenPouch.class, Dist.DEDICATED_SERVER);
		registerMessage(MessageItemNaming.class, Dist.DEDICATED_SERVER);
		registerMessage(MessageFlightState.class, Dist.DEDICATED_SERVER);
		registerMessage(MessageUpdatePuppeteerState.class, Dist.DEDICATED_SERVER);
		registerMessage(MessageUpdateSummoningState.class, Dist.DEDICATED_SERVER);
		registerMessage(MessageRow.class, Dist.DEDICATED_SERVER);
		registerMessage(MessageConnectCavingRope.class, Dist.DEDICATED_SERVER);
	}

	private static void registerMessage(Class<? extends MessageBase> messageType, Side toSide) {
		TheBetweenlands.networkWrapper.registerMessage(getHandler(messageType, toSide), messageType, MessageRegistry.nextMessageId++, toSide);
	}

	private static IMessageHandler<MessageBase, IMessage> getHandler(Class<? extends MessageBase> messageType, Side toSide) {
		if (toSide == Dist.CLIENT) {
			return new ClientboundHandler();
		}
		return new ServerboundHandler();
	}

	private static class ServerboundHandler implements IMessageHandler<MessageBase, IMessage> {
		@Override
		public IMessage onMessage(MessageBase message, MessageContext ctx) {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			try {
				server.callFromMainThread(() -> message.process(ctx));
			} catch (Exception e) {
				e.printStackTrace(); // TODO: proper logging
			}
			return null;
		}
	}

	private static class ClientboundHandler implements IMessageHandler<MessageBase, IMessage> {
		@Override
		public IMessage onMessage(MessageBase message, MessageContext ctx) {
			Minecraft mc = FMLClientHandler.instance().getClient();
			try {
				mc.addScheduledTask(() -> message.process(ctx));
			} catch (Exception e) {
				e.printStackTrace(); // TODO: proper logging
			}
			return null;
		}
	}
}
