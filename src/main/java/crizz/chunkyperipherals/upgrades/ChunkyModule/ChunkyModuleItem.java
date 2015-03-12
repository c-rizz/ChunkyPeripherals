package crizz.chunkyperipherals.upgrades.ChunkyModule;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import crizz.chunkyperipherals.ChunkyPeripherals;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/* Item used to craft  the Chunky turtle
 */
public class ChunkyModuleItem extends Item
{
	public static final String unlocalizedName = "TurtleChunkLoaderItem";
	public ChunkyModuleItem()
	{
		super();
		
		
		setMaxStackSize(64);
		setCreativeTab(ChunkyPeripherals.getCreativeTab());
		setUnlocalizedName(unlocalizedName);
	//	LanguageRegistry.addName(this, "Turtle Chunky Module");
		setTextureName("chunkyperipherals:TurtleChunkLoader");
		GameRegistry.registerItem(this, unlocalizedName);
	}
	
	public void loadRecipe()
	{
		
		GameRegistry.addRecipe(new ItemStack(ChunkyPeripherals.chunkyModuleItem), new Object[]
	            {
	            	"ABA",
	            	"BCB",
	            	"ABA",
	            	'A', new ItemStack(Items.gold_ingot),
	            	'B', Blocks.obsidian,
	            	'C', new ItemStack(Items.ender_pearl)
	            });
	}
	
	public void loadAlternativeRecipe()
	{
		GameRegistry.addRecipe(new ItemStack(ChunkyPeripherals.chunkyModuleItem), new Object[]
	            {
	            	"ABA",
	            	"BCB",
	            	"ABA",
	            	'A', new ItemStack(Items.gold_ingot),
	            	'B', Blocks.obsidian,
	            	'C', new ItemStack(Items.diamond)
	            });
	}
	
	public IIcon getIcon()
	{
		return itemIcon;
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
	{
		par2List.add("Turtle module that keeps the chunk loaded");
	}
}
