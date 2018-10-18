package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.common.network.MessageBase;

public class MessageItemNaming extends MessageBase {
	private String name;
	private EnumHand hand;

	public MessageItemNaming() { }

	public MessageItemNaming(String name, EnumHand hand) {
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
		this.name = buf.readString(128);
		this.hand = buf.readByte() == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().player;
			ItemStack heldItem = player.getHeldItem(this.hand);
			if(this.name != null && !heldItem.isEmpty() && heldItem.getItem() instanceof IRenamableItem && ((IRenamableItem) heldItem.getItem()).canRename(player, this.hand, heldItem, this.name)) {
				if(this.name.length() == 0) {
					((IRenamableItem) heldItem.getItem()).clearRename(player, this.hand, heldItem, this.name);
				} else {
					((IRenamableItem) heldItem.getItem()).setRename(player, this.hand, heldItem, this.name);
				}
			}
		}
		return null;
	}
}
