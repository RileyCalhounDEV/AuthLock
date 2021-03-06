package me.riley.authlock.bukkit.handlers;

import me.riley.authlock.bukkit.AuthLock;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    private String qrCodeURL = "https://www.google.com/chart?chs=128x128&cht=qr&chl=otpauth://totp/%%label%%?secret=%%key%%";
    private String label = "%%name%%:MC2FA";
    private boolean debug = false;
    private boolean commandsDisabled = true;
    private boolean advise = true;
    private boolean guiKeypad = true;
    private KeyStorage keyStorage = KeyStorage.FLAT;
    private Forced forced = Forced.FALSE;
    private List<String> whitelistedCommands = new ArrayList<>();
    private List<String> blacklistedCommands = new ArrayList<>();

    public enum KeyStorage {
        FLAT, SQLITE, MYSQL
    }

    public enum Forced {
        TRUE, FALSE, OP
    }

    public ConfigHandler(AuthLock mc2FA) {
        FileConfiguration config = mc2FA.getConfig();

        if (config.isSet("Debug"))
            debug = config.getBoolean("Debug");

        if (config.isSet("Disable Commands"))
            commandsDisabled = config.getBoolean("Disable Commands");

        if (config.isSet("Advise"))
            advise = config.getBoolean("Advise");

        if (config.isSet("GUI Keypad"))
            guiKeypad = config.getBoolean("GUI Keypad");

        if (config.isSet("Whitelisted Commands"))
            whitelistedCommands = config.getStringList("Whitelisted Commands");
        whitelistedCommands.add("2fa");
        whitelistedCommands.add("twofactorauth");
        whitelistedCommands.add("twofactorauthentication");
        whitelistedCommands.add("twofa");
        whitelistedCommands.add("tfa");

        if (config.isSet("Blacklisted Commands"))
            blacklistedCommands = config.getStringList("Blacklisted Commands");

        if (config.isSet("Key Storage")) {
            try {
                keyStorage = KeyStorage.valueOf(config.getString("Key Storage").toUpperCase());
            } catch (Exception ignored) {
            }
        }

        if (keyStorage == KeyStorage.SQLITE) {
            mc2FA.getLogger().warning("SQLite storage is not yet supported, reverting to flat file storage");
            keyStorage = KeyStorage.FLAT;
        } else if (keyStorage == KeyStorage.MYSQL) {
            mc2FA.getLogger().warning("MySQL storage is not yet supported, reverting to flat file storage");
            keyStorage = KeyStorage.FLAT;
        }

        if (config.isSet("Forced")) {
            try {
                forced = Forced.valueOf(config.getString("Forced").toUpperCase());
            } catch (Exception ignored) {
            }
        }

        if (config.isSet("QR Code URL")) {
            qrCodeURL = config.getString("QR Code URL");
        }

        if (config.isSet("OTP Label")) {
            label = config.getString("OTP Label");
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isCommandsDisabled() {
        return commandsDisabled;
    }

    public boolean isAdvise() {
        return advise;
    }

    public boolean isGuiKeypad() {
        return guiKeypad;
    }

    public List<String> getWhitelistedCommands() {
        return whitelistedCommands;
    }

    public List<String> getBlacklistedCommands() {
        return blacklistedCommands;
    }

    public KeyStorage getKeyStorage() {
        return keyStorage;
    }

    public Forced getForced() {
        return forced;
    }

    public String getQrCodeURL() {
        return qrCodeURL;
    }

    public String getLabel(Player player) {
        if (player == null) {
            return "";
        }
        return label.replaceAll("%%name%%", player.getName());
    }

}
