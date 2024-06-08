package com.mojang.realmsclient.gui;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.gui.task.RepeatableTask;
import com.mojang.realmsclient.util.RealmsPersistence;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsDataFetcher {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Minecraft minecraft;
   private final RealmsClient realmsClient;
   private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
   private volatile boolean stopped = true;
   private final RepeatableTask serverListUpdateTask = RepeatableTask.withImmediateRestart(this::updateServersList, Duration.ofSeconds(60L), this::isActive);
   private final RepeatableTask liveStatsTask = RepeatableTask.withImmediateRestart(this::updateLiveStats, Duration.ofSeconds(10L), this::isActive);
   private final RepeatableTask pendingInviteUpdateTask = RepeatableTask.withRestartDelayAccountingForInterval(this::updatePendingInvites, Duration.ofSeconds(10L), this::isActive);
   private final RepeatableTask trialAvailabilityTask = RepeatableTask.withRestartDelayAccountingForInterval(this::updateTrialAvailable, Duration.ofSeconds(60L), this::isActive);
   private final RepeatableTask unreadNewsTask = RepeatableTask.withRestartDelayAccountingForInterval(this::updateUnreadNews, Duration.ofMinutes(5L), this::isActive);
   private final RealmsPersistence newsLocalStorage;
   private final Set<RealmsServer> removedServers = Sets.newHashSet();
   private List<RealmsServer> servers = Lists.newArrayList();
   private RealmsServerPlayerLists livestats;
   private int pendingInvitesCount;
   private boolean trialAvailable;
   private boolean hasUnreadNews;
   private String newsLink;
   private ScheduledFuture<?> serverListScheduledFuture;
   private ScheduledFuture<?> pendingInviteScheduledFuture;
   private ScheduledFuture<?> trialAvailableScheduledFuture;
   private ScheduledFuture<?> liveStatsScheduledFuture;
   private ScheduledFuture<?> unreadNewsScheduledFuture;
   private final Map<RealmsDataFetcher.Task, Boolean> fetchStatus = new ConcurrentHashMap<>(RealmsDataFetcher.Task.values().length);

   public RealmsDataFetcher(Minecraft p_167333_, RealmsClient p_167334_) {
      this.minecraft = p_167333_;
      this.realmsClient = p_167334_;
      this.newsLocalStorage = new RealmsPersistence();
   }

   @VisibleForTesting
   protected RealmsDataFetcher(Minecraft p_167336_, RealmsClient p_167337_, RealmsPersistence p_167338_) {
      this.minecraft = p_167336_;
      this.realmsClient = p_167337_;
      this.newsLocalStorage = p_167338_;
   }

   public boolean isStopped() {
      return this.stopped;
   }

   public synchronized void init() {
      if (this.stopped) {
         this.stopped = false;
         this.cancelTasks();
         this.scheduleTasks();
      }

   }

   public synchronized void initWithSpecificTaskList() {
      if (this.stopped) {
         this.stopped = false;
         this.cancelTasks();
         this.fetchStatus.put(RealmsDataFetcher.Task.PENDING_INVITE, false);
         this.pendingInviteScheduledFuture = this.pendingInviteUpdateTask.schedule(this.scheduler);
         this.fetchStatus.put(RealmsDataFetcher.Task.TRIAL_AVAILABLE, false);
         this.trialAvailableScheduledFuture = this.trialAvailabilityTask.schedule(this.scheduler);
         this.fetchStatus.put(RealmsDataFetcher.Task.UNREAD_NEWS, false);
         this.unreadNewsScheduledFuture = this.unreadNewsTask.schedule(this.scheduler);
      }

   }

   public boolean isFetchedSinceLastTry(RealmsDataFetcher.Task p_87821_) {
      Boolean obool = this.fetchStatus.get(p_87821_);
      return obool != null && obool;
   }

   public void markClean() {
      this.fetchStatus.replaceAll((p_167340_, p_167341_) -> {
         return false;
      });
   }

   public synchronized void forceUpdate() {
      this.stop();
      this.init();
   }

   public synchronized List<RealmsServer> getServers() {
      return ImmutableList.copyOf(this.servers);
   }

   public synchronized int getPendingInvitesCount() {
      return this.pendingInvitesCount;
   }

   public synchronized boolean isTrialAvailable() {
      return this.trialAvailable;
   }

   public synchronized RealmsServerPlayerLists getLivestats() {
      return this.livestats;
   }

   public synchronized boolean hasUnreadNews() {
      return this.hasUnreadNews;
   }

   public synchronized String newsLink() {
      return this.newsLink;
   }

   public synchronized void stop() {
      this.stopped = true;
      this.cancelTasks();
   }

   private void scheduleTasks() {
      for(RealmsDataFetcher.Task realmsdatafetcher$task : RealmsDataFetcher.Task.values()) {
         this.fetchStatus.put(realmsdatafetcher$task, false);
      }

      this.serverListScheduledFuture = this.serverListUpdateTask.schedule(this.scheduler);
      this.pendingInviteScheduledFuture = this.pendingInviteUpdateTask.schedule(this.scheduler);
      this.trialAvailableScheduledFuture = this.trialAvailabilityTask.schedule(this.scheduler);
      this.liveStatsScheduledFuture = this.liveStatsTask.schedule(this.scheduler);
      this.unreadNewsScheduledFuture = this.unreadNewsTask.schedule(this.scheduler);
   }

   private void cancelTasks() {
      Stream.of(this.serverListScheduledFuture, this.pendingInviteScheduledFuture, this.trialAvailableScheduledFuture, this.liveStatsScheduledFuture, this.unreadNewsScheduledFuture).filter(Objects::nonNull).forEach((p_167343_) -> {
         try {
            p_167343_.cancel(false);
         } catch (Exception exception) {
            LOGGER.error("Failed to cancel Realms task", (Throwable)exception);
         }

      });
   }

   private synchronized void setServers(List<RealmsServer> p_87840_) {
      int i = 0;

      for(RealmsServer realmsserver : this.removedServers) {
         if (p_87840_.remove(realmsserver)) {
            ++i;
         }
      }

      if (i == 0) {
         this.removedServers.clear();
      }

      this.servers = p_87840_;
   }

   public synchronized List<RealmsServer> removeItem(RealmsServer p_210673_) {
      this.servers.remove(p_210673_);
      this.removedServers.add(p_210673_);
      return ImmutableList.copyOf(this.servers);
   }

   private boolean isActive() {
      return !this.stopped;
   }

   private void updateServersList() {
      try {
         List<RealmsServer> list = this.realmsClient.listWorlds().servers;
         if (list != null) {
            list.sort(new RealmsServer.McoServerComparator(this.minecraft.getUser().getName()));
            this.setServers(list);
            this.fetchStatus.put(RealmsDataFetcher.Task.SERVER_LIST, true);
         } else {
            LOGGER.warn("Realms server list was null");
         }
      } catch (Exception exception) {
         this.fetchStatus.put(RealmsDataFetcher.Task.SERVER_LIST, true);
         LOGGER.error("Couldn't get server list", (Throwable)exception);
      }

   }

   private void updatePendingInvites() {
      try {
         this.pendingInvitesCount = this.realmsClient.pendingInvitesCount();
         this.fetchStatus.put(RealmsDataFetcher.Task.PENDING_INVITE, true);
      } catch (Exception exception) {
         LOGGER.error("Couldn't get pending invite count", (Throwable)exception);
      }

   }

   private void updateTrialAvailable() {
      try {
         this.trialAvailable = this.realmsClient.trialAvailable();
         this.fetchStatus.put(RealmsDataFetcher.Task.TRIAL_AVAILABLE, true);
      } catch (Exception exception) {
         LOGGER.error("Couldn't get trial availability", (Throwable)exception);
      }

   }

   private void updateLiveStats() {
      try {
         this.livestats = this.realmsClient.getLiveStats();
         this.fetchStatus.put(RealmsDataFetcher.Task.LIVE_STATS, true);
      } catch (Exception exception) {
         LOGGER.error("Couldn't get live stats", (Throwable)exception);
      }

   }

   private void updateUnreadNews() {
      try {
         RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = this.fetchAndUpdateNewsStorage();
         this.hasUnreadNews = realmspersistence$realmspersistencedata.hasUnreadNews;
         this.newsLink = realmspersistence$realmspersistencedata.newsLink;
         this.fetchStatus.put(RealmsDataFetcher.Task.UNREAD_NEWS, true);
      } catch (Exception exception) {
         LOGGER.error("Couldn't update unread news", (Throwable)exception);
      }

   }

   private RealmsPersistence.RealmsPersistenceData fetchAndUpdateNewsStorage() {
      RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata;
      try {
         RealmsNews realmsnews = this.realmsClient.getNews();
         realmspersistence$realmspersistencedata = new RealmsPersistence.RealmsPersistenceData();
         realmspersistence$realmspersistencedata.newsLink = realmsnews.newsLink;
      } catch (Exception exception) {
         LOGGER.warn("Failed fetching news from Realms, falling back to local cache", (Throwable)exception);
         return this.newsLocalStorage.read();
      }

      RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata1 = this.newsLocalStorage.read();
      boolean flag = realmspersistence$realmspersistencedata.newsLink == null || realmspersistence$realmspersistencedata.newsLink.equals(realmspersistence$realmspersistencedata1.newsLink);
      if (flag) {
         return realmspersistence$realmspersistencedata1;
      } else {
         realmspersistence$realmspersistencedata.hasUnreadNews = true;
         this.newsLocalStorage.save(realmspersistence$realmspersistencedata);
         return realmspersistence$realmspersistencedata;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Task {
      SERVER_LIST,
      PENDING_INVITE,
      TRIAL_AVAILABLE,
      LIVE_STATS,
      UNREAD_NEWS;
   }
}