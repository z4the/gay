package bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static bot.Config.isDebugMode;

public class CommandUtils {
    public static final String OpSystem = System.getProperty("os.name");
    public static final boolean isWindows = OpSystem.toLowerCase().contains("win");
    public static final boolean isLinux = OpSystem.toLowerCase().replace('u', 'i').contains("ix"); // LAZY LEVEL 9999
    public static final boolean isMac = OpSystem.toLowerCase().contains("mac");
    public static final boolean isSolaris = OpSystem.toLowerCase().contains("sunos");
    private static String whatami; //useless for now

    public static final HashMap<String, String> exePaths = new HashMap<>();
    public static final HashMap<String, Boolean> exeDependencies = new HashMap<>();

    public static String ffdesktop;
    public static String ffinterface;
    private static final boolean debug;

    static {
        try {
            title("titleart");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("DemiBOT Alpha v.1.2.7");
        debug = Config.isDebugMode();
        if(debug) {
            System.out.println("*** DEBUG MODE IS TURNED ON! ***");
        }
        try {
            Field defaultHeadlessField = java.awt.GraphicsEnvironment.class.getDeclaredField("defaultHeadless");
            defaultHeadlessField.setAccessible(true);
            defaultHeadlessField.set(null,Boolean.FALSE);
            Field headlessField = java.awt.GraphicsEnvironment.class.getDeclaredField("headless");
            headlessField.setAccessible(true);
            headlessField.set(null,Boolean.FALSE);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        exePaths.put("ffmpeg", "./ffmpeg.exe");
        exePaths.put("nircmd", "./nircmd.exe");
        exePaths.put("wget", "./wget.exe");

        if(isWindows) { System.setProperty("java.awt.headless", "true"); ffdesktop = "gdigrab"; ffinterface = "desktop"; whatami = "Windows";}
        if(isMac) { ffdesktop = "avfoundation"; ffinterface = ":0"; whatami = "Mac";}
        if(isLinux || isSolaris) { ffdesktop = "x11grab"; ffinterface = ":0"; whatami = "Linux"; System.setProperty("java.awt.headless", "true"); } //idk about solaris but whatever
        System.out.println("We're on: " + whatami); //debug
        FileCheck();
        //System.out.println("headless? " + java.awt.GraphicsEnvironment.isHeadless());
    }

    private static final int[] numpad = {
            KeyEvent.VK_NUMPAD0,
            KeyEvent.VK_NUMPAD1,
            KeyEvent.VK_NUMPAD2,
            KeyEvent.VK_NUMPAD3,
            KeyEvent.VK_NUMPAD4,
            KeyEvent.VK_NUMPAD5,
            KeyEvent.VK_NUMPAD6,
            KeyEvent.VK_NUMPAD7,
            KeyEvent.VK_NUMPAD8,
            KeyEvent.VK_NUMPAD9
    };
    private static final DateFormat hourFormat = new SimpleDateFormat("hh:mm a");

    public CommandUtils() {
    }

    public static String readStream(InputStream stream) {
        Scanner scanner = new Scanner(stream).useDelimiter("\\A");
        String result = scanner.hasNext() ? scanner.next() : "";

        scanner.close();
        return result;
    }

    public static void typeString(Robot robot, String str) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        boolean numlockOn = toolkit.getLockingKeyState(KeyEvent.VK_NUM_LOCK);

        if (!numlockOn) {
            robot.keyPress(KeyEvent.VK_NUM_LOCK);
        }

        for (int i = 0; i < str.length(); i++) {
            int code = (int) str.charAt(i);
            String codeStr;

            if (code <= 999) {
                codeStr = "0" + code;
            } else {
                codeStr = String.valueOf(code);
            }

            robot.keyPress(KeyEvent.VK_ALT);

            for (int c = 0; c < codeStr.length(); c++) {
                int key = codeStr.charAt(c) - '0';
                robot.keyPress(numpad[key]);
                robot.keyRelease(numpad[key]);
            }

            robot.keyRelease(KeyEvent.VK_ALT);
        }

        if (!numlockOn) {
            robot.keyPress(KeyEvent.VK_NUM_LOCK);
        }
    }

    public static boolean sendScreenshot(MessageChannel channel) {
        try {
            Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage img = new Robot().createScreenCapture(screen);
            PointerInfo pointer = MouseInfo.getPointerInfo();
            int x = (int) pointer.getLocation().getX();
            int y = (int) pointer.getLocation().getY();

            Image mouseImage = ImageIO.read(new File("src/main/resources/mouse.png")).getScaledInstance(15, 24, 0);
            img.getGraphics().drawImage(mouseImage, x, y, null);
            File imgFile = Config.getDirectFile("output", "ss.png");
            ImageIO.write(img, "png", imgFile);
            channel.sendFile(imgFile).queue();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean sendScreenshot(MessageReceivedEvent event) {
        return sendScreenshot(event.getChannel());
    }

    public static boolean sendFile(MessageChannel channel, String args) {

        String file = args.substring(1, args.length() -1);
        //String file = args;
        if(Config.isDebugMode()) {
            System.out.println("[Upload] File trying to be sent: " + file);
        }
        try {
            File sFile = Config.getDirectFile("downloads", file);
            channel.sendFile(sFile).queue();
            return true;
        } catch (Exception e) {
            if (Config.isDebugMode()) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static boolean sendFile(MessageReceivedEvent event, String args) {
        return sendFile(event.getChannel(), args);
    }

    public static Image getScreenshot() {
        try {
            return ImageIO.read(Config.getDirectFile("output", "ss.png"));
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean FileCheck() {
        return FileCheck(exePaths);
    }
    public static boolean FileCheck(HashMap<String, String> exePaths) { //check path environment too later wipp
        boolean exists = true; //self note: should always be true unless something makes it false
        String[] PathArr = new String[exePaths.size()];

        for(String x : exePaths.keySet()) {
            exeDependencies.put(x, false);

            // this should check if filepath exists
            if (isWindows) {

                File check = new File(exePaths.get(x));
                if(debug) {
                    System.out.println("Does path contain " + x + " ? : " + System.getenv("Path").contains(x)); // needs to be changed to"./DemiBOT/bin"
                }
                if (check.exists() || System.getenv("Path").contains(x)) {
                    exeDependencies.put(x, true);
                    if(debug) {
                        System.out.println("DEBUG: We're clear.");
                    }
                } else {
                    exists = false;
                }
            }
            if (isLinux) { // we can probably make linux and mac the same if statement, idek about solaris who knows /shrug xd

            }
            if(isMac) {

            }
            if(isSolaris) {

            }
        }
        return exists;
    }


/*
shell.exec(`wget ${args} -P "./Downloads/"`, (error, complete) => {
                          if (error && (isLinux || isDarwin)) {
                            send('There was an error running `'+message.content+'`. Make sure your link is a valid download link, or... \nPlease run ``sudo apt install -y wget`` to install ``wget``.');
                            return;
                          } else if (error && isWindows) {
                            send('There was an error running `'+message.content+'`. Make sure your link is a valid download link, or... \nMake sure ``wget.exe`` is in the same location as ``bot.js``.');
                            return;
                          }
 */


    public static void sendEmbed(MessageChannel channel, User user, String command, String title, String description, Consumer<Message> callback) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription(description);
        builder.setColor(Color.red);
        builder.setAuthor(user.getName(), null, user.getAvatarUrl());
        builder.setFooter(String.format("%s • Today at %s", command, hourFormat.format(new Date())), null);
        channel.sendMessage(builder.build()).queue(callback);
    }

    public static void sendEmbed(MessageChannel channel, User user, String command, String title, String description) {
        sendEmbed(channel, user, command, title, description, null);
    }

    public static void sendEmbed(MessageChannel channel, User user, String command, String title) {
        sendEmbed(channel, user, command, title, null, null);
    }

    public static void sendCommandEmbed(MessageChannel channel, User user, String command, String description) {
        sendEmbed(channel, user, command, description, description, null);
    }

    public static void sendEmbedFor(MessageChannel channel, User user, String command, String title, String description, long seconds) {
        sendEmbed(channel, user, command, title, description, message -> message.delete().queueAfter(seconds, TimeUnit.SECONDS));
    }

    public static void sendEmbedFor(MessageChannel channel, User user, String command, String description, long seconds) {
        sendEmbedFor(channel, user, command, description, description, seconds);
    }

    //Hardware Info


    // NETWORKING

    public String getPublicIP() throws IOException {

        String PublicIP = "";
        URL MyIPURL = new URL("http://bot.whatismyipaddress.com");

        BufferedReader sc = new BufferedReader(new InputStreamReader(MyIPURL.openStream()));

        PublicIP = sc.readLine().trim();
        String publicIP = PublicIP;
        return PublicIP;
    }

    public InetAddress getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    public InetAddress getLoopbackAddress() {
        return InetAddress.getLoopbackAddress();
    }

    public String getCanonicalHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getCanonicalHostName();
    }

    public String getLocalIP() throws IOException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public String getHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    public int gethashCode() throws UnknownHostException {
        return InetAddress.getLocalHost().hashCode();
    }
    public static void title(String variation) throws FileNotFoundException {
        try {
            Scanner title = new Scanner(new File("src/main/resources/" + variation));
            while (title.hasNextLine()) {
                System.out.println(title.nextLine());
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Title art not found");
        }
    }

    public static String readFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

}
