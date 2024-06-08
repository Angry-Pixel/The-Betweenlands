package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

public record ConfiguredWorldCarver<WC extends CarverConfiguration>(WorldCarver<WC> worldCarver, WC config) {
   public static final Codec<ConfiguredWorldCarver<?>> DIRECT_CODEC = Registry.CARVER.byNameCodec().dispatch((p_64867_) -> {
      return p_64867_.worldCarver;
   }, WorldCarver::configuredCodec);
   public static final Codec<Holder<ConfiguredWorldCarver<?>>> CODEC = RegistryFileCodec.create(Registry.CONFIGURED_CARVER_REGISTRY, DIRECT_CODEC);
   public static final Codec<HolderSet<ConfiguredWorldCarver<?>>> LIST_CODEC = RegistryCodecs.homogeneousList(Registry.CONFIGURED_CARVER_REGISTRY, DIRECT_CODEC);

   public boolean isStartChunk(Random p_159274_) {
      return this.worldCarver.isStartChunk(this.config, p_159274_);
   }

   public boolean carve(CarvingContext p_190713_, ChunkAccess p_190714_, Function<BlockPos, Holder<Biome>> p_190715_, Random p_190716_, Aquifer p_190717_, ChunkPos p_190718_, CarvingMask p_190719_) {
      return SharedConstants.debugVoidTerrain(p_190714_.getPos()) ? false : this.worldCarver.carve(p_190713_, this.config, p_190714_, p_190715_, p_190716_, p_190717_, p_190718_, p_190719_);
   }
}