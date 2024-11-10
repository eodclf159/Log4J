import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.naming.*;
import javax.naming.directory.*;

public class Login {
    	private static final Logger logger = LogManager.getLogger(Login.class);
	
	public static void main(String[] args) throws IOException {
		int port = 8081;
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("JAVA 서버 구동중 [포트 : " + port + "] ");
		
		while (true) {
			try (Socket clientSocket = serverSocket.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
				
				String line;
				StringBuilder request = new StringBuilder();
				int contentLength = 0;
				
				while ((line = in.readLine()) != null && !line.isEmpty()) {
					request.append(line).append("\n");
					if (line.startsWith("Content-Length:")) {
						contentLength = Integer.parseInt(line.split(":")[1].trim());
					}
				}
				
				char[] body = new char[contentLength];
				in.read(body);
				String requestBody = new String(body);
				
				Map<String, String> params = parseRequestParams(requestBody);
				String username = params.get("username");
				String password = params.get("password");
				
				if (username != null && password != null) {
					logger.info("Login attempt - Username: " + username + ", Password: " + password);
					
					if (validateLogin(username, password)) {
						out.println("HTTP/1.1 200 OK\r\n");
						out.println("welcome!! Login Success!!!!");
					} else {
						logger.warn("아이디나 패스워드 값이 틀렸다!");
						out.println("HTTP/1.1 401 Unauthorized\r\n");
						out.println("Check your username or password");
					}
				} else {
					logger.warn("아이디나 패스워드 값이 존재하지 않음");
					
					out.println("HTTP/1.1 200 OK\r\n");
					out.println("NOT Found your username or password");
				}
				
				clientSocket.close();
			} catch (IOException e) {
				logger.error("사용자 요청 처리중 에러 발생", e);
			}
		}
	}
	
	private static boolean validateLogin(String username, String password) {
		String jdbcUrl = "jdbc:mysql://localhost:3306/test";
		String dbUsername = "root";
		String dbPassword = "test";
		
		String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
		
		try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return true;
				}
			}
		} catch (SQLException e) {
			logger.error("데이터 베이스 연결 실패 또는 쿼리에 문제가 있음", e);
		}
		
		return false;
	}
	
	
	
	private static Map<String, String> parseRequestParams(String body) {
		Map<String, String> params = new HashMap<>();
		String[] pairs = body.split("&");
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			if (keyValue.length > 1) {
				params.put(keyValue[0], keyValue[1]);
			}
		}
		return params;
	}
}

