package thebetweenlands.client.model.baked.whitepearcrop2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WhitePearCrop2ModelLoader implements IGeometryLoader<UnbakedWhitePearCrop2Model> {
	public static final WhitePearCrop2ModelLoader INSTANCE = new WhitePearCrop2ModelLoader();

	@Override
	public UnbakedWhitePearCrop2Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWhitePearCrop2Model();
	}
}

