import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.*;
import java.io.IOException;
import static java.lang.Math.abs;

public class Main {
    public static void main(String[] args) throws IOException {
        int MaxPressure = 0;
        double MinDiffTemp = 1000;
        int ArrMaxPressure[] = new int[5];
        double ArrMornTemp[] = new double[5];
        double ArrNightTemp[] = new double[5];
        double ArrDiffTemp[] = new double[5];
        System.out.println("Данные определены для г. Санкт-Петербург, широта=59.8322 и долгота=30.4122."); // Приветствие
        String pogoda ="https://api.openweathermap.org/data/2.5/onecall?lat=59.8322&lon=30.4122&exclude=minutely&appid=189f2ab04090629c35023a9a2a2fb377"; //ссылка для запроса
        /*оформление запроса*/
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet request = new HttpGet(pogoda);
            // add request headers
            request.addHeader("custom-key", "mkyong");
            request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");
            CloseableHttpResponse response = httpClient.execute(request);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    /*обработка ответа*/
                    JSONObject weatherJsonObject = new JSONObject(result);
                    JSONArray weather = weatherJsonObject.getJSONArray("daily");

                    for (int i = 0; i < 5; i++) {
                        JSONObject detail = weather.getJSONObject(i);
                        JSONObject weathertemp = detail.getJSONObject("temp");
                        ArrMaxPressure[i] = detail.getInt("pressure");//собираем массив давления за 5 дей
                        ArrMornTemp[i] = weathertemp.getDouble("morn")- 273.15; //собираем массив утренней температуры и переводим в цельсий
                        ArrNightTemp[i] = weathertemp.getDouble("night") - 273.15; //собираем массив ночной температуры и переводим в цельсий
                        ArrDiffTemp[i]=abs(ArrMornTemp[i]-ArrNightTemp[i]); //сразу делаем масив с разницей в температурах
                    }
                    /*Находим макисмальное давление*/
                    for (int j = 0; j < 5; j++) {
                        if (ArrMaxPressure[j]> MaxPressure) {
                            MaxPressure = ArrMaxPressure[j];
                        }
                    }
                    System.out.println("Максимальное давление за предстоящие 5 дней: " + MaxPressure+".");
                    /*Определяем минимальное значение разниц температур***/
                    for (int m = 0; m < 5; m++) {
                        if (ArrDiffTemp[m]< MinDiffTemp) {
                            MinDiffTemp = ArrDiffTemp[m];
                        }
                    }
                    /*Определяем дни с миимальной разницей*/
                    for (int n = 0; n < 5; n++) {
                        if (ArrDiffTemp[n]== MinDiffTemp) {
                            if (n==0) {
                                System.out.println("День с минимальной разницей между ночной и утренней  температурой: сегодня (ночью - " + ArrNightTemp[n] + ", утром - " + ArrMornTemp[n]+").");
                            }else{
                                System.out.println("День с минимальной разницей между ночной и утренней  температурой: " + n + " (ночью - " + ArrNightTemp[n] + ", утром - " + ArrMornTemp[n]+").");
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }
}
