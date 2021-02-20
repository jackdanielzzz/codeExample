package com.springbackend.advmanager.controller;

import com.springbackend.advmanager.model.Banner;
import com.springbackend.advmanager.model.Category;
import com.springbackend.advmanager.repository.BannerRepository;
import com.springbackend.advmanager.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/banners")
public class BannerController {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("")
    public List<Banner> getAllBanners(){
        return bannerRepository.findAll();
    }

    @PostMapping(value = "/new")
    public ResponseEntity<String> saveNewBanner(@RequestBody Banner banner) {
        Category category = categoryRepository.findByCategory(banner.getCategoryId().getName());
        if (category != null) banner.setCategoryId(category);
        bannerRepository.save(banner);

        return new ResponseEntity<>("added", HttpStatus.OK);
    }

    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteBanner(@PathVariable Long id) {
        if (bannerRepository.existsById(id)) {
            bannerRepository.findById(id).map(banner -> {
                banner.setDeleted(true);
                return bannerRepository.save(banner);
            });
            return ResponseEntity.status(HttpStatus.OK).body("banner was deleted");
        } else
            return ResponseEntity.badRequest().body("Banner id invalid");
    }

    @PostMapping(value = "/edit/{id}")
    public ResponseEntity<String> editBanner(@RequestBody Banner inputBanner, @PathVariable Long id) {
        if (bannerRepository.existsById(id)) {
            bannerRepository.findById(id).map(banner -> {
                Category category = categoryRepository.findByCategory(inputBanner.getCategoryId().getName());
                banner.setName(inputBanner.getName());
                banner.setContent(inputBanner.getContent());
                banner.setPrice(inputBanner.getPrice());
                if (category != null) banner.setCategoryId(category);
                return bannerRepository.save(banner);
            });
            return ResponseEntity.status(HttpStatus.OK).body("updated successfully");
        } else
            return ResponseEntity.badRequest().body("error");

    }

}
