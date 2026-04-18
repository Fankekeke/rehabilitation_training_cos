package cc.mrbird.febs.controller;

import cc.mrbird.febs.common.domain.FebsConstant;
import cc.mrbird.febs.common.domain.FebsResponse;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.HttpUtil;
import cc.mrbird.febs.common.utils.R;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Slf4j
@Validated
@RestController
@RequestMapping("weather")
public class WeatherController {

    @GetMapping
    public R queryWeather() throws FebsException {
        String url = "http://t.weather.itboy.net/api/weather/city/101010100";
        String forecast = cn.hutool.http.HttpUtil.get(url);
        return R.ok(JSONUtil.parse(forecast));
    }
}
