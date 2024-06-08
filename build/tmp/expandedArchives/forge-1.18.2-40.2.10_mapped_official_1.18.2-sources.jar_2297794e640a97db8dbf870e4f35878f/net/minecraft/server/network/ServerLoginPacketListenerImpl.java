package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ServerLoginPacketListenerImpl implements ServerLoginPacketListener {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_TICKS_BEFORE_LOGIN = 600;
   private static final Random RANDOM = new Random();
   private final byte[] nonce = new byte[4];
   final MinecraftServer server;
   public final Connection connection;
   ServerLoginPacketListenerImpl.State state = ServerLoginPacketListenerImpl.State.HELLO;
   private int tick;
   @Nullable
   public GameProfile gameProfile;
   private final String serverId = "";
   @Nullable
   private ServerPlayer delayedAcceptPlayer;

   public ServerLoginPacketListenerImpl(MinecraftServer p_10027_, Connection p_10028_) {
      this.server = p_10027_;
      this.connection = p_10028_;
      RANDOM.nextBytes(this.nonce);
   }

   public void tick() {
      if (this.state == State.NEGOTIATING) {
         // We force the state into "NEGOTIATING" which is otherwise unused. Once we're completed we move the negotiation onto "READY_TO_ACCEPT"
         // Might want to promote player object creation to here as well..
         boolean negotiationComplete = net.minecraftforge.network.NetworkHooks.tickNegotiation(this, this.connection, this.delayedAcceptPlayer);
         if (negotiationComplete)
            this.state = State.READY_TO_ACCEPT;
      } else if (this.state == ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT) {
         this.handleAcceptedLogin();
      } else if (this.state == ServerLoginPacketListenerImpl.State.DELAY_ACCEPT) {
         ServerPlayer serverplayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId());
         if (serverplayer == null) {
            this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
            this.placeNewPlayer(this.delayedAcceptPlayer);
            this.delayedAcceptPlayer = null;
         }
      }

      if (this.tick++ == 600) {
         this.disconnect(new TranslatableComponent("multiplayer.disconnect.slow_login"));
      }

   }

   public Connection getConnection() {
      return this.connection;
   }

   public void disconnect(Component p_10054_) {
      try {
         LOGGER.info("Disconnecting {}: {}", this.getUserName(), p_10054_.getString());
         this.connection.send(new ClientboundLoginDisconnectPacket(p_10054_));
         this.connection.disconnect(p_10054_);
      } catch (Exception exception) {
         LOGGER.error("Error whilst disconnecting player", (Throwable)exception);
      }

   }

   public void handleAcceptedLogin() {
      if (!this.gameProfile.isComplete()) {
         this.gameProfile = this.createFakeProfile(this.gameProfile);
      }

      Component component = this.server.getPlayerList().canPlayerLogin(this.connection.getRemoteAddress(), this.gameProfile);
      if (component != null) {
         this.disconnect(component);
      } else {
         this.state = ServerLoginPacketListenerImpl.State.ACCEPTED;
         if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
            this.connection.send(new ClientboundLoginCompressionPacket(this.server.getCompressionThreshold()), (p_10041_) -> {
               this.connection.setupCompression(this.server.getCompressionThreshold(), true);
            });
         }

         this.connection.send(new ClientboundGameProfilePacket(this.gameProfile));
         ServerPlayer serverplayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId());

         try {
            ServerPlayer serverplayer1 = this.server.getPlayerList().getPlayerForLogin(this.gameProfile);
            if (serverplayer != null) {
               this.state = ServerLoginPacketListenerImpl.State.DELAY_ACCEPT;
               this.delayedAcceptPlayer = serverplayer1;
            } else {
               this.placeNewPlayer(serverplayer1);
            }
         } catch (Exception exception) {
            LOGGER.error("Couldn't place player in world", (Throwable)exception);
            Component component1 = new TranslatableComponent("multiplayer.disconnect.invalid_player_data");
            this.connection.send(new ClientboundDisconnectPacket(component1));
            this.connection.disconnect(component1);
         }
      }

   }

   private void placeNewPlayer(ServerPlayer p_143700_) {
      this.server.getPlayerList().placeNewPlayer(this.connection, p_143700_);
   }

   public void onDisconnect(Component p_10043_) {
      LOGGER.info("{} lost connection: {}", this.getUserName(), p_10043_.getString());
   }

   public String getUserName() {
      return this.gameProfile != null ? this.gameProfile + " (" + this.connection.getRemoteAddress() + ")" : String.valueOf((Object)this.connection.getRemoteAddress());
   }

   public void handleHello(ServerboundHelloPacket p_10047_) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.HELLO, "Unexpected hello packet");
      this.gameProfile = p_10047_.getGameProfile();
      Validate.validState(isValidUsername(this.gameProfile.getName()), "Invalid characters in username");
      if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
         this.state = ServerLoginPacketListenerImpl.State.KEY;
         this.connection.send(new ClientboundHelloPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
      } else {
         this.state = ServerLoginPacketListenerImpl.State.NEGOTIATING;
      }

   }

   public static boolean isValidUsername(String p_203793_) {
      return p_203793_.chars().filter((p_203791_) -> {
         return p_203791_ <= 32 || p_203791_ >= 127;
      }).findAny().isEmpty();
   }

   public void handleKey(ServerboundKeyPacket p_10049_) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.KEY, "Unexpected key packet");
      PrivateKey privatekey = this.server.getKeyPair().getPrivate();

      final String s;
      try {
         if (!Arrays.equals(this.nonce, p_10049_.getNonce(privatekey))) {
            throw new IllegalStateException("Protocol error");
         }

         SecretKey secretkey = p_10049_.getSecretKey(privatekey);
         Cipher cipher = Crypt.getCipher(2, secretkey);
         Cipher cipher1 = Crypt.getCipher(1, secretkey);
         s = (new BigInteger(Crypt.digestData("", this.server.getKeyPair().getPublic(), secretkey))).toString(16);
         this.state = ServerLoginPacketListenerImpl.State.AUTHENTICATING;
         this.connection.setEncryptionKey(cipher, cipher1);
      } catch (CryptException cryptexception) {
         throw new IllegalStateException("Protocol error", cryptexception);
      }

      Thread thread = new Thread(net.minecraftforge.fml.util.thread.SidedThreadGroups.SERVER, "User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         public void run() {
            GameProfile gameprofile = ServerLoginPacketListenerImpl.this.gameProfile;

            try {
               ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.server.getSessionService().hasJoinedServer(new GameProfile((UUID)null, gameprofile.getName()), s, this.getAddress());
               if (ServerLoginPacketListenerImpl.this.gameProfile != null) {
                  ServerLoginPacketListenerImpl.LOGGER.info("UUID of player {} is {}", ServerLoginPacketListenerImpl.this.gameProfile.getName(), ServerLoginPacketListenerImpl.this.gameProfile.getId());
                  ServerLoginPacketListenerImpl.this.state = ServerLoginPacketListenerImpl.State.NEGOTIATING;
               } else if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Failed to verify username but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.createFakeProfile(gameprofile);
                  ServerLoginPacketListenerImpl.this.state = ServerLoginPacketListenerImpl.State.NEGOTIATING;
               } else {
                  ServerLoginPacketListenerImpl.this.disconnect(new TranslatableComponent("multiplayer.disconnect.unverified_username"));
                  ServerLoginPacketListenerImpl.LOGGER.error("Username '{}' tried to join with an invalid session", (Object)gameprofile.getName());
               }
            } catch (AuthenticationUnavailableException authenticationunavailableexception) {
               if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.createFakeProfile(gameprofile);
                  ServerLoginPacketListenerImpl.this.state = ServerLoginPacketListenerImpl.State.NEGOTIATING;
               } else {
                  ServerLoginPacketListenerImpl.this.disconnect(new TranslatableComponent("multiplayer.disconnect.authservers_down"));
                  ServerLoginPacketListenerImpl.LOGGER.error("Couldn't verify username because servers are unavailable");
               }
            }

         }

         @Nullable
         private InetAddress getAddress() {
            SocketAddress socketaddress = ServerLoginPacketListenerImpl.this.connection.getRemoteAddress();
            return ServerLoginPacketListenerImpl.this.server.getPreventProxyConnections() && socketaddress instanceof InetSocketAddress ? ((InetSocketAddress)socketaddress).getAddress() : null;
         }
      };
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   public void handleCustomQueryPacket(ServerboundCustomQueryPacket p_10045_) {
      if (!net.minecraftforge.network.NetworkHooks.onCustomPayload(p_10045_, this.connection))
      this.disconnect(new TranslatableComponent("multiplayer.disconnect.unexpected_query_response"));
   }

   protected GameProfile createFakeProfile(GameProfile p_10039_) {
      UUID uuid = Player.createPlayerUUID(p_10039_.getName());
      return new GameProfile(uuid, p_10039_.getName());
   }

   static enum State {
      HELLO,
      KEY,
      AUTHENTICATING,
      NEGOTIATING,
      READY_TO_ACCEPT,
      DELAY_ACCEPT,
      ACCEPTED;
   }
}
