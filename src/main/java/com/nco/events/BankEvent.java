package com.nco.events;

import com.nco.RedBot;
import com.nco.utils.DBUtils;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankEvent extends AbstractEvent {

    @Override
    protected boolean canProcessByUser(String[] messageArgs) {
        return messageArgs.length >= 2 && StringUtils.isNumeric(messageArgs[1]);
    }

    @Override
    protected boolean canProcessByName(String[] messageArgs) {
        return messageArgs.length >= 3 && StringUtils.isNumeric(messageArgs[2]);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, ResultSet rs, String[] messageArgs, EmbedBuilder builder) throws SQLException {
        if (updateBank(messageArgs, rs, conn) && insertBank(messageArgs, conn)) {
            int oldBank = rs.getInt("Bank");
            int newBank = oldBank + Integer.parseInt(messageArgs[2]);
            builder.setTitle(messageArgs[0] + "'s Bank Balance Updated");
            builder.setDescription("For \"" + messageArgs[1] + "\"");
            builder.addField("Old Balance", oldBank + "eb", true);
            builder.addBlankField(true);
            builder.addField("New Balance", newBank + "eb", true);
            if (messageArgs.length == 4) {
                int oldDownTime = rs.getInt("DownTime");
                int changeDT = Integer.parseInt(messageArgs[3]);
                int newDownTime = oldDownTime - (changeDT < 0 ? -changeDT : changeDT);

                builder.addField("Old DT", String.valueOf(oldDownTime), true);
                builder.addBlankField(true);
                builder.addField("New DT", String.valueOf(newDownTime), true);
            }
        } else {
            builder.setTitle("ERROR: Bank Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }


    private static boolean updateBank(String[] messageArgs, ResultSet rs, Connection conn) throws SQLException {
        int newBalance = rs.getInt("Bank") + Integer.parseInt(messageArgs[2]);
        int newDownTime = rs.getInt("DownTime");
        if (messageArgs.length == 4) {
            int changeDT = Integer.parseInt(messageArgs[3]);
            newDownTime -= (changeDT < 0 ? -changeDT : changeDT);
        }
        String sql = "UPDATE NCO_PC set Bank = ?, DownTime = ? Where CharacterName = ?";

        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newBalance);
            stat.setInt(2, newDownTime);
            stat.setString(3, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private static boolean insertBank(String[] messageArgs, Connection conn) throws SQLException {
        String sql;
        if (messageArgs.length == 4) {
            sql = "INSERT INTO NCO_BANK (CharacterName, Reason, Amount, DownTime) VALUES (?,?,?,?)";
        } else {
            sql = "INSERT INTO NCO_BANK (CharacterName, Reason, Amount) VALUES (?,?,?)";
        }
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setString(3, messageArgs[2]);
            if (messageArgs.length == 4) {
                int changeDT = Integer.parseInt(messageArgs[3]);
                stat.setInt(4, (changeDT < 0 ? -changeDT : changeDT));
            }
            return stat.executeUpdate() == 1;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Bank Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to manage a characters bank\n" + RedBot.PREFIX +
                "bank \"PC Name\" \"Reason\" \"Amount\" \"DT(Optional)\" \nor \n" + RedBot.PREFIX +
                "bank \"Reason\" \"Amount\" \"DT(Optional)\"";
    }

}
