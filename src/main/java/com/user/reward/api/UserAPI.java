package com.user.reward.api;

import com.user.reward.api.thirdparty.CurrencyUtilities;
import com.user.reward.api.thirdparty.PayPalClient;
import com.user.reward.constant.Parameters;
import com.user.reward.model.Reward;
import com.user.reward.model.User;
import com.user.reward.service.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


/**
 * Created by Chaklader on 2019-04-10.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserAPI {


    private static final String TOTAL_REWARD = "total_reward_";
    private static final String EUR = "EUR";


    private final UserService userService;

    private final PayPalClient payPalClient;

    private final CurrencyUtilities currencyUtilities;


    @Autowired
    public UserAPI(UserService userService, PayPalClient payPalClient, CurrencyUtilities currencyUtilities) {

        this.userService = userService;

        this.payPalClient = payPalClient;

        this.currencyUtilities = currencyUtilities;
    }

    /**
     * create the user from the provided user parameters. We can use
     * similar cURL requests to create an user in the database,
     * <p>
     * $ curl -i -X POST -H "Content-Type:application/json" -d "{ \"name\": \"Maria\", \"countryName\": \"Philippine \", \"currencyName\": \"PHP\"}" http://localhost:8080/api/v1/users/createUser
     * $ curl -i -X POST -H "Content-Type:application/json" -d "{ \"name\": \"Robert\", \"countryName\": \"Germany\", \"currencyName\": \"EUR\"}" http://localhost:8080/api/v1/users/createUser
     * $ curl -i -X POST -H "Content-Type:application/json" -d "{ \"name\": \"Mary\", \"countryName\": \"USA\", \"currencyName\": \"USD\"}" http://localhost:8080/api/v1/users/createUser
     *
     * @param user
     * @return
     */
    @PostMapping(value = "/createUser", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> createUser(@RequestBody @Valid User user) {

        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


    /**
     * find the user by providing the user ID. We can use similar cURL command to find
     * the user,
     * <p>
     * $ curl -X GET http://localhost:8080/api/v1/users/findUserById?userId=1 | jq
     *
     * @param userId we need to provide this user ID to retrieve the user form the database
     * @return
     */
    @GetMapping(value = "/findUserById")
    public ResponseEntity<Object> findUserById(@RequestParam("userId") Long userId) {

        Optional<User> optional = userService.findById(userId);

        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        User user = optional.get();

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * find all the users from the database using the end-point. We can use a cURL request for
     * the purpose,
     * <p>
     * $ curl -X GET http://localhost:8080/api/v1/users/findAllUsers | jq
     *
     * @return
     */
    @GetMapping("/findAllUsers")
    public ResponseEntity<Object> findAllUsers() {

        List<User> users = userService.findAll();

        if (CollectionUtils.isEmpty(users)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(users);
    }

    /**
     * save the steps taken by the users in the database. We can use the cURL request
     * for the purpose,
     * <p>
     * $ curl -X PUT http://localhost:8080/api/v1/users/1/saveUserSteps?steps=200 | jq
     * $ curl -X PUT http://localhost:8080/api/v1/users/2/saveUserSteps?steps=123 | jq
     *
     * @param userId
     * @param steps
     * @return
     */
    @PutMapping("/{userId}/saveUserSteps")
    public ResponseEntity<Object> saveUserSteps(@PathVariable("userId") Long userId, @RequestParam("steps") int steps) {

        Optional<User> optional = userService.findById(userId);

        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        User user = optional.get();

        /*
         * update the current and the total steps
         * */
        user.setCurrentSteps(user.getCurrentSteps() + steps);
        user.setTotalSteps(user.getTotalSteps() + steps);

        userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


    /**
     * Find the user based on the user ID and calculate the reward with the currently
     * taken steps by the user. We can use a cURL request for the purpose,
     * <p>
     * $ curl -X PUT http://localhost:8080/api/v1/users/calculateReward?userId=1 | jq
     * $ curl -X PUT http://localhost:8080/api/v1/users/calculateReward?userId=2 | jq
     *
     * @param userId
     * @return
     */
    @PutMapping("/calculateReward")
    public ResponseEntity<Object> calculateReward(@RequestParam("userId") Long userId) {

        Optional<User> optional = userService.findById(userId);

        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        User user = optional.get();

        double amountForReward = 0.0;
        int currentlyTakenSteps = user.getCurrentSteps();

        /*
         * we will only reward the user if they have taken at
         * least 1000 steps, recorded the data in out app and
         * decided to cash out
         *
         * */
        if (currentlyTakenSteps >= 1000) {
            amountForReward = user.getCurrentSteps() * Parameters.REWARD_PER_STEPS_EUR;
        }

        /*
         * the user has not taken enough steps to be rewarded
         * */
        if (amountForReward == 0.0) {

            JSONObject error = new JSONObject();
            error.put("error", "Not enough steps to get rewarded");
            return ResponseEntity.status(HttpStatus.OK).body(error.toString());
        }

        Reward reward = new Reward();

        reward.setUser(user);
        reward.setAmount(amountForReward);

        user.setCurrentSteps(0);
        user.addReward(reward);

        userService.save(user);

        JSONObject json = new JSONObject();

        /*
         * get the user reward in the local currencies
         * */
        double rewardConverted = amountForReward * currencyUtilities.getCurrencyMap().get(user.getCurrencyName());

        /*
         * convert the reward amount to 2 decimals limit
         * */
        DecimalFormat df = new DecimalFormat("#.00");
        String rewd = df.format(rewardConverted);

        json.put("name", user.getName());
        json.put("currency", user.getCurrencyName());
        json.put("reward", rewd);

        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON_UTF8).body(json.toString());
    }


    /**
     * List of users with payout rewards history and the amount in Euros and the
     * converted amount. We can use similar cURL request for the purpose,
     * <p>
     * $ curl -X GET http://localhost:8080/api/v1/users/findUsersWithRewards | jq
     *
     * @return
     */
    @GetMapping("/findUsersWithRewards")
    public ResponseEntity<Object> findUsersWithRewardHistory() {

        Optional<List<User>> optionalList = userService.findUsersWithRewardHistory();

        if (!optionalList.isPresent()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<User> users = optionalList.get();
        JSONArray array = new JSONArray();

        for (User user : users) {

            JSONObject obj = new JSONObject();

            obj.put("id", user.getId());
            obj.put("name", user.getName());

            /*
             * get the total user reward for steps takes in EUR
             * */
            double reward = user.getRewards().stream().mapToDouble(Reward::getAmount).sum();

            /*
             * this is the user local currency (PHP, USD etc)
             * */
            String currencyName = user.getCurrencyName();

            /*
             * user reward in their native currency
             * */
            Map<String, Double> currencyMap = currencyUtilities.getCurrencyMap();

            /*
             * convert the reward amount to 2 decimals limit
             * */
            DecimalFormat df = new DecimalFormat("#.00");
            double rewardInLocalCurrency = reward * currencyMap.get(currencyName);
            String rewd = df.format(rewardInLocalCurrency);

            obj.put(TOTAL_REWARD + EUR + " :", reward);
            obj.put(TOTAL_REWARD + currencyName + " :", rewd);

            array.put(obj);
        }

        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON_UTF8).body(array.toString());
    }


    /**
     * retrieve the user's payout rewards list (amount in Euros and the converted
     * amount rewarded). We can use a similar cURL request for the purpose,
     * <p>
     * $ curl -X GET http://localhost:8080/api/v1/users/findUsersPayouts?userId=1 | jq
     *
     * @param userId
     * @return
     */
    @GetMapping(value = "/findUsersPayouts")
    public ResponseEntity<Object> findUsersPayoutList(@RequestParam("userId") Long userId) {

        Optional<User> optional = userService.findById(userId);

        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        User user = optional.get();

        List<Reward> rewards = user.getRewards();


        JSONObject obj = new JSONObject();

        obj.put("id", user.getId());
        obj.put("name", user.getName());

        JSONArray array = new JSONArray();

        for (Reward reward : rewards) {

            JSONObject currData = new JSONObject();

            double amount = reward.getAmount();
            String currencyName = user.getCurrencyName();

            Map<String, Double> currencyMap = currencyUtilities.getCurrencyMap();

            currData.put(TOTAL_REWARD + EUR, amount);


            /*
             * convert the reward amount to 2 decimals limit
             * */
            DecimalFormat df = new DecimalFormat("#.00");

            double rewardInLocalCurrency = amount * currencyMap.get(currencyName);
            String rewd = df.format(rewardInLocalCurrency);

            currData.put(TOTAL_REWARD + currencyName, rewd);
            array.put(currData);
        }

        obj.put("rewards", array);
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON_UTF8).body(obj.toString());
    }


    /**
     * find a user base on the user ID and process a payout reward
     * for a user using the PayPal. We can use a similar cURL request
     * to process the payment for our users.
     *
     * <p>
     * $ curl -i -X POST -H "Content-Type:application/json" -d "{ \"id\": \"1\", \"name\": \"Maria\", \"countryName\": \"Philippine \", \"currencyName\": \"PHP\"}" http://localhost:8080/api/v1/users//make-paypal-payment
     *
     * @return
     */
    @PostMapping("/make-paypal-payment")
    public ResponseEntity<Object> paymentUsingPaypal(@RequestBody User user) {

        String s = (String) calculateReward(user.getId()).getBody();

        JSONObject obj = new JSONObject(s);

        /**
         * we get the reward amount in the local currency
         */
        try {

            String reward = obj.get("reward").toString();
            payPalClient.createPayment(reward, user.getCurrencyName());
        } catch (Exception ex) {

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        /*
         * create an unique payment ID for processing the reward payment to our users
         * */
        String paymentId = (UUID.randomUUID()).toString();
        Map<String, Object> paymentCompletion = payPalClient.completePayment(paymentId);

        /*
         * the payment processing is not successful
         * */
        if (paymentCompletion.size() == 0) {

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON_UTF8).body(paymentCompletion);
    }
}
