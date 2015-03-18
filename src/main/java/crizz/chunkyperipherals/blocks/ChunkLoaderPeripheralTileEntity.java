package crizz.chunkyperipherals.blocks;
//p=peripheral.wrap("top")
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import crizz.chunkyperipherals.ChunkLoadingCallback;
import crizz.chunkyperipherals.ChunkyPeripherals;
import crizz.chunkyperipherals.utils.Coord2D;
import crizz.chunkyperipherals.utils.RectangleInt;
import crizz.chunkyperipherals.utils.TicketManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class ChunkLoaderPeripheralTileEntity extends TileEntity implements IPeripheral
{
	private static interface IChunksShape
	{
		public List<ChunkCoordIntPair> getChunksToLoad(int xCoord, int zCoord);
		public Object[] getDescription();
		public boolean isLoadingChunks();
		public void writeToNBT(NBTTagCompound var1);
	}
	
	private static class ChunksShapeCircle implements IChunksShape
	{
		int radius;
		
		public static byte id()
		{
			return 1;
		}
		@Override
		public boolean isLoadingChunks()
		{
			return radius>0;
		}
		
		@Override
		public Object[] getDescription()
		{
			return new Object[]{"Circle ",Integer.valueOf(radius)};
		}
		public ChunksShapeCircle()
		{
			radius = 1;
		}
		public ChunksShapeCircle(int radius)
		{
			this.radius = radius;
		}
		
		public ChunksShapeCircle(NBTTagCompound nbt)
		{
			this.radius = nbt.getInteger("ChunksCircleRadius");
		}
		@Override
		public List<ChunkCoordIntPair> getChunksToLoad(int xCoord, int zCoord)
		{
			//ChunkyPeripherals.infoLog("getChunksToLoad("+xCoord+","+zCoord+")");
			List<ChunkCoordIntPair> ret = new ArrayList<ChunkCoordIntPair>(Double.valueOf(radius*radius/256+1).intValue());
			if(radius==0)
				return ret;
			int radiusSquared = radius*radius;
			//ChunkyPeripherals.infoLog("radiusSqrd = "+radiusSquared);
			//coordinate di partenza per scorrere i chunk
			int roundedUpRadius =((radius/16+1)*16);
			
			int cx_i = Coord2D.getChunkFromBlock_single(xCoord - roundedUpRadius)*16;
			int cz_i = Coord2D.getChunkFromBlock_single(zCoord - roundedUpRadius)*16;
			
			int cx_f = Coord2D.getChunkFromBlock_single(xCoord + roundedUpRadius)*16;
			int cz_f = Coord2D.getChunkFromBlock_single(zCoord + roundedUpRadius)*16;
			
			//ChunkyPeripherals.infoLog("cx_i="+cx_i+"  cz_i="+cz_i+" cx_f="+cx_f+"  cz_f="+cz_f);
			
			
			for(int x=cx_i;x<=cx_f;x+=16)
			{
				for(int z=cz_i;z<=cz_f;z+=16)
				{
					//ChunkyPeripherals.infoLog("x = "+x+"  z = "+z);
					//if one of the chunks vertexes is inside the circle
					if(	circleTouchesChunk(x,z,xCoord,zCoord,radiusSquared))
					{
						ret.add(new ChunkCoordIntPair(x/16,z/16));
						//ChunkyPeripherals.infoLog("adding");
					}
				}
			}
			
			return ret;
		}

		private boolean circleTouchesChunk(int chunkX, int chunkZ, int xCenter, int zCenter, int radiusSquared)
		{
			return(		Coord2D.distanceSquared(chunkX,		chunkZ,   	xCenter,	zCenter)	<= radiusSquared    ||//vertex inside circle
						Coord2D.distanceSquared(chunkX+16,	chunkZ,   	xCenter,	zCenter)	<= radiusSquared	||//vertex inside circle
						Coord2D.distanceSquared(chunkX+16,	chunkZ+16,	xCenter,	zCenter)	<= radiusSquared	||//vertex inside circle
						Coord2D.distanceSquared(chunkX,		chunkZ+16,	xCenter,	zCenter)	<= radiusSquared	||//vertex inside circle
						RectangleInt.isPointInRectangle(xCenter,zCenter,chunkX,chunkZ+15,chunkX+15,chunkZ)			||//circle center inside square
						(chunkX<=xCenter && xCenter<=chunkX+15 && (Math.abs(chunkZ-zCenter)<=radius || Math.abs(chunkZ+16-zCenter)<=radius)) 	||//side intersects circle
						(chunkZ<=zCenter && zCenter<=chunkZ+15 && (Math.abs(chunkX-xCenter)<=radius || Math.abs(chunkX+16-xCenter)<=radius)));//side intersects circle
				
		}
		
		@Override
		public void writeToNBT(NBTTagCompound var1)
		{
			var1.setInteger("ChunksCircleRadius", radius);
		}
	}
	
	private static class ChunksShapeRectangle implements IChunksShape
	{
		//these variables define the distances of the sides from the chunkloader 
		private int west;
		private int north;
		private int east;
		private int south;
		
		private static final String NBT_TAG_WEST = "ChunksShapeRectangle_west";
		private static final String NBT_TAG_NORTH = "ChunksShapeRectangle_north";
		private static final String NBT_TAG_EAST = "ChunksShapeRectangle_east";
		private static final String NBT_TAG_SOUTH = "ChunksShapeRectangle_south";
		
		
		public static byte id()
		{
			return 2;
		}
		
		/**
		 * creates a square centered on the chunkloader
		 * @param sideLength
		 */
		public ChunksShapeRectangle(int sideLength)
		{
			west	=sideLength/2;
			north	=sideLength/2;
			south		=sideLength/2+ sideLength % 2;
			east	=sideLength/2+ sideLength % 2;
		}
		
		/**
		 * creates a rectangle centered on the chunkloader
		 * @param sideXLength length of the sides parallel to the x axis
		 * @param sideYLength length of the sides parallel to the y axis
		 */
		public ChunksShapeRectangle(int sideZLength, int sideXLength)
		{
			west	=sideXLength/2;
			south	=sideZLength/2+ sideZLength % 2;
			north	=sideZLength/2;
			east	=sideXLength/2+ sideXLength % 2;
		}

		
		/**
		 * creates a rectangle
		 * @param west distance of the west side from the chunkloader
		 * @param north distance of the north side from the chunkloader
		 * @param east distance of the east side from the chunkloader
		 * @param south distance of the south side from the chunkloader
		 */
		public ChunksShapeRectangle(int north, int east, int south, int west)
		{
			this.west = west;
			this.north = north;
			this.east = east;
			this.south = south;
		}
		
		public ChunksShapeRectangle(NBTTagCompound nbt)
		{
			west	= nbt.getInteger(NBT_TAG_WEST);
			north		= nbt.getInteger(NBT_TAG_NORTH);
			east	= nbt.getInteger(NBT_TAG_EAST);
			south	= nbt.getInteger(NBT_TAG_SOUTH);
		}
		
		@Override
		public List<ChunkCoordIntPair> getChunksToLoad(int xCoord, int zCoord)
		{
		//	ChunkyPeripherals.infoLog("getChunksToLoad("+xCoord+","+zCoord+")");
			List<ChunkCoordIntPair> ret = new ArrayList<ChunkCoordIntPair>(   ((int)(west+east)/16+1)*((int)(south+north)/16+1)   );
		//	ChunkyPeripherals.infoLog("  north="+north+"  east="+east+"  south="+south+"west="+west);
			
			
			//bottom left vertex
			int z0 = Coord2D.getChunkFromBlock_single(zCoord-(int)(north+1))*16;
			int x0 = Coord2D.getChunkFromBlock_single(xCoord-(int)(west+1))*16;
			//top right vertex
			int zf = Coord2D.getChunkFromBlock_single(zCoord+(int)(south+1))*16+16;
			int xf = Coord2D.getChunkFromBlock_single(xCoord+(int)(east+1))*16+16;
			
			ChunkyPeripherals.infoLog("x0="+x0+"  z0="+z0+"  xf="+xf+"  zf="+zf);

			RectangleInt rect = new RectangleInt(zCoord - (int)(north+1), (int) (xCoord + (east+1)), (int) (zCoord + (south+1)), (int) (xCoord - (west+1)));
			ChunkyPeripherals.infoLog(rect.toString());
			for(int x = x0; x<=xf; x+=16)
			{
				for(int z = z0; z<=zf; z+=16)
				{
					ChunkyPeripherals.infoLog("x="+x+"  z="+z);
					if(rect.intersectsRect(z, x+15, z+15, x))
					{
						ChunkyPeripherals.infoLog("adding");
						ret.add(new ChunkCoordIntPair(x/16,z/16));
					}
					else
					{
						ChunkyPeripherals.infoLog("not adding");
					}
				}
			}
			
			return ret;
		}

		@Override
		public Object[] getDescription()
		{
			return new Object[]{"rectangle","north = "+north,"east  = "+east,"south = "+south,"west  = "+west};
		}

		@Override
		public boolean isLoadingChunks()
		{
			return (west>0 || east>0) && (north>0 || south>0);
		}

		@Override
		public void writeToNBT(NBTTagCompound var1)
		{
			var1.setInteger(NBT_TAG_WEST, west);
			var1.setInteger(NBT_TAG_NORTH, north);
			var1.setInteger(NBT_TAG_EAST, east);
			var1.setInteger(NBT_TAG_SOUTH, south);
		}
	}
	
	
	public static final String internalName = ChunkyPeripherals.MODID+"_ChunkLoaderPeripheralTileEntity";
	private static final String[] methodNames = new String[]{
		"getShape",//0
		"setShapeCircle",//1
		"getChunksNumber",//2
		"getChunksList",//3
		"disable",//4
		"help",//5
		"setShapeSquare",
		"setShapeRectangle"
	};
	
	
	

	public static final String HELP =	"This peripherals keeps loaded the blocks within the bounds of the currently defined shape.\n" +
										"use method \"help(\"command_name\", arg1type, arg2type, ...)\" to get the documentation for a command";
	
	public static final String HELP_GETSHAPE = 
	"getShape()\n" +
	"    returns a string descripting the current shape.\n";
	public static final String HELP_SETSHAPECIRCLE = 
	"setShapeCircle(int radius)\n" +
	"    sets the shape to a circle of the defined \n" +
	"    radius centered on the peripheral\n" +
	"    Arguments:\n" +
	"      radius: radius of the circle, has to be \n" +
	"      positive. Non-integer values will be rounded\n" +
	"      up to the next integer";
	public static final String HELP_SETSHAPESQUARE =
	"setShapeSquare(int sideLength)\n" +
	"    sets the shape to a square of the defined size\n" +
	"    centered on the peripheral.\n" +
	"    Arguments:\n" +
	"      sideLength: length of the side of the square.\n" +
	"      has to be positive. Non-integer values will \n" +
	"      be rounded up to the next integer. If odd\n" +
	"      will be rounded up to the next even number.";
	public static final String HELP_SETSHAPERECTANGLE =
	"three possible methods:\n" +
	"setShapeRectangle(int sideLength)\n" +
	"setShapeRectangle(int sideXLength, int sideZLength)\n" +
	"setShapeRectangle(int north, int east,int south,\n" +
	"                   int west)\n";
	public static final String HELP_SETSHAPERECTANGLE1 =
	"setShapeRectangle(int sideLength)\n" +
	"    equivalent to setShapeSquare(int sideLength)";
	public static final String HELP_SETSHAPERECTANGLE2 =
	"setShapeRectangle(int sideXLength, int sideZLength)\n" +
	"    sets the shape to a rectangle of the defined\n" +
	"    size centered on the peripheral. All arguments\n" +
	"    have to be positive. Non-integer values will\n" +
	"    be rounded up to the next integer. If odd will\n" +
	"    be rounded up to the next even number\n" +
	"    Arguments:\n" +
	"      sideXLength: length of the side aligned to\n" +
	"        the X axis (west-east).\n" +
	"      sideYLength: length of the side aligned to\n" +
	"        the Z axis (north-south).\n";
	public static final String HELP_SETSHAPERECTANGLE4 =
	"setShapeRectangle(int north, int east, int south, int west)\n" +
	"    sets the shape to a rectangle of the defined\n" +
	"    size. The parameters define the distances of\n" +
	"    the sides from the peripheral. All arguments\n" +
	"    have to be positive. Non-integer values will\n" +
	"    be rounded up to the next integer.\n" +
	"    - north: direction of the decreasing z\n" +
	"    - east: direction of the increasing x\n" +
	"    - south: direction of the increasing z\n" +
	"    - west: direction of the decreasing x\n" +
	"    Also, the south direction is indicated by the\n" +
	"    face with the red thing" +
	"    Arguments:\n" +
	"      north:    distance of the north side.\n"+
	"      east:  distance of the east side.\n"+
	"      south: distance of the south side.\n"+
	"      west:   distance of the west side.\n";
	public static final String HELP_GETCHUNKSNUMBER =
	"getChunksNumber()\n"+
	"    returns the number of chunks the peripheral is\n" +
	"    keeping loaded.";
	public static final String HELP_GETCHUNKSLIST = 
	"getChunksList()\n" +
	"    returns a list of the chunks the peripheral is\n" +
	"    keeping loaded.";
	public static final String HELP_DISABLE =
	"disable()\n" +
	"    disables the peripheral, it wont keep loaded\n" +
	"    any chunk. To re-enable it you have to set a\n" +
	"    new shape";
	public static final String HELP_HELP =
	"help(command_name, arg1type, arg2type, ...)\n" +
	"    returns the documentation of the methods.\n" +
	"    Arguments:" +
	"      command_name: string representing the name\n" +
	"       of the command." +
	"      argNtype: string representing the type of\n" +
	"       the argument N.";
	
	
	
	
	private Ticket ticket;

	private IChunksShape shape = new ChunksShapeCircle(1);
	
	@Override
	public String getType()
	{
		return "chunky peripheral";
	}

	@Override
	public String[] getMethodNames()
	{
		String[] r = new String[methodNames.length]; 
		System.arraycopy(methodNames, 0, r, 0, r.length);
		return r;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,int method, Object[] arguments) throws LuaException, InterruptedException
	{
		if(method==0)
		{
			return shape==null? null : shape.getDescription();
		}
		else if(method==1)//setShapeCircle
		{
			if(arguments.length!=1)
				throw new LuaException("Error: wrong arguments number (should be one)");
			if(!(arguments[0] instanceof Double))
				throw new LuaException("Error: argument should be a number, not a "+arguments[1].getClass().getSimpleName());
			if(((Double) arguments[0])<0)
				throw new LuaException("Error: radius must be positive");

			int radius = ((Double) arguments[0]).intValue();
			if(((Double)arguments[0])-radius>0)//round up
				radius++;
			
			if(radius>ChunkyPeripherals.maxChunkLoadingRadius)
				throw new LuaException("Error: radius too big, max="+ChunkyPeripherals.maxChunkLoadingRadius);
			
			shape = new ChunksShapeCircle(radius);
			loadChunks();
		}
		else if(method==6)//setShapeSquare
		{
			if(arguments.length!=1)
				throw new LuaException("Error: wrong arguments number (should be one)");
			if(!(arguments[0] instanceof Double))
				throw new LuaException("Error: argument should be a number, not a "+arguments[1].getClass().getSimpleName());
			if(((Double) arguments[0])<0)
				throw new LuaException("Error: side length must be positive");
			
			int sideLength = ((Double)arguments[0]).intValue();
			if(((Double)arguments[0])-sideLength>0)//round up
				sideLength++;
			if(sideLength%2==1)
				sideLength++;
			
			if(sideLength/2>ChunkyPeripherals.maxChunkLoadingRadius)
				throw new LuaException("Error: side too big, max="+ChunkyPeripherals.maxChunkLoadingRadius*2);
			
			shape = new ChunksShapeRectangle(sideLength);
			loadChunks();
		}
		else if(method==7)//setShapeRectangle
		{
			if(arguments.length==1)//square
			{
				if(!(arguments[0] instanceof Double))
					throw new LuaException("Error: argument should be a number, not a "+arguments[1].getClass().getSimpleName());
				if(((Double) arguments[0])<0)
					throw new LuaException("Error: side length must be positive");
				int sideLength = ((Double)arguments[0]).intValue();
				if(((Double)arguments[0])-sideLength>0)//round up
					sideLength++;
				if(sideLength%2==1)
					sideLength++;
				
				if(sideLength/2>ChunkyPeripherals.maxChunkLoadingRadius)
					throw new LuaException("Error: side too big, max="+ChunkyPeripherals.maxChunkLoadingRadius*2);
				
				shape = new ChunksShapeRectangle(sideLength);
				loadChunks();
			}
			else if(arguments.length==2)//centered rectangle
			{
				if(!(arguments[0] instanceof Double && arguments[1] instanceof Double))
					throw new LuaException("Error: first and second arguments should be numbers");
				if(((Double)arguments[0])<0 || ((Double)arguments[1])<0)
					throw new LuaException("Error: side lengths must be positive");

				int sideXLength = ((Double)arguments[0]).intValue();
				if(((Double)arguments[0])-sideXLength>0)//round up
					sideXLength++;
				int sideYLength = ((Double)arguments[1]).intValue();
				if(((Double)arguments[1])-sideYLength>0)//round up
					sideYLength++;

				if(sideXLength%2==1)
					sideXLength++;
				if(sideYLength%2==1)
					sideYLength++;
				
				int maxChunkLoadingArea = ChunkyPeripherals.maxChunkLoadingRadius*ChunkyPeripherals.maxChunkLoadingRadius*4;
				if(sideXLength*sideYLength>maxChunkLoadingArea)
					throw new LuaException("Error: area too big, max="+maxChunkLoadingArea);
				
				shape = new ChunksShapeRectangle(sideXLength, sideYLength);
				loadChunks();
			}
			else if(arguments.length==4)//generic rectangle
			{
				if(!(arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double))
					throw new LuaException("Error: arguments should be numbers");
				if(((Double)arguments[0])<0 || ((Double)arguments[1])<0 || ((Double)arguments[2])<0 || ((Double)arguments[3])<0)
					throw new LuaException("Error: arguments must be positive");
				
				int north		= ((Double)arguments[0]).intValue();
				if(((Double)arguments[0])-north>0)//round up
					north++;
				int east		= ((Double)arguments[1]).intValue();
				if(((Double)arguments[1])-east>0)//round up
					east++;
				int south 	= ((Double)arguments[2]).intValue();
				if(((Double)arguments[2])-south>0)//round up
					south++;
				int west 	= ((Double)arguments[3]).intValue();
				if(((Double)arguments[3])-west>0)//round up
					west++;
				
				int maxChunkLoadingArea = ChunkyPeripherals.maxChunkLoadingRadius*ChunkyPeripherals.maxChunkLoadingRadius*4;
				if((south+north)*(east+west)>maxChunkLoadingArea)
					throw new LuaException("Error: area too big, max="+maxChunkLoadingArea);
				
				shape = new ChunksShapeRectangle(north, east, south, west);
				loadChunks();
			}
			else
			{
				throw new LuaException("Error: method setShapeRectangle with "+arguments.length+"undefined");
			}
		}
		else if(method==2)//getChunksNumber
		{
			return new Object[]{Integer.valueOf(ticket.getChunkList().size())};
		}
		else if(method==3)//getChunksList
		{
			ImmutableSet<ChunkCoordIntPair> s = ticket.getChunkList();
			Integer[] r = new Integer[s.size()*2];
			int i=0;
			int centerX = xCoord/16;
			int centerY = yCoord/16;
			for(ChunkCoordIntPair c : s)
			{
				r[i++]=c.chunkXPos-centerX;
				r[i++]=c.chunkZPos-centerY;
			}
			return r;
		}
		else if(method==4)//disable
		{
			unloadChunks(ticket.getChunkList().asList());
			shape = null;
		}
		else if(method==5)//help
		{
			if(arguments.length<1)
				throw new LuaException("Error: must have at least one argument (the method name)");
			if(!(arguments[0] instanceof String))
				throw new LuaException("Error: first argument has to be a string (the method name)");
	
			
	
			String method_name = (String)arguments[0];
			String doc = "unknown method";
			if(method_name.compareToIgnoreCase("getShape")==0)
			{
				doc = HELP_GETSHAPE;
			}
			else if(method_name.compareTo("setShapeCircle")==0)
			{
				doc = HELP_SETSHAPECIRCLE;
			}
			else if(method_name.compareTo("setShapeSquare")==0)
			{
				doc = HELP_SETSHAPESQUARE;
			}
			else if(method_name.compareTo("setShapeRectangle")==0)
			{
				if(arguments.length-1==1)
					doc = HELP_SETSHAPERECTANGLE1;
				else if(arguments.length-1==2)
					doc = HELP_SETSHAPERECTANGLE2;
				else if(arguments.length-1==4)
					doc = HELP_SETSHAPERECTANGLE4;
				else if(arguments.length-1==0)
					doc = HELP_SETSHAPERECTANGLE;
			}
			else if(method_name.compareTo("getChunksNumber")==0)
			{
				doc = HELP_GETCHUNKSNUMBER;
			}
			else if(method_name.compareTo("getChunksList")==0)
			{
				doc = HELP_GETCHUNKSLIST;
			}
			else if(method_name.compareTo("disable")==0)
			{
				doc = HELP_DISABLE;
			}
			else if(method_name.compareTo("help")==0)
			{
				doc = HELP_HELP;
			}
			return new Object[]{doc};
		}
		return null;
	}

	@Override
	public void attach(IComputerAccess computer)
	{
	//	ChunkyPeripherals.infoLog("chunky peripheral attached at "+ xCoord+","+ yCoord+","+ zCoord);
	}

	@Override
	public void detach(IComputerAccess computer)
	{
	//	ChunkyPeripherals.infoLog("chunky peripheral detached at "+ xCoord+","+ yCoord+","+ zCoord);
	}

	@Override
	public boolean equals(IPeripheral other)
	{
		return false;
	}

	
	private void loadChunks(List<ChunkCoordIntPair> chunks)
	{
	//	ChunkyPeripherals.infoLog("loadChunks, radius="+radiusInChunks+"  mode="+mode);
		if(ticket==null)
		{
		/*	Ticket t = ChunkLoadingCallback.starterTicketsList.remove(this);//prova a cercarlo nella lista
			if(t==null)//se non era nella lista
			{*/
				ChunkyPeripherals.infoLog("creating new ticket at " + xCoord + ", "+ yCoord + ", "+ zCoord+", "+getWorldObj());
				ticket = TicketManager.getNewTicket(getWorldObj());
				if(ticket.isPlayerTicket())
					ChunkyPeripherals.infoLog("the ticket is a player ticket: player="+ticket.getPlayerName());
				ticket.getModData().setInteger("posX", xCoord);
				ticket.getModData().setInteger("posY", yCoord);
				ticket.getModData().setInteger("posZ", zCoord);
		//	}
		}
		for(ChunkCoordIntPair chunk : chunks)
		{
			ChunkyPeripherals.infoLog("forcing chunk "+chunk);
			ForgeChunkManager.forceChunk(ticket, chunk);
		}
	}
	
	/**
	 * loads chunks according to the current shape 
	 */
	private void loadChunks()
	{
		List<ChunkCoordIntPair> chunksToLoad = shape.getChunksToLoad(xCoord, zCoord);
		ImmutableSet<ChunkCoordIntPair> chunksAlreadyloaded = ticket.getChunkList();
		List<ChunkCoordIntPair> chunksToUnload = (new ArrayList(chunksAlreadyloaded));
		chunksToUnload.removeAll(chunksToLoad);
				
		chunksToLoad.removeAll(ticket.getChunkList());//toglie i chunk che sono già caricati
		unloadChunks(chunksToUnload);
		loadChunks(chunksToLoad);
	}
	
	private void unloadChunks(List<ChunkCoordIntPair> chunks)
	{
		if(ticket!=null)
		{
			for(ChunkCoordIntPair chunk : chunks)
			{
				ChunkyPeripherals.infoLog("unforcing chunk "+chunk);
				ForgeChunkManager.unforceChunk(ticket, chunk);
			}
		}
	}
	
	private List<ChunkCoordIntPair> getChunksToLoad()
	{
		return shape.getChunksToLoad(xCoord, zCoord);
	}
	
	public boolean isActive()
	{
		return shape!=null && shape.isLoadingChunks();
	}
	

	
	public void callback(Ticket t)
	{
		ticket=t;
		loadChunks(getChunksToLoad());
	}
	
	
	
	public void writeToNBT(NBTTagCompound var1)
	{
		if(shape instanceof ChunksShapeCircle)
		{
			var1.setByte("ChunksShapeName", ChunksShapeCircle.id());
		}
		else if(shape instanceof ChunksShapeRectangle)
		{
			var1.setByte("ChunksShapeName", ChunksShapeRectangle.id());
		}
		else if(shape==null)
		{
			var1.setByte("ChunksShapeName", (byte) 0);
		}
		if(shape!=null)
			shape.writeToNBT(var1);
		super.writeToNBT(var1);
	}


	public void readFromNBT(NBTTagCompound var1)
	{
		int shapeId = var1.getByte("ChunksShapeName");
		//ChunkyPeripherals.infoLog("shapeId="+shapeId);
		if(shapeId==ChunksShapeCircle.id())
		{
			shape = new ChunksShapeCircle(var1);
		}
		else if(shapeId==ChunksShapeRectangle.id())
		{
			shape = new ChunksShapeRectangle(var1);
		}
		else if(shapeId==0)
		{
			shape = null;
		}
		super.readFromNBT(var1);
	}
	
	/**
	 * releases the chunks
	 */
	public void onDestroy()
	{
		TicketManager.deleteTicket(ticket);
	}
	
	/**
	 * loads the chunks
	 */
	public void onPlaced()
	{
		loadChunks(getChunksToLoad());
	}
}
