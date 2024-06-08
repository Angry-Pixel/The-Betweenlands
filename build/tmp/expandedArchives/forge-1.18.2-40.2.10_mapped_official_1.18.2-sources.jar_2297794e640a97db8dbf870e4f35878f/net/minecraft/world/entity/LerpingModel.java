package net.minecraft.world.entity;

import com.mojang.math.Vector3f;
import java.util.Map;

public interface LerpingModel {
   Map<String, Vector3f> getModelRotationValues();
}