package thebetweenlands.client.model.baked.funguscrop2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class FungusCrop2ModelLoader implements IGeometryLoader<UnbakedFungusCrop2Model> {
	public static final FungusCrop2ModelLoader INSTANCE = new FungusCrop2ModelLoader();

	@Override
	public UnbakedFungusCrop2Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedFungusCrop2Model();
	}
}

