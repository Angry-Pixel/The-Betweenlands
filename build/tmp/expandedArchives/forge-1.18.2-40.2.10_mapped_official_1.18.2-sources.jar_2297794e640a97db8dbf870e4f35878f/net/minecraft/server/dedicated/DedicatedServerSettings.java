package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.function.UnaryOperator;

public class DedicatedServerSettings {
   private final Path source;
   private DedicatedServerProperties properties;

   public DedicatedServerSettings(Path p_180932_) {
      this.source = p_180932_;
      this.properties = DedicatedServerProperties.fromFile(p_180932_);
   }

   public DedicatedServerProperties getProperties() {
      return this.properties;
   }

   public void forceSave() {
      this.properties.store(this.source);
   }

   public DedicatedServerSettings update(UnaryOperator<DedicatedServerProperties> p_139779_) {
      (this.properties = p_139779_.apply(this.properties)).store(this.source);
      return this;
   }
}