package thebetweenlands.api.capability;

import org.jetbrains.annotations.Nullable;

import net.neoforged.neoforge.capabilities.ItemCapability;
import thebetweenlands.api.capability.lifecrystal.ILifeCrystalHandler;
import thebetweenlands.common.TheBetweenlands;

public class BLCapabilities {
	public static class LifeCrystalHandler {
		
		// TODO Once we get to the transfer rework, change from Void to ItemAccess
//		public static final ItemCapability<ILifeCrystalHandler, @Nullable ItemAccess> ITEM = ItemCapability.create(TheBetweenlands.prefix("life_crystal"), ILifeCrystalHandler.class, ItemAccess.class);
		public static final ItemCapability<ILifeCrystalHandler, @Nullable Void> ITEM = ItemCapability.createVoid(TheBetweenlands.prefix("life_crystal"), ILifeCrystalHandler.class);
		
	}
}
