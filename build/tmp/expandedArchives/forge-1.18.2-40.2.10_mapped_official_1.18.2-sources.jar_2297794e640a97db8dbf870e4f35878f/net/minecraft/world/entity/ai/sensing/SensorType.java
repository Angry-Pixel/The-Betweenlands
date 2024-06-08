package net.minecraft.world.entity.ai.sensing;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.axolotl.AxolotlAi;
import net.minecraft.world.entity.animal.goat.GoatAi;

public class SensorType<U extends Sensor<?>> extends net.minecraftforge.registries.ForgeRegistryEntry<SensorType<?>> {
   public static final SensorType<DummySensor> DUMMY = register("dummy", DummySensor::new);
   public static final SensorType<NearestItemSensor> NEAREST_ITEMS = register("nearest_items", NearestItemSensor::new);
   public static final SensorType<NearestLivingEntitySensor> NEAREST_LIVING_ENTITIES = register("nearest_living_entities", NearestLivingEntitySensor::new);
   public static final SensorType<PlayerSensor> NEAREST_PLAYERS = register("nearest_players", PlayerSensor::new);
   public static final SensorType<NearestBedSensor> NEAREST_BED = register("nearest_bed", NearestBedSensor::new);
   public static final SensorType<HurtBySensor> HURT_BY = register("hurt_by", HurtBySensor::new);
   public static final SensorType<VillagerHostilesSensor> VILLAGER_HOSTILES = register("villager_hostiles", VillagerHostilesSensor::new);
   public static final SensorType<VillagerBabiesSensor> VILLAGER_BABIES = register("villager_babies", VillagerBabiesSensor::new);
   public static final SensorType<SecondaryPoiSensor> SECONDARY_POIS = register("secondary_pois", SecondaryPoiSensor::new);
   public static final SensorType<GolemSensor> GOLEM_DETECTED = register("golem_detected", GolemSensor::new);
   public static final SensorType<PiglinSpecificSensor> PIGLIN_SPECIFIC_SENSOR = register("piglin_specific_sensor", PiglinSpecificSensor::new);
   public static final SensorType<PiglinBruteSpecificSensor> PIGLIN_BRUTE_SPECIFIC_SENSOR = register("piglin_brute_specific_sensor", PiglinBruteSpecificSensor::new);
   public static final SensorType<HoglinSpecificSensor> HOGLIN_SPECIFIC_SENSOR = register("hoglin_specific_sensor", HoglinSpecificSensor::new);
   public static final SensorType<AdultSensor> NEAREST_ADULT = register("nearest_adult", AdultSensor::new);
   public static final SensorType<AxolotlAttackablesSensor> AXOLOTL_ATTACKABLES = register("axolotl_attackables", AxolotlAttackablesSensor::new);
   public static final SensorType<TemptingSensor> AXOLOTL_TEMPTATIONS = register("axolotl_temptations", () -> {
      return new TemptingSensor(AxolotlAi.getTemptations());
   });
   public static final SensorType<TemptingSensor> GOAT_TEMPTATIONS = register("goat_temptations", () -> {
      return new TemptingSensor(GoatAi.getTemptations());
   });
   private final Supplier<U> factory;

   public SensorType(Supplier<U> p_26826_) {
      this.factory = p_26826_;
   }

   public U create() {
      return this.factory.get();
   }

   private static <U extends Sensor<?>> SensorType<U> register(String p_26829_, Supplier<U> p_26830_) {
      return Registry.register(Registry.SENSOR_TYPE, new ResourceLocation(p_26829_), new SensorType<>(p_26830_));
   }
}
