package com.user.reward.api.thirdparty;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.user.reward.constant.Parameters.CLIENT_ID;
import static com.user.reward.constant.Parameters.SECRET;

/**
 * Created by Chaklader on 2019-04-12.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class PayPalClient {


    /**
     * create the payment using the reward amount and currency name
     * for our users
     *
     * @param sum
     * @param currencyName
     * @return
     */
    public Map<String, Object> createPayment(String sum, String currencyName) {

        Map<String, Object> response = new HashMap<>();
        Amount amount = new Amount();

        amount.setCurrency(currencyName);
        amount.setTotal(sum);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("buy");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:4200/cancel");
        redirectUrls.setReturnUrl("http://localhost:4200/");

        payment.setRedirectUrls(redirectUrls);
        Payment createdPayment;

        try {

            String redirectUrl = "";
            APIContext context = new APIContext(CLIENT_ID, SECRET, "sandbox");
            createdPayment = payment.create(context);

            if (createdPayment != null) {

                List<Links> links = createdPayment.getLinks();

                for (Links link : links) {
                    if (link.getRel().equals("approval_url")) {
                        redirectUrl = link.getHref();
                        break;
                    }
                }
                response.put("status", "success");
                response.put("redirect_url", redirectUrl);
            }
        } catch (PayPalRESTException e) {
            System.out.println("Error happened during paymentUsingPaypal creation!");
        }

        return response;
    }


    /**
     * complete the payment to our users using an unique payment ID
     * and return the response code for the success (or faliure)
     *
     * @param paymentId
     * @return
     */
    public Map<String, Object> completePayment(String paymentId) {

        Map<String, Object> response = new HashMap();

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId("eTherapists");

        try {

            APIContext context = new APIContext(CLIENT_ID, SECRET, "sandbox");
            Payment createdPayment = payment.execute(context, paymentExecution);

            if (createdPayment != null) {

                response.put("status", "success");
                response.put("payment", createdPayment);
            }

        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        }

        return response;
    }
}
