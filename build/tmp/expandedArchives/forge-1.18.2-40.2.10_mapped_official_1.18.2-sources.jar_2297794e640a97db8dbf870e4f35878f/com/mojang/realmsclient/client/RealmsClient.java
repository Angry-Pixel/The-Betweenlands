package com.mojang.realmsclient.client;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.ServerActivityList;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsClient {
   public static RealmsClient.Environment currentEnvironment = RealmsClient.Environment.PRODUCTION;
   private static boolean initialized;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String sessionId;
   private final String username;
   private final Minecraft minecraft;
   private static final String WORLDS_RESOURCE_PATH = "worlds";
   private static final String INVITES_RESOURCE_PATH = "invites";
   private static final String MCO_RESOURCE_PATH = "mco";
   private static final String SUBSCRIPTION_RESOURCE = "subscriptions";
   private static final String ACTIVITIES_RESOURCE = "activities";
   private static final String OPS_RESOURCE = "ops";
   private static final String REGIONS_RESOURCE = "regions/ping/stat";
   private static final String TRIALS_RESOURCE = "trial";
   private static final String PATH_INITIALIZE = "/$WORLD_ID/initialize";
   private static final String PATH_GET_ACTIVTIES = "/$WORLD_ID";
   private static final String PATH_GET_LIVESTATS = "/liveplayerlist";
   private static final String PATH_GET_SUBSCRIPTION = "/$WORLD_ID";
   private static final String PATH_OP = "/$WORLD_ID/$PROFILE_UUID";
   private static final String PATH_PUT_INTO_MINIGAMES_MODE = "/minigames/$MINIGAME_ID/$WORLD_ID";
   private static final String PATH_AVAILABLE = "/available";
   private static final String PATH_TEMPLATES = "/templates/$WORLD_TYPE";
   private static final String PATH_WORLD_JOIN = "/v1/$ID/join/pc";
   private static final String PATH_WORLD_GET = "/$ID";
   private static final String PATH_WORLD_INVITES = "/$WORLD_ID";
   private static final String PATH_WORLD_UNINVITE = "/$WORLD_ID/invite/$UUID";
   private static final String PATH_PENDING_INVITES_COUNT = "/count/pending";
   private static final String PATH_PENDING_INVITES = "/pending";
   private static final String PATH_ACCEPT_INVITE = "/accept/$INVITATION_ID";
   private static final String PATH_REJECT_INVITE = "/reject/$INVITATION_ID";
   private static final String PATH_UNINVITE_MYSELF = "/$WORLD_ID";
   private static final String PATH_WORLD_UPDATE = "/$WORLD_ID";
   private static final String PATH_SLOT = "/$WORLD_ID/slot/$SLOT_ID";
   private static final String PATH_WORLD_OPEN = "/$WORLD_ID/open";
   private static final String PATH_WORLD_CLOSE = "/$WORLD_ID/close";
   private static final String PATH_WORLD_RESET = "/$WORLD_ID/reset";
   private static final String PATH_DELETE_WORLD = "/$WORLD_ID";
   private static final String PATH_WORLD_BACKUPS = "/$WORLD_ID/backups";
   private static final String PATH_WORLD_DOWNLOAD = "/$WORLD_ID/slot/$SLOT_ID/download";
   private static final String PATH_WORLD_UPLOAD = "/$WORLD_ID/backups/upload";
   private static final String PATH_CLIENT_COMPATIBLE = "/client/compatible";
   private static final String PATH_TOS_AGREED = "/tos/agreed";
   private static final String PATH_NEWS = "/v1/news";
   private static final String PATH_STAGE_AVAILABLE = "/stageAvailable";
   private static final GuardedSerializer GSON = new GuardedSerializer();

   public static RealmsClient create() {
      Minecraft minecraft = Minecraft.getInstance();
      String s = minecraft.getUser().getName();
      String s1 = minecraft.getUser().getSessionId();
      if (!initialized) {
         initialized = true;
         String s2 = System.getenv("realms.environment");
         if (s2 == null) {
            s2 = System.getProperty("realms.environment");
         }

         if (s2 != null) {
            if ("LOCAL".equals(s2)) {
               switchToLocal();
            } else if ("STAGE".equals(s2)) {
               switchToStage();
            }
         }
      }

      return new RealmsClient(s1, s, minecraft);
   }

   public static void switchToStage() {
      currentEnvironment = RealmsClient.Environment.STAGE;
   }

   public static void switchToProd() {
      currentEnvironment = RealmsClient.Environment.PRODUCTION;
   }

   public static void switchToLocal() {
      currentEnvironment = RealmsClient.Environment.LOCAL;
   }

   public RealmsClient(String p_87166_, String p_87167_, Minecraft p_87168_) {
      this.sessionId = p_87166_;
      this.username = p_87167_;
      this.minecraft = p_87168_;
      RealmsClientConfig.setProxy(p_87168_.getProxy());
   }

   public RealmsServerList listWorlds() throws RealmsServiceException {
      String s = this.url("worlds");
      String s1 = this.execute(Request.get(s));
      return RealmsServerList.parse(s1);
   }

   public RealmsServer getOwnWorld(long p_87175_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(p_87175_)));
      String s1 = this.execute(Request.get(s));
      return RealmsServer.parse(s1);
   }

   public ServerActivityList getActivity(long p_167279_) throws RealmsServiceException {
      String s = this.url("activities" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_167279_)));
      String s1 = this.execute(Request.get(s));
      return ServerActivityList.parse(s1);
   }

   public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
      String s = this.url("activities/liveplayerlist");
      String s1 = this.execute(Request.get(s));
      return RealmsServerPlayerLists.parse(s1);
   }

   public RealmsServerAddress join(long p_87208_) throws RealmsServiceException {
      String s = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + p_87208_));
      String s1 = this.execute(Request.get(s, 5000, 30000));
      return RealmsServerAddress.parse(s1);
   }

   public void initializeWorld(long p_87192_, String p_87193_, String p_87194_) throws RealmsServiceException {
      RealmsDescriptionDto realmsdescriptiondto = new RealmsDescriptionDto(p_87193_, p_87194_);
      String s = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(p_87192_)));
      String s1 = GSON.toJson(realmsdescriptiondto);
      this.execute(Request.post(s, s1, 5000, 10000));
   }

   public Boolean mcoEnabled() throws RealmsServiceException {
      String s = this.url("mco/available");
      String s1 = this.execute(Request.get(s));
      return Boolean.valueOf(s1);
   }

   public Boolean stageAvailable() throws RealmsServiceException {
      String s = this.url("mco/stageAvailable");
      String s1 = this.execute(Request.get(s));
      return Boolean.valueOf(s1);
   }

   public RealmsClient.CompatibleVersionResponse clientCompatible() throws RealmsServiceException {
      String s = this.url("mco/client/compatible");
      String s1 = this.execute(Request.get(s));

      try {
         return RealmsClient.CompatibleVersionResponse.valueOf(s1);
      } catch (IllegalArgumentException illegalargumentexception) {
         throw new RealmsServiceException(500, "Could not check compatible version, got response: " + s1);
      }
   }

   public void uninvite(long p_87184_, String p_87185_) throws RealmsServiceException {
      String s = this.url("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(p_87184_)).replace("$UUID", p_87185_));
      this.execute(Request.delete(s));
   }

   public void uninviteMyselfFrom(long p_87223_) throws RealmsServiceException {
      String s = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_87223_)));
      this.execute(Request.delete(s));
   }

   public RealmsServer invite(long p_87213_, String p_87214_) throws RealmsServiceException {
      PlayerInfo playerinfo = new PlayerInfo();
      playerinfo.setName(p_87214_);
      String s = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_87213_)));
      String s1 = this.execute(Request.post(s, GSON.toJson(playerinfo)));
      return RealmsServer.parse(s1);
   }

   public BackupList backupsFor(long p_87231_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(p_87231_)));
      String s1 = this.execute(Request.get(s));
      return BackupList.parse(s1);
   }

   public void update(long p_87216_, String p_87217_, String p_87218_) throws RealmsServiceException {
      RealmsDescriptionDto realmsdescriptiondto = new RealmsDescriptionDto(p_87217_, p_87218_);
      String s = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_87216_)));
      this.execute(Request.post(s, GSON.toJson(realmsdescriptiondto)));
   }

   public void updateSlot(long p_87180_, int p_87181_, RealmsWorldOptions p_87182_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(p_87180_)).replace("$SLOT_ID", String.valueOf(p_87181_)));
      String s1 = p_87182_.toJson();
      this.execute(Request.post(s, s1));
   }

   public boolean switchSlot(long p_87177_, int p_87178_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(p_87177_)).replace("$SLOT_ID", String.valueOf(p_87178_)));
      String s1 = this.execute(Request.put(s, ""));
      return Boolean.valueOf(s1);
   }

   public void restoreWorld(long p_87225_, String p_87226_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(p_87225_)), "backupId=" + p_87226_);
      this.execute(Request.put(s, "", 40000, 600000));
   }

   public WorldTemplatePaginatedList fetchWorldTemplates(int p_87171_, int p_87172_, RealmsServer.WorldType p_87173_) throws RealmsServiceException {
      String s = this.url("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", p_87173_.toString()), String.format("page=%d&pageSize=%d", p_87171_, p_87172_));
      String s1 = this.execute(Request.get(s));
      return WorldTemplatePaginatedList.parse(s1);
   }

   public Boolean putIntoMinigameMode(long p_87233_, String p_87234_) throws RealmsServiceException {
      String s = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", p_87234_).replace("$WORLD_ID", String.valueOf(p_87233_));
      String s1 = this.url("worlds" + s);
      return Boolean.valueOf(this.execute(Request.put(s1, "")));
   }

   public Ops op(long p_87239_, String p_87240_) throws RealmsServiceException {
      String s = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(p_87239_)).replace("$PROFILE_UUID", p_87240_);
      String s1 = this.url("ops" + s);
      return Ops.parse(this.execute(Request.post(s1, "")));
   }

   public Ops deop(long p_87245_, String p_87246_) throws RealmsServiceException {
      String s = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(p_87245_)).replace("$PROFILE_UUID", p_87246_);
      String s1 = this.url("ops" + s);
      return Ops.parse(this.execute(Request.delete(s1)));
   }

   public Boolean open(long p_87237_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(p_87237_)));
      String s1 = this.execute(Request.put(s, ""));
      return Boolean.valueOf(s1);
   }

   public Boolean close(long p_87243_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(p_87243_)));
      String s1 = this.execute(Request.put(s, ""));
      return Boolean.valueOf(s1);
   }

   public Boolean resetWorldWithSeed(long p_167276_, WorldGenerationInfo p_167277_) throws RealmsServiceException {
      RealmsWorldResetDto realmsworldresetdto = new RealmsWorldResetDto(p_167277_.getSeed(), -1L, p_167277_.getLevelType().getDtoIndex(), p_167277_.shouldGenerateStructures());
      String s = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(p_167276_)));
      String s1 = this.execute(Request.post(s, GSON.toJson(realmsworldresetdto), 30000, 80000));
      return Boolean.valueOf(s1);
   }

   public Boolean resetWorldWithTemplate(long p_87251_, String p_87252_) throws RealmsServiceException {
      RealmsWorldResetDto realmsworldresetdto = new RealmsWorldResetDto((String)null, Long.valueOf(p_87252_), -1, false);
      String s = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(p_87251_)));
      String s1 = this.execute(Request.post(s, GSON.toJson(realmsworldresetdto), 30000, 80000));
      return Boolean.valueOf(s1);
   }

   public Subscription subscriptionFor(long p_87249_) throws RealmsServiceException {
      String s = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_87249_)));
      String s1 = this.execute(Request.get(s));
      return Subscription.parse(s1);
   }

   public int pendingInvitesCount() throws RealmsServiceException {
      return this.pendingInvites().pendingInvites.size();
   }

   public PendingInvitesList pendingInvites() throws RealmsServiceException {
      String s = this.url("invites/pending");
      String s1 = this.execute(Request.get(s));
      PendingInvitesList pendinginviteslist = PendingInvitesList.parse(s1);
      pendinginviteslist.pendingInvites.removeIf(this::isBlocked);
      return pendinginviteslist;
   }

   private boolean isBlocked(PendingInvite p_87198_) {
      try {
         UUID uuid = UUID.fromString(p_87198_.worldOwnerUuid);
         return this.minecraft.getPlayerSocialManager().isBlocked(uuid);
      } catch (IllegalArgumentException illegalargumentexception) {
         return false;
      }
   }

   public void acceptInvitation(String p_87202_) throws RealmsServiceException {
      String s = this.url("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", p_87202_));
      this.execute(Request.put(s, ""));
   }

   public WorldDownload requestDownloadInfo(long p_87210_, int p_87211_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(p_87210_)).replace("$SLOT_ID", String.valueOf(p_87211_)));
      String s1 = this.execute(Request.get(s));
      return WorldDownload.parse(s1);
   }

   @Nullable
   public UploadInfo requestUploadInfo(long p_87257_, @Nullable String p_87258_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(p_87257_)));
      return UploadInfo.parse(this.execute(Request.put(s, UploadInfo.createRequest(p_87258_))));
   }

   public void rejectInvitation(String p_87220_) throws RealmsServiceException {
      String s = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", p_87220_));
      this.execute(Request.put(s, ""));
   }

   public void agreeToTos() throws RealmsServiceException {
      String s = this.url("mco/tos/agreed");
      this.execute(Request.post(s, ""));
   }

   public RealmsNews getNews() throws RealmsServiceException {
      String s = this.url("mco/v1/news");
      String s1 = this.execute(Request.get(s, 5000, 10000));
      return RealmsNews.parse(s1);
   }

   public void sendPingResults(PingResult p_87200_) throws RealmsServiceException {
      String s = this.url("regions/ping/stat");
      this.execute(Request.post(s, GSON.toJson(p_87200_)));
   }

   public Boolean trialAvailable() throws RealmsServiceException {
      String s = this.url("trial");
      String s1 = this.execute(Request.get(s));
      return Boolean.valueOf(s1);
   }

   public void deleteWorld(long p_87255_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_87255_)));
      this.execute(Request.delete(s));
   }

   private String url(String p_87228_) {
      return this.url(p_87228_, (String)null);
   }

   private String url(String p_87204_, @Nullable String p_87205_) {
      try {
         return (new URI(currentEnvironment.protocol, currentEnvironment.baseUrl, "/" + p_87204_, p_87205_, (String)null)).toASCIIString();
      } catch (URISyntaxException urisyntaxexception) {
         throw new IllegalArgumentException(p_87204_, urisyntaxexception);
      }
   }

   private String execute(Request<?> p_87196_) throws RealmsServiceException {
      p_87196_.cookie("sid", this.sessionId);
      p_87196_.cookie("user", this.username);
      p_87196_.cookie("version", SharedConstants.getCurrentVersion().getName());

      try {
         int i = p_87196_.responseCode();
         if (i != 503 && i != 277) {
            String s1 = p_87196_.text();
            if (i >= 200 && i < 300) {
               return s1;
            } else if (i == 401) {
               String s2 = p_87196_.getHeader("WWW-Authenticate");
               LOGGER.info("Could not authorize you against Realms server: {}", (Object)s2);
               throw new RealmsServiceException(i, s2);
            } else {
               RealmsError realmserror = RealmsError.parse(s1);
               if (realmserror != null) {
                  LOGGER.error("Realms http code: {} -  error code: {} -  message: {} - raw body: {}", i, realmserror.getErrorCode(), realmserror.getErrorMessage(), s1);
                  throw new RealmsServiceException(i, s1, realmserror);
               } else {
                  LOGGER.error("Realms http code: {} - raw body (message failed to parse): {}", i, s1);
                  String s = getHttpCodeDescription(i);
                  throw new RealmsServiceException(i, s);
               }
            }
         } else {
            int j = p_87196_.getRetryAfterHeader();
            throw new RetryCallException(j, i);
         }
      } catch (RealmsHttpException realmshttpexception) {
         throw new RealmsServiceException(500, "Could not connect to Realms: " + realmshttpexception.getMessage());
      }
   }

   private static String getHttpCodeDescription(int p_200937_) {
      String s;
      switch(p_200937_) {
      case 429:
         s = I18n.get("mco.errorMessage.serviceBusy");
         break;
      default:
         s = "Unknown error";
      }

      return s;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum CompatibleVersionResponse {
      COMPATIBLE,
      OUTDATED,
      OTHER;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Environment {
      PRODUCTION("pc.realms.minecraft.net", "https"),
      STAGE("pc-stage.realms.minecraft.net", "https"),
      LOCAL("localhost:8080", "http");

      public String baseUrl;
      public String protocol;

      private Environment(String p_87286_, String p_87287_) {
         this.baseUrl = p_87286_;
         this.protocol = p_87287_;
      }
   }
}