package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
   private static final float AVERAGE_PACKETS_SMOOTHING = 0.75F;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Marker ROOT_MARKER = MarkerFactory.getMarker("NETWORK");
   public static final Marker PACKET_MARKER = Util.make(MarkerFactory.getMarker("NETWORK_PACKETS"), (p_202569_) -> {
      p_202569_.add(ROOT_MARKER);
   });
   public static final Marker PACKET_RECEIVED_MARKER = Util.make(MarkerFactory.getMarker("PACKET_RECEIVED"), (p_202562_) -> {
      p_202562_.add(PACKET_MARKER);
   });
   public static final Marker PACKET_SENT_MARKER = Util.make(MarkerFactory.getMarker("PACKET_SENT"), (p_202557_) -> {
      p_202557_.add(PACKET_MARKER);
   });
   public static final AttributeKey<ConnectionProtocol> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
   public static final LazyLoadedValue<NioEventLoopGroup> NETWORK_WORKER_GROUP = new LazyLoadedValue<>(() -> {
      return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
   });
   public static final LazyLoadedValue<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = new LazyLoadedValue<>(() -> {
      return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
   });
   public static final LazyLoadedValue<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = new LazyLoadedValue<>(() -> {
      return new DefaultEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
   });
   private final PacketFlow receiving;
   private final Queue<Connection.PacketHolder> queue = Queues.newConcurrentLinkedQueue();
   private Channel channel;
   private SocketAddress address;
   private PacketListener packetListener;
   private Component disconnectedReason;
   private boolean encrypted;
   private boolean disconnectionHandled;
   private int receivedPackets;
   private int sentPackets;
   private float averageReceivedPackets;
   private float averageSentPackets;
   private int tickCount;
   private boolean handlingFault;
   private java.util.function.Consumer<Connection> activationHandler;

   public Connection(PacketFlow p_129482_) {
      this.receiving = p_129482_;
   }

   public void channelActive(ChannelHandlerContext p_129525_) throws Exception {
      super.channelActive(p_129525_);
      this.channel = p_129525_.channel();
      this.address = this.channel.remoteAddress();
      if (activationHandler != null) activationHandler.accept(this);

      try {
         this.setProtocol(ConnectionProtocol.HANDSHAKING);
      } catch (Throwable throwable) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Failed to change protocol to handshake", throwable);
      }

   }

   public void setProtocol(ConnectionProtocol p_129499_) {
      this.channel.attr(ATTRIBUTE_PROTOCOL).set(p_129499_);
      this.channel.config().setAutoRead(true);
      LOGGER.debug("Enabled auto read");
   }

   public void channelInactive(ChannelHandlerContext p_129527_) {
      this.disconnect(new TranslatableComponent("disconnect.endOfStream"));
   }

   public void exceptionCaught(ChannelHandlerContext p_129533_, Throwable p_129534_) {
      if (p_129534_ instanceof SkipPacketException) {
         LOGGER.debug("Skipping packet due to errors", p_129534_.getCause());
      } else {
         boolean flag = !this.handlingFault;
         this.handlingFault = true;
         if (this.channel.isOpen()) {
            if (p_129534_ instanceof TimeoutException) {
               LOGGER.debug("Timeout", p_129534_);
               this.disconnect(new TranslatableComponent("disconnect.timeout"));
            } else {
               Component component = new TranslatableComponent("disconnect.genericReason", "Internal Exception: " + p_129534_);
               if (flag) {
                  LOGGER.debug("Failed to sent packet", p_129534_);
                  ConnectionProtocol connectionprotocol = this.getCurrentProtocol();
                  Packet<?> packet = (Packet<?>)(connectionprotocol == ConnectionProtocol.LOGIN ? new ClientboundLoginDisconnectPacket(component) : new ClientboundDisconnectPacket(component));
                  this.send(packet, (p_202560_) -> {
                     this.disconnect(component);
                  });
                  this.setReadOnly();
               } else {
                  LOGGER.debug("Double fault", p_129534_);
                  this.disconnect(component);
               }
            }

         }
      }
   }

   protected void channelRead0(ChannelHandlerContext p_129487_, Packet<?> p_129488_) {
      if (this.channel.isOpen()) {
         try {
            genericsFtw(p_129488_, this.packetListener);
         } catch (RunningOnDifferentThreadException runningondifferentthreadexception) {
         } catch (RejectedExecutionException rejectedexecutionexception) {
            this.disconnect(new TranslatableComponent("multiplayer.disconnect.server_shutdown"));
         } catch (ClassCastException classcastexception) {
            LOGGER.error("Received {} that couldn't be processed", p_129488_.getClass(), classcastexception);
            this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_packet"));
         }

         ++this.receivedPackets;
      }

   }

   private static <T extends PacketListener> void genericsFtw(Packet<T> p_129518_, PacketListener p_129519_) {
      p_129518_.handle((T)p_129519_);
   }

   public void setListener(PacketListener p_129506_) {
      Validate.notNull(p_129506_, "packetListener");
      this.packetListener = p_129506_;
   }

   public void send(Packet<?> p_129513_) {
      this.send(p_129513_, (GenericFutureListener<? extends Future<? super Void>>)null);
   }

   public void send(Packet<?> p_129515_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_129516_) {
      if (this.isConnected()) {
         this.flushQueue();
         this.sendPacket(p_129515_, p_129516_);
      } else {
         this.queue.add(new Connection.PacketHolder(p_129515_, p_129516_));
      }

   }

   private void sendPacket(Packet<?> p_129521_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_129522_) {
      ConnectionProtocol connectionprotocol = ConnectionProtocol.getProtocolForPacket(p_129521_);
      ConnectionProtocol connectionprotocol1 = this.getCurrentProtocol();
      ++this.sentPackets;
      if (connectionprotocol1 != connectionprotocol) {
         LOGGER.debug("Disabled auto read");
         this.channel.eventLoop().execute(()->this.channel.config().setAutoRead(false));
      }

      if (this.channel.eventLoop().inEventLoop()) {
         this.doSendPacket(p_129521_, p_129522_, connectionprotocol, connectionprotocol1);
      } else {
         this.channel.eventLoop().execute(() -> {
            this.doSendPacket(p_129521_, p_129522_, connectionprotocol, connectionprotocol1);
         });
      }

   }

   private void doSendPacket(Packet<?> p_178304_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_178305_, ConnectionProtocol p_178306_, ConnectionProtocol p_178307_) {
      if (p_178306_ != p_178307_) {
         this.setProtocol(p_178306_);
      }

      ChannelFuture channelfuture = this.channel.writeAndFlush(p_178304_);
      if (p_178305_ != null) {
         channelfuture.addListener(p_178305_);
      }

      channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   private ConnectionProtocol getCurrentProtocol() {
      return this.channel.attr(ATTRIBUTE_PROTOCOL).get();
   }

   private void flushQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         synchronized(this.queue) {
            Connection.PacketHolder connection$packetholder;
            while((connection$packetholder = this.queue.poll()) != null) {
               this.sendPacket(connection$packetholder.packet, connection$packetholder.listener);
            }

         }
      }
   }

   public void tick() {
      this.flushQueue();
      if (this.packetListener instanceof ServerLoginPacketListenerImpl) {
         ((ServerLoginPacketListenerImpl)this.packetListener).tick();
      }

      if (this.packetListener instanceof ServerGamePacketListenerImpl) {
         ((ServerGamePacketListenerImpl)this.packetListener).tick();
      }

      if (!this.isConnected() && !this.disconnectionHandled) {
         this.handleDisconnection();
      }

      if (this.channel != null) {
         this.channel.flush();
      }

      if (this.tickCount++ % 20 == 0) {
         this.tickSecond();
      }

   }

   protected void tickSecond() {
      this.averageSentPackets = Mth.lerp(0.75F, (float)this.sentPackets, this.averageSentPackets);
      this.averageReceivedPackets = Mth.lerp(0.75F, (float)this.receivedPackets, this.averageReceivedPackets);
      this.sentPackets = 0;
      this.receivedPackets = 0;
   }

   public SocketAddress getRemoteAddress() {
      return this.address;
   }

   public void disconnect(Component p_129508_) {
      if (this.channel.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.disconnectedReason = p_129508_;
      }

   }

   public boolean isMemoryConnection() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   public PacketFlow getReceiving() {
      return this.receiving;
   }

   public PacketFlow getSending() {
      return this.receiving.getOpposite();
   }

   public static Connection connectToServer(InetSocketAddress p_178301_, boolean p_178302_) {
      net.minecraftforge.network.DualStackUtils.checkIPv6(p_178301_.getAddress());
      final Connection connection = new Connection(PacketFlow.CLIENTBOUND);
      connection.activationHandler = net.minecraftforge.network.NetworkHooks::registerClientLoginChannel;
      Class<? extends SocketChannel> oclass;
      LazyLoadedValue<? extends EventLoopGroup> lazyloadedvalue;
      if (Epoll.isAvailable() && p_178302_) {
         oclass = EpollSocketChannel.class;
         lazyloadedvalue = NETWORK_EPOLL_WORKER_GROUP;
      } else {
         oclass = NioSocketChannel.class;
         lazyloadedvalue = NETWORK_WORKER_GROUP;
      }

      (new Bootstrap()).group(lazyloadedvalue.get()).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel p_129552_) {
            try {
               p_129552_.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException channelexception) {
            }

            p_129552_.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new Varint21FrameDecoder()).addLast("decoder", new PacketDecoder(PacketFlow.CLIENTBOUND)).addLast("prepender", new Varint21LengthFieldPrepender()).addLast("encoder", new PacketEncoder(PacketFlow.SERVERBOUND)).addLast("packet_handler", connection);
         }
      }).channel(oclass).connect(p_178301_.getAddress(), p_178301_.getPort()).syncUninterruptibly();
      return connection;
   }

   public static Connection connectToLocalServer(SocketAddress p_129494_) {
      final Connection connection = new Connection(PacketFlow.CLIENTBOUND);
      connection.activationHandler = net.minecraftforge.network.NetworkHooks::registerClientLoginChannel;
      (new Bootstrap()).group(LOCAL_WORKER_GROUP.get()).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel p_129557_) {
            p_129557_.pipeline().addLast("packet_handler", connection);
         }
      }).channel(LocalChannel.class).connect(p_129494_).syncUninterruptibly();
      return connection;
   }

   public void setEncryptionKey(Cipher p_129496_, Cipher p_129497_) {
      this.encrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new CipherDecoder(p_129496_));
      this.channel.pipeline().addBefore("prepender", "encrypt", new CipherEncoder(p_129497_));
   }

   public boolean isEncrypted() {
      return this.encrypted;
   }

   public boolean isConnected() {
      return this.channel != null && this.channel.isOpen();
   }

   public boolean isConnecting() {
      return this.channel == null;
   }

   public PacketListener getPacketListener() {
      return this.packetListener;
   }

   @Nullable
   public Component getDisconnectedReason() {
      return this.disconnectedReason;
   }

   public void setReadOnly() {
      this.channel.config().setAutoRead(false);
   }

   public void setupCompression(int p_129485_, boolean p_182682_) {
      if (p_129485_ >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
            ((CompressionDecoder)this.channel.pipeline().get("decompress")).setThreshold(p_129485_, p_182682_);
         } else {
            this.channel.pipeline().addBefore("decoder", "decompress", new CompressionDecoder(p_129485_, p_182682_));
         }

         if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
            ((CompressionEncoder)this.channel.pipeline().get("compress")).setThreshold(p_129485_);
         } else {
            this.channel.pipeline().addBefore("encoder", "compress", new CompressionEncoder(p_129485_));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }

   }

   public void handleDisconnection() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (this.disconnectionHandled) {
            LOGGER.warn("handleDisconnection() called twice");
         } else {
            this.disconnectionHandled = true;
            if (this.getDisconnectedReason() != null) {
               this.getPacketListener().onDisconnect(this.getDisconnectedReason());
            } else if (this.getPacketListener() != null) {
               this.getPacketListener().onDisconnect(new TranslatableComponent("multiplayer.disconnect.generic"));
            }
         }

      }
   }

   public float getAverageReceivedPackets() {
      return this.averageReceivedPackets;
   }

   public float getAverageSentPackets() {
      return this.averageSentPackets;
   }

   public Channel channel() {
      return channel;
   }

   public PacketFlow getDirection() {
      return this.receiving;
   }

   static class PacketHolder {
      final Packet<?> packet;
      @Nullable
      final GenericFutureListener<? extends Future<? super Void>> listener;

      public PacketHolder(Packet<?> p_129561_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_129562_) {
         this.packet = p_129561_;
         this.listener = p_129562_;
      }
   }
}
