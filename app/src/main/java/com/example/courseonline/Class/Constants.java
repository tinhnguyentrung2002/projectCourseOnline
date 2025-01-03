package com.example.courseonline.Class;


import com.google.android.gms.wallet.WalletConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Constants {

    /**
     * Changing this to ENVIRONMENT_PRODUCTION will make the API return chargeable card information.
     * Please refer to the documentation to read about the required steps needed to enable
     * ENVIRONMENT_PRODUCTION.
     *
     * @value #PAYMENTS_ENVIRONMENT
     */
    public static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST;

    public static final List<String> SUPPORTED_NETWORKS = Arrays.asList(
            "AMEX",
            "DISCOVER",
            "JCB",
            "MASTERCARD",
            "VISA");

    public static final List<String> SUPPORTED_METHODS = Arrays.asList(
            "PAN_ONLY",
            "CRYPTOGRAM_3DS");

    public static final String COUNTRY_CODE = "VN"; // Mã quốc gia cho Việt Nam
    public static final String CURRENCY_CODE = "VND"; // Mã tiền tệ cho Việt Nam

    public static final List<String> SHIPPING_SUPPORTED_COUNTRIES = Arrays.asList("US", "GB");


    public static final String PAYMENT_GATEWAY_TOKENIZATION_NAME = "example";


    public static final HashMap<String, String> PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS =
            new HashMap<String, String>() {{
                put("gateway", PAYMENT_GATEWAY_TOKENIZATION_NAME);
                put("gatewayMerchantId", "exampleGatewayMerchantId");
                // Your processor may require additional parameters.
            }};


    public static final String DIRECT_TOKENIZATION_PUBLIC_KEY = "REPLACE_ME";

    public static final HashMap<String, String> DIRECT_TOKENIZATION_PARAMETERS =
            new HashMap<String, String>() {{
                put("protocolVersion", "ECv2");
                put("publicKey", DIRECT_TOKENIZATION_PUBLIC_KEY);
            }};
}
