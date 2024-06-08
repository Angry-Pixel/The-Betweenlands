package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import org.slf4j.Logger;

public class BlockMath {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL = Util.make(Maps.newEnumMap(Direction.class), (p_121851_) -> {
      p_121851_.put(Direction.SOUTH, Transformation.identity());
      p_121851_.put(Direction.EAST, new Transformation((Vector3f)null, Vector3f.YP.rotationDegrees(90.0F), (Vector3f)null, (Quaternion)null));
      p_121851_.put(Direction.WEST, new Transformation((Vector3f)null, Vector3f.YP.rotationDegrees(-90.0F), (Vector3f)null, (Quaternion)null));
      p_121851_.put(Direction.NORTH, new Transformation((Vector3f)null, Vector3f.YP.rotationDegrees(180.0F), (Vector3f)null, (Quaternion)null));
      p_121851_.put(Direction.UP, new Transformation((Vector3f)null, Vector3f.XP.rotationDegrees(-90.0F), (Vector3f)null, (Quaternion)null));
      p_121851_.put(Direction.DOWN, new Transformation((Vector3f)null, Vector3f.XP.rotationDegrees(90.0F), (Vector3f)null, (Quaternion)null));
   });
   public static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL = Util.make(Maps.newEnumMap(Direction.class), (p_121849_) -> {
      for(Direction direction : Direction.values()) {
         p_121849_.put(direction, VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get(direction).inverse());
      }

   });

   public static Transformation blockCenterToCorner(Transformation p_121843_) {
      Matrix4f matrix4f = Matrix4f.createTranslateMatrix(0.5F, 0.5F, 0.5F);
      matrix4f.multiply(p_121843_.getMatrix());
      matrix4f.multiply(Matrix4f.createTranslateMatrix(-0.5F, -0.5F, -0.5F));
      return new Transformation(matrix4f);
   }

   public static Transformation blockCornerToCenter(Transformation p_175260_) {
      Matrix4f matrix4f = Matrix4f.createTranslateMatrix(-0.5F, -0.5F, -0.5F);
      matrix4f.multiply(p_175260_.getMatrix());
      matrix4f.multiply(Matrix4f.createTranslateMatrix(0.5F, 0.5F, 0.5F));
      return new Transformation(matrix4f);
   }

   public static Transformation getUVLockTransform(Transformation p_121845_, Direction p_121846_, Supplier<String> p_121847_) {
      Direction direction = Direction.rotate(p_121845_.getMatrix(), p_121846_);
      Transformation transformation = p_121845_.inverse();
      if (transformation == null) {
         LOGGER.warn(p_121847_.get());
         return new Transformation((Vector3f)null, (Quaternion)null, new Vector3f(0.0F, 0.0F, 0.0F), (Quaternion)null);
      } else {
         Transformation transformation1 = VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL.get(p_121846_).compose(transformation).compose(VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get(direction));
         return blockCenterToCorner(transformation1);
      }
   }
}