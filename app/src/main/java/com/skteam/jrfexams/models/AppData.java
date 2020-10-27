package com.skteam.jrfexams.models;

import java.util.List;

public class AppData extends Base_Response{
    public int id, user_id;
    public String subscription_amount, subscription_active, isactive, date;
    public List<AppData> res;
}
