import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class view extends JFrame 
{

    DefaultTableModel model = new DefaultTableModel(
        new String[]{"ID","Points","+","-","Name","Gmail","Mobile","Team","Registered At"}, 0) {
        public boolean isCellEditable(int r, int c) { return c == 2 || c == 3; }
    };
    JTable table = new JTable(model);
    JLabel lblCount = new JLabel("Total: 0");
    JTextField txtSearch = new JTextField();

    public view() 
    {
        setTitle("Registered Players");
        setSize(900, 470);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JLabel ls = new JLabel("Search:"), lblHint = new JLabel();
        JButton btnSearch = new JButton("Search"), btnRefresh = new JButton("Refresh"), btnDelete = new JButton("Delete");

        ls.setBounds(15, 15, 55, 25);
        txtSearch.setBounds(70, 15, 180, 25);
        btnSearch.setBounds(258, 15, 85, 25);
        btnRefresh.setBounds(350, 15, 85, 25);
        btnDelete.setBounds(442, 15, 85, 25);
        lblHint.setBounds(575, 18, 300, 18);
        lblCount.setBounds(15, 412, 250, 22);

        btnSearch.addActionListener(e -> search(txtSearch.getText().trim()));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); load(); });
        btnDelete.addActionListener(e -> delete());

        lblHint.setFont(new Font("Arial", Font.ITALIC, 10));
        lblHint.setForeground(Color.GRAY);
        lblCount.setFont(new Font("Arial", Font.PLAIN, 12));

        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setSelectionBackground(new Color(180, 210, 255));

        int[] w = {35, 55, 35, 35, 120, 170, 90, 100, 140};
        for (int i = 0; i < w.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        for (String s : new String[]{"+", "-"}) 
        {
            table.getColumn(s).setCellRenderer(new BtnRenderer(s));
            table.getColumn(s).setCellEditor(new BtnEditor(s));
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(15, 50, 865, 350);

        for (Component c : new Component[]{ls, txtSearch, btnSearch, btnRefresh, btnDelete, lblHint, sp, lblCount}) add(c);

        load();
        setVisible(true);
    }

    private Object[] row(ResultSet rs) throws SQLException 
    {
        return new Object[]{
            rs.getInt("id"), rs.getInt("points"), "+", "-",
            rs.getString("name"), rs.getString("gmail"),
            rs.getString("mobile"), rs.getString("team_name"),
            rs.getString("registered_at")
        };
    }

    private void fill(ResultSet rs, String msg) throws SQLException {
        model.setRowCount(0);
        int n = 0;
        while (rs.next()) {
            model.addRow(row(rs));
            n++;
        }
        lblCount.setText(msg + n + " player(s)" + (msg.startsWith("Total") ? "   |   Ctrl+Click to select multiple" : ""));
    }

    void load() 
    {
        try (Connection con = db.getConnection();
             ResultSet rs = con.prepareStatement("SELECT * FROM players ORDER BY id ASC").executeQuery()) {
            fill(rs, "Total: ");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    private void search(String kw) 
    {
        if (kw.isEmpty()) { load(); return; }
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT * FROM players WHERE name LIKE ? OR team_name LIKE ? ORDER BY id ASC")) {
            ps.setString(1, "%" + kw + "%");
            ps.setString(2, "%" + kw + "%");
            try (ResultSet rs = ps.executeQuery()) { fill(rs, "Found: "); }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    private void delete()
    {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select at least one player to delete.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete " + rows.length + " selected player(s)?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM players WHERE id=?")) {
            for (int i = rows.length - 1; i >= 0; i--) {
                ps.setInt(1, (int) model.getValueAt(rows[i], 0));
                ps.executeUpdate();
            }
            con.prepareStatement("ALTER TABLE players AUTO_INCREMENT = 1").executeUpdate();
            load();
            JOptionPane.showMessageDialog(this, rows.length + " player(s) deleted.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    void updatePoints(int row, int delta) 
    {
        int id = (int) model.getValueAt(row, 0), pts = Math.max(0, (int) model.getValueAt(row, 1) + delta);
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE players SET points=? WHERE id=?")) {
            ps.setInt(1, pts);
            ps.setInt(2, id);
            ps.executeUpdate();
            model.setValueAt(pts, row, 1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    class BtnRenderer extends JButton implements TableCellRenderer 
    {
        BtnRenderer(String s) {
            setText(s);
            setFont(new Font("Arial", Font.BOLD, 12));
            setFocusPainted(false);
            setBackground(s.equals("+") ? new Color(34, 139, 34) : new Color(180, 40, 40));
            setForeground(Color.WHITE);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) { return this; }
    }

    class BtnEditor extends DefaultCellEditor 
    {
        int row;
        String lbl;

        BtnEditor(String lbl) 
        {
            super(new JCheckBox());
            this.lbl = lbl;
            JButton b = new JButton(lbl);
            b.setFont(new Font("Arial", Font.BOLD, 12));
            b.setFocusPainted(false);
            b.setBackground(lbl.equals("+") ? new Color(34, 139, 34) : new Color(180, 40, 40));
            b.setForeground(Color.WHITE);
            b.addActionListener(e -> {
                fireEditingStopped();
                updatePoints(row, lbl.equals("+") ? 1 : -1);
            });
            editorComponent = b;
        }

        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            row = r;
            return editorComponent;
        }

        public Object getCellEditorValue() { 
            return lbl; 
        }
    }
}