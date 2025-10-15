package com.example.yogarena;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QuestionaireViewModel extends ViewModel {

    //data sa mga questionaire

    private final MutableLiveData<Integer> age = new MutableLiveData<>();
    private final MutableLiveData<String> yogaExperience = new MutableLiveData<>();


    private final MutableLiveData<String> yogaReasons = new MutableLiveData<>();

    private final MutableLiveData<String> hasHealthCondition = new MutableLiveData<>();
    private final MutableLiveData<String> healthConditionDetails = new MutableLiveData<>();

    public void setAge(int userAge) {
        age.setValue(userAge);
    }

    public void setYogaExperience(String experience) {
        yogaExperience.setValue(experience);
    }

    public  void setYogaReasons(String reasons){
        yogaReasons.setValue(reasons);
    }

    public void setHasHealthCondition(String condition) {
        hasHealthCondition.setValue(condition);
    }

    public void setHealthConditionDetails(String details) {
        healthConditionDetails.setValue(details);
    }


    public Integer getAge() {
        return age.getValue();
    }

    public String getYogaExperience() {
        return yogaExperience.getValue();
    }

    public String getYogaReasons() {
        return yogaReasons.getValue();
    }

    public String getHasHealthCondition() {
        return hasHealthCondition.getValue();
    }

    public String getHealthConditionDetails() {
        return healthConditionDetails.getValue();
    }



}