package thebetweenlands.common.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceManager;
import thebetweenlands.client.audio.ambience.list.CaveAmbienceType;
import thebetweenlands.client.audio.ambience.list.EventAmbienceType;
import thebetweenlands.client.audio.ambience.list.FrostyAmbienceType;
import thebetweenlands.client.audio.ambience.list.LocationAmbienceType;
import thebetweenlands.client.audio.ambience.list.PresentAmbienceType;
import thebetweenlands.client.audio.ambience.list.SnowFallAmbienceType;
import thebetweenlands.client.audio.ambience.list.SpiritTreeAmbienceType;
import thebetweenlands.client.audio.ambience.list.SurfaceAmbienceType;
import thebetweenlands.client.audio.ambience.list.WindAmbienceType;
import thebetweenlands.client.audio.ambience.list.WaterAmbienceType;
import thebetweenlands.common.world.event.EventBloodSky;
import thebetweenlands.common.world.event.EventSpoopy;
import thebetweenlands.common.world.storage.location.LocationAmbience.EnumLocationAmbience;

@SideOnly(Side.CLIENT)
public class AmbienceRegistry {
	public static final AmbienceLayer BASE_LAYER = new AmbienceLayer(new ResourceLocation("base_layer"));

	public static void preInit() {
		//Base ambience
		AmbienceManager.INSTANCE.registerAmbience(new SurfaceAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new CaveAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new WaterAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new WindAmbienceType());
		
		//Locations
		AmbienceManager.INSTANCE.registerAmbience(new LocationAmbienceType(EnumLocationAmbience.WIGHT_TOWER, SoundRegistry.AMBIENT_WIGHT_FORTRESS) {
			@Override
			public boolean isActive() {
				return super.isActive() && !this.getAmbience().getLocation().getName().equals("wight_tower_boss");
			}
		});
		AmbienceManager.INSTANCE.registerAmbience(new LocationAmbienceType(EnumLocationAmbience.WIGHT_TOWER, SoundRegistry.AMBIENT_WIGHT_FORTRESS) { //Silences the other ambient tracks
			@Override
			public boolean isActive() {
				return super.isActive() && this.getAmbience().getLocation().getName().equals("wight_tower_boss");
			}

			@Override
			public float getVolume() {
				return 0.0F;
			}
		});
		AmbienceManager.INSTANCE.registerAmbience(new SpiritTreeAmbienceType());

		//Events
		AmbienceManager.INSTANCE.registerAmbience(new EventAmbienceType(EventSpoopy.class, SoundRegistry.AMBIENT_SPOOPY, 0));
		AmbienceManager.INSTANCE.registerAmbience(new EventAmbienceType(EventBloodSky.class, SoundRegistry.AMBIENT_BLOOD_SKY, 1).setDelay(140));
		AmbienceManager.INSTANCE.registerAmbience(new FrostyAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new SnowFallAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new PresentAmbienceType());
	}
}
