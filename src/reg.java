import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class reg extends JFrame implements ActionListener {
    JTextField txtName   = new JTextField();
    JTextField txtGmail  = new JTextField();
    JTextField txtMobile = new JTextField();
    JTextField txtTeam   = new JTextField();
    JButton    btnReg    = new JButton("Register");
    JButton    btnClear  = new JButton("Clear");

    public reg() {
        setTitle("Register Player");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        String[] labels = {"Name:", "Gmail:", "Mobile:", "Team Name:"};
        JTextField[] fields = {txtName, txtGmail, txtMobile, txtTeam};

        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]);
            l.setBounds(40, 20 + i * 45, 90, 25);
            fields[i].setBounds(140, 20 + i * 45, 210, 25);
            add(l); add(fields[i]);
        }

        btnReg.setBounds(90, 210, 100, 30);
        btnClear.setBounds(210, 210, 80, 30);
        btnReg.addActionListener(this);
        btnClear.addActionListener(this);
        add(btnReg); add(btnClear);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClear) {
            txtName.setText(""); txtGmail.setText("");
            txtMobile.setText(""); txtTeam.setText("");
            return;
        }

        String name   = txtName.getText().trim();
        String gmail  = txtGmail.getText().trim();
        String mobile = txtMobile.getText().trim();
        String team   = txtTeam.getText().trim();

        if (name.isEmpty() || gmail.isEmpty() || mobile.isEmpty() || team.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required."); return;
        }
        if (!gmail.endsWith("@gmail.com")) {
            JOptionPane.showMessageDialog(this, "Enter a valid Gmail."); return;
        }
        if (!mobile.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Mobile must be 10 digits."); return;
        }

        try {
            Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO players(name,gmail,mobile,team_name) VALUES(?,?,?,?)");
            ps.setString(1, name); ps.setString(2, gmail);
            ps.setString(3, mobile); ps.setString(4, team);
            ps.executeUpdate();
            con.close();
            JOptionPane.showMessageDialog(this, name + " registered!");
            txtName.setText(""); txtGmail.setText("");
            txtMobile.setText(""); txtTeam.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }
}