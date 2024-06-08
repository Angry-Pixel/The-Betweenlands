package net.minecraft.client.resources.metadata.texture;

import com.google.gson.JsonObject;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureMetadataSectionSerializer implements MetadataSectionSerializer<TextureMetadataSection> {
   public TextureMetadataSection fromJson(JsonObject p_119122_) {
      boolean flag = GsonHelper.getAsBoolean(p_119122_, "blur", false);
      boolean flag1 = GsonHelper.getAsBoolean(p_119122_, "clamp", false);
      return new TextureMetadataSection(flag, flag1);
   }

   public String getMetadataSectionName() {
      return "texture";
   }
}