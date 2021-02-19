package com.springbackend.advmanager.controller;

import com.springbackend.advmanager.model.Banner;
import com.springbackend.advmanager.model.Request;
import com.springbackend.advmanager.repository.BannerRepository;
import com.springbackend.advmanager.repository.RequestRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
    @author Yavtushenko Evgeny

    Задача RequestController:
    Для получения текста баннера используется URL вида: http://host:port/bid?category=<REQ_NAME>
    В ответ приложение возвращает текст баннера из указанной в запросе категории.
    При наличии нескольких баннеров с такой категорией должен выбираться баннер с самой высокой ценой.
    Если присутствует несколько баннеров с самой высокой ценой, то среди них выбирается случайный.
    Если по данному запросу не нашлось ни одного баннера, то сервер должен вернуть HTTP status 204.
    Информация о каждом запросе должна записываться в таблицу requests.
    При повторном обращении на протяжении одного дня пользователь с одним и тем же ip-адресом и user-agent-ом не должен увидеть показанную ранее рекламу.
 */

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/")
public class RequestController {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BannerRepository bannerRepository;

    @RequestMapping(value = "bid", method = RequestMethod.GET)
    public ResponseEntity<String> requestHandler(@Param("category") final String category,
                                            @RequestHeader(value="User-Agent") String userAgent,
                                            HttpServletRequest httpRequest) {

        List<Banner> bannerList = bannerRepository.findByCategoryName(category);
        bannerList.sort(Collections.reverseOrder(new MyUtils.sortByPrice()));
        String remoteAddress = MyUtils.getIPFromRequest(httpRequest);
        DateTime today = new DateTime().withTimeAtStartOfDay();
        DateTime tomorrow = today.plusDays(1).withTimeAtStartOfDay();
        List<Request> requestList = requestRepository.findByArgs(userAgent, remoteAddress, today.toDate(), tomorrow.toDate());

        /* Filter banner list according current user-agent, ip and today date */
        for(Request request : requestList)
            if (request.getBannerId() != null)
                bannerList.removeIf(x -> request.getBannerId().getId().equals(x.getId()));

        Request newRequest = new Request();
        newRequest.setDate(new Timestamp(System.currentTimeMillis()));
        newRequest.setIpAddress(remoteAddress);
        newRequest.setUserAgent(userAgent);
        if (bannerList.size() > 0) newRequest.setBannerId(bannerList.get(0));
        requestRepository.save(newRequest);

        return bannerList.size() > 0 ? new ResponseEntity(bannerList.get(0).getContent(), HttpStatus.OK)
                                     : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}