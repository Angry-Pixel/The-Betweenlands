package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.proxy.CommonProxy;

public class MessageOpenPouch extends MessageBase {
	@Override
	public void serialize(PacketBuffer buf) { }

	@Override
	public void deserialize(PacketBuffer buf) { }

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().player;
			ItemStack stack = ItemLurkerSkinPouch.getFirstPouch(player);
			if(!stack.isEmpty()) {
				int meta = stack.getItemDamage();
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_LURKER_POUCH_KEYBIND, player.world, meta, 0, 0);
			}
		}
		return null;
	}
}
