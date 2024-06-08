package net.minecraft.network.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;

public interface ContextAwareComponent {
   MutableComponent resolve(@Nullable CommandSourceStack p_130756_, @Nullable Entity p_130757_, int p_130758_) throws CommandSyntaxException;
}