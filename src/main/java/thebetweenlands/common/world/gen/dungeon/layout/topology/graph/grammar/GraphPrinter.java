package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphPrinter {
	//This entire thing is cursed but it's just for testing

	public static String toEdgeListString(Graph graph) {
		StringBuilder str = new StringBuilder();
		for(Node node : graph.getNodes()) {
			for(Edge edge : node.getEdges()) {
				if(edge.isBidirectional() || edge.getLeft() == node) {
					str.append(node.getType() + " (" + node.getID() + ") --(" + edge.getType() + ")--> " + edge.getOther(node).getType() + " (" + edge.getOther(node).getID() + ")\n");
				}
			}
		}
		return str.substring(0, Math.max(0, str.length() - 1));
	}

	public static String toSpanningTreeString(Node root) {
		StringBuilder str = new StringBuilder();
		Map<Integer, List<Entry>> entriesByDepth = new HashMap<>();
		Map<Integer, Integer> nodeWidths = new HashMap<>();
		int maxDepth = 0;
		int maxEntries = 0;
		for(Entry entry : findNodeDepths(root)) {
			List<Entry> entries = entriesByDepth.get(entry.depth);
			if(entries == null) {
				entriesByDepth.put(entry.depth, entries = new ArrayList<>());
			}
			entries.add(entry);
			maxEntries = Math.max(maxEntries, entries.size());
			maxDepth = Math.max(maxDepth, entry.depth);
			if(nodeWidths.containsKey(entry.depth)) {
				nodeWidths.put(entry.depth, Math.max(nodeWidths.get(entry.depth), entry.toString().length()));
			} else {
				nodeWidths.put(entry.depth, entry.toString().length());
			}
		}
		Map<Node, Integer> nodeY = new HashMap<>();
		int maxY = 0;
		for(int y = 0; y <= maxEntries; y++) {
			for(int x = 0; x <= maxDepth; x++) {
				List<Entry> entries = entriesByDepth.get(x);
				for(Entry entry : entries) {
					if(entry.parent == null) {
						nodeY.put(entry.node, 0);
					} else {
						int parentY = nodeY.get(entry.parent.node);
						int offsetY = 0;
						for(Edge edge : entry.parent.node.getEdges()) {
							if(edge.isFrom(entry.parent.node)) {
								Node neighbor = edge.getOther(entry.parent.node);
								if(neighbor == entry.node) {
									nodeY.put(entry.node, parentY + offsetY);
									maxY = Math.max(maxY, parentY + offsetY);
									break;
								}
								offsetY++;
							}
						}
					}
				}
			}
		}
		boolean moved;
		do {
			moved = false;
			for(int x = maxDepth; x >= 0; x--) {
				List<Entry> entries = entriesByDepth.get(x);
				for(Entry entry : entries) {
					int ey = nodeY.get(entry.node);
					int parentey = entry.parent != null ? nodeY.get(entry.parent.node) : 0;
					List<Entry> prevEntries = entriesByDepth.get(x - 1);
					if(prevEntries != null) {
						for(Entry prevEntry : prevEntries) {
							if(prevEntry != entry.parent) {
								int prevey = nodeY.get(prevEntry.node);
								if(prevey <= ey && prevey > parentey) {
									Entry cprev = prevEntry;
									Set<Entry> visited = new HashSet<>();
									while(cprev != null) {
										if(nodeY.get(cprev.node) == prevey) {
											visited.add(cprev);
											nodeY.put(cprev.node, ey + 1);
											maxY = Math.max(maxY, ey + 1);
											cprev = cprev.parent;
											moved = true;
										} else {
											visited.add(cprev);
											int dy = (ey + 1) - prevey;
											boolean changed;
											do {
												changed = false;
												for(int x2 = cprev.depth; x2 <= maxDepth; x2++) {
													List<Entry> nextEntries = entriesByDepth.get(x2);
													for(Entry nextEntry : nextEntries) {
														if(nodeY.get(nextEntry.node) >= ey && visited.contains(nextEntry.parent) && visited.add(nextEntry)) {
															int ny = nodeY.get(nextEntry.node) + dy;
															nodeY.put(nextEntry.node, ny);
															maxY = Math.max(maxY, ny);
															changed = true;
															moved = true;
														}
													}
												}
											} while(changed);
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		} while(moved);
		for(int y = 0; y <= maxY; y++) {
			for(int x = 0; x <= maxDepth; x++) {
				List<Entry> entries = entriesByDepth.get(x);
				Entry entry = null;
				for(Entry e : entries) {
					if(nodeY.get(e.node) == y) {
						entry = e;
					}
				}
				if(entry != null) {
					str.append(entry.toString());
					for(int j = 0; j < nodeWidths.get(x) + 1 - entry.toString().length(); j++) {
						str.append(" ");
					}
				} else {
					Entry aboveEntry = null;
					int aboveEntryY = 0;
					for(Entry e : entries) {
						int ey = nodeY.get(e.node);
						if(ey <= y && ey >= aboveEntryY) {
							aboveEntry = e;
							aboveEntryY = ey;
						}
					}
					String midChar = " ";
					String fillerChar = " ";
					if(aboveEntry != null) {
						List<Entry> nextEntries = entriesByDepth.get(x + 1);
						if(nextEntries != null) {
							boolean hasRight = false;
							boolean hasLower = false;
							for(Entry nextEntry : nextEntries) {
								if(nextEntry.parent != null && nextEntry.parent.node == aboveEntry.node) {
									if(nodeY.get(nextEntry.node) == y) {
										midChar = "╚";
										for(Entry nextEntry2 : nextEntries) {
											if(nodeY.get(nextEntry2.node) > y && nextEntry2.parent != null && nextEntry2.parent.node == aboveEntry.node) {
												midChar = "╠";
												break;
											}
										}
										fillerChar = "═";
										hasRight = true;
										break;
									} else if(nodeY.get(nextEntry.node) >= y) {
										hasLower = true;
									}
								}
							}
							if(hasLower && !hasRight) {
								midChar = "║";
							}
						}
					}
					int w = nodeWidths.get(x) + 1;
					for(int j = 0; j < w; j++) {
						if(j == w / 2) {
							str.append(midChar);
						} else if(j > w / 2 && j != w - 1) {
							str.append(fillerChar);
						} else {
							str.append(" ");
						}
					}
				}
			}
			if(y != maxY) {
				str.append("\n");
			}
		}
		return str.toString();
	}

	private static class Entry {
		Entry parent;
		Node node;
		int depth;

		Entry(Entry parent, Node node, int depth) {
			this.parent = parent;
			this.node = node;
			this.depth = depth;
		}

		@Override
		public String toString() {
			return this.node.getType() + " (" + this.node.getID() + ")";
		}
	}

	private static List<Entry> findNodeDepths(Node root) {
		List<Entry> visited = new ArrayList<>();
		Set<Node> closed = new HashSet<>();
		Deque<Entry> stack = new LinkedList<Entry>();

		Entry entry = new Entry(null, root, 0);
		closed.add(entry.node);
		visited.add(entry);
		stack.add(entry);

		while(!stack.isEmpty()) {
			entry = stack.removeFirst();

			for(Edge edge : entry.node.getEdges()) {
				if(edge.isFrom(entry.node)) {
					Node neighbor = edge.getOther(entry.node);
					if(closed.add(neighbor)) {
						Entry neighborEntry = new Entry(entry, neighbor, entry.depth + 1);
						visited.add(neighborEntry);
						stack.addLast(neighborEntry);
					}
				}
			}
		}

		return visited;
	}
}
