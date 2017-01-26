package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.ItemRegistry;

public class MessagePouchNaming extends MessageBase {
	private String name;
	private EnumHand hand;

	public MessagePouchNaming() { }

	public MessagePouchNaming(String name, EnumHand hand) {
		this.name = name;
		this.hand = hand;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeString(this.name);
		buf.writeByte(this.hand == EnumHand.MAIN_HAND ? 0 : 1);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.name = buf.readStringFromBuffer(128);
		this.hand = buf.readByte() == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			ItemStack heldItem = player.getHeldItem(this.hand);
			if(heldItem != null && heldItem.getItem() == ItemRegistry.LURKER_SKIN_POUCH && this.name != null) {
				if(this.name.length() == 0) {
					heldItem.clearCustomName();
				} else {
					heldItem.setStackDisplayName(this.name);
				}
			}
		}
		return null;
	}
}
