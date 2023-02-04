import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;


public class UserForm extends JDialog {

    private JTable jtMovies;
    private JPanel UserPanel;
    private JButton btnSearchMovie;
    private JTextField pfMovieName;
    private JLabel Username;
    private JTable jtRecomandation;

    public UserForm(JFrame parent, String uid, String username) {
        super(parent);
        setTitle("User");
        setContentPane(UserPanel);
        setMinimumSize(new Dimension(750, 700));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        String UserID = uid;
        Username.setText(username);
        showmovies();
        recomandation(username);

        btnSearchMovie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchmovie(UserID);
            }
        });

        jtRecomandation.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = jtRecomandation.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) jtRecomandation.getModel();
                pfMovieName.setText(model.getValueAt(i, 0).toString());
            }
        });
        jtMovies.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = jtMovies.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) jtMovies.getModel();
                pfMovieName.setText(model.getValueAt(i, 0).toString());
            }
        });
        setVisible(true);
    }


    private void recomandation(String username) {
        ArrayList<String> movies = new ArrayList<>();
        movies = getMovies(username);
        String[] column = {"Movies"};
        DefaultTableModel model = (DefaultTableModel) jtRecomandation.getModel();
        model.setColumnIdentifiers(column);

        for (String movie : movies) {
            String[] row = {movie};
            model.addRow(row);
        }
    }

    private void showmovies() {

        final String DB_URL = "jdbc:mysql://localhost/movieapp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();

            String sql = "Select * from movies";
            ResultSet resultSet = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            DefaultTableModel model = (DefaultTableModel) jtMovies.getModel();

            int cols = rsmd.getColumnCount();
            String[] colName = new String[4];
            colName[0] = rsmd.getColumnName(2);
            colName[1] = rsmd.getColumnName(3);
            colName[2] = rsmd.getColumnName(4);
            colName[3] = rsmd.getColumnName(6);

            model.setColumnIdentifiers(colName);
            String moviename, gender, director;
            String releasedate;

            while ((resultSet.next())) {
                moviename = resultSet.getString(2);
                gender = resultSet.getString(3);
                director = resultSet.getString(4);
                releasedate = resultSet.getString(6);

                String[] row = {moviename, gender, director, releasedate};
                model.addRow(row);
            }

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getMovies(String username) {

        final String DB_URL = "jdbc:mysql://localhost/movieapp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        ArrayList<String> movies = new ArrayList<>();
        ArrayList<String> genders = genderlikes(username);

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            String sql = "SELECT * FROM movies WHERE gender = ? AND likes > 500 AND id NOT IN (SELECT movieid FROM likes WHERE userid = (SELECT id FROM users WHERE username = ?)) ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(2, username);
            for (String gender : genders) {
                preparedStatement.setString(1, gender);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    String moviename;
                    while (resultSet.next()) {
                        moviename = resultSet.getString(2);
                        System.out.println(moviename);
                        movies.add(moviename);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    public ArrayList<String> genderlikes(String username) {
        ArrayList<String> res = new ArrayList<>();
        final String DB_URL = "jdbc:mysql://localhost/movieapp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";
        try {

            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "SELECT DISTINCT movies.gender FROM movies JOIN likes ON movies.id = likes.movieId JOIN users ON likes.userId = users.id WHERE users.username =? ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            while ((resultSet.next())) {
                String gender = resultSet.getString(1);
                System.out.println(gender);
                res.add(gender);
            }
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private void searchmovie(String UserID) {
        String moviename = pfMovieName.getText();

        final String DB_URL = "jdbc:mysql://localhost/movieapp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM movies WHERE moviename=? ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, moviename);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                MovieForm movieForm = new MovieForm(null,
                        UserID,
                        resultSet.getString("id"),
                        resultSet.getString("moviename"),
                        resultSet.getString("gender"),
                        resultSet.getString("director"),
                        resultSet.getString("studio"),
                        resultSet.getString("date"));
                movieForm.setVisible(true);
                dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
