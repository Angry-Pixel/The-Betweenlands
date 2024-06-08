package com.mojang.realmsclient.dto;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsServerPing extends ValueObject {
   public volatile String nrOfPlayers = "0";
   public volatile String playerList = "";
}