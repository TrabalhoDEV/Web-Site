package com.example.schoolservlet.daos;

import com.example.schoolservlet.daos.interfaces.GenericDAO;
import com.example.schoolservlet.models.Admin;
import com.example.schoolservlet.utils.PostgreConnection;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AdminDAO implements GenericDAO<Admin>{
    @Override
    public Admin findById(int id){
        Admin admin = null;

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM admin WHERE id = ?")){
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setEmail(rs.getString("email"));
                admin.setDocument(rs.getString("document"));
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return admin;
    }

    @Override
    public Map<Integer, Admin> findMany(int skip, int take){
        Map<Integer, Admin> admins = new HashMap<>();

        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM admin ORDER BY id LIMIT ? OFFSET ?")){
            pstmt.setInt(1, take);
            pstmt.setInt(2, skip);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setEmail(rs.getString("email"));
                admin.setDocument(rs.getString("document"));

                admins.put(rs.getInt("id"), admin);
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return admins;
    }

    @Override
    public int totalCount(){
        int totalCount = -1;

        try(Connection conn = PostgreConnection.getConnection();
            Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalCount FROM admin");

            if (rs.next()){
                totalCount = rs.getInt("totalCount");
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }

        return totalCount;
    }

    @Override
    public boolean create(Admin admin){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO admin (document, email, password) VALUES (?, ?,?)")){
            pstmt.setString(1, admin.getDocument());
            pstmt.setString(2, admin.getEmail());
            pstmt.setString(3, BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt()));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean update(Admin admin){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE admin SET document = ?, email = ? WHERE id = ?")){
            pstmt.setString(1, admin.getDocument());
            pstmt.setString(2, admin.getEmail());
            pstmt.setInt(3, admin.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }
    public boolean updatePassword(String email, String newPassword){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE admin SET password = ? WHERE email = ?")){
            pstmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            pstmt.setString(2, email);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM admin WHERE id  = ?")){
            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
    }

    public boolean login(String document, String password){
        try(Connection conn = PostgreConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT document, password FROM admin WHERE document = ?")){
            pstmt.setString(1, document);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                String hash = rs.getString("password");
                return BCrypt.checkpw(password, hash);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return false;
    }
}
