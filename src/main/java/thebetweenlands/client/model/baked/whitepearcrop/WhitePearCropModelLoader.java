package thebetweenlands.client.model.baked.whitepearcrop;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WhitePearCropModelLoader implements IGeometryLoader<UnbakedWhitePearCropModel> {
	public static final WhitePearCropModelLoader INSTANCE = new WhitePearCropModelLoader();

	@Override
	public UnbakedWhitePearCropModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWhitePearCropModel();
	}
}

