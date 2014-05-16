package net.nanase.minecraft.burnowen;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by nanase on 14/05/12.
 */
public class BurnOwen extends JavaPlugin {
    public void onEnable() {
        //プラグインが有効化されたときの処理
        this.getLogger().info("Burn! Owen!");
    }

    public void onDisable() {
        //プラグインが無効化されたときの処理
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("burn")) {
            this.burn(sender, args);
            return true;
        }
        return false;
    }

    private void burn(CommandSender sender, String[] args) {
        boolean targetIsCreative;
        boolean targetIsNotFound = false;
        boolean targetIsOffline = false;
        boolean targetIsSender = false;

        if (args.length == 0) {
            if (sender instanceof Player)
                this.sendMessage((Player) sender, "燃やす人を指定してね！");
            else
                this.sendConsoleMessage(sender, "プレイヤー名が指定されていません.");

            return;
        }

        {
            Player target = this.getServer().getPlayerExact(args[0]);

            if (target == null || !target.isValid()) {
                targetIsNotFound = true;

                if (sender instanceof Player)
                    target = (Player) sender;
                else {
                    this.sendConsoleMessage(sender, "存在しないプレイヤー名です.");
                    return;
                }
            } else if (!target.isOnline()) {
                targetIsOffline = true;

                if (sender instanceof Player)
                    target = (Player) sender;
                else {
                    this.sendConsoleMessage(sender, "オフラインプレイヤーです.");
                    return;
                }
            } else if (args[0].equals(sender.getName()))
                targetIsSender = true;

            targetIsCreative = (target.getGameMode() == GameMode.CREATIVE);
            this.setFireToPlayer(target, targetIsCreative, !targetIsSender && !targetIsNotFound && !targetIsOffline);
        }

        for (Player p : this.getServer().getOnlinePlayers()) {
            if (p.getDisplayName().equals(args[0])) {
                if (targetIsNotFound)
                    this.sendMessage(p, args[0] + " は存在しません！ 代わりに燃えてください！");
                else if (targetIsOffline)
                    this.sendMessage(p, "ログインしていない " + args[0] + " を燃やそうとしました！ そんなに燃やしたいんですか！？ あなたが燃えてください！");
                else {
                    if (targetIsSender) {
                        if (targetIsCreative)
                            this.sendMessage(p, "自分自身を燃やそうとしましたが、クリエイティブのため失敗しました.");
                        else
                            this.sendMessage(p, "自分自身を燃やしました.");
                    } else {
                        if (targetIsCreative)
                            this.sendMessage(p, sender.getName() + " が燃やそうとしましたが、クリエイティブのため失敗しました.");
                        else
                            this.sendMessage(p, sender.getName() + " により燃やされました.");
                    }
                }
            } else {
                if (targetIsNotFound)
                    this.sendMessage(p, sender.getName() + " は存在しない " + args[0] + " を燃やそうとしたため、代わりに燃えました.");
                else if (targetIsOffline)
                    this.sendMessage(p, sender.getName() + " は " + args[0] + " を燃やそうとしましたがログインしていないため、代わりに燃えました.");
                else {
                    if (targetIsSender) {
                        if (targetIsCreative)
                            this.sendMessage(p, sender.getName() + " は自分を燃やそうとしましたが、クリエイティブのため失敗しました.");
                        else
                            this.sendMessage(p, sender.getName() + " は自分を燃やしました.");
                    } else {
                        if (targetIsCreative)
                            this.sendMessage(p, sender.getName() + " は " + args[0] + " を燃やそうとしましたが、クリエイティブのため失敗しました.");
                        else
                            this.sendMessage(p, args[0] + " は " + sender.getName() + " に燃やされました.");
                    }
                }
            }

        }

    }

    private void setFireToPlayer(Player target, boolean isCreative, boolean allowEffect) {
        if (isCreative) {
            Location loc = target.getLocation();

            for (int i = 0; i < 5; i++) {
                target.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0, 2);
                target.getWorld().playEffect(loc, Effect.SMOKE, 10);
            }
        } else {
            target.removePotionEffect(PotionEffectType.REGENERATION);
            target.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);

            if (allowEffect) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 30, 2));
                target.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 30, 1));
            }

            target.setFireTicks(20 * 30);
        }
    }

    private void sendMessage(Player p, String message) {
        p.sendRawMessage("§4[BurnOwen]§F " + message);
    }

    private void sendConsoleMessage(CommandSender sender, String message) {
        sender.sendMessage("§4[BurnOwen]§F " + message);
    }
}
