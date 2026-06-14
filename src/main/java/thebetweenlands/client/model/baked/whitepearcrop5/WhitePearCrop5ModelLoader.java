package thebetweenlands.client.model.baked.whitepearcrop5;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WhitePearCrop5ModelLoader implements IGeometryLoader<UnbakedWhitePearCrop5Model> {
	public static final WhitePearCrop5ModelLoader INSTANCE = new WhitePearCrop5ModelLoader();

	@Override
	public UnbakedWhitePearCrop5Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWhitePearCrop5Model();
	}
}

