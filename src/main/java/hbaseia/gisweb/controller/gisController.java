package hbaseia.gisweb.controller;

import hbaseia.gisweb.Service.HBaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.wololo.geojson.GeoJSON;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
public class gisController {

    @Resource
    private HBaseService hBaseService;

    @GetMapping({"/", "/demo", "demo.html"})
    public String index() {
        return "demo";
    }

    @PostMapping({"/query"})
    @ResponseBody
    public GeoJSON query(@RequestParam(value = "geojson") String geojson) throws IOException {
        return hBaseService.query(geojson);
    }
}
