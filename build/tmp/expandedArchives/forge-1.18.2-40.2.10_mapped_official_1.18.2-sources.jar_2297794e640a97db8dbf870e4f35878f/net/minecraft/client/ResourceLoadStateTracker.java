package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.server.packs.PackResources;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ResourceLoadStateTracker {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   private ResourceLoadStateTracker.ReloadState reloadState;
   private int reloadCount;

   public void startReload(ResourceLoadStateTracker.ReloadReason p_168558_, List<PackResources> p_168559_) {
      ++this.reloadCount;
      if (this.reloadState != null && !this.reloadState.finished) {
         LOGGER.warn("Reload already ongoing, replacing");
      }

      this.reloadState = new ResourceLoadStateTracker.ReloadState(p_168558_, p_168559_.stream().map(PackResources::getName).collect(ImmutableList.toImmutableList()));
   }

   public void startRecovery(Throwable p_168561_) {
      if (this.reloadState == null) {
         LOGGER.warn("Trying to signal reload recovery, but nothing was started");
         this.reloadState = new ResourceLoadStateTracker.ReloadState(ResourceLoadStateTracker.ReloadReason.UNKNOWN, ImmutableList.of());
      }

      this.reloadState.recoveryReloadInfo = new ResourceLoadStateTracker.RecoveryInfo(p_168561_);
   }

   public void finishReload() {
      if (this.reloadState == null) {
         LOGGER.warn("Trying to finish reload, but nothing was started");
      } else {
         this.reloadState.finished = true;
      }

   }

   public void fillCrashReport(CrashReport p_168563_) {
      CrashReportCategory crashreportcategory = p_168563_.addCategory("Last reload");
      crashreportcategory.setDetail("Reload number", this.reloadCount);
      if (this.reloadState != null) {
         this.reloadState.fillCrashInfo(crashreportcategory);
      }

   }

   @OnlyIn(Dist.CLIENT)
   static class RecoveryInfo {
      private final Throwable error;

      RecoveryInfo(Throwable p_168566_) {
         this.error = p_168566_;
      }

      public void fillCrashInfo(CrashReportCategory p_168569_) {
         p_168569_.setDetail("Recovery", "Yes");
         p_168569_.setDetail("Recovery reason", () -> {
            StringWriter stringwriter = new StringWriter();
            this.error.printStackTrace(new PrintWriter(stringwriter));
            return stringwriter.toString();
         });
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ReloadReason {
      INITIAL("initial"),
      MANUAL("manual"),
      UNKNOWN("unknown");

      final String name;

      private ReloadReason(String p_168579_) {
         this.name = p_168579_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ReloadState {
      private final ResourceLoadStateTracker.ReloadReason reloadReason;
      private final List<String> packs;
      @Nullable
      ResourceLoadStateTracker.RecoveryInfo recoveryReloadInfo;
      boolean finished;

      ReloadState(ResourceLoadStateTracker.ReloadReason p_168589_, List<String> p_168590_) {
         this.reloadReason = p_168589_;
         this.packs = p_168590_;
      }

      public void fillCrashInfo(CrashReportCategory p_168593_) {
         p_168593_.setDetail("Reload reason", this.reloadReason.name);
         p_168593_.setDetail("Finished", this.finished ? "Yes" : "No");
         p_168593_.setDetail("Packs", () -> {
            return String.join(", ", this.packs);
         });
         if (this.recoveryReloadInfo != null) {
            this.recoveryReloadInfo.fillCrashInfo(p_168593_);
         }

      }
   }
}