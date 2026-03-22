import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

public class dash extends JFrame implements ActionListener 
{
    JButton btnRegSolo   = new JButton("Register Player");
    JButton btnRegTeam   = new JButton("Register Team");
    JButton btnViewSolo  = new JButton("View Players");
    JButton btnViewTeams = new JButton("View Teams");
    JButton btnCSV       = new JButton("Import CSV");
    JButton btnLogout    = new JButton("Logout");

    public dash(String id) 
    {
        setTitle("Dashboard - " + id);
        setSize(380, 370);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        JLabel lbl = new JLabel("Welcome, " + id + "!", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setBounds(0, 15, 380, 28);
        add(lbl);

        JLabel lblType = new JLabel("", SwingConstants.CENTER);
        lblType.setFont(new Font("Arial", Font.PLAIN, 11));
        lblType.setForeground(Color.GRAY);
        lblType.setBounds(0, 45, 380, 18);
        add(lblType);

        btnRegSolo.setBounds(115, 75, 150, 32);
        btnRegTeam.setBounds(115, 118, 150, 32);
        btnViewSolo.setBounds(115, 161, 150, 32);
        btnViewTeams.setBounds(115, 204, 150, 32);
        btnCSV.setBounds(115, 247, 150, 32);
        btnLogout.setBounds(115, 295, 150, 32);

        for (JButton b : new JButton[]{btnRegSolo, btnRegTeam, btnViewSolo, btnViewTeams, btnCSV, btnLogout})
            b.addActionListener(this);

        add(btnRegSolo); add(btnRegTeam);
        add(btnViewSolo); add(btnViewTeams);
        add(btnCSV); add(btnLogout);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == btnRegSolo)   new reg();
        if (e.getSource() == btnRegTeam)   new teamreg();
        if (e.getSource() == btnViewSolo)  new view();
        if (e.getSource() == btnViewTeams) new viewteams();
        if (e.getSource() == btnLogout)    { new login(); dispose(); }
        if (e.getSource() == btnCSV)       importCSV();
    }

    private void importCSV() 
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(fc.getSelectedFile()))) {
            br.readLine();
            String line;
            Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO players(name,gmail,mobile,team_name) VALUES(?,?,?,?)");
            while ((line = br.readLine()) != null) {
                String[] c = line.split(",");
                if (c.length < 5) continue;
                ps.setString(1, c[1].trim().replaceAll("\"", ""));
                ps.setString(2, c[2].trim().replaceAll("\"", ""));
                ps.setString(3, c[3].trim().replaceAll("[^0-9]", ""));
                ps.setString(4, c[4].trim().replaceAll("\"", ""));
                ps.executeUpdate();
                count++;
            }
            con.close();
            JOptionPane.showMessageDialog(this, count + " players imported.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}