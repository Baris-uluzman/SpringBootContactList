package com.KuehneNagel.contactList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.KuehneNagel.contactList.model.People;
import com.KuehneNagel.contactList.model.paging.Page;
import com.KuehneNagel.contactList.model.paging.PagingRequest;
import com.KuehneNagel.contactList.service.PeopleService;

@RestController
@RequestMapping("peoples")
public class PeopleRestController {

    private final PeopleService peopleService;

    @Autowired
    public PeopleRestController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @PostMapping
    public Page<People> list(@RequestBody PagingRequest pagingRequest) {
        return peopleService.getPeoples(pagingRequest);
    }
}
