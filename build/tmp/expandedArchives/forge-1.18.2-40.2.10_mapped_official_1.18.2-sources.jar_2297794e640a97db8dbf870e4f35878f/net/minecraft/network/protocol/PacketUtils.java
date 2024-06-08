package net.minecraft.network.protocol;

import com.mojang.logging.LogUtils;
import net.minecraft.network.PacketListener;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import org.slf4j.Logger;

public class PacketUtils {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> p_131360_, T p_131361_, ServerLevel p_131362_) throws RunningOnDifferentThreadException {
      ensureRunningOnSameThread(p_131360_, p_131361_, p_131362_.getServer());
   }

   public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> p_131364_, T p_131365_, BlockableEventLoop<?> p_131366_) throws RunningOnDifferentThreadException {
      if (!p_131366_.isSameThread()) {
         p_131366_.executeIfPossible(() -> {
            if (p_131365_.getConnection().isConnected()) {
               try {
                  p_131364_.handle(p_131365_);
               } catch (Exception exception) {
                  if (p_131365_.shouldPropagateHandlingExceptions()) {
                     throw exception;
                  }

                  LOGGER.error("Failed to handle packet {}, suppressing error", p_131364_, exception);
               }
            } else {
               LOGGER.debug("Ignoring packet due to disconnection: {}", (Object)p_131364_);
            }

         });
         throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
      }
   }
}