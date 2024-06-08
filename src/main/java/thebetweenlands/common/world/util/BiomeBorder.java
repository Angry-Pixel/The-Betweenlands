package thebetweenlands.common.world.util;

import java.awt.Point;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;


public class BiomeBorder {
	
	public final List<Point> vectors;
	public final List<Pair<ResourceLocation, Double>> borderingbiomes;
	public final double truedistance;
	// Container holding 
	public BiomeBorder(List<Point> inputvectors, List<Pair<ResourceLocation, Double>> borderbiomes, double distance) {
		this.vectors = inputvectors;
		this.borderingbiomes = borderbiomes;
		this.truedistance = distance;
	}
	
	public Point getAveragePoint() {

		int listsize = this.vectors.size();
		int x = 0;
		int y = 0;
		
		// devide by zero safety nets
		if (listsize != 0) {
			for (int read = 0; read < listsize; read ++) {
				x += this.vectors.get(read).x;
				y += this.vectors.get(read).y;
			}
			
			// devide by zero safety nets
			if (x != 0)
			{
				x = x / listsize;
			}
			
			if (y != 0)
			{
				y = y / listsize;
			}
		}
		return new Point(x,y);
	}
	
	// Will always return first point added to the list
	public Point getClosestPoint() {
		if (this.vectors.size() == 0) {
			return null;
		}
		return this.vectors.get(0);
	}
	
	// Will return all points averaged from the closest scan layer
	public Point getClosestPointsAverage() {
		int listsize = this.vectors.size();
		double layer = 0;
		int count = 0;
		int x = 0;
		int y = 0;
		
		// devide by zero safety nets
		if (listsize != 0) {
			// Get first layer
			layer = this.borderingbiomes.get(0).getSecond();
			
			for (int read = 0; read < listsize; read++)
			{
				// Optimiser
				if (this.borderingbiomes.get(read).getSecond() != layer) {
					break;
				}
				
				// Add to internal point
				x += this.vectors.get(read).x;
				y += this.vectors.get(read).y;
				count++;
			}
			
			// devide by zero safety nets
			if (count != 0) {
				if (x != 0)
				{
					x = x / count;
				}
			
				if (y != 0)
				{
					y = y / count;
				}
			}
		}
		
		return new Point(x,y);
	}
	
	// Will return outermost scan layer
		public double getFarthestLayer() {
			if (this.vectors.size() != 0) {
				// Get first layer and send
				return this.borderingbiomes.get(this.vectors.size()-1).getSecond();
			}
			return 0;
		}
	
	// Will return closest scan layer
	public double getClosestLayer() {
		if (this.vectors.size() != 0) {
			// Get first layer and send
			return this.borderingbiomes.get(0).getSecond();
		}
		return 0;
	}
}


