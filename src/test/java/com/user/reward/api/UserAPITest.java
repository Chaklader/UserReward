package com.user.reward.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.reward.api.thirdparty.CurrencyUtilities;
import com.user.reward.api.thirdparty.PayPalClient;
import com.user.reward.model.Reward;
import com.user.reward.model.User;
import com.user.reward.service.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Chaklader on 2019-04-11.
 */
public class UserAPITest {

    public static final String URL = "";
    private MockMvc mockMvc;

    @Mock
    UserService userService;

    @Mock
    PayPalClient payPalClient;

    @Mock
    CurrencyUtilities currencyUtilities;

    private UserAPI userAPI;
    private User user;

    static byte[] convertObjectToJsonBytes(Object object) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper.writeValueAsBytes(object);
    }


    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        userAPI = new UserAPI(userService, payPalClient, currencyUtilities);
        mockMvc = MockMvcBuilders.standaloneSetup(userAPI).build();

        user = new User();
        user.setId(44L);
        user.setCurrencyName("EUR");
        user.setCountryName("Germany");
        user.setName("John");
    }

    @Test
    public void createUser() throws Exception {
        user.setRewards(new ArrayList<>());
        user.setCurrentSteps(0);
        user.setTotalSteps(0);
        when(userService.save(any(User.class))).thenReturn(user);

        mockMvc.perform(
                post("/api/v1/users/createUser")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(convertObjectToJsonBytes(user))
        ).andExpect(
                status().isCreated()
        ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(
                jsonPath("$.id", is(user.getId().intValue()))
        ).andExpect(
                jsonPath("$.name", is(user.getName()))
        ).andExpect(
                jsonPath("$.countryName", is(user.getCountryName()))
        ).andExpect(
                jsonPath("$.currencyName", is(user.getCurrencyName()))
        ).andExpect(
                jsonPath("$.totalSteps", is(user.getTotalSteps()))
        ).andExpect(
                jsonPath("$.currentSteps", is(user.getCurrentSteps()))
        ).andExpect(
                jsonPath("$.rewards", is(user.getRewards()))
        );
    }

    @Test
    public void findUserById() throws Exception {

        when(userService.findById(any(Long.class))).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users/findUserById")
                .param("userId", user.getId().toString()))
                .andExpect(
                        status().isCreated()
                ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andDo(print())
                .andExpect(
                        jsonPath("$.id", is(user.getId().intValue()))
                ).andExpect(
                jsonPath("$.name", is(user.getName()))
        ).andExpect(
                jsonPath("$.countryName", is(user.getCountryName()))
        ).andExpect(
                jsonPath("$.currencyName", is(user.getCurrencyName()))
        );
    }

    @Test
    public void findAllUsers() throws Exception {

        List<User> users = new ArrayList<>();

        User user1 = new User();
        user1.setId(46L);
        user1.setName("Maria");
        user1.setCurrencyName("USD");
        user1.setCountryName("USA");
        users.add(user);
        users.add(user1);

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users/findAllUsers"))
                .andExpect(
                        status().isCreated()
                ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andDo(print())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(
                        jsonPath("$[0].id", is(user.getId().intValue()))
                ).andExpect(
                jsonPath("$[0].name", is(user.getName()))
        ).andExpect(
                jsonPath("$[0].countryName", is(user.getCountryName()))
        ).andExpect(
                jsonPath("$[0].currencyName", is(user.getCurrencyName()))
        )
                .andExpect(
                        jsonPath("$[1].id", is(user1.getId().intValue()))
                ).andExpect(
                jsonPath("$[1].name", is(user1.getName()))
        ).andExpect(
                jsonPath("$[1].countryName", is(user1.getCountryName()))
        ).andExpect(
                jsonPath("$[1].currencyName", is(user1.getCurrencyName()))
        );

    }

    @Test
    public void saveUserSteps() throws Exception {

        when(userService.findById(any(Long.class))).thenReturn(Optional.of(user));
        int steps = 200;
        when(userService.save(any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/v1/users/{userId}/saveUserSteps", user.getId())
                .param("steps", String.valueOf(steps)))
                .andExpect(
                        status().isCreated()
                ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andDo(print())
                .andExpect(
                        jsonPath("$.currentSteps", is(steps))
                ).andExpect(
                jsonPath("$.totalSteps", is(steps))
        );
    }


    @Test
    public void calculateReward() throws Exception {

        when(userService.findById(any(Long.class))).thenReturn(Optional.of(user));
        int steps = 1000;

        user.setCurrentSteps(steps);
        user.setTotalSteps(steps);

        when(userService.save(any(User.class))).thenReturn(user);
        Map<String, Double> map = new HashMap<>();
        map.put("EUR", 1.0);
        when(currencyUtilities.getCurrencyMap()).thenReturn(map);

        mockMvc.perform(put("/api/v1/users/calculateReward")
                .param("userId", String.valueOf(user.getId())))
                .andExpect(
                        status().isCreated()
                ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andDo(print())
                .andExpect(
                        jsonPath("$.name", is(user.getName()))
                ).andExpect(
                jsonPath("$.currency", is(user.getCurrencyName()))
        ).andExpect(
                jsonPath("$.reward", is("1.00")));
    }

    @Test
    public void findUsersWithRewardHistory() throws Exception {

        Reward reward = new Reward();
        reward.setUser(user);
        reward.setId(55L);
        reward.setAmount(1);

        Reward reward1 = new Reward();
        reward1.setUser(user);
        reward1.setId(57L);
        reward1.setAmount(1);

        user.addReward(reward);
        user.addReward(reward1);

        when(userService.findUsersWithRewardHistory()).thenReturn(Optional.of(Arrays.asList(user)));
        Map<String, Double> map = new HashMap<>();

        map.put("EUR", 1.0);
        when(currencyUtilities.getCurrencyMap()).thenReturn(map);

        mockMvc.perform(get("/api/v1/users/findUsersWithRewards"))
                .andExpect(
                        status().isCreated()
                ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(
                        jsonPath("$[0].id", is(user.getId().intValue()))
                ).andExpect(
                jsonPath("$[0].name", is(user.getName()))
        ).andExpect(
                jsonPath("$[0].['total_reward_EUR :']", is("2.00"))
        );
    }

    @Test
    public void findUsersPayoutList() throws Exception {

        when(userService.findById(any(Long.class))).thenReturn(Optional.of(user));

        Reward reward = new Reward();
        reward.setUser(user);
        reward.setId(55L);
        reward.setAmount(1);

        Reward reward1 = new Reward();
        reward1.setUser(user);
        reward1.setId(57L);
        reward1.setAmount(1);

        user.addReward(reward);
        user.addReward(reward1);

        Map<String, Double> map = new HashMap<>();
        map.put("EUR", 1.0);

        when(currencyUtilities.getCurrencyMap()).thenReturn(map);
        JSONArray rewards = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("total_reward_EUR :", 1);

        rewards.put(object);
        rewards.put(object);

        mockMvc.perform(get("/api/v1/users/findUsersPayouts").param("userId", String.valueOf(user.getId())))
                .andExpect(
                        status().isCreated()
                ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andDo(print())
                .andExpect(
                        jsonPath("$.id", is(user.getId().intValue()))
                ).andExpect(
                jsonPath("$.name", is(user.getName()))
        ).andExpect(
                jsonPath("$.rewards[0].[\"total_reward_EUR :\"]", is("1.00"))
        ).andExpect(
                jsonPath("$.rewards[1].\"total_reward_EUR :\"", is("1.00"))
        );
    }

//    @Test
//    public void paymentUsingPaypal() throws Exception {
//
//        when(userService.findById(any(Long.class))).thenReturn(Optional.of(user));
//
//        int steps = 1000;
//        user.setCurrentSteps(steps);
//        user.setTotalSteps(steps);
//
//        when(userService.save(any(User.class))).thenReturn(user);
//        Map<String, Double> map = new HashMap<>();
//        map.put("EUR", 1.0);
//
//
//        when(currencyUtilities.getCurrencyMap()).thenReturn(map);
//        when(payPalClient.createPayment(any(String.class), any(String.class))).thenReturn(new HashMap<>());
//        when(payPalClient.completePayment(any(String.class))).thenReturn(new HashMap<>());
//
//        mockMvc.perform(post("/api/v1/users/make-paypal-payment").param("userId", String.valueOf(user.getId())))
//                .andExpect(
//                        status().isCreated()
//                ).andExpect(
//                content().contentType(MediaType.APPLICATION_JSON_UTF8)
//        ).andDo(print());
//    }
}
