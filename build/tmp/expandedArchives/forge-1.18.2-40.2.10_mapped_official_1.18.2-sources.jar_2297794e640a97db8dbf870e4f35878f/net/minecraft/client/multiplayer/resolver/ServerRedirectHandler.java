package net.minecraft.client.multiplayer.resolver;

import com.mojang.logging.LogUtils;
import java.util.Hashtable;
import java.util.Optional;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface ServerRedirectHandler {
   Logger LOGGER = LogUtils.getLogger();
   ServerRedirectHandler EMPTY = (p_171897_) -> {
      return Optional.empty();
   };

   Optional<ServerAddress> lookupRedirect(ServerAddress p_171902_);

   static ServerRedirectHandler createDnsSrvRedirectHandler() {
      DirContext dircontext;
      try {
         String s = "com.sun.jndi.dns.DnsContextFactory";
         Class.forName("com.sun.jndi.dns.DnsContextFactory");
         Hashtable<String, String> hashtable = new Hashtable<>();
         hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         hashtable.put("java.naming.provider.url", "dns:");
         hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
         dircontext = new InitialDirContext(hashtable);
      } catch (Throwable throwable) {
         LOGGER.error("Failed to initialize SRV redirect resolved, some servers might not work", throwable);
         return EMPTY;
      }

      return (p_171900_) -> {
         if (p_171900_.getPort() == 25565) {
            try {
               Attributes attributes = dircontext.getAttributes("_minecraft._tcp." + p_171900_.getHost(), new String[]{"SRV"});
               Attribute attribute = attributes.get("srv");
               if (attribute != null) {
                  String[] astring = attribute.get().toString().split(" ", 4);
                  return Optional.of(new ServerAddress(astring[3], ServerAddress.parsePort(astring[2])));
               }
            } catch (Throwable throwable1) {
            }
         }

         return Optional.empty();
      };
   }
}