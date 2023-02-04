import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class MovieForm extends JDialog{
    private JLabel MovieName;
    private JLabel Gender;
    private JLabel Director;
    private JLabel Studio;
    private JLabel ReleaseDate;
    private JPanel MoviePanel;
    private JButton btnLike;

    public MovieForm(JFrame parent,String USERID,String MOVIEID,String moviename,String gender,String director,String studio,String releasedate) {
        super(parent);
        setTitle("Movie");
        setContentPane(MoviePanel);
        setMinimumSize(new Dimension(750,745));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        MovieName.setText(moviename);
        Gender.setText(gender);
        Director.setText(director);
        Studio.setText(studio);
        ReleaseDate.setText(releasedate);

        btnLike.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userlikemovie(USERID,MOVIEID,gender);
            }
        });

    }

    private void userlikemovie(String userid, String movieid,String  gender) {
        final String DB_URL = "jdbc:mysql://localhost/movieapp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO likes (USERID,MOVIEID)" +
                    "VALUE (?,?)";
            PreparedStatement preparedStatement= conn.prepareStatement(sql);
            preparedStatement.setString(1,userid);
            preparedStatement.setString(2,movieid);


            int addedRows = preparedStatement.executeUpdate();

            stmt.close();
            conn.close();
            JOptionPane.showConfirmDialog(this,
                    "You Like This Movie","",JOptionPane.OK_CANCEL_OPTION);

    }catch(Exception e) {
        e.printStackTrace();
        }
    }

}
