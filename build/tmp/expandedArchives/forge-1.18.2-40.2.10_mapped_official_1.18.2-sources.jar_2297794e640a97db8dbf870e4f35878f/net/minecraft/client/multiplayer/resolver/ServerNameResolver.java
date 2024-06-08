package net.minecraft.client.multiplayer.resolver;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerNameResolver {
   public static final ServerNameResolver DEFAULT = new ServerNameResolver(ServerAddressResolver.SYSTEM, ServerRedirectHandler.createDnsSrvRedirectHandler(), AddressCheck.createFromService());
   private final ServerAddressResolver resolver;
   private final ServerRedirectHandler redirectHandler;
   private final AddressCheck addressCheck;

   @VisibleForTesting
   ServerNameResolver(ServerAddressResolver p_171887_, ServerRedirectHandler p_171888_, AddressCheck p_171889_) {
      this.resolver = p_171887_;
      this.redirectHandler = p_171888_;
      this.addressCheck = p_171889_;
   }

   public Optional<ResolvedServerAddress> resolveAddress(ServerAddress p_171891_) {
      Optional<ResolvedServerAddress> optional = this.resolver.resolve(p_171891_);
      if ((!optional.isPresent() || this.addressCheck.isAllowed(optional.get())) && this.addressCheck.isAllowed(p_171891_)) {
         Optional<ServerAddress> optional1 = this.redirectHandler.lookupRedirect(p_171891_);
         if (optional1.isPresent()) {
            optional = this.resolver.resolve(optional1.get()).filter(this.addressCheck::isAllowed);
         }

         return optional;
      } else {
         return Optional.empty();
      }
   }
}