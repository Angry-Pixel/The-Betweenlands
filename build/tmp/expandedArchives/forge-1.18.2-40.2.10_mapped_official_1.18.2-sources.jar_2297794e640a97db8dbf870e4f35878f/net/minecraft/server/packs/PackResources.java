package net.minecraft.server.packs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public interface PackResources extends AutoCloseable, net.minecraftforge.common.extensions.IForgePackResources {
   String METADATA_EXTENSION = ".mcmeta";
   String PACK_META = "pack.mcmeta";

   @Nullable
   InputStream getRootResource(String p_10294_) throws IOException;

   InputStream getResource(PackType p_10289_, ResourceLocation p_10290_) throws IOException;

   Collection<ResourceLocation> getResources(PackType p_10284_, String p_10285_, String p_10286_, int p_10287_, Predicate<String> p_10288_);

   boolean hasResource(PackType p_10292_, ResourceLocation p_10293_);

   Set<String> getNamespaces(PackType p_10283_);

   @Nullable
   <T> T getMetadataSection(MetadataSectionSerializer<T> p_10291_) throws IOException;

   String getName();

   void close();
}
