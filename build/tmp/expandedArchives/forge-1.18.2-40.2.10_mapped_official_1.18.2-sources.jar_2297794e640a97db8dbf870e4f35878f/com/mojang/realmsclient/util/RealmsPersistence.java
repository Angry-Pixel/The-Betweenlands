package com.mojang.realmsclient.util;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;

@OnlyIn(Dist.CLIENT)
public class RealmsPersistence {
   private static final String FILE_NAME = "realms_persistence.json";
   private static final GuardedSerializer GSON = new GuardedSerializer();

   public RealmsPersistence.RealmsPersistenceData read() {
      return readFile();
   }

   public void save(RealmsPersistence.RealmsPersistenceData p_167617_) {
      writeFile(p_167617_);
   }

   public static RealmsPersistence.RealmsPersistenceData readFile() {
      File file1 = getPathToData();

      try {
         String s = FileUtils.readFileToString(file1, StandardCharsets.UTF_8);
         RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = GSON.fromJson(s, RealmsPersistence.RealmsPersistenceData.class);
         return realmspersistence$realmspersistencedata != null ? realmspersistence$realmspersistencedata : new RealmsPersistence.RealmsPersistenceData();
      } catch (IOException ioexception) {
         return new RealmsPersistence.RealmsPersistenceData();
      }
   }

   public static void writeFile(RealmsPersistence.RealmsPersistenceData p_90173_) {
      File file1 = getPathToData();

      try {
         FileUtils.writeStringToFile(file1, GSON.toJson(p_90173_), StandardCharsets.UTF_8);
      } catch (IOException ioexception) {
      }

   }

   private static File getPathToData() {
      return new File(Minecraft.getInstance().gameDirectory, "realms_persistence.json");
   }

   @OnlyIn(Dist.CLIENT)
   public static class RealmsPersistenceData implements ReflectionBasedSerialization {
      @SerializedName("newsLink")
      public String newsLink;
      @SerializedName("hasUnreadNews")
      public boolean hasUnreadNews;
   }
}