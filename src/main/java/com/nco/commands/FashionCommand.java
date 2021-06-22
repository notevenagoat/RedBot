package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class FashionCommand extends AbstractCommand {

    public FashionCommand(String[] messageArgs, User author, MessageChannel channel) {
        super(messageArgs, author, channel);
    }

    @Override
    protected void returnHelp() {
//        String[] fashion = {"Random 1", "Random 2", "Random 3", "Random 4", "Random 5", "Random 6", "Random 7"};
        String[] clothingStyles = {"Bag Lady Chic", "Gang Colors", "Generic Chic", "Bohemian", "Leisurewear", "Nomad Leathers", "Asia Pop", "Urban Flash", "Businesswear"};

        String[] fashion = new String[7];

        for (int i = 0; i < 7; i++)
        {
            int random_int = (int)Math.floor(Math.random()*(8+1)+0);
            fashion[i]=clothingStyles[random_int];
        }
        EmbedBuilder info = new EmbedBuilder();

        info.setTitle("Fashion of the Week");
        info.setDescription("Name of stlyle.");
        info.addField("Bottoms", fashion[0], true);
        info.addField("Top", fashion[1], true);
        info.addField("Jacket", fashion[2], true);
        info.addField("Footwear",fashion[3], true);
        info.addField("Jewerly", fashion[4], true);
        info.addField("Hat", fashion[5], true);
        info.addField("Eyewear", fashion[6], true);
        info.setColor(Color.red);

        channel.sendMessage(info.build()).queue();
        info.clear();
    }

}
