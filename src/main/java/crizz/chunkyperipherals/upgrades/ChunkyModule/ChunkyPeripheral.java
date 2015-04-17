package crizz.chunkyperipherals.upgrades.ChunkyModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import crizz.chunkyperipherals.ChunkyPeripherals;
import crizz.chunkyperipherals.ChunkLoadingCallback;
import crizz.chunkyperipherals.utils.Coord2D;
import crizz.chunkyperipherals.utils.TicketManager;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;


/*Peripheral used by computercraft
 */
public class ChunkyPeripheral implements IPeripheral
{
	static public final int methodsNumber = 1;
	
	private Ticket ticket;
	protected ITurtleAccess turtle;
	//variables indicating the current position
	private int x;
	private int y;
	private int z;
	private int dir;
	
	//variables reflecting the position values saved in nbt, these are updated in the same moment as nbt ones
	private int oldX;
	private int oldY;
	private int oldZ;
	private int oldDir;
	
	//indicates if the olds are valid
	private boolean initialized;
	private boolean attached;
	
	@Override
	public boolean equals(IPeripheral other)
	{
		if(other instanceof ChunkyPeripheral)
		{
			ChunkyPeripheral p2 = (ChunkyPeripheral)other;
			return p2.turtle.getPosition().equals(turtle.getPosition());
		}
		return false;
	}
	
	public ChunkyPeripheral(ITurtleAccess t)
	{
		turtle = t;
		ticket=null;
		initialized=false;
	}
	
	@Override
	public String getType()
	{
		return "Chunky Module";
	}

	@Override
	public String[] getMethodNames()
	{
		return new String[]{"isChunky"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws InterruptedException, LuaException
	{
		if(method==0)
		{
			return new Object[]{true};
		}
		return null;
	}


	@Override
	public void attach(IComputerAccess computer)
	{
		ChunkyPeripherals.infoLog("attaching turtle peripheral at " + turtle.getPosition().posX + ", "+ turtle.getPosition().posY + ", "+ turtle.getPosition().posZ);
		attached=true;
	}

	@Override
	public void detach(IComputerAccess computer)
	{
		ChunkyPeripherals.infoLog("detaching turtle peripheral at " + turtle.getPosition().posX + ", "+ turtle.getPosition().posY + ", "+ turtle.getPosition().posZ);
		
		if(ticket!=null)
		{
			//StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			if(!ChunkyPeripherals.isServerStopping)
			{
				if(ticket.world==null)//game closing
					return;
				unLoadChunks();
				TicketManager.deleteTicket(ticket);
				ticket=null;
				attached=false;
			}
		}
		
	}


	public void update()
	{

		if(attached)
		{
			if(ticket==null)
			{
				ticket = ChunkLoadingCallback.starterTicketsList.remove(turtle);
				if(ticket==null)
				{
					ChunkyPeripherals.infoLog("creating new ticket at " + turtle.getPosition().posX + ", "+ turtle.getPosition().posY + ", "+ turtle.getPosition().posZ);
					ticket = TicketManager.getNewTicket(turtle.getWorld());
					if(ticket.isPlayerTicket())
						ChunkyPeripherals.infoLog("the ticket is a player ticket: player="+ticket.getPlayerName());
				}
				else
				{
					ChunkyPeripherals.infoLog("ticket found in startersTicketList!");
					TicketManager.add(ticket);
				}
				getPos();			
				loadChunks();//the ticket is new, it can't have old chunks loaded
				savePos();
				initialized=true;
			}
			else
			{
				getPos();
				updateLoadedChunks();
				savePos();
			}
		}
	}

	
	
	
	public void callback(Ticket t,int x, int y, int z)
	{
		ChunkyPeripherals.infoLog("loading turtle at "+x+";"+y+";"+z);
		ticket=t;
		getOlds();
		initialized=true;
		update();
	}
	
	private List<ChunkCoordIntPair> listChunksToLoad(int radius, int lx, int lz, int direction)
	{
		if(radius==0 || radius==1)
		{
			int Xchunk = lx>>4;
			int Zchunk = lz>>4;
			List<ChunkCoordIntPair> chunkList = new ArrayList<ChunkCoordIntPair>(3);
			chunkList.add(new ChunkCoordIntPair(Xchunk, Zchunk));
			chunkList.add(Coord2D.getFrontCoord(direction, Xchunk, Zchunk).toChunkCoord());
			chunkList.add(Coord2D.getBackCoord(direction, Xchunk, Zchunk).toChunkCoord());
			
			return chunkList;
		}
		else
		{	
			throw new IllegalArgumentException();		
		}
	}
	//loads the chunk in which the turtle is and the chunk in front of it.
	private List<ChunkCoordIntPair> loadChunks()
	{
	
		List<ChunkCoordIntPair> chunkList = listChunksToLoad(0,x,z,dir);
		
		Iterator<ChunkCoordIntPair> iter = chunkList.iterator();
		while(iter.hasNext())
			ForgeChunkManager.forceChunk(ticket,iter.next());
		return chunkList;
	}
	
	//loads the chunk needed by the turtle and unloads the ones it doesn't need anymore
	private void updateLoadedChunks()
	{		
		//if the turtle has moved from the chunk or has turned
		int Xchunk = x>>4;
		int Zchunk = z>>4;

		if(	Xchunk != oldX>>4	||	Zchunk != oldZ>>4	||	dir != oldDir || !initialized)
		{
			ChunkyPeripherals.infoLog("loading chunks for turtle at "+x+","+y+","+z);
			List<ChunkCoordIntPair> loadedChunks = loadChunks();//loads the new chunks
			Iterator<ChunkCoordIntPair> iter = loadedChunks.iterator();
			if(ChunkyPeripherals.activateInfoLogging)
			{
				while(iter.hasNext())
				{
					ChunkCoordIntPair c = iter.next();
					ChunkyPeripherals.infoLog("loaded " + c.chunkXPos + " ; " + c.chunkZPos);
				}
			}
			if(initialized)
			{
				//calculate the chunks that were already loaded
				List<ChunkCoordIntPair> oldChunkList = listChunksToLoad(0,oldX,oldZ,oldDir);
				ChunkyPeripherals.infoLog("oldX= "+ oldX +" ,oldZ= "+ oldZ +",oldDir== "+ oldDir);
				oldChunkList.removeAll(loadedChunks);//remove from the list the ones that still need to be loaded
				
				//unload the ones still in the list
				Iterator<ChunkCoordIntPair> iter2 = oldChunkList.iterator();
				while(iter2.hasNext())
				{
					ChunkCoordIntPair c = iter2.next();
					ForgeChunkManager.unforceChunk(ticket,c);
					ChunkyPeripherals.infoLog("unloading " + c.chunkXPos + " ; " + c.chunkZPos);
				}
				
			}
		}
	}
	/*
	 * unloads the chunks currently loaded
	 */
	private void unLoadChunks()
	{
		if(ticket!=null)
		{
			List<ChunkCoordIntPair> chunkList = listChunksToLoad(0,x,z,dir);
			
			Iterator<ChunkCoordIntPair> iter = chunkList.iterator();
			while(iter.hasNext())
				ForgeChunkManager.unforceChunk(ticket,iter.next());
		}
	}
	
	/**
	 * Saves the position in the object's variables
	 */
	private void getPos()
	{
		ChunkCoordinates pos=turtle.getPosition();
		x=((int)pos.posX);
		y=((int)pos.posY);
		z=((int)pos.posZ);
		
		dir = turtle.getDirection();
	}
	
	/**
	 * Saves the object position variables in nbt and in the old- variables
	 */
	private void savePos()
	{
		if(oldX!=x)
		{
			ticket.getModData().setInteger("turtleX",x);
			oldX=x;
		}
		if(oldY!=y)
		{
			ticket.getModData().setInteger("turtleY",y);
			oldY=y;
		}
		if(oldZ!=z)
		{
			ticket.getModData().setInteger("turtleZ",z);
			oldZ=z;
		}
		
		
		int ndir = turtle.getDirection();
		if(oldDir!=dir)
		{
			ticket.getModData().setInteger("direction",dir);
			oldDir=dir;
		}
	}
	
	private void getOlds()
	{
		oldX = ticket.getModData().getInteger("turtleX");
		oldY = ticket.getModData().getInteger("turtleY");
		oldZ = ticket.getModData().getInteger("turtleZ");
		oldDir = ticket.getModData().getInteger("direction");
	}
}
