package crizz.chunkyperipherals.utils;


import java.util.HashSet;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import crizz.chunkyperipherals.ChunkyPeripherals;

public class TicketManager
{
	public static HashSet<Ticket> myChunkList;
	
	public static void initialize()
	{
		myChunkList = new HashSet();
	}
	
	public static void reset()
	{
		myChunkList.clear();
	}
	
	public static Ticket getNewTicket(World w)
	{
		if(w==null)
			throw new IllegalArgumentException();
		Ticket t= ForgeChunkManager.requestTicket(ChunkyPeripherals.instance, w, ForgeChunkManager.Type.NORMAL);
		myChunkList.add(t);
		return t;
	}
	
	public static void deleteTicket(Ticket t)
	{
		if(myChunkList!=null)
			myChunkList.remove(t);
		ForgeChunkManager.releaseTicket(t);
	}
	
	public static void add(Ticket t)
	{
		ChunkyPeripherals.infoLog("adding to TicketList");
		myChunkList.add(t);
	}
	
	public static void remove(Ticket t)
	{
		myChunkList.remove(t);
	}
	
	public static boolean checkBlockIsKeptLoaded(int x, int z)
	{
		int cx = x>=0? x/16 : x/16 -1;
		int cz = z>=0? z/16 : z/16 -1;
		for(Ticket t : myChunkList)
		{
			for(ChunkCoordIntPair chunk: t.getChunkList())
			{
				if(cx==chunk.chunkXPos && cz==chunk.chunkZPos)
					return true;
			}
		}
		return false;
	}
}
