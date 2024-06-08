package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class DataPackConfig {
   public static final DataPackConfig DEFAULT = new DataPackConfig(ImmutableList.of("vanilla"), ImmutableList.of());
   public static final Codec<DataPackConfig> CODEC = RecordCodecBuilder.create((p_45854_) -> {
      return p_45854_.group(Codec.STRING.listOf().fieldOf("Enabled").forGetter((p_151457_) -> {
         return p_151457_.enabled;
      }), Codec.STRING.listOf().fieldOf("Disabled").forGetter((p_151455_) -> {
         return p_151455_.disabled;
      })).apply(p_45854_, DataPackConfig::new);
   });
   private final List<String> enabled;
   private final List<String> disabled;

   public DataPackConfig(List<String> p_45848_, List<String> p_45849_) {
      this.enabled = com.google.common.collect.Lists.newArrayList(p_45848_);
      this.disabled = ImmutableList.copyOf(p_45849_);
   }

   public List<String> getEnabled() {
      return this.enabled;
   }

   public List<String> getDisabled() {
      return this.disabled;
   }

   public void addModPacks(List<String> modPacks) {
      enabled.addAll(modPacks.stream().filter(p->!enabled.contains(p)).collect(java.util.stream.Collectors.toList()));
   }
}
