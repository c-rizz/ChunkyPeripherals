package crizz.chunkyperipherals.utils;

public class Coord3D implements Comparable
{
	int x;
	int y;
	int z;
	
	public int compareTo(Object obj)
	{
		if(!(obj instanceof Coord3D))
			return -1;
		
		Coord3D cObj = (Coord3D) obj;
		
		if(x==cObj.x && y==cObj.y && z==cObj.z)
			return 0;
		
		int diff = x+y+z - (cObj.x + cObj.y +cObj.z);
		if(diff==0)
			return -1;
		return diff;
	}
	
}
