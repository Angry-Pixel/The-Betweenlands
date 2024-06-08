package net.minecraft.network.chat;

public class TranslatableFormatException extends IllegalArgumentException {
   public TranslatableFormatException(TranslatableComponent p_131336_, String p_131337_) {
      super(String.format("Error parsing: %s: %s", p_131336_, p_131337_));
   }

   public TranslatableFormatException(TranslatableComponent p_131333_, int p_131334_) {
      super(String.format("Invalid index %d requested for %s", p_131334_, p_131333_));
   }

   public TranslatableFormatException(TranslatableComponent p_131339_, Throwable p_131340_) {
      super(String.format("Error while parsing: %s", p_131339_), p_131340_);
   }
}