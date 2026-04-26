import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class teamreg extends JFrame implements ActionListener {

    JTextField[][] f = new JTextField[4][3];
    JTextField txtTeam = new JTextField();

    JButton btnReg = new JButton("Register Team");
    JButton btnClear = new JButton("Clear");

    public teamreg() {

        setTitle("Register Team");
        setSize(550, 350);
        setLayout(null);
        setLocationRelativeTo(null);

        int y = 20;

        add(new JLabel("Team Name")).setBounds(30, y, 100, 25);
        txtTeam.setBounds(130, y, 200, 25);
        add(txtTeam);

        y += 50;

        add(new JLabel("Player")).setBounds(30, y, 60, 25);
        add(new JLabel("Name")).setBounds(100, y, 100, 25);
        add(new JLabel("Gmail")).setBounds(210, y, 120, 25);
        add(new JLabel("Mobile")).setBounds(340, y, 100, 25);

        y += 30;

        for (int i = 0; i < 4; i++) {

            add(new JLabel("P" + (i + 1))).setBounds(30, y, 40, 25);

            for (int j = 0; j < 3; j++) {
                f[i][j] = new JTextField();
                f[i][j].setBounds(100 + (j * 110), y, 100, 25);
                add(f[i][j]);
            }

            y += 40;
        }

        btnReg.setBounds(120, y, 130, 30);
        btnClear.setBounds(280, y, 100, 30);

        add(btnReg);
        add(btnClear);

        btnReg.addActionListener(this);
        btnClear.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnClear) {
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 3; j++)
                    f[i][j].setText("");
            txtTeam.setText("");
            return;
        }

        String team = txtTeam.getText().trim();
        if (team.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter team name");
            return;
        }

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "INSERT INTO team_data(team_name,player_name,gmail,mobile) VALUES(?,?,?,?)")) {

            for (int i = 0; i < 4; i++) {

                String n = f[i][0].getText();
                String g = f[i][1].getText();
                String m = f[i][2].getText();

                if (n.isEmpty() || g.isEmpty() || m.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fill all fields");
                    return;
                }

                ps.setString(1, team);
                ps.setString(2, n);
                ps.setString(3, g);
                ps.setString(4, m);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Team Registered");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Error");
        }
    }
}