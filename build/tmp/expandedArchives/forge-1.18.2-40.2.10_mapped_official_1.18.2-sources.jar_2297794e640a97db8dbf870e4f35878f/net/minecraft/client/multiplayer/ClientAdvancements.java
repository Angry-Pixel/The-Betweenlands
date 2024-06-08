package net.minecraft.client.multiplayer;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientAdvancements {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Minecraft minecraft;
   private final AdvancementList advancements = new AdvancementList();
   private final Map<Advancement, AdvancementProgress> progress = Maps.newHashMap();
   @Nullable
   private ClientAdvancements.Listener listener;
   @Nullable
   private Advancement selectedTab;

   public ClientAdvancements(Minecraft p_104395_) {
      this.minecraft = p_104395_;
   }

   public void update(ClientboundUpdateAdvancementsPacket p_104400_) {
      if (p_104400_.shouldReset()) {
         this.advancements.clear();
         this.progress.clear();
      }

      this.advancements.remove(p_104400_.getRemoved());
      this.advancements.add(p_104400_.getAdded());

      for(Entry<ResourceLocation, AdvancementProgress> entry : p_104400_.getProgress().entrySet()) {
         Advancement advancement = this.advancements.get(entry.getKey());
         if (advancement != null) {
            AdvancementProgress advancementprogress = entry.getValue();
            advancementprogress.update(advancement.getCriteria(), advancement.getRequirements());
            this.progress.put(advancement, advancementprogress);
            if (this.listener != null) {
               this.listener.onUpdateAdvancementProgress(advancement, advancementprogress);
            }

            if (!p_104400_.shouldReset() && advancementprogress.isDone() && advancement.getDisplay() != null && advancement.getDisplay().shouldShowToast()) {
               this.minecraft.getToasts().addToast(new AdvancementToast(advancement));
            }
         } else {
            LOGGER.warn("Server informed client about progress for unknown advancement {}", entry.getKey());
         }
      }

   }

   public AdvancementList getAdvancements() {
      return this.advancements;
   }

   public void setSelectedTab(@Nullable Advancement p_104402_, boolean p_104403_) {
      ClientPacketListener clientpacketlistener = this.minecraft.getConnection();
      if (clientpacketlistener != null && p_104402_ != null && p_104403_) {
         clientpacketlistener.send(ServerboundSeenAdvancementsPacket.openedTab(p_104402_));
      }

      if (this.selectedTab != p_104402_) {
         this.selectedTab = p_104402_;
         if (this.listener != null) {
            this.listener.onSelectedTabChanged(p_104402_);
         }
      }

   }

   public void setListener(@Nullable ClientAdvancements.Listener p_104398_) {
      this.listener = p_104398_;
      this.advancements.setListener(p_104398_);
      if (p_104398_ != null) {
         for(Entry<Advancement, AdvancementProgress> entry : this.progress.entrySet()) {
            p_104398_.onUpdateAdvancementProgress(entry.getKey(), entry.getValue());
         }

         p_104398_.onSelectedTabChanged(this.selectedTab);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public interface Listener extends AdvancementList.Listener {
      void onUpdateAdvancementProgress(Advancement p_104404_, AdvancementProgress p_104405_);

      void onSelectedTabChanged(@Nullable Advancement p_104406_);
   }
}