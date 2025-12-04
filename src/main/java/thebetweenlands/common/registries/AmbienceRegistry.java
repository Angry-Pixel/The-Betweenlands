package thebetweenlands.common.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceManager;
import thebetweenlands.client.audio.ambience.list.*;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.DecayPitTarget;
import thebetweenlands.common.world.event.BloodSkyEvent;
import thebetweenlands.common.world.event.SpoopyEvent;
import thebetweenlands.common.world.storage.location.LocationAmbience;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;
import thebetweenlands.common.world.storage.location.LocationStorage;

import java.util.List;

public class AmbienceRegistry {

	public static final AmbienceLayer BASE_LAYER = new AmbienceLayer(TheBetweenlands.prefix("base_layer"));
	public static final AmbienceLayer DETAIL_LAYER = new AmbienceLayer(TheBetweenlands.prefix("detail_layer"));

	public static void init() {
		//Base ambience
		AmbienceManager.INSTANCE.registerAmbience(new SurfaceAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new DenseFogSurfaceAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new DeepWatersAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new CaveAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new WaterAmbienceType(true));
		AmbienceManager.INSTANCE.registerAmbience(new WaterAmbienceType(false));
		AmbienceManager.INSTANCE.registerAmbience(new WindAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new FloatingIslandAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new MountRainAmbienceType());

		//Locations
		AmbienceManager.INSTANCE.registerAmbience(new LocationAmbienceType(LocationAmbience.EnumLocationAmbience.WIGHT_TOWER, SoundRegistry.AMBIENT_WIGHT_FORTRESS.get()) {
			@Override
			public boolean isActive() {
				return super.isActive() && !this.getAmbience().getLocation().getName().equals("wight_tower_boss");
			}
		});
		AmbienceManager.INSTANCE.registerAmbience(new LocationAmbienceType(LocationAmbience.EnumLocationAmbience.WIGHT_TOWER, SoundRegistry.AMBIENT_WIGHT_FORTRESS.get()) { //Silences the other ambient tracks
			@Override
			public boolean isActive() {
				return super.isActive() && this.getAmbience().getLocation().getName().equals("wight_tower_boss");
			}

			@Override
			public float getVolume() {
				return 0.0F;
			}
		});
		AmbienceManager.INSTANCE.registerAmbience(new LocationAmbienceType(LocationAmbience.EnumLocationAmbience.SLUDGE_WORM_DUNGEON, SoundRegistry.AMBIENT_SLUDGE_WORM_DUNGEON.get()) {
			@Override
			public boolean isActive() {
				if(super.isActive()) {
					LocationAmbience ambience = this.getAmbience();
					if(ambience != null) {
						LocationStorage location = ambience.getLocation();
						if(location instanceof LocationSludgeWormDungeon dungeon) {
							return this.getPlayer().getEyePosition(1).y < dungeon.getStructurePos().getY();
						}
					}
				}
				return false;
			}

			private float getProgressiveVolume() {
				LocationAmbience ambience = this.getAmbience();
				if(ambience != null) {
					LocationStorage location = ambience.getLocation();
					if(location instanceof LocationSludgeWormDungeon dungeon) {
						double dist = dungeon.getStructurePos().getY() - this.getPlayer().getEyePosition(1).y;
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
		AmbienceManager.INSTANCE.registerAmbience(new LocationAmbienceType(LocationAmbience.EnumLocationAmbience.SLUDGE_WORM_DUNGEON, SoundRegistry.PIT_OF_DECAY_LOOP.get()) {
			@Override
			public boolean isActive() {
				if(super.isActive() && this.getAmbience() != null) {
					LocationStorage location = this.getAmbience().getLocation();

					if(location.getName().equals("sludge_worm_dungeon_pit")) {
						List<DecayPitTarget> targets = this.getPlayer().level().getEntitiesOfClass(DecayPitTarget.class, location.getEnclosingBounds());

						for(DecayPitTarget target : targets) {
							if(location.isInside(target)) {
								return true;
							}
						}
					}
				}

				return false;
			}

			@Override
			public SoundSource getCategory() {
				return SoundSource.MUSIC;
			}
		});
		AmbienceManager.INSTANCE.registerAmbience(new SpiritTreeAmbienceType());

		//Events
		AmbienceManager.INSTANCE.registerAmbience(new EventAmbienceType(SpoopyEvent.class, SoundRegistry.AMBIENT_SPOOPY.get(), 0));
		AmbienceManager.INSTANCE.registerAmbience(new EventAmbienceType(BloodSkyEvent.class, SoundRegistry.AMBIENT_BLOOD_SKY.get(), 1).setDelay(140));
		AmbienceManager.INSTANCE.registerAmbience(new FrostyAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new SnowFallAmbienceType());
		AmbienceManager.INSTANCE.registerAmbience(new PresentAmbienceType());
	}
}
