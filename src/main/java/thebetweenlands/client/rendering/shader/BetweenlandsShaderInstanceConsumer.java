package thebetweenlands.client.rendering.shader;

import java.util.function.Consumer;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.TheBetweenlands;

@OnlyIn(Dist.CLIENT)
public class BetweenlandsShaderInstanceConsumer implements Consumer<ShaderInstance> {
	
	@Override
	public void accept(ShaderInstance instance) {
	}
}
