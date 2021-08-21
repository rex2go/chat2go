package eu.rex2go.chat2go.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.logging.Level;

public abstract class RexConfig {

    @Getter
    private final int version;
    protected JavaPlugin plugin;
    @Getter
    private String fileName;
    @Getter
    private FileConfiguration config;
    @Getter
    private File file;

    RexConfig(JavaPlugin plugin, String fileName, int version) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder() + File.separator + fileName);
        this.version = version;

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);

        int ver = config.getInt("version");
        if (version != 0 && version > ver) {
            File outdated = new File(file.getPath() + ".outdated");
            if (outdated.exists()) {
                outdated.delete();
            }

            file.renameTo(outdated);

            this.file = new File(plugin.getDataFolder() + File.separator + fileName);
            if (!file.exists()) {
                plugin.saveResource(fileName, false);
            }

            this.config = YamlConfiguration.loadConfiguration(file);
        }
    }

    public void load() {
        Field[] fields = getClass().getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(ConfigInfo.class)) continue;
            ConfigInfo configInfo = field.getAnnotation(ConfigInfo.class);
            Object object = getConfig().get(configInfo.path());

            try {
                field.setAccessible(true);
                field.set(this, object);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "asd");

                plugin.getLogger().log(
                        Level.SEVERE,
                        e.getClass().getName() + " in " + fileName + ": " + field.getName()
                );
            }
        }
    }

    ;

    public void reload() {
        this.file = new File(plugin.getDataFolder() + File.separator + fileName);

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigInfo {
        String path();
    }
}
