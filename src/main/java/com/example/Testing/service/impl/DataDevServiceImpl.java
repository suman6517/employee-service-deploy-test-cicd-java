package com.example.Testing.service.impl;

import com.example.Testing.service.DataService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
public class DataDevServiceImpl implements DataService
{

    @Override
    public String getData()
    {
        return "Dev Data";
    }
}
