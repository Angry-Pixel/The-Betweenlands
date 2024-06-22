package thebetweenlands.client.mixin;

import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thebetweenlands.client.rendering.mixintypes.subclasses.BakedQuadEX;
import thebetweenlands.client.rendering.mixintypes.subclasses.BlockElementFaceEX;

@Mixin(FaceBakery.class)
public class MixinFaceBakery {

	@Inject(method = "bakeQuad", at = @At("RETURN"), cancellable = true)
	protected void bakeQuadPatch(Vector3f p_111601_, Vector3f p_111602_, BlockElementFace p_111603_, TextureAtlasSprite p_111604_, Direction p_111605_, ModelState p_111606_, BlockElementRotation p_111607_, boolean p_111608_, ResourceLocation p_111609_, CallbackInfoReturnable<BakedQuad> cir) {
		if (p_111603_ instanceof BlockElementFaceEX) {
			BlockElementFaceEX element = (BlockElementFaceEX) p_111603_;
			BakedQuad quad = cir.getReturnValue();
			cir.setReturnValue(new BakedQuadEX(quad, element.flatshade, element.overrideLightingSide));
		}
	}
}
