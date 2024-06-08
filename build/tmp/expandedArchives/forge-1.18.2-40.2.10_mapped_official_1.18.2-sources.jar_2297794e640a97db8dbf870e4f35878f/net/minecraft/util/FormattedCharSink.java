package net.minecraft.util;

import net.minecraft.network.chat.Style;

@FunctionalInterface
public interface FormattedCharSink {
   boolean accept(int p_13746_, Style p_13747_, int p_13748_);
}