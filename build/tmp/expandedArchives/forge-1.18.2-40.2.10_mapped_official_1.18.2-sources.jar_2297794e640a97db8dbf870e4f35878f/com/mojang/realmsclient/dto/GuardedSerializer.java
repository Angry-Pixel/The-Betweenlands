package com.mojang.realmsclient.dto;

import com.google.gson.Gson;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardedSerializer {
   private final Gson gson = new Gson();

   public String toJson(ReflectionBasedSerialization p_87414_) {
      return this.gson.toJson(p_87414_);
   }

   @Nullable
   public <T extends ReflectionBasedSerialization> T fromJson(String p_87416_, Class<T> p_87417_) {
      return this.gson.fromJson(p_87416_, p_87417_);
   }
}