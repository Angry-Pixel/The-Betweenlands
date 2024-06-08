package net.minecraft.client.multiplayer.resolver;

import com.mojang.logging.LogUtils;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface ServerAddressResolver {
   Logger LOGGER = LogUtils.getLogger();
   ServerAddressResolver SYSTEM = (p_171878_) -> {
      try {
         InetAddress inetaddress = InetAddress.getByName(p_171878_.getHost());
         return Optional.of(ResolvedServerAddress.from(new InetSocketAddress(inetaddress, p_171878_.getPort())));
      } catch (UnknownHostException unknownhostexception) {
         LOGGER.debug("Couldn't resolve server {} address", p_171878_.getHost(), unknownhostexception);
         return Optional.empty();
      }
   };

   Optional<ResolvedServerAddress> resolve(ServerAddress p_171880_);
}