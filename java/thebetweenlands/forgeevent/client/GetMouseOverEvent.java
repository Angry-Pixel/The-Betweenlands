package thebetweenlands.forgeevent.client;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

public abstract class GetMouseOverEvent extends Event {
	private float delta;

	public GetMouseOverEvent(float delta) {
		this.delta = delta;
	}

	public float getDelta() {
		return delta;
	}

	@Cancelable
	public static class Pre extends GetMouseOverEvent {
		public Pre(float delta) {
			super(delta);
		}
	}

	public static class Post extends GetMouseOverEvent {
		public Post(float delta) {
			super(delta);
		}
	}
}
