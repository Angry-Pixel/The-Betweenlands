package thebetweenlands.entities.properties.list;

import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.utils.vectormath.Point3i;

import java.util.Map;
import java.util.WeakHashMap;

public class EntityPropertiesLantern {
	public static EntityPropertiesLantern getPlayerData(EntityPlayer player) {
		EntityPropertiesLantern data = playerMap.get(player);
		if (data == null && player != null) {
			data = new EntityPropertiesLantern();
			playerMap.put(player, data);
		}
		return data;
	}

	public static void update() {
		for (Map.Entry<EntityPlayer, EntityPropertiesLantern> dataEntry : playerMap.entrySet()) {
			Point3i lastClicked = dataEntry.getValue().lastClicked;
			if (dataEntry.getKey().worldObj.getTileEntity(lastClicked.x, lastClicked.y, lastClicked.z) == null) {
				dataEntry.getValue().lastClicked = UNKNOWN;
			}
		}
	}

	private static Map<EntityPlayer, EntityPropertiesLantern> playerMap = new WeakHashMap<EntityPlayer, EntityPropertiesLantern>();

	private static final Point3i UNKNOWN = new Point3i();

	private Point3i lastClicked;

	public EntityPropertiesLantern() {
		setUnknownLastClicked();
	}

	public Point3i getLastClicked() {
		return lastClicked;
	}

	public boolean hasLastClicked() {
		return lastClicked != UNKNOWN;
	}

	public void setLastClicked(int x, int y, int z) {
		lastClicked = new Point3i(x, y, z);
	}

	public void setUnknownLastClicked() {
		lastClicked = UNKNOWN;
	}
}
