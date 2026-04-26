import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class winner_team extends JFrame {

    JTextArea area = new JTextArea();

    public winner_team(){

        setTitle("Winner Team");
        setSize(400,350);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        area.setFont(new Font("Arial", Font.BOLD, 14));
        area.setEditable(false);

        add(new JScrollPane(area), BorderLayout.CENTER);

        loadWinner();

        setVisible(true);
    }

    void loadWinner(){

        try(Connection con = db.getConnection()){

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT DISTINCT team_name FROM team_data"
            );

            int count = 0;
            String team = "";

            while(rs.next()){
                team = rs.getString("team_name");
                count++;
            }

            if(count != 1){
                area.setText("No Winner");
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                "SELECT player_name FROM team_data WHERE team_name=?"
            );
            ps.setString(1, team);

            ResultSet rs2 = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            sb.append("Team: ").append(team).append("\n\n");

            int i = 1;
            while(rs2.next()){
                sb.append("Player ").append(i++).append(": ")
                  .append(rs2.getString("player_name")).append("\n");
            }

            area.setText(sb.toString());

        }catch(Exception e){
            area.setText("DB Error");
        }
    }
}