package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundSource;

public interface Saddleable {
   boolean isSaddleable();

   void equipSaddle(@Nullable SoundSource p_21748_);

   boolean isSaddled();
}