package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsServiceException extends Exception {
   public final int httpResultCode;
   public final String rawResponse;
   @Nullable
   public final RealmsError realmsError;

   public RealmsServiceException(int p_87783_, String p_87784_, RealmsError p_87785_) {
      super(p_87784_);
      this.httpResultCode = p_87783_;
      this.rawResponse = p_87784_;
      this.realmsError = p_87785_;
   }

   public RealmsServiceException(int p_200943_, String p_200944_) {
      super(p_200944_);
      this.httpResultCode = p_200943_;
      this.rawResponse = p_200944_;
      this.realmsError = null;
   }

   public String toString() {
      if (this.realmsError != null) {
         String s = "mco.errorMessage." + this.realmsError.getErrorCode();
         String s1 = I18n.exists(s) ? I18n.get(s) : this.realmsError.getErrorMessage();
         return "Realms service error (%d/%d) %s".formatted(this.httpResultCode, this.realmsError.getErrorCode(), s1);
      } else {
         return "Realms service error (%d) %s".formatted(this.httpResultCode, this.rawResponse);
      }
   }

   public int realmsErrorCodeOrDefault(int p_200946_) {
      return this.realmsError != null ? this.realmsError.getErrorCode() : p_200946_;
   }
}