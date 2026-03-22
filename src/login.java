import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class login extends JFrame implements ActionListener {
    JTextField     txtId   = new JTextField();
    JPasswordField txtPass = new JPasswordField();
    JButton        btnLogin = new JButton("Login");

    public login() {
        setTitle("Esports Manager - Login");
        setSize(380, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        JLabel l1 = new JLabel("Organizer ID:");
        JLabel l2 = new JLabel("Password:");
        l1.setBounds(50, 40, 100, 25);
        l2.setBounds(50, 85, 100, 25);
        txtId.setBounds(155, 40, 170, 25);
        txtPass.setBounds(155, 85, 170, 25);
        btnLogin.setBounds(135, 135, 100, 30);

        btnLogin.addActionListener(this);
        txtPass.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) btnLogin.doClick();
            }
        });

        add(l1); add(l2); add(txtId); add(txtPass); add(btnLogin);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String id   = txtId.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();

        if (id.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter ID and Password.");
            return;
        }

        try {
            Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM organizers WHERE org_id=? AND password=?");
            ps.setString(1, id);
            ps.setString(2, pass);
            if (ps.executeQuery().next()) {
                new dash(id);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                txtPass.setText("");
            }
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new login();
    }
}