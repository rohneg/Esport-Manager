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
    JButton btnCSV = new JButton("Import CSV");
    JButton btnLogout = new JButton("Logout");

    public dash(String id) {
        setTitle("Dashboard - " + id);
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        JLabel lbl = new JLabel("Welcome, " + id + "!", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setBounds(0, 15, 400, 28);
        add(lbl);

        int x = 115, y = 75, w = 150, h = 32, gap = 43;

        JButton[] buttons = {
            btnRegSolo, btnRegTeam, btnViewSolo,
            btnViewTeams, btnCSV, btnLogout
        };

        for (JButton b : buttons) {
            b.setBounds(x, y, w, h);
            b.addActionListener(this);
            add(b);
            y += gap;
        }

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnRegSolo) new reg();
        else if (src == btnRegTeam) new teamreg();
        else if (src == btnViewSolo) new view();
        else if (src == btnViewTeams) new viewteams();
        else if (src == btnCSV) importCSV();
        else if (src == btnLogout) {
            new login();
            dispose();
        }
    }

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