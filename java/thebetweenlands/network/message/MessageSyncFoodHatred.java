package thebetweenlands.network.message;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesFood;
import thebetweenlands.network.message.base.AbstractMessage;

import java.util.Map;

public class MessageSyncFoodHatred extends AbstractMessage<MessageSyncFoodHatred> {
	private Map<String, Integer> hatredMap;

	public MessageSyncFoodHatred() {

	}

	public MessageSyncFoodHatred(Map<String, Integer> hatredMap) {
		this.hatredMap = hatredMap;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessageClientSide(MessageSyncFoodHatred message, EntityPlayer player) {
		EntityPropertiesFood property = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesFood.class);
		for (Map.Entry<String, Integer> entry : message.hatredMap.entrySet()) {
			String modid = "minecraft";
			String item = entry.getKey();
			if (item.contains("\\:")) {
				String[] s = item.split(":");
				modid = s[0];
				item = s[1];
			}
			property.increaseFoodHatred((ItemFood) GameRegistry.findItem(modid, item), entry.getValue());
		}
	}

	@Override
	public void onMessageServerSide(MessageSyncFoodHatred message, EntityPlayer player) {

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		hatredMap = Maps.newHashMap();
		int size = buf.readInt();
		System.out.println("Size: " + size);
		for (int i = 0; i < size; i++) {
			String food = ByteBufUtils.readUTF8String(buf);
			int level = buf.readInt();
			hatredMap.put(food, level);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(hatredMap.size());
		for (Map.Entry<String, Integer> entry : hatredMap.entrySet()) {
			System.out.println("Writing " + entry);
			ByteBufUtils.writeUTF8String(buf, entry.getKey());
			buf.writeInt(entry.getValue());
		}
	}
}
