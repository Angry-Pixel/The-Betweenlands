package net.minecraft.client.multiplayer.resolver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.blocklist.BlockListSupplier;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Predicate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface AddressCheck {
   boolean isAllowed(ResolvedServerAddress p_171829_);

   boolean isAllowed(ServerAddress p_171830_);

   static AddressCheck createFromService() {
      final ImmutableList<Predicate<String>> immutablelist = Streams.stream(ServiceLoader.load(BlockListSupplier.class)).map(BlockListSupplier::createBlockList).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
      return new AddressCheck() {
         public boolean isAllowed(ResolvedServerAddress p_171835_) {
            String s = p_171835_.getHostName();
            String s1 = p_171835_.getHostIp();
            return immutablelist.stream().noneMatch((p_171841_) -> {
               return p_171841_.test(s) || p_171841_.test(s1);
            });
         }

         public boolean isAllowed(ServerAddress p_171837_) {
            String s = p_171837_.getHost();
            return immutablelist.stream().noneMatch((p_171844_) -> {
               return p_171844_.test(s);
            });
         }
      };
   }
}