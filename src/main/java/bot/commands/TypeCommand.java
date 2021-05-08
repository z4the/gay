package bot.commands;

import bot.CommandUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class TypeCommand implements Command {

    public void process(MessageReceivedEvent event, List < String > args) throws Exception {
        Robot robot = new Robot();
        String text = String.join(" ", args);

        CommandUtils.typeString(robot, text);
        robot.delay(2000);
        CommandUtils.sendScreenshot(event);
    }
}