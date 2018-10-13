package thebetweenlands.monkeytest;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.player.EntityPlayer;

public interface IParticleTracked {

  public boolean alive();

  public boolean isAdditive();

  public boolean ignoreDepth();

  boolean renderThroughBlocks();

  void onUpdate();

  void renderParticle(BufferBuilder buffer, EntityPlayer player, float partialTicks, float f, float f4, float f1, float f2, float f3);
}
