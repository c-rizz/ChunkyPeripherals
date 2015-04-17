package crizz.chunkyperipherals.upgrades.MinyChunkyModule;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.item.Item;
import crizz.chunkyperipherals.ChunkyPeripherals;
import crizz.chunkyperipherals.upgrades.ChunkyModule.ChunkyPeripheral;
import crizz.chunkyperipherals.utils.Coord2D;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleAnimation;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;

public class MinyChunkyPeripheral extends ChunkyPeripheral implements IPeripheral
{
	static public final int methodsNumber = 6;//4;
	
	public MinyChunkyPeripheral(ITurtleAccess t)
	{
		super(t);
	}

	private Class getTurtleToolClass()
	{
		Class turtleToolClass = null;
		try {
			turtleToolClass = Class.forName("dan200.computercraft.shared.turtle.upgrades.TurtleTool");
		} catch (ClassNotFoundException e) {
			ChunkyPeripherals.logger.error("TurtleTool class not found");
			e.printStackTrace();
		}
		return turtleToolClass;
	}
	private Object getTurtleTool()
	{
		Class turtleToolClass = getTurtleToolClass();
	//	ChunkyPeripherals.infoLog("turtleToolClass = "+turtleToolClass);
		Object turtleTool = null;
		if(turtleToolClass!=null)
		{
			try {
				try {
					turtleTool = turtleToolClass.getConstructor(int.class, String.class, Item.class).newInstance(5,"mining",Items.diamond_pickaxe);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return turtleTool;
	}
	
	
	@Override
	public String getType()
	{
		return "Miny Chunky Module";
	}
	
	@Override
	public String[] getMethodNames() 
	{
		String[] superMethods = super.getMethodNames();
		String[] subMethods = null;
		if(superMethods==null)
			subMethods = new String[methodsNumber];
		else
			subMethods = new String[superMethods.length+methodsNumber];
		
		for(int i=0; i<superMethods.length; i++)
		{
			subMethods[i]=superMethods[i];
		}
		subMethods[superMethods.length]="dig";
		subMethods[superMethods.length+1]="digDown";
		subMethods[superMethods.length+2]="digUp";
		subMethods[superMethods.length+3]="attack";
		subMethods[superMethods.length+4]="attackDown";
		subMethods[superMethods.length+5]="attackUp";
		return subMethods;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,int method, Object[] arguments) throws InterruptedException, LuaException
	{
		super.callMethod(computer, context, method, arguments);
		method -= super.methodsNumber;
		switch (method)
		{
		case 0:
			return turtle.executeCommand(context, new DigCommand(2));//front
		case 1:
			return turtle.executeCommand(context, new DigCommand(0));//down
		case 2:
			return turtle.executeCommand(context, new DigCommand(1));//up
		case 3:
			return turtle.executeCommand(context, new AttackCommand(2));//front
		case 4:
			return turtle.executeCommand(context, new AttackCommand(0));//Down
		case 5:
			return turtle.executeCommand(context, new AttackCommand(1));//Up
		default:
			ChunkyPeripherals.logger.error("called unknown method ("+method+")");
		}
		return null;
	}
	
	private int lastExecution=0;
	static public class DigCommand implements ITurtleCommand
	{
		private int digDir;
		//direction to dig: 0=front, -1=down, 1=up
		public DigCommand(int dir)
		{
			digDir=dir;
		}
		@Override
		public TurtleCommandResult execute(ITurtleAccess turtle)
		{
			//find position and direction of the turtle
			ChunkCoordinates pos=turtle.getPosition();
			int x=((int)pos.posX);
			int y=((int)pos.posY);
			int z=((int)pos.posZ);
			int dir = turtle.getDirection();
			
			//find the position of the block to destroy
			switch(digDir)
			{
			//front
			case 2:
				Coord2D fc = Coord2D.getFrontCoord(dir, x, z);
				x=fc.x;
				z=fc.z;
				break;
			//down
			case 0:
				y--;
				break;
			//up
			case 1:
				y++;
				break;
			default:
				ChunkyPeripherals.logger.error("unknown digging direction");
			}
			
			//get block's info
			final Block block = turtle.getWorld().getBlock(x,y,z);
			int metadata = turtle.getWorld().getBlockMetadata(x,y,z);
			
			//check if the block is breakable
			if(block==Blocks.bedrock || block.getBlockHardness(turtle.getWorld(), x,y,z)<=-1.0F)
				return TurtleCommandResult.failure();
			
			//get items to drop
			ArrayList<ItemStack> items = block.getDrops(turtle.getWorld(), x,y,z, metadata, 0);
			//CRMod.infoLog("Block id= "+id+"   meta= "+metadata);
			
			//play mining sound
			turtle.getWorld().playAuxSFX(2001, x, y, z,/* block.blockID + (metadata << 12)*/Block.getIdFromBlock(block));
			//destroy the block
			turtle.getWorld().setBlockToAir(x, y, z);
			
			
			
			
			if ((items != null) && (items.size() > 0))//se ci sono oggetti da mettere nell'inventario
		    {
				//get the turtle's inventory
				IInventory inventory = turtle.getInventory();
	    		int slotSize = inventory.getInventoryStackLimit();
	    		int slotsNumber = inventory.getSizeInventory();
				//put the dropped item's in the inventory
				for ( ItemStack stack : items)//scorro tutti gli stack che devo raccogliere
				{
					//CRMod.infoLog("itemstack: id="+stack.itemID+"  damage="+stack.getItemDamage());
					for(int currentSlot = turtle.getSelectedSlot();currentSlot<slotsNumber && stack.stackSize>0;currentSlot++)//scorro tutti gli slot finchè non esaurisco lo stack
					{
						turtle.setSelectedSlot(currentSlot);
						ItemStack slotStack = inventory.getStackInSlot(currentSlot);
						
						if(slotStack==null || slotStack.stackSize==0)//se lo slot è vuoto
						{
							int toBeStoredSize = (stack.stackSize>slotSize)? slotSize : stack.stackSize;//number of items to put in the slot
							inventory.setInventorySlotContents((turtle.getSelectedSlot()), stack.splitStack(toBeStoredSize));
						//	stack.stackSize-=toBeStoredSize;
						}
						else if(slotStack.stackSize<64)//se è vuoto ma non del tutto
						{
							if(slotStack.getItem()==stack.getItem())
							{
								int toBeStoredSize = (slotStack.stackSize + stack.stackSize);
								if(toBeStoredSize>64)
									toBeStoredSize=64;
								stack.stackSize-= toBeStoredSize - slotStack.stackSize;
								slotStack.stackSize=toBeStoredSize;
							}
						}    	
					}
					//if it doesn't fit I drop it in the world
					EntityItem itemDropped = new EntityItem(turtle.getWorld(), x, y, z, stack);
					turtle.getWorld().spawnEntityInWorld(itemDropped);
				  }
	        }
			
			//make the turtle wait
			turtle.playAnimation(TurtleAnimation.Wait);
			//done
			return TurtleCommandResult.success();
		}
	}
	
	
	
	public class AttackCommand implements ITurtleCommand
	{
		private int attackDir;
		//direction to dig: 0=front, -1=down, 1=up
		public AttackCommand(int dir)
		{
			if(dir<2)//up or down
				attackDir=dir;
			else//front
			{
				attackDir = turtle.getDirection();
			}
		}
		@Override
		public TurtleCommandResult execute(ITurtleAccess turtle)
		{//per qualche motivo attacca una sola volta lo stesso nemico
			
			ChunkyPeripherals.infoLog("attacking "+attackDir);
			
			Object turtleTool = getTurtleTool();
			Class turtleToolClass = getTurtleToolClass();
			
			if(turtleTool==null)
			{
				ChunkyPeripherals.logger.error("minyChunky peripheral error, turtleTool == null");
				return TurtleCommandResult.failure();
			}
		//	ChunkyPeripherals.infoLog("turtleTool = "+turtleTool);
			try {
				turtleToolClass.getMethod("useTool", ITurtleAccess.class, TurtleSide.class, TurtleVerb.class, int.class).invoke(turtleTool,turtle, TurtleSide.Left, TurtleVerb.Attack, attackDir);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return TurtleCommandResult.success();
		}
	}
	
	
	
	static public class analyze implements ITurtleCommand
	{
		@Override
		public TurtleCommandResult execute(ITurtleAccess turtle)
		{
			
			IInventory inventory = turtle.getInventory();
			
			
			
			
	            for(int i=0;i<inventory.getSizeInventory();i++)//scorro tutti gli slot
	            {
	            	if(inventory.getStackInSlot(i)!=null)
	            		ChunkyPeripherals.infoLog("slot "+i+" id="+inventory.getStackInSlot(i).getItem()+"  damage="+inventory.getStackInSlot(i).getItemDamage());
	            }
	          
			
			return TurtleCommandResult.success();
		}
	}
}
