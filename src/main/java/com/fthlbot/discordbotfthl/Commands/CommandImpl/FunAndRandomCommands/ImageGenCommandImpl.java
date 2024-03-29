package com.fthlbot.discordbotfthl.Commands.CommandImpl.FunAndRandomCommands;

import com.fthlbot.discordbotfthl.core.Annotation.CommandType;
import com.fthlbot.discordbotfthl.core.Annotation.Invoker;
import com.fthlbot.discordbotfthl.core.Handlers.Command;
import org.imgscalr.Scalr;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.util.logging.ExceptionLogger;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

@Component
@Invoker(
        alias = "snitch",
        description = "Generates a snitch image.",
        usage = "snitch <@User>",
        type = CommandType.MISC
)
public class ImageGenCommandImpl implements Command {
    @Override
    public void execute(SlashCommandCreateEvent event) {
        event.getSlashCommandInteraction().respondLater().thenAccept(res -> {
            //TODO: Add a counter to the "Cringe Retard" text
            User user = event.getSlashCommandInteraction().getArguments().get(0).getUserValue().get();
            if (user.isBotOwner()){
                user = event.getInteraction().getUser();
            }
            try {
                res.addAttachment(getImage(user), "snitch.png").update();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).exceptionally(ExceptionLogger.get());
    }

    private static final int AVATAR_X = 275;
    private static final int AVATAR_Y = 80;
    private static final int IMAGE_SIZE = 80;

    public BufferedImage getImage(User user) throws IOException {
        URL background = getClass().getResource("/snitch/background.png");
        URL profile = getClass().getResource("/snitch/profile.png");
        URL avatar = user.getAvatar().getUrl();

        assert background != null;
        BufferedImage image = ImageIO.read(background);
        assert profile != null;
        BufferedImage profileImage = ImageIO.read(profile);
        BufferedImage avatarImage = ImageIO.read(avatar);

        Graphics2D g = image.createGraphics();

        g.drawImage(simpleResizeImage(avatarImage), AVATAR_X, AVATAR_Y, null);
        g.drawImage(profileImage, 259, 76, null);
        g.setFont(new Font("Roboto", Font.BOLD, 12));
        g.drawString(user.getDiscriminatedName(), 270, 205);
        //296, 191

        g.create();
        return image;
    }

    private BufferedImage simpleResizeImage(BufferedImage originalImage) {
        return Scalr.resize(originalImage, IMAGE_SIZE);
    }


}
