package thebetweenlands.client.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thebetweenlands.client.rendering.mixintypes.subclasses.BlockElementFaceEX;

import java.lang.reflect.Type;

// Inject flat shading value
@Mixin(BlockElementFace.Deserializer.class)
public class MixinBlockElementFaceDeserializer {
	@Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockElementFace;", at = @At("RETURN"), cancellable = true)
	public void deserialize(JsonElement p_111365_, Type p_111366_, JsonDeserializationContext p_111367_, CallbackInfoReturnable<BlockElementFace> cir) throws JsonParseException {
		BlockElementFace out = cir.getReturnValue();
		JsonObject jsonobject = p_111365_.getAsJsonObject();
		if (!getShading(jsonobject)) {
			cir.setReturnValue(out);
			return;
		}
		cir.setReturnValue(new BlockElementFaceEX(out, true, getOverrideLightingSide(jsonobject)));
	}

	public Boolean getShading(JsonObject json) {
		return GsonHelper.getAsBoolean(json, "flatshade", false);
	}

	public Direction getOverrideLightingSide(JsonObject json) {
		return Direction.byName(GsonHelper.getAsString(json, "facing", ""));
	}
}
