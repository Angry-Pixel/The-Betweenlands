package thebetweenlands.client.audio.ambience;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.audio.ambience.list.CaveAmbienceType;
import thebetweenlands.client.audio.ambience.list.EventAmbienceType;
import thebetweenlands.client.audio.ambience.list.LocationAmbienceType;
import thebetweenlands.client.audio.ambience.list.SurfaceAmbienceType;
import thebetweenlands.world.events.impl.EventBloodSky;
import thebetweenlands.world.events.impl.EventSpoopy;
import thebetweenlands.world.storage.chunk.storage.location.LocationAmbience.EnumLocationAmbience;

@SideOnly(Side.CLIENT)
public class AmbienceRegistry {
	public static void register() {
		//Base ambience
		AmbienceManager.INSTANCE.registerAmbience(new SurfaceAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new CaveAmbienceType());

		//Locations
		AmbienceManager.INSTANCE.registerAmbience(new LocationAmbienceType(EnumLocationAmbience.WIGHT_TOWER, new ResourceLocation("thebetweenlands:ambientWightFortress")) {
			@Override
			public boolean isActive() {
				return super.isActive() && !this.getAmbience().getLocation().getName().equals("translate:wightTowerBoss");
			}
		});
		AmbienceManager.INSTANCE.registerAmbience(new LocationAmbienceType(EnumLocationAmbience.WIGHT_TOWER, null) { //Silences the other ambient tracks
			@Override
			public boolean isActive() {
				return super.isActive() && this.getAmbience().getLocation().getName().equals("translate:wightTowerBoss");
			}
		});

		//Events
		AmbienceManager.INSTANCE.registerAmbience(new EventAmbienceType(EventSpoopy.class, new ResourceLocation("thebetweenlands:ambientSpoopy"), 0));
		AmbienceManager.INSTANCE.registerAmbience(new EventAmbienceType(EventBloodSky.class, new ResourceLocation("thebetweenlands:ambientBloodSky"), 1).setDelay(140));
	}
}
