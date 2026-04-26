import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class viewteams extends JFrame {

    DefaultTableModel model = new DefaultTableModel(
        new String[]{"ID","Team","Pts","+","-","Name","Gmail","Mobile"},0
    ){
        public boolean isCellEditable(int r,int c){
            return c==3 || c==4;
        }
    };

    JTable table = new JTable(model);

    public viewteams(){

        setTitle("Teams View");
        setSize(950,500);
        setLayout(null);
        setLocationRelativeTo(null);

        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){

                super.getTableCellRendererComponent(t,v,s,f,r,c);

                int group = r/4;

                if(s){
                    setBackground(new Color(0,70,140));
                    setForeground(Color.WHITE);
                }else{
                    if(group%2==0)
                        setBackground(new Color(255,235,235));
                    else
                        setBackground(new Color(235,235,255));
                    setForeground(Color.BLACK);
                }

                if(r%4!=0 && c<=4) setText("");

                if(c<=4)
                    setHorizontalAlignment(SwingConstants.CENTER);
                else
                    setHorizontalAlignment(SwingConstants.LEFT);

                return this;
            }
        });

        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                int row = table.rowAtPoint(e.getPoint());
                int start = (row/4)*4;

                if(!e.isControlDown())
                    table.clearSelection();

                table.addRowSelectionInterval(start,start+3);
            }
        });

        table.getColumn("+").setCellRenderer(new Btn("+"));
        table.getColumn("-").setCellRenderer(new Btn("-"));

        table.getColumn("+").setCellEditor(new Edit("+"));
        table.getColumn("-").setCellEditor(new Edit("-"));

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10,10,920,380);
        add(sp);

        JButton del = new JButton("Delete");
        del.setBounds(10,400,120,30);
        add(del);

        JButton refresh = new JButton("Refresh");
        refresh.setBounds(140,400,120,30);
        add(refresh);

        del.addActionListener(e->deleteTeam());
        refresh.addActionListener(e->{
            table.clearSelection();
            load();
        });

        load();
        setVisible(true);
    }

    void load(){

        try(Connection con=db.getConnection()){

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT * FROM team_data ORDER BY team_name,id"
            );

            model.setRowCount(0);

            String prev = null;
            int id = 1;

            while(rs.next()){

                String team = rs.getString("team_name");

                if(prev == null || !team.equals(prev)){
                    model.addRow(new Object[]{
                        id++,
                        team,
                        getPoints(con, team),
                        "+","-",
                        rs.getString("player_name"),
                        rs.getString("gmail"),
                        rs.getString("mobile")
                    });
                    prev = team;
                }else{
                    model.addRow(new Object[]{
                        "","","","","",
                        rs.getString("player_name"),
                        rs.getString("gmail"),
                        rs.getString("mobile")
                    });
                }
            }

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"DB Error");
        }
    }

    int getPoints(Connection con,String team) throws Exception{
        ResultSet rs = con.createStatement().executeQuery(
            "SELECT SUM(points) FROM team_data WHERE team_name='"+team+"'"
        );
        rs.next();
        return rs.getInt(1);
    }

    void update(int row,int d){

        String team = table.getValueAt((row/4)*4,1).toString();

        try(Connection con=db.getConnection()){

            PreparedStatement ps;

            if(d > 0){
                ps = con.prepareStatement(
                    "UPDATE team_data SET points = points + 1 " +
                    "WHERE id = (SELECT id FROM (SELECT id FROM team_data WHERE team_name=? LIMIT 1) t)"
                );
            }else{
                ps = con.prepareStatement(
                    "UPDATE team_data SET points = points - 1 " +
                    "WHERE id = (SELECT id FROM (SELECT id FROM team_data WHERE team_name=? AND points > 0 LIMIT 1) t)"
                );
            }

            ps.setString(1, team);
            ps.executeUpdate();

            load();

        }catch(Exception e){}
    }

    void deleteTeam(){

        int[] rows = table.getSelectedRows();
        if(rows.length==0) return;

        HashSet<String> teams = new HashSet<>();

        for(int r:rows){
            String team = table.getValueAt((r/4)*4,1).toString();
            teams.add(team);
        }

        try(Connection con=db.getConnection()){
            for(String t:teams){
                con.createStatement().executeUpdate(
                    "DELETE FROM team_data WHERE team_name='"+t+"'"
                );
            }

            ResultSet rs = con.createStatement().executeQuery("SELECT COUNT(*) FROM team_data");
            rs.next();

            if(rs.getInt(1)==0){
                con.createStatement().executeUpdate("ALTER TABLE team_data AUTO_INCREMENT = 1");
            }

            load();

        }catch(Exception e){}
    }

    class Btn extends JButton implements TableCellRenderer{

        String type;

        Btn(String t){
            type = t;
            setText(t);
            setForeground(Color.WHITE);
        }

        public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){

            if(r % 4 != 0) return new JLabel("");

            setBackground(type.equals("+") ? new Color(0,150,0) : new Color(180,40,40));

            return this;
        }
    }

    class Edit extends DefaultCellEditor{
        int row;
        String type;

        Edit(String t){
            super(new JCheckBox());
            type=t;

            JButton b=new JButton(t);
            b.addActionListener(e->{
                fireEditingStopped();
                update(row,type.equals("+")?1:-1);
            });

            editorComponent=b;
        }

        public Component getTableCellEditorComponent(JTable t,Object v,boolean s,int r,int c){
            row=r;
            return editorComponent;
        }

        public Object getCellEditorValue(){
            return type;
        }
    }
}