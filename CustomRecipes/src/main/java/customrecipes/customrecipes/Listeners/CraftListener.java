package customrecipes.customrecipes.Listeners;

import customrecipes.customrecipes.CustomConfig.RecipeConfig;
import customrecipes.customrecipes.GUI.RecipeGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Set;

import static customrecipes.customrecipes.CustomRecipes.*;
import static customrecipes.customrecipes.GUI.RecipeGUI.CreateGUI;

public class CraftListener implements Listener {

    @EventHandler
    public void onTakeInventory(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        ItemStack c = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        String worldName = p.getWorld().getName();

        if (inv.equals(rGUI)) {
            if (c != null) {
                if (c.getType().equals(Material.WHITE_STAINED_GLASS_PANE) && c.getItemMeta().getDisplayName().equals("-")) {
                    e.setCancelled(true);
                } else if (c.getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
                    registerRecipe(inv);
                    clearInv(inv);
                    p.closeInventory();
                    p.sendMessage(ChatColor.GREEN + "Custom recipe created!");
                }
            }
        }

        if (playerShowcaseGUIs.containsKey(p)) {
            if (inv.equals(playerShowcaseGUIs.get(p))) {
                if (c != null) {
                    e.setCancelled(true);
                    if (RecipeConfig.get().getConfigurationSection(worldName) == null) {
                        return;
                    }

                    Set<String> recipes = RecipeConfig.get().getConfigurationSection(worldName).getKeys(false);

                    for (String recipe : recipes) {
                        ItemStack istack = RecipeConfig.get().getItemStack(worldName + "." + recipe + ".Result").clone();
                        ItemMeta itemMeta = istack.getItemMeta();
                        itemMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&eClick to preview")));
                        istack.setItemMeta(itemMeta);
                        if (istack.equals(c))
                        {
                            new RecipeGUI().createShowcaseGui(worldName + "." + recipe, p);
                            return;
                        }
                    }
                    if (c.getType() == Material.BARRIER) {
                        CreateGUI(worldName,p);
                    }
                }
            }
        }

        if ((e.getClickedInventory() instanceof CraftingInventory)) {
            RecipeConfig.reload();
            CraftingInventory craftingInventory = (CraftingInventory) e.getClickedInventory();
            if (craftingInventory.getResult() == null || craftingInventory.getResult().getType().isAir()) {
                return;
            }

            if (e.getSlotType() == InventoryType.SlotType.RESULT) {

                if (RecipeConfig.get().getConfigurationSection(worldName) == null) {
                    return;
                }

                Set<String> recipes = RecipeConfig.get().getConfigurationSection(worldName).getKeys(false);

                    for (String recipe : recipes) {
                        if (matchesCustomRecipe(craftingInventory.getMatrix(), worldName + "." + recipe)) {
                            Player player = (Player) e.getWhoClicked();
                            e.setCancelled(true);

                            ShapedRecipe newRecipe = new ShapedRecipe(craftingInventory.getResult());
                            newRecipe.shape("ABC", "DEF", "GHI");
                            char[] placeholders = "ABCDEFGHI".toCharArray();
                            for (int i = 0; i < placeholders.length; i++) {
                                if ( craftingInventory.getMatrix()[i] != null &&  craftingInventory.getMatrix()[i].getType() != Material.AIR) {
                                    newRecipe.setIngredient(placeholders[i], new RecipeChoice.ExactChoice( craftingInventory.getMatrix()[i]));
                                }
                            }

                            InventoryView inventoryView = player.getOpenInventory();
                            CraftItemEvent craftEvent = new CraftItemEvent(newRecipe, inventoryView, InventoryType.SlotType.CRAFTING, e.getSlot(), e.getClick(),null);
                            Bukkit.getServer().getPluginManager().callEvent(craftEvent);

                            if (!craftEvent.isCancelled()) {
                                ItemStack craftedItem = craftingInventory.getResult();
                                String name = "";
                                if (craftedItem != null && !craftedItem.getType().isAir()) {

                                    if (e.getClick().isShiftClick())
                                        player.getInventory().addItem(craftedItem.clone());
                                    else
                                        player.setItemOnCursor(craftedItem.clone());

                                    if (craftedItem.getItemMeta().hasDisplayName()) {
                                        name = craftedItem.getItemMeta().getDisplayName();
                                    } else if (craftedItem.getItemMeta().hasLocalizedName()) {
                                        name = craftedItem.getItemMeta().getLocalizedName();
                                    } else
                                        name = craftedItem.getType().name();
                                }

                                Bukkit.broadcastMessage(ChatColor.BOLD + e.getWhoClicked().getName() + ChatColor.RESET + " has crafted the " + ChatColor.translateAlternateColorCodes('&', name));

                                craftingInventory.setResult(null);
                                consumeRequiredItems(craftingInventory, worldName + "." + recipe);
                            }
                        }
                    }
            }
        }
    }

    public void clearInv(Inventory inv) {
        inv.clear(10);
        inv.clear(11);
        inv.clear(12);
        inv.clear(19);
        inv.clear(20);
        inv.clear(21);
        inv.clear(28);
        inv.clear(29);
        inv.clear(30);
        inv.clear(24);
    }

    public void registerRecipe(Inventory inventory) {
        ItemStack item = inventory.getItem(24);
        if (item == null) {
            return;
        }

        RecipeConfig.get().set(itemName + ".Result", item);

        ItemStack[] ingredientItems = {
                inventory.getItem(10),
                inventory.getItem(11),
                inventory.getItem(12),
                inventory.getItem(19),
                inventory.getItem(20),
                inventory.getItem(21),
                inventory.getItem(28),
                inventory.getItem(29),
                inventory.getItem(30)
        };

        for (int i = 0; i < 9; i++) {
            ItemStack ingredient = ingredientItems[i];
            if (ingredient != null) {
                RecipeConfig.get().set(itemName + "." + (char) ('1' + i), ingredient);
            } else {
                RecipeConfig.get().set(itemName + "." + (char) ('1' + i), Material.AIR);
            }
        }

        RecipeConfig.save();
    }

    @EventHandler
    public void onPrepareEvent(PrepareItemCraftEvent e) {
        CraftingInventory craftingInventory = e.getInventory();
        ItemStack[] matrix = craftingInventory.getMatrix();
        Player p = (Player) e.getView().getPlayer();
        String worldName = p.getWorld().getName();

        RecipeConfig.reload();

        if (RecipeConfig.get().getConfigurationSection(worldName) == null) {
            return;
        }

        Set<String> recipes = RecipeConfig.get().getConfigurationSection(worldName).getKeys(false);
        for (String recipe : recipes) {
            if (matchesCustomRecipe(matrix, recipe)) {
                ItemStack result = new ItemStack(RecipeConfig.get().getItemStack(worldName + "." + recipe + ".Result"));
                e.getInventory().setResult(result);
            }
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        Player p = (Player) e.getPlayer();

        if (playerShowcaseGUIs.containsKey(p) && inv.equals(playerShowcaseGUIs.get(p))) {
            playerShowcaseGUIs.remove(p);
        }
    }

    private boolean matchesCustomRecipe(ItemStack[] matrix, String key) {
        for (int i = 1; i <= 9; i++) {
            String itemKey = key + "." + i;
            if (RecipeConfig.get().contains(itemKey)) {
                ItemStack requiredStack = RecipeConfig.get().getItemStack(itemKey);
                ItemStack matrixItem = matrix[i - 1];

                if (requiredStack == null) {
                    if (matrixItem != null) {
                        return false;
                    }
                } else if (matrixItem == null || !matrixItem.isSimilar(requiredStack) || matrixItem.getAmount() < requiredStack.getAmount()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void consumeRequiredItems(CraftingInventory inventory, String key) {
        for (int i = 1; i <= 9; i++) {
            String itemKey = key + "." + i;
            ItemStack requiredStack = RecipeConfig.get().getItemStack(itemKey);
            ItemStack currentItem = inventory.getItem(i);

            if (requiredStack != null && currentItem != null && currentItem.isSimilar(requiredStack)) {
                int requiredAmount = requiredStack.getAmount();
                int currentAmount = currentItem.getAmount();

                int amount = 0;
                 amount = currentAmount - requiredAmount;
                inventory.setItem(i, new ItemStack(currentItem.getType(), amount));
            }
        }
    }
}
