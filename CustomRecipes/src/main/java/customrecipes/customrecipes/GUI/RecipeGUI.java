package customrecipes.customrecipes.GUI;

import customrecipes.customrecipes.CustomConfig.CustomRecipesGUI;
import customrecipes.customrecipes.CustomConfig.RecipeConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static customrecipes.customrecipes.CustomRecipes.*;

public class RecipeGUI
{
    public void createRecipeGui() {
         rGUI = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&',"          &8&lRecipe Crafter"));

        for (int i = 0; i < 45; i++) {
            if (i == 10 || i == 11 || i == 12 || i == 19 || i == 20 || i == 21 || i == 28 || i == 29 || i == 30 || i == 24) {
            } else if (i == 26) {
                ItemStack item2 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta iMeta2 = item2.getItemMeta();
                iMeta2.setDisplayName(ChatColor.GREEN + "Confirm");
                item2.setItemMeta(iMeta2);
                rGUI.setItem(i, item2);
            } else {
                ItemStack item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                ItemMeta iMeta = item.getItemMeta();
                iMeta.removeItemFlags();
                iMeta.setDisplayName("-");
                item.setItemMeta(iMeta);
                rGUI.setItem(i, item);
            }
        }
    }

    public void createShowcaseGui(String name, Player p) {

       Inventory showcaseGUI = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&',"        &8&lRecipe Showcase"));

        for (int i = 0; i < 45; i++) {
            if (i == 10 || i == 11 || i == 12 || i == 19 || i == 20 || i == 21 || i == 28 || i == 29 || i == 30 ) {
                int[] slots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
                int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9};

                for (int j = 0; j < slots.length; j++) {
                    int k = slots[j];
                    int num = nums[j];

                    ItemStack ingredient = RecipeConfig.get().getItemStack(name + "." + num);
                    if (ingredient != null) {
                        showcaseGUI.setItem(k, ingredient);
                    }
                }
            }else if (i == 24) {
                ItemStack ingredient = RecipeConfig.get().getItemStack(name + ".Result");
                if (ingredient != null) {
                    showcaseGUI.setItem(i, ingredient);
                }
            }else if (i == 44) {
                ItemStack item = new ItemStack(Material.BARRIER);
                ItemMeta iMeta = item.getItemMeta();
                iMeta.removeItemFlags();
                iMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lReturn"));
                item.setItemMeta(iMeta);
                showcaseGUI.setItem(i, item);
            }else {
                ItemStack item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                ItemMeta iMeta = item.getItemMeta();
                iMeta.removeItemFlags();
                iMeta.setDisplayName("-");
                item.setItemMeta(iMeta);
                showcaseGUI.setItem(i, item);
            }
        }

        playerShowcaseGUIs.put(p, showcaseGUI);
        p.openInventory(playerShowcaseGUIs.get(p));
    }


    public static void CreateGUI(String worldName,Player p)
    {
        CustomRecipesGUI.reload();

        if (CustomRecipesGUI.get().getConfigurationSection(worldName) == null) {
            p.sendMessage(ChatColor.RED + "This worlds recipes have not been setup");
            return;
        }

        Inventory showcaseGUI = Bukkit.createInventory(null, CustomRecipesGUI.get().getInt(worldName + "." + "guiSize"), ChatColor.translateAlternateColorCodes('&',CustomRecipesGUI.get().getString(worldName + "." + "guiTitle")));
        Material emptySlots = Material.valueOf(CustomRecipesGUI.get().getString(worldName + "." + "emptySlots"));

        for (int i = 0; i < CustomRecipesGUI.get().getInt(worldName + "." + "guiSize"); i++) {
            ItemStack item = new ItemStack(emptySlots);
            if (emptySlots != Material.AIR) {
                ItemMeta iMeta = item.getItemMeta();
                iMeta.removeItemFlags();
                iMeta.setDisplayName("-");
                item.setItemMeta(iMeta);
            }
            showcaseGUI.setItem(i, item);
        }

        if (CustomRecipesGUI.get().getConfigurationSection(worldName + "." + "items") != null) {
            for (String key : CustomRecipesGUI.get().getConfigurationSection(worldName + "." + "items").getKeys(false))
            {
                int slot = Integer.parseInt(key);
                ItemStack itemStack;
                String name = CustomRecipesGUI.get().getString(worldName + "." + "items." + key);

                itemStack = RecipeConfig.get().getItemStack(worldName + "." + name + ".Result").clone();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&eClick to preview")));
                itemStack.setItemMeta(itemMeta);
                showcaseGUI.setItem(slot, itemStack);
            }
        }

        playerShowcaseGUIs.put(p, showcaseGUI);
        p.openInventory(playerShowcaseGUIs.get(p));
    }
}
