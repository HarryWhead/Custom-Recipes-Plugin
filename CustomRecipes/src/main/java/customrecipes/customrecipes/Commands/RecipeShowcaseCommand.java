package customrecipes.customrecipes.Commands;

import customrecipes.customrecipes.CustomConfig.CustomRecipesGUI;
import customrecipes.customrecipes.CustomConfig.RecipeConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static customrecipes.customrecipes.CustomRecipes.showEnabled;
import static customrecipes.customrecipes.GUI.RecipeGUI.CreateGUI;

public class RecipeShowcaseCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        RecipeConfig.reload();
        CustomRecipesGUI.reload();
        if(sender instanceof Player) {
            Player p = (Player) sender;

            if (showEnabled || p.isOp()) {
                String worldName = p.getWorld().getName();
                CreateGUI(worldName,p);
            }
        }
        return true;
    }
}
