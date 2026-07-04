package thebetweenlands.client.handler;

import java.util.concurrent.ConcurrentLinkedQueue;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import thebetweenlands.client.renderer.util.VertexBufferData;
import thebetweenlands.client.renderer.util.VertexBufferFence;
import thebetweenlands.client.renderer.util.VertexBufferFence.VertexBufferWeakFence;

public final class VertexBufferCleanupHandler {

	// Evil statics, I know
	// They're a necessary evil here to make sure everything gets cleaned up
	private static final ConcurrentLinkedQueue<VertexBufferFence.VertexBufferWeakFence> PENDING_FENCES = new ConcurrentLinkedQueue<>();

	private static final ObjectArraySet<VertexBufferFence.VertexBufferWeakFence> FENCES = new ObjectArraySet<>();
	
	public static void submit(VertexBufferFence.VertexBufferWeakFence fence) {
		if(!fence.hasExpired()) {
			PENDING_FENCES.add(fence);
		}
	}
	
	public static VertexBufferFence.VertexBufferStrongFence createAndSubmit(VertexBufferData data) {
		VertexBufferFence.VertexBufferFencePair pair = VertexBufferFence.createVertexFence(data);
		submit(pair.weakFence());
		return pair.strongFence();
	}
	
	private static void addPendingFences() {
		VertexBufferWeakFence fence = null;
		while((fence = PENDING_FENCES.poll()) != null) {
			FENCES.add(fence);
		}
	}
	
	// Closes any vertex buffers that we otherwise would've lost references to
	public static void tickCleanup() {
		addPendingFences();
		
		ObjectIterator<VertexBufferWeakFence> iterator = FENCES.iterator();
		
		while(iterator.hasNext()) {
			VertexBufferWeakFence fence = iterator.next();
			if(fence.hasExpired()) {
				fence.getVertexBufferData().vertexBuffer().close();
				iterator.remove();
			}
		}
	}
	
}
