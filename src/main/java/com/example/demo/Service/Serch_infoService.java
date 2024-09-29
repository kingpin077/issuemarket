package com.example.demo.Service;

import com.example.demo.Entity.Serch_infoEntity;
import com.example.demo.Repository.Serch_infoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Serch_infoService {

    @Autowired
    private Serch_infoRepository filterRepository;

    public Serch_infoEntity saveKeyword(String keyword) {
        Serch_infoEntity filter = new Serch_infoEntity();
        filter.setKeyword(keyword);
        return filterRepository.save(filter);
    }
}
