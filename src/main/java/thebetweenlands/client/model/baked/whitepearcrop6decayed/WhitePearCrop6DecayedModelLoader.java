package thebetweenlands.client.model.baked.whitepearcrop6decayed;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WhitePearCrop6DecayedModelLoader implements IGeometryLoader<UnbakedWhitePearCrop6DecayedModel> {
	public static final WhitePearCrop6DecayedModelLoader INSTANCE = new WhitePearCrop6DecayedModelLoader();

	@Override
	public UnbakedWhitePearCrop6DecayedModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWhitePearCrop6DecayedModel();
	}
}

