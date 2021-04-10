package com.nco.events;

import com.nco.utils.DBUtils;
import com.nco.RedBot;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FameEvent extends AbstractEvent {

    @Override
    protected boolean canProcessByUser(String[] messageArgs) {
        return messageArgs.length == 2;
    }

    @Override
    protected boolean canProcessByName(String[] messageArgs) {
        return messageArgs.length == 3;
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, ResultSet rs, String[] messageArgs, EmbedBuilder builder) throws SQLException {
        if (updateFame(messageArgs, rs.getInt("Fame"), conn) && insertFame(messageArgs, conn)) {
            int oldFame = rs.getInt("Fame");
            int newFame = oldFame + Integer.parseInt(messageArgs[2]);
            int oldReputation = rs.getInt("Reputation");
            int newReputation = newFame / 20;

            builder.setTitle(messageArgs[0] + "'s Fame Updated");
            builder.setDescription("For \"" + messageArgs[1] + "\"");
            builder.addField("Old Fame", String.valueOf(oldFame), true);
            builder.addBlankField(true);
            builder.addField("New Fame", String.valueOf(newFame), true);

            if (oldReputation != newReputation) {
                builder.addField("Old Reputation", rs.getString("Reputation"), true);
                builder.addField("New Reputation", String.valueOf(newReputation), true);
            }
        } else {
            builder.setTitle("ERROR: Fame Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private static boolean updateFame(String[] messageArgs, int currentFame, Connection conn) throws SQLException {
        int newFameTotal = currentFame + Integer.parseInt(messageArgs[2]);
        int newReputation = newFameTotal / 20;
        String sql = "UPDATE NCO_PC set Fame = ?, Reputation = ? Where CharacterName = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newFameTotal);
            stat.setInt(2, newReputation);
            stat.setString(3, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private static boolean insertFame(String[] messageArgs, Connection conn) throws SQLException {
        String sql = "INSERT INTO NCO_FAME (CharacterName, Reason, Fame) VALUES (?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setString(3, messageArgs[2]);
            return stat.executeUpdate() == 1;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Fame Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to add fame onto a characters \n" + RedBot.PREFIX +
                "fame \"PC Name\" \"Reason\" \"Amount\" \nor \n" + RedBot.PREFIX + "fame \"Reason\" \"Amount\"";
    }
}
