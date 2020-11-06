package com.virtualmind.jrfexams.models;

import java.util.List;

public class Quiz extends Base_Response{
    public int id, date_id;
    public String question, option1, option2, option3, option4, answer;
    public List<Quiz> res;
}
