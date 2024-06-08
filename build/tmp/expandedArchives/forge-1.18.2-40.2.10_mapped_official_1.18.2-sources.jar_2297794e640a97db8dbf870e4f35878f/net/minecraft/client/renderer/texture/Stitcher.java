package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Stitcher {
   private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();

   private static final Comparator<Stitcher.Holder> HOLDER_COMPARATOR = Comparator.<Stitcher.Holder, Integer>comparing((p_118201_) -> {
      return -p_118201_.height;
   }).thenComparing((p_118199_) -> {
      return -p_118199_.width;
   }).thenComparing((p_118197_) -> {
      return p_118197_.spriteInfo.name();
   });
   private final int mipLevel;
   private final Set<Stitcher.Holder> texturesToBeStitched = Sets.newHashSetWithExpectedSize(256);
   private final List<Stitcher.Region> storage = Lists.newArrayListWithCapacity(256);
   private int storageX;
   private int storageY;
   private final int maxWidth;
   private final int maxHeight;

   public Stitcher(int p_118171_, int p_118172_, int p_118173_) {
      this.mipLevel = p_118173_;
      this.maxWidth = p_118171_;
      this.maxHeight = p_118172_;
   }

   public int getWidth() {
      return this.storageX;
   }

   public int getHeight() {
      return this.storageY;
   }

   public void registerSprite(TextureAtlasSprite.Info p_118186_) {
      Stitcher.Holder stitcher$holder = new Stitcher.Holder(p_118186_, this.mipLevel);
      this.texturesToBeStitched.add(stitcher$holder);
   }

   public void stitch() {
      List<Stitcher.Holder> list = Lists.newArrayList(this.texturesToBeStitched);
      list.sort(HOLDER_COMPARATOR);

      for(Stitcher.Holder stitcher$holder : list) {
         if (!this.addToStorage(stitcher$holder)) {
            LOGGER.info(new net.minecraftforge.fml.loading.AdvancedLogMessageAdapter(sb->{
               sb.append("Unable to fit: ").append(stitcher$holder.spriteInfo.name());
               sb.append(" - size: ").append(stitcher$holder.spriteInfo.width()).append("x").append(stitcher$holder.spriteInfo.height());
               sb.append(" - Maybe try a lower resolution resourcepack?\n");
               list.forEach(h-> sb.append("\t").append(h).append("\n"));
            }));
            throw new StitcherException(stitcher$holder.spriteInfo, list.stream().map((p_118195_) -> {
               return p_118195_.spriteInfo;
            }).collect(ImmutableList.toImmutableList()));
         }
      }

      this.storageX = Mth.smallestEncompassingPowerOfTwo(this.storageX);
      this.storageY = Mth.smallestEncompassingPowerOfTwo(this.storageY);
   }

   public void gatherSprites(Stitcher.SpriteLoader p_118181_) {
      for(Stitcher.Region stitcher$region : this.storage) {
         stitcher$region.walk((p_118184_) -> {
            Stitcher.Holder stitcher$holder = p_118184_.getHolder();
            TextureAtlasSprite.Info textureatlassprite$info = stitcher$holder.spriteInfo;
            p_118181_.load(textureatlassprite$info, this.storageX, this.storageY, p_118184_.getX(), p_118184_.getY());
         });
      }

   }

   static int smallestFittingMinTexel(int p_118189_, int p_118190_) {
      return (p_118189_ >> p_118190_) + ((p_118189_ & (1 << p_118190_) - 1) == 0 ? 0 : 1) << p_118190_;
   }

   private boolean addToStorage(Stitcher.Holder p_118179_) {
      for(Stitcher.Region stitcher$region : this.storage) {
         if (stitcher$region.add(p_118179_)) {
            return true;
         }
      }

      return this.expand(p_118179_);
   }

   private boolean expand(Stitcher.Holder p_118192_) {
      int i = Mth.smallestEncompassingPowerOfTwo(this.storageX);
      int j = Mth.smallestEncompassingPowerOfTwo(this.storageY);
      int k = Mth.smallestEncompassingPowerOfTwo(this.storageX + p_118192_.width);
      int l = Mth.smallestEncompassingPowerOfTwo(this.storageY + p_118192_.height);
      boolean flag1 = k <= this.maxWidth;
      boolean flag2 = l <= this.maxHeight;
      if (!flag1 && !flag2) {
         return false;
      } else {
         boolean flag3 = flag1 && i != k;
         boolean flag4 = flag2 && j != l;
         boolean flag;
         if (flag3 ^ flag4) {
            flag = !flag3 && flag1; // Forge: Fix stitcher not expanding entire height before growing width, and (potentially) growing larger then the max size.
         } else {
            flag = flag1 && i <= j;
         }

         Stitcher.Region stitcher$region;
         if (flag) {
            if (this.storageY == 0) {
               this.storageY = p_118192_.height;
            }

            stitcher$region = new Stitcher.Region(this.storageX, 0, p_118192_.width, this.storageY);
            this.storageX += p_118192_.width;
         } else {
            stitcher$region = new Stitcher.Region(0, this.storageY, this.storageX, p_118192_.height);
            this.storageY += p_118192_.height;
         }

         stitcher$region.add(p_118192_);
         this.storage.add(stitcher$region);
         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Holder {
      public final TextureAtlasSprite.Info spriteInfo;
      public final int width;
      public final int height;

      public Holder(TextureAtlasSprite.Info p_118206_, int p_118207_) {
         this.spriteInfo = p_118206_;
         this.width = Stitcher.smallestFittingMinTexel(p_118206_.width(), p_118207_);
         this.height = Stitcher.smallestFittingMinTexel(p_118206_.height(), p_118207_);
      }

      public String toString() {
         return "Holder{width=" + this.width + ", height=" + this.height + ", name=" + this.spriteInfo.name() + '}';
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Region {
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;
      private List<Stitcher.Region> subSlots;
      private Stitcher.Holder holder;

      public Region(int p_118216_, int p_118217_, int p_118218_, int p_118219_) {
         this.originX = p_118216_;
         this.originY = p_118217_;
         this.width = p_118218_;
         this.height = p_118219_;
      }

      public Stitcher.Holder getHolder() {
         return this.holder;
      }

      public int getX() {
         return this.originX;
      }

      public int getY() {
         return this.originY;
      }

      public boolean add(Stitcher.Holder p_118222_) {
         if (this.holder != null) {
            return false;
         } else {
            int i = p_118222_.width;
            int j = p_118222_.height;
            if (i <= this.width && j <= this.height) {
               if (i == this.width && j == this.height) {
                  this.holder = p_118222_;
                  return true;
               } else {
                  if (this.subSlots == null) {
                     this.subSlots = Lists.newArrayListWithCapacity(1);
                     this.subSlots.add(new Stitcher.Region(this.originX, this.originY, i, j));
                     int k = this.width - i;
                     int l = this.height - j;
                     if (l > 0 && k > 0) {
                        int i1 = Math.max(this.height, k);
                        int j1 = Math.max(this.width, l);
                        if (i1 >= j1) {
                           this.subSlots.add(new Stitcher.Region(this.originX, this.originY + j, i, l));
                           this.subSlots.add(new Stitcher.Region(this.originX + i, this.originY, k, this.height));
                        } else {
                           this.subSlots.add(new Stitcher.Region(this.originX + i, this.originY, k, j));
                           this.subSlots.add(new Stitcher.Region(this.originX, this.originY + j, this.width, l));
                        }
                     } else if (k == 0) {
                        this.subSlots.add(new Stitcher.Region(this.originX, this.originY + j, i, l));
                     } else if (l == 0) {
                        this.subSlots.add(new Stitcher.Region(this.originX + i, this.originY, k, j));
                     }
                  }

                  for(Stitcher.Region stitcher$region : this.subSlots) {
                     if (stitcher$region.add(p_118222_)) {
                        return true;
                     }
                  }

                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public void walk(Consumer<Stitcher.Region> p_118224_) {
         if (this.holder != null) {
            p_118224_.accept(this);
         } else if (this.subSlots != null) {
            for(Stitcher.Region stitcher$region : this.subSlots) {
               stitcher$region.walk(p_118224_);
            }
         }

      }

      public String toString() {
         return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + "}";
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface SpriteLoader {
      void load(TextureAtlasSprite.Info p_118229_, int p_118230_, int p_118231_, int p_118232_, int p_118233_);
   }
}
