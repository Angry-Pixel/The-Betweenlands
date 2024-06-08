package net.minecraft.network.protocol.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class ServerStatus {
   public static final int FAVICON_WIDTH = 64;
   public static final int FAVICON_HEIGHT = 64;
   @Nullable
   private Component description;
   @Nullable
   private ServerStatus.Players players;
   @Nullable
   private ServerStatus.Version version;
   @Nullable
   private String favicon;
   private transient net.minecraftforge.network.ServerStatusPing forgeData;

   public net.minecraftforge.network.ServerStatusPing getForgeData() {
      return this.forgeData;
   }

   public void setForgeData(net.minecraftforge.network.ServerStatusPing data){
      this.forgeData = data;
      invalidateJson();
   }

   @Nullable
   public Component getDescription() {
      return this.description;
   }

   public void setDescription(Component p_134909_) {
      this.description = p_134909_;
      invalidateJson();
   }

   @Nullable
   public ServerStatus.Players getPlayers() {
      return this.players;
   }

   public void setPlayers(ServerStatus.Players p_134911_) {
      this.players = p_134911_;
      invalidateJson();
   }

   @Nullable
   public ServerStatus.Version getVersion() {
      return this.version;
   }

   public void setVersion(ServerStatus.Version p_134913_) {
      this.version = p_134913_;
      invalidateJson();
   }

   public void setFavicon(String p_134907_) {
      this.favicon = p_134907_;
      invalidateJson();
   }

   @Nullable
   public String getFavicon() {
      return this.favicon;
   }

   private java.util.concurrent.Semaphore mutex = new java.util.concurrent.Semaphore(1);
   private String json = null;
   /**
    * Returns this object as a Json string.
    * Converting to JSON if a cached version is not available.
    *
    * Also to prevent potentially large memory allocations on the server
    * this is moved from the SPacketServerInfo writePacket function
    *
    * As this method is called from the impl threads so thread safety is important!
    */
   public String getJson() {
      String ret = this.json;
      if (ret == null) {
         mutex.acquireUninterruptibly();
         ret = this.json;
         if (ret == null) {
            ret = net.minecraft.network.protocol.status.ClientboundStatusResponsePacket.GSON.toJson(this);
            this.json = ret;
         }
         mutex.release();
      }
      return ret;
   }

   /**
    * Invalidates the cached json, causing the next call to getJson to rebuild it.
    * This is needed externally because PlayerCountData.setPlayer's is public.
    */
   public void invalidateJson() {
      this.json = null;
   }

   public static class Players {
      private final int maxPlayers;
      private final int numPlayers;
      @Nullable
      private GameProfile[] sample;

      public Players(int p_134921_, int p_134922_) {
         this.maxPlayers = p_134921_;
         this.numPlayers = p_134922_;
      }

      public int getMaxPlayers() {
         return this.maxPlayers;
      }

      public int getNumPlayers() {
         return this.numPlayers;
      }

      @Nullable
      public GameProfile[] getSample() {
         return this.sample;
      }

      public void setSample(GameProfile[] p_134925_) {
         this.sample = p_134925_;
      }

      public static class Serializer implements JsonDeserializer<ServerStatus.Players>, JsonSerializer<ServerStatus.Players> {
         public ServerStatus.Players deserialize(JsonElement p_134930_, Type p_134931_, JsonDeserializationContext p_134932_) throws JsonParseException {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(p_134930_, "players");
            ServerStatus.Players serverstatus$players = new ServerStatus.Players(GsonHelper.getAsInt(jsonobject, "max"), GsonHelper.getAsInt(jsonobject, "online"));
            if (GsonHelper.isArrayNode(jsonobject, "sample")) {
               JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "sample");
               if (jsonarray.size() > 0) {
                  GameProfile[] agameprofile = new GameProfile[jsonarray.size()];

                  for(int i = 0; i < agameprofile.length; ++i) {
                     JsonObject jsonobject1 = GsonHelper.convertToJsonObject(jsonarray.get(i), "player[" + i + "]");
                     String s = GsonHelper.getAsString(jsonobject1, "id");
                     agameprofile[i] = new GameProfile(UUID.fromString(s), GsonHelper.getAsString(jsonobject1, "name"));
                  }

                  serverstatus$players.setSample(agameprofile);
               }
            }

            return serverstatus$players;
         }

         public JsonElement serialize(ServerStatus.Players p_134934_, Type p_134935_, JsonSerializationContext p_134936_) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("max", p_134934_.getMaxPlayers());
            jsonobject.addProperty("online", p_134934_.getNumPlayers());
            GameProfile[] agameprofile = p_134934_.getSample();
            if (agameprofile != null && agameprofile.length > 0) {
               JsonArray jsonarray = new JsonArray();

               for(int i = 0; i < agameprofile.length; ++i) {
                  JsonObject jsonobject1 = new JsonObject();
                  UUID uuid = agameprofile[i].getId();
                  jsonobject1.addProperty("id", uuid == null ? "" : uuid.toString());
                  jsonobject1.addProperty("name", agameprofile[i].getName());
                  jsonarray.add(jsonobject1);
               }

               jsonobject.add("sample", jsonarray);
            }

            return jsonobject;
         }
      }
   }

   public static class Serializer implements JsonDeserializer<ServerStatus>, JsonSerializer<ServerStatus> {
      public ServerStatus deserialize(JsonElement p_134947_, Type p_134948_, JsonDeserializationContext p_134949_) throws JsonParseException {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_134947_, "status");
         ServerStatus serverstatus = new ServerStatus();
         if (jsonobject.has("description")) {
            serverstatus.setDescription(p_134949_.deserialize(jsonobject.get("description"), Component.class));
         }

         if (jsonobject.has("players")) {
            serverstatus.setPlayers(p_134949_.deserialize(jsonobject.get("players"), ServerStatus.Players.class));
         }

         if (jsonobject.has("version")) {
            serverstatus.setVersion(p_134949_.deserialize(jsonobject.get("version"), ServerStatus.Version.class));
         }

         if (jsonobject.has("favicon")) {
            serverstatus.setFavicon(GsonHelper.getAsString(jsonobject, "favicon"));
         }

         if (jsonobject.has("forgeData")) {
            serverstatus.setForgeData(net.minecraftforge.network.ServerStatusPing.Serializer.deserialize(GsonHelper.getAsJsonObject(jsonobject, "forgeData")));
         }

         return serverstatus;
      }

      public JsonElement serialize(ServerStatus p_134951_, Type p_134952_, JsonSerializationContext p_134953_) {
         JsonObject jsonobject = new JsonObject();
         if (p_134951_.getDescription() != null) {
            jsonobject.add("description", p_134953_.serialize(p_134951_.getDescription()));
         }

         if (p_134951_.getPlayers() != null) {
            jsonobject.add("players", p_134953_.serialize(p_134951_.getPlayers()));
         }

         if (p_134951_.getVersion() != null) {
            jsonobject.add("version", p_134953_.serialize(p_134951_.getVersion()));
         }

         if (p_134951_.getFavicon() != null) {
            jsonobject.addProperty("favicon", p_134951_.getFavicon());
         }

         if(p_134951_.getForgeData() != null){
            jsonobject.add("forgeData", net.minecraftforge.network.ServerStatusPing.Serializer.serialize(p_134951_.getForgeData()));
         }

         return jsonobject;
      }
   }

   public static class Version {
      private final String name;
      private final int protocol;

      public Version(String p_134965_, int p_134966_) {
         this.name = p_134965_;
         this.protocol = p_134966_;
      }

      public String getName() {
         return this.name;
      }

      public int getProtocol() {
         return this.protocol;
      }

      public static class Serializer implements JsonDeserializer<ServerStatus.Version>, JsonSerializer<ServerStatus.Version> {
         public ServerStatus.Version deserialize(JsonElement p_134971_, Type p_134972_, JsonDeserializationContext p_134973_) throws JsonParseException {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(p_134971_, "version");
            return new ServerStatus.Version(GsonHelper.getAsString(jsonobject, "name"), GsonHelper.getAsInt(jsonobject, "protocol"));
         }

         public JsonElement serialize(ServerStatus.Version p_134975_, Type p_134976_, JsonSerializationContext p_134977_) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("name", p_134975_.getName());
            jsonobject.addProperty("protocol", p_134975_.getProtocol());
            return jsonobject;
         }
      }
   }
}
