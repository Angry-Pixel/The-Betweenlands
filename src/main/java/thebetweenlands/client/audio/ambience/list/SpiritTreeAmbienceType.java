package thebetweenlands.client.audio.ambience.list;

import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class SpiritTreeAmbienceType extends AmbienceType {
	private double getClosestSpiritTree() {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(Minecraft.getInstance().world);
		double closestSpiritTree = -1;
		for(ILocalStorage storage : worldStorage.getLocalStorageHandler().getLoadedStorages()) {
			if(storage instanceof LocationStorage && ((LocationStorage)storage).getType() == EnumLocationType.SPIRIT_TREE) {
				double dist = Minecraft.getInstance().player.getPositionVector().distanceTo(storage.getBoundingBox().getCenter());
				if(dist < 75) {
					if(closestSpiritTree < 0 || dist < closestSpiritTree) {
						closestSpiritTree = dist;
					}
				}
			}
		}
		return closestSpiritTree;
	}

	@Override
	public boolean isActive() {
		return this.getClosestSpiritTree() >= 0;
	}

	@Override
	public AmbienceLayer getAmbienceLayer() {
		return AmbienceRegistry.BASE_LAYER;
	}

	@Override
	public int getPriority() {
		return 999;
	}

	@Override
	public SoundCategory getCategory() {
		return SoundCategory.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_SNOWFALL;
	}

	@Override
	public float getVolume() {
		return 0.0F;
	}
	
	@Override
	public float getLowerPriorityVolume() {
		float strength = 1.0F - (float)Math.max(0, (this.getClosestSpiritTree() - 20) / (75.0F - 20));
		return 1.0F - strength;
	}
}
