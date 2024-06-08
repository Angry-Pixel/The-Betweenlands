package net.minecraft.server.packs.metadata;

import com.google.gson.JsonObject;

public interface MetadataSectionSerializer<T> {
   String getMetadataSectionName();

   T fromJson(JsonObject p_10365_);
}