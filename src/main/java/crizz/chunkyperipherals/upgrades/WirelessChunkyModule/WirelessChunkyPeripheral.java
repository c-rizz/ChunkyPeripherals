package crizz.chunkyperipherals.upgrades.WirelessChunkyModule;

import java.lang.reflect.InvocationTargetException;

import crizz.chunkyperipherals.CCReflectionHelper;
import crizz.chunkyperipherals.ChunkyPeripherals;
import crizz.chunkyperipherals.CCReflectionHelper.CCClassNotFoundException;
import crizz.chunkyperipherals.upgrades.ChunkyModule.ChunkyPeripheral;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;

public class WirelessChunkyPeripheral extends ChunkyPeripheral implements IPeripheral
{
	IPeripheral wirelessModemSubPeripheral;

	static public int methodsNumber;
	
	public WirelessChunkyPeripheral(ITurtleAccess t, TurtleSide side)
	{
		super(t);
		
		Class wirelessModemClass = null;
		ITurtleUpgrade turtleModemObject = (ITurtleUpgrade) CCReflectionHelper.runMainCCClassMethod("getTurtleUpgrade", 1);
		wirelessModemSubPeripheral = turtleModemObject.createPeripheral(t,side);
		
		methodsNumber = wirelessModemSubPeripheral.getMethodNames().length;
		
	}

	public Object getSubPeripheral()
	{
		return wirelessModemSubPeripheral;
	}
	
	@Override
	public String getType()
	{
		return "modem";
	}
	

    public String[] getMethodNames()
    {
    	//return wirelessModemSubPeripheral.getMethodNames();
    	
    	
    	String[] inheritedMethods	= super.getMethodNames();
    	String[] newMethods			= wirelessModemSubPeripheral.getMethodNames();
		String[] methods = null;
		
		int methodsNum = ((newMethods!=null)? newMethods.length : 0) + ((inheritedMethods!=null)? inheritedMethods.length : 0);
		
		methods = new String[methodsNum];
		
		for(int i=0; i<inheritedMethods.length; i++)
		{
			methods[i]=inheritedMethods[i];
		}
		for(int i=inheritedMethods.length; i<methodsNum; i++)
		{
			methods[i]=newMethods[i-inheritedMethods.length];
		}
		return methods;
    }
    
    public Object[] callMethod( IComputerAccess computer, ILuaContext context, int method, Object[] arguments ) throws LuaException, InterruptedException
    {
    	if(method<super.methodsNumber)
    	{
    		return super.callMethod(computer, context, method, arguments);
    	}
    	else
    	{
    		method -= super.methodsNumber;
    		return wirelessModemSubPeripheral.callMethod(computer, context, method, arguments);
    	}
    }

    public void attach( IComputerAccess computer )
    {
    	super.attach(computer);
    	wirelessModemSubPeripheral.attach(computer);
    }

    public void detach( IComputerAccess computer )
    {
    	super.detach(computer);
    	wirelessModemSubPeripheral.detach(computer);
    }

    public boolean equals( IPeripheral other )
    {
    	return wirelessModemSubPeripheral.equals(other);
    }

}
