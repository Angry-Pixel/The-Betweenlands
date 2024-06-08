package net.minecraft.client.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultClientPackResources extends VanillaPackResources {
   private final AssetIndex assetIndex;

   public DefaultClientPackResources(PackMetadataSection p_174827_, AssetIndex p_174828_) {
      super(p_174827_, "minecraft", "realms");
      this.assetIndex = p_174828_;
   }

   @Nullable
   protected InputStream getResourceAsStream(PackType p_118621_, ResourceLocation p_118622_) {
      if (p_118621_ == PackType.CLIENT_RESOURCES) {
         File file1 = this.assetIndex.getFile(p_118622_);
         if (file1 != null && file1.exists()) {
            try {
               return new FileInputStream(file1);
            } catch (FileNotFoundException filenotfoundexception) {
            }
         }
      }

      return super.getResourceAsStream(p_118621_, p_118622_);
   }

   public boolean hasResource(PackType p_118618_, ResourceLocation p_118619_) {
      if (p_118618_ == PackType.CLIENT_RESOURCES) {
         File file1 = this.assetIndex.getFile(p_118619_);
         if (file1 != null && file1.exists()) {
            return true;
         }
      }

      return super.hasResource(p_118618_, p_118619_);
   }

   @Nullable
   protected InputStream getResourceAsStream(String p_118616_) {
      File file1 = this.assetIndex.getRootFile(p_118616_);
      if (file1 != null && file1.exists()) {
         try {
            return new FileInputStream(file1);
         } catch (FileNotFoundException filenotfoundexception) {
         }
      }

      return super.getResourceAsStream(p_118616_);
   }

   public Collection<ResourceLocation> getResources(PackType p_118610_, String p_118611_, String p_118612_, int p_118613_, Predicate<String> p_118614_) {
      Collection<ResourceLocation> collection = super.getResources(p_118610_, p_118611_, p_118612_, p_118613_, p_118614_);
      collection.addAll(this.assetIndex.getFiles(p_118612_, p_118611_, p_118613_, p_118614_));
      return collection;
   }
}