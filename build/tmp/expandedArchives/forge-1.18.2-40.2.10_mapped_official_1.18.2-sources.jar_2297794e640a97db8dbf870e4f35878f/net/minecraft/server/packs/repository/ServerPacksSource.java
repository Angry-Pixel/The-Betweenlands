package net.minecraft.server.packs.repository;

import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

public class ServerPacksSource implements RepositorySource {
   public static final PackMetadataSection BUILT_IN_METADATA = new PackMetadataSection(new TranslatableComponent("dataPack.vanilla.description"), PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()));
   public static final String VANILLA_ID = "vanilla";
   private final VanillaPackResources vanillaPack = new VanillaPackResources(BUILT_IN_METADATA, "minecraft");

   public void loadPacks(Consumer<Pack> p_10548_, Pack.PackConstructor p_10549_) {
      Pack pack = Pack.create("vanilla", false, () -> {
         return this.vanillaPack;
      }, p_10549_, Pack.Position.BOTTOM, PackSource.BUILT_IN);
      if (pack != null) {
         p_10548_.accept(pack);
      }

   }
}