package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class TreeDecoratorType<P extends TreeDecorator> extends net.minecraftforge.registries.ForgeRegistryEntry<TreeDecoratorType<?>> {
   public static final TreeDecoratorType<TrunkVineDecorator> TRUNK_VINE = register("trunk_vine", TrunkVineDecorator.CODEC);
   public static final TreeDecoratorType<LeaveVineDecorator> LEAVE_VINE = register("leave_vine", LeaveVineDecorator.CODEC);
   public static final TreeDecoratorType<CocoaDecorator> COCOA = register("cocoa", CocoaDecorator.CODEC);
   public static final TreeDecoratorType<BeehiveDecorator> BEEHIVE = register("beehive", BeehiveDecorator.CODEC);
   public static final TreeDecoratorType<AlterGroundDecorator> ALTER_GROUND = register("alter_ground", AlterGroundDecorator.CODEC);
   private final Codec<P> codec;

   private static <P extends TreeDecorator> TreeDecoratorType<P> register(String p_70053_, Codec<P> p_70054_) {
      return Registry.register(Registry.TREE_DECORATOR_TYPES, p_70053_, new TreeDecoratorType<>(p_70054_));
   }

   public TreeDecoratorType(Codec<P> p_70050_) {
      this.codec = p_70050_;
   }

   public Codec<P> codec() {
      return this.codec;
   }
}
