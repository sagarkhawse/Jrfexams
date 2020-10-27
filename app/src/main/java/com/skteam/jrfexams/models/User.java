package com.skteam.jrfexams.models;

import java.util.List;

import retrofit2.http.Field;

public class User extends Base_Response {
public int user_id;
public String email, phone, full_name,password;
public List<User> res;

//transaction
public String order_id, order_amount, customer_phone, customer_email, transaction_id,status ,date, action;


}
