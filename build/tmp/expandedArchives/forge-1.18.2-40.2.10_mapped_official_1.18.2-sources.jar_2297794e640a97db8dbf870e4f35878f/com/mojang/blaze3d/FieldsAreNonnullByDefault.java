package com.mojang.blaze3d;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Nonnull
@TypeQualifierDefault({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@OnlyIn(Dist.CLIENT)
public @interface FieldsAreNonnullByDefault {
}