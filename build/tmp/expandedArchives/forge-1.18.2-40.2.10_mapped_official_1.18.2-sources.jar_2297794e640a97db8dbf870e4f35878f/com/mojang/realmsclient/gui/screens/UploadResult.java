package com.mojang.realmsclient.gui.screens;

import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UploadResult {
   public final int statusCode;
   @Nullable
   public final String errorMessage;

   UploadResult(int p_90136_, String p_90137_) {
      this.statusCode = p_90136_;
      this.errorMessage = p_90137_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private int statusCode = -1;
      private String errorMessage;

      public UploadResult.Builder withStatusCode(int p_90147_) {
         this.statusCode = p_90147_;
         return this;
      }

      public UploadResult.Builder withErrorMessage(@Nullable String p_90149_) {
         this.errorMessage = p_90149_;
         return this;
      }

      public UploadResult build() {
         return new UploadResult(this.statusCode, this.errorMessage);
      }
   }
}