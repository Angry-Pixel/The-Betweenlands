package net.minecraft.nbt;

public class NbtAccounter {
   public static final NbtAccounter UNLIMITED = new NbtAccounter(0L) {
      public void accountBits(long p_128927_) {
      }
   };
   private final long quota;
   private long usage;

   public NbtAccounter(long p_128922_) {
      this.quota = p_128922_;
   }

   public void accountBits(long p_128923_) {
      this.usage += p_128923_ / 8L;
      if (this.usage > this.quota) {
         throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.usage + "bytes where max allowed: " + this.quota);
      }
   }

   /*
    * UTF8 is not a simple encoding system, each character can be either
    * 1, 2, or 3 bytes. Depending on where it's numerical value falls.
    * We have to count up each character individually to see the true
    * length of the data.
    *
    * Basic concept is that it uses the MSB of each byte as a 'read more' signal.
    * So it has to shift each 7-bit segment.
    *
    * This will accurately count the correct byte length to encode this string, plus the 2 bytes for it's length prefix.
    */
   public String readUTF(String data) {
      accountBits(16); //Header length
      if (data == null)
         return data;

      int len = data.length();
      int utflen = 0;

      for (int i = 0; i < len; i++) {
          int c = data.charAt(i);
          if ((c >= 0x0001) && (c <= 0x007F)) utflen += 1;
          else if (c > 0x07FF)                utflen += 3;
          else                                utflen += 2;
      }
      accountBits(8 * utflen);

      return data;
   }
}
