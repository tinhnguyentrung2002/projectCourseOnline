package com.example.courseonline.util;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AccessToken {
    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";
    public String getAccessToken(){
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"courseonline-6050b\",\n" +
                    "  \"private_key_id\": \"f615899dfc86e4eca13005435fadc8f308fe153f\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDFjCjm9WZGR53v\\nJmOGbNJnXjB4u/V3lsCWODgm9CJdekOPYuGzw1c59HRZDLk/62wCl9VMfaG1Spjq\\ny/ZRQLQZrImrCrV4QbFIw8ikuDa8jaBTSi/4KtoelD+VMUtvuAQ80PiygKRMkjCH\\nnBZWXc7+lXcQ8g7R3O0XrnJP7rB0sU/jgjOH0PXeFeqcu/7aBejZDQST5ZW1i182\\nchicGEcsbwiZiboXEkV/ULtAP8hlO+cEtAQu5aWgpUAUzlYiEeE62CMDMaKXyaAM\\nhBitYJjT0xgdBKkidSQyVk7mGb0fdlx7rq8KQes8QIKQVKP5ZyPr2sRXpYqEMSLA\\n7ZGGscJxAgMBAAECggEACv91g9LWgw6iewmI1xdws6BcM/iI3wPK9yysPvTurdls\\nRfZkE4vflmJ8O2dDL/e6v2k1uYixVkRlKjCOkcb3pTSZxHvijvj0oP+vXbDuG9te\\n+TbDNWkoqV+pAN9p8E9kbatXpn5qxzgzUKAZY+0HOggBr9/oqyPbekBJbRb1hsWC\\n9MU1IzQ2sf55spbh3ThPdziORffTZlXC2k9dAnyz0cCLjKCo7AnR/zn3HWbSzgWT\\nLbP3lbw+ZtzTdR+XCnVD5EHysaFvgbstq0awAw7JF8ALvfsDu3uWXO4pCiuE7Im8\\nfzfOXQhkPQzm8ojueqRxtwjss//Xvh4d8ElQtjpULQKBgQDhzXnIKv8ZWrTTPZiW\\nDB1UOnEhGyX4F6AsWT43i30LwtGCtVI4ZrmIwN4KHI1zi2HjMoOjdcSXflOJgS8J\\n64OdKKo1oaZYeM7Mb8iZ/+7iNlSj1tF1g/l3ts5MAMUBFhe2MK6C3jDDm55wqrV/\\nBgi6kZ9qrq10PSCD3/gKhTVsNQKBgQDf91kS4seQ5YzoN3vswK6YJiKuJqq9SmN+\\nZw3o2hXpBoGVFOPohiS1Qahicqo4BjurYghvJq3e+2opFpSXjG7LzVYPCurTdizR\\n3H+T6IriyXQs+UyvgAJn3dXoSsdJ3prmkQzR3VMqiBtO2EgKUvGVzBBAsVg65/YZ\\ntr5L6y0szQKBgQCFJRgOQ+wwEyBU/xkeQP56UAutLcjxxVyZSfY54nJVoQbRQAG/\\naDM2xCMpazStUR7jTUH5/NNzUPYJOrd17f2pifTzsuHXgUTPn4p15EgD821tPpo7\\n5RoQ0SZMApgnz9Mjhwkf+cxfSz/dx4tF31T+TifmTCHTTMgDeulTqsCZkQKBgQCU\\nH7L5mqMCYc9fhdKgMo9GcXJeW5RwvNIwt1eruRDknTj2sGpLTuzYNCR5d/y8MOMl\\n5iBN4vb7kqwToKuFz4s3nwZbzIyibnYWFg8Kc7Hna/U6CWd+atssdrsSduJ3KJKq\\nCyS/Z/GpH3twGfsLWPAOcngNUT1ZODC7sPEKpO+rwQKBgBlIfMFU014OQ9Dv6kD+\\noDD0vs1eLlsbn2RKt7221mqQZNiO8sN9W1zCW3HM0SZO4Z/hc+BHeKR12m/RxoAq\\n0a1z47TeoZX+PdIZ2fqnU1cCt99FskNESanw/ixeWAMHEDPGRKVdUpUpJVK7KK0C\\npuWQ786SoXmomgJBXVRKQACV\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-gubq1@courseonline-6050b.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"110619583449193692930\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-gubq1%40courseonline-6050b.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(Arrays.asList(firebaseMessagingScope));
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        }
       catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
