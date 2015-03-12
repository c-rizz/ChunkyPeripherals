package crizz.chunkyperipherals.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import crizz.chunkyperipherals.ChunkyPeripherals;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ChunkLoaderPeripheralBlock extends BlockContainer implements IPeripheralProvider
{
	public final static String unlocalizedName = ChunkyPeripherals.MODID + "_" + "ChunkLoaderPeripheralBlock";
	private static IIcon normalIcon;
	private static IIcon iconZPlus;
	
	public ChunkLoaderPeripheralBlock()
	{
		super(Material.rock);
		setBlockName(unlocalizedName);//unlocalized name
		setCreativeTab(ChunkyPeripherals.getCreativeTab());
		setBlockTextureName(ChunkyPeripherals.MODID + ":" + "ChunkLoaderPeripheralBlock");
		GameRegistry.registerTileEntity(ChunkLoaderPeripheralTileEntity.class, ChunkLoaderPeripheralTileEntity.internalName);
		ComputerCraftAPI.registerPeripheralProvider(this);
	}
	
	public void loadRecipe()
	{
		GameRegistry.addRecipe(new ItemStack(ChunkyPeripherals.chunkLoaderPeripheralBlock), new Object[]
	            {
	            	"SSS",
	            	"SCS",
	            	"SSS",
	            	'S', Blocks.stone,
	            	'C', new ItemStack(ChunkyPeripherals.chunkyModuleItem)
	            });
	}
	
	public void loadAlternativeRecipe()
	{
		
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side)
	{
		IPeripheral r = (IPeripheral) world.getTileEntity(x, y, z);
	//	ChunkyPeripherals.infoLog("chunkyperipheral.getPeripheral("+world+","+x+","+y+","+z+","+side+")="+r);
		return r;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
	//	ChunkyPeripherals.infoLog("ChunkLoaderPeripheralBlock.createNewTileEntity");
		return new ChunkLoaderPeripheralTileEntity();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_)
	{
		if(!world.isRemote)//only on server
		{
		//	ChunkyPeripherals.infoLog("ChunkLoaderPeripheralBlock.breakBlock");
			TileEntity t = world.getTileEntity(x, y, z);
			if(t instanceof ChunkLoaderPeripheralTileEntity)
			{
				((ChunkLoaderPeripheralTileEntity)t).onDestroy();
			}
			else
			{
				ChunkyPeripherals.logger.error("ChunkLoaderPeripheralBlock.onBlockPreDestroy() error");
			}
			world.removeTileEntity(x, y, z);
		}
		else
		{
			super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
		}
	}
	
	@Override
    public void onPostBlockPlaced(World world, int maybex, int maybey, int maybez, int maybeMetadata)
	{
		super.onPostBlockPlaced(world, maybex, maybey, maybez, maybeMetadata);
		if(!world.isRemote)//only on server
		{
		//	ChunkyPeripherals.infoLog("ChunkLoaderPeripheralBlock.onPostBlockPlaced");
			TileEntity t = world.getTileEntity(maybex, maybey, maybez);
			if( t instanceof ChunkLoaderPeripheralTileEntity)
			{
				((ChunkLoaderPeripheralTileEntity)t).onPlaced();
			}
			else
			{
				ChunkyPeripherals.logger.error("ChunkLoaderPeripheralBlock.onPostBlockPlaced() error");
			}
		}
	}

	
	@Override
	public void registerBlockIcons(IIconRegister reg)
	{
		normalIcon = reg.registerIcon(ChunkyPeripherals.MODID + ":" + "ChunkLoaderPeripheralBlock");
	    iconZPlus = reg.registerIcon(ChunkyPeripherals.MODID + ":" + "ChunkLoaderPeripheralBlockZ+");
	}
	
	@Override
	public IIcon getIcon(int side, int meta)
	{
		if(side==3)
			return iconZPlus;
		else
			return normalIcon;
		
	}
}
