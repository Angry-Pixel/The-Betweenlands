package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;

public class EndFeatures {
   public static final Holder<ConfiguredFeature<SpikeConfiguration, ?>> END_SPIKE = FeatureUtils.register("end_spike", Feature.END_SPIKE, new SpikeConfiguration(false, ImmutableList.of(), (BlockPos)null));
   public static final Holder<ConfiguredFeature<EndGatewayConfiguration, ?>> END_GATEWAY_RETURN = FeatureUtils.register("end_gateway_return", Feature.END_GATEWAY, EndGatewayConfiguration.knownExit(ServerLevel.END_SPAWN_POINT, true));
   public static final Holder<ConfiguredFeature<EndGatewayConfiguration, ?>> END_GATEWAY_DELAYED = FeatureUtils.register("end_gateway_delayed", Feature.END_GATEWAY, EndGatewayConfiguration.delayedExitSearch());
   public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> CHORUS_PLANT = FeatureUtils.register("chorus_plant", Feature.CHORUS_PLANT);
   public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> END_ISLAND = FeatureUtils.register("end_island", Feature.END_ISLAND);
}