package com.user.reward.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.user.reward.constant.Misc.BIGGER_OR_EQUAL_TO_ZERO;

/**
 * Created by Chaklader on 2019-04-10.
 */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotNull
    @NotEmpty
    private String name;

    @Column(name = "countryName")
    @NotNull
    @NotEmpty
    private String countryName;


    @Column(name = "currencyName")
    @NotNull
    @NotEmpty
    private String currencyName;


    /*
     * total steps is for the keepign the history of the user movement
     * */
    @Column(name = "totalSteps")
    @Min(value = 0L, message = BIGGER_OR_EQUAL_TO_ZERO)
    private int totalSteps;

    /*
     * current steps is for providing the user reward. We will need to set
     * it to zero after processing the user paymentUsingPaypal
     * */
    @Column(name = "currentSteps")
    @Min(value = 0L, message = BIGGER_OR_EQUAL_TO_ZERO)
    private int currentSteps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reward> rewards = new ArrayList<>();

    public User() {

    }

    public User(@NotNull @NotEmpty String name, @NotNull @NotEmpty String countryName) {
        this.name = name;
        this.countryName = countryName;
    }

    public User(@NotNull @NotEmpty String name, @NotNull @NotEmpty String countryName, @Min(value = 0L, message = "The value must be positive") int totalSteps) {
        this.name = name;
        this.countryName = countryName;
        this.totalSteps = totalSteps;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public int getCurrentSteps() {
        return currentSteps;
    }

    public void setCurrentSteps(int currentSteps) {
        this.currentSteps = currentSteps;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }

    public void addReward(Reward reward) {

        if (this.rewards == null){
            this.rewards = new ArrayList<>();
        }

        this.rewards.add(reward);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {

        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countryName='" + countryName + '\'' +
                ", totalSteps=" + totalSteps +
                ", currentSteps=" + currentSteps +
                '}';
    }
}
