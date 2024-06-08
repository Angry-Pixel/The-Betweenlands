package net.minecraft.client.server;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LanServerPinger extends Thread {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String MULTICAST_GROUP = net.minecraftforge.network.DualStackUtils.getMulticastGroup();
   public static final int PING_PORT = 4445;
   private static final long PING_INTERVAL = 1500L;
   private final String motd;
   private final DatagramSocket socket;
   private boolean isRunning = true;
   private final String serverAddress;

   public LanServerPinger(String p_120109_, String p_120110_) throws IOException {
      super("LanServerPinger #" + UNIQUE_THREAD_ID.incrementAndGet());
      this.motd = p_120109_;
      this.serverAddress = p_120110_;
      this.setDaemon(true);
      this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      this.socket = new DatagramSocket();
   }

   public void run() {
      String s = createPingString(this.motd, this.serverAddress);
      byte[] abyte = s.getBytes(StandardCharsets.UTF_8);

      while(!this.isInterrupted() && this.isRunning) {
         try {
            InetAddress inetaddress = InetAddress.getByName(MULTICAST_GROUP);
            DatagramPacket datagrampacket = new DatagramPacket(abyte, abyte.length, inetaddress, 4445);
            this.socket.send(datagrampacket);
         } catch (IOException ioexception) {
            LOGGER.warn("LanServerPinger: {}", (Object)ioexception.getMessage());
            break;
         }

         try {
            sleep(1500L);
         } catch (InterruptedException interruptedexception) {
         }
      }

   }

   public void interrupt() {
      super.interrupt();
      this.isRunning = false;
   }

   public static String createPingString(String p_120114_, String p_120115_) {
      return "[MOTD]" + p_120114_ + "[/MOTD][AD]" + p_120115_ + "[/AD]";
   }

   public static String parseMotd(String p_120112_) {
      int i = p_120112_.indexOf("[MOTD]");
      if (i < 0) {
         return "missing no";
      } else {
         int j = p_120112_.indexOf("[/MOTD]", i + "[MOTD]".length());
         return j < i ? "missing no" : p_120112_.substring(i + "[MOTD]".length(), j);
      }
   }

   public static String parseAddress(String p_120117_) {
      int i = p_120117_.indexOf("[/MOTD]");
      if (i < 0) {
         return null;
      } else {
         int j = p_120117_.indexOf("[/MOTD]", i + "[/MOTD]".length());
         if (j >= 0) {
            return null;
         } else {
            int k = p_120117_.indexOf("[AD]", i + "[/MOTD]".length());
            if (k < 0) {
               return null;
            } else {
               int l = p_120117_.indexOf("[/AD]", k + "[AD]".length());
               return l < k ? null : p_120117_.substring(k + "[AD]".length(), l);
            }
         }
      }
   }
}
