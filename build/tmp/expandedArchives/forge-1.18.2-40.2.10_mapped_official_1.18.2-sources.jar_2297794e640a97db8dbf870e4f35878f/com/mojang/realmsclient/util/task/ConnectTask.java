package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsConnect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConnectTask extends LongRunningTask {
   private final RealmsConnect realmsConnect;
   private final RealmsServer server;
   private final RealmsServerAddress address;

   public ConnectTask(Screen p_90309_, RealmsServer p_90310_, RealmsServerAddress p_90311_) {
      this.server = p_90310_;
      this.address = p_90311_;
      this.realmsConnect = new RealmsConnect(p_90309_);
   }

   public void run() {
      this.setTitle(new TranslatableComponent("mco.connect.connecting"));
      this.realmsConnect.connect(this.server, ServerAddress.parseString(this.address.address));
   }

   public void abortTask() {
      this.realmsConnect.abort();
      Minecraft.getInstance().getClientPackSource().clearServerPack();
   }

   public void tick() {
      this.realmsConnect.tick();
   }
}