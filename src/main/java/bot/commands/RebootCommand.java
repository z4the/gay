package bot.commands;

import bot.CommandUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class RebootCommand implements Command {
    public void process(MessageReceivedEvent event, List<String> args) throws Exception {
        Robot robot = new Robot();
        try { //make new process and then kill existing one ?_?
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.start();
            robot.delay(1000);
            CommandUtils.sendScreenshot(event);
        } catch (Exception e) {
            event.getChannel().sendMessage("We couldn't find the process: " + String.join(" ", args)).queue();
        }
    }
}
