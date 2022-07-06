
import org.jsoup.Jsoup;
import org.openqa.selenium.Cookie;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FilmParser {

    public byte[] postData;
    Properties properties;
    java.net.CookieManager cookieManager;
    private final String USER_AGENT = "Mozilla/5.0";
    static final String COOKIES_HEADER = "Set-Cookie";
    FilmParser() throws IOException {
        this.properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/utils.properties"));
        String parameters = String.format(
                "username=%s&password=%s",
                properties.getProperty("USERNAME"),
                properties.getProperty("PASSWORD")
        );
        postData = parameters.getBytes(StandardCharsets.UTF_8);
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);


    }
    public void sendPost() throws Exception {
        String address = properties.getProperty("film.authAddress");
        try {
            URL url = new URL(address);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type","application/json; charset=utf-8");
            httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Accept", "*/*");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.connect();
            try {OutputStream os = httpURLConnection.getOutputStream();
                os.write(postData);
            }catch (Exception e){
                System.err.println(e.getMessage());
            }
            if( HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode() ){
                Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        cookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
                    }
                    CookieStore cookieStore = cookieManager.getCookieStore();
                    sendGet();
                }
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }
    public void sendGet() throws Exception {
        String address = properties.getProperty("film.profilesAddress");
        try {
            URL url = new URL(address);
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type","application/json; charset=utf-8");
            httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Accept", "*/*");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.connect();
            if(HttpURLConnection.HTTP_OK==httpURLConnection.getResponseCode()) {
                try {InputStream inputStream=httpURLConnection.getInputStream();
                    InputStreamReader reader=new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    BufferedReader bufferedReader=new BufferedReader(reader);
                    StringBuffer stringBuffer=new StringBuffer();
                    String line=null;
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuffer.append(line);
                    }
                    reader.close();
                    System.out.println(Jsoup.parse(stringBuffer.toString()));
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
    }
}

