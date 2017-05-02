import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.*;
import java.sql.*;

/**
 * Created by Vladik on 01.05.2017.
 */
public class ConnectionToDB {
    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/images", "root", "root");
        Mat imagine = Imgcodecs.imread("ip1.jpg");
        Mat imagine2 = Imgcodecs.imread("ip2.jpg");
        Images imageWrite = new Images(imagine, new MatOfKeyPoint(), imagine2);
        try {
            FileOutputStream fileOut = new FileOutputStream("images.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(imageWrite);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in images.ser");
        }catch(IOException i) {
            i.printStackTrace();
        }

        FileInputStream fis = new FileInputStream("images.ser");
        PreparedStatement statement = connection.prepareStatement("insert into images (dataImage, idUser) VALUES (?, ?)");
        statement.setBinaryStream(1, fis);
        statement.setInt(2, 3);
        statement.executeUpdate();

        Statement stat = connection.createStatement();
        ResultSet resultSet = stat.executeQuery("select dataImage from images.images");
        resultSet.next();
        ObjectInputStream in = new ObjectInputStream( resultSet.getBlob("dataImage").getBinaryStream());
        Images image = (Images) in.readObject();

        Mat writeImage = image.getDescriptor();
        Imgcodecs.imwrite("YEEEEEEEEEEEEEEEEEEEEE.jpg", writeImage);
        System.out.println();

        resultSet.close();
        stat.close();
        connection.close();
    }
}
