import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class view extends JFrame {

    DefaultTableModel model = new DefaultTableModel(
        new String[]{"ID","Points","+","-","Name","Gmail","Mobile","Team","Registered At"}, 0) {
        public boolean isCellEditable(int r, int c) { return c == 2 || c == 3; }
    };
    JTable     table    = new JTable(model);
    JLabel     lblCount = new JLabel("Total: 0");
    JTextField txtSearch = new JTextField();

    public view() {
        setTitle("Registered Players");
        setSize(900, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        JLabel ls = new JLabel("Search:");
        ls.setBounds(15, 15, 55, 25);
        txtSearch.setBounds(70, 15, 180, 25);

        JButton btnSearch  = new JButton("Search");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete  = new JButton("Delete");
        btnSearch.setBounds(258, 15, 85, 25);
        btnRefresh.setBounds(350, 15, 85, 25);
        btnDelete.setBounds(442, 15, 85, 25);

        btnSearch.addActionListener(e -> search(txtSearch.getText().trim()));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); load(); });
        btnDelete.addActionListener(e -> delete());

        add(ls); add(txtSearch); add(btnSearch); add(btnRefresh); add(btnDelete);

        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setSelectionBackground(new Color(180, 210, 255));

        int[] widths = {35, 55, 35, 35, 120, 170, 90, 100, 140};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getColumn("+").setCellRenderer(new BtnRenderer("+"));
        table.getColumn("-").setCellRenderer(new BtnRenderer("-"));
        table.getColumn("+").setCellEditor(new BtnEditor("+"));
        table.getColumn("-").setCellEditor(new BtnEditor("-"));

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(15, 50, 865, 330);
        add(sp);

        lblCount.setBounds(15, 392, 200, 22);
        lblCount.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lblCount);

        load();
        setVisible(true);
    }

    void load() {
        model.setRowCount(0);
        try {
            Connection con = db.getConnection();
            ResultSet rs = con.prepareStatement("SELECT * FROM players ORDER BY id ASC").executeQuery();
            int n = 0;
            while (rs.next()) {
                n++;
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getInt("points"), "+", "-",
                    rs.getString("name"), rs.getString("gmail"),
                    rs.getString("mobile"), rs.getString("team_name"),
                    rs.getString("registered_at")
                });
            }
            lblCount.setText("Total: " + n + " player(s)");
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    private void search(String kw) {
        if (kw.isEmpty()) { load(); return; }
        model.setRowCount(0);
        try {
            Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM players WHERE name LIKE ? OR team_name LIKE ? ORDER BY id ASC");
            ps.setString(1, "%" + kw + "%"); ps.setString(2, "%" + kw + "%");
            ResultSet rs = ps.executeQuery();
            int n = 0;
            while (rs.next()) {
                n++;
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getInt("points"), "+", "-",
                    rs.getString("name"), rs.getString("gmail"),
                    rs.getString("mobile"), rs.getString("team_name"),
                    rs.getString("registered_at")
                });
            }
            lblCount.setText("Found: " + n + " player(s)");
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 4);
        if (JOptionPane.showConfirmDialog(this, "Delete " + name + "?") != 0) return;
        try {
            Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM players WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            con.prepareStatement("ALTER TABLE players AUTO_INCREMENT = 1").executeUpdate();
            con.close();
            load();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    void updatePoints(int row, int delta) {
        int id  = (int) model.getValueAt(row, 0);
        int pts = Math.max(0, (int) model.getValueAt(row, 1) + delta);
        try {
            Connection con = db.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE players SET points=? WHERE id=?");
            ps.setInt(1, pts); ps.setInt(2, id);
            ps.executeUpdate();
            con.close();
            model.setValueAt(pts, row, 1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    class BtnRenderer extends JButton implements TableCellRenderer {
        BtnRenderer(String lbl) {
            setText(lbl);
            setFont(new Font("Arial", Font.BOLD, 12));
            setFocusPainted(false);
            setBackground(lbl.equals("+") ? new Color(34, 139, 34) : new Color(180, 40, 40));
            setForeground(Color.WHITE);
        }
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean s, boolean f, int r, int c) { return this; }
    }

    class BtnEditor extends DefaultCellEditor {
        private int currentRow;
        private String lbl;

        BtnEditor(String lbl) {
            super(new JCheckBox());
            this.lbl = lbl;
            JButton btn = new JButton(lbl);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setBackground(lbl.equals("+") ? new Color(34, 139, 34) : new Color(180, 40, 40));
            btn.setForeground(Color.WHITE);
            btn.addActionListener(e -> {
                fireEditingStopped();
                updatePoints(currentRow, lbl.equals("+") ? 1 : -1);
            });
            editorComponent = btn;
        }

        public Component getTableCellEditorComponent(JTable t, Object v,
                boolean s, int row, int col) { currentRow = row; return editorComponent; }

        public Object getCellEditorValue() { return lbl; }
    }
}