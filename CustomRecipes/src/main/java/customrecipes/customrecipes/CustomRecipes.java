package customrecipes.customrecipes;

import customrecipes.customrecipes.Commands.RecipeCommands;
import customrecipes.customrecipes.Commands.RecipeShowcaseCommand;
import customrecipes.customrecipes.Commands.RecipeTabCompleter;
import customrecipes.customrecipes.CustomConfig.CustomRecipesGUI;
import customrecipes.customrecipes.CustomConfig.RecipeConfig;
import customrecipes.customrecipes.GUI.RecipeGUI;
import customrecipes.customrecipes.Listeners.CraftListener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class CustomRecipes extends JavaPlugin {
    public static Inventory rGUI;
    public static Map<Player, Inventory> playerShowcaseGUIs = new HashMap<>();
    public static String itemName = null;
    public static boolean showEnabled = true;
    @Override
    public void onEnable() {
        // Plugin startup logic

        saveResource("CustomRecipesGUI.yml", false);

        //Setup config
        RecipeConfig.setup();
        RecipeConfig.get().options().copyDefaults(true);
        RecipeConfig.save();

        CustomRecipesGUI.setup();
        CustomRecipesGUI.get().options().copyDefaults(true);
        CustomRecipesGUI.save();

        new RecipeGUI().createRecipeGui();

        getServer().getPluginManager().registerEvents(new CraftListener(), this);
        getCommand("CustomRecipe").setExecutor(new RecipeCommands());
        getCommand("CustomRecipe").setTabCompleter(new RecipeTabCompleter());
        getCommand("recipes").setExecutor(new RecipeShowcaseCommand());
    }
}
