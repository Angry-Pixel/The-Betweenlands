package net.minecraft.util.profiling.jfr.event;

import java.net.SocketAddress;
import jdk.jfr.Category;
import jdk.jfr.DataAmount;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;

@Category({"Minecraft", "Network"})
@StackTrace(false)
@Enabled(false)
public abstract class PacketEvent extends Event {
   @Name("protocolId")
   @Label("Protocol Id")
   public final int protocolId;
   @Name("packetId")
   @Label("Packet Id")
   public final int packetId;
   @Name("remoteAddress")
   @Label("Remote Address")
   public final String remoteAddress;
   @Name("bytes")
   @Label("Bytes")
   @DataAmount
   public final int bytes;

   public PacketEvent(int p_185419_, int p_185420_, SocketAddress p_185421_, int p_185422_) {
      this.protocolId = p_185419_;
      this.packetId = p_185420_;
      this.remoteAddress = p_185421_.toString();
      this.bytes = p_185422_;
   }

   public static final class Fields {
      public static final String REMOTE_ADDRESS = "remoteAddress";
      public static final String PROTOCOL_ID = "protocolId";
      public static final String PACKET_ID = "packetId";
      public static final String BYTES = "bytes";

      private Fields() {
      }
   }
}