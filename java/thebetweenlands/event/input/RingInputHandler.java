package thebetweenlands.event.input;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.input.KeyBindingsBL;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesRingInput;
import thebetweenlands.network.base.SubscribePacket;
import thebetweenlands.network.packet.client.PacketRingInput;

public class RingInputHandler {
	public static final RingInputHandler INSTANCE = new RingInputHandler();

	private boolean wasPressed = false;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onInput(InputEvent event) {
		this.updateClientRecruitmentState();
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
		if(player != null && player.ticksExisted % 20 == 0) {
			TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketRingInput(KeyBindingsBL.USE_RING.getIsKeyPressed())));
		}
	}

	@SideOnly(Side.CLIENT)
	private void updateClientRecruitmentState() {
		if(!this.wasPressed && KeyBindingsBL.USE_RING.getIsKeyPressed()) {
			this.wasPressed = true;
			TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketRingInput(true)));
		} else if(this.wasPressed && !KeyBindingsBL.USE_RING.getIsKeyPressed()) {
			this.wasPressed = false;
			TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketRingInput(false)));
		}
	}

	@SubscribePacket
	public static void onInputPacket(PacketRingInput packet) {
		if(packet.getContext().getServerHandler() != null) {
			EntityPlayer sender = packet.getContext().getServerHandler().playerEntity;
			if(sender != null) {
				EntityPropertiesRingInput prop = BLEntityPropertiesRegistry.HANDLER.getProperties(sender, EntityPropertiesRingInput.class);
				if(prop != null)
					prop.setInUse(packet.isPressed());
			}
		}
	}
}
