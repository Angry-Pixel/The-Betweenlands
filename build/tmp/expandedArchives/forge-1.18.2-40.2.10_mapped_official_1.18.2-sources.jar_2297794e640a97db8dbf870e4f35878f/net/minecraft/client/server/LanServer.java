package net.minecraft.client.server;

import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanServer {
   private final String motd;
   private final String address;
   private long pingTime;

   public LanServer(String p_120076_, String p_120077_) {
      this.motd = p_120076_;
      this.address = p_120077_;
      this.pingTime = Util.getMillis();
   }

   public String getMotd() {
      return this.motd;
   }

   public String getAddress() {
      return this.address;
   }

   public void updatePingTime() {
      this.pingTime = Util.getMillis();
   }
}