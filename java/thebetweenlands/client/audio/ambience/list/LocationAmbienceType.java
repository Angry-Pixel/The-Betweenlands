package thebetweenlands.client.audio.ambience.list;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.world.storage.chunk.storage.location.LocationAmbience;
import thebetweenlands.world.storage.chunk.storage.location.LocationAmbience.EnumLocationAmbience;
import thebetweenlands.world.storage.chunk.storage.location.LocationStorage;

public class LocationAmbienceType extends AmbienceType {
	private final EnumLocationAmbience ambience;
	private final ResourceLocation sound;

	public LocationAmbienceType(EnumLocationAmbience ambience, ResourceLocation sound) {
		this.ambience = ambience;
		this.sound = sound;
	}

	protected LocationAmbience getAmbience() {
		return LocationStorage.getAmbience(this.getPlayer());
	}

	@Override
	public boolean isActive() {
		return this.getAmbience() != null;
	}

	@Override
	public EnumAmbienceLayer getAmbienceLayer() {
		return EnumAmbienceLayer.LAYER1;
	}

	@Override
	public int getPriority() {
		return 2;
	}

	@Override
	public ResourceLocation getSound() {
		return this.sound;
	}

	@Override
	public boolean stopsMusic() {
		return true;
	}
}
