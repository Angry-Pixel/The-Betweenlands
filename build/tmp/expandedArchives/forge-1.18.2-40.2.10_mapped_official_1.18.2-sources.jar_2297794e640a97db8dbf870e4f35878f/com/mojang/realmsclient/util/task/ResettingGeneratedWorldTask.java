package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResettingGeneratedWorldTask extends ResettingWorldTask {
   private final WorldGenerationInfo generationInfo;

   public ResettingGeneratedWorldTask(WorldGenerationInfo p_167659_, long p_167660_, Component p_167661_, Runnable p_167662_) {
      super(p_167660_, p_167661_, p_167662_);
      this.generationInfo = p_167659_;
   }

   protected void sendResetRequest(RealmsClient p_167664_, long p_167665_) throws RealmsServiceException {
      p_167664_.resetWorldWithSeed(p_167665_, this.generationInfo);
   }
}