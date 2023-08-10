package com.imooc.restroom.api;

import com.imooc.restroom.pojo.Toilet;

import java.util.List;

public interface IRestroomService {
    public List<Toilet> getAvailableToilet();

    public Toilet occupy(Long id);

    public Toilet release(Long id);

    public boolean checkAvailability(Long id);
}
