/**
 * Copyright © 2016-2023 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.service.sms.sparrow;

import org.apache.hc.core5.http.HttpException;
import org.thingsboard.rule.engine.api.sms.exception.SmsException;
import org.thingsboard.rule.engine.api.sms.exception.SmsParseException;
import org.thingsboard.rule.engine.api.sms.exception.SmsSendException;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.sms.config.SparrowSmsProviderConfiguration;
import org.thingsboard.server.service.sms.AbstractSmsSender;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SparrowSmsSender extends AbstractSmsSender {

    private static final Pattern PHONE_NUMBERS_SID_MESSAGE_SERVICE_SID = Pattern.compile("^(PN|MG).*$");

    private HttpClient httpClient;
    private String token;
    private String apiUrl = "http://api.sparrowsms.com/v2/sms";

    private String numberFrom;

    

    private String validatePhoneSparrowNumber(String phoneNumber) throws SmsParseException {
        phoneNumber = phoneNumber.trim();

        if (phoneNumber.isEmpty()) {
            throw new SmsParseException("Sender empty");
        }
        return phoneNumber;
    }

    public SparrowSmsSender(SparrowSmsProviderConfiguration config) {
        if (StringUtils.isEmpty(config.getToken()) || StringUtils.isEmpty(config.getNumberFrom())) {
            throw new IllegalArgumentException("Invalid sparrow sms provider configuration: token and numberFrom should be specified!");
        }
        this.numberFrom = this.validatePhoneSparrowNumber(config.getNumberFrom());
        this.httpClient = HttpClient.newHttpClient();
        this.token = config.getToken();
    }

    @Override
    public int sendSms(String numberTo, String message) throws SmsException {
        numberTo = this.validatePhoneNumber(numberTo).replace("+977", "");
        message = this.prepareMessage(message);
        try {

            String urlString = String.format(
                "%s/?token=%s&from=%s&to=%s&text=%s",
                apiUrl,
                this.encodeValue(this.token),
                this.encodeValue(this.numberFrom),
                this.encodeValue(numberTo),
                this.encodeValue(message));
            URI uri = new URI(urlString);
            System.out.println(uri);

            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

            HttpResponse<String> postResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() !=200){
                throw new SmsSendException("Failed to send SMS message " , new HttpException(postResponse.body()));
            }

            return this.countMessageSegments(message);
        } catch (Exception e) {
            throw new SmsSendException("Failed to send SMS message - " + e.getMessage(), e);
        }
    }
    private String encodeValue(String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    @Override
    public void destroy() {

    }
}
