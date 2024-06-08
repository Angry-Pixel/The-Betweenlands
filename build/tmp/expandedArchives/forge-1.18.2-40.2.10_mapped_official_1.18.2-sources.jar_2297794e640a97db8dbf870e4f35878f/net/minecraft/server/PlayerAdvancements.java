package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public class PlayerAdvancements {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int VISIBILITY_DEPTH = 2;
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).setPrettyPrinting().create();
   private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> TYPE_TOKEN = new TypeToken<Map<ResourceLocation, AdvancementProgress>>() {
   };
   private final DataFixer dataFixer;
   private final PlayerList playerList;
   private final File file;
   private final Map<Advancement, AdvancementProgress> advancements = Maps.newLinkedHashMap();
   private final Set<Advancement> visible = Sets.newLinkedHashSet();
   private final Set<Advancement> visibilityChanged = Sets.newLinkedHashSet();
   private final Set<Advancement> progressChanged = Sets.newLinkedHashSet();
   private ServerPlayer player;
   @Nullable
   private Advancement lastSelectedTab;
   private boolean isFirstPacket = true;

   public PlayerAdvancements(DataFixer p_135973_, PlayerList p_135974_, ServerAdvancementManager p_135975_, File p_135976_, ServerPlayer p_135977_) {
      this.dataFixer = p_135973_;
      this.playerList = p_135974_;
      this.file = p_135976_;
      this.player = p_135977_;
      this.load(p_135975_);
   }

   public void setPlayer(ServerPlayer p_135980_) {
      this.player = p_135980_;
   }

   public void stopListening() {
      for(CriterionTrigger<?> criteriontrigger : CriteriaTriggers.all()) {
         criteriontrigger.removePlayerListeners(this);
      }

   }

   public void reload(ServerAdvancementManager p_135982_) {
      this.stopListening();
      this.advancements.clear();
      this.visible.clear();
      this.visibilityChanged.clear();
      this.progressChanged.clear();
      this.isFirstPacket = true;
      this.lastSelectedTab = null;
      this.load(p_135982_);
   }

   private void registerListeners(ServerAdvancementManager p_135995_) {
      for(Advancement advancement : p_135995_.getAllAdvancements()) {
         this.registerListeners(advancement);
      }

   }

   private void ensureAllVisible() {
      List<Advancement> list = Lists.newArrayList();

      for(Entry<Advancement, AdvancementProgress> entry : this.advancements.entrySet()) {
         if (entry.getValue().isDone()) {
            list.add(entry.getKey());
            this.progressChanged.add(entry.getKey());
         }
      }

      for(Advancement advancement : list) {
         this.ensureVisibility(advancement);
      }

   }

   private void checkForAutomaticTriggers(ServerAdvancementManager p_136003_) {
      for(Advancement advancement : p_136003_.getAllAdvancements()) {
         if (advancement.getCriteria().isEmpty()) {
            this.award(advancement, "");
            advancement.getRewards().grant(this.player);
         }
      }

   }

   private void load(ServerAdvancementManager p_136007_) {
      if (this.file.isFile()) {
         try {
            JsonReader jsonreader = new JsonReader(new StringReader(Files.toString(this.file, StandardCharsets.UTF_8)));

            try {
               jsonreader.setLenient(false);
               Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(jsonreader));
               if (!dynamic.get("DataVersion").asNumber().result().isPresent()) {
                  dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
               }

               dynamic = this.dataFixer.update(DataFixTypes.ADVANCEMENTS.getType(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getCurrentVersion().getWorldVersion());
               dynamic = dynamic.remove("DataVersion");
               Map<ResourceLocation, AdvancementProgress> map = GSON.getAdapter(TYPE_TOKEN).fromJsonTree(dynamic.getValue());
               if (map == null) {
                  throw new JsonParseException("Found null for advancements");
               }

               Stream<Entry<ResourceLocation, AdvancementProgress>> stream = map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));

               for(Entry<ResourceLocation, AdvancementProgress> entry : stream.collect(Collectors.toList())) {
                  Advancement advancement = p_136007_.getAdvancement(entry.getKey());
                  if (advancement == null) {
                     LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), this.file);
                  } else {
                     this.startProgress(advancement, entry.getValue());
                  }
               }
            } catch (Throwable throwable1) {
               try {
                  jsonreader.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }

               throw throwable1;
            }

            jsonreader.close();
         } catch (JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't parse player advancements in {}", this.file, jsonparseexception);
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't access player advancements in {}", this.file, ioexception);
         }
      }

      this.checkForAutomaticTriggers(p_136007_);

      if (net.minecraftforge.common.ForgeConfig.SERVER.fixAdvancementLoading.get())
         net.minecraftforge.common.AdvancementLoadFix.loadVisibility(this, this.visible, this.visibilityChanged, this.advancements, this.progressChanged, this::shouldBeVisible);
      else
      this.ensureAllVisible();
      this.registerListeners(p_136007_);
   }

   public void save() {
      Map<ResourceLocation, AdvancementProgress> map = Maps.newHashMap();

      for(Entry<Advancement, AdvancementProgress> entry : this.advancements.entrySet()) {
         AdvancementProgress advancementprogress = entry.getValue();
         if (advancementprogress.hasProgress()) {
            map.put(entry.getKey().getId(), advancementprogress);
         }
      }

      if (this.file.getParentFile() != null) {
         this.file.getParentFile().mkdirs();
      }

      JsonElement jsonelement = GSON.toJsonTree(map);
      jsonelement.getAsJsonObject().addProperty("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());

      try {
         OutputStream outputstream = new FileOutputStream(this.file);

         try {
            Writer writer = new OutputStreamWriter(outputstream, Charsets.UTF_8.newEncoder());

            try {
               GSON.toJson(jsonelement, writer);
            } catch (Throwable throwable2) {
               try {
                  writer.close();
               } catch (Throwable throwable1) {
                  throwable2.addSuppressed(throwable1);
               }

               throw throwable2;
            }

            writer.close();
         } catch (Throwable throwable3) {
            try {
               outputstream.close();
            } catch (Throwable throwable) {
               throwable3.addSuppressed(throwable);
            }

            throw throwable3;
         }

         outputstream.close();
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save player advancements to {}", this.file, ioexception);
      }

   }

   public boolean award(Advancement p_135989_, String p_135990_) {
      // Forge: don't grant advancements for fake players
      if (this.player instanceof net.minecraftforge.common.util.FakePlayer) return false;
      boolean flag = false;
      AdvancementProgress advancementprogress = this.getOrStartProgress(p_135989_);
      boolean flag1 = advancementprogress.isDone();
      if (advancementprogress.grantProgress(p_135990_)) {
         this.unregisterListeners(p_135989_);
         this.progressChanged.add(p_135989_);
         flag = true;
         if (!flag1 && advancementprogress.isDone()) {
            p_135989_.getRewards().grant(this.player);
            if (p_135989_.getDisplay() != null && p_135989_.getDisplay().shouldAnnounceChat() && this.player.level.getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
               this.playerList.broadcastMessage(new TranslatableComponent("chat.type.advancement." + p_135989_.getDisplay().getFrame().getName(), this.player.getDisplayName(), p_135989_.getChatComponent()), ChatType.SYSTEM, Util.NIL_UUID);
            }
            net.minecraftforge.common.ForgeHooks.onAdvancement(this.player, p_135989_);
         }
      }

      if (advancementprogress.isDone()) {
         this.ensureVisibility(p_135989_);
      }

      return flag;
   }

   public boolean revoke(Advancement p_135999_, String p_136000_) {
      boolean flag = false;
      AdvancementProgress advancementprogress = this.getOrStartProgress(p_135999_);
      if (advancementprogress.revokeProgress(p_136000_)) {
         this.registerListeners(p_135999_);
         this.progressChanged.add(p_135999_);
         flag = true;
      }

      if (!advancementprogress.hasProgress()) {
         this.ensureVisibility(p_135999_);
      }

      return flag;
   }

   private void registerListeners(Advancement p_136005_) {
      AdvancementProgress advancementprogress = this.getOrStartProgress(p_136005_);
      if (!advancementprogress.isDone()) {
         for(Entry<String, Criterion> entry : p_136005_.getCriteria().entrySet()) {
            CriterionProgress criterionprogress = advancementprogress.getCriterion(entry.getKey());
            if (criterionprogress != null && !criterionprogress.isDone()) {
               CriterionTriggerInstance criteriontriggerinstance = entry.getValue().getTrigger();
               if (criteriontriggerinstance != null) {
                  CriterionTrigger<CriterionTriggerInstance> criteriontrigger = CriteriaTriggers.getCriterion(criteriontriggerinstance.getCriterion());
                  if (criteriontrigger != null) {
                     criteriontrigger.addPlayerListener(this, new CriterionTrigger.Listener<>(criteriontriggerinstance, p_136005_, entry.getKey()));
                  }
               }
            }
         }

      }
   }

   private void unregisterListeners(Advancement p_136009_) {
      AdvancementProgress advancementprogress = this.getOrStartProgress(p_136009_);

      for(Entry<String, Criterion> entry : p_136009_.getCriteria().entrySet()) {
         CriterionProgress criterionprogress = advancementprogress.getCriterion(entry.getKey());
         if (criterionprogress != null && (criterionprogress.isDone() || advancementprogress.isDone())) {
            CriterionTriggerInstance criteriontriggerinstance = entry.getValue().getTrigger();
            if (criteriontriggerinstance != null) {
               CriterionTrigger<CriterionTriggerInstance> criteriontrigger = CriteriaTriggers.getCriterion(criteriontriggerinstance.getCriterion());
               if (criteriontrigger != null) {
                  criteriontrigger.removePlayerListener(this, new CriterionTrigger.Listener<>(criteriontriggerinstance, p_136009_, entry.getKey()));
               }
            }
         }
      }

   }

   public void flushDirty(ServerPlayer p_135993_) {
      if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()) {
         Map<ResourceLocation, AdvancementProgress> map = Maps.newHashMap();
         Set<Advancement> set = Sets.newLinkedHashSet();
         Set<ResourceLocation> set1 = Sets.newLinkedHashSet();

         for(Advancement advancement : this.progressChanged) {
            if (this.visible.contains(advancement)) {
               map.put(advancement.getId(), this.advancements.get(advancement));
            }
         }

         for(Advancement advancement1 : this.visibilityChanged) {
            if (this.visible.contains(advancement1)) {
               set.add(advancement1);
            } else {
               set1.add(advancement1.getId());
            }
         }

         if (this.isFirstPacket || !map.isEmpty() || !set.isEmpty() || !set1.isEmpty()) {
            p_135993_.connection.send(new ClientboundUpdateAdvancementsPacket(this.isFirstPacket, set, set1, map));
            this.visibilityChanged.clear();
            this.progressChanged.clear();
         }
      }

      this.isFirstPacket = false;
   }

   public void setSelectedTab(@Nullable Advancement p_135984_) {
      Advancement advancement = this.lastSelectedTab;
      if (p_135984_ != null && p_135984_.getParent() == null && p_135984_.getDisplay() != null) {
         this.lastSelectedTab = p_135984_;
      } else {
         this.lastSelectedTab = null;
      }

      if (advancement != this.lastSelectedTab) {
         this.player.connection.send(new ClientboundSelectAdvancementsTabPacket(this.lastSelectedTab == null ? null : this.lastSelectedTab.getId()));
      }

   }

   public AdvancementProgress getOrStartProgress(Advancement p_135997_) {
      AdvancementProgress advancementprogress = this.advancements.get(p_135997_);
      if (advancementprogress == null) {
         advancementprogress = new AdvancementProgress();
         this.startProgress(p_135997_, advancementprogress);
      }

      return advancementprogress;
   }

   private void startProgress(Advancement p_135986_, AdvancementProgress p_135987_) {
      p_135987_.update(p_135986_.getCriteria(), p_135986_.getRequirements());
      this.advancements.put(p_135986_, p_135987_);
   }

   private void ensureVisibility(Advancement p_136011_) {
      boolean flag = this.shouldBeVisible(p_136011_);
      boolean flag1 = this.visible.contains(p_136011_);
      if (flag && !flag1) {
         this.visible.add(p_136011_);
         this.visibilityChanged.add(p_136011_);
         if (this.advancements.containsKey(p_136011_)) {
            this.progressChanged.add(p_136011_);
         }
      } else if (!flag && flag1) {
         this.visible.remove(p_136011_);
         this.visibilityChanged.add(p_136011_);
      }

      if (flag != flag1 && p_136011_.getParent() != null) {
         this.ensureVisibility(p_136011_.getParent());
      }

      for(Advancement advancement : p_136011_.getChildren()) {
         this.ensureVisibility(advancement);
      }

   }

   private boolean shouldBeVisible(Advancement p_136013_) {
      for(int i = 0; p_136013_ != null && i <= 2; ++i) {
         if (i == 0 && this.hasCompletedChildrenOrSelf(p_136013_)) {
            return true;
         }

         if (p_136013_.getDisplay() == null) {
            return false;
         }

         AdvancementProgress advancementprogress = this.getOrStartProgress(p_136013_);
         if (advancementprogress.isDone()) {
            return true;
         }

         if (p_136013_.getDisplay().isHidden()) {
            return false;
         }

         p_136013_ = p_136013_.getParent();
      }

      return false;
   }

   private boolean hasCompletedChildrenOrSelf(Advancement p_136015_) {
      AdvancementProgress advancementprogress = this.getOrStartProgress(p_136015_);
      if (advancementprogress.isDone()) {
         return true;
      } else {
         for(Advancement advancement : p_136015_.getChildren()) {
            if (this.hasCompletedChildrenOrSelf(advancement)) {
               return true;
            }
         }

         return false;
      }
   }
}
