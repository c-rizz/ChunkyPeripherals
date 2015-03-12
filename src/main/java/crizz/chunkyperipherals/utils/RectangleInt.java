package crizz.chunkyperipherals.utils;

public class RectangleInt
{
	int left,top,right,bottom;
	
	public RectangleInt(int left,int top,int right,int bottom)
	{
		this.left	= left;
		this.top	= top;
		this.right	= right;
		this.bottom	= bottom;
	}
	
	public boolean containsOrTouches(int x, int y)
	{
		return left<=x && x<=right && bottom<=y && y<=top;
	}
	
	public static boolean containsOrTouches(int x, int y, int left, int top, int right, int bottom)
	{
		return left<=x && x<=right && bottom<=y && y<=top;
	}
	
	public boolean contains(int x, int y)
	{
		return left<x && x<right && bottom<y && y<top;
	}
	
	public boolean intersectsRect(int left, int top, int right, int bottom)
	{
		return left<this.right && right > this.left && top>this.bottom && bottom<this.top; 
	}
	
	public static boolean isPointInRectangle(int x, int y, int left, int top, int right, int bottom)
	{
		return left<=x && x<=right && bottom<=y && y<=top;
	}
	
	public String toString()
	{
		return "RectInt: "+left+" "+top+" "+right+" "+bottom;
	}
}
