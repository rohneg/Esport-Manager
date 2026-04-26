import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

public class dash extends JFrame implements ActionListener {

    JButton btnRegSolo = new JButton("Register Player");
    JButton btnRegTeam = new JButton("Register Team");
    JButton btnViewSolo = new JButton("View Players");
    JButton btnViewTeams = new JButton("View Teams");
    JButton btnCSV = new JButton("Import CSV");   // ✅ restored
    JButton btnWinnerTeam = new JButton("Winner Team");
    JButton btnWinnerPlayer = new JButton("Winner Player");
    JButton btnLogout = new JButton("Logout");

    public dash(String id) {
        setTitle("Dashboard - " + id);
        setSize(400, 480);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        int x = 100, y = 30, w = 180, h = 30, gap = 40;

        JButton[] btns = {
            btnRegSolo, btnRegTeam, btnViewSolo,
            btnViewTeams, btnCSV, btnWinnerTeam,
            btnWinnerPlayer, btnLogout
        };

        for (JButton b : btns) {
            b.setBounds(x, y, w, h);
            add(b);
            b.addActionListener(this);
            y += gap;
        }

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();

        if (s == btnRegSolo) new reg();
        else if (s == btnRegTeam) new teamreg();
        else if (s == btnViewSolo) new view();
        else if (s == btnViewTeams) new viewteams();
        else if (s == btnCSV) importCSV();  // ✅ restored
        else if (s == btnWinnerTeam) new winner_team();
        else if (s == btnWinnerPlayer) new winner_player();
        else if (s == btnLogout) {
            new login();
            dispose();
        }
    }

    // ✅ same old CSV method
    private void importCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        int count = 0;

        try (
            BufferedReader br = new BufferedReader(new FileReader(fc.getSelectedFile()));
            Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO players(name,gmail,mobile,team_name) VALUES(?,?,?,?)"
            )
        ) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] c = line.split(",");
                if (c.length < 5) continue;

                ps.setString(1, c[1].trim().replace("\"", ""));
                ps.setString(2, c[2].trim().replace("\"", ""));
                ps.setString(3, c[3].trim().replaceAll("[^0-9]", ""));
                ps.setString(4, c[4].trim().replace("\"", ""));
                ps.executeUpdate();
                count++;
            }

            JOptionPane.showMessageDialog(this, count + " players imported.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}