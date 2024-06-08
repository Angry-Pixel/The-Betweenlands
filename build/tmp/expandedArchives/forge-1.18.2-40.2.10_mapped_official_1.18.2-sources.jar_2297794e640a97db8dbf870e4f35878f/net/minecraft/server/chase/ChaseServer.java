package net.minecraft.server.chase;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.server.commands.ChaseCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ChaseServer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String serverBindAddress;
   private final int serverPort;
   private final PlayerList playerList;
   private final int broadcastIntervalMs;
   private volatile boolean wantsToRun;
   @Nullable
   private ServerSocket serverSocket;
   private final CopyOnWriteArrayList<Socket> clientSockets = new CopyOnWriteArrayList<>();

   public ChaseServer(String p_196032_, int p_196033_, PlayerList p_196034_, int p_196035_) {
      this.serverBindAddress = p_196032_;
      this.serverPort = p_196033_;
      this.playerList = p_196034_;
      this.broadcastIntervalMs = p_196035_;
   }

   public void start() throws IOException {
      if (this.serverSocket != null && !this.serverSocket.isClosed()) {
         LOGGER.warn("Remote control server was asked to start, but it is already running. Will ignore.");
      } else {
         this.wantsToRun = true;
         this.serverSocket = new ServerSocket(this.serverPort, 50, InetAddress.getByName(this.serverBindAddress));
         Thread thread = new Thread(this::runAcceptor, "chase-server-acceptor");
         thread.setDaemon(true);
         thread.start();
         Thread thread1 = new Thread(this::runSender, "chase-server-sender");
         thread1.setDaemon(true);
         thread1.start();
      }
   }

   private void runSender() {
      ChaseServer.PlayerPosition chaseserver$playerposition = null;

      while(this.wantsToRun) {
         if (!this.clientSockets.isEmpty()) {
            ChaseServer.PlayerPosition chaseserver$playerposition1 = this.getPlayerPosition();
            if (chaseserver$playerposition1 != null && !chaseserver$playerposition1.equals(chaseserver$playerposition)) {
               chaseserver$playerposition = chaseserver$playerposition1;
               byte[] abyte = chaseserver$playerposition1.format().getBytes(StandardCharsets.US_ASCII);

               for(Socket socket : this.clientSockets) {
                  if (!socket.isClosed()) {
                     Util.ioPool().submit(() -> {
                        try {
                           OutputStream outputstream = socket.getOutputStream();
                           outputstream.write(abyte);
                           outputstream.flush();
                        } catch (IOException ioexception) {
                           LOGGER.info("Remote control client socket got an IO exception and will be closed", (Throwable)ioexception);
                           IOUtils.closeQuietly(socket);
                        }

                     });
                  }
               }
            }

            List<Socket> list = this.clientSockets.stream().filter(Socket::isClosed).collect(Collectors.toList());
            this.clientSockets.removeAll(list);
         }

         if (this.wantsToRun) {
            try {
               Thread.sleep((long)this.broadcastIntervalMs);
            } catch (InterruptedException interruptedexception) {
            }
         }
      }

   }

   public void stop() {
      this.wantsToRun = false;
      IOUtils.closeQuietly(this.serverSocket);
      this.serverSocket = null;
   }

   private void runAcceptor() {
      while(true) {
         try {
            if (this.wantsToRun) {
               if (this.serverSocket != null) {
                  LOGGER.info("Remote control server is listening for connections on port {}", (int)this.serverPort);
                  Socket socket = this.serverSocket.accept();
                  LOGGER.info("Remote control server received client connection on port {}", (int)socket.getPort());
                  this.clientSockets.add(socket);
               }
               continue;
            }
         } catch (ClosedByInterruptException closedbyinterruptexception) {
            if (this.wantsToRun) {
               LOGGER.info("Remote control server closed by interrupt");
            }
         } catch (IOException ioexception) {
            if (this.wantsToRun) {
               LOGGER.error("Remote control server closed because of an IO exception", (Throwable)ioexception);
            }
         } finally {
            IOUtils.closeQuietly(this.serverSocket);
         }

         LOGGER.info("Remote control server is now stopped");
         this.wantsToRun = false;
         return;
      }
   }

   @Nullable
   private ChaseServer.PlayerPosition getPlayerPosition() {
      List<ServerPlayer> list = this.playerList.getPlayers();
      if (list.isEmpty()) {
         return null;
      } else {
         ServerPlayer serverplayer = list.get(0);
         String s = ChaseCommand.DIMENSION_NAMES.inverse().get(serverplayer.getLevel().dimension());
         return s == null ? null : new ChaseServer.PlayerPosition(s, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), serverplayer.getYRot(), serverplayer.getXRot());
      }
   }

   static record PlayerPosition(String dimensionName, double x, double y, double z, float yRot, float xRot) {
      String format() {
         return String.format(Locale.ROOT, "t %s %.2f %.2f %.2f %.2f %.2f\n", this.dimensionName, this.x, this.y, this.z, this.yRot, this.xRot);
      }
   }
}