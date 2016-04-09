package thebetweenlands.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.items.equipment.ItemLurkerSkinPouch;
import thebetweenlands.network.message.base.AbstractMessage;
import thebetweenlands.proxy.CommonProxy;

public class MessageOpenPouch extends AbstractMessage<MessageOpenPouch> {
	@Override
	public void fromBytes(ByteBuf buf) { }

	@Override
	public void toBytes(ByteBuf buf) { }

	@Override
	public void onMessageClientSide(MessageOpenPouch message, EntityPlayer player) { }

	@Override
	public void onMessageServerSide(MessageOpenPouch message, EntityPlayer player) {
		if(player != null) {
			ItemStack stack = ItemLurkerSkinPouch.getFirstPouch(player);
			if(stack != null) {
				int meta = stack.getItemDamage();
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_LURKER_POUCH_KEYBIND, player.worldObj, meta, 0, 0);
			}
		}
	}
}
