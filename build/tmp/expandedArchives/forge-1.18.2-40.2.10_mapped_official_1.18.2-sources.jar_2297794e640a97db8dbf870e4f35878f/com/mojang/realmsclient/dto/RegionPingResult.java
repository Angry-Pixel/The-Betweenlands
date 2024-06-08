package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Locale;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RegionPingResult extends ValueObject implements ReflectionBasedSerialization {
   @SerializedName("regionName")
   private final String regionName;
   @SerializedName("ping")
   private final int ping;

   public RegionPingResult(String p_87650_, int p_87651_) {
      this.regionName = p_87650_;
      this.ping = p_87651_;
   }

   public int ping() {
      return this.ping;
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s --> %.2f ms", this.regionName, (float)this.ping);
   }
}