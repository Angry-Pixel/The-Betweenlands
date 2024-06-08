package net.minecraft.network.chat;

public enum ChatType {
   CHAT((byte)0, false),
   SYSTEM((byte)1, true),
   GAME_INFO((byte)2, true);

   private final byte index;
   private final boolean interrupt;

   private ChatType(byte p_130608_, boolean p_130609_) {
      this.index = p_130608_;
      this.interrupt = p_130609_;
   }

   public byte getIndex() {
      return this.index;
   }

   public static ChatType getForIndex(byte p_130612_) {
      for(ChatType chattype : values()) {
         if (p_130612_ == chattype.index) {
            return chattype;
         }
      }

      return CHAT;
   }

   public boolean shouldInterrupt() {
      return this.interrupt;
   }
}