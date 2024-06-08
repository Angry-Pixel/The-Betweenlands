package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.QuartPos;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.dimension.DimensionType;

public record NoiseSettings(int minY, int height, NoiseSamplingSettings noiseSamplingSettings, NoiseSlider topSlideSettings, NoiseSlider bottomSlideSettings, int noiseSizeHorizontal, int noiseSizeVertical, TerrainShaper terrainShaper) {
   public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.<NoiseSettings>create((p_64536_) -> {
      return p_64536_.group(Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("min_y").forGetter(NoiseSettings::minY), Codec.intRange(0, DimensionType.Y_SIZE).fieldOf("height").forGetter(NoiseSettings::height), NoiseSamplingSettings.CODEC.fieldOf("sampling").forGetter(NoiseSettings::noiseSamplingSettings), NoiseSlider.CODEC.fieldOf("top_slide").forGetter(NoiseSettings::topSlideSettings), NoiseSlider.CODEC.fieldOf("bottom_slide").forGetter(NoiseSettings::bottomSlideSettings), Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::noiseSizeHorizontal), Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::noiseSizeVertical), TerrainShaper.CODEC.fieldOf("terrain_shaper").forGetter(NoiseSettings::terrainShaper)).apply(p_64536_, NoiseSettings::new);
   }).comapFlatMap(NoiseSettings::guardY, Function.identity());
   static final NoiseSettings NETHER_NOISE_SETTINGS = create(0, 128, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlider(0.9375D, 3, 0), new NoiseSlider(2.5D, 4, -1), 1, 2, TerrainProvider.nether());
   static final NoiseSettings END_NOISE_SETTINGS = create(0, 128, new NoiseSamplingSettings(2.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-23.4375D, 64, -46), new NoiseSlider(-0.234375D, 7, 1), 2, 1, TerrainProvider.end());
   static final NoiseSettings CAVES_NOISE_SETTINGS = create(-64, 192, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlider(0.9375D, 3, 0), new NoiseSlider(2.5D, 4, -1), 1, 2, TerrainProvider.caves());
   static final NoiseSettings FLOATING_ISLANDS_NOISE_SETTINGS = create(0, 256, new NoiseSamplingSettings(2.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-23.4375D, 64, -46), new NoiseSlider(-0.234375D, 7, 1), 2, 1, TerrainProvider.floatingIslands());

   private static DataResult<NoiseSettings> guardY(NoiseSettings p_158721_) {
      if (p_158721_.minY() + p_158721_.height() > DimensionType.MAX_Y + 1) {
         return DataResult.error("min_y + height cannot be higher than: " + (DimensionType.MAX_Y + 1));
      } else if (p_158721_.height() % 16 != 0) {
         return DataResult.error("height has to be a multiple of 16");
      } else {
         return p_158721_.minY() % 16 != 0 ? DataResult.error("min_y has to be a multiple of 16") : DataResult.success(p_158721_);
      }
   }

   public static NoiseSettings create(int p_212299_, int p_212300_, NoiseSamplingSettings p_212301_, NoiseSlider p_212302_, NoiseSlider p_212303_, int p_212304_, int p_212305_, TerrainShaper p_212306_) {
      NoiseSettings noisesettings = new NoiseSettings(p_212299_, p_212300_, p_212301_, p_212302_, p_212303_, p_212304_, p_212305_, p_212306_);
      guardY(noisesettings).error().ifPresent((p_158719_) -> {
         throw new IllegalStateException(p_158719_.message());
      });
      return noisesettings;
   }

   static NoiseSettings overworldNoiseSettings(boolean p_212308_) {
      return create(-64, 384, new NoiseSamplingSettings(1.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-0.078125D, 2, p_212308_ ? 0 : 8), new NoiseSlider(p_212308_ ? 0.4D : 0.1171875D, 3, 0), 1, 2, TerrainProvider.overworld(p_212308_));
   }

   public int getCellHeight() {
      return QuartPos.toBlock(this.noiseSizeVertical());
   }

   public int getCellWidth() {
      return QuartPos.toBlock(this.noiseSizeHorizontal());
   }

   public int getCellCountY() {
      return this.height() / this.getCellHeight();
   }

   public int getMinCellY() {
      return Mth.intFloorDiv(this.minY(), this.getCellHeight());
   }
}