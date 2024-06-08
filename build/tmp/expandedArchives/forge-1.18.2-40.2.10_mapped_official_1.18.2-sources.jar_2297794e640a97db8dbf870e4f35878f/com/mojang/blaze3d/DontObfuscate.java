package com.mojang.blaze3d;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@TypeQualifierDefault({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
@OnlyIn(Dist.CLIENT)
public @interface DontObfuscate {
}