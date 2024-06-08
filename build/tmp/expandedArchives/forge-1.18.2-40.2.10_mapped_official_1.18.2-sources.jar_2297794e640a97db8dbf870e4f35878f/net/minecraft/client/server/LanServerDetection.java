package net.minecraft.client.server;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LanServerDetection {
   static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   static final Logger LOGGER = LogUtils.getLogger();

   @OnlyIn(Dist.CLIENT)
   public static class LanServerDetector extends Thread {
      private final LanServerDetection.LanServerList serverList;
      private final InetAddress pingGroup;
      private final MulticastSocket socket;

      public LanServerDetector(LanServerDetection.LanServerList p_120090_) throws IOException {
         super("LanServerDetector #" + LanServerDetection.UNIQUE_THREAD_ID.incrementAndGet());
         this.serverList = p_120090_;
         this.setDaemon(true);
         this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LanServerDetection.LOGGER));
         this.socket = new MulticastSocket(4445);
         this.pingGroup = InetAddress.getByName(LanServerPinger.MULTICAST_GROUP);
         this.socket.setSoTimeout(5000);
         this.socket.joinGroup(this.pingGroup);
      }

      public void run() {
         byte[] abyte = new byte[1024];

         while(!this.isInterrupted()) {
            DatagramPacket datagrampacket = new DatagramPacket(abyte, abyte.length);

            try {
               this.socket.receive(datagrampacket);
            } catch (SocketTimeoutException sockettimeoutexception) {
               continue;
            } catch (IOException ioexception1) {
               LanServerDetection.LOGGER.error("Couldn't ping server", (Throwable)ioexception1);
               break;
            }

            String s = new String(datagrampacket.getData(), datagrampacket.getOffset(), datagrampacket.getLength(), StandardCharsets.UTF_8);
            LanServerDetection.LOGGER.debug("{}: {}", datagrampacket.getAddress(), s);
            this.serverList.addServer(s, datagrampacket.getAddress());
         }

         try {
            this.socket.leaveGroup(this.pingGroup);
         } catch (IOException ioexception) {
         }

         this.socket.close();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LanServerList {
      private final List<LanServer> servers = Lists.newArrayList();
      private boolean isDirty;

      public synchronized boolean isDirty() {
         return this.isDirty;
      }

      public synchronized void markClean() {
         this.isDirty = false;
      }

      public synchronized List<LanServer> getServers() {
         return Collections.unmodifiableList(this.servers);
      }

      public synchronized void addServer(String p_120097_, InetAddress p_120098_) {
         String s = LanServerPinger.parseMotd(p_120097_);
         String s1 = LanServerPinger.parseAddress(p_120097_);
         if (s1 != null) {
            if (net.minecraftforge.network.DualStackUtils.checkIPv6(p_120098_)) {
               final String ip;

               // compress to short form if enabled in config
               if (net.minecraftforge.common.ForgeConfig.CLIENT.compressLanIPv6Addresses.get())
                  ip = com.google.common.net.InetAddresses.toAddrString(p_120098_);
               else
                  ip = p_120098_.getHostAddress();

               s1 = "[" + ip + "]:" + s1;
            } else {
            s1 = p_120098_.getHostAddress() + ":" + s1;
            }
            boolean flag = false;

            for(LanServer lanserver : this.servers) {
               if (lanserver.getAddress().equals(s1)) {
                  lanserver.updatePingTime();
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               this.servers.add(new LanServer(s, s1));
               this.isDirty = true;
            }

         }
      }
   }
}
