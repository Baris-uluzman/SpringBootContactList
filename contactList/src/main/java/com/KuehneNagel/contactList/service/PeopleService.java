package com.KuehneNagel.contactList.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.KuehneNagel.contactList.model.People;
import com.KuehneNagel.contactList.model.PeopleComparators;
import com.KuehneNagel.contactList.model.paging.Column;
import com.KuehneNagel.contactList.model.paging.Order;
import com.KuehneNagel.contactList.model.paging.Page;
import com.KuehneNagel.contactList.model.paging.PagingRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeopleService {

    private static final Comparator<People> EMPTY_COMPARATOR = (e1, e2) -> 0;

    public Page<People> getPeoples(PagingRequest pagingRequest) {
        // Hashmap to map CSV data to 
		// Bean attributes.
		Map<String, String> mapping = new 
		              HashMap<String, String>();
		mapping.put("name", "Name");
		mapping.put("url", "Url");            
     
		// HeaderColumnNameTranslateMappingStrategy
		HeaderColumnNameTranslateMappingStrategy<People> strategy =
		     new HeaderColumnNameTranslateMappingStrategy<People>();
		strategy.setType(People.class);
		strategy.setColumnMapping(mapping);
     
		// Create castobaen and csvreader object
		CSVReader csvReader = null;
		try {
		    csvReader = new CSVReader(new FileReader
		    (getClass().getClassLoader().getResource("people.csv").getPath()
		    		));
		}
		catch (FileNotFoundException e) {   
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    return new Page<>();
		}
		CsvToBean csvToBean = new CsvToBean();
     
		return getPage(csvToBean.parse(strategy, csvReader), pagingRequest);
        
    }

    private Page<People> getPage(List<People> Peoples, PagingRequest pagingRequest) {
        List<People> filtered = Peoples.stream()
                                           .sorted(sortPeoples(pagingRequest))
                                           .filter(filterPeoples(pagingRequest))
                                           .skip(pagingRequest.getStart())
                                           .limit(pagingRequest.getLength())
                                           .collect(Collectors.toList());

        long count = Peoples.stream()
                             .filter(filterPeoples(pagingRequest))
                             .count();

        Page<People> page = new Page<>(filtered);
        page.setRecordsFiltered((int) count);
        page.setRecordsTotal((int) count);
        page.setDraw(pagingRequest.getDraw());

        return page;
    }

    private Predicate<People> filterPeoples(PagingRequest pagingRequest) {
        if (pagingRequest.getSearch() == null || StringUtils.isEmpty(pagingRequest.getSearch()
                                                                                  .getValue())) {
            return People -> true;
        }

        String value = pagingRequest.getSearch()
                                    .getValue();

        return People -> People.getName()
                                   .toLowerCase()
                                   .contains(value.toLowerCase());
    }

    private Comparator<People> sortPeoples(PagingRequest pagingRequest) {
        if (pagingRequest.getOrder() == null) {
            return EMPTY_COMPARATOR;
        }

        try {
            Order order = pagingRequest.getOrder()
                                       .get(0);

            int columnIndex = order.getColumn();
            Column column = pagingRequest.getColumns()
                                         .get(columnIndex);

            Comparator<People> comparator = PeopleComparators.getComparator(column.getData(), order.getDir());
            if (comparator == null) {
                return EMPTY_COMPARATOR;
            }

            return comparator;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return EMPTY_COMPARATOR;
    }
}
