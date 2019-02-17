package thebetweenlands.common.network.serverbound;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thebetweenlands.api.capability.ISummoningCapability;
import thebetweenlands.common.item.equipment.ItemRingOfSummoning;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class MessageUpdateSummoningState extends MessageBase {
	private boolean active;

	public MessageUpdateSummoningState() { }

	public MessageUpdateSummoningState(boolean active) {
		this.active = active;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBoolean(this.active);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.active = buf.readBoolean();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().player;
			if(player.hasCapability(CapabilityRegistry.CAPABILITY_SUMMON, null)) {
				ISummoningCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_SUMMON, null);

				if(!this.active && cap.isActive()) {
					cap.setActive(false);
					cap.setCooldownTicks(ItemRingOfSummoning.USE_COOLDOWN);
				} else if(this.active && !cap.isActive() && cap.getCooldownTicks() <= 0 && ItemRingOfSummoning.isRingActive(player)) {
					cap.setActive(true);
					cap.setActiveTicks(0);
					player.world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.PEAT_MUMMY_CHARGE, SoundCategory.PLAYERS, 0.4F, (player.world.rand.nextFloat() * 0.4F + 0.8F) * 0.8F);
				}
			}
		}
		return null;
	}

}
