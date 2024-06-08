package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResettingTemplateWorldTask extends ResettingWorldTask {
   private final WorldTemplate template;

   public ResettingTemplateWorldTask(WorldTemplate p_167668_, long p_167669_, Component p_167670_, Runnable p_167671_) {
      super(p_167669_, p_167670_, p_167671_);
      this.template = p_167668_;
   }

   protected void sendResetRequest(RealmsClient p_167673_, long p_167674_) throws RealmsServiceException {
      p_167673_.resetWorldWithTemplate(p_167674_, this.template.id);
   }
}