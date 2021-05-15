package bot;

import bot.commands.*;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private static Map<String, Command> commands = new HashMap<String, Command>();
    static {
        addCommand("encode", new EncodeCommand());
        addCommand("decode", new DecodeCommand());
        addCommand("ping", new PingCommand());
        addCommand("test", new TestCommand());

    }

    public CommandManager(boolean debug) {
        if(debug) {
            System.out.println("!! DEBUG MODE IS ACTIVATED !!");
            System.out.println("Headless? " + java.awt.GraphicsEnvironment.isHeadless());
            System.out.println("User IDs: " + Config.getUserIds());
            System.out.println("Owner IDs: " + Config.getOwnerIds());
        }
    }

    public static void addCommand(String commandName, Command command) {
        commands.put(commandName, command);
    }

    public static Command getCommand(String commandName) {
        return commands.get(commandName);
    }

    public static Command getCommandStarts(String msg) {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            if (msg.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }
}

