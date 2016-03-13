package thebetweenlands.client.audio.ambience.list;

import java.util.Iterator;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.event.player.PlayerLocationHandler;
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

	private List<LocationStorage> getAmbientLocations() {
		List<LocationStorage> locations = PlayerLocationHandler.getLocations(this.getPlayer());
		Iterator<LocationStorage> locationIT = locations.iterator();
		while(locationIT.hasNext()) {
			LocationStorage location = locationIT.next();
			if(!location.hasAmbience())
				locationIT.remove();
		}
		return locations;
	}

	private LocationStorage getHighestAmbientLocation() {
		List<LocationStorage> ambientLocations = this.getAmbientLocations();
		LocationStorage highestLocation = null;
		for(LocationStorage location : ambientLocations) {
			if(this.hasAmbienceType(location)) {
				if(highestLocation == null || highestLocation.getLayer() < location.getLayer())
					highestLocation = location;
			}
		}
		return highestLocation;
	}

	private boolean hasAmbienceType(LocationStorage location) {
		for(LocationAmbience ambience : location.getAmbiences())
			if(ambience.type.equals(this.ambience))
				return true;
		return false;
	}

	@Override
	public boolean isActive() {
		return this.getHighestAmbientLocation() != null;
	}

	@Override
	public EnumAmbienceLayer getAmbienceLayer() {
		return EnumAmbienceLayer.LAYER1;
	}

	@Override
	public int getPriority() {
		LocationStorage highestLocation = this.getHighestAmbientLocation();
		return 1 + (highestLocation != null ? highestLocation.getLayer() : 0);
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
