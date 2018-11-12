package thebetweenlands.common.inventory.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

public class RuneChainLinkInfo {
	public static final class Link {
		private int outputRune;
		private int output;

		private Link(int outputRune, int output) {
			this.outputRune = outputRune;
			this.output = output;
		}

		public int getOutputRune() {
			return this.outputRune;
		}

		public int getOutput() {
			return this.output;
		}
	}

	private final Map<Integer, Map<Integer, Link>> links = new HashMap<>();

	public Collection<Integer> getLinkedSlots(int rune) {
		Set<Integer> linkedSlots = new HashSet<>();
		Map<Integer, Link> links = this.links.get(rune);
		if(links != null) {
			for(Entry<Integer, Link> link : links.entrySet()) {
				linkedSlots.add(link.getKey());
			}
		}
		return linkedSlots;
	}

	@Nullable
	public Link getLink(int rune, int input) {
		if(input >= 0) {
			Map<Integer, Link> links = this.links.get(rune);
			if(links != null) {
				return links.get(input);
			}
		}
		return null;
	}

	public boolean link(int rune, int input, int outputRune, int output) {
		if(rune <= outputRune) {
			return false;
		}

		Map<Integer, Link> links = this.links.get(rune);

		if(links == null) {
			this.links.put(rune, links = new HashMap<>());
		}

		links.put(input, new Link(outputRune, output));

		return true;
	}

	@Nullable
	public Link unlink(int rune, int input) {
		Map<Integer, Link> links = this.links.get(rune);
		if(links != null) {
			Link removed = links.remove(input);
			if(links.isEmpty()) {
				this.links.remove(rune);
			}
			return removed;
		}
		return null;
	}

	public void remove(int rune) {
		this.links.remove(rune);
	}

	public void move(int oldPosition, int newPosition) {
		//First adjust links that point towards the old position
		for(Entry<Integer, Map<Integer, Link>> entry : this.links.entrySet()) {
			Map<Integer, Link> links = entry.getValue();
			for(Link link : links.values()) {
				if(link.outputRune == oldPosition) {
					link.outputRune = newPosition;
				}
			}
		}

		Map<Integer, Link> links = this.links.get(oldPosition);
		
		if(links != null) {
			Map<Integer, Link> newPosLinks = this.links.get(newPosition);
			if(newPosLinks == null) {
				this.links.put(newPosition, newPosLinks = new HashMap<>());
			} else {
				newPosLinks.clear();
			}
	
			newPosLinks.putAll(links);
	
			this.links.remove(oldPosition);
		} else {
			if(this.links.containsKey(newPosition)) {
				this.links.remove(newPosition);
			}
		}
	}
}
