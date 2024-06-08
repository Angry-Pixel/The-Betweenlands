package net.minecraft.world.ticks;

import java.util.function.Function;
import net.minecraft.nbt.Tag;

public interface SerializableTickContainer<T> {
   Tag save(long p_193426_, Function<T, String> p_193427_);
}