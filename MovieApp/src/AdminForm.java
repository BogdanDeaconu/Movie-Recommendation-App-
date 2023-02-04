import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import static java.lang.Integer.parseInt;

public class AdminForm extends JDialog {
    private JTextField pfMovieName;
    private JComboBox pfGender;
    private JTextField pfReleaseDate;
    private JButton btnAddMovie;
    private JPanel AdminPanel;
    private JTextField pfFilePath;
    private JButton btnAddMoviePath;
    private JTextField pfDirector;
    private JTextField pfStudio;


    public AdminForm(JFrame parent) {
        super(parent);
        setTitle("Admin");
        setContentPane(AdminPanel);
        setMinimumSize(new Dimension(750, 700));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        pfGender.addItem("Action");pfGender.addItem("Adventure");pfGender.addItem("Drama");
        pfGender.addItem("Comedy");pfGender.addItem("Anime");pfGender.addItem("Documentary");
        pfGender.addItem("Comedy-Drama");pfGender.addItem("Mystery");pfGender.addItem("Science Fiction");

        btnAddMovie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    addmovie();
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        btnAddMoviePath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    readmovies();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        setVisible(true);
    }

    private void readmovies() throws Exception {
        String filePath = pfFilePath.getText();
        if(filePath.isEmpty()){
            JOptionPane.showMessageDialog(this,
                    "Please enter file path",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        final String DB_URL = "jdbc:mysql://localhost/movieapp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        int batchsize = 20;

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            conn.setAutoCommit(false);

            String sql = "INSERT INTO movies (moviename, gender,director,studio, date, likes)"+
                    "VALUES (?,?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            BufferedReader linereader = new BufferedReader(new FileReader(filePath));
            String lineText= null;
            int count = 0;

            linereader.readLine();
            while((lineText=linereader.readLine())!= null){
                String[] data = lineText.split(",");

                String moviename=data[0];
                String gender=data[1];
                String director=data[2];
                String studio=data[3];
                String sdate=data[4];
                String likes=data[5];
                
                stmt.setString(1,moviename);
                stmt.setString(2,gender);
                stmt.setString(3,director);
                stmt.setString(4,studio);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date releasedate = sdf.parse(sdate);
                java.sql.Date date = new java.sql.Date(releasedate.getTime());
                stmt.setDate(5,date);
                stmt.setInt(6,parseInt(likes));
                stmt.addBatch();

                if(count%batchsize==0){
                    stmt.executeBatch();
                }
            }
            linereader.close();
            stmt.executeBatch();
            conn.commit();
            conn.close();
            System.out.println("Movies added succesfully");
        }
        catch(Exception exception){
            exception.printStackTrace();
        }

    }

    private void addmovie() throws ParseException {
        int likes = 0;
        String moviename = pfMovieName.getText();
        String gender = String.valueOf(pfGender.getSelectedItem());
        String director=pfDirector.getText();
        String studio=pfStudio.getText();
        String date = pfReleaseDate.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date releasedate = sdf.parse(date);

        if(moviename.isEmpty() || gender.isEmpty() || director.isEmpty() || studio.isEmpty() || date.isEmpty()){
            JOptionPane.showMessageDialog(this,
                    "Please enter all fields",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        movie = addMovieToDataBase(moviename, gender,director,studio, releasedate, likes);
        if( movie != null){
                dispose();
        }  else{
                JOptionPane.showMessageDialog(this,
                        "Failes to add Movie to DataBase",
                        "Try again",
                        JOptionPane.ERROR_MESSAGE);
        }

    }

    Movie movie;
    private Movie addMovieToDataBase(String moviename, String gender,String director,String studio, Date releasedate,int likes) {
        Movie movie = null;
        final String DB_URL = "jdbc:mysql://localhost/movieapp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        java.sql.Date date = new java.sql.Date(releasedate.getTime());
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO movies (moviename, gender,director,studio, date, likes) " +
                    "VALUES (?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, moviename);
            preparedStatement.setString(2, gender);
            preparedStatement.setString(3,director);
            preparedStatement.setString(4,studio);
            preparedStatement.setDate(5, date);
            preparedStatement.setInt(6, likes);


            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                movie = new Movie();
                movie.moviename = moviename;
                movie.gender = gender;
                movie.director=director;
                movie.studio=studio;
                movie.date = date;
                movie.likes = likes;
            }

            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return movie;
    }

    public static void main(String[ ] args){
        AdminForm myForm = new AdminForm( null);
        Movie movie = myForm.movie;
        if(movie != null){
            System.out.println("Succesful add of:" + movie.moviename);
        }
        else{
            System.out.println("Add canceled");
        }
    }
}