package thebetweenlands.common.registries;

import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
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
import thebetweenlands.client.audio.ambience.list.WaterAmbienceType;
import thebetweenlands.client.audio.ambience.list.WindAmbienceType;
import thebetweenlands.common.entity.EntityDecayPitTarget;
import thebetweenlands.common.world.event.EventBloodSky;
import thebetweenlands.common.world.event.EventSpoopy;
import thebetweenlands.common.world.storage.location.LocationAmbience;
import thebetweenlands.common.world.storage.location.LocationAmbience.EnumLocationAmbience;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;
import thebetweenlands.common.world.storage.location.LocationStorage;

@SideOnly(Side.CLIENT)
public class AmbienceRegistry {
	public static final AmbienceLayer BASE_LAYER = new AmbienceLayer(new ResourceLocation("base_layer"));
	public static final AmbienceLayer DETAIL_LAYER = new AmbienceLayer(new ResourceLocation("detail_layer"));

	public static void preInit() {
		//Base ambience
		AmbienceManager.INSTANCE.registerAmbience(new SurfaceAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new CaveAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new WaterAmbienceType(true));
		AmbienceManager.INSTANCE.registerAmbience(new WaterAmbienceType(false));
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
		AmbienceManager.INSTANCE.registerAmbience(new LocationAmbienceType(EnumLocationAmbience.SLUDGE_WORM_DUNGEON, SoundRegistry.AMBIENT_SLUDGE_WORM_DUNGEON) {
			@Override
			public boolean isActive() {
				if(super.isActive()) {
					LocationAmbience ambience = this.getAmbience();
					if(ambience != null) {
						LocationStorage location = ambience.getLocation();
						if(location instanceof LocationSludgeWormDungeon) {
							return this.getPlayer().getPositionEyes(1).y < ((LocationSludgeWormDungeon) location).getStructurePos().getY();
						}
					}
				}
				return false;
			}

			private float getProgressiveVolume() {
				LocationAmbience ambience = this.getAmbience();
				if(ambience != null) {
					LocationStorage location = ambience.getLocation();
					if(location instanceof LocationSludgeWormDungeon) {
						double dist = ((LocationSludgeWormDungeon) location).getStructurePos().getY() - this.getPlayer().getPositionEyes(1).y;
						return Math.min((float)dist / 12.0F, 1.0F);
					}
				}
				return 0;
			}

			@Override
			public float getLowerPriorityVolume() {
				return Math.max(0, 1 - this.getProgressiveVolume());
			}

			@Override
			public float getVolume() {
				return this.getProgressiveVolume();
			}
		});
		AmbienceManager.INSTANCE.registerAmbience(new LocationAmbienceType(EnumLocationAmbience.SLUDGE_WORM_DUNGEON, SoundRegistry.PIT_OF_DECAY_LOOP) {
			@Override
			public boolean isActive() {
				if(super.isActive() && this.getAmbience() != null) {
					LocationStorage location = this.getAmbience().getLocation();

					if(location.getName().equals("sludge_worm_dungeon_pit")) {
						List<EntityDecayPitTarget> targets = this.getPlayer().world.getEntitiesWithinAABB(EntityDecayPitTarget.class, location.getEnclosingBounds());

						for(EntityDecayPitTarget target : targets) {
							if(location.isInside(target)) {
								return true;
							}
						}
					}
				}

				return false;
			}
			
			@Override
			public SoundCategory getCategory() {
				return SoundCategory.MUSIC;
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
