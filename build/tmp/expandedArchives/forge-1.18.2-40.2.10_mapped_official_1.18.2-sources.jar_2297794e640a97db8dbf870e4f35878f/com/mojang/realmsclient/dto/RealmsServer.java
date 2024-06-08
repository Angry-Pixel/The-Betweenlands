package com.mojang.realmsclient.dto;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServer extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public long id;
   public String remoteSubscriptionId;
   public String name;
   public String motd;
   public RealmsServer.State state;
   public String owner;
   public String ownerUUID;
   public List<PlayerInfo> players;
   public Map<Integer, RealmsWorldOptions> slots;
   public boolean expired;
   public boolean expiredTrial;
   public int daysLeft;
   public RealmsServer.WorldType worldType;
   public int activeSlot;
   public String minigameName;
   public int minigameId;
   public String minigameImage;
   public RealmsServerPing serverPing = new RealmsServerPing();

   public String getDescription() {
      return this.motd;
   }

   public String getName() {
      return this.name;
   }

   public String getMinigameName() {
      return this.minigameName;
   }

   public void setName(String p_87509_) {
      this.name = p_87509_;
   }

   public void setDescription(String p_87516_) {
      this.motd = p_87516_;
   }

   public void updateServerPing(RealmsServerPlayerList p_87507_) {
      List<String> list = Lists.newArrayList();
      int i = 0;

      for(String s : p_87507_.players) {
         if (!s.equals(Minecraft.getInstance().getUser().getUuid())) {
            String s1 = "";

            try {
               s1 = RealmsUtil.uuidToName(s);
            } catch (Exception exception) {
               LOGGER.error("Could not get name for {}", s, exception);
               continue;
            }

            list.add(s1);
            ++i;
         }
      }

      this.serverPing.nrOfPlayers = String.valueOf(i);
      this.serverPing.playerList = Joiner.on('\n').join(list);
   }

   public static RealmsServer parse(JsonObject p_87500_) {
      RealmsServer realmsserver = new RealmsServer();

      try {
         realmsserver.id = JsonUtils.getLongOr("id", p_87500_, -1L);
         realmsserver.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", p_87500_, (String)null);
         realmsserver.name = JsonUtils.getStringOr("name", p_87500_, (String)null);
         realmsserver.motd = JsonUtils.getStringOr("motd", p_87500_, (String)null);
         realmsserver.state = getState(JsonUtils.getStringOr("state", p_87500_, RealmsServer.State.CLOSED.name()));
         realmsserver.owner = JsonUtils.getStringOr("owner", p_87500_, (String)null);
         if (p_87500_.get("players") != null && p_87500_.get("players").isJsonArray()) {
            realmsserver.players = parseInvited(p_87500_.get("players").getAsJsonArray());
            sortInvited(realmsserver);
         } else {
            realmsserver.players = Lists.newArrayList();
         }

         realmsserver.daysLeft = JsonUtils.getIntOr("daysLeft", p_87500_, 0);
         realmsserver.expired = JsonUtils.getBooleanOr("expired", p_87500_, false);
         realmsserver.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", p_87500_, false);
         realmsserver.worldType = getWorldType(JsonUtils.getStringOr("worldType", p_87500_, RealmsServer.WorldType.NORMAL.name()));
         realmsserver.ownerUUID = JsonUtils.getStringOr("ownerUUID", p_87500_, "");
         if (p_87500_.get("slots") != null && p_87500_.get("slots").isJsonArray()) {
            realmsserver.slots = parseSlots(p_87500_.get("slots").getAsJsonArray());
         } else {
            realmsserver.slots = createEmptySlots();
         }

         realmsserver.minigameName = JsonUtils.getStringOr("minigameName", p_87500_, (String)null);
         realmsserver.activeSlot = JsonUtils.getIntOr("activeSlot", p_87500_, -1);
         realmsserver.minigameId = JsonUtils.getIntOr("minigameId", p_87500_, -1);
         realmsserver.minigameImage = JsonUtils.getStringOr("minigameImage", p_87500_, (String)null);
      } catch (Exception exception) {
         LOGGER.error("Could not parse McoServer: {}", (Object)exception.getMessage());
      }

      return realmsserver;
   }

   private static void sortInvited(RealmsServer p_87505_) {
      p_87505_.players.sort((p_87502_, p_87503_) -> {
         return ComparisonChain.start().compareFalseFirst(p_87503_.getAccepted(), p_87502_.getAccepted()).compare(p_87502_.getName().toLowerCase(Locale.ROOT), p_87503_.getName().toLowerCase(Locale.ROOT)).result();
      });
   }

   private static List<PlayerInfo> parseInvited(JsonArray p_87498_) {
      List<PlayerInfo> list = Lists.newArrayList();

      for(JsonElement jsonelement : p_87498_) {
         try {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            PlayerInfo playerinfo = new PlayerInfo();
            playerinfo.setName(JsonUtils.getStringOr("name", jsonobject, (String)null));
            playerinfo.setUuid(JsonUtils.getStringOr("uuid", jsonobject, (String)null));
            playerinfo.setOperator(JsonUtils.getBooleanOr("operator", jsonobject, false));
            playerinfo.setAccepted(JsonUtils.getBooleanOr("accepted", jsonobject, false));
            playerinfo.setOnline(JsonUtils.getBooleanOr("online", jsonobject, false));
            list.add(playerinfo);
         } catch (Exception exception) {
         }
      }

      return list;
   }

   private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray p_87514_) {
      Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();

      for(JsonElement jsonelement : p_87514_) {
         try {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            JsonParser jsonparser = new JsonParser();
            JsonElement jsonelement1 = jsonparser.parse(jsonobject.get("options").getAsString());
            RealmsWorldOptions realmsworldoptions;
            if (jsonelement1 == null) {
               realmsworldoptions = RealmsWorldOptions.createDefaults();
            } else {
               realmsworldoptions = RealmsWorldOptions.parse(jsonelement1.getAsJsonObject());
            }

            int i = JsonUtils.getIntOr("slotId", jsonobject, -1);
            map.put(i, realmsworldoptions);
         } catch (Exception exception) {
         }
      }

      for(int j = 1; j <= 3; ++j) {
         if (!map.containsKey(j)) {
            map.put(j, RealmsWorldOptions.createEmptyDefaults());
         }
      }

      return map;
   }

   private static Map<Integer, RealmsWorldOptions> createEmptySlots() {
      Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();
      map.put(1, RealmsWorldOptions.createEmptyDefaults());
      map.put(2, RealmsWorldOptions.createEmptyDefaults());
      map.put(3, RealmsWorldOptions.createEmptyDefaults());
      return map;
   }

   public static RealmsServer parse(String p_87519_) {
      try {
         return parse((new JsonParser()).parse(p_87519_).getAsJsonObject());
      } catch (Exception exception) {
         LOGGER.error("Could not parse McoServer: {}", (Object)exception.getMessage());
         return new RealmsServer();
      }
   }

   private static RealmsServer.State getState(String p_87526_) {
      try {
         return RealmsServer.State.valueOf(p_87526_);
      } catch (Exception exception) {
         return RealmsServer.State.CLOSED;
      }
   }

   private static RealmsServer.WorldType getWorldType(String p_87530_) {
      try {
         return RealmsServer.WorldType.valueOf(p_87530_);
      } catch (Exception exception) {
         return RealmsServer.WorldType.NORMAL;
      }
   }

   public int hashCode() {
      return Objects.hash(this.id, this.name, this.motd, this.state, this.owner, this.expired);
   }

   public boolean equals(Object p_87528_) {
      if (p_87528_ == null) {
         return false;
      } else if (p_87528_ == this) {
         return true;
      } else if (p_87528_.getClass() != this.getClass()) {
         return false;
      } else {
         RealmsServer realmsserver = (RealmsServer)p_87528_;
         return (new EqualsBuilder()).append(this.id, realmsserver.id).append((Object)this.name, (Object)realmsserver.name).append((Object)this.motd, (Object)realmsserver.motd).append((Object)this.state, (Object)realmsserver.state).append((Object)this.owner, (Object)realmsserver.owner).append(this.expired, realmsserver.expired).append((Object)this.worldType, (Object)this.worldType).isEquals();
      }
   }

   public RealmsServer clone() {
      RealmsServer realmsserver = new RealmsServer();
      realmsserver.id = this.id;
      realmsserver.remoteSubscriptionId = this.remoteSubscriptionId;
      realmsserver.name = this.name;
      realmsserver.motd = this.motd;
      realmsserver.state = this.state;
      realmsserver.owner = this.owner;
      realmsserver.players = this.players;
      realmsserver.slots = this.cloneSlots(this.slots);
      realmsserver.expired = this.expired;
      realmsserver.expiredTrial = this.expiredTrial;
      realmsserver.daysLeft = this.daysLeft;
      realmsserver.serverPing = new RealmsServerPing();
      realmsserver.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
      realmsserver.serverPing.playerList = this.serverPing.playerList;
      realmsserver.worldType = this.worldType;
      realmsserver.ownerUUID = this.ownerUUID;
      realmsserver.minigameName = this.minigameName;
      realmsserver.activeSlot = this.activeSlot;
      realmsserver.minigameId = this.minigameId;
      realmsserver.minigameImage = this.minigameImage;
      return realmsserver;
   }

   public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> p_87511_) {
      Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();

      for(Entry<Integer, RealmsWorldOptions> entry : p_87511_.entrySet()) {
         map.put(entry.getKey(), entry.getValue().clone());
      }

      return map;
   }

   public String getWorldName(int p_87496_) {
      return this.name + " (" + this.slots.get(p_87496_).getSlotName(p_87496_) + ")";
   }

   public ServerData toServerData(String p_87523_) {
      return new ServerData(this.name, p_87523_, false);
   }

   @OnlyIn(Dist.CLIENT)
   public static class McoServerComparator implements Comparator<RealmsServer> {
      private final String refOwner;

      public McoServerComparator(String p_87534_) {
         this.refOwner = p_87534_;
      }

      public int compare(RealmsServer p_87536_, RealmsServer p_87537_) {
         return ComparisonChain.start().compareTrueFirst(p_87536_.state == RealmsServer.State.UNINITIALIZED, p_87537_.state == RealmsServer.State.UNINITIALIZED).compareTrueFirst(p_87536_.expiredTrial, p_87537_.expiredTrial).compareTrueFirst(p_87536_.owner.equals(this.refOwner), p_87537_.owner.equals(this.refOwner)).compareFalseFirst(p_87536_.expired, p_87537_.expired).compareTrueFirst(p_87536_.state == RealmsServer.State.OPEN, p_87537_.state == RealmsServer.State.OPEN).compare(p_87536_.id, p_87537_.id).result();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum State {
      CLOSED,
      OPEN,
      UNINITIALIZED;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum WorldType {
      NORMAL,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;
   }
}