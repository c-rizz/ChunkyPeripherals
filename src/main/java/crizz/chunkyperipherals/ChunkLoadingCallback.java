package crizz.chunkyperipherals;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import crizz.chunkyperipherals.blocks.ChunkLoaderPeripheralTileEntity;
import crizz.chunkyperipherals.upgrades.ChunkyModule.ChunkyPeripheral;
import crizz.chunkyperipherals.upgrades.ChunkyModule.ChunkyUpgrade;
import crizz.chunkyperipherals.upgrades.MinyChunkyModule.MinyChunkyUpgrade;
import crizz.chunkyperipherals.utils.Coord2D;
import crizz.chunkyperipherals.utils.TicketManager;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;


import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ChunkLoadingCallback implements ForgeChunkManager.LoadingCallback
{
     public static ConcurrentHashMap<Object, Ticket> starterTicketsList;
			//the chunk loading callback loads the needed chunks on these tickets so that when computercraft is properly
			//loaded it will call update on the chunky peripherals in this chunk. Every chunky peripheral will create a 
			//ticket and go on by itself. La lista ha un ticket per ogni world
 	
	
	
	
	
	
	
	public void 	ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	{
		ChunkyPeripherals.infoLog("chunk loading callback (tickets list size="+tickets.size()+")");
		TicketManager.reset();
		
		ListIterator iter = tickets.listIterator();
		
		while(iter.hasNext())
		{
			Ticket ticket = (Ticket)(iter.next());
			if(ticket.getModId().equals("chunkyperipherals"))
			{
				try
				{
					ChunkyPeripherals.infoLog("ticket = "+ticket);
					if(ChunkyTurtleLoadingCallBack(ticket,world))
					{
						TicketManager.add(ticket);
					}
					else if(ChunkloaderPeripheralBlockLoadingCallBack(ticket,world))
					{
						TicketManager.add(ticket);
					}
					else
					{
						ChunkyPeripherals.logger.warn("unhandled ticket. Releasing the ticket");
						TicketManager.deleteTicket(ticket);
					}
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
				catch (InvocationTargetException e)
				{
					e.printStackTrace();
				}
				catch (NoSuchMethodException e)
				{
					e.printStackTrace();
				}
				catch (SecurityException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	//if at the ticket's loactions there is a chunky turtle it loads it
	private boolean ChunkyTurtleLoadingCallBack(Ticket ticket, World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		int x = ticket.getModData().getInteger("turtleX");
		int y = ticket.getModData().getInteger("turtleY");
		int z = ticket.getModData().getInteger("turtleZ");
		int dir = ticket.getModData().getInteger("direction");
		
		ChunkyPeripherals.infoLog("searching chunky module for ticket at "+x+","+y+","+z);
		
		//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::		
		//Find Tile Entity
		//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

		
		
		TileEntity t = world.getTileEntity(x, y, z);
		
		if(t==null)
		{
			ChunkyPeripherals.logger.warn("no tile entity at ("+x+";"+y+";"+z+")");
			return false;
		}
		
		
		
		//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::		
		//Check tile entity is a turtle
		//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

		
		
		ITurtleAccess turtle =  null;
		if(!(t instanceof ITurtleAccess))
		{
			Class found = t.getClass();
			
			if(found.getName().equals("dan200.computercraft.shared.turtle.blocks.TileTurtleExpanded"))
			{				
				Method getAccessMethod = null;
				getAccessMethod = found.getMethod("getAccess");
				
				turtle = (ITurtleAccess)getAccessMethod.invoke(t);
				if(turtle==null)
				{
					ChunkyPeripherals.logger.warn("brain not found ("+x+";"+y+";"+z+")");
					return false;
				}
			}
			else
			{			
				ChunkyPeripherals.logger.warn("tile entity isn't a turtle ("+x+";"+y+";"+z+") but "+ found.getName());
				return false;
			}
		}
		else
		{
			turtle = (ITurtleAccess)t;
		}
		
		
		if(!(turtle instanceof ITurtleAccess))
		{
			ChunkyPeripherals.logger.warn("not a turtle at ("+x+","+y+","+z+") but "+ turtle.getClass().getName());
			return false;
		}		
	
	
		
		//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::		
		//Check that the turtle has the chunky module
		//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

		
		
		
		ITurtleUpgrade peripheral=null;
		
		peripheral = turtle.getUpgrade(TurtleSide.Left);
		if(peripheral==null || (!(peripheral instanceof ChunkyUpgrade) && !(peripheral instanceof MinyChunkyUpgrade)))
		{
			peripheral = turtle.getUpgrade(TurtleSide.Right);
			if(peripheral==null || (!(peripheral instanceof ChunkyUpgrade) && !(peripheral instanceof MinyChunkyUpgrade)))
			{
				ChunkyPeripherals.logger.warn("turtle peripheral not found ("+x+";"+y+";"+z+") ("+peripheral+")");
				return false;
			}
		}

		
		
		//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		//Load the chunks in which the turtle is
		//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		

		
		
		ChunkyPeripherals.infoLog("    loading turtle at "+x+","+y+","+z);
		ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(x>>4, z>>4));
		//load the chunk in which the turtle may go
		ForgeChunkManager.forceChunk(ticket, Coord2D.getFrontCoord(dir,x>>4, z>>4).toChunkCoord());
		ForgeChunkManager.forceChunk(ticket, Coord2D.getBackCoord(dir,x>>4, z>>4).toChunkCoord());
		
		starterTicketsList.put(turtle, ticket);

		
		return true;
	}
	
	private boolean ChunkloaderPeripheralBlockLoadingCallBack(Ticket ticket, World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		int x = ticket.getModData().getInteger("posX");
		int y = ticket.getModData().getInteger("posY");
		int z = ticket.getModData().getInteger("posZ");
		
		ChunkyPeripherals.infoLog("searching chunk loader peripheral block for ticket at "+x+","+y+","+z);
		
		//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::		
		//Find Tile Entity
		//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

		
		
		TileEntity t = world.getTileEntity(x, y, z);
		
		if(t==null)
		{
			ChunkyPeripherals.logger.warn("no tile entity at ("+x+";"+y+";"+z+")");
			return false;
		}
		
		if(t instanceof ChunkLoaderPeripheralTileEntity)
		{
			ChunkyPeripherals.infoLog("    loading chunk loader peripheral at "+x+","+y+","+z);
			((ChunkLoaderPeripheralTileEntity)t).callback(ticket);
			return true;
		}
		else
		{
			return false;
		}
	}
}