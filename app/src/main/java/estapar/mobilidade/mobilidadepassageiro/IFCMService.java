package estapar.mobilidade.mobilidadepassageiro;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
        "Content-Type:application/json",
        "Authorization:key=AAAAghbN6Gg:APA91bFw7B0Gcsm8Y0FTJWc_NmPOzuZU3VgdbDapsf6X6efsdCcslvT6vL0Cd2KZfsavwMmWuOkLrriJ6Dx0w9mTFI2mkqmWfd5CmMGSkDRR_MWjA1Z00FUii6IhLQPjI4IdweFGlLEZ"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
//AAAAP7SWAck:APA91bGRkuBj4OI2ivteUtENPTUMncix4pTZEzJocqz1ZEp9_UgDv5fJ0mrYUhx27vnPAPXeAWf9iqrl_GExYImb3835OYn9sSWptmASpWVhrjxseQmYrPjcmKeaGMQlqulnR1zHYPLC