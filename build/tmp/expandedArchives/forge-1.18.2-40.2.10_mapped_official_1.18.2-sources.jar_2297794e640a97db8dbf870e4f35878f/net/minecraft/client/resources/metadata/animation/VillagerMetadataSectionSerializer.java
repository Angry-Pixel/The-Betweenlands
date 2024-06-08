package net.minecraft.client.resources.metadata.animation;

import com.google.gson.JsonObject;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerMetadataSectionSerializer implements MetadataSectionSerializer<VillagerMetaDataSection> {
   public VillagerMetaDataSection fromJson(JsonObject p_119095_) {
      return new VillagerMetaDataSection(VillagerMetaDataSection.Hat.getByName(GsonHelper.getAsString(p_119095_, "hat", "none")));
   }

   public String getMetadataSectionName() {
      return "villager";
   }
}