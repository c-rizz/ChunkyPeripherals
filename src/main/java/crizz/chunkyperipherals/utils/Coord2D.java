package crizz.chunkyperipherals.utils;

import net.minecraft.util.Facing;
import net.minecraft.world.ChunkCoordIntPair;
import crizz.chunkyperipherals.ChunkyPeripherals;

public class Coord2D
{
	public int x;
	public int z;
	
	public Coord2D(int nx, int nz)
	{
		x=nx;
		z=nz;
	}
	
	public Coord2D getFrontCoord(int dir)
	{
		return new Coord2D(x + Facing.offsetsXForSide[dir],z + Facing.offsetsZForSide[dir]);
	}
	
	
	
	public Coord2D getBackCoord(int dir)
	{
		return new Coord2D(x - Facing.offsetsXForSide[dir],z - Facing.offsetsZForSide[dir]);
	}
	
	static public Coord2D getFrontCoord(int dir, int rx, int rz)
	{
		return new Coord2D(rx,rz).getFrontCoord(dir);
	}
	
	static public Coord2D getBackCoord(int dir, int rx, int rz)
	{
		return new Coord2D(rx,rz).getBackCoord(dir);
	}
	
	public ChunkCoordIntPair toChunkCoord()
	{
		return new ChunkCoordIntPair(x,z);
	}
	
	public boolean equals(Coord2D obj)
	{
		return (x==obj.x && z==obj.z);
	}
	
	public static int distanceSquared(int x1,int y1,int x2,int y2)
	{
		int r = (x1-x2)*(x1-x2) +(y1-y2)*(y1-y2);
		//ChunkyPeripherals.infoLog("distSqrd = "+r);
		return r; 
	}
	
	public static ChunkCoordIntPair getChunkFromBlock(int blockX, int blockZ)
	{
		int cx = blockX>=0? blockX/16 : blockX/16 - 1;
		int cz = blockZ>=0? blockZ/16 : blockZ/16 - 1;
		return new ChunkCoordIntPair(cx,cz);
	}
	
	public static int getChunkFromBlock_single(int coord)
	{
		return coord>=0? coord/16 : coord/16 - 1;
	}

}
