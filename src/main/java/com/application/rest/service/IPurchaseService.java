package com.application.rest.service;


import com.application.rest.entities.Purchase;

import java.util.List;
import java.util.Optional;

public interface IPurchaseService {

    List<Purchase> findAll();

    Optional<Purchase> findById(Long id);

    void save (Purchase purchase);

    void deleteById(Long id);

}
