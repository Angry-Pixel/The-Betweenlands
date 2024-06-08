package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.Bootstrap;
import org.slf4j.Logger;

public class DataGenerator {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Collection<Path> inputFolders;
   private final Path outputFolder;
   private final List<DataProvider> providers = Lists.newArrayList();
   private final List<DataProvider> providerView = java.util.Collections.unmodifiableList(providers);

   public DataGenerator(Path p_123911_, Collection<Path> p_123912_) {
      this.outputFolder = p_123911_;
      this.inputFolders = Lists.newArrayList(p_123912_);
   }

   public Collection<Path> getInputFolders() {
      return this.inputFolders;
   }

   public Path getOutputFolder() {
      return this.outputFolder;
   }

   public void run() throws IOException {
      HashCache hashcache = new HashCache(this.outputFolder, "cache");
      hashcache.keep(this.getOutputFolder().resolve("version.json"));
      Stopwatch stopwatch = Stopwatch.createStarted();
      Stopwatch stopwatch1 = Stopwatch.createUnstarted();

      for(DataProvider dataprovider : this.providers) {
         LOGGER.info("Starting provider: {}", (Object)dataprovider.getName());
         net.minecraftforge.fml.StartupMessageManager.addModMessage("Generating: " + dataprovider.getName());
         stopwatch1.start();
         dataprovider.run(hashcache);
         stopwatch1.stop();
         LOGGER.info("{} finished after {} ms", dataprovider.getName(), stopwatch1.elapsed(TimeUnit.MILLISECONDS));
         stopwatch1.reset();
      }

      LOGGER.info("All providers took: {} ms", (long)stopwatch.elapsed(TimeUnit.MILLISECONDS));
      hashcache.purgeStaleAndWrite();
   }

   public void addProvider(DataProvider p_123915_) {
      this.providers.add(p_123915_);
   }

   public List<DataProvider> getProviders() {
       return this.providerView;
   }

   public void addInput(Path value) {
      this.inputFolders.add(value);
   }

   static {
      Bootstrap.bootStrap();
   }
}
