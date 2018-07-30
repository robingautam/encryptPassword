package demo;

import java.io.IOException;
import java.io.PrintWriter;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.crypto.Cipher;
//import javax.crypto.NoSuchPaddingException;
//import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;

//import com.mysql.jdbc.util.Base64Decoder;

@WebServlet("/securityDemo")
public class securityDemo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		     String AlgoName = "AES";
		     String keyString = "Desire_SecretKey";
		     PrintWriter out = response.getWriter();
		     String password = request.getParameter("password");
		  try {
			  //encryption of  the password
			 SecretKeySpec skeySpec = new SecretKeySpec(keyString.getBytes(), AlgoName);
		     Cipher cipher = Cipher.getInstance(AlgoName);
		        
		     cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		        
		      byte[] encrypted = cipher.doFinal(password.getBytes());
		      String encpass = Base64.encodeBase64String(encrypted);
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "");
			PreparedStatement pst = conn.prepareStatement("insert into security(password) values (?)");
			pst.setString(1, encpass);
			int i = pst.executeUpdate();
			if (i!=0) {
				out.println("password has been inserted");
			}
			else {
				out.print("password not inserted");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			out.println(e);
		}
		  try {
			  Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "");
				Statement stat = conn.createStatement();
				ResultSet rs = stat.executeQuery("select * from security");
				while (rs.next()) {
					// decryption of the password
					String pass = rs.getString("password");
					 SecretKeySpec skeySpec = new SecretKeySpec(keyString.getBytes(), AlgoName);
				        Cipher cipher = Cipher.getInstance(AlgoName);
				        
				        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
				        byte[] decodedValue = new Base64().decode(pass.getBytes());
				        byte[] fpass = cipher.doFinal(decodedValue);
				        String decodepassword = new String(fpass);
				        out.println(decodepassword);
				      
				}
				 
			  
		  }
		  catch(Exception e) {
			    out.println("some problem in fetching the password "+e);
		  }
		
	}

}
