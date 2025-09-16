// src/test/java/common/ApiBaseTest.java

package common;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class ApiBaseTest {

    protected static String authToken;
    protected static int userId;
    protected static String tapSessionId;
    protected static String deviceId;
    protected static String tapDshan;
    protected static String sessionCookie;
    protected static String mobileNumber;
    protected static String tapAuth;

    protected static Map<String, String> commonHeaders = new HashMap<>();
    protected static RequestSpecification requestSpec;

    @BeforeClass(alwaysRun = true)
    public void setup() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RestAssured.baseURI = properties.getProperty("base.url");

        commonHeaders.put("Content-Type", "application/json");
        commonHeaders.put("Origin", "https://stage.getultra.club");
        commonHeaders.put("Accept", "*/*");
        commonHeaders.put("X-Client-Type", "Web Investor App");
        commonHeaders.put("X-Device-Type", "Desktop");
        commonHeaders.put("X-OS-Type", "Mac OS");
        commonHeaders.put("X-OS-Version", "10.15.7");
        commonHeaders.put("X-Device-Model", "Macintosh");
        commonHeaders.put("X-Platform-Request", "WEB_READ");
        commonHeaders.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36");
        commonHeaders.put("X-App-Id", "ULTRA");

        // Shared request spec with default headers for all tests
        requestSpec = RestAssured.given().headers(commonHeaders);

        // Try to load existing user/session from session_data.json at project root
        try {
            File sessionFile = new File("session_data.json");
            if (sessionFile.exists()) {
                String content = Files.readString(sessionFile.toPath(), StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(content);
                authToken = json.optString("X-Tap-Auth", authToken);
                tapSessionId = json.optString("X-Tap-Session", tapSessionId);
                deviceId = json.optString("X-Device-Id", deviceId);
                tapDshan = json.optString("X-Tap-Dshan", tapDshan);
                sessionCookie = json.optString("_session", sessionCookie);
                userId = json.optInt("X-User-Id", userId);
            }
        } catch (Exception ignore) { }
    }
}