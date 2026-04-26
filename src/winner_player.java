import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class winner_player extends JFrame {

    JLabel lbl = new JLabel("",SwingConstants.CENTER);

    public winner_player() {
        setTitle("Winner Player");
        setSize(300,200);
        setLocationRelativeTo(null);

        lbl.setFont(new Font("Arial",Font.BOLD,14));
        add(lbl);

        check();
        setVisible(true);
    }

    void check() {
        try (Connection con = db.getConnection()) {

            ResultSet rs = con.prepareStatement(
                "SELECT COUNT(*) as c FROM players"
            ).executeQuery();

            rs.next();

            if (rs.getInt("c") != 1) {
                lbl.setText("No Winner");
                return;
            }

            ResultSet rs2 = con.prepareStatement(
                "SELECT * FROM players"
            ).executeQuery();

            rs2.next();

            lbl.setText("<html>"+rs2.getString("name")+"<br>"+rs2.getString("team_name")+"</html>");

        } catch(Exception e){
            lbl.setText("Error");
        }
    }
}