package net.minecraft.client.resources.sounds;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleSoundInstance extends AbstractSoundInstance {
   public SimpleSoundInstance(SoundEvent p_119717_, SoundSource p_119718_, float p_119719_, float p_119720_, BlockPos p_119721_) {
      this(p_119717_, p_119718_, p_119719_, p_119720_, (double)p_119721_.getX() + 0.5D, (double)p_119721_.getY() + 0.5D, (double)p_119721_.getZ() + 0.5D);
   }

   public static SimpleSoundInstance forUI(SoundEvent p_119753_, float p_119754_) {
      return forUI(p_119753_, p_119754_, 0.25F);
   }

   public static SimpleSoundInstance forUI(SoundEvent p_119756_, float p_119757_, float p_119758_) {
      return new SimpleSoundInstance(p_119756_.getLocation(), SoundSource.MASTER, p_119758_, p_119757_, false, 0, SoundInstance.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
   }

   public static SimpleSoundInstance forMusic(SoundEvent p_119746_) {
      return new SimpleSoundInstance(p_119746_.getLocation(), SoundSource.MUSIC, 1.0F, 1.0F, false, 0, SoundInstance.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
   }

   public static SimpleSoundInstance forRecord(SoundEvent p_119748_, double p_119749_, double p_119750_, double p_119751_) {
      return new SimpleSoundInstance(p_119748_, SoundSource.RECORDS, 4.0F, 1.0F, false, 0, SoundInstance.Attenuation.LINEAR, p_119749_, p_119750_, p_119751_);
   }

   public static SimpleSoundInstance forLocalAmbience(SoundEvent p_119767_, float p_119768_, float p_119769_) {
      return new SimpleSoundInstance(p_119767_.getLocation(), SoundSource.AMBIENT, p_119769_, p_119768_, false, 0, SoundInstance.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
   }

   public static SimpleSoundInstance forAmbientAddition(SoundEvent p_119760_) {
      return forLocalAmbience(p_119760_, 1.0F, 1.0F);
   }

   public static SimpleSoundInstance forAmbientMood(SoundEvent p_119762_, double p_119763_, double p_119764_, double p_119765_) {
      return new SimpleSoundInstance(p_119762_, SoundSource.AMBIENT, 1.0F, 1.0F, false, 0, SoundInstance.Attenuation.LINEAR, p_119763_, p_119764_, p_119765_);
   }

   public SimpleSoundInstance(SoundEvent p_119709_, SoundSource p_119710_, float p_119711_, float p_119712_, double p_119713_, double p_119714_, double p_119715_) {
      this(p_119709_, p_119710_, p_119711_, p_119712_, false, 0, SoundInstance.Attenuation.LINEAR, p_119713_, p_119714_, p_119715_);
   }

   private SimpleSoundInstance(SoundEvent p_119723_, SoundSource p_119724_, float p_119725_, float p_119726_, boolean p_119727_, int p_119728_, SoundInstance.Attenuation p_119729_, double p_119730_, double p_119731_, double p_119732_) {
      this(p_119723_.getLocation(), p_119724_, p_119725_, p_119726_, p_119727_, p_119728_, p_119729_, p_119730_, p_119731_, p_119732_, false);
   }

   public SimpleSoundInstance(ResourceLocation p_119734_, SoundSource p_119735_, float p_119736_, float p_119737_, boolean p_119738_, int p_119739_, SoundInstance.Attenuation p_119740_, double p_119741_, double p_119742_, double p_119743_, boolean p_119744_) {
      super(p_119734_, p_119735_);
      this.volume = p_119736_;
      this.pitch = p_119737_;
      this.x = p_119741_;
      this.y = p_119742_;
      this.z = p_119743_;
      this.looping = p_119738_;
      this.delay = p_119739_;
      this.attenuation = p_119740_;
      this.relative = p_119744_;
   }
}