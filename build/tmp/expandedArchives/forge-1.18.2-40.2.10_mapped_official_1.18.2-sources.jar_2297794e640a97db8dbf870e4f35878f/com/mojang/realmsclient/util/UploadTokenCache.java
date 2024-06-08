package com.mojang.realmsclient.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UploadTokenCache {
   private static final Long2ObjectMap<String> TOKEN_CACHE = new Long2ObjectOpenHashMap<>();

   public static String get(long p_90293_) {
      return TOKEN_CACHE.get(p_90293_);
   }

   public static void invalidate(long p_90298_) {
      TOKEN_CACHE.remove(p_90298_);
   }

   public static void put(long p_90295_, String p_90296_) {
      TOKEN_CACHE.put(p_90295_, p_90296_);
   }
}