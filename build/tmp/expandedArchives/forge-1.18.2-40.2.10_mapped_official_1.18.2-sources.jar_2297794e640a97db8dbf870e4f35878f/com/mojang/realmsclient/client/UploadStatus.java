package com.mojang.realmsclient.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UploadStatus {
   public volatile long bytesWritten;
   public volatile long totalBytes;
}