package com.example.user.Stripe;

import android.app.Application;

import com.stripe.android.PaymentConfiguration;

public class Stripe extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51R1LTVDeqOq4LQ6DaFpplpVv8iGA1qK6Hhg5v6i0BqdnkOAHcaexVtfvqEAkTn19jlTnuBvsbE32EhDGRAGzgGQQ00Ey4atjpz"
        );
    }
}
